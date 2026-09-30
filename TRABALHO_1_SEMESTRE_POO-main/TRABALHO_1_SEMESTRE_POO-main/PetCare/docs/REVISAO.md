# Revisão e conferência da entrega

Base revisada: commit `cd2a765a8658997f8a69341923836a0847e8e786` do repositório
`Thaleschequer1004/TRABALHO_1_SEMESTRE`. As classes existentes foram aproveitadas e
completadas, mantendo o pacote `petcare.model` e os construtores do UML.

## Divergências do enunciado

1. A seção 5.1 menciona 14 classes, mas os diagramas nomeiam **19 classes** e o
   `HistoricoClinico` referencia também `Tratamento`. Foram implementadas as **20
   classes de domínio**, além de `Main` e da auxiliar interna `Validacao`.
2. O trabalho chama de pilares Herança, Encapsulamento, Composição e Delegação.
   O relatório explica exatamente esses quatro conceitos. Polimorfismo aparece
   como consequência das sobrescritas com `@Override`.
3. As regras detalhadas na seção 5.2 do trabalho prevalecem sobre a visão ampliada:
   estoque abaixo do mínimo usa `<`, e reforço vencido usa data anterior a hoje.
   O lembrete é acionado explicitamente; não há um serviço automático de calendário.
4. Foi acrescentado `NAO_AGENDADO` antes da confirmação para evitar que uma reserva
   recusada apareça como `AGENDADO`. Os demais estados seguem o UML.
5. Uma consulta ou exame usa 30 minutos por padrão, configuráveis antes de agendar;
   a cirurgia usa no mínimo 120. Todo o intervalo precisa caber entre 08h e 18h.
6. Relações com objetos já existentes, como Tutor, Veterinario e Notificador, são
   associações/agregações em UML estrito. A composição forte mais clara é
   Animal → HistoricoClinico, criado pelo próprio animal. O relatório distingue isso.

## O que foi corrigido

| Módulo | Problema encontrado | Resultado |
| --- | --- | --- |
| Usuários | `logout` vazio e gerenciamento sem ação | Estado de login e desativação efetivos. |
| Recepção | Cadastro não armazenava, buscas retornavam `null` e cancelamento vazio | Cadastro compartilhado em memória, buscas por dados e cancelamento por id. |
| Veterinário | Laudo/receita não alteravam registros; agenda vazia | Atualização dos registros vinculados e consulta da agenda real. |
| Animal | Tutor nulo permitido e nascimento fictício | Tutor obrigatório, vínculo único e nascimento informado explicitamente. |
| Clínico | Histórico fechado não bloqueava edição de objetos internos | Finalização valida todos os laudos e prescrições obrigatórias antes de bloquear todos os registros. |
| Agenda | Conflito apenas no início; reagendamento ignorava conflito | Sobreposição por intervalo, sala, animal e veterinário; alteração recusada preserva o horário anterior. |
| Estoque | Reserva descontava como saída e não tinha estorno | Saldo físico separado do reservado; cancelamento libera e conclusão consome. |
| Controlados | Setter falhava depois de alterar estado | Validação anterior à alteração; lote, validade e responsável obrigatórios. |
| Financeiro | Vencimento não mudava status e relatório sempre zerado | Faturas vencidas, faturamento, recebimentos, despesas e estatísticas calculados. |
| Auditoria | Repetir `registrar()` duplicava o log; consulta administrativa vazia | Registro idempotente com data, usuário, ação e entidade, consultável. |

## Regras obrigatórias e onde conferir

| Regra | Implementação principal | Evidência |
| --- | --- | --- |
| RN01 | `Animal` e `Tutor.adicionarAnimal` | Tutor nulo e segundo vínculo rejeitados. |
| RN02 | `Cirurgia.setDuracao`, `Agendamento` | Cirurgia de 119 min recusada e conflito dentro das duas horas. |
| RN03 | `Agendamento.validarVeterinario` | Veterinário indisponível não agenda. |
| RN04 | `Agendamento.validarHorario` | Início às 08h e término às 18h permitidos; demais intervalos recusados. |
| RN07 | `HistoricoClinico.finalizar`, `RegistroClinico.ensureEditable` | Adição, edição e exclusão de registros finalizados bloqueadas. |
| RN08 | `ItemEstoque.isAbaixoMinimo` | Igual ao mínimo retorna falso; abaixo retorna verdadeiro. |
| RN09 | `ItemEstoque.setControlado` | Não ativa controle sem lote, validade e responsável. |
| RN11 | `Vacina.precisaReforco` | Ontem retorna verdadeiro; hoje retorna falso. |
| RN12 | `Fatura.confirmarPagOnline` | Retorna verdadeiro e registra pagamento simulado. |
| RN15 | `LogAuditoria.registrar` | Data/hora, usuário e ação registrados, sem duplicação. |

Os testes também cobrem conflitos de sala, estorno de reserva, falta de insumos sem
reserva parcial, medicamentos vencidos, dados inválidos, proteção de listas,
relatórios com valores conhecidos e modificadores `private` de todos os atributos.

## Convenções para apresentar o código

- `Administrador.ajustarEstoque(item, qtd)` define o **saldo absoluto**, respeitando
  o que já está reservado. Entradas e saídas normais usam os métodos de `Estoque`.
- `Administrador.gerenciarUsuario(u)` desativa o usuário; não permite autodesativação.
- `Administrador.excluirRegistro(id, tipo)` suporta `RegistroClinico`, ligado a um
  animal cadastrado pela recepção, e recusa registro finalizado.
- `Veterinario.registrarLaudo` localiza um registro já incluído no histórico.
  `emitirPrescricao` atua em `Consulta`, classe que possui esse atributo no UML.
- `Consulta.setPrescricaoObrigatoria(true)` exige prescrição para finalizar quando
  aplicável. Todo registro exige laudo/relatório médico para finalizar.
- `Relatorio` considera atendimentos concluídos no mês do agendamento, faturamento
  no mês da emissão e receitas no mês do pagamento. Retorno é a proporção dos
  atendimentos explicitamente marcados como retorno. Em empate, o primeiro tipo
  encontrado na agenda é exibido. Despesas são informadas no relatório do período.
- `LogAuditoria` registra ações clínicas, alterações administrativas e eventos da
  agenda; `ItemEstoque.getMovimentacoes` preserva as entradas, saídas e reservas com
  lote, quantidade, data/hora e responsável. É rastreabilidade local demonstrativa.

## Verificação realizada

Compilação em Java 17 com `-Xlint:all`, sem avisos. Execução de `petcare.Main` concluída.
`petcare.PetCareTest`: **207 verificações passaram**, incluindo verificações estruturais
por atributo (a contagem não representa 207 cenários independentes).

O script Linux foi executado neste ambiente. O arquivo `.bat` foi preparado para
Windows, mas não foi executado em uma máquina Windows nesta revisão.

## Antes de entregar

- Preencher cinco nomes, matrículas, professor e data no relatório editável.
- Exportar novamente o relatório para PDF e manter duas páginas.
- Rodar a demonstração e os testes em um computador do grupo.
- Ler o relatório e conseguir explicar o código de cada módulo.
- Enviar o código-fonte e o relatório no portal indicado pelo professor.
