package petcare;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import petcare.model.*;

/** Demonstracao reproduzivel de todos os modulos, sem bibliotecas externas. */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA PETCARE ===");
        LocalDate hoje = LocalDate.now();
        String data = hoje.toString();
        Usuario usuario = new Usuario(5, "Usuario demonstracao", "usuario@petcare.com", "demo123", "RECEP");
        Administrador admin = new Administrador(1, "Ana Administradora", "admin@petcare.com", "admin123");
        Veterinario vet = new Veterinario(2, "Ana Veterinaria", "ana@petcare.com", "CRMV-123", "Clinica geral");
        Recepcionista recepcao = new Recepcionista(3, "Bia Recepcao", "bia@petcare.com", "101");
        Tutor tutor = new Tutor(4, "Joao Silva", "joao@email.com", "12345678900", "27999999999");
        tutor.setEndereco("Rua das Flores, 10");
        System.out.println("[LOGIN] Veterinario: " + vet.login(vet.getEmail(), "vet1234"));
        for (Usuario perfil : List.of(usuario, admin, vet, recepcao, tutor)) perfil.exibir(); // Polimorfismo.

        Animal rex = new Animal(10, "Rex", "Cao", "Labrador", tutor);
        rex.setPeso(28.5);
        rex.setDataNascimento(hoje.minusYears(3).toString());
        recepcao.cadastrarTutor(tutor);
        recepcao.cadastrarAnimal(rex);
        rex.exibir();
        System.out.println("[CADASTRO] Busca por CPF: " + recepcao.buscarTutor(tutor.getCpf()).getNome());
        System.out.println("[CADASTRO] Busca por codigo: " + recepcao.buscarAnimal("10").getNome());
        System.out.println("[ANIMAL] Idade: " + rex.calcularIdade());

        HistoricoClinico historico = rex.getHistorico();
        Consulta consulta = new Consulta(100, data, "Avaliacao clinica", vet, "Rotina");
        historico.adicionarConsulta(consulta);
        vet.registrarLaudo(100, "Paciente avaliado, sem alteracoes relevantes.");
        vet.emitirPrescricao(100, "Repouso e hidratacao");
        consulta.setDataRetorno(hoje.plusDays(7).toString());
        Consulta retorno = new Consulta(105, data, "Reavaliacao", vet, "Retorno");
        historico.adicionarConsulta(retorno);
        retorno.anexarLaudo("Boa evolucao; sem necessidade de prescricao.");
        Vacina vacina = new Vacina(101, hoje.minusYears(1).toString(), "Aplicacao anual", vet, "Antirrabica");
        vacina.setDataReforco(hoje.minusDays(1).toString());
        vacina.setLote("VAC-2026");
        vacina.setFabricante("Fabricante demonstrativo");
        vacina.anexarLaudo("Aplicacao registrada.");
        historico.adicionarVacina(vacina);
        Exame exame = new Exame(103, data, "Hemograma", vet, "Laboratorial");
        exame.setResultado("Sem alteracoes");
        exame.setLaboratorio("Laboratorio demonstrativo");
        exame.anexarImagem("laudos/rex-hemograma.png"); // Caminho ilustrativo, sem upload externo.
        exame.anexarLaudo("Resultado avaliado pelo veterinario.");
        historico.adicionarExame(exame);
        Tratamento tratamento = new Tratamento(104, data, "Tratamento preventivo", vet, "Acompanhamento por 5 dias");
        tratamento.anexarLaudo("Plano de acompanhamento registrado.");
        historico.adicionarTratamento(tratamento);
        RegistroClinico registroBase = new RegistroClinico(106, data, "Observacao geral", vet);
        registroBase.anexarLaudo("Observacao registrada.");
        registroBase.finalizar();

        Notificador mensagens = new Notificador("EMAIL", tutor.getEmail());
        Notificador alertas = new Notificador("EMAIL", admin.getEmail());
        Estoque estoque = new Estoque(alertas);
        ItemEstoque medicamento = new ItemEstoque(300, "Anestesico demonstrativo", 50, 10, "LOT-2026");
        medicamento.setValidade(hoje.plusMonths(6).toString());
        medicamento.setResponsavelRetirada(vet.getNome());
        medicamento.setControlado(true);
        estoque.adicionarItem(medicamento);
        estoque.registrarSaida(300, 45, vet.getNome());
        estoque.registrarEntrada(300, 20, admin.getNome());
        admin.ajustarEstoque(medicamento, 30); // Novo saldo absoluto, nao uma entrada.

        Agendamento ag = new Agendamento(200, data + " 09:00", "CONSULTA", rex, vet, mensagens);
        exigir(recepcao.agendarConsulta(ag), "Consulta nao agendada");
        ag.reagendar(data + " 09:30");
        ag.concluir(consulta);
        Agendamento agRetorno = new Agendamento(201, data + " 10:30", "CONSULTA", rex, vet, mensagens);
        agRetorno.setRetorno(true);
        exigir(agRetorno.agendar(), "Retorno nao agendado");
        agRetorno.concluir(retorno);
        Agendamento agExame = new Agendamento(202, data + " 11:00", "EXAME", rex, vet, mensagens);
        exigir(agExame.agendar(), "Exame nao agendado");
        agExame.concluir(exame);

        Cirurgia cirurgia = new Cirurgia(102, data, "Procedimento odontologico", vet, "Sala cirurgica");
        cirurgia.setEquipe(List.of("Dra. Ana", "Auxiliar Carlos"));
        cirurgia.setAnestesia("Protocolo demonstrativo");
        cirurgia.adicionarMedicamento(medicamento.getNome());
        cirurgia.anexarLaudo("Procedimento e recuperacao registrados.");
        historico.adicionarCirurgia(cirurgia);
        Agendamento agCirurgia = new Agendamento(203, data + " 14:00", "CIRURGIA", rex, vet, mensagens);
        agCirurgia.configurarCirurgia(cirurgia, estoque, Map.of(300, 2));
        exigir(agCirurgia.agendar(), "Cirurgia nao agendada");
        estoque.exibir();
        agCirurgia.concluir(cirurgia);
        Agendamento cancelado = new Agendamento(204, data + " 17:00", "CONSULTA", rex, vet, mensagens);
        cancelado.configurarInsumos(estoque, Map.of(300, 1));
        exigir(cancelado.agendar(), "Consulta de cancelamento nao agendada");
        recepcao.cancelarConsulta(204);
        System.out.println("[AGENDA] " + vet.consultarAgenda().size() + " registros com historico");
        ag.getHistorico().forEach(System.out::println);
        ag.exibir();
        System.out.println("[VACINA] Precisa reforco: " + vacina.precisaReforco());
        mensagens.enviarLembreteVacina(rex, vacina.getDataReforco());
        for (RegistroClinico r : List.of(registroBase, consulta, retorno, vacina, cirurgia, exame, tratamento)) r.exibir();
        historico.exibir();
        tutor.verHistoricoAnimal(rex.getId());

        Fatura fatura = new Fatura(400, tutor, 150.0, "Consulta clinica", mensagens);
        fatura.emitir(); fatura.exibir();
        System.out.println("[PAGAMENTO SIMULADO] " + fatura.confirmarPagOnline());
        Fatura pendente = new Fatura(401, tutor, 250.0, "Exame e acompanhamento", mensagens);
        pendente.emitir();
        System.out.println("[FATURAS] " + tutor.verFaturas().size());
        Relatorio relatorio = admin.gerarRelatorio(hoje.getMonthValue(), hoje.getYear());
        relatorio.registrarDespesa(40);
        relatorio.exibir(); relatorio.exportar();
        estoque.gerarRelatorioRastreab();
        estoque.alertarVencimentos();
        LogAuditoria log = new LogAuditoria(admin, "DEMONSTRACAO", "Sistema");
        log.registrar(); log.exibir();
        System.out.println("[AUDITORIA] Logs do administrador: " + LogAuditoria.buscarPorUsuario(admin).size());
        System.out.println("[AUDITORIA] Total: " + admin.visualizarLogs().size());

        historico.finalizar();
        try {
            historico.adicionarConsulta(new Consulta(107, data, "Tentativa", vet, "Teste"));
            throw new AssertionError("Historico deveria estar bloqueado");
        } catch (IllegalStateException erro) {
            System.out.println("[REGRA] Historico finalizado bloqueou nova insercao.");
        }
        admin.gerenciarUsuario(usuario);
        vet.logout();
        System.out.println("=== DEMONSTRACAO CONCLUIDA ===");
    }
    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new IllegalStateException(mensagem);
    }
}
