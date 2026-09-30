package petcare.model;

import java.util.List;

public class Notificador {
    private String canal;
    private String destinatario;
    private boolean ativo;

    public Notificador(String canal, String destinatario) {
        setCanal(canal);
        this.destinatario = requireText(destinatario, "destinatario");
        this.ativo = true;
    }

    public String getDestinatario() { return destinatario; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getCanal() { return canal; }

    public void setCanal(String canal) {
        String valor = requireText(canal, "canal").toUpperCase();
        if (!valor.equals("EMAIL") && !valor.equals("SMS") && !valor.equals("APP")) {
            throw new IllegalArgumentException("Canal deve ser EMAIL, SMS ou APP");
        }
        this.canal = valor;
    }

    public void enviarConfirmacao(Agendamento agendamento) { enviarAlerta(contato(agendamento.getAnimal().getTutor()), "Agendamento confirmado: " + agendamento.getTipo()); }
    public void enviarCancelamento(Agendamento agendamento) { enviarAlerta(contato(agendamento.getAnimal().getTutor()), "Agendamento cancelado: " + agendamento.getTipo()); }
    public void enviarReagendamento(Agendamento agendamento) { enviarAlerta(contato(agendamento.getAnimal().getTutor()), "Agendamento reagendado para " + agendamento.getDataHora()); }
    public void enviarLembreteVacina(Animal animal, String data) { enviarAlerta(contato(animal.getTutor()), "Vacina de " + animal.getNome() + " em " + data); }
    public void enviarAlertaEstoque(ItemEstoque item) { enviarAlerta(destinatario, "Estoque baixo: " + item.getNome()); }
    public void enviarFatura(Fatura fatura) { enviarAlerta(contato(fatura.getTutor()), "Fatura simulada R$" + String.format("%.2f", fatura.getValor()) + " | " + fatura.gerarBoleto() + " | " + fatura.gerarLinkPagamento()); }
    public void enviarAlerta(String destinatario, String mensagem) { requireText(destinatario, "Destinatario"); requireText(mensagem, "Mensagem"); if (ativo) System.out.println("[NOTIFICADOR] " + canal + " -> " + destinatario + ": " + mensagem); }
    public void enviarCampanha(List<String> lista, String mensagem) { for (String contato : lista) enviarAlerta(contato, mensagem); }

    private String contato(Tutor tutor) {
        if (canal.equals("SMS")) return tutor.getTelefone();
        if (canal.equals("APP")) return "tutor#" + tutor.getId();
        return tutor.getEmail();
    }

    private static String requireText(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException(campo + " obrigatorio");
        return valor;
    }
}
