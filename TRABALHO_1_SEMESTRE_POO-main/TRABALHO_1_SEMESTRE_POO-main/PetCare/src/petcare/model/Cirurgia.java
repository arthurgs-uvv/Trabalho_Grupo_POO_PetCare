package petcare.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cirurgia extends RegistroClinico {
    private int duracao;
    private String sala;
    private List<String> equipe;
    private String anestesia;
    private List<String> medicamentos;

    public Cirurgia(int id, String data, String descricao, Veterinario veterinario, String sala) {
        super(id, data, descricao, veterinario);
        this.sala = Validacao.texto(sala, "Sala");
        this.duracao = 120;
        this.equipe = new ArrayList<>();
        this.medicamentos = new ArrayList<>();
    }

    public String getSala() {
        return sala;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        ensureEditable();
        if (duracao < 120)
            throw new IllegalArgumentException("Cirurgia deve durar no minimo 120 minutos");
        this.duracao = duracao;
    }

    public void setEquipe(List<String> equipe) {
        ensureEditable();
        Validacao.objeto(equipe, "Equipe");
        equipe.forEach(p -> Validacao.texto(p, "Membro da equipe"));
        this.equipe = new ArrayList<>(equipe);
    }

    public void adicionarMedicamento(String medicamento) {
        ensureEditable();
        medicamentos.add(Validacao.texto(medicamento, "Medicamento"));
    }

    public void setAnestesia(String anestesia) {
        ensureEditable();
        this.anestesia = Validacao.texto(anestesia, "Anestesia");
    }
    public String getAnestesia() { return anestesia; }
    public List<String> getMedicamentos() { return Collections.unmodifiableList(medicamentos); }

    public boolean validarRecursos() {
        return sala != null && !sala.isBlank() && !equipe.isEmpty() && !medicamentos.isEmpty();
    }

    public List<String> getEquipe() {
        return Collections.unmodifiableList(equipe);
    }

    @Override
    public void exibir() {
        System.out.println("[CIRURGIA] Sala: " + sala + " | Duracao: " + duracao + " min");
    }
}
