package ticketserver;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;

public class ClientMain extends JFrame {
    private JTextField tfReg;
    private JTextField tfAmount;
    private JTextArea area;
    private JButton btnBuy;
    private JButton btnShow;
    private JButton btnAdmin;
    private JLabel lblRate;
    private static final double RATE = 5.0;

    public ClientMain() {
        super("Klient - Bilet Parkingowy");
        setSize(450, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelTop = new JPanel(new GridLayout(6, 2, 8, 8));
        panelTop.setBorder(BorderFactory.createTitledBorder("Dane biletu"));

        panelTop.add(new JLabel("Numer rejestracyjny:"));
        tfReg = new JTextField();
        panelTop.add(tfReg);

        panelTop.add(new JLabel("Kwota do zapłaty (PLN):"));
        tfAmount = new JTextField();
        panelTop.add(tfAmount);

        panelTop.add(new JLabel("Cena za godzinę:"));
        lblRate = new JLabel(String.format("%.2f PLN", RATE));
        panelTop.add(lblRate);

        btnBuy = new JButton("Kup bilet");
        panelTop.add(new JLabel());
        panelTop.add(btnBuy);

        btnShow = new JButton("Sprawdź bilety");
        panelTop.add(new JLabel());
        panelTop.add(btnShow);

        btnAdmin = new JButton("Panel admina");
        panelTop.add(new JLabel());
        panelTop.add(btnAdmin);

        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBorder(BorderFactory.createTitledBorder("Twój bilet"));

        add(panelTop, BorderLayout.NORTH);
        add(new JScrollPane(area), BorderLayout.CENTER);

        btnBuy.addActionListener(e -> sendTicketRequest());
        btnShow.addActionListener(e -> new CheckTicketsWindow(false).setVisible(true));
        btnAdmin.addActionListener(e -> new LoginWindow().setVisible(true));
    }

    private void sendTicketRequest() {
        String reg = tfReg.getText().trim().toUpperCase();
        String amountStr = tfAmount.getText().trim();

        if (reg.length() != 8) {
            area.setText("Błąd: numer rejestracyjny musi mieć dokładnie 8 znaków!");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            area.setText("Błąd: podaj poprawną kwotę (dodatnią)!");
            return;
        }

        try (Socket socket = new Socket("localhost", 5000);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(reg + "," + amount);

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
