package ticketserver;

import java.io.*;
import java.net.*;
import javax.swing.*;

public class ClientMain extends JFrame {
    private JTextField tfMessage;
    private JButton btnSend;
    private JTextArea area;

    public ClientMain() {
        super("Klient - TicketServer");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tfMessage = new JTextField();
        btnSend = new JButton("Wyślij");
        area = new JTextArea();
        area.setEditable(false);

        JPanel top = new JPanel(new java.awt.BorderLayout());
        top.add(new JLabel("Wiadomość: "), "West");
        top.add(tfMessage, "Center");
        top.add(btnSend, "East");

        add(top, "North");
        add(new JScrollPane(area), "Center");

        btnSend.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String msg = tfMessage.getText().trim();
        if (msg.isEmpty()) return;

        try (Socket socket = new Socket("localhost", 5000);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(msg);
            String response = in.readLine();
            area.append("Odpowiedź: " + response + "\n");

        } catch (IOException ex) {
            area.append("Błąd: " + ex.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientMain().setVisible(true));
    }
}
