/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema_integrado_petcare;

/**
 *
 * @author alunolab11
 */
public class Cirurgia extends RegistroClinico{
    private int duracao;
    private String sala;
    private List<String> equipe;
    private String anestesia;
    private List<String> medicamentos;
    
    public Cirurgia(int id, String data, String desc, Veterinario vet, String sala) {
        RegistroClinico(id, data, desc, vet);
        this.sala = sala;
    }

    public int getDuracao() {
        return duracao;
    }

    public String getSala() {
        return sala;
    }

    public void setEquipe(<any> equipe) {
        this.equipe = equipe;
    }
    
    public void adicionarMedicamento(m) {
        
    }
    
    public boolean validarRecursos() {
        return false;
    }
    
    @Override
    public void exibir(){
        
    }
}
