package petcare.model;

import java.time.LocalDate;
import java.time.Period;

public class Animal {
    private int id;
    private String nome;
    private String especie;
    private String raca;
    private String dataNascimento;
    private double peso;
    private Tutor tutor;
    private final HistoricoClinico historico;

    public Animal(int id, String nome, String especie, String raca, Tutor tutor) {
        this.id = Validacao.id(id);
        this.nome = require(nome, "nome");
        this.especie = require(especie, "especie");
        this.raca = require(raca, "raca");
        this.tutor = Validacao.objeto(tutor, "Tutor principal");
        this.dataNascimento = null;
        this.historico = new HistoricoClinico(id);
        if (tutor != null)
            tutor.adicionarAnimal(this);
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public HistoricoClinico getHistorico() {
        return historico;
    }

    public void setPeso(double peso) {
        if (!Double.isFinite(peso) || peso <= 0)
            throw new IllegalArgumentException("Peso deve ser maior que zero");
        this.peso = peso;
    }

    public double getPeso() {
        return peso;
    }

    public int calcularIdade() {
        if (dataNascimento == null) throw new IllegalStateException("Informe a data de nascimento");
        return Period.between(LocalDate.parse(dataNascimento), LocalDate.now()).getYears();
    }

    public void setDataNascimento(String data) {
        LocalDate nascimento = LocalDate.parse(Validacao.data(data));
        if (nascimento.isAfter(LocalDate.now())) throw new IllegalArgumentException("Nascimento futuro");
        dataNascimento = nascimento.toString();
    }

    public String getDataNascimento() { return dataNascimento; }
    public String getEspecie() { return especie; }
    public String getRaca() { return raca; }

    public void exibir() {
        System.out.println("[ANIMAL] " + nome + " | " + raca + " | Tutor: " + (tutor == null ? "-" : tutor.getNome()));
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(field + " obrigatorio");
        return value;
    }
}
