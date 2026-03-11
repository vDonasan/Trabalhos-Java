package PetShop3;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class PetShopGUI extends JFrame {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final Color TERRA      = new Color(0xC0, 0x6C, 0x3A);
    private static final Color TERRA_DARK = new Color(0x8B, 0x48, 0x20);
    private static final Color TERRA_LITE = new Color(0xF2, 0xD6, 0xBB);
    private static final Color CREME      = new Color(0xFD, 0xF6, 0xEE);
    private static final Color VERDE      = new Color(0x4A, 0x7C, 0x59);
    private static final Color CINZA      = new Color(0x5C, 0x5C, 0x5C);
    private static final Color CINZA_LITE = new Color(0xE8, 0xE0, 0xD8);
    private static final Color BRANCO     = Color.WHITE;
    private static final Color VERMELHO   = new Color(0xC0, 0x39, 0x2B);

    // ── Fontes ────────────────────────────────────────────────────────────────
    private static final Font FNT_TITULO = new Font("Georgia", Font.BOLD, 22);
    private static final Font FNT_LABEL  = new Font("Georgia", Font.BOLD, 13);
    private static final Font FNT_CAMPO  = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FNT_BTN    = new Font("Georgia", Font.BOLD, 13);
    private static final Font FNT_SMALL  = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font FNT_LOG    = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private static final Font FNT_NAV    = new Font("Georgia", Font.BOLD, 13);
    private static final Font FNT_HEADER = new Font("Georgia", Font.BOLD, 18);

    // ── Tipos de pet ──────────────────────────────────────────────────────────
    private static final String[][] TIPOS = {
            {"Cao","cao"}, {"Gato","gato"}, {"Passaro","passaro"}, {"Peixe","peixe"},
            {"Coelho","coelho"}, {"Hamster","hamster"}, {"Tartaruga","tartaruga"}, {"Reptil","reptil"},
    };

    // ── Estado ────────────────────────────────────────────────────────────────
    private final PetShopController controller;
    private final ArrayList<Pet> petsEmCadastro = new ArrayList<>();

    private JPanel     painelConteudo;
    private CardLayout cartoes;
    private JTextArea  areaLog;

    // Cadastro
    private JTextField campoNomeTutor, campoEndereco, campoTelefone, campoDataNascTutor;
    private JTextField campoNomePet, campoDataNascPet;
    private JLabel     labelContadorPets;
    private DefaultListModel<String> modeloPetsTemp;
    private JPanel     painelTiposCad;
    private String     tipoSelecionadoCad = "";
    private JTextField campoTipoCustomCad;

    // Listagem
    private JTextField campoBuscaGlobal, campoBuscaCodigo;
    private JComboBox<String> comboFiltroTipo;
    private DefaultTableModel modeloTabela;
    private JTable tabelaResultados;

    // Gerenciar - adicionar
    private JTextField campoCodigoTutorPet, campoNomePetExist, campoDataPetExist;
    private JPanel     painelTiposExist;
    private String     tipoSelecionadoExist = "";
    private JTextField campoTipoCustomExist;

    // Gerenciar - remover
    private JTextField campoCodigoRemover, campoNomePetRemover;

    // Editar tutor
    private JTextField campoEditCodigo, campoEditNome, campoEditEndereco, campoEditTelefone, campoEditData;

    private final Map<String, JButton> botoesNav = new LinkedHashMap<>();

    // ══════════════════════════════════════════════════════════════════════════
    //  ÍCONES VETORIAIS
    // ══════════════════════════════════════════════════════════════════════════
    private static ImageIcon icone(String tipo, int sz, Color cor) {
        BufferedImage img = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(cor);
        switch (tipo.toLowerCase()) {
            case "patas": {
                g.fillOval(sz/2-7, sz/2, 14, 12);
                g.fillOval(sz/2-11, sz/2-6, 9, 8); g.fillOval(sz/2+2, sz/2-6, 9, 8);
                g.fillOval(sz/2-16, sz/2+2, 7, 7); g.fillOval(sz/2+9, sz/2+2, 7, 7);
                g.fillOval(sz-18, 4, 8, 7); g.fillOval(sz-13, 2, 5, 5); g.fillOval(sz-21, 2, 5, 5);
                break;
            }
            case "cao": case "cão": case "cachorro": case "dog": {
                Stroke s = g.getStroke(); g.setStroke(new BasicStroke(sz/12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawOval(sz/4, sz/6, sz/2, sz/2);
                g.fillOval(sz/2-sz/12, sz/2-sz/20, sz/6, sz/8);
                g.fillOval(sz/2-sz/7, sz/3, sz/10, sz/10);
                g.drawArc(sz/4-sz/8, sz/6, sz/5, sz/4, 90, 160);
                g.drawArc(sz/2+sz/14, sz/6, sz/5, sz/4, -10, 160);
                g.drawRoundRect(sz/4, sz*5/9, sz/2, sz*3/8, sz/5, sz/5);
                Path2D r1 = new Path2D.Float(); r1.moveTo(sz*3/4,sz*5/8); r1.curveTo(sz-4,sz/2,sz-2,sz/4,sz*7/8,sz/6); g.draw(r1);
                g.drawLine(sz/3,sz*7/8,sz/3,sz-3); g.drawLine(sz*2/3,sz*7/8,sz*2/3,sz-3);
                g.setStroke(s); break;
            }
            case "gato": case "cat": {
                Stroke s = g.getStroke(); g.setStroke(new BasicStroke(sz/12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawOval(sz/4, sz/5, sz/2, sz*2/5);
                g.drawPolygon(new int[]{sz/4,sz/4+sz/8,sz/4-sz/10}, new int[]{sz/5,sz/5-sz/6,sz/5-sz/8}, 3);
                g.drawPolygon(new int[]{sz*3/4,sz*3/4-sz/8,sz*3/4+sz/10}, new int[]{sz/5,sz/5-sz/6,sz/5-sz/8}, 3);
                g.drawOval(sz/3,sz/3,sz/7,sz/9); g.drawOval(sz/2+sz/14,sz/3,sz/7,sz/9);
                g.fillPolygon(new int[]{sz/2,sz/2-sz/16,sz/2+sz/16}, new int[]{sz/2,sz/2+sz/10,sz/2+sz/10}, 3);
                g.drawLine(sz/2-sz/16,sz/2+sz/14,sz/8,sz/2); g.drawLine(sz/2-sz/16,sz/2+sz/14,sz/8,sz/2+sz/8);
                g.drawLine(sz/2+sz/16,sz/2+sz/14,sz*7/8,sz/2); g.drawLine(sz/2+sz/16,sz/2+sz/14,sz*7/8,sz/2+sz/8);
                g.drawRoundRect(sz/4+2,sz*3/5,sz/2-4,sz/3,sz/6,sz/6);
                Path2D r2=new Path2D.Float(); r2.moveTo(sz*3/4-2,sz*7/8); r2.curveTo(sz-2,sz*3/4,sz-2,sz/2,sz*3/4,sz/3); g.draw(r2);
                g.setStroke(s); break;
            }
            case "passaro": case "pássaro": case "bird": case "ave": {
                Stroke s = g.getStroke(); g.setStroke(new BasicStroke(sz/12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawOval(sz/4,sz/3,sz/2,sz/3); g.drawOval(sz/2,sz/5,sz/4,sz/4);
                g.fillPolygon(new int[]{sz*3/4,sz-2,sz*3/4}, new int[]{sz/4,sz/3,sz*5/12}, 3);
                g.fillOval(sz*3/4-sz/14,sz/4,sz/10,sz/10);
                Path2D a=new Path2D.Float(); a.moveTo(sz/4+4,sz/2); a.curveTo(sz/4-sz/6,sz/3,sz/4,sz/5,sz/2,sz/3); g.draw(a);
                g.drawLine(sz/2-sz/10,sz*2/3,sz/2-sz/8,sz*5/6); g.drawLine(sz/2-sz/8,sz*5/6,sz/2-sz/4,sz*7/8); g.drawLine(sz/2-sz/8,sz*5/6,sz/2,sz*7/8);
                g.setStroke(s); break;
            }
            case "peixe": case "fish": {
                Stroke s = g.getStroke(); g.setStroke(new BasicStroke(sz/12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawOval(sz/6,sz/3,sz*7/12,sz/3);
                Path2D c=new Path2D.Float(); c.moveTo(sz*3/4,sz/2); c.lineTo(sz-3,sz/5); c.lineTo(sz-3,sz*4/5); c.closePath(); g.draw(c);
                g.fillOval(sz/4,sz*5/12,sz/8,sz/8);
                g.drawArc(sz/3,sz/3,sz/5,sz/4,30,120); g.drawArc(sz/2-sz/10,sz/3,sz/5,sz/4,30,120);
                Path2D b=new Path2D.Float(); b.moveTo(sz/3,sz/3); b.curveTo(sz/2,sz/8,sz*2/3,sz/8,sz*2/3,sz/3); g.draw(b);
                g.setStroke(s); break;
            }
            case "coelho": case "rabbit": {
                Stroke s = g.getStroke(); g.setStroke(new BasicStroke(sz/12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawOval(sz/4,2,sz/6,sz/3); g.drawOval(sz/2,2,sz/6,sz/3);
                g.drawOval(sz/4-2,sz/4,sz/2+4,sz/3);
                g.fillOval(sz/3,sz/3,sz/9,sz/9); g.fillOval(sz/2+sz/16,sz/3,sz/9,sz/9);
                g.fillOval(sz/2-sz/14,sz/2,sz/7,sz/10);
                g.drawOval(sz/5,sz/2+sz/10,sz*3/5,sz*2/5);
                g.setStroke(s); break;
            }
            case "hamster": case "roedor": {
                Stroke s = g.getStroke(); g.setStroke(new BasicStroke(sz/12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawOval(sz/5,sz/3,sz*3/5,sz/2); g.drawOval(sz/4,sz/8,sz/2,sz/3);
                g.drawOval(sz/4,sz/12,sz/6,sz/6); g.drawOval(sz*7/12,sz/12,sz/6,sz/6);
                g.fillOval(sz/3,sz/4,sz/9,sz/9); g.fillOval(sz/2+sz/16,sz/4,sz/9,sz/9);
                g.drawArc(sz/5,sz/3,sz/5,sz/5,200,140); g.drawArc(sz*3/5,sz/3,sz/5,sz/5,200,140);
                g.setStroke(s); break;
            }
            case "tartaruga": case "turtle": {
                Stroke s = g.getStroke(); g.setStroke(new BasicStroke(sz/12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawOval(sz/5,sz/4,sz*3/5,sz/2); g.drawOval(sz*3/8,sz/3,sz/4,sz/5);
                g.drawLine(sz/2,sz/4,sz/2,sz/3); g.drawLine(sz/5,sz/2,sz*3/8,sz/2); g.drawLine(sz*4/5,sz/2,sz*5/8,sz/2);
                g.drawOval(sz*3/5,sz/4-sz/10,sz/6,sz/7);
                g.drawOval(sz/8,sz/3,sz/7,sz/6); g.drawOval(sz/8,sz*3/5,sz/7,sz/6);
                g.drawOval(sz*3/4,sz/3,sz/7,sz/6); g.drawOval(sz*3/4,sz*3/5,sz/7,sz/6);
                g.setStroke(s); break;
            }
            case "reptil": case "réptil": case "cobra": case "lagarto": {
                Stroke s = g.getStroke(); g.setStroke(new BasicStroke(sz/10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Path2D corpo=new Path2D.Float(); corpo.moveTo(sz/8,sz*2/3); corpo.curveTo(sz/4,sz/4,sz*3/4,sz*3/4,sz*7/8,sz/3); g.draw(corpo);
                g.setStroke(new BasicStroke(sz/14f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawOval(sz*3/4-2,sz/5,sz/5,sz/6);
                g.setStroke(new BasicStroke(sz/18f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(sz-3,sz/3,sz-3+sz/10,sz/4); g.drawLine(sz-3,sz/3,sz-3+sz/10,sz/3+sz/10);
                g.fillOval(sz*3/4+sz/14,sz/4,sz/10,sz/10);
                g.setStroke(s); break;
            }
            default: {
                g.fillOval(sz/2-sz/7,sz/2-sz/8,sz*2/7,sz/4);
                g.fillOval(sz/2-sz/5,sz/4,sz/5,sz/5); g.fillOval(sz/2,sz/4,sz/5,sz/5);
                g.fillOval(sz/2-sz/4,sz/3,sz/5,sz/6); g.fillOval(sz/2+sz/16,sz/3,sz/5,sz/6);
            }
        }
        g.dispose();
        return new ImageIcon(img);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Construtor
    // ══════════════════════════════════════════════════════════════════════════
    public PetShopGUI() {
        controller = new PetShopController();
        configurarJanela();
        construirLayout();
        controller.carregarDados();
        atualizarTabela();
        log("Sistema iniciado. Bem-vindo ao PetShop!");
        // Avisa o usuario se houve restauracao de backup
        String avisoBackup = controller.getMensagemRestauracao();
        if (avisoBackup != null) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, avisoBackup, "Restauracao de Dados", JOptionPane.WARNING_MESSAGE));
        }
    }

    private void configurarJanela() {
        setTitle("Sistema PetShop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        getContentPane().setBackground(CREME);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { controller.salvarDados(); }
        });
    }

    private void construirLayout() {
        setLayout(new BorderLayout());
        add(criarSidebar(), BorderLayout.WEST);
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(CREME);
        centro.add(criarHeader(), BorderLayout.NORTH);
        cartoes = new CardLayout();
        painelConteudo = new JPanel(cartoes);
        painelConteudo.setBackground(CREME);
        painelConteudo.add(criarAbaCadastro(),  "cadastro");
        painelConteudo.add(criarAbaListagem(),  "listagem");
        painelConteudo.add(criarAbaGerenciar(), "gerenciar");
        painelConteudo.add(criarAbaEditar(),    "editar");
        painelConteudo.add(criarAbaLog(),       "log");
        centro.add(painelConteudo, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
        navegarPara("cadastro");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Sidebar
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel criarSidebar() {
        JPanel sb = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0,0,TERRA_DARK,0,getHeight(),new Color(0x5C,0x30,0x10)));
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setPreferredSize(new Dimension(220, 0));

        JLabel ico = new JLabel(icone("patas", 64, TERRA_LITE));
        ico.setAlignmentX(Component.LEFT_ALIGNMENT);
        ico.setBorder(new EmptyBorder(24,20,4,0));
        JLabel nome = new JLabel("PetShop");
        nome.setFont(new Font("Georgia", Font.BOLD, 22));
        nome.setForeground(TERRA_LITE);
        nome.setAlignmentX(Component.LEFT_ALIGNMENT);
        nome.setBorder(new EmptyBorder(0,20,2,0));
        JLabel sub = new JLabel("Sistema de Gestao");
        sub.setFont(new Font("Georgia", Font.ITALIC, 11));
        sub.setForeground(new Color(255,255,255,130));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setBorder(new EmptyBorder(0,20,18,0));
        sb.add(ico); sb.add(nome); sb.add(sub);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255,255,255,40));
        sep.setMaximumSize(new Dimension(220,1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sb.add(sep); sb.add(Box.createVerticalStrut(10));

        adicionarItemNav(sb, "cadastro",  "Cadastrar",       icone("patas",   20, TERRA_LITE));
        adicionarItemNav(sb, "listagem",  "Buscar / Listar", icone("peixe",   20, TERRA_LITE));
        adicionarItemNav(sb, "gerenciar", "Gerenciar Pets",  icone("gato",    20, TERRA_LITE));
        adicionarItemNav(sb, "editar",    "Editar Tutor",    icone("patas",   20, TERRA_LITE));
        adicionarItemNav(sb, "log",       "Historico",       icone("passaro", 20, TERRA_LITE));

        sb.add(Box.createVerticalGlue());
        JLabel rodape = new JLabel("v3.0 - 2025");
        rodape.setFont(FNT_SMALL);
        rodape.setForeground(new Color(255,255,255,70));
        rodape.setAlignmentX(Component.LEFT_ALIGNMENT);
        rodape.setBorder(new EmptyBorder(0,20,14,0));
        sb.add(rodape);
        return sb;
    }

    private void adicionarItemNav(JPanel sb, String card, String texto, ImageIcon ico) {
        JButton btn = new JButton(texto, ico) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (Boolean.TRUE.equals(getClientProperty("ativo"))) {
                    g2.setColor(new Color(255,255,255,45)); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                    g2.setColor(TERRA_LITE); g2.fillRoundRect(0,8,4,getHeight()-16,4,4);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255,255,255,22)); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                }
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(FNT_NAV); btn.setForeground(new Color(255,255,255,210));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        btn.setIconTextGap(10);
        btn.setBorder(new EmptyBorder(10,14,10,10));
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setMinimumSize(new Dimension(220,46)); btn.setMaximumSize(new Dimension(220,46)); btn.setPreferredSize(new Dimension(220,46));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> navegarPara(card));
        botoesNav.put(card, btn); sb.add(btn); sb.add(Box.createVerticalStrut(4));
    }

    private void navegarPara(String card) {
        cartoes.show(painelConteudo, card);
        botoesNav.forEach((k,v) -> { v.putClientProperty("ativo", k.equals(card)); v.repaint(); });
        if ("listagem".equals(card)) atualizarTabela();
        // Ao entrar no cadastro com fila vazia, reseta seleção de tipo
        if ("cadastro".equals(card) && petsEmCadastro.isEmpty()) {
            tipoSelecionadoCad = "";
            if (painelTiposCad != null) painelTiposCad.repaint();
        }
        if ("gerenciar".equals(card)) {
            tipoSelecionadoExist = "";
            if (painelTiposExist != null) painelTiposExist.repaint();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Header
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel criarHeader() {
        JPanel h = new JPanel(new BorderLayout(16,0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0,0,BRANCO,getWidth(),0,CREME));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(CINZA_LITE); g2.fillRect(0,getHeight()-1,getWidth(),1);
            }
        };
        h.setBorder(new EmptyBorder(10,22,10,22));
        JPanel pt = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        pt.setOpaque(false);
        pt.add(new JLabel(icone("patas",32,TERRA)));
        JLabel titulo = new JLabel("Sistema de Gestao PetShop");
        titulo.setFont(FNT_TITULO); titulo.setForeground(TERRA_DARK);
        pt.add(titulo);
        h.add(pt, BorderLayout.WEST);

        campoBuscaGlobal = campoEstilizado("Buscar tutor ou pet...", 24);
        campoBuscaGlobal.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filtrarTabela(); }
            public void removeUpdate(DocumentEvent e)  { filtrarTabela(); }
            public void changedUpdate(DocumentEvent e) {}
        });
        // Só navega se não for por acidente — removido auto-navigate ao focar
        JPanel bp = new JPanel(new BorderLayout()); bp.setOpaque(false);
        bp.add(campoBuscaGlobal, BorderLayout.CENTER);
        h.add(bp, BorderLayout.CENTER);
        return h;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Aba Cadastro
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel criarAbaCadastro() {
        JPanel p = new JPanel(new BorderLayout(16,16));
        p.setBackground(CREME); p.setBorder(new EmptyBorder(20,24,16,24));
        p.add(tituloSecao("Cadastrar Novo Tutor", icone("patas",24,TERRA_DARK)), BorderLayout.NORTH);
        JPanel corpo = new JPanel(new GridLayout(1,3,16,0));
        corpo.setBackground(CREME);
        corpo.add(cartaoDadosTutor()); corpo.add(cartaoDadosPet()); corpo.add(cartaoPetsNaFila());
        p.add(corpo, BorderLayout.CENTER);
        JPanel rod = new JPanel(new FlowLayout(FlowLayout.CENTER,14,8));
        rod.setBackground(CREME);
        JButton btnAdd  = botaoPrimario("+ Adicionar Pet a fila",   VERDE);
        JButton btnCad  = botaoPrimario("Cadastrar Tutor com Pets", TERRA);
        JButton btnLimp = botaoSecundario("Limpar tudo");
        btnAdd.addActionListener(e  -> adicionarPet());
        btnCad.addActionListener(e  -> executarCadastro());
        btnLimp.addActionListener(e -> limparCadastro());
        rod.add(btnAdd); rod.add(btnCad); rod.add(btnLimp);
        p.add(rod, BorderLayout.SOUTH);
        return p;
    }

    private JPanel cartaoDadosTutor() {
        JPanel c = cartao("Dados do Tutor", icone("patas",18,TERRA));
        campoNomeTutor     = campoEstilizado("Ex: Maria Silva", 0);
        campoEndereco      = campoEstilizado("Ex: Rua das Flores, 100", 0);
        campoTelefone      = campoEstilizado("Ex: (41) 99999-0000", 0);
        campoDataNascTutor = campoData();
        c.add(rotulo("Nome *"));               c.add(campoNomeTutor);
        c.add(rotulo("Endereco"));             c.add(campoEndereco);
        c.add(rotulo("Telefone"));             c.add(campoTelefone);
        c.add(rotulo("Data de Nascimento *")); c.add(campoDataNascTutor);
        c.add(Box.createVerticalGlue());
        return c;
    }

    private JPanel cartaoDadosPet() {
        JPanel c = cartao("Dados do Pet", icone("cao",18,TERRA));
        campoNomePet     = campoEstilizado("Ex: Bolinha", 0);
        campoDataNascPet = campoData();
        c.add(rotulo("Nome do Pet *")); c.add(campoNomePet);
        c.add(rotulo("Tipo *"));
        painelTiposCad = criarSeletorTipo("cad");
        c.add(painelTiposCad);
        c.add(rotulo("Tipo personalizado (se nao listado):"));
        campoTipoCustomCad = campoEstilizado("Ex: Iguana, Porquinho...", 0);
        // Ao digitar no custom, deseleciona botões
        campoTipoCustomCad.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { if (!campoTipoCustomCad.getText().isEmpty()) { tipoSelecionadoCad=""; painelTiposCad.repaint(); } }
            public void removeUpdate(DocumentEvent e)  {}
            public void changedUpdate(DocumentEvent e) {}
        });
        c.add(campoTipoCustomCad);
        c.add(rotulo("Data Nascimento (opcional)")); c.add(campoDataNascPet);
        JLabel dica = new JLabel("<html><i>Adicione quantos pets quiser antes de cadastrar o tutor.</i></html>");
        dica.setFont(FNT_SMALL); dica.setForeground(new Color(0x88,0x77,0x66));
        dica.setBorder(new EmptyBorder(8,2,0,0)); dica.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.add(dica); c.add(Box.createVerticalGlue());
        return c;
    }

    private JPanel cartaoPetsNaFila() {
        JPanel c = cartao("Pets na Fila", icone("patas",18,TERRA));
        labelContadorPets = new JLabel("Nenhum pet adicionado ainda");
        labelContadorPets.setFont(FNT_LABEL); labelContadorPets.setForeground(CINZA);
        labelContadorPets.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.add(labelContadorPets); c.add(Box.createVerticalStrut(8));
        modeloPetsTemp = new DefaultListModel<>();
        JList<String> lista = new JList<>(modeloPetsTemp);
        lista.setFont(FNT_CAMPO); lista.setCellRenderer(new PetListCellRenderer());
        JScrollPane sc = new JScrollPane(lista);
        sc.setBorder(BorderFactory.createLineBorder(TERRA_LITE,1,true));
        sc.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.add(sc);
        return c;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Seletor de tipo com ícones
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel criarSeletorTipo(String contexto) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,4,4));
        p.setOpaque(false); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        for (String[] t : TIPOS) {
            String label = t[0]; String chave = t[1];
            JButton btn = new JButton(label, icone(chave,28,TERRA)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    String sel = contexto.equals("cad") ? tipoSelecionadoCad : tipoSelecionadoExist;
                    if (label.equals(sel)) {
                        g2.setColor(TERRA_LITE); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                        g2.setColor(TERRA); g2.setStroke(new BasicStroke(2f)); g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,8,8);
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(0xF2,0xD6,0xBB,180)); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    } else {
                        g2.setColor(new Color(0xF8,0xF3,0xED)); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                        g2.setColor(CINZA_LITE); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                    }
                    g2.dispose(); super.paintComponent(g);
                }
            };
            btn.setFont(new Font("SansSerif",Font.PLAIN,10)); btn.setForeground(CINZA);
            btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM); btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.setPreferredSize(new Dimension(58,52));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                if (contexto.equals("cad")) {
                    tipoSelecionadoCad = label;
                    if (campoTipoCustomCad != null) campoTipoCustomCad.setText(""); // limpa campo custom
                } else {
                    tipoSelecionadoExist = label;
                    if (campoTipoCustomExist != null) campoTipoCustomExist.setText("");
                }
                p.repaint();
            });
            p.add(btn);
        }
        return p;
    }

    private String resolverTipo(String contexto) {
        if (contexto.equals("cad")) {
            String custom = campoTipoCustomCad != null ? campoTipoCustomCad.getText().trim() : "";
            return !custom.isEmpty() ? custom : tipoSelecionadoCad;
        } else {
            String custom = campoTipoCustomExist != null ? campoTipoCustomExist.getText().trim() : "";
            return !custom.isEmpty() ? custom : tipoSelecionadoExist;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Aba Listagem com filtro de tipo
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel criarAbaListagem() {
        JPanel p = new JPanel(new BorderLayout(0,12));
        p.setBackground(CREME); p.setBorder(new EmptyBorder(20,24,16,24));
        p.add(tituloSecao("Buscar e Gerenciar Tutores", icone("peixe",24,TERRA_DARK)), BorderLayout.NORTH);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT,10,4));
        barra.setBackground(CREME);
        barra.add(rotulo("Codigo:"));
        campoBuscaCodigo = campoEstilizado("Buscar por codigo...", 10);
        campoBuscaCodigo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filtrarTabela(); }
            public void removeUpdate(DocumentEvent e)  { filtrarTabela(); }
            public void changedUpdate(DocumentEvent e) {}
        });
        barra.add(campoBuscaCodigo);

        // Filtro por tipo
        barra.add(rotulo("  Tipo:"));
        String[] tiposFiltro = new String[TIPOS.length + 1];
        tiposFiltro[0] = "Todos";
        for (int i = 0; i < TIPOS.length; i++) tiposFiltro[i+1] = TIPOS[i][0];
        comboFiltroTipo = new JComboBox<>(tiposFiltro);
        comboFiltroTipo.setFont(FNT_CAMPO); comboFiltroTipo.setPreferredSize(new Dimension(110, 32));
        comboFiltroTipo.addActionListener(e -> filtrarTabela());
        barra.add(comboFiltroTipo);

        JButton btnBuscar  = botaoPrimario("Detalhes", TERRA);
        JButton btnEditar  = botaoPrimario("Editar",   VERDE);
        JButton btnExcluir = botaoPrimario("Excluir",  VERMELHO);
        JButton btnRefresh = botaoSecundario("Atualizar");
        btnBuscar.addActionListener(e  -> buscarPorCodigo());
        btnEditar.addActionListener(e  -> abrirEdicaoDaTabela());
        btnExcluir.addActionListener(e -> excluirPorCodigo());
        btnRefresh.addActionListener(e -> { atualizarTabela(); log("Lista atualizada."); });
        barra.add(btnBuscar); barra.add(btnEditar); barra.add(btnExcluir);
        barra.add(Box.createHorizontalStrut(8)); barra.add(btnRefresh);

        String[] cols = {"Cod.", "Tutor", "Telefone", "Endereco", "Nasc. Tutor", "Idade", "Pet", "Tipo", "Nasc. Pet"};
        modeloTabela = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaResultados = new JTable(modeloTabela);
        estilizarTabela(tabelaResultados);
        // Ordenacao por clique no cabecalho
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabela);
        tabelaResultados.setRowSorter(sorter);
        JScrollPane scrollT = new JScrollPane(tabelaResultados);
        scrollT.setBorder(BorderFactory.createLineBorder(CINZA_LITE,1,true));
        scrollT.getViewport().setBackground(BRANCO);

        JPanel corpo = new JPanel(new BorderLayout(0,8));
        corpo.setBackground(CREME);
        corpo.add(barra, BorderLayout.NORTH); corpo.add(scrollT, BorderLayout.CENTER);
        p.add(corpo, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Aba Editar Tutor
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel criarAbaEditar() {
        JPanel p = new JPanel(new BorderLayout(16,16));
        p.setBackground(CREME); p.setBorder(new EmptyBorder(20,24,16,24));
        p.add(tituloSecao("Editar Dados do Tutor", icone("patas",24,TERRA_DARK)), BorderLayout.NORTH);

        JPanel corpo = new JPanel(new GridLayout(1,2,24,0));
        corpo.setBackground(CREME);

        // Cartão busca + form
        JPanel cEd = cartao("Alterar Dados", icone("patas",18,TERRA));
        campoEditCodigo    = campoEstilizado("Codigo do tutor", 0);
        campoEditNome      = campoEstilizado("Novo nome", 0);
        campoEditEndereco  = campoEstilizado("Novo endereco", 0);
        campoEditTelefone  = campoEstilizado("Novo telefone", 0);
        campoEditData      = campoData();

        JButton btnCarregar = botaoPrimario("Carregar dados", TERRA);
        btnCarregar.addActionListener(e -> carregarDadosEdicao());
        btnCarregar.setAlignmentX(Component.LEFT_ALIGNMENT);

        cEd.add(rotulo("Codigo do Tutor *")); cEd.add(campoEditCodigo);
        cEd.add(Box.createVerticalStrut(4));
        cEd.add(btnCarregar);
        cEd.add(Box.createVerticalStrut(10));
        JSeparator sep2 = new JSeparator(); sep2.setForeground(TERRA_LITE);
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE,1)); cEd.add(sep2);
        cEd.add(Box.createVerticalStrut(8));
        cEd.add(rotulo("Novo Nome (deixe vazio para manter):"));       cEd.add(campoEditNome);
        cEd.add(rotulo("Novo Endereco (deixe vazio para manter):"));   cEd.add(campoEditEndereco);
        cEd.add(rotulo("Novo Telefone (deixe vazio para manter):"));   cEd.add(campoEditTelefone);
        cEd.add(rotulo("Nova Data Nasc (deixe vazio para manter):")); cEd.add(campoEditData);
        cEd.add(Box.createVerticalStrut(12));
        JButton btnSalvar = botaoPrimario("Salvar alteracoes", VERDE);
        btnSalvar.addActionListener(e -> salvarEdicao());
        btnSalvar.setAlignmentX(Component.LEFT_ALIGNMENT);
        cEd.add(btnSalvar);
        cEd.add(Box.createVerticalGlue());

        // Cartão dicas
        JPanel cDica = cartao("Como usar", icone("passaro",18,TERRA));
        String[] dicas = {
                "1. Digite o codigo do tutor.",
                "2. Clique em 'Carregar dados' para",
                "   pre-preencher os campos atuais.",
                "3. Altere apenas os campos desejados.",
                "   Campos vazios mantem o valor atual.",
                "4. Clique em 'Salvar alteracoes'.",
                "",
                "Dica: o botao 'Editar' na aba",
                "Buscar/Listar ja preenche o codigo",
                "automaticamente para voce."
        };
        for (String d : dicas) {
            JLabel l = new JLabel(d);
            l.setFont(FNT_SMALL); l.setForeground(CINZA);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            cDica.add(l);
        }
        cDica.add(Box.createVerticalGlue());

        corpo.add(cEd); corpo.add(cDica);
        p.add(corpo, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Aba Gerenciar Pets
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel criarAbaGerenciar() {
        JPanel p = new JPanel(new BorderLayout(16,16));
        p.setBackground(CREME); p.setBorder(new EmptyBorder(20,24,16,24));
        p.add(tituloSecao("Gerenciar Pets de Tutores Existentes", icone("gato",24,TERRA_DARK)), BorderLayout.NORTH);

        JPanel corpo = new JPanel(new GridLayout(1,2,20,0)); corpo.setBackground(CREME);

        JPanel cAd = cartao("Adicionar Pet a Tutor Existente", icone("cao",18,TERRA));
        campoCodigoTutorPet = campoEstilizado("Codigo do tutor", 0);
        campoNomePetExist   = campoEstilizado("Nome do pet", 0);
        campoDataPetExist   = campoData();
        cAd.add(rotulo("Codigo do Tutor *")); cAd.add(campoCodigoTutorPet);
        cAd.add(rotulo("Nome do Pet *"));     cAd.add(campoNomePetExist);
        cAd.add(rotulo("Tipo *"));
        painelTiposExist = criarSeletorTipo("exist");
        cAd.add(painelTiposExist);
        cAd.add(rotulo("Tipo personalizado:"));
        campoTipoCustomExist = campoEstilizado("Ex: Iguana...", 0);
        campoTipoCustomExist.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { if (!campoTipoCustomExist.getText().isEmpty()) { tipoSelecionadoExist=""; painelTiposExist.repaint(); } }
            public void removeUpdate(DocumentEvent e)  {}
            public void changedUpdate(DocumentEvent e) {}
        });
        cAd.add(campoTipoCustomExist);
        cAd.add(rotulo("Data Nascimento (opcional)")); cAd.add(campoDataPetExist);
        cAd.add(Box.createVerticalStrut(10));
        JButton btnAd = botaoPrimario("+ Adicionar Pet", VERDE);
        btnAd.addActionListener(e -> adicionarPetExistente());
        cAd.add(btnAd); cAd.add(Box.createVerticalGlue());

        JPanel cRem = cartao("Remover Pet de Tutor Existente", icone("reptil",18,TERRA));
        campoCodigoRemover  = campoEstilizado("Codigo do tutor", 0);
        campoNomePetRemover = campoEstilizado("Nome exato do pet", 0);
        cRem.add(rotulo("Codigo do Tutor *")); cRem.add(campoCodigoRemover);
        cRem.add(rotulo("Nome do Pet *"));     cRem.add(campoNomePetRemover);
        cRem.add(Box.createVerticalStrut(10));
        JButton btnRem = botaoPrimario("- Remover Pet", VERMELHO);
        btnRem.addActionListener(e -> removerPetExistente());
        cRem.add(btnRem); cRem.add(Box.createVerticalGlue());

        corpo.add(cAd); corpo.add(cRem);
        p.add(corpo, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Aba Log
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel criarAbaLog() {
        JPanel p = new JPanel(new BorderLayout(0,10));
        p.setBackground(CREME); p.setBorder(new EmptyBorder(20,24,16,24));
        p.add(tituloSecao("Historico de Operacoes", icone("passaro",24,TERRA_DARK)), BorderLayout.NORTH);
        areaLog = new JTextArea();
        areaLog.setEditable(false); areaLog.setFont(FNT_LOG);
        areaLog.setBackground(new Color(0x1E,0x1E,0x2E)); areaLog.setForeground(new Color(0xCB,0xD2,0xEA));
        areaLog.setMargin(new Insets(10,14,10,14));
        JScrollPane sc = new JScrollPane(areaLog);
        sc.setBorder(BorderFactory.createLineBorder(TERRA_DARK,2));
        p.add(sc, BorderLayout.CENTER);
        JButton btnLimp = botaoSecundario("Limpar historico");
        btnLimp.addActionListener(e -> areaLog.setText(""));
        JPanel rod = new JPanel(new FlowLayout(FlowLayout.RIGHT)); rod.setBackground(CREME);
        rod.add(btnLimp); p.add(rod, BorderLayout.SOUTH);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Máscara de data dd/mm/aaaa
    // ══════════════════════════════════════════════════════════════════════════
    private JTextField campoData() {
        JTextField tf = new JTextField(10);
        tf.setFont(FNT_CAMPO);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CINZA_LITE,1,true),
                BorderFactory.createEmptyBorder(6,10,6,10)));
        tf.setBackground(new Color(0xFF,0xFD,0xF9)); tf.setForeground(CINZA);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(TERRA,2,true),
                        BorderFactory.createEmptyBorder(5,9,5,9)));
                if (tf.getText().isEmpty()) tf.setText("__/__/____");
            }
            @Override public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(CINZA_LITE,1,true),
                        BorderFactory.createEmptyBorder(6,10,6,10)));
                if (tf.getText().equals("__/__/____")) tf.setText("");
            }
        });
        // Mascara numerica dd/mm/aaaa — robusto para digitacao e colagem (Ctrl+V)
        ((AbstractDocument) tf.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int off, String str, AttributeSet a) throws BadLocationException {
                replace(fb, off, 0, str, a);
            }
            @Override
            public void replace(FilterBypass fb, int off, int len, String str, AttributeSet a) throws BadLocationException {
                // Extrai digitos ja existentes, excluindo o trecho sendo substituido
                String cur = fb.getDocument().getText(0, fb.getDocument().getLength());
                String antes = cur.substring(0, off);
                String depois = cur.substring(Math.min(off + len, cur.length()));
                // Digitos do que sobra do campo atual (antes + depois da selecao)
                StringBuilder digits = new StringBuilder();
                for (char c : (antes + depois).toCharArray())
                    if (Character.isDigit(c)) digits.append(c);
                // Remove os _ que eram placeholders (nao sao digitos reais)
                // Re-extrai apenas de posicoes que nao eram _ na mascara original
                // Mais simples e correto: reconstruir do zero com cur sem a parte removida
                digits = new StringBuilder();
                String semMascara = (antes + depois).replace("/","").replace("_","");
                for (char c : semMascara.toCharArray()) if (Character.isDigit(c)) digits.append(c);
                // Insere os novos digitos (colagem ou digitacao)
                if (str != null) {
                    String limpo = str.replace("/","").replace("-","").replace(".","");
                    for (char c : limpo.toCharArray())
                        if (Character.isDigit(c) && digits.length() < 8) digits.append(c);
                }
                // Formata dd/mm/aaaa
                String d = digits.toString();
                StringBuilder masked = new StringBuilder("__/__/____");
                for (int i = 0; i < d.length() && i < 8; i++) {
                    int pos = i < 2 ? i : i < 4 ? i+1 : i+2;
                    masked.setCharAt(pos, d.charAt(i));
                }
                fb.replace(0, fb.getDocument().getLength(), masked.toString(), a);
                // Posiciona cursor apos o ultimo digito inserido
                int cursorPos = masked.length();
                for (int i = 0; i < masked.length(); i++) {
                    if (masked.charAt(i) == '_') { cursorPos = i; break; }
                }
                final int cp = cursorPos;
                SwingUtilities.invokeLater(() -> { if (tf.isDisplayable()) tf.setCaretPosition(cp); });
            }
            @Override
            public void remove(FilterBypass fb, int off, int len) throws BadLocationException {
                replace(fb, off, len, "", null);
            }
        });
        return tf;
    }

    /** Estados possíveis de um campo de data com máscara. */
    private enum EstadoData { VAZIO, INCOMPLETO, COMPLETO }

    private EstadoData estadoData(JTextField tf) {
        String raw = tf.getText().trim();
        if (raw.isEmpty() || raw.equals("__/__/____")) return EstadoData.VAZIO;
        if (raw.contains("_"))                         return EstadoData.INCOMPLETO;
        return EstadoData.COMPLETO;
    }

    /** Retorna o valor formatado dd/mm/aaaa, ou "" se vazio/incompleto. */
    private String valorData(JTextField tf) {
        return estadoData(tf) == EstadoData.COMPLETO ? tf.getText().trim() : "";
    }

    /**
     * Valida campo de data obrigatório. Retorna o valor se ok.
     * Lança Exception com mensagem amigável para vazio ou incompleto.
     */
    private String validarDataObrigatoria(JTextField tf, String rotulo) throws Exception {
        switch (estadoData(tf)) {
            case VAZIO:      throw new Exception("Informe a data de " + rotulo + ".");
            case INCOMPLETO: throw new Exception("Data de " + rotulo + " incompleta. Use dd/mm/aaaa.");
            default:         return tf.getText().trim();
        }
    }

    /** Valida campo de data opcional. Retorna "" se vazio, lança se incompleto. */
    private String validarDataOpcional(JTextField tf, String rotulo) throws Exception {
        if (estadoData(tf) == EstadoData.INCOMPLETO)
            throw new Exception("Data de " + rotulo + " incompleta. Use dd/mm/aaaa.");
        return valorData(tf);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Lógica — Cadastro
    // ══════════════════════════════════════════════════════════════════════════
    private void adicionarPet() {
        String nome = campoNomePet.getText().trim();
        String tipo = resolverTipo("cad");
        if (nome.isEmpty()) { alerta("Preencha o nome do pet."); return; }
        if (tipo.isEmpty()) { alerta("Selecione ou informe o tipo do pet."); return; }
        try {
            String data = validarDataOpcional(campoDataNascPet, "nascimento do pet");
            Pet novo;
            if (data.isEmpty()) { novo = new Pet(nome, tipo); }
            else { LocalDateParsed dp = parseData(data,"pet"); novo = new Pet(nome,tipo,dp.d,dp.m,dp.a); }
            petsEmCadastro.add(novo);
            modeloPetsTemp.addElement(nome + "  (" + tipo + ")");
            atualizarContador();
            log("Pet adicionado a fila: " + nome + " (" + tipo + ")");
            campoNomePet.setText(""); campoDataNascPet.setText("");
            tipoSelecionadoCad = ""; campoTipoCustomCad.setText(""); painelTiposCad.repaint();
            campoNomePet.requestFocus();
        } catch (Exception ex) { erro(ex.getMessage()); }
    }

    private void executarCadastro() {
        String nomeT = campoNomeTutor.getText().trim();
        String endT  = campoEndereco.getText().trim();
        String telT  = campoTelefone.getText().trim();
        if (nomeT.isEmpty())          { alerta("Informe o nome do tutor."); return; }
        if (petsEmCadastro.isEmpty()) { alerta("Adicione ao menos um pet a fila antes de cadastrar."); return; }
        try {
            String dataT = validarDataObrigatoria(campoDataNascTutor, "nascimento do tutor");
            LocalDateParsed dp = parseData(dataT,"tutor");
            int codigo = controller.gerarCodigo();
            Tutor t = new Tutor(codigo, titleCase(nomeT), endT, telT, dp.d, dp.m, dp.a);
            for (Pet pet : petsEmCadastro) t.adicionarPet(pet);
            controller.cadastrarTutor(t);
            log("Tutor cadastrado - codigo " + codigo + " | " + nomeT + " | " + petsEmCadastro.size() + " pet(s).");
            petsEmCadastro.clear(); modeloPetsTemp.clear();
            atualizarContador(); limparCamposCadastro(); atualizarTabela();
            mostrarToast(titleCase(nomeT) + " cadastrado com codigo " + codigo + "!");
        } catch (Exception ex) { erro(ex.getMessage()); }
    }

    private void limparCadastro() {
        petsEmCadastro.clear(); modeloPetsTemp.clear();
        atualizarContador(); limparCamposCadastro();
        tipoSelecionadoCad = ""; if (painelTiposCad != null) painelTiposCad.repaint();
        log("Formulario limpo.");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Lógica — Editar Tutor
    // ══════════════════════════════════════════════════════════════════════════
    private void carregarDadosEdicao() {
        String codStr = campoEditCodigo.getText().trim();
        if (codStr.isEmpty()) { alerta("Informe o codigo do tutor."); return; }
        try {
            int cod = Integer.parseInt(codStr);
            Tutor t = controller.buscarTutorPorCodigo(cod);
            if (t == null) { alerta("Tutor com codigo " + cod + " nao encontrado."); return; }
            // Limpa todos os campos antes de preencher — evita resquícios do tutor anterior
            campoEditNome.setText("");
            campoEditEndereco.setText("");
            campoEditTelefone.setText("");
            campoEditData.setText("");
            // Preenche com dados atuais
            campoEditNome.setText(t.getNome());
            campoEditEndereco.setText(t.getEndereco());
            campoEditTelefone.setText(t.getTelefone());
            campoEditData.setText(DateUtil.formatar(t.getDataNasc()));
            log("Dados carregados para edicao: tutor " + cod + " - " + t.getNome());
        } catch (NumberFormatException e) { alerta("Codigo invalido."); }
    }

    private void salvarEdicao() {
        String codStr = campoEditCodigo.getText().trim();
        if (codStr.isEmpty()) { alerta("Informe o codigo do tutor."); return; }
        try {
            int cod = Integer.parseInt(codStr);
            String nome  = campoEditNome.getText().trim();
            String end   = campoEditEndereco.getText().trim();
            String tel   = campoEditTelefone.getText().trim();
            String data  = validarDataOpcional(campoEditData, "nascimento do tutor");
            String res = controller.editarTutor(cod,
                    nome.isEmpty()  ? null : titleCase(nome),
                    end.isEmpty()   ? null : end,
                    tel.isEmpty()   ? null : tel,
                    data.isEmpty()  ? null : data);
            log(res);
            atualizarTabela();
            mostrarToast("Tutor " + cod + " atualizado!");
        } catch (NumberFormatException e) { alerta("Codigo invalido."); }
        catch (Exception e) { alerta(e.getMessage()); }
    }

    /** Pega codigo da linha selecionada na tabela e abre aba de edição. */
    private void abrirEdicaoDaTabela() {
        int row = tabelaResultados.getSelectedRow();
        if (row < 0) { alerta("Selecione uma linha na tabela primeiro."); return; }
        String cod = (String) modeloTabela.getValueAt(row, 0);
        campoEditCodigo.setText(cod);
        navegarPara("editar");
        carregarDadosEdicao();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Lógica — Busca / Filtro / Exclusão
    // ══════════════════════════════════════════════════════════════════════════
    private void filtrarTabela() {
        if (modeloTabela == null) return;
        String termo = campoBuscaGlobal.getText().trim().toLowerCase();
        String cod   = campoBuscaCodigo == null ? "" : campoBuscaCodigo.getText().trim().toLowerCase();
        String filtroTipo = comboFiltroTipo == null ? "Todos" : (String) comboFiltroTipo.getSelectedItem();
        popularTabela(termo.isEmpty() ? cod : termo, filtroTipo);
    }

    private void atualizarTabela() {
        if (modeloTabela == null) return;
        String filtroTipo = comboFiltroTipo == null ? "Todos" : (String) comboFiltroTipo.getSelectedItem();
        popularTabela("", filtroTipo == null ? "Todos" : filtroTipo);
    }

    /**
     * Popula a tabela diretamente a partir dos objetos Tutor/Pet —
     * sem parse de string, imune a : ou | nos dados.
     */
    private void popularTabela(String filtro, String filtroTipo) {
        modeloTabela.setRowCount(0);
        java.util.List<Tutor> lista = controller.getTutores();
        for (Tutor t : lista) {
            String cod   = String.valueOf(t.getCodigo());
            String nome  = t.getNome();
            String tel   = t.getTelefone().isEmpty() ? "\u2014" : t.getTelefone();
            String end   = t.getEndereco();
            String nasc  = DateUtil.formatar(t.getDataNasc());
            String idade = String.valueOf(t.getIdade());
            java.util.List<Pet> pets = t.getPets();
            if (pets.isEmpty()) {
                if (!"Todos".equals(filtroTipo)) continue;
                if (filtro.isEmpty() || (cod+nome+tel+end).toLowerCase().contains(filtro))
                    modeloTabela.addRow(new Object[]{cod, nome, tel, end, nasc, idade, "\u2014", "\u2014", "\u2014"});
            } else {
                for (Pet p : pets) {
                    String nomePet = p.getNome();
                    String tipoPet = p.getTipo();
                    String nascPet = p.getDataNasc() != null ? DateUtil.formatar(p.getDataNasc()) : "\u2014";
                    if (!"Todos".equals(filtroTipo) && !tipoPet.equalsIgnoreCase(filtroTipo)) continue;
                    if (filtro.isEmpty() || (cod+nome+tel+end+nomePet+tipoPet).toLowerCase().contains(filtro))
                        modeloTabela.addRow(new Object[]{cod, nome, tel, end, nasc, idade, nomePet, tipoPet, nascPet});
                }
            }
        }
    }

    private void buscarPorCodigo() {
        String txt = campoBuscaCodigo.getText().trim();
        if (txt.isEmpty()) { alerta("Digite um codigo para buscar."); return; }
        try {
            int cod = Integer.parseInt(txt);
            log("--- Busca codigo " + cod + " ---\n" + controller.buscarPorCodigo(cod).trim() + "\n--------------------------------");
            navegarPara("log");
        } catch (NumberFormatException e) { alerta("Codigo invalido."); }
    }

    private void excluirPorCodigo() {
        String txt = campoBuscaCodigo.getText().trim();
        if (txt.isEmpty()) { alerta("Digite um codigo para excluir."); return; }
        try {
            int cod = Integer.parseInt(txt);
            int ok = JOptionPane.showConfirmDialog(this,
                    "Confirma a exclusao do tutor com codigo " + cod + "?\nEsta acao nao pode ser desfeita.",
                    "Confirmar Exclusao", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) { log(controller.excluirPorCodigo(cod).trim()); atualizarTabela(); }
        } catch (NumberFormatException e) { alerta("Codigo invalido."); }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Lógica — Gerenciar Pets
    // ══════════════════════════════════════════════════════════════════════════
    private void adicionarPetExistente() {
        String codStr=campoCodigoTutorPet.getText().trim(), nome=campoNomePetExist.getText().trim();
        String tipo=resolverTipo("exist");
        if (codStr.isEmpty()||nome.isEmpty()||tipo.isEmpty()) { alerta("Preencha codigo, nome e tipo."); return; }
        try {
            String data = validarDataOpcional(campoDataPetExist, "nascimento do pet");
            int cod=Integer.parseInt(codStr);
            Pet pet;
            if (data.isEmpty()) {
                pet = new Pet(nome, tipo);
            } else {
                LocalDateParsed dp = parseData(data, "pet");
                pet = new Pet(nome, tipo, dp.d, dp.m, dp.a);
            }
            log(controller.adicionarPetATutor(cod, pet).trim()); atualizarTabela();
            campoNomePetExist.setText(""); campoDataPetExist.setText("");
            tipoSelecionadoExist=""; campoTipoCustomExist.setText(""); painelTiposExist.repaint();
        } catch (NumberFormatException e) { alerta("Codigo invalido."); }
        catch (Exception e) { erro(e.getMessage()); }
    }

    private void removerPetExistente() {
        String codStr=campoCodigoRemover.getText().trim(), nome=campoNomePetRemover.getText().trim();
        if (codStr.isEmpty()||nome.isEmpty()) { alerta("Preencha codigo e nome do pet."); return; }
        try {
            int cod=Integer.parseInt(codStr);
            int ok=JOptionPane.showConfirmDialog(this,"Remover pet \""+nome+"\" do tutor "+cod+"?","Confirmar",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if (ok==JOptionPane.YES_OPTION) { log(controller.removerPetDeTutor(cod,nome).trim()); atualizarTabela(); campoNomePetRemover.setText(""); }
        } catch (NumberFormatException e) { alerta("Codigo invalido."); }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Helpers visuais
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel tituloSecao(String txt, ImageIcon ico) {
        JPanel p = new JPanel(new BorderLayout(10,0)); p.setBackground(CREME); p.setBorder(new EmptyBorder(0,0,14,0));
        JPanel esq = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0)); esq.setOpaque(false);
        esq.add(new JLabel(ico)); JLabel l=new JLabel(txt); l.setFont(FNT_HEADER); l.setForeground(TERRA_DARK); esq.add(l);
        p.add(esq, BorderLayout.WEST);
        JSeparator sep=new JSeparator(); sep.setForeground(CINZA_LITE); p.add(sep, BorderLayout.SOUTH);
        return p;
    }

    private JPanel cartao(String titulo, ImageIcon ico) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,16)); g2.fillRoundRect(3,5,getWidth()-5,getHeight()-5,14,14);
                g2.setColor(BRANCO); g2.fillRoundRect(0,0,getWidth()-4,getHeight()-5,14,14);
                g2.dispose(); super.paintComponent(g);
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setOpaque(false); p.setBorder(new EmptyBorder(14,16,14,16));
        JPanel cab = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0)); cab.setOpaque(false); cab.setAlignmentX(Component.LEFT_ALIGNMENT);
        cab.add(new JLabel(ico)); JLabel tit=new JLabel(titulo); tit.setFont(FNT_LABEL); tit.setForeground(TERRA_DARK); cab.add(tit);
        p.add(cab); p.add(Box.createVerticalStrut(4));
        JSeparator sep=new JSeparator(); sep.setForeground(TERRA_LITE); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1)); p.add(sep);
        p.add(Box.createVerticalStrut(8));
        return p;
    }

    private JLabel rotulo(String txt) {
        JLabel l=new JLabel(txt); l.setFont(FNT_SMALL); l.setForeground(new Color(0x77,0x66,0x55));
        l.setAlignmentX(Component.LEFT_ALIGNMENT); l.setBorder(new EmptyBorder(6,0,2,0)); return l;
    }

    private JTextField campoEstilizado(String placeholder, int cols) {
        JTextField tf = cols>0 ? new JTextField(cols) : new JTextField();
        tf.setFont(FNT_CAMPO); tf.setToolTipText(placeholder);
        tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(CINZA_LITE,1,true),BorderFactory.createEmptyBorder(6,10,6,10)));
        tf.setBackground(new Color(0xFF,0xFD,0xF9)); tf.setForeground(CINZA);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE,36)); tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(TERRA,2,true),BorderFactory.createEmptyBorder(5,9,5,9))); }
            @Override public void focusLost(FocusEvent e)   { tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(CINZA_LITE,1,true),BorderFactory.createEmptyBorder(6,10,6,10))); }
        });
        return tf;
    }

    private JButton botaoPrimario(String txt, Color cor) {
        JButton btn = new JButton(txt) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()?cor.darker():getModel().isRollover()?cor.brighter():cor;
                g2.setColor(bg); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(FNT_BTN); btn.setForeground(BRANCO); btn.setOpaque(false);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(9,20,9,20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType","text");
        btn.addPropertyChangeListener("foreground", evt -> btn.setForeground(BRANCO));
        return btn;
    }

    private JButton botaoSecundario(String txt) {
        JButton btn=new JButton(txt); btn.setFont(FNT_BTN); btn.setForeground(CINZA); btn.setBackground(CINZA_LITE);
        btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(CINZA_LITE,1,true),BorderFactory.createEmptyBorder(8,16,8,16)));
        btn.setFocusPainted(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void estilizarTabela(JTable t) {
        t.setFont(FNT_CAMPO); t.setRowHeight(28); t.setShowGrid(false); t.setIntercellSpacing(new Dimension(0,0));
        t.setBackground(BRANCO); t.setForeground(CINZA); t.setSelectionBackground(TERRA_LITE); t.setSelectionForeground(TERRA_DARK); t.setFillsViewportHeight(true);
        JTableHeader h=t.getTableHeader(); h.setFont(new Font("Georgia",Font.BOLD,12)); h.setBackground(TERRA_DARK); h.setForeground(BRANCO); h.setBorder(BorderFactory.createEmptyBorder());
        ((DefaultTableCellRenderer)h.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(table,val,sel,focus,row,col);
                setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
                if (sel) { setBackground(TERRA_LITE); setForeground(TERRA_DARK); }
                else { setBackground(row%2==0?BRANCO:new Color(0xFD,0xF0,0xE6)); setForeground(CINZA); }
                return this;
            }
        });
        // Cod, Tutor, Tel, Endereco, Nasc.T, Idade, Pet, Tipo, Nasc.P
        int[] larg={44,140,100,130,90,44,110,80,90};
        for (int i=0; i<larg.length && i<t.getColumnCount(); i++) t.getColumnModel().getColumn(i).setPreferredWidth(larg[i]);
    }

    private void mostrarToast(String msg) {
        if (!isDisplayable() || !isVisible()) { log("Info: " + msg); return; }
        try {
            JWindow toast=new JWindow(this);
            JLabel l=new JLabel("  "+msg+"  "); l.setFont(new Font("Georgia",Font.BOLD,13));
            l.setForeground(BRANCO); l.setBackground(VERDE); l.setOpaque(true); l.setBorder(new EmptyBorder(10,18,10,18));
            toast.add(l); toast.pack();
            Point loc=getLocationOnScreen();
            toast.setLocation(loc.x+getWidth()-toast.getWidth()-20, loc.y+getHeight()-toast.getHeight()-40);
            toast.setVisible(true);
            javax.swing.Timer t = new javax.swing.Timer(2800, e -> toast.dispose());
            t.setRepeats(false); t.start();
        } catch (Exception ex) { log("Info: " + msg); }
    }

    private void atualizarContador() {
        int n=petsEmCadastro.size();
        if (n==0) { labelContadorPets.setText("Nenhum pet adicionado ainda"); labelContadorPets.setForeground(CINZA); }
        else { labelContadorPets.setText(n+" pet"+(n>1?"s":"")+" na fila"); labelContadorPets.setForeground(VERDE); }
    }

    private void limparCamposCadastro() {
        campoNomeTutor.setText(""); campoEndereco.setText(""); campoTelefone.setText(""); campoDataNascTutor.setText("");
        campoNomePet.setText(""); campoDataNascPet.setText(""); campoTipoCustomCad.setText("");
        campoNomeTutor.requestFocus();
    }

    private void log(String msg) {
        if (areaLog!=null) { areaLog.append(msg+"\n"); areaLog.setCaretPosition(areaLog.getDocument().getLength()); }
        System.out.println("[LOG] "+msg);
    }

    private void alerta(String msg) { JOptionPane.showMessageDialog(this,msg,"Atencao",JOptionPane.WARNING_MESSAGE); }
    private void erro(String msg)   { JOptionPane.showMessageDialog(this,msg,"Erro",   JOptionPane.ERROR_MESSAGE); }

    private String titleCase(String s) {
        if (s == null || s.trim().isEmpty()) return "";
        String[] partes = s.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < partes.length; i++) {
            String w = partes[i];
            if (!w.isEmpty()) {
                int[] cps = w.codePoints().toArray();
                sb.appendCodePoint(Character.toUpperCase(cps[0]));
                for (int j = 1; j < cps.length; j++) sb.appendCodePoint(cps[j]);
            }
            if (i < partes.length - 1) sb.append(" ");
        }
        return sb.toString();
    }

    private static class LocalDateParsed { int d, m, a; }
    private static LocalDateParsed parseData(String texto, String ctx) throws Exception {
        if (texto==null||texto.trim().isEmpty()) throw new Exception("Data do "+ctx+" nao pode ser vazia.");
        String[] p=texto.trim().split("/"); if (p.length!=3) throw new Exception("Data do "+ctx+" em formato invalido. Use dd/mm/aaaa.");
        try { LocalDateParsed r=new LocalDateParsed(); r.d=Integer.parseInt(p[0].trim()); r.m=Integer.parseInt(p[1].trim()); r.a=Integer.parseInt(p[2].trim()); DateUtil.validar(r.d,r.m,r.a,ctx); return r; }
        catch (NumberFormatException e) { throw new Exception("Data do "+ctx+" contem caracteres nao numericos."); }
    }

    private static class PetListCellRenderer extends DefaultListCellRenderer {
        @Override public Component getListCellRendererComponent(JList<?> list, Object val, int idx, boolean sel, boolean focus) {
            JLabel l=(JLabel)super.getListCellRendererComponent(list,val,idx,sel,focus);
            l.setText("  >> "+val); l.setBorder(new EmptyBorder(6,6,6,6)); l.setFont(new Font("SansSerif",Font.PLAIN,12));
            if (!sel) { l.setBackground(idx%2==0?new Color(0xF2,0xD6,0xBB):new Color(0xFA,0xE8,0xD5)); l.setForeground(new Color(0x5C,0x30,0x10)); }
            return l;
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
                if ("Nimbus".equals(info.getName())) { UIManager.setLookAndFeel(info.getClassName()); break; }
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(PetShopGUI::new);
    }
}