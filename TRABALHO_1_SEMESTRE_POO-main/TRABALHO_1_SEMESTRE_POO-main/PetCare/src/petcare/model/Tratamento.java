package petcare.model;

public class Tratamento extends RegistroClinico {
    private String plano;

    public Tratamento(int id, String data, String descricao, Veterinario veterinario, String plano) {
        super(id, data, descricao, veterinario);
        this.plano = Validacao.texto(plano, "Plano");
    }
    public String getPlano() { return plano; }
    @Override public void exibir() { System.out.println("[TRATAMENTO] " + plano); }
}
