package petcare;

import petcare.model.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Modifier;

/** Testes de comportamento, executados com java -cp out petcare.PetCareTest. */
public class PetCareTest {
    private static int verificacoes;
    public static void main(String[] args) throws Exception {
        String hoje = LocalDate.now().toString();
        String dia = LocalDate.now().plusDays(2).toString();
        Veterinario vet = new Veterinario(1, "Vet", "vet@teste.com", "123", "Geral");
        Veterinario outroVet = new Veterinario(2, "Vet 2", "vet2@teste.com", "456", "Geral");
        Tutor tutor = new Tutor(3, "Tutor", "tutor@teste.com", "12345678900", "27999999999");
        Tutor outroTutor = new Tutor(4, "Outro", "outro@teste.com", "98765432100", "27988888888");
        Recepcionista recep = new Recepcionista(5, "Recep", "recep@teste.com", "101");
        Administrador admin = new Administrador(6, "Admin", "admin@teste.com", "abcd");
        Animal animal = new Animal(1, "Rex", "Cao", "SRD", tutor);
        Animal outroAnimal = new Animal(2, "Mel", "Gato", "SRD", outroTutor);
        Notificador notif = new Notificador("EMAIL", "teste@teste.com");
        notif.setAtivo(false);
        erro(IllegalArgumentException.class, () -> animal.setPeso(-1), "Peso negativo");
        erro(IllegalArgumentException.class, () -> animal.setPeso(Double.NaN), "Peso NaN");
        erro(IllegalArgumentException.class, () -> new Animal(9, "R", "Cao", "SRD", null), "Tutor obrigatorio");
        erro(IllegalArgumentException.class, () -> outroTutor.adicionarAnimal(animal), "RN01 tutor unico");
        animal.setDataNascimento(LocalDate.now().minusYears(3).toString());
        ok(animal.calcularIdade() == 3, "Idade real");
        erro(IllegalArgumentException.class, () -> tutor.setEmail("sem-arroba"), "Email invalido");
        ok(vet.login(vet.getEmail(), "vet1234") && vet.isAutenticado(), "Login");
        vet.logout(); ok(!vet.isAutenticado(), "Logout");
        recep.cadastrarAnimal(animal); recep.cadastrarAnimal(outroAnimal);
        ok(recep.buscarTutor(tutor.getCpf()) == tutor, "Busca CPF");
        ok(recep.buscarTutor(tutor.getTelefone()) == tutor, "Busca telefone");
        ok(recep.buscarAnimal("rex") == animal, "Busca nome");
        ok(recep.buscarAnimal("1") == animal, "Busca id");
        ok(recep.buscarAnimal("inexistente") == null, "Busca sem resultado");

        Agendamento cedo = ag(1, dia + " 07:59", animal, vet, notif);
        ok(!cedo.agendar() && cedo.getStatus().equals("NAO_AGENDADO"), "RN04 antes das 8");
        Agendamento limite = ag(2, dia + " 17:30", animal, vet, notif);
        ok(limite.agendar(), "Termino as 18 permitido");
        ok(!limite.agendar(), "Agendamento nao duplica");
        ok(!ag(3, dia + " 18:00", animal, vet, notif).agendar(), "Procedimento nao ultrapassa 18");
        vet.setDisponivel(false);
        ok(!ag(4, dia + " 08:00", animal, vet, notif).agendar(), "RN03 indisponivel");
        vet.setDisponivel(true);
        Agendamento primeiro = ag(5, dia + " 08:00", animal, vet, notif);
        ok(primeiro.agendar(), "Inicio as 8 permitido");
        Agendamento conflitoVet = ag(6, dia + " 08:15", outroAnimal, vet, notif);
        conflitoVet.setSala("Outra sala");
        ok(!conflitoVet.agendar(), "Conflito por intervalo e veterinario");
        ok(!ag(7, dia + " 08:15", outroAnimal, outroVet, notif).agendar(), "Conflito de sala");
        Agendamento seguinte = ag(8, dia + " 08:30", animal, vet, notif);
        ok(seguinte.agendar(), "Intervalos consecutivos");
        erro(IllegalArgumentException.class, () -> seguinte.reagendar(dia + " 08:10"), "Reagendamento conflita");
        ok(seguinte.getDataHora().endsWith("08:30"), "Reagendamento falho preserva horario");
        erro(java.time.DateTimeException.class, () -> seguinte.reagendar("2026-02-30 09:00"), "Data invalida estrita");
        ok(seguinte.getDataHora().endsWith("08:30"), "Data invalida nao altera estado");
        recep.cancelarConsulta(8);
        erro(IllegalStateException.class, () -> seguinte.reagendar(dia + " 09:00"), "Cancelado nao reagenda");
        ok(ag(9, dia + " 08:30", animal, vet, notif).agendar(), "Cancelamento libera horario");
        ok(vet.consultarAgenda().contains(primeiro), "Agenda real do veterinario");

        Cirurgia cirurgia = new Cirurgia(100, dia, "Cirurgia", vet, "Cirurgica");
        erro(IllegalArgumentException.class, () -> cirurgia.setDuracao(119), "RN02 minimo 120");
        cirurgia.setEquipe(List.of("Vet", "Auxiliar")); cirurgia.adicionarMedicamento("Medicamento");
        Agendamento cir = new Agendamento(10, dia + " 14:00", "CIRURGIA", animal, vet, notif);
        ok(!cir.agendar(), "Cirurgia sem recursos bloqueada");
        Estoque estoque = new Estoque(notif);
        ItemEstoque item = new ItemEstoque(1, "Medicamento", 10, 5, "L1");
        erro(IllegalArgumentException.class, () -> item.setControlado(true), "RN09 metadados obrigatorios");
        ok(!item.isControlado(), "Setter rejeitado preserva estado");
        item.setValidade(LocalDate.now().plusDays(10).toString());
        item.setResponsavelRetirada("Vet"); item.setControlado(true);
        erro(IllegalArgumentException.class, () -> item.setResponsavelRetirada(" "), "Responsavel vazio");
        ok(item.getResponsavelRetirada().equals("Vet"), "Responsavel anterior preservado");
        estoque.adicionarItem(item);
        cir.configurarCirurgia(cirurgia, estoque, Map.of(1, 3));
        ok(!estoque.reservarParaProced(cir) && item.getQuantidadeReservada() == 0, "Nao reserva fora da confirmacao da agenda");
        ok(cir.agendar(), "Cirurgia com reserva");
        ok(item.getQuantidade() == 10 && item.getQuantidadeDisponivel() == 7, "Reserva nao e consumo");
        ok(estoque.reservarParaProced(cir) && item.getQuantidadeReservada() == 3, "Reserva idempotente");
        ok(!estoque.registrarSaida(1, 8, "Vet"), "Saida nao consome saldo reservado");
        erro(IllegalArgumentException.class, () -> admin.ajustarEstoque(item, 2), "Ajuste respeita reservas");
        Agendamento sobreposicao = ag(11, dia + " 15:30", outroAnimal, vet, notif);
        sobreposicao.setSala("Outra");
        ok(!sobreposicao.agendar(), "Cirurgia reserva duas horas");
        cir.cancelar("Teste");
        ok(item.getQuantidadeReservada() == 0 && item.getQuantidade() == 10, "Cancelamento devolve reserva");
        ok(!estoque.reservarParaProced(cir), "Cancelado nao reserva novamente");
        Agendamento tarde = new Agendamento(12, dia + " 16:01", "CIRURGIA", animal, vet, notif);
        tarde.configurarCirurgia(cirurgia, estoque, Map.of(1, 2));
        ok(!tarde.agendar() && item.getQuantidadeReservada() == 0, "Cirurgia fora do horario nao reserva");
        Agendamento falta = ag(13, dia + " 12:00", animal, vet, notif);
        falta.configurarInsumos(estoque, Map.of(1, 2, 999, 1));
        ok(!falta.agendar() && item.getQuantidadeReservada() == 0, "Falta de insumo nao reserva parcialmente");
        admin.ajustarEstoque(item, 5); ok(!item.isAbaixoMinimo(), "RN08 igualdade nao abaixo");
        estoque.registrarSaida(1, 1, "Vet"); ok(item.isAbaixoMinimo(), "RN08 abaixo do minimo");
        ItemEstoque vencido = new ItemEstoque(2, "Vencido", 5, 1, "L2");
        vencido.setValidade(LocalDate.now().minusDays(1).toString());
        ok(!vencido.darSaida(1, "Vet") && !vencido.reservar(1), "Item vencido nao sai nem reserva");
        ok(!item.getMovimentacoes().isEmpty(), "Rastreabilidade preservada");

        Consulta consulta = new Consulta(101, hoje, "Consulta", vet, "Rotina");
        animal.getHistorico().adicionarConsulta(consulta);
        erro(IllegalStateException.class, consulta::finalizar, "Finalizacao exige laudo");
        erro(IllegalArgumentException.class, () -> outroAnimal.getHistorico().adicionarConsulta(consulta), "Registro nao pertence a dois animais");
        consulta.setPrescricaoObrigatoria(true);
        vet.registrarLaudo(101, "Laudo medico");
        erro(IllegalStateException.class, consulta::finalizar, "Prescricao quando aplicavel");
        vet.emitirPrescricao(101, "Prescricao");
        ok(consulta.getLaudo().equals("Laudo medico") && consulta.getPrescricao().equals("Prescricao"), "Veterinario altera registro real");
        erro(IllegalArgumentException.class, () -> outroVet.registrarLaudo(101, "Alteracao"), "Outro veterinario bloqueado");
        Agendamento concluido = ag(14, hoje + " 09:00", animal, vet, notif);
        concluido.configurarInsumos(estoque, Map.of(1, 1));
        concluido.setRetorno(true);
        ok(concluido.agendar(), "Agendamento para conclusao");
        concluido.concluir(consulta);
        ok(concluido.getStatus().equals("CONCLUIDO") && item.getQuantidade() == 3 && item.getQuantidadeReservada() == 0, "Conclusao consome reserva");
        erro(IllegalStateException.class, () -> consulta.setPrescricao("Alteracao"), "Registro finalizado imutavel");
        erro(IllegalStateException.class, () -> admin.excluirRegistro(101, "RegistroClinico"), "Exclusao finalizado bloqueada");
        Vacina vacina = new Vacina(102, LocalDate.now().minusYears(1).toString(), "Vacina", vet, "V1");
        vacina.setDataReforco(LocalDate.now().minusDays(1).toString()); ok(vacina.precisaReforco(), "RN11 reforco anterior");
        vacina.setDataReforco(hoje); ok(!vacina.precisaReforco(), "RN11 hoje nao anterior");
        animal.getHistorico().adicionarVacina(vacina);
        erro(IllegalStateException.class, animal.getHistorico()::finalizar, "Historico sem laudo nao finaliza parcialmente");
        ok(!animal.getHistorico().isFinalizado(), "Historico preservado apos falha");
        vacina.anexarLaudo("Aplicacao registrada"); animal.getHistorico().finalizar();
        erro(IllegalStateException.class, () -> vacina.setLote("Outro"), "Finalizar historico bloqueia registros");
        erro(IllegalStateException.class, () -> animal.getHistorico().adicionarConsulta(consulta), "RN07 nao adiciona");
        erro(UnsupportedOperationException.class, () -> animal.getHistorico().getConsultas().clear(), "Colecao protegida");

        Consulta descartavel = new Consulta(103, hoje, "Registro duplicado", outroVet, "Teste");
        outroAnimal.getHistorico().adicionarConsulta(descartavel);
        ok(admin.excluirRegistro(103, "RegistroClinico"), "Exclusao real de registro editavel");
        ok(outroAnimal.getHistorico().getConsultas().isEmpty(), "Exclusao remove da colecao");
        erro(IllegalArgumentException.class, () -> outroVet.registrarLaudo(103, "Teste"), "Registro excluido nao e encontrado");
        Fatura fatura = new Fatura(1, tutor, 150, "Consulta", notif);
        fatura.emitir(); fatura.emitir();
        fatura.setDataVencimento(LocalDate.now().minusDays(1).toString());
        ok(fatura.getStatus().equals("VENCIDO") && fatura.isPendente(), "Fatura vencida em aberto");
        ok(fatura.confirmarPagOnline() && fatura.getStatus().equals("PAGO"), "RN12 API simulada");
        erro(IllegalArgumentException.class, () -> new Fatura(9, tutor, Double.NaN, "Teste", notif), "Fatura NaN");
        Relatorio relatorio = new Relatorio(LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        relatorio.registrarDespesa(20); relatorio.gerar();
        ok(relatorio.calcularFaturamento() == 150 && relatorio.getReceitasPagas() == 150, "Faturamento real sem duplicar");
        ok(relatorio.getTotalAtendimentos() == 1 && relatorio.calcularTaxaRetorno() == 100, "Atendimentos e retorno");
        ok(relatorio.calcularTempMedio() == 30 && relatorio.getTotalDespesas() == 20, "Media e despesas");
        ok(relatorio.getProcedMaisRealizado().equals("CONSULTA"), "Procedimento mais frequente");
        LogAuditoria log = new LogAuditoria(admin, "TESTE", "Sistema", 1);
        int antes = admin.visualizarLogs().size(); log.registrar(); log.registrar();
        ok(admin.visualizarLogs().size() == antes + 1 && log.getDataHora() != null, "RN15 log idempotente com data");
        ok(LogAuditoria.buscarPorUsuario(admin).contains(log) && LogAuditoria.buscarPorEntidade("Sistema").contains(log), "Buscas auditoria");
        admin.gerenciarUsuario(outroVet);
        ok(!outroVet.login(outroVet.getEmail(), "vet1234"), "Usuario desativado nao entra");

        for (Class<?> tipo : List.of(Usuario.class, Administrador.class, Veterinario.class, Recepcionista.class,
                Tutor.class, Animal.class, HistoricoClinico.class, RegistroClinico.class, Consulta.class, Cirurgia.class,
                Exame.class, Vacina.class, Tratamento.class, Agendamento.class, Notificador.class, ItemEstoque.class,
                Estoque.class, Fatura.class, Relatorio.class, LogAuditoria.class)) {
            for (var campo : tipo.getDeclaredFields()) ok(Modifier.isPrivate(campo.getModifiers()), tipo.getSimpleName() + "." + campo.getName() + " privado");
        }
        System.out.println("SUCESSO: " + verificacoes + " verificacoes passaram.");
    }
    private static Agendamento ag(int id, String data, Animal animal, Veterinario vet, Notificador notif) {
        return new Agendamento(id, data, "CONSULTA", animal, vet, notif);
    }
    private static void ok(boolean condicao, String nome) {
        if (!condicao) throw new AssertionError(nome);
        verificacoes++;
    }
    private static void erro(Class<? extends Throwable> tipo, Runnable acao, String nome) {
        try { acao.run(); } catch (Throwable erro) {
            if (!tipo.isInstance(erro)) throw new AssertionError(nome + ": excecao inesperada", erro);
            verificacoes++; return;
        }
        throw new AssertionError(nome + ": deveria rejeitar a operacao");
    }
}
