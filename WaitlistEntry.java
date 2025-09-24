package AIRLINE_TICKETING;

public class WaitlistEntry implements Comparable<WaitlistEntry> {
private final Passenger passenger;
private final String destination;
private final String desiredClass; // "Business" or "Economy"
private final long timestamp;


public WaitlistEntry(Passenger passenger, String destination, String desiredClass) {
this.passenger = passenger;
this.destination = destination;
this.desiredClass = desiredClass;
this.timestamp = System.currentTimeMillis();
}


public Passenger getPassenger() { return passenger; }
public String getDestination() { return destination; }
public String getDesiredClass() { return desiredClass; }
public long getTimestamp() { return timestamp; }


// FIFO by timestamp (earlier entries have higher priority)
@Override
public int compareTo(WaitlistEntry other) {
return Long.compare(this.timestamp, other.timestamp);
}


@Override
public String toString() {
return passenger.toString() + " (waiting for " + desiredClass + " to " + destination + ")";
}
}