package AIRLINE_TICKETING;

public class Passenger {
private final String name;
private final String email; // used as unique identifier in this simple demo


public Passenger(String name, String email) {
this.name = name == null ? "" : name.trim();
this.email = email == null ? "" : email.trim().toLowerCase();
}


public String getName() { return name; }
public String getEmail() { return email; }


@Override
public String toString() {
return name + " <" + email + ">";
}
}