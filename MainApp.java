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

 // Theme colors (same as front page)
    private static final Color BRAND_COLOR = new Color(0, 151, 178);  // teal = BOOK A FLIGHT
    private static final Color BOX_GRAY    = new Color(230, 230, 230); // gray = input/seat boxes
    
    
    private static void showMainMenu() {
        mainFrame = new JFrame("✈️ Oppa Flight Airline Ticketing System");
        mainFrame.setSize(1100, 680);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        // ===== Header Panel (cyan with magenta border) =====
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
        cancelBtn.addActionListener(e -> handleCancel());
        rebookBtn.addActionListener(e -> handleRebook());
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

        // Place left and right columns into formPanel (two columns)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(leftCol, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(rightCol, gbc);

        // Add form to outer and show
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
        // Placeholder action for confirmation
        confirmBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dlg,
                "Booking Confirmed!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        });

        bottomRow.add(backBtn);
        bottomRow.add(confirmBtn);

        outer.add(bottomRow, BorderLayout.SOUTH);

        // ==== Add everything to dialog ====
        dlg.add(outer);
        dlg.setResizable(false);
        dlg.setVisible(true);

        // ----- Action listeners for PICK and AUTO ASSIGN -----
        pickSeatBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.equals(namePlaceholder) || name.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Please enter a valid name.");
                    return;
                }
                String uniqueId = uidField.getText().trim();
                if (uniqueId.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Please enter a unique ID.");
                    return;
                }
                Passenger p = new Passenger(name, uniqueId);

                String destination = (String) destCombo.getSelectedItem();
                String seatClass = (String) seatCombo.getSelectedItem();

                Flight flight = manager.getFlight(destination);
                if (flight == null) {
                    JOptionPane.showMessageDialog(dlg, "Invalid destination selected.");
                    return;
                }

                if (manager.hasDuplicateBooking(p.getEmail(), destination)) {
                    JOptionPane.showMessageDialog(dlg, "You already have a booking to " + destination + ".");
                    return;
                }

                int avail = flight.getAvailableSeatCount(seatClass);
                if (avail <= 0) {
                    int wantWait = JOptionPane.showConfirmDialog(dlg,
                            "No seats available in " + seatClass + ". Add to waitlist?", "Waitlist",
                            JOptionPane.YES_NO_OPTION);
                    if (wantWait == JOptionPane.YES_OPTION) {
                        manager.addToWaitlist(p, destination, seatClass);
                        JOptionPane.showMessageDialog(dlg,
                                "Added to waitlist for " + destination + " (" + seatClass + ")");
                        dlg.dispose();
                    }
                    return;
                }

                List<Integer> availableSeats = flight.getAvailableSeatsList(seatClass);
                String seatsStr = availableSeats.size() > 20
                        ? "Many seats available (" + availableSeats.size() + ")"
                        : availableSeats.toString();
                String s = JOptionPane.showInputDialog(dlg, "Available seats: " + seatsStr + "\nEnter seat number (1-40):");
                if (s == null) return;
                int seatNum;
                try {
                    seatNum = Integer.parseInt(s.trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dlg, "Invalid seat number");
                    return;
                }
                if (!flight.isSeatAvailable(seatClass, seatNum)) {
                    JOptionPane.showMessageDialog(dlg, "Seat not available.");
                    return;
                }
                Booking bk = manager.reserveWithSeat(p, destination, seatClass, seatNum);
                JOptionPane.showMessageDialog(dlg, bk != null ? "Booking successful: " + bk : "Booking failed.");
                if (bk != null) dlg.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });

        autoAssignBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.equals(namePlaceholder) || name.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Please enter a valid name.");
                    return;
                }
                String uniqueId = uidField.getText().trim();
                if (uniqueId.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Please enter a unique ID.");
                    return;
                }
                Passenger p = new Passenger(name, uniqueId);

                String destination = (String) destCombo.getSelectedItem();
                String seatClass = (String) seatCombo.getSelectedItem();

                Flight flight = manager.getFlight(destination);
                if (flight == null) {
                    JOptionPane.showMessageDialog(dlg, "Invalid destination selected.");
                    return;
                }

                if (manager.hasDuplicateBooking(p.getEmail(), destination)) {
                    JOptionPane.showMessageDialog(dlg, "You already have a booking to " + destination + ".");
                    return;
                }

                Booking bk = manager.reserveAutoAssign(p, destination, seatClass);
                JOptionPane.showMessageDialog(dlg, bk != null
                        ? "Booking successful: " + bk
                        : "All seats full. Added to waitlist for " + seatClass + " to " + destination + ".");
                if (bk != null) dlg.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> dlg.dispose());
        chooseSeatSmall.addActionListener(e -> JOptionPane.showMessageDialog(dlg, "Use PICK A SEAT to choose a seat manually."));
    }

    // Small helper to create those teal rectangle labels like in the screenshot
    private static JLabel makeTealLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setOpaque(true);
        lbl.setBackground(BRAND_COLOR);  // ✅ same teal as BOOK A FLIGHT
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

    private static void handleCancel() {
        try {
            String email = JOptionPane.showInputDialog(mainFrame, "Enter your email to find your bookings:");
            if (email == null) return;
            java.util.List<Booking> bookings = manager.getBookingsByEmail(email);
            if (bookings.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "No bookings found for " + email);
                return;
            }
            String[] choices = bookings.stream().map(Booking::toString).toArray(String[]::new);
            String pick = (String) JOptionPane.showInputDialog(mainFrame, "Select booking to cancel:",
                    "Cancel Booking", JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
            if (pick == null) return;

            String bookingId = bookings.stream().filter(b -> pick.equals(b.toString())).findFirst().map(Booking::getBookingId).orElse(null);
            if (bookingId == null) return;

            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Cancel booking " + bookingId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = manager.cancelBooking(bookingId);
                JOptionPane.showMessageDialog(mainFrame, ok ? "Booking canceled." : "Failed to cancel booking.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error: " + ex.getMessage());
        }
    }

    private static void handleRebook() {
        try {
            String email = JOptionPane.showInputDialog(mainFrame, "Enter your email to find your bookings:");
            if (email == null) return;
            java.util.List<Booking> bookings = manager.getBookingsByEmail(email);
            if (bookings.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "No bookings found for " + email);
                return;
            }
            String[] choices = bookings.stream().map(Booking::toString).toArray(String[]::new);
            String pick = (String) JOptionPane.showInputDialog(mainFrame, "Select booking to rebook:",
                    "Rebook", JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
            if (pick == null) return;

            Booking original = bookings.stream().filter(b -> pick.equals(b.toString())).findFirst().orElse(null);
            if (original == null) return;

            Object[] dests = manager.getDestinations().toArray();
            String newDest = (String) JOptionPane.showInputDialog(mainFrame, "Choose new destination:", "Destinations",
                    JOptionPane.PLAIN_MESSAGE, null, dests, dests[0]);
            if (newDest == null) return;

            String[] classes = {"Economy", "Business"};
            String newClass = (String) JOptionPane.showInputDialog(mainFrame, "Choose seat class:", "Seat Class",
                    JOptionPane.PLAIN_MESSAGE, null, classes, classes[0]);
            if (newClass == null) return;

            Flight flight = manager.getFlight(newDest);
            if (manager.hasDuplicateBooking(original.getPassenger().getEmail(), newDest)) {
                JOptionPane.showMessageDialog(mainFrame, "Duplicate booking not allowed.");
                return;
            }

            int avail = flight.getAvailableSeatCount(newClass);
            if (avail <= 0) {
                int wantWait = JOptionPane.showConfirmDialog(mainFrame,
                        "No seats available. Add to waitlist?", "Waitlist", JOptionPane.YES_NO_OPTION);
                if (wantWait == JOptionPane.YES_OPTION) {
                    manager.addToWaitlist(original.getPassenger(), newDest, newClass);
                    JOptionPane.showMessageDialog(mainFrame, "Added to waitlist for " + newDest + " (" + newClass + ")");
                }
                return;
            }

            int pickSeatOrAuto = JOptionPane.showOptionDialog(mainFrame, "Pick a seat or auto-assign?", "Seat selection",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, new String[]{"Pick a seat", "Auto-assign"}, "Auto-assign");
            Booking newBk;
            if (pickSeatOrAuto == 0) {
                java.util.List<Integer> availableSeats = flight.getAvailableSeatsList(newClass);
                String seatsStr = availableSeats.size() > 20 ? "Many seats available" : availableSeats.toString();
                String s = JOptionPane.showInputDialog(mainFrame, "Available seats: " + seatsStr + "\nEnter seat number:");
                if (s == null) return;
                int seatNum = Integer.parseInt(s.trim());
                if (!flight.isSeatAvailable(newClass, seatNum)) {
                    JOptionPane.showMessageDialog(mainFrame, "Seat not available.");
                    return;
                }
                newBk = manager.rebook(original.getBookingId(), newDest, newClass, seatNum);
            } else {
                newBk = manager.rebook(original.getBookingId(), newDest, newClass, null);
            }

            JOptionPane.showMessageDialog(mainFrame, newBk != null ? "Rebook successful: " + newBk : "Rebook failed.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error: " + ex.getMessage());
        }
    }

    private static void handleView() {
        StringBuilder sb = new StringBuilder("Flights summary (origin: Philippines):\n\n");
        sb.append(manager.getFlightsSummary());
        sb.append("\n\nTip: Use Cancel/Rebook to see your bookings.");
        JOptionPane.showMessageDialog(mainFrame, sb.toString());
    }
 // Opens a static page with same size as main window
    private static void showStaticPage(String title) {
        JFrame staticFrame = new JFrame(title);
        staticFrame.setSize(1100, 680);
        staticFrame.setLocationRelativeTo(mainFrame);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 32));
        lbl.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(lbl, BorderLayout.NORTH);

        // ===== PLACEHOLDER CONTENT =====
        JTextArea content = new JTextArea("Write your " + title + " content here...");
        content.setFont(new Font("Serif", Font.PLAIN, 18));
        content.setEditable(false);
        content.setWrapStyleWord(true);
        content.setLineWrap(true);
        panel.add(new JScrollPane(content), BorderLayout.CENTER);

        // Back button
        JButton backBtn = new JButton("Back to Main Menu");
        backBtn.addActionListener(e -> staticFrame.dispose());
        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        staticFrame.add(panel);
        staticFrame.setVisible(true);
    }
}
