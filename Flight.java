package AIRLINE_TICKETING;

import java.util.*;


public class Flight {
public static final int ECON_CAPACITY = 40;
public static final int BUS_CAPACITY = 40;


private final String destination; // e.g., "Japan"
private final Set<Integer> bookedEconomy = new HashSet<>();
private final Set<Integer> bookedBusiness = new HashSet<>();


/* separate waitlists for each class (FIFO by timestamp) 
 			Priority Queue*/
private final FlightPriorityQueue<WaitlistEntry> waitlistEconomy = new FlightPriorityQueue<>();
private final FlightPriorityQueue<WaitlistEntry> waitlistBusiness = new FlightPriorityQueue<>();



public Flight(String destination) {
this.destination = destination;
}

public String getDestination() { return destination; }


public synchronized boolean isSeatAvailable(String seatClass, int seatNumber) {
if (seatClass.equalsIgnoreCase("Economy")) {
return seatNumber >= 1 && seatNumber <= ECON_CAPACITY && !bookedEconomy.contains(seatNumber);
} else {
return seatNumber >= 1 && seatNumber <= BUS_CAPACITY && !bookedBusiness.contains(seatNumber);
}
}


public synchronized int getAvailableSeatCount(String seatClass) {
if (seatClass.equalsIgnoreCase("Economy")) {
return ECON_CAPACITY - bookedEconomy.size();
} else {
return BUS_CAPACITY - bookedBusiness.size();
}
}


public synchronized boolean bookSeat(String seatClass, int seatNumber) {
if (!isSeatAvailable(seatClass, seatNumber)) return false;
if (seatClass.equalsIgnoreCase("Economy")) {
bookedEconomy.add(seatNumber);
} else {
bookedBusiness.add(seatNumber);
}
return true;
}

//Auto-assign lowest-available seat number in chosen class, or -1 if full
public synchronized int autoAssignSeat(String seatClass) {
if (getAvailableSeatCount(seatClass) <= 0) return -1;
int cap = seatClass.equalsIgnoreCase("Economy") ? ECON_CAPACITY : BUS_CAPACITY;
Set<Integer> booked = seatClass.equalsIgnoreCase("Economy") ? bookedEconomy : bookedBusiness;
for (int i = 1; i <= cap; i++) {
if (!booked.contains(i)) {
booked.add(i);
return i;
}
}
return -1;
}


public synchronized boolean freeSeat(String seatClass, int seatNumber) {
boolean removed;
if (seatClass.equalsIgnoreCase("Economy")) {
removed = bookedEconomy.remove(seatNumber);
} else {
removed = bookedBusiness.remove(seatNumber);
}
return removed;
}


public synchronized List<Integer> getBookedSeats(String seatClass) {
    if (seatClass.equalsIgnoreCase("Economy")) {
        return new ArrayList<>(bookedEconomy);
    } else {
        return new ArrayList<>(bookedBusiness);
    }
}

public synchronized List<Integer> getAvailableSeatsList(String seatClass) {
    int cap = seatClass.equalsIgnoreCase("Economy") ? ECON_CAPACITY : BUS_CAPACITY;
    Set<Integer> booked = seatClass.equalsIgnoreCase("Economy") ? bookedEconomy : bookedBusiness;

    List<Integer> available = new ArrayList<>();
    for (int i = 1; i <= cap; i++) {
        if (!booked.contains(i)) {
            available.add(i);
        }
    }
    return available;
}

public synchronized void addToWaitlist(WaitlistEntry e) {
if (e.getDesiredClass().equalsIgnoreCase("Economy")) waitlistEconomy.offer(e);
else waitlistBusiness.offer(e);
}


public synchronized WaitlistEntry pollWaitlist(String seatClass) {
if (seatClass.equalsIgnoreCase("Economy")) return waitlistEconomy.poll();
else return waitlistBusiness.poll();
}


public synchronized int getWaitlistSize(String seatClass) {
if (seatClass.equalsIgnoreCase("Economy")) return waitlistEconomy.size();
else return waitlistBusiness.size();
}
}