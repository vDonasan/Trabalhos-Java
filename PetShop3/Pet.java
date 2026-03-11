package PetShop3;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Pet implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String tipo;
    private LocalDate dataNasc;

    // ── Construtor sem data ──────────────────────────────────────────────────
    public Pet(String nome, String tipo) {
        this.nome = nome;
        this.tipo = tipo;
        this.dataNasc = null;
    }

    // ── Construtor com data (valida antes de criar LocalDate) ─────────────────
    public Pet(String nome, String tipo, int d, int m, int a) {
        this.nome = nome;
        this.tipo = tipo;
        this.dataNasc = DateUtil.validar(d, m, a, "pet \"" + nome + "\"");
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getNome()         { return nome; }
    public String getTipo()         { return tipo; }
    public LocalDate getDataNasc()  { return dataNasc; }

    public int getIdade() {
        if (dataNasc == null) return 0;
        return Period.between(dataNasc, LocalDate.now()).getYears();
    }

    // ── toString compacto usado pela listagem / busca ─────────────────────────
    @Override
    public String toString() {
        if (dataNasc == null) {
            return String.format("- %s   Tipo: %s   Dt.nasc: --/--/----   Idade: --", nome, tipo);
        }
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        return String.format("- %s   Tipo: %s   Dt.nasc: %s   Idade: %d",
                nome, tipo, dataNasc.format(f), getIdade());
    }
}
