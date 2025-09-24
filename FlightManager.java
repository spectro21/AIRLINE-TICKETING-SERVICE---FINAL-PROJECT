package AIRLINE_TICKETING;

//File: FlightManager.java
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class FlightManager {
private final Map<String, Flight> flights = new HashMap<>();
private final Map<String, Booking> bookingsById = new HashMap<>();
private final Map<String, List<Booking>> bookingsByEmail = new HashMap<>();
private final AtomicInteger bookingCounter = new AtomicInteger(1000);


//Available destinations from Philippines
private static final String[] DESTINATIONS = {"Japan", "Korea", "Thailand", "Singapore", "Canada", "USA"};


public FlightManager() {
for (String d : DESTINATIONS) flights.put(d, new Flight(d));
}


public Set<String> getDestinations() { return flights.keySet(); }


private String nextBookingId() { return "BK" + bookingCounter.getAndIncrement(); }


//Check duplicate booking: same passenger email already has booking to same destination
public boolean hasDuplicateBooking(String email, String destination) {
List<Booking> list = bookingsByEmail.getOrDefault(email.toLowerCase(), Collections.emptyList());
for (Booking b : list) if (b.getDestination().equalsIgnoreCase(destination)) return true;
return false;
}

public Booking reserveWithSeat(Passenger p, String destination, String seatClass, int seatNumber) {
Flight flight = flights.get(destination);
if (flight == null) throw new IllegalArgumentException("Unknown destination: " + destination);


if (hasDuplicateBooking(p.getEmail(), destination)) {
return null; // caller will interpret as duplicate
}


if (!flight.isSeatAvailable(seatClass, seatNumber)) {
return null; // caller will handle (seat taken)
}


boolean success = flight.bookSeat(seatClass, seatNumber);
if (!success) return null;


String id = nextBookingId();
Booking bk = new Booking(id, p, destination, seatClass, seatNumber);
storeBooking(bk);
// TODO: persist to DB via DatabaseConnector
return bk;
}


public Booking reserveAutoAssign(Passenger p, String destination, String seatClass) {
Flight flight = flights.get(destination);
if (flight == null) throw new IllegalArgumentException("Unknown destination: " + destination);


if (hasDuplicateBooking(p.getEmail(), destination)) return null;


int seat = flight.autoAssignSeat(seatClass);
if (seat == -1) {
// flight full for this class: add to waitlist
WaitlistEntry e = new WaitlistEntry(p, destination, seatClass);
flight.addToWaitlist(e);
return null; // indicates waitlisted
}
String id = nextBookingId();
Booking bk = new Booking(id, p, destination, seatClass, seat);
storeBooking(bk);
// TODO: persist to DB
return bk;
}

//Add to waitlist directly (when user chooses to be waitlisted or seat class full)
public void addToWaitlist(Passenger p, String destination, String seatClass) {
Flight flight = flights.get(destination);
WaitlistEntry e = new WaitlistEntry(p, destination, seatClass);
flight.addToWaitlist(e);
}


private void storeBooking(Booking b) {
bookingsById.put(b.getBookingId(), b);
bookingsByEmail.computeIfAbsent(b.getPassenger().getEmail().toLowerCase(), k -> new ArrayList<>()).add(b);
}


public List<Booking> getBookingsByEmail(String email) {
return new ArrayList<>(bookingsByEmail.getOrDefault(email.toLowerCase(), Collections.emptyList()));
}


public Booking getBookingById(String id) {
return bookingsById.get(id);
}


//Cancel booking by id. Returns true if canceled.
public boolean cancelBooking(String bookingId) {
Booking b = bookingsById.remove(bookingId);
if (b == null) return false;
//remove from email map
List<Booking> list = bookingsByEmail.getOrDefault(b.getPassenger().getEmail().toLowerCase(), new ArrayList<>());
list.removeIf(x -> x.getBookingId().equals(bookingId));
if (list.isEmpty()) bookingsByEmail.remove(b.getPassenger().getEmail().toLowerCase());


//free seat
Flight flight = flights.get(b.getDestination());
if (flight != null) {
boolean freed = flight.freeSeat(b.getSeatClass(), b.getSeatNumber());
//If seat freed, try to allocate to waitlist
if (freed) {
allocateFromWaitlistIfAny(b.getDestination(), b.getSeatClass());
}
}

//TODO: delete from DB via DatabaseConnector
return true;
}


//Try allocate seat to next waitlist entry for this destination and class.
private void allocateFromWaitlistIfAny(String destination, String seatClass) {
Flight flight = flights.get(destination);
if (flight == null) return;
WaitlistEntry next = flight.pollWaitlist(seatClass);
if (next == null) return;
//auto assign a seat to this person
int assignedSeat = flight.autoAssignSeat(seatClass);
if (assignedSeat == -1) {
//Shouldn't happen because we just freed one, but fallback: re-add to waitlist front
flight.addToWaitlist(next);
return;
}
String id = nextBookingId();
Booking newBk = new Booking(id, next.getPassenger(), destination, seatClass, assignedSeat);
storeBooking(newBk);
//TODO: notify user (e.g., via email) — we will simply print a message to console for now
System.out.println("Allocated seat to waitlisted passenger: " + newBk);
}


//Rebook: attempt to create a new booking on new destination for same passenger; if successful, cancel old booking.
//Returns the new Booking if successful, null otherwise. If waitlisted, returns null but adds to waitlist.
public Booking rebook(String bookingId, String newDestination, String newClass, Integer desiredSeat) {
Booking old = bookingsById.get(bookingId);
if (old == null) return null;
Passenger p = old.getPassenger();


if (hasDuplicateBooking(p.getEmail(), newDestination)) {
return null; // duplicate exists
}


//Try to reserve new seat first. If success, cancel old booking.
Flight flight = flights.get(newDestination);
if (flight == null) return null;

Booking newBk = null;
if (desiredSeat != null) {
if (flight.isSeatAvailable(newClass, desiredSeat)) {
flight.bookSeat(newClass, desiredSeat);
String id = nextBookingId();
newBk = new Booking(id, p, newDestination, newClass, desiredSeat);
storeBooking(newBk);
// then cancel old
cancelBooking(old.getBookingId());
return newBk;
} else {
// specific seat taken; fail and return null (caller may add to waitlist)
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
// full -> add to waitlist
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
dest, f.getAvailableSeatCount("Economy"), f.getAvailableSeatCount("Business"), f.getWaitlistSize("Economy"), f.getWaitlistSize("Business")));
}
return sb.toString();
}


public Flight getFlight(String destination) {
return flights.get(destination);
}
}