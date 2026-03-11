package PetShop3;

public class PetShopController {

    private final PetShop model = new PetShop();

    public PetShopController() {}

    public void carregarDados()  { model.carregarDadosPublico(); }
    public void salvarDados()    { model.salvarDados(); }
    public int  gerarCodigo()    { return model.geraCodigo(); }

    public int cadastrarTutor(Tutor t) { model.adicionarTutor(t); return t.getCodigo(); }

    public int cadastrarTutorComPet(String nomeT, String endT, String telT, String dataT,
                                    String nomeP, String tipoP, String dataP) throws Exception {
        return model.cadastrar(nomeT, endT, telT, dataT, nomeP, tipoP, dataP);
    }

    /** Edita dados do tutor (nome, endereco, telefone, data). Campos null = sem alteracao. */
    public String editarTutor(int codigo, String nome, String endereco, String telefone, String data) {
        return model.editarTutor(codigo, nome, endereco, telefone, data);
    }

    /** Retorna o objeto Tutor pelo codigo, ou null. */
    public Tutor buscarTutorPorCodigo(int codigo) {
        return model.buscarTutor(codigo);
    }

    public String adicionarPetATutor(int codigoTutor, Pet pet) {
        return model.adicionarPetATutor(codigoTutor, pet);
    }

    public String removerPetDeTutor(int codigoTutor, String nomePet) {
        return model.removerPetDeTutor(codigoTutor, nomePet);
    }

    public String getMensagemRestauracao() { return model.getMensagemRestauracao(); }

    /** Retorna copia da lista de tutores para a GUI popular a tabela diretamente. */
    public java.util.List<Tutor> getTutores() { return model.getTutoresPublico(); }

    public String imprimirCadastro()  { return model.imprimir(); }
    public String buscarPorCodigo(int cod) { return model.buscar(cod); }
    public String excluirPorCodigo(int cod) { return model.excluir(cod); }
}