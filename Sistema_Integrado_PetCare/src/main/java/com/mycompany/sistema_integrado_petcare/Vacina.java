/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema_integrado_petcare;

/**
 *
 * @author alunolab11
 */
public class Vacina extends RegistroClinico{
    private String nomeVacina;
    private String dataAplicacao;
    private String dataReforco;
    private String lote;
    private String fabricante;
    
    public Vacina(int id, String data, String desc, Veterinario vet, String nomeVacina) {
        RegistroClinico(id, data, desc, vet);
        this.nomeVacina = nomeVacina;
    }

    public String getNomeVacina() {
        return nomeVacina;
    }

    public String getDataReforco() {
        return dataReforco;
    }

    public void setDataReforco(String dataReforco) {
        this.dataReforco = dataReforco;
    }
    
    public boolean precisaReforco() {
        return false;
    }
    
    public String getLote() {
        return lote;
    }
    
    @Override
    public void exibir() {
        
    }    
}
