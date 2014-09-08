import dbInterface.Point;
import dbInterface.User;
import dbInterface.Users;
import dbInterface.Workout;
import net.sf.javaml.core.kdtree.KDTree;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by dimitar on 17.7.14.
 */
public class KD_Tree_Maker {
    public static HashMap<String, KDTree> treeMap;
    //public static HashMap<Integer, Workout> workoutMap;

    public static String TREE_MAP = "tree_map.m";
    public static String WORKOUT_MAP = "workout_map.m";

    public static void init() throws IOException, ClassNotFoundException {
        treeMap = new HashMap<String, KDTree>();
        //workoutMap = new HashMap<Integer, Workout>();

        for (int i = 0; i<7; i++){
            treeMap.put("Level" + (i+7), new KDTree(2));
        }

        Iterator<Integer> walkerWorkout = MapHelper.workout_object_map.keySet().iterator();
        int countW = 0;
        while(walkerWorkout.hasNext()){
            int workoutID = walkerWorkout.next();
            System.out.println(countW++ + " " + MapHelper.workouts_map.size());
            Workout tempWorkout = MapHelper.workout_object_map.get(workoutID);
            Iterator<Point> walkerPoint = tempWorkout.getPoints().iterator();
            //workoutMap.put(tempWorkout.getId(), tempWorkout);

            int count = 1;
            while(walkerPoint.hasNext()){
                Point tempPoint = walkerPoint.next();
                double lat = tempPoint.getLat();
                double lng = tempPoint.getLng();
                if (count % 14 == 0){
                    treeMap.get("Level12").insert(new double[]{lat, lng}, tempWorkout.getId());
                }
                if (count % 18 == 0){
                    treeMap.get("Level11").insert(new double[]{lat, lng}, tempWorkout.getId());
                }
                if (count % 22 == 0){
                    treeMap.get("Level10").insert(new double[]{lat, lng}, tempWorkout.getId());
                }
                if (count % 30 == 0){
                    treeMap.get("Level9").insert(new double[]{lat, lng}, tempWorkout.getId());
                }
                if (count % 40 == 0){
                    treeMap.get("Level8").insert(new double[]{lat, lng}, tempWorkout.getId());
                }
                if (count % 100 == 0){
                    treeMap.get("Level7").insert(new double[]{lat, lng}, tempWorkout.getId());
                }
                count++;
            }
        }
    }

    public static HashMap<String, KDTree> userTree;

    public static void buildUserTree(User user){
        userTree = new HashMap<String, KDTree>();
        //workoutMap = new HashMap<Integer, Workout>();

        for (int i = 0; i<7; i++){
            userTree.put("Level" + (i+7), new KDTree(2));
        }

        Iterator<Workout> walkerWorkout = user.getWorkouts().iterator();
        int countW = 0;
        while(walkerWorkout.hasNext()){
            Workout tempWorkout = walkerWorkout.next();
            int workoutID = tempWorkout.getId();
            System.out.println(countW++ + " " + MapHelper.workouts_map.size());
            Iterator<Point> walkerPoint = tempWorkout.getPoints().iterator();
            //workoutMap.put(tempWorkout.getId(), tempWorkout);

            int count = 1;
            while(walkerPoint.hasNext()){
                Point tempPoint = walkerPoint.next();
                double lat = tempPoint.getLat();
                double lng = tempPoint.getLng();
                if (count % 14 == 0){
                    userTree.get("Level12").insert(new double[]{lat, lng}, tempWorkout.getId());
                }
                if (count % 18 == 0){
                    userTree.get("Level11").insert(new double[]{lat, lng}, tempWorkout.getId());
                }
                if (count % 22 == 0){
                    userTree.get("Level10").insert(new double[]{lat, lng}, tempWorkout.getId());
                }
                if (count % 30 == 0){
                    userTree.get("Level9").insert(new double[]{lat, lng}, tempWorkout.getId());
                }
                if (count % 40 == 0){
                    userTree.get("Level8").insert(new double[]{lat, lng}, tempWorkout.getId());
                }
                if (count % 100 == 0){
                    userTree.get("Level7").insert(new double[]{lat, lng}, tempWorkout.getId());
                }
                count++;
            }
        }
    }

    public static TreeSet<Integer> search(double [] lowb, double [] upb, String zoomLevel){
        if (userTree == null) {
            Object[] workouts = treeMap.get(zoomLevel).range(lowb, upb);
            TreeSet<Integer> workoutsU = new TreeSet<Integer>();
            for (int i = 0; i < workouts.length; i++) {
                workoutsU.add((Integer) workouts[i]);
            }

            return workoutsU;
        } else {
            Object[] workouts = userTree.get(zoomLevel).range(lowb, upb);
            TreeSet<Integer> workoutsU = new TreeSet<Integer>();
            for (int i = 0; i < workouts.length; i++) {
                workoutsU.add((Integer) workouts[i]);
            }

            return workoutsU;
        }

    }

    public static void main(String [] args) throws IOException, ClassNotFoundException {
        DBHelper.startDB();
        MapHelper.loadMaps();
        init();
        DBHelper.stopDB();

        double [] searchStart = new double[]{43.984486, -121.67303};
        double [] searchEnd = new double[]{43.9867, -121.66923};

        Iterator<String> walker = treeMap.keySet().iterator();
        while(walker.hasNext()){
            String key = walker.next();
            System.out.println(key);
            Object [] ids = treeMap.get(key).range(searchStart, searchEnd);
            for (int i = 0; i<ids.length; i++){
                System.out.println((Integer) ids[i]);
            }
        }


    }
}
