package petcare.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogAuditoria {
    private static final List<LogAuditoria> LOGS = new ArrayList<>();
    private static int sequencia;
    private int id;
    private String dataHora;
    private Usuario usuario;
    private String acao;
    private String entidadeAfetada;
    private int idEntidade;
    private String ipOrigem;

    public LogAuditoria(Usuario usuario, String acao, String entidade) { this(usuario, acao, entidade, 0); }

    public LogAuditoria(Usuario usuario, String acao, String entidade, int idEntidade) {
        this.usuario = Validacao.objeto(usuario, "Usuario");
        this.acao = Validacao.texto(acao, "Acao");
        this.entidadeAfetada = Validacao.texto(entidade, "Entidade");
        if (idEntidade < 0) throw new IllegalArgumentException("Id da entidade invalido");
        this.idEntidade = idEntidade;
        this.ipOrigem = "127.0.0.1"; // Aplicacao local, sem servidor web.
        this.id = ++sequencia;
    }

    public void registrar() {
        if (dataHora != null) return;
        dataHora = LocalDateTime.now().toString();
        LOGS.add(this);
    }
    public int getId() { return id; }
    public int getIdEntidade() { return idEntidade; }
    public String getAcao() { return acao; }
    public Usuario getUsuario() { return usuario; }
    public String getDataHora() { return dataHora; }
    public String getEntidadeAfetada() { return entidadeAfetada; }
    public String getIpOrigem() { return ipOrigem; }
    public static List<LogAuditoria> getLogs() { return List.copyOf(LOGS); }
    public static List<LogAuditoria> buscarPorUsuario(Usuario usuario) {
        Validacao.objeto(usuario, "Usuario");
        return LOGS.stream().filter(l -> l.usuario.getId() == usuario.getId()).toList();
    }
    public static List<LogAuditoria> buscarPorEntidade(String entidade) {
        Validacao.texto(entidade, "Entidade");
        return LOGS.stream().filter(l -> l.entidadeAfetada.equalsIgnoreCase(entidade)).toList();
    }
    public void exibir() {
        System.out.println("[LOG] " + dataHora + " | Usuario: " + usuario.getNome() + " | Acao: " + acao
                + " | Entidade: " + entidadeAfetada + " #" + idEntidade);
    }
}
