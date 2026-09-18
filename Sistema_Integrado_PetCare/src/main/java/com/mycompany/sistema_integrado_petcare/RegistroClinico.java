/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema_integrado_petcare;

/**
 *
 * @author alunolab11
 */
public class RegistroClinico {
    private int id;
    private String data;
    private String descricao;
    private Veterinario veterinario;
    private String laudoAnexo;
    private boolean finalizado;

    public RegistroClinico(int id, String data, String descricao, Veterinario veterinario) {
        this.id = id;
        this.data = data;
        this.descricao = descricao;
        this.veterinario = veterinario;
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
    
    public void finalizar() {
        
    }
    
    public boolean isFinalizado() {
        return false;
    }
    
    public void anexarLaudo(path) {
        
    }
    
    public String getLaudo() {
        return "";
    }
    
    public void exibir() {
        
    }
}
