package AIRLINE_TICKETING;

import java.sql.Connection;
import java.sql.DriverManager;

//Placeholder for DB integration. Hook into saveBooking / deleteBooking / loadBookings when you add a real DB.
public class DatabaseConnector throws FileNotFoundException{
	
	
	Class.forName("com.mysql.cj.connect_db.Driver");
   Connection con = DriverManager.getConnection("connect_db:mysql/localhost:3306/unisoft", "root", "root");
}