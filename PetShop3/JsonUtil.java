package PetShop3;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Serialização/desserialização JSON manual (sem dependência externa).
 *
 * Formato gerado:
 * [
 *   {
 *     "codigo": 1,
 *     "nome": "João Silva",
 *     "endereco": "Rua A, 10",
 *     "dataNasc": "15/03/1985",
 *     "pets": [
 *       { "nome": "Rex", "tipo": "Cão", "dataNasc": "10/06/2020" }
 *     ]
 *   }
 * ]
 */
public class JsonUtil {

    private JsonUtil() {}

    // ════════════════════════════════════════════════════════════════════════
    //  ESCRITA
    // ════════════════════════════════════════════════════════════════════════

    public static void salvar(List<Tutor> tutores, Path arquivo) throws IOException {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < tutores.size(); i++) {
            sb.append(tutorParaJson(tutores.get(i), "  "));
            if (i < tutores.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        Files.writeString(arquivo, sb.toString(), StandardCharsets.UTF_8);
    }

    private static String tutorParaJson(Tutor t, String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("{\n");
        sb.append(indent).append("  \"codigo\": ").append(t.getCodigo()).append(",\n");
        sb.append(indent).append("  \"nome\": ").append(str(t.getNome())).append(",\n");
        sb.append(indent).append("  \"endereco\": ").append(str(t.getEndereco())).append(",\n");
        sb.append(indent).append("  \"telefone\": ").append(str(t.getTelefone())).append(",\n");
        sb.append(indent).append("  \"dataNasc\": ").append(str(DateUtil.formatar(t.getDataNasc()))).append(",\n");
        sb.append(indent).append("  \"pets\": [");

        List<Pet> pets = t.getPets();
        if (pets.isEmpty()) {
            sb.append("]");
        } else {
            sb.append("\n");
            for (int i = 0; i < pets.size(); i++) {
                sb.append(petParaJson(pets.get(i), indent + "    "));
                if (i < pets.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append(indent).append("  ]");
        }
        sb.append("\n").append(indent).append("}");
        return sb.toString();
    }

    private static String petParaJson(Pet p, String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("{\n");
        sb.append(indent).append("  \"nome\": ").append(str(p.getNome())).append(",\n");
        sb.append(indent).append("  \"tipo\": ").append(str(p.getTipo())).append(",\n");
        sb.append(indent).append("  \"dataNasc\": ").append(str(DateUtil.formatar(p.getDataNasc()))).append("\n");
        sb.append(indent).append("}");
        return sb.toString();
    }

    private static String str(String s) {
        if (s == null) return "null";
        // escapa aspas e barras invertidas
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LEITURA  (parser manual simples — suficiente para o formato gerado)
    // ════════════════════════════════════════════════════════════════════════

    public static ArrayList<Tutor> carregar(Path arquivo) throws IOException {
        String json = Files.readString(arquivo, StandardCharsets.UTF_8).trim();
        ArrayList<Tutor> lista = new ArrayList<>();

        // Remove colchetes externos
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]"))  json = json.substring(0, json.length() - 1);
        json = json.trim();

        if (json.isEmpty()) return lista;

        // Divide em blocos de tutor (cada { ... })
        List<String> blocos = dividirBlocos(json);
        for (String bloco : blocos) {
            Tutor t = parseTutor(bloco.trim());
            if (t != null) lista.add(t);
        }
        return lista;
    }

    /**
     * Divide o conteúdo do array em blocos de objeto de nível 1.
     * Respeita aninhamento de chaves.
     */
    private static List<String> dividirBlocos(String json) {
        List<String> blocos = new ArrayList<>();
        int nivel = 0;
        int inicio = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (nivel == 0) inicio = i;
                nivel++;
            } else if (c == '}') {
                nivel--;
                if (nivel == 0 && inicio >= 0) {
                    blocos.add(json.substring(inicio, i + 1));
                    inicio = -1;
                }
            }
        }
        return blocos;
    }

    private static Tutor parseTutor(String bloco) {
        try {
            int codigo      = Integer.parseInt(campo(bloco, "codigo"));
            String nome     = campo(bloco, "nome");
            String end      = campo(bloco, "endereco");
            String telefone = campo(bloco, "telefone");
            String dataNasc = campo(bloco, "dataNasc");

            LocalDate dt = DateUtil.parse(dataNasc, "tutor (leitura)");

            Tutor t = new Tutor(codigo, nome, end, telefone,
                    dt.getDayOfMonth(), dt.getMonthValue(), dt.getYear());

            // Extrai bloco de pets
            int pInicio = bloco.indexOf("\"pets\"");
            if (pInicio >= 0) {
                int ab = bloco.indexOf('[', pInicio);
                int fe = bloco.lastIndexOf(']');
                if (ab >= 0 && fe > ab) {
                    String petsJson = bloco.substring(ab + 1, fe).trim();
                    if (!petsJson.isEmpty()) {
                        List<String> petBlocos = dividirBlocos(petsJson);
                        for (String pb : petBlocos) {
                            Pet p = parsePet(pb.trim());
                            if (p != null) t.adicionarPet(p);
                        }
                    }
                }
            }
            return t;
        } catch (Exception e) {
            System.err.println("Aviso: erro ao ler tutor do JSON — " + e.getMessage());
            return null;
        }
    }

    private static Pet parsePet(String bloco) {
        try {
            String nome    = campo(bloco, "nome");
            String tipo    = campo(bloco, "tipo");
            String dataNasc = campo(bloco, "dataNasc");

            if (dataNasc == null || dataNasc.startsWith("--")) {
                return new Pet(nome, tipo);
            }
            LocalDate dt = DateUtil.parse(dataNasc, "pet (leitura)");
            return new Pet(nome, tipo, dt.getDayOfMonth(), dt.getMonthValue(), dt.getYear());
        } catch (Exception e) {
            System.err.println("Aviso: erro ao ler pet do JSON — " + e.getMessage());
            return null;
        }
    }

    /**
     * Extrai o valor de um campo JSON simples: "chave": "valor" ou "chave": número
     */
    private static String campo(String bloco, String chave) {
        String busca = "\"" + chave + "\"";
        int idx = bloco.indexOf(busca);
        if (idx < 0) return "";

        int colon = bloco.indexOf(':', idx + busca.length());
        if (colon < 0) return "";

        // Pula espaços após ':'
        int start = colon + 1;
        while (start < bloco.length() && Character.isWhitespace(bloco.charAt(start))) start++;

        if (bloco.charAt(start) == '"') {
            // Valor string
            int end = bloco.indexOf('"', start + 1);
            while (end > 0 && bloco.charAt(end - 1) == '\\') {
                end = bloco.indexOf('"', end + 1);
            }
            return bloco.substring(start + 1, end)
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        } else {
            // Valor numérico ou null
            int end = start;
            while (end < bloco.length() && !",\n}".contains(String.valueOf(bloco.charAt(end)))) end++;
            return bloco.substring(start, end).trim();
        }
    }
}