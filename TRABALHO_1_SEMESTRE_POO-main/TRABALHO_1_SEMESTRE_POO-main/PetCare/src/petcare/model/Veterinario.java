package petcare.model;

import java.util.List;

public class Veterinario extends Usuario {
    private String crmv;
    private String especialidade;
    private boolean disponivel;

    public Veterinario(int id, String nome, String email, String crmv, String especialidade) {
        super(id, nome, email, "vet1234", "VET");
        this.crmv = require(crmv, "CRMV");
        this.especialidade = require(especialidade, "especialidade");
        this.disponivel = true;
    }

    public String getCrmv() {
        return crmv;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public void registrarLaudo(int id, String texto) {
        registroAutorizado(id).anexarLaudo(texto);
    }

    public void emitirPrescricao(int id, String receita) {
        RegistroClinico registro = registroAutorizado(id);
        if (!(registro instanceof Consulta consulta))
            throw new IllegalArgumentException("Prescricao deste modelo pertence a Consulta");
        consulta.setPrescricao(receita);
        new LogAuditoria(this, "EMITIR_PRESCRICAO", "RegistroClinico", id).registrar();
    }

    public List<Agendamento> consultarAgenda() {
        return Agendamento.getAgenda().stream().filter(a -> a.getVeterinario().getId() == getId()).toList();
    }

    private RegistroClinico registroAutorizado(int id) {
        if (!isAtivo()) throw new IllegalStateException("Veterinario inativo");
        RegistroClinico registro = RegistroClinico.buscar(id);
        if (registro.getVeterinario().getId() != getId()) throw new IllegalArgumentException("Registro de outro veterinario");
        return registro;
    }

    @Override
    public void exibir() {
        System.out.println("[VETERINARIO] " + getNome() + " | CRMV: " + crmv);
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(field + " obrigatorio");
        return value;
    }
}
