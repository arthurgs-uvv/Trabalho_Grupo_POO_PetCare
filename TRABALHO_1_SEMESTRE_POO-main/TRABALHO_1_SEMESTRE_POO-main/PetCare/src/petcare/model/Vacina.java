package petcare.model;

import java.time.LocalDate;

public class Vacina extends RegistroClinico {
    private String nomeVacina;
    private String dataAplicacao;
    private String dataReforco;
    private String lote;
    private String fabricante;

    public Vacina(int id, String data, String descricao, Veterinario veterinario, String nomeVacina) {
        super(id, data, descricao, veterinario);
        this.nomeVacina = Validacao.texto(nomeVacina, "nomeVacina");
        this.dataAplicacao = data;
    }

    public String getNomeVacina() {
        return nomeVacina;
    }

    public String getDataReforco() {
        return dataReforco;
    }

    public void setDataReforco(String dt) {
        ensureEditable();
        String data = Validacao.data(dt);
        if (java.time.LocalDate.parse(data).isBefore(java.time.LocalDate.parse(dataAplicacao)))
            throw new IllegalArgumentException("Reforco anterior a aplicacao");
        this.dataReforco = data;
    }

    public boolean precisaReforco() {
        return dataReforco != null && LocalDate.parse(dataReforco).isBefore(LocalDate.now());
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        ensureEditable();
        this.lote = Validacao.texto(lote, "lote");
    }

    public void setFabricante(String fabricante) {
        ensureEditable();
        this.fabricante = Validacao.texto(fabricante, "fabricante");
    }

    public String getFabricante() { return fabricante; }
    public String getDataAplicacao() { return dataAplicacao; }

    @Override
    public void exibir() {
        System.out.println("[VACINA] " + nomeVacina + " | Reforco: " + dataReforco);
    }
}
