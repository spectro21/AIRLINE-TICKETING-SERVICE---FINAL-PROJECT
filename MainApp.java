package AIRLINE_TICKETING;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
        // if it already looks like an email, keep it as-is; otherwise append domain
        return uid.contains("@") ? uid : (uid + "@example.com");
    }
    
 // Theme colors (same as front page)
    private static final Color BRAND_COLOR = new Color(0, 151, 178);  // teal = BOOK A FLIGHT
    private static final Color BOX_GRAY    = new Color(230, 230, 230); // gray = input/seat boxes
    
    
    private static void showMainMenu() {
        mainFrame = new JFrame("✈️ Oppa Flight Airline Ticketing System");
        mainFrame.setSize(1100, 680);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        // ===== Header Panel (cyan ) =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 204, 255)); // cyan
        JLabel headerLabel = new JLabel("OPPA AIRLINES", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial Black", Font.BOLD, 34));
        headerLabel.setForeground(Color.BLACK);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createMatteBorder(4, 0, 4, 0, Color.CYAN));

     // ===== Navigation Bar =====
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

            // Attach action listeners for static pages
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
        // ===== Welcome Label (script-like) =====
        JLabel welcomeLabel = new JLabel("WELCOME TO OPPA AIRLINES", SwingConstants.CENTER);
        // use a fallback italic font if Monotype Corsiva not present
        welcomeLabel.setFont(new Font("Serif", Font.ITALIC, 34));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // ===== Main Buttons (2x2 and Exit centered) =====
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(25, 40, 25, 40);

        JButton bookBtn = makeMainButton("BOOK A FLIGHT");
        JButton cancelBtn = makeMainButton("CANCEL BOOKING");
        JButton rebookBtn = makeMainButton("REBOOK FLIGHT");
        JButton viewBtn = makeMainButton("VIEW FLIGHTS");
        JButton exitBtn = makeMainButton("EXIT");

        // Hook actions (preserve original logic methods)
        bookBtn.addActionListener(e -> showBookFlightForm()); // OPEN the new form (keeps logic functions intact)
        cancelBtn.addActionListener(e -> showCancelForm()); // OLD //  cancelBtn.addActionListener(e -> handleCancel());
        rebookBtn.addActionListener(e -> showRebookForm()); // OLD //  rebookBtn.addActionListener(e -> handleRebook());
        viewBtn.addActionListener(e -> handleView());
        exitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainFrame, "Goodbye!");
            System.exit(0);
        });

        // Layout the 2x2 grid + centered exit
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

        // Center panel holds nav, welcome, and the main button area
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

    
    
    
    
    
    
    
    
    
    //BOOKING FORM
    // New: Booking form that matches the provided reference image, functional buttons
    private static void showBookFlightForm() {
        JDialog dlg = new JDialog(mainFrame, "Book a Flight", true);
        dlg.setSize(1000, 520);
        dlg.setLayout(new BorderLayout());
        dlg.setLocationRelativeTo(mainFrame);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(0, 204, 255));
        outer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Top spacer for alignment consistent with screenshot
        outer.add(Box.createVerticalStrut(10), BorderLayout.NORTH);

        // Form area - two columns
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 18, 6, 18);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // LEFT COLUMN (Name, Unique ID, CHOOSE SEAT label, PICK A SEAT button)
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
        // placeholder behavior
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

        String uidPlaceholder = "Ex. 1111"; // sample placeholder
        JTextField uidField = new JTextField(uidPlaceholder);
        uidField.setFont(new Font("Arial", Font.PLAIN, 16));
        uidField.setBackground(new Color(230, 230, 230));
        uidField.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        uidField.setForeground(Color.DARK_GRAY);
        // placeholder behavior
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
        chooseSeatSmall.setBackground(new Color(0x00, 0x97, 0xB2)); // 👈 new color
        chooseSeatSmall.setForeground(Color.BLACK);
        chooseSeatSmall.setFocusPainted(false);
        chooseSeatSmall.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        chooseSeatSmall.setMaximumSize(new Dimension(220, 40));
        chooseSeatSmall.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftCol.add(chooseSeatSmall);
        leftCol.add(Box.createVerticalStrut(12));

        // PICK A SEAT (big gray button)
        JButton pickSeatBtn = new JButton("PICK A SEAT");
        pickSeatBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        pickSeatBtn.setBackground(new Color(220, 220, 220));
        pickSeatBtn.setFocusPainted(false);
        pickSeatBtn.setPreferredSize(new Dimension(420, 48));
        pickSeatBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        pickSeatBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftCol.add(pickSeatBtn);

        // RIGHT COLUMN (Destination, Seat Class, AUTO ASSIGN button)
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

        // AUTO ASSIGN (big gray button)
        JButton autoAssignBtn = new JButton("AUTO ASSIGN");
        autoAssignBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        autoAssignBtn.setBackground(new Color(220, 220, 220));
        autoAssignBtn.setFocusPainted(false);
        autoAssignBtn.setPreferredSize(new Dimension(420, 48));
        autoAssignBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        autoAssignBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightCol.add(autoAssignBtn);

        // === Seat selection panel (hidden by default) ===
        JPanel seatPanel = new JPanel(new GridLayout(5, 8, 5, 5)); // 5 rows x 8 cols = 40 seats
        seatPanel.setBackground(Color.WHITE);
        seatPanel.setVisible(false); // show only after clicking "PICK A SEAT"

        JToggleButton[] seatButtons = new JToggleButton[40];
        for (int i = 0; i < 40; i++) {
            int seatNum = i + 1;
            JToggleButton btn = new JToggleButton(String.valueOf(seatNum));
            btn.setBackground(Color.LIGHT_GRAY);
            btn.addActionListener(ev -> {
                // clear selection first
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

        // Place left and right columns into formPanel (two columns)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(leftCol, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(rightCol, gbc);

        // Add form to outer
        outer.add(formPanel, BorderLayout.CENTER);

        // ===== Bottom row with Back + Confirm =====
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomRow.setBackground(Color.WHITE);

        JButton backBtn = new JButton("BACK");
        backBtn.setBackground(new Color(0, 153, 204));
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("Arial", Font.BOLD, 12));
        backBtn.setPreferredSize(new Dimension(100, 35));
        backBtn.setFocusPainted(false);
        // Close this dialog when back is pressed
        backBtn.addActionListener(e -> dlg.dispose());

        JButton confirmBtn = new JButton("CONFIRM");
        confirmBtn.setBackground(new Color(0, 153, 204));
        confirmBtn.setForeground(Color.BLACK);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.setPreferredSize(new Dimension(100, 35));
        confirmBtn.setFocusPainted(false);
        // (no placeholder listener here — we'll attach the real one below)

        bottomRow.add(backBtn);
        bottomRow.add(confirmBtn);

        outer.add(bottomRow, BorderLayout.SOUTH);

        // ---- Attach listeners BEFORE showing dialog ----

        // Toggle seat panel visibility when PICK A SEAT pressed
        pickSeatBtn.addActionListener(e -> {
            seatPanel.setVisible(!seatPanel.isVisible());
            leftCol.revalidate();
            leftCol.repaint();
        });

        // AUTO-ASSIGN handler
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

        // CONFIRM handler (use selected seat from seatButtons)
        confirmBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String uid = uidField.getText().trim();
            String destination = (String) destCombo.getSelectedItem();
            String seatClass = (String) seatCombo.getSelectedItem();

            // find selected seat
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
                // Seat already taken → added to waitlist
                JOptionPane.showMessageDialog(dlg,
                    "Seat already taken, you’ve been added to the waitlist for " + seatClass + " to " + destination + ".",
                    "Waitlist", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            }
        });

        // Finally add outer to dialog and show (after UI is fully built and listeners attached)
        dlg.add(outer);
        dlg.setResizable(false);
        dlg.setVisible(true);
    }

    
    
    // Small helper to create those teal rectangle labels like in the screenshot
    private static JLabel makeTealLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setOpaque(true);
        lbl.setBackground(BRAND_COLOR);  //  same teal as BOOK A FLIGHT
        lbl.setForeground(Color.BLACK);
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return lbl;
    }
    // Helper to create main big buttons (teal look)
    private static JButton makeMainButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(420, 84));
        btn.setBackground(new Color(0, 153, 204)); // teal
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        return btn;
    }

    // ---- Logic remains the same as original (copied verbatim) ----
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

    // ---- Original booking logic (unchanged) ----
    private static void handleReserve() {
        try {
            String name = JOptionPane.showInputDialog(mainFrame, "Enter passenger name:");
            if (name == null) return;
            String email = JOptionPane.showInputDialog(mainFrame, "Enter passenger email (unique id):");
            if (email == null) return;
            Passenger p = new Passenger(name, email);

            Object[] dests = manager.getDestinations().toArray();
            String destination = (String) JOptionPane.showInputDialog(mainFrame, "Choose destination:", "Destinations",
                    JOptionPane.PLAIN_MESSAGE, null, dests, dests[0]);
            if (destination == null) return;

            String[] classes = {"Economy", "Business"};
            String seatClass = (String) JOptionPane.showInputDialog(mainFrame, "Choose seat class:", "Seat Class",
                    JOptionPane.PLAIN_MESSAGE, null, classes, classes[0]);
            if (seatClass == null) return;

            Flight flight = manager.getFlight(destination);
            if (manager.hasDuplicateBooking(p.getEmail(), destination)) {
                JOptionPane.showMessageDialog(mainFrame, "You already have a booking to " + destination + ".");
                return;
            }

            int avail = flight.getAvailableSeatCount(seatClass);
            if (avail <= 0) {
                int wantWait = JOptionPane.showConfirmDialog(mainFrame,
                        "No seats available in " + seatClass + ". Add to waitlist?", "Waitlist",
                        JOptionPane.YES_NO_OPTION);
                if (wantWait == JOptionPane.YES_OPTION) {
                    manager.addToWaitlist(p, destination, seatClass);
                    JOptionPane.showMessageDialog(mainFrame,
                            "Added to waitlist for " + destination + " (" + seatClass + ")");
                }
                return;
            }

            int pick = JOptionPane.showOptionDialog(mainFrame, "Choose seat selection method:", "Seat selection",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, new String[]{"Pick a seat", "Auto-assign"}, "Auto-assign");

            if (pick == 0) {
                java.util.List<Integer> availableSeats = flight.getAvailableSeatsList(seatClass);
                String seatsStr = availableSeats.size() > 20
                        ? "Many seats available (" + availableSeats.size() + ")"
                        : availableSeats.toString();
                String s = JOptionPane.showInputDialog(mainFrame, "Available seats: " + seatsStr + "\nEnter seat number (1-40):");
                if (s == null) return;
                int seatNum;
                try { seatNum = Integer.parseInt(s.trim()); }
                catch (NumberFormatException ex) { JOptionPane.showMessageDialog(mainFrame, "Invalid seat number"); return; }
                if (!flight.isSeatAvailable(seatClass, seatNum)) {
                    JOptionPane.showMessageDialog(mainFrame, "Seat not available.");
                    return;
                }
                Booking bk = manager.reserveWithSeat(p, destination, seatClass, seatNum);
                JOptionPane.showMessageDialog(mainFrame, bk != null ? "Booking successful: " + bk : "Booking failed.");
            } else {
                Booking bk = manager.reserveAutoAssign(p, destination, seatClass);
                JOptionPane.showMessageDialog(mainFrame, bk != null
                        ? "Booking successful: " + bk
                        : "All seats full. Added to waitlist for " + seatClass + " to " + destination + ".");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error: " + ex.getMessage());
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
 // === Cancel Booking Form ===
    private static void showCancelForm() {
        JDialog dlg = new JDialog(mainFrame, "Cancel Booking", true);
        dlg.setSize(980, 420);
        dlg.setLayout(new BorderLayout());
        dlg.setLocationRelativeTo(mainFrame);

        // --- Header (OPPA AIRLINES) ---
     // --- Top light blue strip (no text) ---
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 204, 255)); // same light blue as your Book a Flight window
        header.setPreferredSize(new Dimension(0, 16)); // small strip height
        dlg.add(header, BorderLayout.NORTH);

     // --- Outer content area (white) ---
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 16, 16, 16, new Color(0, 204, 255)), // thick light blue border
                BorderFactory.createEmptyBorder(20, 30, 18, 30)                          // inner padding
        ));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 18, 6, 18);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        // -------- LEFT COLUMN --------
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

        // -------- RIGHT COLUMN --------
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

        // Add columns to formPanel
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(leftCol, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(rightCol, gbc);

        outer.add(formPanel, BorderLayout.CENTER);

        // --- Bottom row (right-aligned buttons) ---
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 10));
        bottomRow.setBackground(Color.WHITE);

        JButton backBtn = new JButton("BACK");
     // style BACK as secondary button
     backBtn.setBackground(new Color(0, 153, 204));
     backBtn.setForeground(Color.BLACK);              // black text
     backBtn.setFont(new Font("Arial", Font.BOLD, 12));
     backBtn.setPreferredSize(new Dimension(100, 35));
     backBtn.setFocusPainted(false);
     backBtn.setBorder(BorderFactory.createLineBorder(new Color(0, 153, 204), 2)); // blue border
     backBtn.addActionListener(e -> dlg.dispose());

        JButton confirmBtn = new JButton("CONFIRM");
        // use inspo confirm button style
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

            // ===== TODO: plug your cancel logic here =====
            
            // Example patterns (pick one that matches your manager API):
            // boolean ok = manager.cancelBooking(uid);                        // if manager accepts String ID
            // boolean ok = manager.cancelBooking(Integer.parseInt(uid));     // if manager expects int
            // Or find booking object by unique id/email then call manager.cancelBooking(bookingId)
            //
            // I won't call manager.* methods directly here so this method compiles cleanly.
            boolean ok = false;
            try {
                // Try to locate booking by ID (with or without BK prefix)
                Booking booking = manager.getBookingById(uid);

                if (booking == null) {
                    // fallback: try broader search (email, name, etc.)
                    List<Booking> matches = manager.findBookingsByAny(uid);
                    if (!matches.isEmpty()) {
                        booking = matches.get(0); // pick first match
                    }
                }

                if (booking == null) {
                    JOptionPane.showMessageDialog(dlg, "No booking found with ID/email: " + uid);
                    return;
                }

                ok = manager.cancelBooking(booking.getBookingId());  // pass normalized ID
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error while canceling: " + ex.getMessage());
                return;
            }

            // Example fallback: show message if user didn't implement cancellation yet
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



    // === Helper - style inputs (grey background, padding, preferred size) ===
    private static void styleInputField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBackground(new Color(230, 230, 230));
        field.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        field.setPreferredSize(new Dimension(420, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        // simple focus behavior to clear example text once focused
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().startsWith("Ex.")) {
                    field.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    // keep it empty (or re-add placeholder text if you prefer)
                }
            }
        });
    }

    // === Helper - style action buttons to match small teal rectangles ===
    private static void styleActionButton(JButton btn) {
        btn.setBackground(new Color(0, 133, 140)); // teal
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setPreferredSize(new Dimension(120, 36));
    }


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
 // === Rebook Booking Form ===
    private static void showRebookForm() {
        JDialog dlg = new JDialog(mainFrame, "Rebook Booking", true);
        dlg.setSize(980, 420);
        dlg.setLayout(new BorderLayout());
        dlg.setLocationRelativeTo(mainFrame);

        // --- Header strip ---
        JPanel header = new JPanel();
        header.setBackground(new Color(0, 204, 255));
        header.setPreferredSize(new Dimension(0, 16));
        dlg.add(header, BorderLayout.NORTH);

        // --- Outer content area ---
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

        // -------- LEFT COLUMN --------
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

        // -------- RIGHT COLUMN --------
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

        // Add columns to formPanel
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(leftCol, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(rightCol, gbc);

        outer.add(formPanel, BorderLayout.CENTER);

        // --- Bottom row buttons ---
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

            // Cancel using the actual bookingId
            boolean cancelled = manager.cancelBooking(oldBooking.getBookingId());
            if (!cancelled) {
                JOptionPane.showMessageDialog(dlg,
                        "Failed to cancel booking " + oldBooking.getBookingId(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Close and redirect to booking form
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
    
    
    
    
    
    
//END LINE
 // === New Booking Options after Rebook ===
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

        // ===== LEFT COLUMN =====
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setBackground(Color.WHITE);

        JLabel nameLabel = makeTealLabel("Name");
        leftCol.add(nameLabel);
        leftCol.add(Box.createVerticalStrut(8));

        JTextField nameField = new JTextField(name); // pre-filled
        styleInputField(nameField);
        leftCol.add(nameField);
        leftCol.add(Box.createVerticalStrut(20));

        JLabel uidLabel = makeTealLabel("Unique ID");
        leftCol.add(uidLabel);
        leftCol.add(Box.createVerticalStrut(8));

        JTextField uidField = new JTextField(uid); // pre-filled
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

        // ===== RIGHT COLUMN =====
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

        // Add columns
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.5;
        formPanel.add(leftCol, gbc);
        gbc.gridx = 1;
        formPanel.add(rightCol, gbc);

        outer.add(formPanel, BorderLayout.CENTER);

        // ===== BOTTOM ROW =====
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

    
    
    
    
    private static void handleView() {
        StringBuilder sb = new StringBuilder("Flights summary (origin: Philippines):\n\n");
        sb.append(manager.getFlightsSummary());
        JOptionPane.showMessageDialog(mainFrame, sb.toString());
    }
 // Opens a static page with same size as main window
    private static void showStaticPage(String title) {
        JFrame staticFrame = new JFrame(title);
        staticFrame.setSize(1100, 680);
        staticFrame.setLocationRelativeTo(mainFrame);

        // -- NEWLY ADDED --
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 204, 255)); // cyan
        JLabel lbl = new JLabel("OPPA AIRLINES", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial BLACK", Font.BOLD, 34));
        lbl.setForeground(Color.BLACK);
        headerPanel.add(lbl, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);

        JTextArea content = new JTextArea();
        content.setFont(new Font("Serif", Font.PLAIN, 18));
        content.setEditable(false);
        content.setWrapStyleWord(true);
        content.setLineWrap(true);

        // Different content for ABOUT US, CONTACT US, etc.
        switch (title) {
            case "ABOUT US" -> content.setText(
                "At Oppa AirLines, we believe that traveling should be more than just reaching a destination, " +
                "it should be a seamless, stress-free experience. Our mission is to make every journey simple, " +
                "convenient, and enjoyable by offering reliable flights, easy booking options, and exceptional " +
                "customer care. Whether you're flying for business or leisure, we are committed to connecting you " +
                "to the world with comfort and efficiency. With a focus on safety, innovation, and hospitality, " +
                "we strive to give our passengers peace of mind and the confidence to travel with ease."
            );
            case "CONTACT US" -> content.setText(
                "✈️ Oppa Airlines - Contact Information\n\n" +
                "📍 Office Location\n" +
                "   Bangkal, Davao City, Philippines\n\n" +
                "📞 Phone Numbers\n" +
                "   Landline: (082) 234-5678\n" +
                "   Mobile: 0917-987-6543\n\n" +
                "✉️ Email Address\n" +
                "   oppairlines.ph@gmail.com\n\n" +
                "🕐 Office Hours\n" +
                "   Monday - Friday: 8:30 AM - 6:00 PM\n" +
                "   Saturday: 9:00 AM - 3:00 PM\n" +
                "   Sunday: Closed\n\n" +
                "📱 Social Media\n" +
                "   Facebook: fb.com/OppaAirlinesPH\n" +
                "   Instagram: @OppaAirlines"
            );
            case "PROMO" -> content.setText(
                "🇨🇦 Canada - Up to 30% OFF on round-trip fares\n\n" +
                "🇺🇸 USA - Save ₱5,000 on select flights\n\n" +
                "🇸🇬 Singapore - Special fare starts at ₱6,999\n\n" +
                "🇯🇵 Japan - Buy 1, Get 1 Half Off (limited seats only!)\n\n" +
                "🇹🇭 Thailand - Round-trip fare as low as ₱8,499\n\n" +
                "🇰🇷 Korea - FREE 20kg baggage allowance included\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "📅 Booking Period: September - December\n" +
                "✈️ Travel Period: Until March next year\n" +
                "📍 Visit us at Bangkal, Davao City\n" +
                "📞 (082) 234-5678 | 📱 0917-987-6543\n" +
                "✉️ oppairlines.ph@gmail.com\n" +
                "📱 Follow us: fb.com/OppaAirlinesPH | @OppaAirlines\n" +
                "✈️ Book early, travel happy - only with Oppa Airlines!"
            );
            case "NEED HELP?" -> content.setText(
                "We're here to make your journey as smooth as possible. If you're experiencing issues with booking, " +
                "payments, cancellations, or flight updates, check our quick guides and FAQs for instant solutions. " +
                "Still stuck? Our support team is just a click or call away—ready to assist you 24/7.\n\n" +
                "📞 Phone Numbers\n" +
                "   Landline: (082) 234-5678\n" +
                "   Mobile: 0917-987-6543\n\n" +
                "✉️ Email Address\n" +
                "   oppairlines.ph@gmail.com"
            );
            default -> content.setText("Write your " + title + " content here...");
        }

        // ===== CONTENT WRAPPER =====
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(230, 230, 230));
        content.setBackground(new Color(230, 230, 230));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        contentPanel.add(content, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Back button
        JButton backBtn = new JButton("BACK");
        backBtn.setBackground(new Color(0, 153, 204));
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("Arial", Font.BOLD, 12));
        backBtn.setPreferredSize(new Dimension(100, 35));
        backBtn.addActionListener(e -> staticFrame.dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        bottom.setBackground(Color.WHITE);
        bottom.add(backBtn);

        panel.add(bottom, BorderLayout.SOUTH);

        staticFrame.add(panel);
        staticFrame.setVisible(true);
    }
}