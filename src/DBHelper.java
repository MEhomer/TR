import dbInterface.*;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by dimitar on 16.7.14.
 */
public class DBHelper {
    public static EmbeddedGraphDatabase graphDB;
    public static final String DB_PATH = "/home/dimitar/IdeaProjects/TeamWork/TR/DB";

    public static void startDB(){
        graphDB = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
    }

    public static void stopDB(){
        graphDB.shutdown();
    }

    public static Point getPointByID(long id){
        Node pointNode = graphDB.getNodeById(id);
        double alt = (Double) pointNode.getProperty("altitude");
        double lng = (Double) pointNode.getProperty("longitude");
        double lat = (Double) pointNode.getProperty("latitude");
        Double tsD = (Double) pointNode.getProperty("timestamp");

        return new Point(lat, lng, alt, tsD.longValue());
    }

    public static WorkoutType getWorkoutTypeByID(long id){
        Node wtNode = graphDB.getNodeById(id);

        String name = (String) wtNode.getProperty("name");
        String kind = (String) wtNode.getProperty("kind");

        return new WorkoutType(name, kind);
    }

    public static Workout getWorkoutByID(long id){
        Node workoutNode = graphDB.getNodeById(id);
        int workoutID = (Integer) workoutNode.getProperty("id");

        Iterator<Relationship> relWalker = workoutNode.getRelationships(RelationType.WORKOUT_TO_POINT).iterator();

        LinkedList<Point> lPoints = new LinkedList<Point>();
        while(relWalker.hasNext()){
            Relationship rel = relWalker.next();
            Node pointNode = rel.getEndNode();
            Point tempPoint = getPointByID(pointNode.getId());
            lPoints.addFirst(tempPoint);
        }

        WorkoutType wt = getWorkoutTypeByID(workoutNode.getSingleRelationship(RelationType.WORKOUTTYPE_TO_WORKOUT, Direction.BOTH).getStartNode().getId());

        return new Workout(workoutID, lPoints, wt.getName(), wt.getKind());
    }

    public static User getUserByID(long id){
        Node userNode = graphDB.getNodeById(id);
        int userID = (Integer) userNode.getProperty("id");

        User tmpUser = new User(userID);

        Iterator<Relationship> relWalker = userNode.getRelationships(RelationType.USER_TO_WORKOUT).iterator();
        while(relWalker.hasNext()){
            Relationship rel = relWalker.next();
            tmpUser.addWorkout(getWorkoutByID(rel.getEndNode().getId()));
        }

        return tmpUser;
    }
}
