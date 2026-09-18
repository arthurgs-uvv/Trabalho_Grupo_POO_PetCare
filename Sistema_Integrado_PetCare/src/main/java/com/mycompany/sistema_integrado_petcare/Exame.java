/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema_integrado_petcare;

/**
 *
 * @author alunolab11
 */
public class Exame extends RegistroClinico{
    private String tipo;
    private String resultado;
    private String imagemAnexo;
    private String laboratorio;
    
    public Exame(int id, String data, String desc, Veterinario vet, String tipo) {
        RegistroClinico(id, data, desc, vet);
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getResultado() {
        return resultado;
    }
    
    public void anexarImagem(path) {
        
    }
    
    public String getImagem() {
        return imagemAnexo;
    }
    
    @Override
    public void exibir() {
        
    }
}
