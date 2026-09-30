package petcare.model;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class Estoque {
    private final List<ItemEstoque> itens = new ArrayList<>();
    private final Notificador notificador;
    private final Map<Agendamento, Map<Integer, Integer>> reservas = new LinkedHashMap<>();

    public Estoque(Notificador notificador) { this.notificador = Validacao.objeto(notificador, "Notificador"); }
    public void adicionarItem(ItemEstoque item) {
        Validacao.objeto(item, "Item");
        if (itens.stream().anyMatch(i -> i.getId() == item.getId())) throw new IllegalArgumentException("Id de item duplicado");
        itens.add(item);
        verificarAlerta(item);
    }
    public void registrarEntrada(int id, int qtd, String responsavel) {
        ItemEstoque item = buscarPorId(id);
        item.darEntrada(qtd, responsavel);
        verificarAlerta(item);
    }
    public boolean registrarSaida(int id, int qtd, String responsavel) {
        ItemEstoque item = buscarPorId(id);
        boolean ok = item.darSaida(qtd, responsavel);
        if (ok) verificarAlerta(item);
        return ok;
    }
    public boolean reservarParaProced(Agendamento agendamento) {
        Validacao.objeto(agendamento, "Agendamento");
        if (!agendamento.podeReservar()) return false;
        if (reservas.containsKey(agendamento)) return true;
        Map<Integer, Integer> pedido = agendamento.getInsumos();
        if (pedido.isEmpty()) return !agendamento.getTipo().equals("CIRURGIA");
        // Confere todos os itens antes de modificar qualquer quantidade.
        for (Map.Entry<Integer, Integer> entrada : pedido.entrySet()) {
            ItemEstoque item = itens.stream().filter(i -> i.getId() == entrada.getKey()).findFirst().orElse(null);
            if (item == null || item.isVencido() || item.getQuantidadeDisponivel() < entrada.getValue()
                    || !item.validoEm(agendamento.getDataHora().substring(0, 10))) return false;
        }
        pedido.forEach((id, qtd) -> buscarPorId(id).reservar(qtd));
        reservas.put(agendamento, new LinkedHashMap<>(pedido));
        return true;
    }
    boolean reservaValida(Agendamento agendamento, String data) {
        Map<Integer, Integer> pedido = reservas.get(agendamento);
        if (pedido == null) return agendamento.getInsumos().isEmpty();
        return pedido.keySet().stream().allMatch(id -> !buscarPorId(id).isVencido() && buscarPorId(id).validoEm(data));
    }
    void liberarReserva(Agendamento agendamento) {
        Map<Integer, Integer> pedido = reservas.remove(agendamento);
        if (pedido != null) pedido.forEach((id, qtd) -> buscarPorId(id).liberarReserva(qtd));
    }
    void consumirReserva(Agendamento agendamento, String responsavel) {
        if (!reservaValida(agendamento, agendamento.getDataHora().substring(0, 10)))
            throw new IllegalStateException("Insumos vencidos ou reserva ausente");
        Map<Integer, Integer> pedido = reservas.remove(agendamento);
        if (pedido != null) pedido.forEach((id, qtd) -> {
            ItemEstoque item = buscarPorId(id);
            item.consumirReserva(qtd, responsavel);
            verificarAlerta(item);
        });
    }
    public void verificarAlertas() { itens.forEach(this::verificarAlerta); }
    private void verificarAlerta(ItemEstoque item) {
        if (item.isAbaixoMinimo()) notificador.enviarAlertaEstoque(item);
    }
    public ItemEstoque buscarItem(String nome) {
        return itens.stream().filter(i -> i.getNome().equalsIgnoreCase(nome)).findFirst().orElse(null);
    }
    public List<ItemEstoque> getItens() { return List.copyOf(itens); }
    public List<ItemEstoque> rastrearControlados() { return itens.stream().filter(ItemEstoque::isControlado).toList(); }
    public void alertarVencimentos() {
        itens.stream().filter(ItemEstoque::isVencido)
                .forEach(i -> notificador.enviarAlerta(notificador.getDestinatario(), "Item vencido: " + i.getNome()));
    }
    public void gerarRelatorioRastreab() {
        rastrearControlados().forEach(i -> { i.exibir(); i.getMovimentacoes().forEach(System.out::println); });
    }
    public void exibir() { itens.forEach(ItemEstoque::exibir); }
    private ItemEstoque buscarPorId(int id) {
        return itens.stream().filter(i -> i.getId() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item nao encontrado"));
    }
}
