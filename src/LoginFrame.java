import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField nameField;
    private JButton continueButton;

    public LoginFrame() {
        setTitle("Restoran Sipariş Sistemi - Giriş");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        JPanel panel = new JPanel();
        panel.setBackground(new Color(173, 216, 230)); 
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;


        JLabel label = new JLabel("Lütfen İsminizi Girin:");
        label.setForeground(Color.DARK_GRAY);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label, gbc);

        
        nameField = new JTextField(15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        nameField.setForeground(Color.GRAY);
        gbc.gridy = 1;
        panel.add(nameField, gbc);

        
        continueButton = new JButton("Devam Et");
        continueButton.setFont(new Font("Arial", Font.PLAIN, 14));
        continueButton.setBackground(new Color(100, 149, 237)); 
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        gbc.gridy = 2;
        panel.add(continueButton, gbc);

        getContentPane().add(panel);

        continueButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen isim girin!");
            } else {
                MenuFrame menuFrame = new MenuFrame(name);
                menuFrame.setVisible(true);
                this.dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
