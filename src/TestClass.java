import dbInterface.Point;
import dbInterface.Workout;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by dimitar on 16.7.14.
 */
public class TestClass {
    public static EmbeddedGraphDatabase graphDB;
    public static final String DB_PATH = "/home/dimitar/IdeaProjects/TeamWork/TR/DB";
    public static final String USERS_MAP_PATH = "/home/dimitar/IdeaProjects/TeamWork/TR/Maps/users.m";
    public static final String WORKOUTS_MAP_PATH = "/home/dimitar/IdeaProjects/TeamWork/TR/Maps/workouts.m";
    public static final String WORKOUT_TYPES_MAP_PATH = "/home/dimitar/IdeaProjects/TeamWork/TR/Maps/workout_types.m";
    public static final String POINTS_MAP_PATH = "/home/dimitar/IdeaProjects/TeamWork/TR/Maps/points_map.m";
    public static final String POINTS_SET_PATH = "/home/dimitar/IdeaProjects/TeamWork/TR/Maps/points_set.m";
    public static final String WORKOUT_OBJECT_MAP_PATH = "/home/dimitar/IdeaProjects/TeamWork/TR/Maps/workout_object.m";

    public static HashMap<Integer, Long> users_map;
    public static HashMap<Integer, Long> workouts_map;
    public static HashMap<String, Long> workout_types_map;
    public static HashMap<Integer, Long> points_map;

    public static void main(String [] args) throws IOException, ClassNotFoundException {
        /*loadMaps();
        DBHelper.startDB();
        buildWorkoutMap();
        DBHelper.stopDB();*/
        MapHelper.loadMaps();
        Iterator<Integer> walkerIDS = MapHelper.workout_object_map.keySet().iterator();
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        while(walkerIDS.hasNext()){
            Workout workout = MapHelper.workout_object_map.get(walkerIDS.next());
            Iterator<Point> walkerP = workout.getPoints().iterator();
            while(walkerP.hasNext()){
                Point p = walkerP.next();
                if (p.getAlt() > max){
                    max = p.getAlt();
                }
                if (p.getAlt() < min){
                    min = p.getAlt();
                }
            }
        }

        System.out.println(max);
        System.out.println(min);
    }

    public static void startDB(){
        graphDB = (EmbeddedGraphDatabase) new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
    }

    public static void stopDB(){
        graphDB.shutdown();
    }

    public static void loadMaps() throws IOException, ClassNotFoundException {
        ObjectInputStream objReader = new ObjectInputStream(new FileInputStream(USERS_MAP_PATH));
        users_map = (HashMap<Integer, Long>) objReader.readObject();
        objReader = new ObjectInputStream(new FileInputStream(WORKOUTS_MAP_PATH));
        workouts_map = (HashMap<Integer, Long>) objReader.readObject();
    }

    public static void buildWorkoutMap() throws IOException {
        HashMap<Integer, Workout> o_workout_map = new HashMap<Integer, Workout>();

        Iterator<Integer> id_walker = workouts_map.keySet().iterator();

        while(id_walker.hasNext()){
            int id = id_walker.next();
            System.out.println(id);
            Workout workoutTemp = DBHelper.getWorkoutByID(workouts_map.get(id));
            o_workout_map.put(id, workoutTemp);
        }

        System.out.println("Serializing map");
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(WORKOUT_OBJECT_MAP_PATH));
        out.writeObject(o_workout_map);
        out.close();
        System.out.println("Done");
    }
}
