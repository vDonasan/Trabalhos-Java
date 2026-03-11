package PetShop3;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PetShop {

    private static final Path DIR        = Paths.get(System.getProperty("user.home"), ".petshop");
    private static final Path ARQUIVO   = DIR.resolve("tutores.json");
    private static final Path META      = DIR.resolve("meta.txt");  // persiste o maior codigo usado

    private final ArrayList<Tutor> tutores = new ArrayList<>();
    private final AtomicInteger proximoCodigo = new AtomicInteger(1);

    public PetShop() {}

    // ── Código ────────────────────────────────────────────────────────────────
    public int geraCodigo() { return proximoCodigo.getAndIncrement(); }

    private void sincronizarContador() {
        int maiorTutor = tutores.stream().mapToInt(Tutor::getCodigo).max().orElse(0);
        // Lê o maior código já usado salvo em meta.txt (persiste após exclusões)
        int maiorMeta = lerMeta();
        proximoCodigo.set(Math.max(maiorTutor, maiorMeta) + 1);
    }

    private int lerMeta() {
        try { if (Files.exists(META)) return Integer.parseInt(Files.readString(META).trim()); }
        catch (Exception ignored) {}
        return 0;
    }

    private void salvarMeta() {
        try { Files.writeString(META, String.valueOf(proximoCodigo.get() - 1)); }
        catch (Exception ignored) {}
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────
    public void adicionarTutor(Tutor t) { tutores.add(t); gravaDados(); }

    public int cadastrar(String nomeT, String endT, String telT, String dataT,
                         String nomeP, String tipoP, String dataP) throws Exception {
        if (nomeT == null || nomeT.trim().isEmpty()) throw new Exception("Nome do tutor vazio.");
        if (nomeP == null || nomeP.trim().isEmpty()) throw new Exception("Nome do pet vazio.");
        if (tipoP == null || tipoP.trim().isEmpty()) throw new Exception("Tipo do pet vazio.");
        LocalDateParts tp = parseDateStr(dataT, "tutor");
        int codigo = geraCodigo();
        Tutor t = new Tutor(codigo, nomeT, endT == null ? "" : endT,
                telT == null ? "" : telT, tp.d, tp.m, tp.a);
        if (dataP == null || dataP.trim().isEmpty()) { t.adicionarPet(new Pet(nomeP, tipoP)); }
        else { LocalDateParts pp = parseDateStr(dataP, "pet"); t.adicionarPet(new Pet(nomeP, tipoP, pp.d, pp.m, pp.a)); }
        tutores.add(t);
        gravaDados();
        return codigo;
    }

    /** Edita dados de um tutor existente de forma atomica — ou tudo funciona ou nada muda. */
    public String editarTutor(int codigo, String nome, String endereco, String telefone, String dataStr) {
        for (Tutor t : tutores) {
            if (t.getCodigo() == codigo) {
                // Guarda estado original para rollback
                String nomeOrig     = t.getNome();
                String enderecoOrig = t.getEndereco();
                String telefoneOrig = t.getTelefone();
                java.time.LocalDate dataOrig = t.getDataNasc();
                try {
                    // Valida data ANTES de modificar qualquer campo
                    LocalDateParts dp = null;
                    if (dataStr != null && !dataStr.trim().isEmpty()) {
                        dp = parseDateStr(dataStr, "tutor");
                        // Valida via DateUtil (lanca IllegalArgumentException se invalida)
                        DateUtil.validar(dp.d, dp.m, dp.a, "tutor");
                    }
                    // Aplica mudancas so apos validacao completa
                    if (nome     != null && !nome.trim().isEmpty()) t.setNome(nome.trim());
                    if (endereco != null)                           t.setEndereco(endereco.trim());
                    if (telefone != null)                           t.setTelefone(telefone.trim());
                    if (dp       != null)                           t.setDataNasc(dp.d, dp.m, dp.a);
                    gravaDados();
                    return "Tutor " + codigo + " atualizado com sucesso.";
                } catch (Exception e) {
                    // Rollback — restaura estado original
                    t.setNome(nomeOrig);
                    t.setEndereco(enderecoOrig);
                    t.setTelefone(telefoneOrig);
                    t.setDataNasc(dataOrig.getDayOfMonth(), dataOrig.getMonthValue(), dataOrig.getYear());
                    return "Erro ao editar: " + e.getMessage();
                }
            }
        }
        return "Tutor " + codigo + " nao encontrado.";
    }

    public String adicionarPetATutor(int codigoTutor, Pet pet) {
        for (Tutor t : tutores) {
            if (t.getCodigo() == codigoTutor) { t.adicionarPet(pet); gravaDados(); return "Pet \"" + pet.getNome() + "\" adicionado ao tutor " + codigoTutor + "."; }
        }
        return "Tutor " + codigoTutor + " nao encontrado.";
    }

    public String removerPetDeTutor(int codigoTutor, String nomePet) {
        for (Tutor t : tutores) {
            if (t.getCodigo() == codigoTutor) {
                if (t.removerPetPorNome(nomePet)) { gravaDados(); return "Pet \"" + nomePet + "\" removido do tutor " + codigoTutor + "."; }
                return "Pet \"" + nomePet + "\" nao encontrado no tutor " + codigoTutor + ".";
            }
        }
        return "Tutor " + codigoTutor + " nao encontrado.";
    }

    /** Retorna o Tutor pelo código, ou null se não encontrado. */
    public Tutor buscarTutor(int cod) {
        for (Tutor t : tutores) if (t.getCodigo() == cod) return t;
        return null;
    }

    public String imprimir() {
        if (tutores.isEmpty()) return "<cadastro vazio>\n";
        StringBuilder sb = new StringBuilder();
        for (Tutor t : tutores) sb.append(t.toString()).append("\n\n");
        return sb.toString();
    }

    public String buscar(int cod) {
        Tutor t = buscarTutor(cod);
        return t != null ? t.toStringBusca() : "Tutor " + cod + " nao encontrado.\n";
    }

    public String excluir(int cod) {
        boolean removido = tutores.removeIf(t -> t.getCodigo() == cod);
        if (removido) { gravaDados(); return "Tutor " + cod + " excluido com sucesso.\n"; }
        return "Tutor " + cod + " nao encontrado.\n";
    }

    // ── Persistência com backup automático ───────────────────────────────────
    // Faz backup no máximo uma vez por sessão (na primeira gravação), evitando
    // dezenas de .bak por sessão de uso normal.
    private boolean backupFeitoNessaSessao = false;

    public void gravaDados() {
        try {
            Files.createDirectories(DIR);
            // Backup único por sessão — usa timestamp com ms para evitar colisão
            if (Files.exists(ARQUIVO) && !backupFeitoNessaSessao) {
                String ts = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
                Path bak = DIR.resolve("tutores_" + ts + ".bak");
                Files.copy(ARQUIVO, bak, StandardCopyOption.REPLACE_EXISTING);
                backupFeitoNessaSessao = true;
                // Mantém só os 5 backups mais recentes
                try (var stream = Files.list(DIR)) {
                    stream.filter(p -> p.getFileName().toString().endsWith(".bak"))
                            .sorted(java.util.Comparator.reverseOrder())
                            .skip(5)
                            .forEach(p -> { try { Files.delete(p); } catch (Exception ignored) {} });
                }
            }
            // Grava meta ANTES do JSON principal — se o processo morrer entre os dois,
            // o meta fica com valor antigo (seguro), não com valor futuro
            salvarMeta();
            JsonUtil.salvar(tutores, ARQUIVO);
            System.out.println("Dados salvos (" + tutores.size() + " tutores) -> " + ARQUIVO);
        } catch (Exception ex) {
            System.err.println("Erro ao salvar dados: " + ex.getMessage());
        }
    }

    public void carregarDados() {
        if (!Files.exists(ARQUIVO)) {
            System.out.println("Arquivo de dados inexistente. Sera criado ao salvar.");
            sincronizarContador(); // lê meta.txt mesmo sem arquivo principal
            return;
        }
        try {
            ArrayList<Tutor> carregados = JsonUtil.carregar(ARQUIVO);
            tutores.clear();
            tutores.addAll(carregados);
            System.out.println("Dados carregados: " + tutores.size() + " tutor(es).");
        } catch (Exception ex) {
            System.err.println("Erro ao carregar dados: " + ex.getMessage());
            tentarRestaurarBackup(); // popula tutores se conseguir
        } finally {
            // Sempre sincroniza o contador — seja com a lista carregada, restaurada ou vazia
            sincronizarContador();
        }
    }

    // Mensagem de restauração para a GUI ler
    private String mensagemRestauracao = null;
    public String getMensagemRestauracao() {
        String m = mensagemRestauracao;
        mensagemRestauracao = null;
        return m;
    }

    private void tentarRestaurarBackup() {
        try (var stream = Files.list(DIR)) {
            java.util.Optional<Path> ultimo = stream
                    .filter(p -> p.getFileName().toString().endsWith(".bak"))
                    .max(java.util.Comparator.naturalOrder());
            if (ultimo.isPresent()) {
                ArrayList<Tutor> carregados = JsonUtil.carregar(ultimo.get());
                tutores.clear(); tutores.addAll(carregados); sincronizarContador();
                mensagemRestauracao =
                        "AVISO: o arquivo principal estava corrompido.\n" +
                                "Dados restaurados do backup: " + ultimo.get().getFileName() + "\n" +
                                "A ultima operacao da sessao anterior pode ter sido perdida.";
                System.out.println("Dados restaurados do backup: " + ultimo.get());
            } else {
                mensagemRestauracao =
                        "AVISO: o arquivo de dados esta corrompido e nao ha backup disponivel.\n" +
                                "O sistema iniciou vazio.";
            }
        } catch (Exception e) { System.err.println("Falha ao restaurar backup: " + e.getMessage()); }
    }

    public java.util.List<Tutor> getTutoresPublico() {
        return java.util.Collections.unmodifiableList(tutores);
    }

    public void salvarDados()          { gravaDados(); }
    public void carregarDadosPublico() { carregarDados(); }

    // ── Utilitário ────────────────────────────────────────────────────────────
    private static class LocalDateParts { int d, m, a; }

    private static LocalDateParts parseDateStr(String texto, String ctx) throws Exception {
        if (texto == null || texto.trim().isEmpty()) throw new Exception("Data do " + ctx + " nao pode ser vazia.");
        String[] p = texto.trim().split("/");
        if (p.length != 3) throw new Exception("Data do " + ctx + " em formato invalido. Use dd/mm/aaaa.");
        try {
            LocalDateParts r = new LocalDateParts();
            r.d = Integer.parseInt(p[0].trim()); r.m = Integer.parseInt(p[1].trim()); r.a = Integer.parseInt(p[2].trim());
            return r;
        } catch (NumberFormatException e) { throw new Exception("Data do " + ctx + " contem caracteres invalidos."); }
    }
}