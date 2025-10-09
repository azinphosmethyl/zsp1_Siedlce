package worddlasora;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;

public class WordDlaSora {

    public static void main(String[] args) {
        JFrame okno = new JFrame("Word");
        okno.setSize(700, 500);
        okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        okno.setLayout(new BorderLayout());

        JTextArea pole = new JTextArea();
        pole.setLineWrap(true);
        pole.setWrapStyleWord(true);
        okno.add(new JScrollPane(pole), BorderLayout.CENTER);

        JLabel licznik = new JLabel("Znaki: 0");
        okno.add(licznik, BorderLayout.SOUTH);

        pole.getDocument().addDocumentListener(new DocumentListener() {
            public void zmien() {
                licznik.setText("Znaki: " + pole.getText().length());
            }
            public void insertUpdate(DocumentEvent e) { zmien(); }
            public void removeUpdate(DocumentEvent e) { zmien(); }
            public void changedUpdate(DocumentEvent e) { zmien(); }
        });

        Integer[] rozmiary = {1, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 38, 40, 72};
        JComboBox<Integer> listaRozmiarow = new JComboBox<>(rozmiary);
        listaRozmiarow.setSelectedItem(14);

        JButton kolorTekstu = new JButton("Kolor tekstu");
        JButton kolorTla = new JButton("Kolor tła");

        listaRozmiarow.addActionListener(e -> {
            int rozmiar = (Integer) listaRozmiarow.getSelectedItem();
            pole.setFont(new Font("Arial", Font.PLAIN, rozmiar));
        });

        kolorTekstu.addActionListener(e -> {
            Color nowyKolor = JColorChooser.showDialog(okno, "Wybierz kolor tekstu", pole.getForeground());
            if (nowyKolor != null) pole.setForeground(nowyKolor);
        });

        kolorTla.addActionListener(e -> {
            Color nowyKolor = JColorChooser.showDialog(okno, "Wybierz kolor tła", pole.getBackground());
            if (nowyKolor != null) pole.setBackground(nowyKolor);
        });

        JPanel gornyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gornyPanel.add(new JLabel("Rozmiar:"));
        gornyPanel.add(listaRozmiarow);
        gornyPanel.add(kolorTekstu);
        gornyPanel.add(kolorTla);

        okno.add(gornyPanel, BorderLayout.NORTH);

        okno.setVisible(true);
    }
}
