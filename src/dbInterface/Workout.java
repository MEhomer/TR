package dbInterface;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Dimitar on 13.2.14.
 */
//Workout class
public class Workout implements Serializable {
    private int id; // This is workout id
    private List<Point> points; // This is List of points of the workout
    private WorkoutType workoutType; // This is the workout type and kind
    // Constructor with 4 parameters, the workout id, list of points, workoutTypeName and workoutTypeKind
    public Workout(int id, List<Point> points, String workoutTypeName, String workoutTypeKind){
        this.id = id; // Setting the workout id

        this.workoutType = new WorkoutType(workoutTypeName, workoutTypeKind); // Setting the workout type

        this.points = new LinkedList<Point>(); // Setting the workout list of points

        Iterator<Point> walker = points.iterator();

        while(walker.hasNext()){
            this.points.add(walker.next());
        }
    }

    public int getId() {
        return id;
    }

    public List<Point> getPoints() {
        return points;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        } else if (obj.getClass() != getClass()){
            return false;
        } else {
            Workout other = (Workout) obj;
            if (id == other.id){
                return true;
            }

            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("WorkoutID: " + id);
        sb.append("\n");
        sb.append(workoutType);
        sb.append("\n");

        Iterator<Point> walker = points.iterator();

        while(walker.hasNext()){
            sb.append(walker.next());
        }

        return sb.toString();
    }
}
