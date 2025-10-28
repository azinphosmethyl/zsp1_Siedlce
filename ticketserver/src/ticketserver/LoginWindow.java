package ticketserver;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    private JTextField tfUser;
    private JPasswordField pfPass;

    public LoginWindow() {
        super("Logowanie administratora");
        setSize(300, 180);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Login:"));
        tfUser = new JTextField();
        panel.add(tfUser);

        panel.add(new JLabel("Hasło:"));
        pfPass = new JPasswordField();
        panel.add(pfPass);

        JButton btnLogin = new JButton("Zaloguj");
        JButton btnCancel = new JButton("Anuluj");
        panel.add(btnLogin);
        panel.add(btnCancel);

        add(panel);
        btnLogin.addActionListener(e -> login());
        btnCancel.addActionListener(e -> dispose());
    }

    private void login() {
        String user = tfUser.getText().trim();
        String pass = new String(pfPass.getPassword());
        if (user.equals("admin") && pass.equals("1234")) {
            dispose();
            new CheckTicketsWindow(true).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Niepoprawne dane logowania");
        }
    }
}
