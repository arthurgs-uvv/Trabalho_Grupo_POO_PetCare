package petcare.model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class Relatorio {
    private static int sequencia;
    private int id;
    private int mes;
    private int ano;
    private int totalAtendimentos;
    private double faturamentoTotal;
    private double totalDespesas;
    private double taxaRetorno;
    private double tempMedioAtend;
    private String procedMaisRealizado;
    private double receitasPagas;

    public Relatorio(int mes, int ano) {
        if (ano < 1 || ano > 9999) throw new IllegalArgumentException("Ano invalido");
        YearMonth.of(ano, mes);
        this.id = ++sequencia;
        this.mes = mes;
        this.ano = ano;
        this.procedMaisRealizado = "Sem atendimentos";
    }
    public void gerar() {
        YearMonth periodo = YearMonth.of(ano, mes);
        List<Agendamento> atendimentos = Agendamento.getAgenda().stream()
                .filter(a -> a.getStatus().equals("CONCLUIDO"))
                .filter(a -> YearMonth.from(Validacao.horario(a.getDataHora())).equals(periodo)).toList();
        totalAtendimentos = atendimentos.size();
        faturamentoTotal = Fatura.getEmitidas().stream()
                .filter(f -> YearMonth.from(LocalDate.parse(f.getDataEmissao())).equals(periodo))
                .mapToDouble(Fatura::getValor).sum();
        receitasPagas = Fatura.getEmitidas().stream().filter(f -> f.getDataPagamento() != null)
                .filter(f -> YearMonth.from(LocalDate.parse(f.getDataPagamento())).equals(periodo))
                .mapToDouble(Fatura::getValor).sum();
        taxaRetorno = totalAtendimentos == 0 ? 0 : 100.0 * atendimentos.stream().filter(Agendamento::isRetorno).count() / totalAtendimentos;
        tempMedioAtend = atendimentos.stream().mapToInt(Agendamento::getDuracao).average().orElse(0);
        Map<String, Integer> quantidades = new LinkedHashMap<>();
        atendimentos.forEach(a -> quantidades.merge(a.getTipo(), 1, Integer::sum));
        procedMaisRealizado = "Sem atendimentos";
        int maior = 0;
        for (Map.Entry<String, Integer> entrada : quantidades.entrySet()) {
            if (entrada.getValue() > maior) { maior = entrada.getValue(); procedMaisRealizado = entrada.getKey(); }
        }
    }
    public void registrarDespesa(double valor) { totalDespesas = Validacao.valor(totalDespesas + Validacao.valor(valor)); }
    public int getTotalAtendimentos() { return totalAtendimentos; }
    public double getTotalDespesas() { return totalDespesas; }
    public double getReceitasPagas() { return receitasPagas; }
    public String getEstatisticas() {
        return String.format(java.util.Locale.forLanguageTag("pt-BR"),
                "Atendimentos: %d | Faturamento: R$%.2f | Recebido: R$%.2f | Despesas: R$%.2f | Retorno: %.1f%% | Media: %.1f min | Mais realizado: %s",
                totalAtendimentos, faturamentoTotal, receitasPagas, totalDespesas, taxaRetorno, tempMedioAtend, procedMaisRealizado);
    }
    public double calcularFaturamento() { gerar(); return faturamentoTotal; }
    public double calcularTaxaRetorno() { gerar(); return taxaRetorno; }
    public double calcularTempMedio() { gerar(); return tempMedioAtend; }
    public String getProcedMaisRealizado() { gerar(); return procedMaisRealizado; }
    public void exportar() {
        gerar();
        try {
            Path pasta = Path.of("relatorios");
            Files.createDirectories(pasta);
            Path arquivo = pasta.resolve(String.format("petcare-%04d-%02d.txt", ano, mes));
            Files.writeString(arquivo, "PETCARE - " + mes + "/" + ano + System.lineSeparator() + getEstatisticas(), StandardCharsets.UTF_8);
            System.out.println("[RELATORIO] Exportado: " + arquivo);
        } catch (IOException erro) { throw new UncheckedIOException("Falha ao exportar relatorio", erro); }
    }
    public void exibir() { gerar(); System.out.println("[RELATORIO] " + mes + "/" + ano + " | " + getEstatisticas()); }
}
