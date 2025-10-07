package AIRLINE_TICKETING;

import java.util.*;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FlightManager {
    private final Map<String, Flight> flights = new HashMap<>();
    private final Map<String, Booking> bookingsById = new HashMap<>();
    private final Map<String, List<Booking>> bookingsByEmail = new HashMap<>();
    private final AtomicInteger bookingCounter = new AtomicInteger(1000);

    private static final String[] DESTINATIONS = {"Japan", "Korea", "Thailand", "Singapore", "Canada", "USA"};

    public FlightManager() {
        for (String d : DESTINATIONS) flights.put(d, new Flight(d));
        loadFromDatabase();
    }

    public Set<String> getDestinations() {
        return flights.keySet();
    }

    private String nextBookingId() {
        return "BK" + bookingCounter.getAndIncrement();
    }

    public boolean hasDuplicateBooking(String email, String destination) {
        List<Booking> list = bookingsByEmail.getOrDefault(email.toLowerCase(), Collections.emptyList());
        for (Booking b : list)
            if (b.getDestination().equalsIgnoreCase(destination)) return true;
        return false;
    }

    public Booking reserveWithSeat(Passenger p, String destination, String seatClass, int seatNumber) {
        Flight flight = flights.get(destination);
        if (flight == null) throw new IllegalArgumentException("Unknown destination: " + destination);

        if (hasDuplicateBooking(p.getEmail(), destination)) return null;

        if (!flight.isSeatAvailable(seatClass, seatNumber)) {
            flight.addToWaitlist(new WaitlistEntry(p, destination, seatClass));
            insertWaitlistDB(p, destination, seatClass);
            return null;
        }

        boolean success = flight.bookSeat(seatClass, seatNumber);
        if (!success) {
            flight.addToWaitlist(new WaitlistEntry(p, destination, seatClass));
            insertWaitlistDB(p, destination, seatClass);
            return null;
        }

        String id = nextBookingId();
        Booking bk = new Booking(id, p, destination, seatClass, seatNumber);
        storeBooking(bk);
        
        insertBookingDB(bk, p);
        
        return bk;
    }

    
    public Booking reserveAutoAssign(Passenger p, String destination, String seatClass) {
        Flight flight = flights.get(destination);
        if (flight == null) throw new IllegalArgumentException("Unknown destination: " + destination);

        if (hasDuplicateBooking(p.getEmail(), destination)) return null;

        int seat = flight.autoAssignSeat(seatClass);
        if (seat == -1) {
            flight.addToWaitlist(new WaitlistEntry(p, destination, seatClass));
            insertWaitlistDB(p, destination, seatClass);
            return null;
        }

        String id = nextBookingId();
        Booking bk = new Booking(id, p, destination, seatClass, seat);
        storeBooking(bk);
        
        insertBookingDB(bk, p);
        
        return bk;
    }

    public void addToWaitlist(Passenger p, String destination, String seatClass) {
        Flight flight = flights.get(destination);
        flight.addToWaitlist(new WaitlistEntry(p, destination, seatClass));
    }

    private void storeBooking(Booking b) {
        bookingsById.put(b.getBookingId(), b);
        bookingsByEmail.computeIfAbsent(b.getPassenger().getEmail().toLowerCase(), k -> new ArrayList<>()).add(b);
    }

    public List<Booking> getBookingsByEmail(String email) {
        return new ArrayList<>(bookingsByEmail.getOrDefault(email.toLowerCase(), Collections.emptyList()));
    }

    public Booking getBookingById(String id) {
        Booking b = bookingsById.get(id);
        if (b != null) return b;

        if (!id.startsWith("BK")) return bookingsById.get("BK" + id);

        return null;
    }

    public List<Booking> findBookingsByAny(String input) {
        if (input == null || input.trim().isEmpty()) return Collections.emptyList();
        String s = input.trim();
        List<Booking> result = new ArrayList<>();

        Booking b = bookingsById.get(s);
        if (b != null) result.add(b);

        if (result.isEmpty() && !s.toUpperCase().startsWith("BK")) {
            Booking b2 = bookingsById.get("BK" + s);
            if (b2 != null) result.add(b2);
        }

        if (result.isEmpty()) {
            String email = s.contains("@") ? s.toLowerCase() : (s + "@example.com").toLowerCase();
            List<Booking> byEmail = bookingsByEmail.get(email);
            if (byEmail != null && !byEmail.isEmpty()) result.addAll(byEmail);
        }

        if (result.isEmpty()) {
            for (Map.Entry<String, List<Booking>> e : bookingsByEmail.entrySet()) {
                String key = e.getKey();
                String local = key.contains("@") ? key.substring(0, key.indexOf("@")) : key;
                if (local.equalsIgnoreCase(s) || key.equalsIgnoreCase(s.toLowerCase())) {
                    result.addAll(e.getValue());
                }
            }
        }

        if (result.isEmpty()) {
            for (Booking bk : bookingsById.values()) {
                String emailLower = bk.getPassenger().getEmail().toLowerCase();
                String nameLower = bk.getPassenger().getName().toLowerCase();
                if (emailLower.equalsIgnoreCase(s) || emailLower.equalsIgnoreCase((s + "@example.com")) ||
                        nameLower.equalsIgnoreCase(s.toLowerCase())) {
                    result.add(bk);
                }
            }
        }

        Map<String, Booking> uniq = new LinkedHashMap<>();
        for (Booking bk : result) uniq.put(bk.getBookingId(), bk);
        return new ArrayList<>(uniq.values());
    }

    public boolean cancelBooking(String bookingId) {
        Booking b = bookingsById.remove(bookingId);
        if (b == null) return false;

        // remove from bookingsByEmail
        List<Booking> list = bookingsByEmail.getOrDefault(b.getPassenger().getEmail(), new ArrayList<>());
        list.removeIf(x -> x.getBookingId().equals(bookingId));
        if (list.isEmpty()) bookingsByEmail.remove(b.getPassenger().getEmail());

        // free seat
        Flight flight = flights.get(b.getDestination());
        if (flight != null && flight.freeSeat(b.getSeatClass(), b.getSeatNumber())) {
            allocateFromWaitlistIfAny(b.getDestination(), b.getSeatClass());
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "DELETE FROM Bookings WHERE booking_id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, bookingId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void allocateFromWaitlistIfAny(String destination, String seatClass) {
        Flight flight = flights.get(destination);
        if (flight == null) return;

        WaitlistEntry next = flight.pollWaitlist(seatClass);
        if (next == null) return;

        int assignedSeat = flight.autoAssignSeat(seatClass);
        if (assignedSeat == -1) {
            flight.addToWaitlist(next);
            
            return;
        }

        String id = nextBookingId();
        Booking newBk = new Booking(id, next.getPassenger(), destination, seatClass, assignedSeat);
        storeBooking(newBk);
        insertBookingDB(newBk, next.getPassenger());
        System.out.println("Allocated seat to waitlisted passenger: " + newBk);
    }

    public Booking rebook(String bookingId, String newDestination, String newClass, Integer desiredSeat) {
        Booking old = bookingsById.get(bookingId);
        if (old == null) return null;

        Passenger p = old.getPassenger();

        if (hasDuplicateBooking(p.getEmail(), newDestination)) return null;

        Flight flight = flights.get(newDestination);
        if (flight == null) return null;

        Booking newBk = null;
        if (desiredSeat != null) {
            if (flight.isSeatAvailable(newClass, desiredSeat)) {
                flight.bookSeat(newClass, desiredSeat);
                String id = nextBookingId();
                newBk = new Booking(id, p, newDestination, newClass, desiredSeat);
                storeBooking(newBk);
                cancelBooking(old.getBookingId());
                return newBk;
            } else {
                return null;
            }
        } else {
            int seat = flight.autoAssignSeat(newClass);
            if (seat != -1) {
                String id = nextBookingId();
                newBk = new Booking(id, p, newDestination, newClass, seat);
                storeBooking(newBk);
                cancelBooking(old.getBookingId());
                return newBk;
            } else {
                addToWaitlist(p, newDestination, newClass);
                return null;
            }
        }
    }

    public String getFlightsSummary() {
        StringBuilder sb = new StringBuilder();
        for (String dest : flights.keySet()) {
            Flight f = flights.get(dest);
            sb.append(String.format("%s: Economy available %d, Business available %d, Waitlist E=%d, W=%d\n",
                    dest, f.getAvailableSeatCount("Economy"), f.getAvailableSeatCount("Business"),
                    f.getWaitlistSize("Economy"), f.getWaitlistSize("Business")));
        }
        return sb.toString();
    }

    public Flight getFlight(String destination) {
        return flights.get(destination);
    }

    // ✅ Added method to get all bookings
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookingsById.values());
    }
    
    private int ensurePassengerInDB(Passenger p) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            // Insert if not exists
            String sql = "INSERT INTO Passengers (name, email) VALUES (?, ?) " +
                         "ON DUPLICATE KEY UPDATE name=VALUES(name)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, p.getName());
                stmt.setString(2, p.getEmail());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1); // new ID
                }
            }

            // Already existed → fetch ID
            String fetch = "SELECT passenger_id FROM Passengers WHERE email=?";
            try (PreparedStatement stmt = conn.prepareStatement(fetch)) {
                stmt.setString(1, p.getEmail());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) return rs.getInt("passenger_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void insertBookingDB(Booking bk, Passenger p) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            int passengerId = ensurePassengerInDB(p);

    
            String fetchFlight = "SELECT flight_id FROM Flights WHERE destination=? AND seat_class=? LIMIT 1";
            int flightId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(fetchFlight)) {
                stmt.setString(1, bk.getDestination());
                stmt.setString(2, bk.getSeatClass());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    flightId = rs.getInt("flight_id");
                } else {
                    
                    String insertFlight = "INSERT INTO Flights (destination, seat_class, seat_capacity) VALUES (?, ?, 40)";
                    try (PreparedStatement stmt2 = conn.prepareStatement(insertFlight, Statement.RETURN_GENERATED_KEYS)) {
                        stmt2.setString(1, bk.getDestination());
                        stmt2.setString(2, bk.getSeatClass());
                        stmt2.executeUpdate();
                        ResultSet rs2 = stmt2.getGeneratedKeys();
                        if (rs2.next()) flightId = rs2.getInt(1);
                    }
                }
            }

         
            String sql = "INSERT INTO Bookings (booking_id, passenger_id, flight_id, seat_class, seat_number) VALUES (?,?,?,?,?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, bk.getBookingId());
                stmt.setInt(2, passengerId);
                stmt.setInt(3, flightId);
                stmt.setString(4, bk.getSeatClass());
                stmt.setInt(5, bk.getSeatNumber());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertWaitlistDB(Passenger p, String destination, String seatClass) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            int passengerId = ensurePassengerInDB(p);
            String sql = "INSERT INTO Waitlist (passenger_id, destination, seat_class) VALUES (?,?,?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, passengerId);
                stmt.setString(2, destination);
                stmt.setString(3, seatClass);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void loadFromDatabase() {
        try (Connection conn = DatabaseConnector.getConnection()) {

            // 1. Load Passengers
            Map<Integer, Passenger> passengerMap = new HashMap<>();
            String passengerSql = "SELECT passenger_id, name, email FROM Passengers";
            try (PreparedStatement stmt = conn.prepareStatement(passengerSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Passenger p = new Passenger(
                        rs.getString("name"),
                        rs.getString("email")
                    );
                    passengerMap.put(rs.getInt("passenger_id"), p);
                }
            }

            // 2. Load Flights (if you want dynamic flights in DB)
            String flightSql = "SELECT flight_id, destination, seat_class, seat_capacity FROM Flights";
            try (PreparedStatement stmt = conn.prepareStatement(flightSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String destination = rs.getString("destination");
                    if (!flights.containsKey(destination)) {
                        flights.put(destination, new Flight(destination));
                    }
                }
            }

            // 3. Load Bookings
            String bookingSql = "SELECT booking_id, passenger_id, flight_id, seat_class, seat_number " +
                                "FROM Bookings";
            try (PreparedStatement stmt = conn.prepareStatement(bookingSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String bookingId = rs.getString("booking_id");
                    int passengerId = rs.getInt("passenger_id");
                    String seatClass = rs.getString("seat_class");
                    int seatNum = rs.getInt("seat_number");

                    // Map booking to passenger
                    Passenger p = passengerMap.get(passengerId);

                    // Find destination from Flights table
                    String flightDest = null;
                    try (PreparedStatement stmt2 = conn.prepareStatement(
                            "SELECT destination FROM Flights WHERE flight_id=?")) {
                        stmt2.setInt(1, rs.getInt("flight_id"));
                        ResultSet rs2 = stmt2.executeQuery();
                        if (rs2.next()) {
                            flightDest = rs2.getString("destination");
                        }
                    }

                    if (p != null && flightDest != null) {
                        // Rebuild Booking object
                        Booking bk = new Booking(bookingId, p, flightDest, seatClass, seatNum);

                        // Store in in-memory maps
                        storeBooking(bk);

                        // Mark seat as booked in Flight object
                        Flight f = flights.get(flightDest);
                        if (f != null) {
                            f.bookSeat(seatClass, seatNum);
                        }
                    }
                }
            }

            // 4. Load Waitlist
            String waitlistSql = "SELECT w.passenger_id, w.destination, w.seat_class, w.request_time " +
                                 "FROM Waitlist w";
            try (PreparedStatement stmt = conn.prepareStatement(waitlistSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Passenger p = passengerMap.get(rs.getInt("passenger_id"));
                    if (p != null) {
                        WaitlistEntry entry = new WaitlistEntry(
                            p,
                            rs.getString("destination"),
                            rs.getString("seat_class")
                        );
                        flights.get(entry.getDestination()).addToWaitlist(entry);
                    }
                }
            }

            System.out.println("✅ Data synced from DB into memory.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("⚠ Error syncing from DB!");
        }
    }

    }


