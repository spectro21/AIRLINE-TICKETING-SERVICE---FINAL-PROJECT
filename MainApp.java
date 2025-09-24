package AIRLINE_TICKETING;

import javax.swing.*;
import java.awt.*;

public class MainApp {
    private static final FlightManager manager = new FlightManager();
    private static JFrame mainFrame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::showMainMenu);
    }

    private static void showMainMenu() {
        mainFrame = new JFrame("✈️ Oppa Flight Airline Ticketing System");
        mainFrame.setSize(900, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204));
        JLabel title = new JLabel("WELCOME TO OPPA AIRLINE!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        titlePanel.add(title);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        buttonPanel.setBackground(new Color(230, 240, 255));

        JButton reserveBtn = makeButton(" Reserve / Book a Flight", new Color(51, 153, 255));
        JButton cancelBtn = makeButton(" Cancel Booking", new Color(255, 102, 102));
        JButton rebookBtn = makeButton(" Rebook Flight", new Color(255, 178, 102));
        JButton viewBtn = makeButton(" View Flights", new Color(153, 204, 153));
        JButton exitBtn = makeButton(" Exit", new Color(192, 192, 192));

        // Add listeners (keep original logic)
        reserveBtn.addActionListener(e -> handleReserve());
        cancelBtn.addActionListener(e -> handleCancel());
        rebookBtn.addActionListener(e -> handleRebook());
        viewBtn.addActionListener(e -> handleView());
        exitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainFrame, "Goodbye!");
            System.exit(0);
        });

        // Add to panel
        buttonPanel.add(reserveBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(rebookBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(exitBtn);

        // Add panels to frame
        mainFrame.add(titlePanel, BorderLayout.NORTH);
        mainFrame.add(buttonPanel, BorderLayout.CENTER);

        // Center the frame
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    // Reusable button maker
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

    // ---- Logic remains the same ----
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
}
