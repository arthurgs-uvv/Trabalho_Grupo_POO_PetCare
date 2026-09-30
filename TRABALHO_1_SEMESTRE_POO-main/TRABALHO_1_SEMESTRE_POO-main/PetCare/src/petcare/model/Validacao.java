package petcare.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/** Validacoes compartilhadas; nenhuma alteracao acontece antes de validar. */
final class Validacao {
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private Validacao() { }

    static String texto(String valor, String campo) {
        if (valor == null || valor.isBlank())
            throw new IllegalArgumentException(campo + " obrigatorio");
        return valor.trim();
    }

    static int id(int valor) {
        if (valor <= 0) throw new IllegalArgumentException("Id deve ser positivo");
        return valor;
    }

    static <T> T objeto(T valor, String campo) {
        if (valor == null) throw new IllegalArgumentException(campo + " obrigatorio");
        return valor;
    }

    static String data(String valor) {
        return LocalDate.parse(texto(valor, "Data (yyyy-MM-dd)")).toString();
    }

    static LocalDateTime horario(String valor) {
        return LocalDateTime.parse(texto(valor, "Data e hora (yyyy-MM-dd HH:mm)"), DATA_HORA);
    }

    static double valor(double valor) {
        if (!Double.isFinite(valor) || valor < 0)
            throw new IllegalArgumentException("Valor deve ser finito e nao negativo");
        return valor;
    }
}
