package petcare.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Agenda em memoria. Intervalos usam inicio inclusivo e fim exclusivo. */
public class Agendamento {
    private static final List<Agendamento> AGENDA = new ArrayList<>();
    private int id;
    private String dataHora;
    private String tipo;
    private String status;
    private Animal animal;
    private Veterinario veterinario;
    private String sala;
    private Notificador notificador;
    private final List<String> historico = new ArrayList<>();
    private int duracao;
    private Cirurgia cirurgia;
    private Estoque estoque;
    private final Map<Integer, Integer> insumos = new LinkedHashMap<>();
    private boolean retorno;
    private boolean confirmando;
    private Usuario responsavel;

    public Agendamento(int id, String dataHora, String tipo, Animal animal, Veterinario veterinario,
            Notificador notificador) {
        this.id = Validacao.id(id);
        Validacao.horario(dataHora);
        this.dataHora = dataHora;
        this.tipo = Validacao.texto(tipo, "Tipo").toUpperCase(java.util.Locale.ROOT);
        if (!List.of("CONSULTA", "EXAME", "CIRURGIA").contains(this.tipo))
            throw new IllegalArgumentException("Tipo invalido");
        this.animal = Validacao.objeto(animal, "Animal");
        this.veterinario = Validacao.objeto(veterinario, "Veterinario");
        this.notificador = Validacao.objeto(notificador, "Notificador");
        this.responsavel = veterinario;
        this.sala = "Sala 1";
        this.duracao = this.tipo.equals("CIRURGIA") ? 120 : 30;
        // Somente agendar() pode confirmar a reserva.
        this.status = "NAO_AGENDADO";
    }

    public boolean agendar() {
        if (!status.equals("NAO_AGENDADO")) return false;
        if (!validarHorario() || !validarVeterinario() || !recursosValidos()) return false;
        if (AGENDA.stream().anyMatch(a -> a.id == id || conflita(a, dataHora))) return false;
        confirmando = true;
        try {
            if (!reservarRecursos()) return false;
        } finally { confirmando = false; }
        AGENDA.add(this);
        status = "AGENDADO";
        registrar("AGENDAR", "Agendado em " + dataHora);
        notificarTutor();
        return true;
    }

    public void cancelar(String motivo) {
        exigirAtivo();
        motivo = Validacao.texto(motivo, "Motivo");
        if (estoque != null) estoque.liberarReserva(this);
        status = "CANCELADO";
        registrar("CANCELAR_AGENDAMENTO", "Cancelado: " + motivo);
        notificador.enviarCancelamento(this);
    }

    public void reagendar(String novaData) {
        exigirAtivo();
        Validacao.horario(novaData);
        if (!horarioValido(novaData) || !validarVeterinario() || !recursosValidos()
                || (estoque != null && !estoque.reservaValida(this, novaData.substring(0, 10)))
                || AGENDA.stream().anyMatch(a -> conflita(a, novaData)))
            throw new IllegalArgumentException("Horario, veterinario ou recursos indisponiveis");
        String anterior = dataHora;
        dataHora = novaData;
        registrar("REAGENDAR", "Reagendado de " + anterior + " para " + novaData);
        notificador.enviarReagendamento(this);
    }

    public void concluir(RegistroClinico registro) {
        exigirAtivo();
        Validacao.objeto(registro, "Registro clinico");
        HistoricoClinico h = animal.getHistorico();
        boolean vinculado = (tipo.equals("CONSULTA") && h.getConsultas().contains(registro))
                || (tipo.equals("EXAME") && h.getExames().contains(registro))
                || (tipo.equals("CIRURGIA") && registro == cirurgia && h.getCirurgias().contains(registro));
        if (!vinculado || registro.getVeterinario().getId() != veterinario.getId()
                || !registro.getData().equals(dataHora.substring(0, 10)))
            throw new IllegalArgumentException("Registro nao corresponde ao animal, tipo, veterinario ou data");
        if (estoque != null && !estoque.reservaValida(this, dataHora.substring(0, 10)))
            throw new IllegalStateException("Insumos vencidos ou reserva ausente");
        registro.finalizar();
        if (estoque != null) estoque.consumirReserva(this, veterinario.getNome());
        status = "CONCLUIDO";
        registrar("CONCLUIR_AGENDAMENTO", "Procedimento concluido");
    }

    public boolean validarHorario() { return horarioValido(dataHora); }

    private boolean horarioValido(String horario) {
        LocalDateTime inicio = Validacao.horario(horario);
        LocalDateTime fim = inicio.plusMinutes(duracao);
        return !inicio.toLocalTime().isBefore(LocalTime.of(8, 0))
                && inicio.toLocalDate().equals(fim.toLocalDate())
                && !fim.toLocalTime().isAfter(LocalTime.of(18, 0));
    }

    public boolean validarVeterinario() { return veterinario.isAtivo() && veterinario.isDisponivel(); }

    private boolean recursosValidos() {
        if (!tipo.equals("CIRURGIA")) return true;
        return cirurgia != null && cirurgia.validarRecursos() && cirurgia.getDuracao() == duracao
                && cirurgia.getSala().equals(sala) && estoque != null && !insumos.isEmpty();
    }

    public boolean reservarRecursos() {
        if (!recursosValidos() || status.equals("CANCELADO") || status.equals("CONCLUIDO")) return false;
        // A reserva efetiva so acontece dentro de agendar(), depois das validacoes.
        return estoque == null || estoque.reservarParaProced(this);
    }

    public void configurarCirurgia(Cirurgia cirurgia, Estoque estoque, Map<Integer, Integer> insumos) {
        exigirNovo();
        Validacao.objeto(cirurgia, "Cirurgia");
        Validacao.objeto(estoque, "Estoque");
        validarInsumos(insumos);
        if (!tipo.equals("CIRURGIA") || cirurgia.getVeterinario().getId() != veterinario.getId())
            throw new IllegalArgumentException("Cirurgia incompativel");
        this.cirurgia = cirurgia;
        this.duracao = cirurgia.getDuracao();
        this.sala = cirurgia.getSala();
        this.estoque = estoque;
        this.insumos.clear(); this.insumos.putAll(insumos);
    }

    public void configurarInsumos(Estoque estoque, Map<Integer, Integer> insumos) {
        exigirNovo();
        Validacao.objeto(estoque, "Estoque");
        validarInsumos(insumos);
        this.estoque = estoque;
        this.insumos.clear(); this.insumos.putAll(insumos);
    }

    private void validarInsumos(Map<Integer, Integer> itens) {
        Validacao.objeto(itens, "Insumos");
        for (Map.Entry<Integer, Integer> item : itens.entrySet()) {
            Validacao.id(Validacao.objeto(item.getKey(), "Id do item"));
            Validacao.id(Validacao.objeto(item.getValue(), "Quantidade"));
        }
    }

    boolean podeReservar() { return confirmando || status.equals("AGENDADO"); }
    void setResponsavel(Usuario responsavel) { this.responsavel = Validacao.objeto(responsavel, "Responsavel"); }

    public void setSala(String sala) { exigirNovo(); this.sala = Validacao.texto(sala, "Sala"); }
    public void setDuracao(int minutos) {
        exigirNovo();
        if (minutos <= 0 || (tipo.equals("CIRURGIA") && minutos < 120))
            throw new IllegalArgumentException("Duracao invalida");
        this.duracao = minutos;
    }
    public void setRetorno(boolean retorno) { exigirNovo(); this.retorno = retorno; }
    public boolean isRetorno() { return retorno; }
    public int getId() { return id; }
    public int getDuracao() { return duracao; }
    public String getSala() { return sala; }
    public Animal getAnimal() { return animal; }
    public Veterinario getVeterinario() { return veterinario; }
    public String getStatus() { return status; }
    public String getTipo() { return tipo; }
    public String getDataHora() { return dataHora; }
    public Map<Integer, Integer> getInsumos() { return Collections.unmodifiableMap(insumos); }
    public List<String> getHistorico() { return Collections.unmodifiableList(historico); }
    public static List<Agendamento> getAgenda() { return List.copyOf(AGENDA); }
    public static Agendamento buscar(int id) {
        return AGENDA.stream().filter(a -> a.id == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Agendamento nao encontrado"));
    }
    public void notificarTutor() { notificador.enviarConfirmacao(this); }
    public void exibir() {
        System.out.println("[AGENDAMENTO] " + dataHora + " | Tipo: " + tipo + " | Status: " + status);
    }

    private boolean conflita(Agendamento outro, String horario) {
        if (outro == this || outro.status.equals("CANCELADO")) return false;
        boolean mesmoRecurso = outro.veterinario.getId() == veterinario.getId()
                || outro.sala.equalsIgnoreCase(sala) || outro.animal.getId() == animal.getId();
        LocalDateTime inicio = Validacao.horario(horario);
        LocalDateTime outroInicio = Validacao.horario(outro.dataHora);
        return mesmoRecurso && inicio.isBefore(outroInicio.plusMinutes(outro.duracao))
                && outroInicio.isBefore(inicio.plusMinutes(duracao));
    }
    private void exigirNovo() {
        if (!status.equals("NAO_AGENDADO")) throw new IllegalStateException("Configure antes de agendar");
    }
    private void exigirAtivo() {
        if (!status.equals("AGENDADO")) throw new IllegalStateException("Agendamento nao esta ativo");
    }
    private void registrar(String acao, String descricao) {
        historico.add(LocalDateTime.now() + " | " + descricao);
        new LogAuditoria(responsavel, acao, "Agendamento", id).registrar();
    }
}
