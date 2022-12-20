package main;

import com.google.gson.Gson;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class JsonReader {
    public static Terminal readInitialTerminal(String filePath){
        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8);

            // convert JSON file to map
            Map<?, ?> map = gson.fromJson(reader, Map.class);

            List<Container> containers = new ArrayList<>();
            Slot[][] slotGrid = new Slot[((Double)map.get("length")).intValue()][((Double)map.get("width")).intValue()];
            Map<Integer, Point> slotLocations = new HashMap<>();
            List<Assignment> assignments = new ArrayList<>();
            for(Map<String, Double> containerMap : (List<Map>)map.get("containers")){
                containers.add(new Container(containerMap.get("id").intValue(), containerMap.get("length").intValue()));
            }

            for(Map<String, Double> slotMap : (List<Map>)map.get("slots")){
                int slotId = slotMap.get("id").intValue();
                int slotX = slotMap.get("x").intValue();
                int slotY = slotMap.get("y").intValue();
                Point location = new Point(slotX, slotY);
                slotGrid[slotX][slotY] = new Slot(slotId, location);
                slotLocations.put(slotId, location);
            }

            for (Map<String, ?> assignmentMap : (List<Map>)map.get("assignments")) {
                Container assignmentContainer = containers.stream().filter(container -> container.getId() == ((Double)assignmentMap.get("container_id")).intValue()).collect(Collectors.toList()).get(0);
                Slot[] assignmentSlots = new Slot[assignmentContainer.getLength()];
                int leftMostSlotId = ((Double)assignmentMap.get("slot_id")).intValue();
                Point leftMostSlotLocation = slotLocations.get(leftMostSlotId);
                assignmentSlots[0] = slotGrid[(int) leftMostSlotLocation.getX()][(int) leftMostSlotLocation.getY()];
                for(int i = 1; i < assignmentContainer.getLength(); i++){
                    assignmentSlots[i] = slotGrid[(int) leftMostSlotLocation.getX()+i][(int) leftMostSlotLocation.getY()];
                }
                assignmentContainer.setSlots(assignmentSlots);
                assignments.add(new Assignment(assignmentContainer, assignmentSlots));
            }

            List<Crane> cranes = new ArrayList<>();
            for (Map<String, Double> craneMap: (List<Map>)map.get("cranes")){
                cranes.add(new Crane(((Double)craneMap.get("id")).intValue(), new Point(craneMap.get("x").doubleValue(), craneMap.get("y").doubleValue()), craneMap.get("xspeed").doubleValue(), craneMap.get("yspeed").doubleValue(), craneMap.get("xmin").doubleValue(), craneMap.get("xmax").doubleValue(), craneMap.get("ymin").doubleValue(), craneMap.get("ymax").doubleValue()));
            }
            int maxHeight = ((Double)map.get("maxheight")).intValue();
            int targetHeight = maxHeight;
            if(map.containsKey("targetheight")){
                targetHeight = ((Double)map.get("targetheight")).intValue();
            }
            Terminal terminal = new Terminal(map.get("name").toString(), containers, slotLocations, slotGrid, assignments.toArray(new Assignment[0]), cranes, maxHeight, targetHeight, ((Double)map.get("width")).intValue(), ((Double)map.get("length")).intValue());
            for(Container container : terminal.getContainers()){
                terminal.initializeSlots(container, container.getSlots());
            }
            reader.close();
            return terminal;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Assignment[] readAssignments(String filePath, Terminal initialTerminal){
        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8);

            // convert JSON file to map
            Map<?, ?> map = gson.fromJson(reader, Map.class);

            List<Assignment> assignments = new ArrayList<>();

            for (Map<String, ?> assignmentMap : (List<Map>)map.get("assignments")) {
                Container assignmentContainer = initialTerminal.getContainers().stream().filter(container -> container.getId() == ((Double)assignmentMap.get("container_id")).intValue()).collect(Collectors.toList()).get(0);
                Slot[] assignmentSlots = new Slot[assignmentContainer.getLength()];
                int leftMostSlotId = ((Double)assignmentMap.get("slot_id")).intValue();
                Point leftMostSlotLocation = initialTerminal.getSlotLocations().get(leftMostSlotId);
                assignmentSlots[0] = initialTerminal.getSlotGrid()[(int) leftMostSlotLocation.getX()][(int) leftMostSlotLocation.getY()];
                for(int i = 1; i < assignmentContainer.getLength(); i++){
                    assignmentSlots[i] = initialTerminal.getSlotGrid()[(int) leftMostSlotLocation.getX()+i][(int) leftMostSlotLocation.getY()];
                }
                assignments.add(new Assignment(assignmentContainer, assignmentSlots));
            }
            reader.close();
            return assignments.toArray(new Assignment[0]);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

//    public static Target readTargetAssignments(String filePath, Terminal targetTerminal){
//        try {
//            // create Gson instance
//            Gson gson = new Gson();
//
//            // create a reader
//            Reader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8);
//
//            // convert JSON file to map
//            Map<?, ?> map = gson.fromJson(reader, Map.class);
//
//            List<Assignment> assignments = new ArrayList<>();
//
////            Terminal targetTerminal = terminals.stream().filter(terminal -> terminal.getName().equals(map.get("name").toString())).collect(Collectors.toList()).get(0);
//
//            for (Map<String, ?> assignmentMap : (List<Map>)map.get("assignments")) {
//                Container assignmentContainer = targetTerminal.getContainers().stream().filter(container -> container.getId() == ((Double)assignmentMap.get("container_id")).intValue()).collect(Collectors.toList()).get(0);
//                Slot[] assignmentSlots = new Slot[assignmentContainer.getLength()];
//                int leftMostSlotId = ((Double)assignmentMap.get("slot_id")).intValue();
//                Slot leftMostSlot = Arrays.asList(targetTerminal.getSlots()).stream().filter(slot -> slot.getId() == (leftMostSlotId)).collect(Collectors.toList()).get(0);
//                for(int i = 1; i < assignmentContainer.getLength(); i++){
//                    Point nextSlotLocation = new Point(leftMostSlot.getLocation().getX()+i, leftMostSlot.getLocation().getY());
//                    assignmentSlots[i] = Arrays.asList(targetTerminal.getSlots()).stream().filter(slot -> slot.getLocation() == nextSlotLocation).collect(Collectors.toList()).get(0);
//                }
//                assignments.add(new Assignment(assignmentContainer, assignmentSlots));
//            }
//            reader.close();
//            int maxHeight = ((Double)map.get("maxheight")).intValue();
//            int targetHeight = maxHeight;
//            if(map.containsKey("targetheight")){
//                targetHeight = ((Double)map.get("maxheight")).intValue();
//            }
//
//            return new Target(targetTerminal, maxHeight, targetHeight, assignments);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }
}
