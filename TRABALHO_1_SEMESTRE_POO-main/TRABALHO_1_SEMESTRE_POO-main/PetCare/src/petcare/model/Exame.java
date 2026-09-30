package petcare.model;

public class Exame extends RegistroClinico {
    private String tipo;
    private String resultado;
    private String imagemAnexo;
    private String laboratorio;

    public Exame(int id, String data, String descricao, Veterinario veterinario, String tipo) {
        super(id, data, descricao, veterinario);
        this.tipo = Validacao.texto(tipo, "tipo");
    }

    public String getTipo() {
        return tipo;
    }

    public void setResultado(String resultado) {
        ensureEditable();
        this.resultado = Validacao.texto(resultado, "resultado");
    }

    public String getResultado() {
        return resultado;
    }

    public void anexarImagem(String path) {
        ensureEditable();
        this.imagemAnexo = Validacao.texto(path, "imagemAnexo");
    }

    public String getImagem() {
        return imagemAnexo;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        ensureEditable();
        this.laboratorio = Validacao.texto(laboratorio, "laboratorio");
    }

    @Override
    public void exibir() {
        System.out.println("[EXAME] Tipo: " + tipo + " | Resultado: " + resultado);
    }
}
