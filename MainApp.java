package AIRLINE_TICKETING;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainApp {
    private static final FlightManager manager = new FlightManager();
    private static JFrame mainFrame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::showMainMenu);
    }

    private static String normalizeUidToEmail(String uid) {
        if (uid == null) return null;
        uid = uid.trim();
        if (uid.isEmpty()) return uid;
        return uid.contains("@") ? uid : (uid + "@example.com");
    }
    
    private static final Color BRAND_COLOR = new Color(0, 151, 178);
    private static final Color BOX_GRAY    = new Color(230, 230, 230);
    
    private static void showMainMenu() {
        mainFrame = new JFrame("✈️ Oppa Flight Airline Ticketing System");
        mainFrame.setSize(1100, 680);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 204, 255));
        JLabel headerLabel = new JLabel("OPPA AIRLINES", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial Black", Font.BOLD, 34));
        headerLabel.setForeground(Color.BLACK);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createMatteBorder(4, 0, 4, 0, Color.CYAN));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 12));
        navPanel.setBackground(Color.WHITE);
        String[] navItems = {"ABOUT US", "CONTACT US", "PROMO", "NEED HELP?"};
        for (String item : navItems) {
            JButton navBtn = new JButton(item);
            navBtn.setBackground(new Color(102, 128, 255));
            navBtn.setForeground(Color.BLACK);
            navBtn.setFont(new Font("Arial", Font.BOLD, 12));
            navBtn.setFocusPainted(false);
            navBtn.setPreferredSize(new Dimension(130, 36));

            navBtn.addActionListener(e -> {
                switch (item) {
                    case "ABOUT US" -> showStaticPage("ABOUT US");
                    case "CONTACT US" -> showStaticPage("CONTACT US");
                    case "PROMO" -> showStaticPage("PROMO");
                    case "NEED HELP?" -> showStaticPage("NEED HELP?");
                }
            });
            
            navPanel.add(navBtn);
        }

        JLabel welcomeLabel = new JLabel("WELCOME TO OPPA AIRLINES", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.ITALIC, 34));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(25, 40, 25, 40);

        JButton bookBtn = makeMainButton("BOOK A FLIGHT");
        JButton cancelBtn = makeMainButton("CANCEL BOOKING");
        JButton rebookBtn = makeMainButton("REBOOK FLIGHT");
        JButton viewBtn = makeMainButton("VIEW FLIGHTS");
        JButton exitBtn = makeMainButton("EXIT");

        bookBtn.addActionListener(e -> showBookFlightForm());
        cancelBtn.addActionListener(e -> showCancelForm());
        rebookBtn.addActionListener(e -> showRebookForm());
        viewBtn.addActionListener(e -> handleView(manager));
        exitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainFrame, "Goodbye!");
            System.exit(0);
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(bookBtn, gbc);

        gbc.gridx = 1;
        mainPanel.add(cancelBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(rebookBtn, gbc);

        gbc.gridx = 1;
        mainPanel.add(viewBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(exitBtn, gbc);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(navPanel, BorderLayout.NORTH);
        centerPanel.add(welcomeLabel, BorderLayout.CENTER);
        centerPanel.add(mainPanel, BorderLayout.SOUTH);

        mainFrame.add(headerPanel, BorderLayout.NORTH);
        mainFrame.add(centerPanel, BorderLayout.CENTER);

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private static void showBookFlightForm() {
        JDialog dlg = new JDialog(mainFrame, "Book a Flight", true);
        dlg.setSize(1000, 520);
        dlg.setLayout(new BorderLayout());
        dlg.setLocationRelativeTo(mainFrame);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(0, 204, 255));
        outer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        outer.add(Box.createVerticalStrut(10), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 18, 6, 18);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setBackground(Color.WHITE);

        JLabel nameLabel = makeTealLabel("Name");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftCol.add(nameLabel);
        leftCol.add(Box.createVerticalStrut(8));

        String namePlaceholder = "Ex. Justine Nabunturan";
        JTextField nameField = new JTextField(namePlaceholder);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        nameField.setBackground(new Color(230, 230, 230));
        nameField.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        nameField.setForeground(Color.DARK_GRAY);
        nameField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (nameField.getText().equals(namePlaceholder)) {
                    nameField.setText("");
                    nameField.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (nameField.getText().trim().isEmpty()) {
                    nameField.setText(namePlaceholder);
                    nameField.setForeground(Color.DARK_GRAY);
                }
            }
        });
        leftCol.add(nameField);
        leftCol.add(Box.createVerticalStrut(20));

        JLabel uidLabel = makeTealLabel("Unique ID");
        uidLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftCol.add(uidLabel);
        leftCol.add(Box.createVerticalStrut(8));

        String uidPlaceholder = "Ex. 1111";
        JTextField uidField = new JTextField(uidPlaceholder);
        uidField.setFont(new Font("Arial", Font.PLAIN, 16));
        uidField.setBackground(new Color(230, 230, 230));
        uidField.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        uidField.setForeground(Color.DARK_GRAY);
        uidField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (uidField.getText().equals(uidPlaceholder)) {
                    uidField.setText("");
                    uidField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (uidField.getText().trim().isEmpty()) {
                    uidField.setText(uidPlaceholder);
                    uidField.setForeground(Color.DARK_GRAY);
                }
            }
        });

        leftCol.add(uidField);
        leftCol.add(Box.createVerticalStrut(28));

        JButton chooseSeatSmall = new JButton("CHOOSE SEAT");
        chooseSeatSmall.setBackground(new Color(0x00, 0x97, 0xB2));
        chooseSeatSmall.setForeground(Color.BLACK);
        chooseSeatSmall.setFocusPainted(false);
        chooseSeatSmall.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        chooseSeatSmall.setMaximumSize(new Dimension(220, 40));
        chooseSeatSmall.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftCol.add(chooseSeatSmall);
        leftCol.add(Box.createVerticalStrut(12));

        JButton pickSeatBtn = new JButton("PICK A SEAT");
        pickSeatBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        pickSeatBtn.setBackground(new Color(220, 220, 220));
        pickSeatBtn.setFocusPainted(false);
        pickSeatBtn.setPreferredSize(new Dimension(420, 48));
        pickSeatBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        pickSeatBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftCol.add(pickSeatBtn);

        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setBackground(Color.WHITE);

        JLabel destLabel = makeTealLabel("Destination");
        destLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightCol.add(destLabel);
        rightCol.add(Box.createVerticalStrut(8));

        Object[] dests = manager.getDestinations().toArray();
        JComboBox<Object> destCombo = new JComboBox<>(dests);
        destCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        destCombo.setBackground(new Color(230, 230, 230));
        destCombo.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        rightCol.add(destCombo);
        rightCol.add(Box.createVerticalStrut(20));

        JLabel seatClassLabel = makeTealLabel("Seat Class");
        seatClassLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightCol.add(seatClassLabel);
        rightCol.add(Box.createVerticalStrut(8));

        String[] seatOptions = {"Economy", "Business"};
        JComboBox<String> seatCombo = new JComboBox<>(seatOptions);
        seatCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        seatCombo.setBackground(new Color(230, 230, 230));
        seatCombo.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        rightCol.add(seatCombo);
        rightCol.add(Box.createVerticalStrut(28));

        JButton autoAssignBtn = new JButton("AUTO ASSIGN");
        autoAssignBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        autoAssignBtn.setBackground(new Color(220, 220, 220));
        autoAssignBtn.setFocusPainted(false);
        autoAssignBtn.setPreferredSize(new Dimension(420, 48));
        autoAssignBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        autoAssignBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightCol.add(autoAssignBtn);

        JPanel seatPanel = new JPanel(new GridLayout(5, 8, 5, 5));
        seatPanel.setBackground(Color.WHITE);
        seatPanel.setVisible(false);

        JToggleButton[] seatButtons = new JToggleButton[40];
        for (int i = 0; i < 40; i++) {
            int seatNum = i + 1;
            JToggleButton btn = new JToggleButton(String.valueOf(seatNum));
            btn.setBackground(Color.LIGHT_GRAY);
            btn.addActionListener(ev -> {
                for (JToggleButton b : seatButtons) {
                    if (b != null) b.setSelected(false);
                }
                btn.setSelected(true);
            });
            seatButtons[i] = btn;
            seatPanel.add(btn);
        }
        leftCol.add(Box.createVerticalStrut(15));
        leftCol.add(seatPanel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(leftCol, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(rightCol, gbc);

        outer.add(formPanel, BorderLayout.CENTER);

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomRow.setBackground(Color.WHITE);

        JButton backBtn = new JButton("BACK");
        backBtn.setBackground(new Color(0, 153, 204));
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("Arial", Font.BOLD, 12));
        backBtn.setPreferredSize(new Dimension(100, 35));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> dlg.dispose());

        JButton confirmBtn = new JButton("CONFIRM");
        confirmBtn.setBackground(new Color(0, 153, 204));
        confirmBtn.setForeground(Color.BLACK);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.setPreferredSize(new Dimension(100, 35));
        confirmBtn.setFocusPainted(false);

        bottomRow.add(backBtn);
        bottomRow.add(confirmBtn);

        outer.add(bottomRow, BorderLayout.SOUTH);

        pickSeatBtn.addActionListener(e -> {
            seatPanel.setVisible(!seatPanel.isVisible());
            leftCol.revalidate();
            leftCol.repaint();
        });

        autoAssignBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String uid = uidField.getText().trim();
            String destination = (String) destCombo.getSelectedItem();
            String seatClass = (String) seatCombo.getSelectedItem();

            String rawUid = uidField.getText().trim();
            String email = normalizeUidToEmail(rawUid);
            Passenger p = new Passenger(name, email);
            
            Booking bk = manager.reserveAutoAssign(p, destination, seatClass);

            if (bk != null) {
                JOptionPane.showMessageDialog(dlg,
                    "Auto-assigned seat " + bk.getSeatNumber() + " (" + seatClass + ")",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            } else {
                JOptionPane.showMessageDialog(dlg,
                    "Class full, added to waitlist!",
                    "Waitlist", JOptionPane.WARNING_MESSAGE);
                dlg.dispose();
            }
        });

        confirmBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String uid = uidField.getText().trim();
            String destination = (String) destCombo.getSelectedItem();
            String seatClass = (String) seatCombo.getSelectedItem();

            int chosenSeat = -1;
            for (JToggleButton b : seatButtons) {
                if (b != null && b.isSelected()) {
                    try {
                        chosenSeat = Integer.parseInt(b.getText());
                    } catch (NumberFormatException ex) {
                        chosenSeat = -1;
                    }
                    break;
                }
            }

            if (chosenSeat == -1) {
                JOptionPane.showMessageDialog(dlg, "Please select a seat first!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String rawUid = uidField.getText().trim();
            String email = normalizeUidToEmail(rawUid);
            Passenger p = new Passenger(name, email);
            Booking bk = manager.reserveWithSeat(p, destination, seatClass, chosenSeat);

            if (bk != null) {
                JOptionPane.showMessageDialog(dlg,
                    "Seat " + bk.getSeatNumber() + " booked successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            } else {
                JOptionPane.showMessageDialog(dlg,
                    "Seat already taken, you've been added to the waitlist for " + seatClass + " to " + destination + ".",
                    "Waitlist", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            }
        });

        dlg.add(outer);
        dlg.setResizable(false);
        dlg.setVisible(true);
    }

    private static JLabel makeTealLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setOpaque(true);
        lbl.setBackground(BRAND_COLOR);
        lbl.setForeground(Color.BLACK);
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return lbl;
    }

    private static JButton makeMainButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(420, 84));
        btn.setBackground(new Color(0, 153, 204));
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        return btn;
    }

    private static JButton makeButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return btn;
    }

    private static void showCancelForm() {
        JDialog dlg = new JDialog(mainFrame, "Cancel Booking", true);
        dlg.setSize(980, 420);
        dlg.setLayout(new BorderLayout());
        dlg.setLocationRelativeTo(mainFrame);

        JPanel header = new JPanel();
        header.setBackground(new Color(0, 204, 255));
        header.setPreferredSize(new Dimension(0, 16));
        dlg.add(header, BorderLayout.NORTH);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 16, 16, 16, new Color(0, 204, 255)),
                BorderFactory.createEmptyBorder(20, 30, 18, 30)
        ));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 18, 6, 18);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        JPanel leftCol = new JPanel();
        leftCol.setBackground(Color.WHITE);
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));

        JLabel nameLabel = makeTealLabel("Name");
        JTextField nameField = new JTextField("Ex. Justine Nabunturan");
        styleInputField(nameField);

        leftCol.add(nameLabel);
        leftCol.add(Box.createVerticalStrut(8));
        leftCol.add(nameField);
        leftCol.add(Box.createVerticalStrut(18));

        JLabel idLabel = makeTealLabel("Unique ID");
        JTextField idField = new JTextField("Ex. 1111");
        styleInputField(idField);

        leftCol.add(idLabel);
        leftCol.add(Box.createVerticalStrut(8));
        leftCol.add(idField);

        JPanel rightCol = new JPanel();
        rightCol.setBackground(Color.WHITE);
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));

        JLabel reasonLabel = makeTealLabel("Reason");
        String[] reasons = { "Change Date", "Flight Cancellation", "Others" };
        JComboBox<String> reasonCombo = new JComboBox<>(reasons);
        reasonCombo.setEditable(false);
        reasonCombo.setBackground(new Color(230, 230, 230));
        reasonCombo.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        reasonCombo.setPreferredSize(new Dimension(420, 40));
        reasonCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        rightCol.add(reasonLabel);
        rightCol.add(Box.createVerticalStrut(8));
        rightCol.add(reasonCombo);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(leftCol, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(rightCol, gbc);

        outer.add(formPanel, BorderLayout.CENTER);

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 10));
        bottomRow.setBackground(Color.WHITE);

        JButton backBtn = new JButton("BACK");
        backBtn.setBackground(new Color(0, 153, 204));
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("Arial", Font.BOLD, 12));
        backBtn.setPreferredSize(new Dimension(100, 35));
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createLineBorder(new Color(0, 153, 204), 2));
        backBtn.addActionListener(e -> dlg.dispose());

        JButton confirmBtn = new JButton("CONFIRM");
        confirmBtn.setBackground(new Color(0, 153, 204));
        confirmBtn.setForeground(Color.BLACK);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.setPreferredSize(new Dimension(100, 35));
        confirmBtn.setFocusPainted(false);

        confirmBtn.addActionListener(e -> {
            String uid = idField.getText().trim();
            String name = nameField.getText().trim();
            String reason = (String) reasonCombo.getSelectedItem();
            if (uid.isEmpty() || uid.startsWith("Ex.")) {
                JOptionPane.showMessageDialog(dlg, "Please enter the Unique ID.");
                return;
            }

            int ans = JOptionPane.showConfirmDialog(dlg,
                    "Cancel booking for\nName: " + name + "\nUnique ID: " + uid + "\nReason: " + reason + "\n\nProceed?",
                    "Confirm Cancel", JOptionPane.YES_NO_OPTION);
            if (ans != JOptionPane.YES_OPTION) return;

            boolean ok = false;
            try {
                Booking booking = manager.getBookingById(uid);

                if (booking == null) {
                    List<Booking> matches = manager.findBookingsByAny(uid);
                    if (!matches.isEmpty()) {
                        booking = matches.get(0);
                    }
                }

                if (booking == null) {
                    JOptionPane.showMessageDialog(dlg, "No booking found with ID/email: " + uid);
                    return;
                }

                ok = manager.cancelBooking(booking.getBookingId());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error while canceling: " + ex.getMessage());
                return;
            }

            if (!ok) {
                JOptionPane.showMessageDialog(dlg,
                        "Cancellation didn't run. Please replace the TODO area with your manager cancellation call (see commented examples).");
            } else {
                JOptionPane.showMessageDialog(dlg, "Booking canceled successfully!");
                dlg.dispose();
            } 
        });

        bottomRow.add(backBtn);
        bottomRow.add(confirmBtn);
        outer.add(bottomRow, BorderLayout.SOUTH);

        dlg.add(outer, BorderLayout.CENTER);
        dlg.setResizable(false);
        dlg.setVisible(true);
    }

    private static void styleInputField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBackground(new Color(230, 230, 230));
        field.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        field.setPreferredSize(new Dimension(420, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().startsWith("Ex.")) {
                    field.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                }
            }
        });
    }

    private static void styleActionButton(JButton btn) {
        btn.setBackground(new Color(0, 133, 140));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setPreferredSize(new Dimension(120, 36));
    }

    private static void showRebookForm() {
        JDialog dlg = new JDialog(mainFrame, "Rebook Booking", true);
        dlg.setSize(980, 420);
        dlg.setLayout(new BorderLayout());
        dlg.setLocationRelativeTo(mainFrame);

        JPanel header = new JPanel();
        header.setBackground(new Color(0, 204, 255));
        header.setPreferredSize(new Dimension(0, 16));
        dlg.add(header, BorderLayout.NORTH);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 16, 16, 16, new Color(0, 204, 255)),
                BorderFactory.createEmptyBorder(20, 30, 18, 30)
        ));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 18, 6, 18);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        JPanel leftCol = new JPanel();
        leftCol.setBackground(Color.WHITE);
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));

        JLabel nameLabel = makeTealLabel("Name");
        JTextField nameField = new JTextField("Ex. Justine Nabunturan");
        styleInputField(nameField);

        leftCol.add(nameLabel);
        leftCol.add(Box.createVerticalStrut(8));
        leftCol.add(nameField);
        leftCol.add(Box.createVerticalStrut(18));

        JLabel idLabel = makeTealLabel("Unique ID");
        JTextField idField = new JTextField("Ex. 1111");
        styleInputField(idField);

        leftCol.add(idLabel);
        leftCol.add(Box.createVerticalStrut(8));
        leftCol.add(idField);

        JPanel rightCol = new JPanel();
        rightCol.setBackground(Color.WHITE);
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));

        JLabel optionLabel = makeTealLabel("Rebook Option");
        String[] options = { "Change Date", "Change Destination", "Change Class" };
        JComboBox<String> optionCombo = new JComboBox<>(options);
        optionCombo.setEditable(false);
        optionCombo.setBackground(new Color(230, 230, 230));
        optionCombo.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        optionCombo.setPreferredSize(new Dimension(420, 40));
        optionCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        rightCol.add(optionLabel);
        rightCol.add(Box.createVerticalStrut(8));
        rightCol.add(optionCombo);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(leftCol, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(rightCol, gbc);

        outer.add(formPanel, BorderLayout.CENTER);

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 10));
        bottomRow.setBackground(Color.WHITE);

        JButton backBtn = new JButton("BACK");
        backBtn.setBackground(new Color(0, 153, 204));
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("Arial", Font.BOLD, 12));
        backBtn.setPreferredSize(new Dimension(100, 35));
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createLineBorder(new Color(0, 153, 204), 2));
        backBtn.addActionListener(e -> dlg.dispose());

        JButton confirmBtn = new JButton("CONFIRM");
        confirmBtn.setBackground(new Color(0, 153, 204));
        confirmBtn.setForeground(Color.BLACK);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.setPreferredSize(new Dimension(100, 35));
        confirmBtn.setFocusPainted(false);

        confirmBtn.addActionListener(e -> {
            String uid = idField.getText().trim();
            String name = nameField.getText().trim();
            String option = (String) optionCombo.getSelectedItem();

            if (uid.isEmpty() || uid.startsWith("Ex.")) {
                JOptionPane.showMessageDialog(dlg, "Please enter the Unique ID.");
                return;
            }

            List<Booking> matches = manager.findBookingsByAny(uid);
            Booking oldBooking = matches.isEmpty() ? null : matches.get(0);

            if (oldBooking == null) {
                JOptionPane.showMessageDialog(dlg, "No booking found for ID/UID: " + uid);
                return;
            }

            int ans = JOptionPane.showConfirmDialog(
                    dlg,
                    "Rebook flight for\nName: " + name +
                            "\nUnique ID: " + uid +
                            "\nOption: " + option + "\n\nProceed?",
                    "Confirm Rebook",
                    JOptionPane.YES_NO_OPTION
            );

            if (ans != JOptionPane.YES_OPTION) return;

            boolean cancelled = manager.cancelBooking(oldBooking.getBookingId());
            if (!cancelled) {
                JOptionPane.showMessageDialog(dlg,
                        "Failed to cancel booking " + oldBooking.getBookingId(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dlg.dispose();
            showBookFlightForm();
        });
        bottomRow.add(backBtn);
        bottomRow.add(confirmBtn);
        outer.add(bottomRow, BorderLayout.SOUTH);

        dlg.add(outer, BorderLayout.CENTER);
        dlg.setResizable(false);
        dlg.setVisible(true);
    }

    private static void showRebookBookingOptions(String name, String uid) {
        JDialog dlg = new JDialog(mainFrame, "Rebook Options", true);
        dlg.setSize(1000, 520);
        dlg.setLayout(new BorderLayout());
        dlg.setLocationRelativeTo(mainFrame);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(0, 204, 255));
        outer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 18, 6, 18);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setBackground(Color.WHITE);

        JLabel nameLabel = makeTealLabel("Name");
        leftCol.add(nameLabel);
        leftCol.add(Box.createVerticalStrut(8));

        JTextField nameField = new JTextField(name);
        styleInputField(nameField);
        leftCol.add(nameField);
        leftCol.add(Box.createVerticalStrut(20));

        JLabel uidLabel = makeTealLabel("Unique ID");
        leftCol.add(uidLabel);
        leftCol.add(Box.createVerticalStrut(8));

        JTextField uidField = new JTextField(uid);
        styleInputField(uidField);
        leftCol.add(uidField);
        leftCol.add(Box.createVerticalStrut(28));

        JButton chooseSeatSmall = new JButton("CHOOSE SEAT");
        chooseSeatSmall.setBackground(new Color(0x00, 0x97, 0xB2));
        chooseSeatSmall.setFocusPainted(false);
        chooseSeatSmall.setMaximumSize(new Dimension(220, 40));
        leftCol.add(chooseSeatSmall);
        leftCol.add(Box.createVerticalStrut(12));

        JButton pickSeatBtn = new JButton("PICK A SEAT");
        pickSeatBtn.setBackground(new Color(220, 220, 220));
        pickSeatBtn.setFocusPainted(false);
        pickSeatBtn.setPreferredSize(new Dimension(420, 48));
        leftCol.add(pickSeatBtn);

        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setBackground(Color.WHITE);

        JLabel destLabel = makeTealLabel("Destination");
        rightCol.add(destLabel);
        rightCol.add(Box.createVerticalStrut(8));

        Object[] dests = manager.getDestinations().toArray();
        JComboBox<Object> destCombo = new JComboBox<>(dests);
        destCombo.setBackground(new Color(230, 230, 230));
        destCombo.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        destCombo.setPreferredSize(new Dimension(420, 40));
        destCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        rightCol.add(destCombo);
        rightCol.add(Box.createVerticalStrut(20));

        JLabel seatClassLabel = makeTealLabel("Seat Class");
        rightCol.add(seatClassLabel);
        rightCol.add(Box.createVerticalStrut(8));

        String[] seatOptions = {"Economy", "Business"};
        JComboBox<String> seatCombo = new JComboBox<>(seatOptions);
        seatCombo.setBackground(new Color(230, 230, 230));
        seatCombo.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        seatCombo.setPreferredSize(new Dimension(420, 40));
        seatCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        rightCol.add(seatCombo);
        rightCol.add(Box.createVerticalStrut(28));

        JButton autoAssignBtn = new JButton("AUTO ASSIGN");
        autoAssignBtn.setBackground(new Color(220, 220, 220));
        autoAssignBtn.setFocusPainted(false);
        autoAssignBtn.setPreferredSize(new Dimension(420, 48));
        rightCol.add(autoAssignBtn);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.5;
        formPanel.add(leftCol, gbc);
        gbc.gridx = 1;
        formPanel.add(rightCol, gbc);

        outer.add(formPanel, BorderLayout.CENTER);

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomRow.setBackground(Color.WHITE);

        JButton backBtn = new JButton("BACK");
        backBtn.setBackground(new Color(0, 153, 204));
        backBtn.setPreferredSize(new Dimension(100, 35));
        backBtn.addActionListener(ev -> dlg.dispose());

        JButton confirmBtn = new JButton("CONFIRM");
        confirmBtn.setBackground(new Color(0, 153, 204));
        confirmBtn.setPreferredSize(new Dimension(100, 35));
        confirmBtn.addActionListener(ev -> {
            String pname = nameField.getText().trim();
            String puid = uidField.getText().trim();
            String destination = (String) destCombo.getSelectedItem();
            String seatClass = (String) seatCombo.getSelectedItem();

            Passenger p = new Passenger(pname, puid + "@example.com");
            Booking bk = manager.reserveAutoAssign(p, destination, seatClass);

            if (bk != null) {
                JOptionPane.showMessageDialog(dlg,
                        "Rebooked successfully!\nSeat: " + bk.getSeatNumber(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            } else {
                JOptionPane.showMessageDialog(dlg,
                        "Class full, added to waitlist.",
                        "Waitlist", JOptionPane.WARNING_MESSAGE);
            }
        });

        bottomRow.add(backBtn);
        bottomRow.add(confirmBtn);

        outer.add(bottomRow, BorderLayout.SOUTH);

        dlg.add(outer);
        dlg.setResizable(false);
        dlg.setVisible(true);
    }

    private static void handleView(FlightManager flightManager) {
        ViewFlightsFrame viewFrame = new ViewFlightsFrame(flightManager);
        viewFrame.setVisible(true);
    }

    private static void showStaticPage(String title) {
        JFrame staticFrame = new JFrame(title);
        staticFrame.setSize(1100, 680);
        staticFrame.setLocationRelativeTo(mainFrame);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 204, 255));
        headerPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 204, 255), 4));
        
        JLabel headerLabel = new JLabel("OPPA AIRLINES", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial Black", Font.BOLD, 34));
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(Color.WHITE);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(0, 204, 255));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);
        contentWrapper.add(titlePanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(210, 210, 210));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        if (title.equals("ABOUT US")) {
            JTextArea textArea = new JTextArea();
            textArea.setFont(new Font("Arial", Font.PLAIN, 14));
            textArea.setBackground(new Color(210, 210, 210));
            textArea.setForeground(Color.BLACK);
            textArea.setEditable(false);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            textArea.setText(
                "At Oppa AirLines, we believe that traveling should be more than just reaching a destination, " +
                "it should be a seamless, stress-free experience. Our mission is to make every journey simple, " +
                "convenient, and enjoyable by offering reliable flights, easy booking options, and exceptional " +
                "customer care. Whether you're flying for business or leisure, we are committed to connecting you " +
                "to the world with comfort and efficiency. With a focus on safety, innovation, and hospitality, " +
                "we strive to give our passengers peace of mind and the confidence to travel with ease."
            );
            contentPanel.add(textArea, BorderLayout.CENTER);
            
        } else if (title.equals("CONTACT US")) {
            JPanel contactPanel = new JPanel();
            contactPanel.setLayout(new BoxLayout(contactPanel, BoxLayout.Y_AXIS));
            contactPanel.setBackground(new Color(210, 210, 210));
            
            JLabel contactTitle = new JLabel("Oppa Airlines - Contact Information");
            contactTitle.setFont(new Font("Arial", Font.BOLD, 14));
            contactTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            contactPanel.add(contactTitle);
            contactPanel.add(Box.createVerticalStrut(15));
            
            String[] contactInfo = {
                "Office Location",
                "     Bangkal, Davao City, Philippines",
                "Phone Numbers",
                "     Landline: (082) 234-5678",
                "     Mobile: 0917-987-6543",
                "Email Address",
                "     oppairlines.ph@gmail.com",
                "Office Hours",
                "     Monday - Friday: 8:30 AM - 6:00 PM",
                "     Saturday: 9:00 AM - 3:00 PM",
                "     Sunday: Closed",
                "Social Media",
                "     Facebook: fb.com/OppaAirlinesPH",
                "     Instagram: @OppaAirlines"
            };
            
            for (String line : contactInfo) {
                JLabel label = new JLabel(line);
                label.setFont(new Font("Arial", Font.PLAIN, 13));
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                contactPanel.add(label);
                contactPanel.add(Box.createVerticalStrut(5));
            }
            
            contentPanel.add(contactPanel, BorderLayout.CENTER);
            
        } else if (title.equals("PROMO")) {
            JPanel promoPanel = new JPanel(new GridLayout(1, 2, 20, 0));
            promoPanel.setBackground(new Color(210, 210, 210));
            
            JPanel leftPromoPanel = new JPanel();
            leftPromoPanel.setLayout(new BoxLayout(leftPromoPanel, BoxLayout.Y_AXIS));
            leftPromoPanel.setBackground(new Color(210, 210, 210));
            
            String[] promos = {
                "Canada - Up to 30% OFF on round-trip fares",
                "USA - Save P5,000 on select flights",
                "Singapore - Special fare starts at P6,999",
                "Japan - Buy 1, Get 1 Half Off (limited seats only!)",
                "Thailand - Round-trip fare as low as P8,499",
                "Korea - FREE 20kg baggage allowance included"
            };
            
            for (String promo : promos) {
                JLabel promoLabel = new JLabel("• " + promo);
                promoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
                promoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                leftPromoPanel.add(promoLabel);
                leftPromoPanel.add(Box.createVerticalStrut(12));
            }
            
            JPanel rightPromoPanel = new JPanel();
            rightPromoPanel.setLayout(new BoxLayout(rightPromoPanel, BoxLayout.Y_AXIS));
            rightPromoPanel.setBackground(new Color(210, 210, 210));
            
            String[] bookingInfo = {
                "Booking Period: September - December",
                "Travel Period: Until March next year",
                "Visit us at Bangkal, Davao City",
                "(082) 234-5678 | 0917-987-6543",
                "oppairlines.ph@gmail.com",
                "Follow us: fb.com/OppaAirlinesPH | @OppaAirlines",
                "Book early, travel happy - only with Oppa Airlines!"
            };
            
            for (String info : bookingInfo) {
                JLabel infoLabel = new JLabel("• " + info);
                infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
                infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                rightPromoPanel.add(infoLabel);
                rightPromoPanel.add(Box.createVerticalStrut(8));
            }
            
            promoPanel.add(leftPromoPanel);
            promoPanel.add(rightPromoPanel);
            contentPanel.add(promoPanel, BorderLayout.CENTER);
            
        } else if (title.equals("NEED HELP?")) {
            JPanel helpPanel = new JPanel(new BorderLayout());
            helpPanel.setBackground(new Color(210, 210, 210));

            JTextArea helpText = new JTextArea();
            helpText.setFont(new Font("Arial", Font.PLAIN, 13));
            helpText.setBackground(new Color(210, 210, 210));
            helpText.setForeground(Color.BLACK);
            helpText.setEditable(false);
            helpText.setWrapStyleWord(true);
            helpText.setLineWrap(true);
            helpText.setText(
                "We're here to make your journey as smooth as possible. If you're experiencing issues with booking, " +
                "payments, cancellations, or flight updates, check our quick guides and FAQs for instant solutions. " +
                "Still stuck? Our support team is just a click or call away—ready to assist you 24/7."
            );
            helpText.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            helpPanel.add(helpText, BorderLayout.NORTH);

            JPanel contactPanel = new JPanel();
            contactPanel.setLayout(new BoxLayout(contactPanel, BoxLayout.Y_AXIS));
            contactPanel.setBackground(new Color(210, 210, 210));
            contactPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            String[] helpContact = {
                "Phone Numbers",
                "   Landline: (082) 234-5678",
                "   Mobile: 0917-987-6543",
                "Email Address",
                "   oppairlines.ph@gmail.com"
            };

            for (String line : helpContact) {
                JLabel label = new JLabel(line);
                label.setFont(new Font("Arial", Font.PLAIN, 13));
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                contactPanel.add(label);
                contactPanel.add(Box.createVerticalStrut(5));
            }

            helpPanel.add(contactPanel, BorderLayout.CENTER);
            contentPanel.add(helpPanel, BorderLayout.CENTER);
        }

        contentWrapper.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        bottomPanel.setBackground(Color.WHITE);
        
        JButton backBtn = new JButton("BACK");
        backBtn.setBackground(new Color(0, 204, 255));
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("Arial", Font.BOLD, 12));
        backBtn.setPreferredSize(new Dimension(100, 35));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> staticFrame.dispose());
        
        bottomPanel.add(backBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        staticFrame.add(mainPanel);
        staticFrame.setVisible(true);
    }

    // Inner class for ViewFlightsFrame
    static class ViewFlightsFrame extends JFrame {
        private FlightManager flightManager;
        private JTable bookingsTable;
        private DefaultTableModel tableModel;
        private JLabel infoLabel;
        
        public ViewFlightsFrame(FlightManager flightManager) {
            this.flightManager = flightManager;
            
            setTitle("View Booked Flights - OPPA Airlines");
            setSize(1000, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            initComponents();
            loadBookings();
        }
        
        private void initComponents() {
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JPanel titlePanel = new JPanel();
            titlePanel.setBackground(new Color(0, 191, 255));
            JLabel titleLabel = new JLabel("BOOKED FLIGHTS");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
            titleLabel.setForeground(Color.BLACK);
            titlePanel.add(titleLabel);
            titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
            
            String[] columnNames = {"Booking ID", "Passenger Name", "Email", "Destination", "Class", "Seat Number"};
            tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            bookingsTable = new JTable(tableModel);
            bookingsTable.setFont(new Font("Arial", Font.PLAIN, 14));
            bookingsTable.setRowHeight(30);
            bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            bookingsTable.getTableHeader().setBackground(new Color(100, 149, 237));
            bookingsTable.getTableHeader().setForeground(Color.WHITE);
            bookingsTable.setSelectionBackground(new Color(173, 216, 230));
            
            JScrollPane scrollPane = new JScrollPane(bookingsTable);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255), 2));
            
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            bottomPanel.setBackground(Color.WHITE);
            
            JButton refreshButton = createStyledButton("REFRESH", new Color(34, 139, 34));
            JButton closeButton = createStyledButton("CLOSE", new Color(220, 20, 60));
            
            refreshButton.addActionListener(e -> loadBookings());
            closeButton.addActionListener(e -> dispose());
            
            bottomPanel.add(refreshButton);
            bottomPanel.add(closeButton);
            
            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoPanel.setBackground(Color.WHITE);
            infoLabel = new JLabel("Total Bookings: 0");
            infoLabel.setFont(new Font("Arial", Font.BOLD, 16));
            infoPanel.add(infoLabel);
            
            mainPanel.add(titlePanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            
            JPanel bottomContainer = new JPanel(new BorderLayout());
            bottomContainer.setBackground(Color.WHITE);
            bottomContainer.add(infoPanel, BorderLayout.NORTH);
            bottomContainer.add(bottomPanel, BorderLayout.CENTER);
            mainPanel.add(bottomContainer, BorderLayout.SOUTH);
            
            add(mainPanel);
        }
        
        private JButton createStyledButton(String text, Color bgColor) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setBackground(bgColor);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setPreferredSize(new Dimension(150, 45));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(bgColor.darker());
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(bgColor);
                }
            });
            
            return button;
        }
        
        private void loadBookings() {
            tableModel.setRowCount(0);
            
            List<Booking> bookings = flightManager.getAllBookings();
            
            for (Booking booking : bookings) {
                Object[] row = {
                    booking.getBookingId(),
                    booking.getPassenger().getName(),
                    booking.getPassenger().getEmail(),
                    booking.getDestination(),
                    booking.getSeatClass(),
                    booking.getSeatNumber()
                };
                tableModel.addRow(row);
            }
            
            infoLabel.setText("Total Bookings: " + bookings.size());
            
            if (bookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No bookings found.", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}