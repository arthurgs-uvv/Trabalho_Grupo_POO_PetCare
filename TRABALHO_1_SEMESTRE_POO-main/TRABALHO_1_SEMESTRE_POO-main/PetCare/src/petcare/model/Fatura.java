package petcare.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Fatura {
    private static final List<Fatura> EMITIDAS = new ArrayList<>();
    private int id;
    private Tutor tutor;
    private double valor;
    private String dataEmissao;
    private String dataVencimento;
    private String status;
    private String descricaoServico;
    private Notificador notificador;
    private String dataPagamento;

    public Fatura(int id, Tutor tutor, double valor, String descricaoServico, Notificador notificador) {
        this.id = Validacao.id(id);
        this.tutor = Validacao.objeto(tutor, "Tutor");
        this.valor = Validacao.valor(valor);
        this.descricaoServico = Validacao.texto(descricaoServico, "Descricao");
        this.notificador = Validacao.objeto(notificador, "Notificador");
        this.status = "PENDENTE";
        this.dataEmissao = LocalDate.now().toString();
        this.dataVencimento = LocalDate.now().plusDays(7).toString();
        if (tutor.verFaturas().stream().anyMatch(f -> f.id == id)) throw new IllegalArgumentException("Id de fatura duplicado");
        tutor.adicionarFatura(this);
    }
    public void emitir() {
        if (EMITIDAS.stream().anyMatch(f -> f != this && f.id == id)) throw new IllegalArgumentException("Id de fatura duplicado");
        if (!EMITIDAS.contains(this)) {
            EMITIDAS.add(this);
            notificador.enviarFatura(this);
        }
    }
    public String gerarBoleto() { return "BOLETO-SIMULADO-SIP-" + id; }
    public String gerarLinkPagamento() { return "https://petcare.example/pagamento/" + id; }
    public void registrarPagamento() {
        emitir();
        if (!status.equals("PAGO")) {
            status = "PAGO";
            dataPagamento = LocalDate.now().toString();
            new LogAuditoria(tutor, "PAGAMENTO_SIMULADO", "Fatura", id).registrar();
        }
    }
    /** Retorno da API sempre positivo, conforme a versao academica da RN12. */
    public boolean confirmarPagOnline() { registrarPagamento(); return true; }
    public boolean isPendente() { return !getStatus().equals("PAGO"); }
    public double getValor() { return valor; }
    public int getId() { return id; }
    public Tutor getTutor() { return tutor; }
    public String getDataEmissao() { return dataEmissao; }
    public String getDataVencimento() { return dataVencimento; }
    public String getDataPagamento() { return dataPagamento; }
    public String getDescricaoServico() { return descricaoServico; }
    public void setDataVencimento(String data) {
        if (status.equals("PAGO")) throw new IllegalStateException("Fatura ja paga");
        dataVencimento = Validacao.data(data);
    }
    public String getStatus() {
        if (!status.equals("PAGO")) status = LocalDate.parse(dataVencimento).isBefore(LocalDate.now()) ? "VENCIDO" : "PENDENTE";
        return status;
    }
    public static List<Fatura> getEmitidas() { return List.copyOf(EMITIDAS); }
    public void exibir() {
        System.out.printf(java.util.Locale.forLanguageTag("pt-BR"), "[FATURA] Valor: R$%.2f | Status: %s%n", valor, getStatus());
        notificador.enviarFatura(this);
    }
}
