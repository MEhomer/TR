import codeanticode.glgraphics.GLConstants;
import controlP5.*;
import dbInterface.Point;
import dbInterface.User;
import dbInterface.Workout;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

import java.io.IOException;
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
        size(900, 500, GLConstants.GLGRAPHICS);

        map = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());
        MapUtils.createDefaultEventDispatcher(this, map);

        if (P3D == OPENGL) println("I am run on Processing 2.0");
        else println("I am run on Processing < 2.0");

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
        background(255);
        map.draw();

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

        LinkedList<LinkedList<Location>> linesWorkouts = linesWorkouts(map.getZoomLevel(), locationStart, locationEnd);
        Iterator<LinkedList<Location>> locationsIterator = linesWorkouts.iterator();

        while(locationsIterator.hasNext()){
            map.addMarker(new SimpleLinesMarker(locationsIterator.next()));
        }
    }

    public LinkedList<LinkedList<Location>> linesWorkouts(int level, Location start, Location end){
        LinkedList<LinkedList<Location>> linesWorkouts = new LinkedList<LinkedList<Location>>();

        if (level < 7) {
            return linesWorkouts;
        }

        int jump = 0;
        if (level == 7) {
            jump = 100;
        } else if (level == 8) {
            jump = 40;
        } else if (level == 9) {
            jump = 30;
        } else if (level == 10) {
            jump = 22;
        } else if (level == 11) {
            jump = 18;
        } else {
            jump = 14;
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
            linesWorkouts.add(locations);
        }


        return linesWorkouts;
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
    }
}
