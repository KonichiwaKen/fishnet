package models;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("Users")
public class User {

	@Id
	public String id;
	
	public String firstName;
	public String lastName;
	public String email;
	public String password;
	
	public User() {}
	
	public User(String firstName, String lastName, String email,
			String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}
    
}
