package petcare.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemEstoque {
    private int id;
    private String nome;
    private int quantidade;
    private int quantidadeMinima;
    private String lote;
    private String validade;
    private boolean controlado;
    private String responsavelRetirada;
    private String categoria;
    private int quantidadeReservada;
    private final List<String> movimentacoes = new ArrayList<>();

    public ItemEstoque(int id, String nome, int quantidade, int quantidadeMinima, String lote) {
        this.id = Validacao.id(id);
        this.nome = Validacao.texto(nome, "Nome");
        if (quantidade < 0 || quantidadeMinima < 0) throw new IllegalArgumentException("Quantidades invalidas");
        this.quantidade = quantidade;
        this.quantidadeMinima = quantidadeMinima;
        this.lote = lote == null ? null : Validacao.texto(lote, "Lote");
        this.categoria = "Material";
    }
    public int getId() { return id; }
    public String getNome() { return nome; }
    public int getQuantidade() { return quantidade; }
    public int getQuantidadeMinima() { return quantidadeMinima; }
    public int getQuantidadeReservada() { return quantidadeReservada; }
    public int getQuantidadeDisponivel() { return quantidade - quantidadeReservada; }
    public boolean isControlado() { return controlado; }
    public String getLote() { return lote; }
    public String getValidade() { return validade; }
    public String getResponsavelRetirada() { return responsavelRetirada; }
    public String getCategoria() { return categoria; }
    public List<String> getMovimentacoes() { return List.copyOf(movimentacoes); }

    public void setControlado(boolean controlado) {
        if (controlado) {
            Validacao.texto(lote, "Lote");
            Validacao.data(validade);
            Validacao.texto(responsavelRetirada, "Responsavel");
        }
        this.controlado = controlado;
    }
    public void setValidade(String validade) { this.validade = Validacao.data(validade); }
    public void setResponsavelRetirada(String responsavel) {
        this.responsavelRetirada = Validacao.texto(responsavel, "Responsavel");
    }
    public void setCategoria(String categoria) { this.categoria = Validacao.texto(categoria, "Categoria"); }

    public void darEntrada(int qtd, String responsavel) {
        Validacao.id(qtd);
        Validacao.texto(responsavel, "Responsavel");
        quantidade = Math.addExact(quantidade, qtd);
        registrar("ENTRADA", qtd, responsavel);
    }
    public boolean darSaida(int qtd, String responsavel) {
        Validacao.texto(responsavel, "Responsavel");
        if (qtd <= 0 || qtd > getQuantidadeDisponivel() || isVencido()) return false;
        quantidade -= qtd;
        responsavelRetirada = responsavel;
        registrar("SAIDA", qtd, responsavel);
        return true;
    }
    public boolean reservar(int qtd) {
        if (qtd <= 0 || qtd > getQuantidadeDisponivel() || isVencido()) return false;
        quantidadeReservada += qtd;
        registrar("RESERVA", qtd, "Sistema");
        return true;
    }
    void liberarReserva(int qtd) {
        if (qtd <= 0 || qtd > quantidadeReservada) throw new IllegalArgumentException("Reserva invalida");
        quantidadeReservada -= qtd;
        registrar("LIBERAR_RESERVA", qtd, "Sistema");
    }
    void consumirReserva(int qtd, String responsavel) {
        Validacao.texto(responsavel, "Responsavel");
        if (qtd <= 0 || qtd > quantidadeReservada || isVencido())
            throw new IllegalStateException("Reserva indisponivel ou vencida");
        quantidadeReservada -= qtd;
        quantidade -= qtd;
        responsavelRetirada = responsavel;
        registrar("CONSUMIR_RESERVA", qtd, responsavel);
    }
    void ajustarQuantidade(int novaQuantidade, String responsavel) {
        Validacao.texto(responsavel, "Responsavel");
        if (novaQuantidade < quantidadeReservada || novaQuantidade < 0)
            throw new IllegalArgumentException("Saldo nao pode ser inferior ao reservado");
        quantidade = novaQuantidade;
        registrar("AJUSTE_SALDO", novaQuantidade, responsavel);
    }
    public boolean isAbaixoMinimo() { return quantidade < quantidadeMinima; }
    public boolean isVencido() { return validade != null && LocalDate.parse(validade).isBefore(LocalDate.now()); }
    boolean validoEm(String data) { return validade == null || !LocalDate.parse(validade).isBefore(LocalDate.parse(data)); }
    private void registrar(String acao, int qtd, String responsavel) {
        movimentacoes.add(LocalDateTime.now() + " | " + acao + " | Item: " + id + " | Lote: " + lote
                + " | Validade: " + validade + " | Qtd: " + qtd + " | Responsavel: " + responsavel);
    }
    public void exibir() {
        System.out.println("[ESTOQUE] " + nome + " | Qtd: " + quantidade + " | Reservada: " + quantidadeReservada
                + " | Minimo: " + quantidadeMinima + " -> " + (isAbaixoMinimo() ? "ALERTA" : "OK"));
    }
}
