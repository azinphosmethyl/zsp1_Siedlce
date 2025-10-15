package worddlasora;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.*;

public class WordDlaSora {
    public static void main(String[] args) {
        JFrame okno = new JFrame("Word");
        okno.setSize(1600, 800);
        okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        okno.setLayout(new BorderLayout());

        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("ComboBox.font", new Font("SansSerif", Font.PLAIN, 14));

        JTextArea pole = new JTextArea();
        pole.setLineWrap(true);
        pole.setWrapStyleWord(true);
        pole.setFont(new Font("Arial", Font.PLAIN, 14));
        pole.setMargin(new Insets(10, 10, 10, 10));
        okno.add(new JScrollPane(pole), BorderLayout.CENTER);

        JLabel licznik = new JLabel("Znaki: 0");
        licznik.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        okno.add(licznik, BorderLayout.SOUTH);

        pole.getDocument().addDocumentListener(new DocumentListener() {
            void zmien() { licznik.setText("Znaki: " + pole.getText().length()); }
            public void insertUpdate(DocumentEvent e) { zmien(); }
            public void removeUpdate(DocumentEvent e) { zmien(); }
            public void changedUpdate(DocumentEvent e) { zmien(); }
        });

        JToolBar pasek = new JToolBar();
        pasek.setFloatable(false);
        pasek.setBackground(new Color(245, 245, 245));

        String[] czcionki = {"Arial", "Calibri", "Consolas", "Times New Roman", "Verdana", "Courier New"};
        JComboBox<String> listaCzcionek = new JComboBox<>(czcionki);
        listaCzcionek.setSelectedItem("Arial");

        Integer[] rozmiary = {8, 10, 12, 14, 16, 18, 20, 24, 28, 32};
        JComboBox<Integer> listaRozmiarow = new JComboBox<>(rozmiary);
        listaRozmiarow.setSelectedItem(14);

        JToggleButton pogrubienie = new JToggleButton("B");
        pogrubienie.setFont(new Font("SansSerif", Font.BOLD, 14));

        JToggleButton kursywa = new JToggleButton("I");
        kursywa.setFont(new Font("SansSerif", Font.ITALIC, 14));

        JButton kolorTekstu = new JButton("Kolor tekstu");
        JButton kolorTla = new JButton("Kolor tła");

        JButton kopiuj = new JButton("Kopiuj");
        JButton wytnij = new JButton("Wytnij");
        JButton wklej = new JButton("Wklej");

        JCheckBox zawijanie = new JCheckBox("Zawijaj tekst");
        zawijanie.setSelected(true);

        final Font[] aktualnaCzcionka = {new Font("Arial", Font.PLAIN, 14)};

        Runnable aktualizujStyl = () -> {
            int styl = Font.PLAIN;
            if (pogrubienie.isSelected()) styl |= Font.BOLD;
            if (kursywa.isSelected()) styl |= Font.ITALIC;
            aktualnaCzcionka[0] = new Font(
                    (String) listaCzcionek.getSelectedItem(),
                    styl,
                    (Integer) listaRozmiarow.getSelectedItem()
            );
            pole.setFont(aktualnaCzcionka[0]);
        };

        listaCzcionek.addActionListener(e -> aktualizujStyl.run());
        listaRozmiarow.addActionListener(e -> aktualizujStyl.run());
        pogrubienie.addActionListener(e -> aktualizujStyl.run());
        kursywa.addActionListener(e -> aktualizujStyl.run());

        kolorTekstu.addActionListener(e -> {
            Color nowyKolor = JColorChooser.showDialog(okno, "Wybierz kolor tekstu", pole.getForeground());
            if (nowyKolor != null) pole.setForeground(nowyKolor);
        });

        kolorTla.addActionListener(e -> {
            Color nowyKolor = JColorChooser.showDialog(okno, "Wybierz kolor tła", pole.getBackground());
            if (nowyKolor != null) pole.setBackground(nowyKolor);
        });

        kopiuj.addActionListener(e -> pole.copy());
        wytnij.addActionListener(e -> pole.cut());
        wklej.addActionListener(e -> pole.paste());
        zawijanie.addActionListener(e -> pole.setLineWrap(zawijanie.isSelected()));

        pasek.add(new JLabel("Czcionka: "));
        pasek.add(listaCzcionek);
        pasek.addSeparator();
        pasek.add(new JLabel("Rozmiar: "));
        pasek.add(listaRozmiarow);
        pasek.addSeparator();
        pasek.add(pogrubienie);
        pasek.add(kursywa);
        pasek.addSeparator();
        pasek.add(kolorTekstu);
        pasek.add(kolorTla);
        pasek.addSeparator();
        pasek.add(kopiuj);
        pasek.add(wytnij);
        pasek.add(wklej);
        pasek.addSeparator();
        pasek.add(zawijanie);

        okno.add(pasek, BorderLayout.NORTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuPlik = new JMenu("Plik");
        JMenuItem zapisz = new JMenuItem("Zapisz");
        JMenuItem wczytaj = new JMenuItem("Wczytaj");
        JMenuItem wyjdz = new JMenuItem("Wyjdź");
        menuPlik.add(zapisz);
        menuPlik.add(wczytaj);
        menuPlik.addSeparator();
        menuPlik.add(wyjdz);
        menuBar.add(menuPlik);
        okno.setJMenuBar(menuBar);

        zapisz.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(okno) == JFileChooser.APPROVE_OPTION) {
                File plik = chooser.getSelectedFile();
                try (PrintWriter out = new PrintWriter(plik)) {
                    Font f = pole.getFont();
                    out.println("[USTAWIENIA]");
                    out.println("czcionka=" + f.getFamily());
                    out.println("rozmiar=" + f.getSize());
                    out.println("styl=" + f.getStyle());
                    out.println("kolorTekstu=" + pole.getForeground().getRGB());
                    out.println("kolorTla=" + pole.getBackground().getRGB());
                    out.println("zawijanie=" + pole.getLineWrap());
                    out.println("[TEKST]");
                    out.println(pole.getText());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(okno, "Błąd zapisu: " + ex.getMessage());
                }
            }
        });

        wczytaj.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(okno) == JFileChooser.APPROVE_OPTION) {
                File plik = chooser.getSelectedFile();
                try (BufferedReader br = new BufferedReader(new FileReader(plik))) {
                    String linia;
                    StringBuilder tekst = new StringBuilder();
                    boolean czyTekst = false;
                    String czcionka = "Arial";
                    int rozmiar = 14;
                    int styl = Font.PLAIN;
                    Color kolorTxt = Color.BLACK;
                    Color kolorBg = Color.WHITE;
                    boolean wrap = true;
                    while ((linia = br.readLine()) != null) {
                        if (linia.equals("[TEKST]")) { czyTekst = true; continue; }
                        if (!czyTekst) {
                            if (linia.startsWith("czcionka=")) czcionka = linia.substring(9);
                            else if (linia.startsWith("rozmiar=")) rozmiar = Integer.parseInt(linia.substring(8));
                            else if (linia.startsWith("styl=")) styl = Integer.parseInt(linia.substring(5));
                            else if (linia.startsWith("kolorTekstu=")) kolorTxt = new Color(Integer.parseInt(linia.substring(12)));
                            else if (linia.startsWith("kolorTla=")) kolorBg = new Color(Integer.parseInt(linia.substring(9)));
                            else if (linia.startsWith("zawijanie=")) wrap = Boolean.parseBoolean(linia.substring(10));
                        } else tekst.append(linia).append("\n");
                    }
                    pole.setText(tekst.toString());
                    pole.setFont(new Font(czcionka, styl, rozmiar));
                    pole.setForeground(kolorTxt);
                    pole.setBackground(kolorBg);
                    pole.setLineWrap(wrap);
                    listaCzcionek.setSelectedItem(czcionka);
                    listaRozmiarow.setSelectedItem(rozmiar);
                    pogrubienie.setSelected((styl & Font.BOLD) != 0);
                    kursywa.setSelected((styl & Font.ITALIC) != 0);
                    zawijanie.setSelected(wrap);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(okno, "Błąd wczytywania: " + ex.getMessage());
                }
            }
        });

        wyjdz.addActionListener(e -> System.exit(0));

        okno.setVisible(true);
    }
}
