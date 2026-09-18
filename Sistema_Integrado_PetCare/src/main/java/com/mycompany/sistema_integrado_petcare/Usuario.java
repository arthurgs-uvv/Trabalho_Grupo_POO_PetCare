/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sip;

/**
 *
 * @author alunolab11
 */
public class Usuario {

    private int id;
    private String nome, email, senha, perfil, dataCadastro;
    private boolean ativo;

    public Usuario(int id, String nome, String email, String senha, String perfil) {
        this.id = id;
        this.nome = nome;
        setEmail(email);
        this.senha = senha;
        this.perfil = perfil;
    }

    public boolean login(String email, String senha) {
        if (nome.equals(getNome()) && email.equals(getEmail())){
            return true;
        }
        else
            System.out.println("Error: Nome ou Email errado");
        return false;
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

    public void setNome(String nome) {
        if (!nome.isEmpty()) {
            this.nome = nome;
        } else {
            System.out.println("ERROR: Nome está vazio");
        }
    }

    public final void setEmail(String email) {
        if (email.contains("@")) {
            this.email = email;
        } else {
            System.out.println("Email em formato incorreto");
        }
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}
