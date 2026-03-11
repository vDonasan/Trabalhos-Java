============================================================
  SISTEMA PETSHOP — Instruções de Uso (versão refatorada)
============================================================

COMO COMPILAR
─────────────
  javac -d out PJBL2/*.java
  jar cfe PetShop.jar PJBL2.PetShopGUI -C out .

COMO EXECUTAR (GUI)
───────────────────
  java -jar PetShop.jar
  ou:
  java -cp out PJBL2.PetShopGUI

============================================================
  ABA 1 — CADASTRO  (fluxo natural)
============================================================

  1. Preencha os dados do TUTOR (nome obrigatório, data obrigatória)
  2. Preencha os dados de um PET e clique em "➕ Adicionar Pet à fila"
     → Repita quantas vezes precisar (contador mostra quantos pets estão na fila)
  3. Clique em "✔ Cadastrar Tutor"
     → Todos os pets da fila serão vinculados ao tutor cadastrado
     → O formulário é limpo automaticamente

  ⚠ Pelo menos UM pet deve ser adicionado à fila antes de cadastrar o tutor.

============================================================
  ABA 2 — BUSCA / EXCLUSÃO
============================================================

  • Digite o código do tutor e clique em "🔍 Buscar" ou "🗑 Excluir"
  • Para ver todos os tutores cadastrados, clique em "📄 Imprimir todos"
  • A exclusão pede confirmação antes de remover

============================================================
  ABA 3 — GERENCIAR PETS  (funcionalidade nova)
============================================================

  • Adicionar pet a tutor existente: informe o código do tutor
    e os dados do novo pet, depois clique em "➕ Adicionar Pet"

  • Remover pet de tutor existente: informe o código do tutor
    e o nome exato do pet, depois clique em "➖ Remover Pet"

============================================================
  DADOS
============================================================

  Os dados são salvos em formato JSON em:
    Windows : C:\Users\<seu_usuario>\.petshop\tutores.json
    Linux   : /home/<seu_usuario>/.petshop/tutores.json
    macOS   : /Users/<seu_usuario>/.petshop/tutores.json

  • O arquivo é criado automaticamente na primeira gravação.
  • Por ser JSON (texto puro), pode ser aberto e editado
    em qualquer editor de texto se necessário.
  • Ao contrário da versão anterior (serialização binária),
    o arquivo NÃO quebra se os campos das classes mudarem.

============================================================
  FORMATOS DE DATA ACEITOS
============================================================

  dd/mm/aaaa  →  01/03/2020   (formato preferencial)
  d/m/aa      →  1/3/20       (ano de 2 dígitos é expandido automaticamente)

  ⚠ Datas inválidas (ex: 30/02/2000) exibem mensagem de erro clara.
  ⚠ Datas no futuro não são aceitas.

============================================================
