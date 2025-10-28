package ticketserver;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.*;

public class CheckTicketsWindow extends JFrame {
    private JTextField tfReg;
    private JTextField tfDeleteId;
    private JTextPane pane;
    private boolean isAdmin;

    public CheckTicketsWindow(boolean isAdmin) {
        super(isAdmin ? "Panel admina - wszystkie bilety" : "Sprawdź bilety");
        this.isAdmin = isAdmin;
        setSize(700, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new GridLayout(isAdmin ? 1 : 2, 2, 8, 8));
        if (!isAdmin) {
            top.add(new JLabel("Numer rejestracyjny:"));
            tfReg = new JTextField();
            top.add(tfReg);
        }

        JButton btnCheck = new JButton("Pobierz bilety");
        JButton btnDelete = new JButton("Usuń bilet po ID");
        JButton btnClose = new JButton("Zamknij");

        JPanel bottom = new JPanel();
        if (isAdmin) {
            tfDeleteId = new JTextField(10);
            bottom.add(new JLabel("ID biletu do usunięcia:"));
            bottom.add(tfDeleteId);
            bottom.add(btnDelete);
        }
        bottom.add(btnCheck);
        bottom.add(btnClose);

        pane = new JTextPane();
        pane.setEditable(false);
        pane.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(pane);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        btnCheck.addActionListener(e -> fetchTickets());
        btnDelete.addActionListener(e -> deleteTicket());
        btnClose.addActionListener(e -> dispose());
    }

    private void fetchTickets() {
        String reg = isAdmin ? "ALL" : tfReg.getText().trim().toUpperCase();
        if (!isAdmin && reg.length() != 8) {
            setText("Błąd: numer rejestracyjny musi mieć dokładnie 8 znaków!", Color.RED);
            return;
        }

        try (Socket socket = new Socket("localhost", 5000);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(isAdmin ? "ADMIN_SHOW" : "SHOW," + reg);

            StringBuilder ticketBuilder = new StringBuilder();
            String line;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            while ((line = in.readLine()) != null) {
                if (line.startsWith("===========")) ticketBuilder = new StringBuilder();
                ticketBuilder.append(line).append("\n");

                if (line.startsWith("Wygasa:")) {
                    String time = line.substring(8).trim();
                    LocalDateTime expire = LocalDateTime.parse(time, fmt);
                    boolean expired = expire.isBefore(LocalDateTime.now());
                    appendColored(ticketBuilder.toString(), expired ? Color.RED : new Color(0, 150, 0));
                    ticketBuilder = new StringBuilder();
                }

                if (line.startsWith("Brak") || line.startsWith("Błąd") || line.startsWith("Kwota")) {
                    appendColored(line + "\n", Color.RED);
                }
            }

        } catch (IOException ex) {
            setText("Błąd połączenia z serwerem: " + ex.getMessage(), Color.RED);
        }
    }

    private void deleteTicket() {
        String id = tfDeleteId.getText().trim();
        if (id.isEmpty()) {
            setText("Podaj ID biletu do usunięcia!", Color.RED);
            return;
        }

        try (Socket socket = new Socket("localhost", 5000);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("DELETE," + id);
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            setText(response.toString(), Color.BLUE);

        } catch (IOException ex) {
            setText("Błąd połączenia z serwerem: " + ex.getMessage(), Color.RED);
        }
    }

    private void appendColored(String text, Color color) {
        StyledDocument doc = pane.getStyledDocument();
        Style style = pane.addStyle("", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), text + "\n", style);
        } catch (BadLocationException ignored) {}
    }

    private void setText(String text, Color color) {
        pane.setText("");
        appendColored(text, color);
    }
}
