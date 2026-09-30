package petcare.model;

public class RegistroClinico {
    private static final java.util.List<RegistroClinico> REGISTROS = new java.util.ArrayList<>();
    private int id;
    private String data;
    private String descricao;
    private Veterinario veterinario;
    private String laudoAnexo;
    private boolean finalizado;
    private Integer idAnimal;

    public RegistroClinico(int id, String data, String descricao, Veterinario veterinario) {
        this.id = Validacao.id(id);
        this.data = Validacao.data(data);
        this.descricao = require(descricao, "descricao");
        this.veterinario = Validacao.objeto(veterinario, "Veterinario");
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void finalizar() {
        if (laudoAnexo == null) throw new IllegalStateException("Inclua o relatorio medico antes de finalizar");
        if (!finalizado) {
            finalizado = true;
            new LogAuditoria(veterinario, "FINALIZAR_REGISTRO", "RegistroClinico", id).registrar();
        }
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public void anexarLaudo(String path) {
        ensureEditable();
        laudoAnexo = require(path, "laudo");
        new LogAuditoria(veterinario, "ANEXAR_LAUDO", "RegistroClinico", id).registrar();
    }

    public String getLaudo() {
        return laudoAnexo;
    }

    static void cadastrar(RegistroClinico registro, int idAnimal) {
        if (registro.idAnimal != null && registro.idAnimal != idAnimal)
            throw new IllegalArgumentException("Registro pertence a outro animal");
        if (REGISTROS.stream().anyMatch(r -> r != registro && r.id == registro.id))
            throw new IllegalArgumentException("Id de registro duplicado");
        registro.idAnimal = idAnimal;
        if (!REGISTROS.contains(registro)) REGISTROS.add(registro);
    }

    static void remover(RegistroClinico registro) { REGISTROS.remove(registro); }

    static RegistroClinico buscar(int id) {
        return REGISTROS.stream().filter(r -> r.id == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Registro nao encontrado no historico"));
    }

    public void exibir() {
        System.out.println("[REGISTRO] " + id + " | " + data + " | " + descricao);
    }

    protected void ensureEditable() {
        if (finalizado)
            throw new IllegalStateException("Registro clinico finalizado e bloqueado");
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(field + " obrigatorio");
        return value;
    }
}
