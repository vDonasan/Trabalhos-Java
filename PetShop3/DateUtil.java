package PetShop3;

import java.time.LocalDate;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;

/**
 * Utilitário centralizado de validação e parsing de datas.
 * Evita que LocalDate.of() lance exceções sem mensagem amigável.
 */
public class DateUtil {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateUtil() {}

    /**
     * Valida e cria um LocalDate a partir de d/m/a.
     * Lança IllegalArgumentException com mensagem clara se a data for inválida.
     */
    public static LocalDate validar(int d, int m, int a, String contexto) {
        try {
            LocalDate data = LocalDate.of(a, m, d);
            if (data.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException(
                    "Data de nascimento do " + contexto + " não pode ser no futuro.");
            }
            return data;
        } catch (DateTimeException e) {
            throw new IllegalArgumentException(
                "Data inválida para " + contexto + ": " +
                String.format("%02d/%02d/%04d", d, m, a) +
                " (verifique dia, mês e ano).");
        }
    }

    /**
     * Faz parse de string "dd/mm/aaaa" com validação completa.
     * Aceita anos com 2 ou 4 dígitos.
     */
    public static LocalDate parse(String texto, String contexto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Data do " + contexto + " não pode ser vazia.");
        }

        String[] partes = texto.trim().split("/");
        if (partes.length != 3) {
            throw new IllegalArgumentException(
                "Data do " + contexto + " em formato inválido. Use dd/mm/aaaa.");
        }

        int d, m, a;
        try {
            d = Integer.parseInt(partes[0].trim());
            m = Integer.parseInt(partes[1].trim());
            a = Integer.parseInt(partes[2].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Data do " + contexto + " contém caracteres não numéricos.");
        }

        // Aceita anos de 2 dígitos (ex: 99 → 1999, 01 → 2001)
        if (a >= 0 && a <= 99) {
            a += (a <= LocalDate.now().getYear() % 100) ? 2000 : 1900;
        }

        return validar(d, m, a, contexto);
    }

    /** Formata LocalDate como dd/MM/yyyy. */
    public static String formatar(LocalDate data) {
        return data == null ? "--/--/----" : data.format(FMT);
    }
}
