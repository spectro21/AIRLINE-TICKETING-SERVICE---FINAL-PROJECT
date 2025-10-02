package AIRLINE_TICKETING;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FlightManager {
    private final Map<String, Flight> flights = new HashMap<>();
    private final Map<String, Booking> bookingsById = new HashMap<>();
    private final Map<String, List<Booking>> bookingsByEmail = new HashMap<>();
    private final AtomicInteger bookingCounter = new AtomicInteger(1000);

    private static final String[] DESTINATIONS = {"Japan", "Korea", "Thailand", "Singapore", "Canada", "USA"};

    public FlightManager() {
        for (String d : DESTINATIONS) flights.put(d, new Flight(d));
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
            return null;
        }

        boolean success = flight.bookSeat(seatClass, seatNumber);
        if (!success) {
            flight.addToWaitlist(new WaitlistEntry(p, destination, seatClass));
            return null;
        }

        String id = nextBookingId();
        Booking bk = new Booking(id, p, destination, seatClass, seatNumber);
        storeBooking(bk);
        return bk;
    }

    public Booking reserveAutoAssign(Passenger p, String destination, String seatClass) {
        Flight flight = flights.get(destination);
        if (flight == null) throw new IllegalArgumentException("Unknown destination: " + destination);

        if (hasDuplicateBooking(p.getEmail(), destination)) return null;

        int seat = flight.autoAssignSeat(seatClass);
        if (seat == -1) {
            flight.addToWaitlist(new WaitlistEntry(p, destination, seatClass));
            return null;
        }

        String id = nextBookingId();
        Booking bk = new Booking(id, p, destination, seatClass, seat);
        storeBooking(bk);
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

        List<Booking> list = bookingsByEmail.getOrDefault(b.getPassenger().getEmail().toLowerCase(), new ArrayList<>());
        list.removeIf(x -> x.getBookingId().equals(bookingId));
        if (list.isEmpty()) bookingsByEmail.remove(b.getPassenger().getEmail().toLowerCase());

        Flight flight = flights.get(b.getDestination());
        if (flight != null) {
            boolean freed = flight.freeSeat(b.getSeatClass(), b.getSeatNumber());
            if (freed) {
                allocateFromWaitlistIfAny(b.getDestination(), b.getSeatClass());
            }
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
}
