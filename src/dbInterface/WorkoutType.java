package dbInterface;

import java.io.Serializable;

/**
 * Created by Dimitar on 8.4.14.
 */
// WorkoutType class
public class WorkoutType implements Serializable {
    private String name; //Running, Cycling, Walking... etc
    private String kind; //Outdoor or indoor
    // Constructor with two arguments, the WorkoutType name and WorkoutType kind
    public WorkoutType(String name, String kind) {
        this.name = name; // Setting the name
        this.kind = kind; // Setting the kind
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ");
        sb.append(name);
        sb.append("\n");
        sb.append("Kind: ");
        sb.append(kind);

        return sb.toString();
    }
}
