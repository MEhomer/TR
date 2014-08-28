import dbInterface.*;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dimitar on 16.7.14.
 */
public class DBHelper {
    public static Logger log = Logger.getGlobal();
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

    public static void insertIntoDB(int user_id, int workout_id,
                                    String workout_type, String workout_kind,
                                    ArrayList<Point> point_list) {

        try {
            MapHelper.loadMapsRest();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Transaction tx = graphDB.beginTx();

        try {
            Node workoutType_node = null;

            if (MapHelper.workout_types_map.containsKey(workout_type)) {
                workoutType_node = graphDB.getNodeById(MapHelper.workout_types_map
                        .get(workout_type));
            } else {
                workoutType_node = graphDB.createNode();
                workoutType_node.setProperty("label", "workout_type");
                workoutType_node.setProperty("name", workout_type);
                workoutType_node.setProperty("kind", workout_kind);
                MapHelper.workout_types_map.put(workout_type, workoutType_node.getId());
            }

            Node user_node = null;
            if (MapHelper.users_map.containsKey(user_id)) {
                user_node = graphDB.getNodeById(MapHelper.users_map.get(user_id));
            } else {
                user_node = graphDB.createNode();
                user_node.setProperty("label", "user");
                user_node.setProperty("id", user_id);
                MapHelper.users_map.put(user_id, user_node.getId());
            }

            Node workout_node = null;

            if (MapHelper.workouts_map.containsKey(workout_id)) {
                // return custom error
            } else {
                workout_node = graphDB.createNode();
                workout_node.setProperty("label", "workout");
                workout_node.setProperty("id", workout_id);
                MapHelper.workouts_map.put(workout_id, workout_node.getId());

                user_node.createRelationshipTo(workout_node,
                        RelationType.USER_TO_WORKOUT);
                workoutType_node.createRelationshipTo(workout_node,
                        RelationType.WORKOUTTYPE_TO_WORKOUT);

                int count = 0;

                Node x = null;

                if (!point_list.isEmpty()) {
                    Point point = point_list.get(0);
                    x = graphDB.createNode();
                    MapHelper.points_set.add(x.getId());
                    x.setProperty("label", "point");
                    x.setProperty("start", true);
                    x.setProperty("latitude", point.getLat());
                    x.setProperty("longitude", point.getLng());
                    x.setProperty("altitude", point.getAlt());
                    x.setProperty("timestamp", point.getTs());
                    workout_node.createRelationshipTo(x,
                            RelationType.WORKOUT_TO_POINT);

                    for (int i = 1; i < point_list.size(); i++) {
                        point = point_list.get(i);

                        Node p = graphDB.createNode();

                        p.setProperty("label", "point");
                        p.setProperty("latitude", point.getLat());
                        p.setProperty("longitude", point.getLng());
                        p.setProperty("altitude", point.getAlt());
                        p.setProperty("timestamp", point.getTs());

                        x.createRelationshipTo(p, RelationType.POINT_TO_POINT);

                        workout_node.createRelationshipTo(p,
                                RelationType.WORKOUT_TO_POINT);

                        MapHelper.points_set.add(p.getId());
                        count++;

                        if (count > 0 && count % 1000 == 0) {
                            tx.success();
                            tx.finish();
                            tx = graphDB.beginTx();
                        }

                        x = p;
                    }
                }

                count++;

                if (count > 0 && count % 10000 == 0) {
                    tx.success();
                    tx.finish();
                    tx = graphDB.beginTx();
                }

            }

            tx.success();
        } finally {
            tx.finish();
        }

        MapHelper.workout_object_map.put(workout_id, new Workout(workout_id, point_list, workout_type, workout_kind));
        log.log(Level.INFO, "New data inserted into db");
    }
}
