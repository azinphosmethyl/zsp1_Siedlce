package ticketserver;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;

public class ClientMain extends JFrame {
    private JTextField tfReg;
    private JTextField tfHours;
    private JTextArea area;
    private JButton btnBuy;
    private JLabel lblRate;
    private static final double RATE = 5.0;

    public ClientMain() {
        super("Klient - Bilet Parkingowy");
        setSize(450, 375);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelTop = new JPanel(new GridLayout(4, 2, 8, 8));
        panelTop.setBorder(BorderFactory.createTitledBorder("Dane biletu"));

        panelTop.add(new JLabel("Numer rejestracyjny:"));
        tfReg = new JTextField();
        panelTop.add(tfReg);

        panelTop.add(new JLabel("Czas postoju (h):"));
        tfHours = new JTextField();
        panelTop.add(tfHours);

        panelTop.add(new JLabel("Cena za godzinę:"));
        lblRate = new JLabel(String.format("%.2f PLN", RATE));
        panelTop.add(lblRate);

        btnBuy = new JButton("Kup bilet");
        panelTop.add(new JLabel());
        panelTop.add(btnBuy);

        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBorder(BorderFactory.createTitledBorder("Twój bilet"));

        add(panelTop, BorderLayout.NORTH);
        add(new JScrollPane(area), BorderLayout.CENTER);

        btnBuy.addActionListener(e -> sendTicketRequest());
    }

    private void sendTicketRequest() {
        String reg = tfReg.getText().trim().toUpperCase();
        String hoursStr = tfHours.getText().trim();

        if (reg.length() != 8) {
            area.setText("Błąd: numer rejestracyjny musi mieć dokładnie 8 znaków!");
            return;
        }

        int hours;
        try {
            hours = Integer.parseInt(hoursStr);
            if (hours <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            area.setText("Błąd: podaj poprawną liczbę godzin (dodatnią)!");
            return;
        }

        try (Socket socket = new Socket("localhost", 5000);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(reg + "," + hours);

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }

            area.setText(response.toString());

        } catch (IOException ex) {
            area.setText("Błąd połączenia z serwerem: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientMain().setVisible(true));
    }
}
