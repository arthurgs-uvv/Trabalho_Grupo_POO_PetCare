# Sistema Integrado PetCare

Trabalho de Programação Orientada a Objetos I. Implementação acadêmica em Java puro,
baseada no UML e na seção 5 do arquivo `Trabalho_Grupo_POO_PetCare.pdf`.

## Entrega

- `src/petcare/model/`: todas as classes dos módulos 3.1 a 3.5 e a auxiliar `Validacao`.
- `src/petcare/Main.java`: demonstração integrada com objetos de todas as classes de domínio.
- `docs/Relatorio_POO_PetCare.pdf`: relatório de **duas páginas**, pronto para leitura.
- `docs/Relatorio_POO_PetCare.docx`: versão editável, para preencher os integrantes.
- `docs/REVISAO.md`: correções, decisões de implementação e correspondência com os requisitos.
- `test/petcare/PetCareTest.java`: testes executáveis sem dependências externas.

Antes de enviar, preencha os cinco nomes e matrículas, o professor e a data no Word,
exporte novamente para PDF e confira se continuam sendo duas páginas. A entrega no
portal da disciplina deve ser feita pelo grupo.

## Executar no Windows

Instale/use **JDK 17 ou superior** (é necessário o compilador `javac`). Abra o terminal
na pasta extraída, que contém este README, e execute:

```bat
executar.bat
executar.bat teste
```

No PowerShell, use `./executar.bat` e `./executar.bat teste`.
Os testes terminam com `SUCESSO: 207 verificacoes passaram.`. Se alguma regra falhar,
o programa termina com erro. Não é necessário habilitar `-ea`.

## Executar no Linux ou macOS

```sh
sh executar.sh
sh executar.sh teste
```

As classes compiladas ficam em `out/`. A demonstração exporta um relatório de dados
em `relatorios/petcare-AAAA-MM.txt`. Essas pastas são geradas localmente e não fazem
parte do código-fonte. As datas da demonstração acompanham a data do computador.

## Abrir em uma IDE

Abra/importe a pasta como projeto Java. Marque `src` como pasta de código e `test`
como pasta de testes, selecione JDK 17 e execute `petcare.Main`.
Também é possível executar `petcare.PetCareTest` como uma aplicação Java.

## Resultado esperado da demonstração

- Cadastro e busca de tutor/animal; login, logout e perfis com `exibir()` sobrescrito.
- Consulta, retorno, exame e cirurgia concluídos; uma consulta cancelada.
- Reserva de medicamentos, consumo ao concluir e liberação ao cancelar.
- Duas faturas emitidas: R$ 400,00 faturados e R$ 150,00 recebidos.
- Quatro atendimentos, retorno de 25%, média de duração de 52,5 minutos e R$ 40,00 em despesas.
- Prontuário finalizado, com tentativa de inserção bloqueada, e logs de auditoria.

## Os quatro conceitos pedidos no trabalho

| Conceito | Aplicação |
| --- | --- |
| Herança | Perfis estendem `Usuario`; registros especializados estendem `RegistroClinico`. |
| Encapsulamento | Atributos privados, validação antes da alteração e listas sem escrita externa. |
| Composição | `Animal` cria e mantém seu `HistoricoClinico`; outras classes mantêm objetos colaboradores. |
| Delegação | Agenda, estoque e faturamento usam `Notificador` para comunicar; `Main` coordena os objetos. |

## Escopo e simulações

Esta é a implementação de console exigida pelo trabalho de POO, não o produto web
completo descrito no documento de visão. Dados e auditoria ficam **em memória** e se
perdem ao encerrar o processo. Notificações são impressas no console; boletos, links
e pagamento são **simulações**. Não há cobrança nem mensagens externas.

Os construtores do UML que não recebem senha usam senhas demonstrativas (`vet1234`,
`recep123`, `tutor123`), alteráveis com `setSenha`. A autenticação é didática, em memória;
não é um sistema de autorização pronto para produção. Não foram implementados banco
de dados, servidor web, backup em nuvem, criptografia, envio real de mensagens ou
integrações externas. Caminhos de laudos e imagens são referências textuais, sem upload.

O exemplo é para execução local em uma única thread. A disponibilidade de sala é
verificada pela agenda; a equipe e os medicamentos cirúrgicos são informados ao
configurar o procedimento. Não há agenda independente de cada equipamento ou membro
da equipe. A duração média usa a duração planejada do atendimento concluído.
