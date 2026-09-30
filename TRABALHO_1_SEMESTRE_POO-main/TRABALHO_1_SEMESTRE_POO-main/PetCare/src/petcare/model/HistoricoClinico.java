package petcare.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoricoClinico {
    private int idAnimal;
    private final List<Consulta> consultas;
    private final List<Vacina> vacinas;
    private final List<Cirurgia> cirurgias;
    private final List<Exame> exames;
    private final List<Tratamento> tratamentos;
    private boolean finalizado;

    public HistoricoClinico(int idAnimal) {
        this.idAnimal = Validacao.id(idAnimal);
        consultas = new ArrayList<>();
        vacinas = new ArrayList<>();
        cirurgias = new ArrayList<>();
        exames = new ArrayList<>();
        tratamentos = new ArrayList<>();
    }

    public void adicionarConsulta(Consulta c) {
        ensureEditable();
        validarRegistro(c);
        if (!consultas.contains(c)) consultas.add(c);
    }

    public void adicionarVacina(Vacina v) {
        ensureEditable();
        validarRegistro(v);
        if (!vacinas.contains(v)) vacinas.add(v);
    }

    public void adicionarCirurgia(Cirurgia c) {
        ensureEditable();
        validarRegistro(c);
        if (!cirurgias.contains(c)) cirurgias.add(c);
    }

    public void adicionarExame(Exame e) {
        ensureEditable();
        validarRegistro(e);
        if (!exames.contains(e)) exames.add(e);
    }

    public void adicionarTratamento(Tratamento t) {
        ensureEditable();
        validarRegistro(t);
        if (!tratamentos.contains(t)) tratamentos.add(t);
    }

    public void finalizar() {
        // Valida todos antes de finalizar qualquer um, evitando fechamento parcial.
        List<RegistroClinico> registros = todos();
        for (RegistroClinico registro : registros) {
            if (registro instanceof Consulta consulta && consulta.isPrescricaoObrigatoria() && consulta.getPrescricao() == null)
                throw new IllegalStateException("Ha consulta sem prescricao obrigatoria");
            if (registro.getLaudo() == null) throw new IllegalStateException("Ha registro sem relatorio medico");
        }
        registros.forEach(RegistroClinico::finalizar);
        finalizado = true;
    }

    public boolean isFinalizado() {
        return finalizado;
    }

    public List<Consulta> getConsultas() {
        return Collections.unmodifiableList(consultas);
    }

    public List<Vacina> getVacinas() {
        return Collections.unmodifiableList(vacinas);
    }

    public List<Cirurgia> getCirurgias() {
        return Collections.unmodifiableList(cirurgias);
    }

    public List<Exame> getExames() {
        return Collections.unmodifiableList(exames);
    }

    public void exibir() {
        System.out.println("[HISTORICO] Consultas: " + consultas.size() + " | Vacinas: " + vacinas.size()
                + " | Cirurgias: " + cirurgias.size());
    }

    public List<Tratamento> getTratamentos() { return Collections.unmodifiableList(tratamentos); }

    private List<RegistroClinico> todos() {
        List<RegistroClinico> lista = new ArrayList<>();
        lista.addAll(consultas); lista.addAll(vacinas); lista.addAll(cirurgias);
        lista.addAll(exames); lista.addAll(tratamentos);
        return lista;
    }

    private void validarRegistro(RegistroClinico registro) {
        Validacao.objeto(registro, "Registro");
        RegistroClinico.cadastrar(registro, idAnimal);
    }

    boolean excluir(int id) {
        RegistroClinico registro = todos().stream().filter(r -> r.getId() == id).findFirst().orElse(null);
        if (registro == null) return false;
        ensureEditable();
        if (registro.isFinalizado()) throw new IllegalStateException("Registro finalizado nao pode ser excluido");
        boolean removido = consultas.remove(registro) || vacinas.remove(registro) || cirurgias.remove(registro)
                || exames.remove(registro) || tratamentos.remove(registro);
        if (removido) RegistroClinico.remover(registro);
        return removido;
    }

    private void ensureEditable() {
        if (finalizado)
            throw new IllegalStateException("Historico clinico finalizado e bloqueado");
    }
}
