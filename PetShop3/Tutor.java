package PetShop3;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tutor implements Serializable {
    private static final long serialVersionUID = 2L;

    private int    codigo;
    private String nome;
    private String endereco;
    private String telefone;   // NOVO
    private LocalDate dataNasc;
    private ArrayList<Pet> pets = new ArrayList<>();

    // ── Construtor completo (com telefone) ────────────────────────────────────
    public Tutor(int codigo, String nome, String endereco, String telefone, int d, int m, int a) {
        this.codigo    = codigo;
        this.nome      = nome;
        this.endereco  = endereco;
        this.telefone  = telefone == null ? "" : telefone.trim();
        this.dataNasc  = DateUtil.validar(d, m, a, "tutor \"" + nome + "\"");
    }

    // ── Construtor legado (sem telefone — mantém compatibilidade) ─────────────
    public Tutor(int codigo, String nome, String endereco, int d, int m, int a) {
        this(codigo, nome, endereco, "", d, m, a);
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public int       getCodigo()   { return codigo; }
    public String    getNome()     { return nome; }
    public String    getEndereco() { return endereco; }
    public String    getTelefone() { return telefone; }
    public LocalDate getDataNasc() { return dataNasc; }
    public int       getNumeroPets() { return pets.size(); }
    public int       getIdade()    { return Period.between(dataNasc, LocalDate.now()).getYears(); }

    /** Retorna cópia imutável da lista de pets. */
    public List<Pet> getPets() { return Collections.unmodifiableList(pets); }

    // ── Setters para edição ───────────────────────────────────────────────────
    public void setNome(String nome)         { this.nome = nome; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public void setTelefone(String telefone) { this.telefone = telefone == null ? "" : telefone.trim(); }
    public void setDataNasc(int d, int m, int a) {
        this.dataNasc = DateUtil.validar(d, m, a, "tutor \"" + nome + "\"");
    }

    // ── Pets ──────────────────────────────────────────────────────────────────
    public void adicionarPet(Pet p) {
        if (p == null) throw new IllegalArgumentException("Pet nao pode ser nulo.");
        pets.add(p);
    }

    public boolean removerPetPorNome(String nomePet) {
        return pets.removeIf(p -> p.getNome().equalsIgnoreCase(nomePet));
    }

    // ── toString ──────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        int d = dataNasc.getDayOfMonth(), m = dataNasc.getMonthValue(), a = dataNasc.getYear();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d. Nome: %s | Endereco: %s | Tel: %s | Nasc: %02d/%02d/%04d | Idade: %d",
                codigo, nome, endereco, telefone.isEmpty() ? "-" : telefone, d, m, a, getIdade()));
        if (pets.isEmpty()) { sb.append("\n   <Sem pets cadastrados>"); }
        else { sb.append("\n   Pets:"); for (Pet p : pets) sb.append("\n   ").append(p.toString()); }
        return sb.toString();
    }

    public String toStringBusca() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d. %s, endereco: %s, tel: %s, nascido em %s (idade: %d).",
                codigo, nome, endereco,
                telefone.isEmpty() ? "-" : telefone,
                DateUtil.formatar(dataNasc), getIdade()));
        if (pets.isEmpty()) { sb.append("\n<Sem pets cadastrados>"); }
        else { sb.append("\nPets:"); for (Pet p : pets) sb.append("\n").append(p.toString()); }
        return sb.toString();
    }
}