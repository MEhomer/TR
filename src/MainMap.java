import codeanticode.glgraphics.GLConstants;
import controlP5.*;
import dbInterface.Point;
import dbInterface.User;
import dbInterface.Workout;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;


/**
 * Created by dimitar on 17.7.14.
 */
public class MainMap extends PApplet {
    private UnfoldingMap map;
    private User displayUser;
    public ListBox userLB;
    public ListBox workoutsLB;
    public ControlP5 c5;

    public int fromHourValue;
    public int toHourValue;

    public boolean changed = true;

    //Tests
    Slider fromHour;
    Slider toHour;

    @Override
    public void setup() {
        size(1140, 500, GLConstants.GLGRAPHICS);

        map = new UnfoldingMap(this, 122.5f, 0f, 895f, 500f, new OpenStreetMap.OpenStreetMapProvider());
        MapUtils.createDefaultEventDispatcher(this, map);

        c5 = new ControlP5(this);
        userLB = createUserListBox();
        workoutsLB = createWorkouts(new ArrayList<Integer>(MapHelper.workouts_map.keySet()));

        fromHour = createFromSlider();
        fromHourValue = 0;
        toHour = createToSlider();
        toHourValue = 23;
    }

    @Override
    public void draw() {
        background(0);
        map.draw();

        info_box(workout_type, length, averageAlt);

        if (changed == true) {
            if (displayUser != null) {
                LinkedList<Integer> workoutIDS = new LinkedList<Integer>();
                Iterator<Workout> workoutWalker = displayUser.getWorkouts().iterator();
                while (workoutWalker.hasNext()) {
                    workoutIDS.add(workoutWalker.next().getId());
                }
                workoutsLB = createWorkouts(workoutIDS);
            } else {
                workoutsLB = createWorkouts(new ArrayList<Integer>(MapHelper.workouts_map.keySet()));
            }
            changed = false;
        }

        Location locationStart = map.getLocation(0, height);
        Location locationEnd = map.getLocation(width, 0);

        map.getMarkers().clear();

        /*LinkedList<LinkedList<Location>> linesWorkouts =*/
        linesWorkouts(map.getZoomLevel(), locationStart, locationEnd);
        //Iterator<LinkedList<Location>> locationsIterator = linesWorkouts.iterator();

        /*while(locationsIterator.hasNext()){
            LinesMarker m = new LinesMarker(locationsIterator.next());
            map.addMarker(m);
        }*/
    }

    public String workout_type = null;
    public double length = 0.0;
    public double averageAlt = 0.0;
    public double averageSpeed = 0.0;
    public int selectedID = 0;

    public void info_box(String workout_type, double length, double averageAlt){
        pushStyle();
        fill(color(0, 110, 235, 200));
        rect(130, 5, 203, 105, 5, 5, 5, 5);
        fill(color(0, 0, 0));
        DecimalFormat df = new DecimalFormat("0.00");
        text("Workout: ", 140, 20);
        if (workout_type != null)
            text(selectedID, 260, 20);
        text("Workout type:", 140, 40);
        if (workout_type != null)
            text(workout_type, 260, 40);
        text("Length: ", 140, 60);
        if (workout_type != null)
            text(Double.toString(Double.parseDouble(df.format(length))) + " km", 260, 60);
        text("Average altitude:", 140, 80);
        if (workout_type != null)
            text(Double.toString(Double.parseDouble(df.format(averageAlt))), 260, 80);
        text("Average speed:", 140, 100);
        if (workout_type != null)
            text(Double.toString(Double.parseDouble(df.format(averageSpeed))) + " km/h", 260, 100);

        popStyle();
    }

    public HashMap<Integer, Integer> zoomLevelMap = new HashMap<Integer, Integer>(){
        {
            put(7, 100);
            put(8, 90);
            put(9, 80);
            put(10, 70);
            put(11, 2);
        }
    };

    public void linesWorkouts(int level, Location start, Location end){
        LinkedList<LinkedList<Location>> linesWorkouts = new LinkedList<LinkedList<Location>>();

        if (level < 7) {
            return;
        }

        int jump = 0;
        if (zoomLevelMap.containsKey(level)){
            jump = zoomLevelMap.get(level);
        } else {
            jump = 2;
        }

        String zoomLevel = null;
        if (level > 11) {
            zoomLevel = "Level12";
        } else {
            zoomLevel = "Level" + level;
        }

        TreeSet<Integer> workoutIDS = KD_Tree_Maker.search(new double[]{start.getLat(), start.getLon()},
                new double[]{end.getLat(), end.getLon()}, zoomLevel);

        Iterator<Integer> walkerID = workoutIDS.iterator();

        LinkedList<LinesMarker> lMarkers = new LinkedList<LinesMarker>();
        LinesMarker selected = null;
        while (walkerID.hasNext()) {
            int ID = walkerID.next();
            Workout workoutTemp = MapHelper.workout_object_map.get(ID);
            Date date = new Date((long)workoutTemp.getPoints().get(0).getTs());
            int hour = date.getHours();
            if (hour < fromHourValue || hour > toHourValue){ continue; }
            LinkedList<Location> locations = new LinkedList<Location>();
            Iterator<Point> pointWalker = workoutTemp.getPoints().iterator();
            int jumpLevel = jump;
            while (pointWalker.hasNext()) {
                jumpLevel -= 1;
                if (jumpLevel == 0) {
                    Point point = pointWalker.next();
                    locations.add(new Location(point.getLat(), point.getLng()));
                    jumpLevel = jump;
                }
            }
            //LinesMarker m = new LinesMarker(locations, ID);
            LinesMarker m = new LinesMarker(locations, ID);
            if (workout_type != null){
                if (m.getID() == selectedID){
                    m.setSelected(true);
                    selected = m;
                    lMarkers.addLast(m);
                } else {
                    m.setSelected(false);
                    lMarkers.addFirst(m);
                }
            } else {
                m.setSelected(false);
                lMarkers.addFirst(m);
            }
            //linesWorkouts.add(locations);
        }

        Iterator<LinesMarker> walkerLM = lMarkers.iterator();
        while(walkerLM.hasNext()){
            map.addMarker(walkerLM.next());
        }

        if (selected != null) {
            map.addMarker(selected.startPoint);
            map.addMarker(selected.endPoint);
        }

        //return linesWorkouts;
    }

    public void controlEvent(ControlEvent theEvent) {
        // ListBox is if type ControlGroup.
        // 1 controlEvent will be executed, where the event
        // originates from a ControlGroup. therefore
        // you need to check the Event with
        // if (theEvent.isGroup())
        // to avoid an error message from controlP5.



        if(theEvent.isGroup() && theEvent.name().equals("usersList")) {
            int value = (int) theEvent.group().value();
            println("test " + value);

            if (value == -1){
                displayUser = null;
                KD_Tree_Maker.userTree = null;
                //workoutsLB = createWorkouts(new ArrayList<Integer>(MapHelper.workouts_map.keySet()));
                changed = true;
            } else {
                displayUser = DBHelper.getUserByID(MapHelper.users_map.get(value));
                /*LinkedList<Integer> workoutIDS = new LinkedList<Integer>();
                Iterator<Workout> workoutWalker = displayUser.getWorkouts().iterator();
                while(workoutWalker.hasNext()){
                    workoutIDS.add(workoutWalker.next().getId());
                }
                workoutsLB = createWorkouts(workoutIDS);*/
                KD_Tree_Maker.buildUserTree(displayUser);
                System.out.println("Done loading user!");
                changed = true;
            }
        } else if (theEvent.isGroup() && theEvent.getName().equals("workoutsList")){
            int value = (int) theEvent.getGroup().getValue();
            Workout tempWorkout = MapHelper.workout_object_map.get(value);
            double lat = tempWorkout.getPoints().get(0).getLat();
            double lng = tempWorkout.getPoints().get(0).getLng();

            workout_type = tempWorkout.getWorkoutType().getName();
            length = calculateLength(tempWorkout);
            averageAlt = averageAlt(tempWorkout);
            averageSpeed = fromMPSToKMPS(averageSpeed(tempWorkout));
            selectedID = value;

            map.zoomAndPanTo(new Location(lat, lng), 14);
        } else if (theEvent.getController().getName().equals("From")){
            int value = (int) theEvent.getController().getValue();
            if (value == fromHourValue){
                return;
            }
            fromHourValue = value;
            System.out.println(value);
            changed = true;
        } else if (theEvent.getController().getName().equals("To")){
            int value = (int) theEvent.getController().getValue();
            if (value == toHourValue){
                return;
            }
            toHourValue = value;
            System.out.println(value);
            changed = true;
        }
    }


    public Slider createFromSlider(){
        Slider tempSlider;
        tempSlider = c5.addSlider("From")
                .setPosition(135, height-20)
                .setWidth(300)
                .setRange(0,23) // values can range from big to small as well
                .setValue(0)
                .setNumberOfTickMarks(24)
                .setSliderMode(Slider.FLEXIBLE)
                .setColorBackground(color(0, 110, 235, 200))
                .setColorValueLabel(color(0, 0, 0))
                .setColorTickMark(color(0, 150, 190, 200))
        ;

        return tempSlider;
    }

    public Slider createToSlider(){
        Slider tempSlider;
        tempSlider = c5.addSlider("To")
                .setPosition(width-122-313, height-20)
                .setWidth(300)
                .setRange(0,23) // values can range from big to small as well
                .setValue(23)
                .setNumberOfTickMarks(24)
                .setSliderMode(Slider.FLEXIBLE)
                .setColorBackground(color(0, 110, 235, 200))
                .setColorValueLabel(color(0, 0, 0))
                .setColorTickMark(color(0, 150, 190, 200))
        ;

        return tempSlider;
    }

    public ListBox createUserListBox(){
        userLB = c5.addListBox("usersList")
                .setPosition(2, 20)
                .setSize(120, height - 20)
                .setItemHeight(15)
                .setBarHeight(15)
                .setColorBackground(color(0, 110, 235, 200))
                .setColorActive(color(0))
                .setColorForeground(color(0, 170, 235, 200));

        userLB.captionLabel().toUpperCase(true);
        userLB.captionLabel().set("Users");
        userLB.captionLabel().setColor(color(0, 0, 0));
        userLB.captionLabel().style().marginTop = 3;
        userLB.valueLabel().style().marginTop = 3;

        Iterator<Integer> uidWalker = MapHelper.users_map.keySet().iterator();

        ListBoxItem lbAll = userLB.addItem("All Users", -1);
        lbAll.setColorBackground(color(0, 150, 190, 200));
        lbAll.setColorLabel(color(20, 20, 20));
        while (uidWalker.hasNext()) {
            int id = uidWalker.next();
            ListBoxItem lbi = userLB.addItem("User id: "+id,(int)id);
            lbi.setColorBackground(color(0, 150, 190, 200));
            lbi.setColorLabel(color(20, 20, 20));
        }

        return userLB;
    }

    public ListBox createWorkouts(java.util.List<Integer> workouts){
        c5.remove("workoutsList");
        workoutsLB = c5.addListBox("workoutsList")
                .setPosition(width - 122, 20)
                .setSize(120, height - 20)
                .setItemHeight(15)
                .setBarHeight(15)
                .setColorBackground(color(0, 110, 235, 200))
                .setColorActive(color(0))
                .setColorForeground(color(0, 170, 235, 200));

        workoutsLB.captionLabel().toUpperCase(true);
        workoutsLB.captionLabel().set("Workouts");
        workoutsLB.captionLabel().setColor(color(0, 0, 0));
        workoutsLB.captionLabel().style().marginTop = 3;
        workoutsLB.valueLabel().style().marginTop = 3;

        Iterator<Integer> workoutWalker = workouts.iterator();

        while (workoutWalker.hasNext()) {
            int id = workoutWalker.next();
            Workout tempWorkout = MapHelper.workout_object_map.get(id);
            Date date = new Date((long)tempWorkout.getPoints().get(0).getTs());
            int hour = date.getHours();
            if (hour < fromHourValue || hour > toHourValue){ continue; }
            ListBoxItem lbi = workoutsLB.addItem("Workout id: "+id,(int)id);
            lbi.setColorBackground(color(0, 150, 190, 200));
            lbi.setColorLabel(color(20, 20, 20));
        }

        return workoutsLB;
    }

    public static void main(String [] args) {
        try {
            MapHelper.loadMaps();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            KD_Tree_Maker.init();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        DBHelper.startDB();
        PApplet.main(new String[]{"MainMap"});

       /* try {
            MapHelper.saveMaps();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

       /* Point A = new Point(40.267443f, 29.069824f, 0.0f, 0);
        Point B = new Point(40.198003f, 29.348602f, 0.0f, 0);
        System.out.println(distance(A, B));
        System.out.println((distance(B, A)*1000)/(2));*/
    }


    //Haversine formula for distance
    public static double distance(Point first, Point second){
        double R = 6371;
        double latR1 = Math.toRadians(first.getLat());
        double latR2 = Math.toRadians(second.getLat());
        double difLatR = Math.toRadians(second.getLat() - first.getLat());
        double difLngR = Math.toRadians(second.getLng() - first.getLng());

        double a = Math.sin(difLatR/2) * Math.sin(difLatR/2)
                 + Math.cos(latR1) * Math.cos(latR2)
                 * Math.sin(difLngR/2) * Math.sin(difLngR/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d = R * c;

        return d;
     }

    public double calculateLength(Workout w){
        Iterator<Point> walkerP = w.getPoints().iterator();

        double length = 0.0;
        Point start = null;
        Point end = null;
        if (walkerP.hasNext()){
            start = walkerP.next();
        }

        while(walkerP.hasNext()){
            end = walkerP.next();

            length += distance(start, end);
            start = end;
        }

        return length;
    }

    public double averageAlt(Workout w){
        double alt = 0.0;
        Iterator<Point> walkerP = w.getPoints().iterator();
        while(walkerP.hasNext()){
            alt += walkerP.next().getAlt();
        }

        return alt/w.getPoints().size();
    }

    public double fromMPSToKMPS(double mps){
        return (mps * 3600) / 1000;
    }
    public double averageSpeed(Workout w){
        if (w.getPoints().size() > 0){
            Point start = w.getPoints().get(0);
            Point end = w.getPoints().get(w.getPoints().size() - 1);
            double miliseconds = new Date((long) end.getTs()).getTime() - new Date((long) start.getTs()).getTime();
            double length = calculateLength(w);
            double averageSpeed = (length * 1000) / (miliseconds / 1000);
            return averageSpeed;
        }

        return 0.0f;
    }
}
