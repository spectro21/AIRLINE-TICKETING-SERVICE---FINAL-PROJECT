package AIRLINE_TICKETING;

public class Booking {
private final String bookingId;
private final Passenger passenger;
private final String destination;
private final String seatClass; // "Business" or "Economy"
private final int seatNumber;
private final long timestamp;


public Booking(String bookingId, Passenger passenger, String destination, String seatClass, int seatNumber) {
this.bookingId = bookingId;
this.passenger = passenger;
this.destination = destination;
this.seatClass = seatClass;
this.seatNumber = seatNumber;
this.timestamp = System.currentTimeMillis();
}


public String getBookingId() { return bookingId; }
public Passenger getPassenger() { return passenger; }
public String getDestination() { return destination; }
public String getSeatClass() { return seatClass; }
public int getSeatNumber() { return seatNumber; }
public long getTimestamp() { return timestamp; }


@Override
public String toString() {
return String.format("[%s] %s -> %s | %s seat %d", bookingId, passenger.getEmail(), destination, seatClass, seatNumber);
}
}