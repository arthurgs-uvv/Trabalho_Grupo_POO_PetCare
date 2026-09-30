package petcare.model;

import java.time.LocalDate;

public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String perfil;
    private boolean ativo;
    private boolean autenticado;
    private String dataCadastro;

    public Usuario(int id, String nome, String email, String senha, String perfil) {
        this.id = Validacao.id(id);
        setNome(nome);
        setEmail(email);
        setSenha(senha);
        if (!java.util.List.of("ADMIN", "VET", "RECEP", "TUTOR").contains(perfil))
            throw new IllegalArgumentException("Perfil invalido");
        this.perfil = perfil;
        this.ativo = true;
        this.dataCadastro = LocalDate.now().toString();
    }

    public boolean login(String email, String senha) {
        autenticado = ativo && this.email.equals(email) && this.senha.equals(senha);
        return autenticado;
    }

    public void logout() { autenticado = false; }

    public boolean isAutenticado() { return autenticado; }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getPerfil() {
        return perfil;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome obrigatorio");
        this.nome = nome;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("[^\\s@]+@[^\\s@]+"))
            throw new IllegalArgumentException("Email invalido");
        this.email = email;
    }

    public void setSenha(String senha) {
        if (senha == null || senha.length() < 4)
            throw new IllegalArgumentException("Senha deve ter ao menos 4 caracteres");
        this.senha = senha;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void desativar() {
        ativo = false;
        logout();
    }

    public void exibir() {
        System.out.println("[USUARIO] " + nome + " | Perfil: " + perfil);
    }
}
