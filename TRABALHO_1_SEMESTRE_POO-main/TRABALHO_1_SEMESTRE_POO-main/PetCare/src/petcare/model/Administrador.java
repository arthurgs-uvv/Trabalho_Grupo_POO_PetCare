package petcare.model;

import java.util.List;

public class Administrador extends Usuario {
    private int nivelAcesso;
    private String departamento;

    public Administrador(int id, String nome, String email, String senha) {
        super(id, nome, email, senha, "ADMIN");
        this.nivelAcesso = 10;
        this.departamento = "Administracao";
    }

    public boolean excluirRegistro(int id, String tipo) {
        exigirAtivo();
        Validacao.id(id);
        if (!Validacao.texto(tipo, "Tipo").equalsIgnoreCase("RegistroClinico"))
            throw new IllegalArgumentException("Exclusao suportada: RegistroClinico");
        for (Animal animal : Recepcionista.animaisCadastrados()) {
            if (animal.getHistorico().excluir(id)) {
                new LogAuditoria(this, "EXCLUIR_REGISTRO", tipo, id).registrar();
                return true;
            }
        }
        return false;
    }

    public void ajustarEstoque(ItemEstoque item, int qtd) {
        exigirAtivo();
        Validacao.objeto(item, "Item");
        item.ajustarQuantidade(qtd, getNome());
        new LogAuditoria(this, "AJUSTE_ESTOQUE", "ItemEstoque", item.getId()).registrar();
    }

    public Relatorio gerarRelatorio(int mes, int ano) {
        exigirAtivo();
        Relatorio r = new Relatorio(mes, ano);
        r.gerar();
        return r;
    }

    public void gerenciarUsuario(Usuario usuario) {
        exigirAtivo();
        Validacao.objeto(usuario, "Usuario");
        if (usuario == this) throw new IllegalArgumentException("Nao desative o proprio administrador");
        usuario.desativar();
        new LogAuditoria(this, "DESATIVAR_USUARIO", "Usuario", usuario.getId()).registrar();
    }

    public List<LogAuditoria> visualizarLogs() {
        exigirAtivo();
        return LogAuditoria.getLogs();
    }

    public int getNivelAcesso() { return nivelAcesso; }
    public String getDepartamento() { return departamento; }
    private void exigirAtivo() {
        if (!isAtivo()) throw new IllegalStateException("Administrador inativo");
    }

    @Override
    public void exibir() {
        System.out.println("[ADMIN] " + getNome());
    }
}
