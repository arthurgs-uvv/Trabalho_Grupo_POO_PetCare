package petcare.model;

public class Consulta extends RegistroClinico {
    private String motivo;
    private String prescricao;
    private String dataRetorno;
    private boolean prescricaoObrigatoria;

    public Consulta(int id, String data, String descricao, Veterinario veterinario, String motivo) {
        super(id, data, descricao, veterinario);
        this.motivo = Validacao.texto(motivo, "Motivo");
    }

    public String getMotivo() {
        return motivo;
    }

    public void setPrescricao(String rx) {
        ensureEditable();
        this.prescricao = Validacao.texto(rx, "Prescricao");
    }

    public String getPrescricao() {
        return prescricao;
    }

    public void setDataRetorno(String dt) {
        ensureEditable();
        String data = Validacao.data(dt);
        if (java.time.LocalDate.parse(data).isBefore(java.time.LocalDate.parse(getData())))
            throw new IllegalArgumentException("Retorno anterior a consulta");
        this.dataRetorno = data;
    }

    public String getDataRetorno() {
        return dataRetorno;
    }

    public void setPrescricaoObrigatoria(boolean obrigatoria) {
        ensureEditable();
        prescricaoObrigatoria = obrigatoria;
    }

    public boolean isPrescricaoObrigatoria() { return prescricaoObrigatoria; }

    @Override
    public void finalizar() {
        if (prescricaoObrigatoria && prescricao == null)
            throw new IllegalStateException("Consulta exige prescricao antes de finalizar");
        super.finalizar();
    }

    @Override
    public void exibir() {
        System.out.println("[CONSULTA] " + getDescricao() + " | Motivo: " + motivo);
    }
}
