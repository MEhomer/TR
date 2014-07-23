package dbInterface;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Dimitar on 13.2.14.
 */
// User class
public class User implements Serializable {
    private int id; // This is user ID
    private List<Workout> workouts; // This is a List of Users Workouts
    // Constructor with only one argument, and that is the user ID
    public User(int id){
        this.id = id; // Setting the user ID
        this.workouts = new LinkedList<Workout>(); // Initializing the workouts list
    }
    // Method used for adding workouts to the workouts List
    public void addWorkout(Workout workout){
        this.workouts.add(workout); // Adding workout to the workouts List
    }

    public int getId() {
        return id;
    } // Returning the user ID

    public List<Workout> getWorkouts() {
        return workouts;
    } // Returning the List

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        } else if (obj.getClass() != getClass()){
            return false;
        } else {
            User other = (User) obj;
            if (id == other.id){
                return true;
            }

            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("UserID: " + id);
        sb.append("\n");
        sb.append("Workouts:");
        sb.append("\n");
        Iterator<Workout> walker = workouts.iterator();

        while(walker.hasNext()){
            sb.append(walker.next());
        }

        return sb.toString();
    }
}
