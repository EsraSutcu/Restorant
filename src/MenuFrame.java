import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class MenuFrame extends JFrame {
    private String userName;
    private DefaultListModel<MenuItem> menuModel;
    private JList<MenuItem> menuList;
    private JTextField quantityField;
    private JButton addButton, confirmOrderButton;
    private JTextArea orderArea;
    private JLabel totalLabel;
    private double totalPrice = 0;

    public MenuFrame(String userName) {
        this.userName = userName;
        setTitle("Restoran Menüsü - Hoşgeldin, " + userName);
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        menuModel = new DefaultListModel<>();
        menuList = new JList<>(menuModel);
        loadMenuItems();

        quantityField = new JTextField("1", 3);
        addButton = new JButton("Sepete Ekle");
        confirmOrderButton = new JButton("Siparişi Onayla");

        orderArea = new JTextArea(10, 30);
        orderArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(orderArea);

        totalLabel = new JLabel("Toplam: 0 TL");

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Menü"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(menuList), BorderLayout.CENTER);

        JPanel quantityPanel = new JPanel();
        quantityPanel.add(new JLabel("Adet:"));
        quantityPanel.add(quantityField);
        quantityPanel.add(addButton);
        leftPanel.add(quantityPanel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("Sipariş Özeti"), BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Siparişi Onayla butonunu da sağ panele ekle
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalLabel, BorderLayout.CENTER);
        bottomPanel.add(confirmOrderButton, BorderLayout.EAST);

        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        setLayout(new GridLayout(1, 2));
        add(leftPanel);
        add(rightPanel);

        addButton.addActionListener(e -> addToOrder());

        confirmOrderButton.addActionListener(e -> showPaymentDialog());
    }

    private void loadMenuItems() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM menu_items")) {

            while (rs.next()) {
                menuModel.addElement(new MenuItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                ));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Menü yüklenirken hata: " + ex.getMessage());
        }
    }

    private void addToOrder() {
        MenuItem selected = menuList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Lütfen bir ürün seçin!");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Geçerli bir adet girin!");
            return;
        }

        try {
            OrderDAO.saveOrder(selected.getId(), quantity);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Sipariş kaydedilemedi: " + ex.getMessage());
            return;
        }

        double itemTotal = selected.getPrice() * quantity;
        totalPrice += itemTotal;
        orderArea.append(selected.getName() + " x " + quantity + " = " + itemTotal + " TL\n");
        totalLabel.setText("Toplam: " + totalPrice + " TL");
    }

    private void showPaymentDialog() {
        // Ödeme tipi seçim paneli
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new FlowLayout());

        JButton cashButton = new JButton("💵 Nakit Ödeme");
        cashButton.setFont(new Font("Arial", Font.BOLD, 18));
        cashButton.setBackground(new Color(144, 238, 144)); // Açık yeşil
        cashButton.setFocusPainted(false);

        JButton cardButton = new JButton("💳 Kart ile Ödeme");
        cardButton.setFont(new Font("Arial", Font.BOLD, 18));
        cardButton.setBackground(new Color(135, 206, 235)); // Açık mavi
        cardButton.setFocusPainted(false);

        paymentPanel.add(cashButton);
        paymentPanel.add(cardButton);

        JDialog dialog = new JDialog(this, "Ödeme Tipi Seçiniz", true);
        dialog.getContentPane().add(paymentPanel);
        dialog.setSize(350, 120);
        dialog.setLocationRelativeTo(this);

        cashButton.addActionListener(e -> {
            dialog.dispose();
            JOptionPane.showMessageDialog(this,
                "Siparişiniz başarıyla oluşturuldu.\n" +
                "Toplam Tutar: " + totalPrice + " TL\n" +
                "Ödeme Tipi: Nakit");
            this.dispose();
        });

        cardButton.addActionListener(e -> {
            dialog.dispose();
            JOptionPane.showMessageDialog(this,
                "Siparişiniz başarıyla oluşturuldu.\n" +
                "Toplam Tutar: " + totalPrice + " TL\n" +
                "Ödeme Tipi: Kart");
            this.dispose();
        });

        dialog.setVisible(true);
    }
}
