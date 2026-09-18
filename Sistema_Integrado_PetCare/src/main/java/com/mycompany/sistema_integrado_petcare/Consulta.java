/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema_integrado_petcare;

/**
 *
 * @author alunolab11
 */
public class Consulta extends RegistroClinico{
    private String motivo;
    private String prescricao;
    private String dataRetorno;
    
    public Consulta(int id, String data, String descricao, Veterinario veterinario, String motivo) {
        RegistroClinico(id, data, descricao, veterinario);
        this.motivo = motivo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setPrescricao(String prescricao) {
        this.prescricao = prescricao;
    }

    public String getPrescricao() {
        return prescricao;
    }
    
    public void setDataRetorno(String dataRetorno) {
        this.dataRetorno = dataRetorno;
    }
    
    public String getDataRetorno() {
        return dataRetorno;
    }
    
    @Override
    public void exibir() {
    
    }
}
