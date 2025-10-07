package worddlasora;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;

public class WordDlaSora {

    public static void main(String[] args) {
        JFrame okno = new JFrame("Word");
        okno.setSize(400, 400);
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

        okno.setVisible(true);
    }
}
