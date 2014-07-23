import dbInterface.Workout;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

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
}
