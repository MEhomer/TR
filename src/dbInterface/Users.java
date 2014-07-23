package dbInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Dimitar on 13.2.14.
 */
// Users class
public class Users implements Serializable {
    private Map<Integer, User> users; // Map of Integer and User. Key is User ID and Value is the User
    // Default constructor
    public Users(){
        users = new TreeMap<Integer, User>(); // Initializing the map
    }
    // Adding a user to the map
    public void addUser(User user){
        users.put(user.getId(), user);
    }
    // Getting the user from his ID
    public User getUser(int id){
        return users.get(id);
    }
    // Returning a List of Users
    public ArrayList<User> getUsers(){
        return Collections.list(Collections.enumeration(users.values()));
    }
}
