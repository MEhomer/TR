import dbInterface.Workout;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by dimitar on 16.7.14.
 */
public class MapHelper {
    public static final String USERS_MAP_PATH = "/home/dimitar/IdeaProjects/TeamWork/TR/Maps/users.m";
    public static final String WORKOUTS_MAP_PATH = "/home/dimitar/IdeaProjects/TeamWork/TR/Maps/workouts.m";
    public static final String WORKOUT_OBJECT_MAP_PATH = "/home/dimitar/IdeaProjects/TeamWork/TR/Maps/workout_object.m";
    public static final String WORKOUT_TYPES_MAP_PATH = "/home/dimitar/IdeaProjects/TeamWork/TR/Maps/workout_types.m";
    public static final String POINTS_MAP_PATH = "/home/dimitar/IdeaProjects/TeamWork/TR/Maps/points_map.m";
    public static final String POINTS_SET_PATH = "/home/dimitar/IdeaProjects/TeamWork/TR/Maps/points_set.m";

    public static HashMap<Integer, Long> users_map;
    public static HashMap<Integer, Long> workouts_map;
    public static HashMap<Integer, Workout> workout_object_map;
    public static HashMap<String, Long> workout_types_map;
    public static HashMap<Integer, Long> points_map;
    public static HashSet<Long> points_set;

    public static void loadMaps() throws IOException, ClassNotFoundException {
        ObjectInputStream objReader = new ObjectInputStream(new FileInputStream(USERS_MAP_PATH));
        users_map = (HashMap<Integer, Long>) objReader.readObject();
        objReader.close();

        objReader = new ObjectInputStream(new FileInputStream(WORKOUTS_MAP_PATH));
        workouts_map = (HashMap<Integer, Long>) objReader.readObject();
        objReader.close();

        objReader = new ObjectInputStream(new FileInputStream(WORKOUT_OBJECT_MAP_PATH));
        workout_object_map = (HashMap<Integer, Workout>) objReader.readObject();
        objReader.close();
    }

    public static void loadMapsRest() throws IOException, ClassNotFoundException {
        ObjectInputStream objReader;

        objReader = new ObjectInputStream(new FileInputStream(WORKOUT_TYPES_MAP_PATH));
        workout_types_map = (HashMap<String, Long>) objReader.readObject();
        objReader.close();

        objReader = new ObjectInputStream(new FileInputStream(POINTS_SET_PATH));
        points_set = (HashSet<Long>) objReader.readObject();
        objReader.close();
    }

    public static void saveMapsRest() throws IOException {
        ObjectOutputStream objWriter;
        objWriter = new ObjectOutputStream(new FileOutputStream(WORKOUT_TYPES_MAP_PATH));
        objWriter.writeObject(workout_types_map);
        objWriter.close();
        workout_types_map = null;

        objWriter = new ObjectOutputStream(new FileOutputStream(POINTS_SET_PATH));
        objWriter.writeObject(points_set);
        objWriter.close();
        points_set = null;
    }

    public static void saveMaps() throws IOException {
        ObjectOutputStream objWriter;

        objWriter = new ObjectOutputStream(new FileOutputStream(USERS_MAP_PATH));
        objWriter.writeObject(users_map);
        objWriter.close();

        objWriter = new ObjectOutputStream(new FileOutputStream(WORKOUTS_MAP_PATH));
        objWriter.writeObject(workouts_map);
        objWriter.close();

        objWriter = new ObjectOutputStream(new FileOutputStream(WORKOUT_OBJECT_MAP_PATH));
        objWriter.writeObject(workout_object_map);
        objWriter.close();
    }
}
