package petcare.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Recepcionista extends Usuario {
    private static final List<Tutor> TUTORES = new ArrayList<>();
    private static final List<Animal> ANIMAIS = new ArrayList<>();
    private String ramal;
    private String turno;

    public Recepcionista(int id, String nome, String email, String ramal) {
        super(id, nome, email, "recep123", "RECEP");
        this.ramal = Validacao.texto(ramal, "Ramal");
        this.turno = "08h-18h";
    }

    public void cadastrarTutor(Tutor tutor) {
        exigirAtivo();
        Validacao.objeto(tutor, "Tutor");
        if (TUTORES.contains(tutor)) return;
        if (TUTORES.stream().anyMatch(t -> t.getId() == tutor.getId() || t.getCpf().equals(tutor.getCpf())))
            throw new IllegalArgumentException("Tutor ja cadastrado");
        TUTORES.add(tutor);
        new LogAuditoria(this, "CADASTRAR_TUTOR", "Tutor", tutor.getId()).registrar();
    }

    public void cadastrarAnimal(Animal animal) {
        exigirAtivo();
        Validacao.objeto(animal, "Animal");
        if (ANIMAIS.contains(animal)) return;
        if (ANIMAIS.stream().anyMatch(a -> a.getId() == animal.getId()))
            throw new IllegalArgumentException("Animal ja cadastrado");
        cadastrarTutor(animal.getTutor());
        ANIMAIS.add(animal);
        new LogAuditoria(this, "CADASTRAR_ANIMAL", "Animal", animal.getId()).registrar();
    }

    public boolean agendarConsulta(Agendamento agendamento) {
        exigirAtivo();
        Validacao.objeto(agendamento, "Agendamento").setResponsavel(this);
        return agendamento.agendar();
    }

    public void cancelarConsulta(int id) {
        exigirAtivo();
        Agendamento.buscar(id).setResponsavel(this);
        Agendamento.buscar(id).cancelar("Cancelado pela recepcao: " + getNome());
        new LogAuditoria(this, "CANCELAR_PELA_RECEPCAO", "Agendamento", id).registrar();
    }

    public Tutor buscarTutor(String termo) {
        String busca = Validacao.texto(termo, "Busca").toLowerCase(Locale.ROOT);
        return TUTORES.stream().filter(t -> t.getNome().toLowerCase(Locale.ROOT).contains(busca)
                || t.getCpf().equals(busca) || t.getTelefone().equals(busca)
                || String.valueOf(t.getId()).equals(busca)).findFirst().orElse(null);
    }

    public Animal buscarAnimal(String termo) {
        String busca = Validacao.texto(termo, "Busca").toLowerCase(Locale.ROOT);
        return ANIMAIS.stream().filter(a -> a.getNome().toLowerCase(Locale.ROOT).contains(busca)
                || String.valueOf(a.getId()).equals(busca)).findFirst().orElse(null);
    }

    static List<Animal> animaisCadastrados() { return List.copyOf(ANIMAIS); }
    public String getRamal() { return ramal; }
    public String getTurno() { return turno; }
    private void exigirAtivo() {
        if (!isAtivo()) throw new IllegalStateException("Recepcionista inativo");
    }
    @Override public void exibir() {
        System.out.println("[RECEPCIONISTA] " + getNome() + " | Ramal: " + ramal);
    }
}
