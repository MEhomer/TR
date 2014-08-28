import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import scala.collection.generic.BitOperations.Int;
import dbInterface.Point;

public class Parser {

    public static String userID = "userID";
    public static String workoutID = "workoutID";
    public static String workoutTYPE = "workoutTYPE";
    public static String workoutKIND = "workoutKIND";
    public static String points = "points";

    static int user_id = -1;
    static int workout_id = -1;
    static String workout_type = null;
    static String workout_kind = null;
    static ArrayList<Point> point_list = new ArrayList<Point>();

    public static void parseData(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(userID)) {
                    String[] tmp = line.split("=");
                    user_id = Integer.parseInt(tmp[1]);
                } else if (line.startsWith(workoutID)) {
                    String[] tmp = line.split("=");
                    workout_id = Integer.parseInt(tmp[1]);
                } else if (line.startsWith(workoutTYPE)) {
                    String[] tmp = line.split("=");
                    workout_type = tmp[1];
                } else if (line.startsWith(workoutKIND)) {
                    String[] tmp = line.split("=");
                    workout_kind = tmp[1];
                } else if (line.startsWith(points)) {
                    while ((line = br.readLine()) != null) {
                        String tmp[] = line.split(" ");
                        Point p = new Point(Double.parseDouble(tmp[0]),
                                Double.parseDouble(tmp[1]),
                                Double.parseDouble(tmp[2]),
                                Long.parseLong(tmp[3]));
                        point_list.add(p);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(userID + ": " + user_id);
        System.out.println(workoutID + ": " + workout_id);
        System.out.println(workoutTYPE + ": " + workout_type);
        System.out.println(workoutKIND + ": " + workout_kind);
        System.out.println(points + ": " + point_list);

        if (workout_id != -1 && workout_kind != null && workout_type != null
                && !point_list.isEmpty()) {
            DBHelper.insertIntoDB(user_id, workout_id, workout_type, workout_kind, point_list);
        }

    }

    public static void main(String[] args) {
        parseData(new File("C:\\Users\\Aleksandar\\Desktop\\json.txt"));
    }
}
