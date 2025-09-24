package AIRLINE_TICKETING;

//Placeholder for DB integration. Hook into saveBooking / deleteBooking / loadBookings when you add a real DB.
public class DatabaseConnector {
public boolean connect() {
//TODO: implement a real database connection
System.out.println("[DB] connect() called - not implemented");
return false;
}


public void saveBooking(Booking b) {
//TODO persist booking
System.out.println("[DB] saveBooking() - not implemented: " + b);
}


public void deleteBooking(String bookingId) {
//TODO delete booking
System.out.println("[DB] deleteBooking() - not implemented: " + bookingId);
}


public void loadBookings(FlightManager manager) {
//TODO load saved bookings into manager
System.out.println("[DB] loadBookings() - not implemented");
}
}