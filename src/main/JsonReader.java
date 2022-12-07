package main;

import com.google.gson.Gson;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonReader {
    public static Terminal readTerminal(String filePath){
        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader

            Reader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8);

            // convert JSON file to map
            Map<?, ?> map = gson.fromJson(reader, Map.class);

            List<Container> containers = new ArrayList<>();
            List<Slot> slots = new ArrayList<>();
//            List<Assignment> assignments = new ArrayList<>();
            for(Map<String, Double> containerMap : (List<Map>)map.get("containers")){
                containers.add(new Container(containerMap.get("id").intValue(), containerMap.get("length").intValue()));
            }

            for(Map<String, Double> slotMap : (List<Map>)map.get("slots")){
                slots.add(new Slot(slotMap.get("id").intValue(), new Point(slotMap.get("x").intValue(), slotMap.get("y").intValue())));
            }

            for (Map<String, ?> assignmentMap : (List<Map>)map.get("assignments")) {
                Container assignmentContainer = containers.stream().filter(container -> container.getId() == ((Double)assignmentMap.get("container_id")).intValue()).collect(Collectors.toList()).get(0);
                Slot[] assignmentSlots = new Slot[assignmentContainer.getLength()];
                int leftMostSlotId = ((Double)assignmentMap.get("slot_id")).intValue();
                Slot leftMostSlot = slots.stream().filter(slot -> slot.getId() == (leftMostSlotId)).collect(Collectors.toList()).get(0);
                assignmentSlots[0] = leftMostSlot;
                for(int i = 1; i < assignmentContainer.getLength(); i++){
                    Point nextSlotLocation = new Point(leftMostSlot.getLocation().getX()+i, leftMostSlot.getLocation().getY());
                    assignmentSlots[i] = slots.stream().filter(slot -> slot.getLocation().equals(nextSlotLocation)).collect(Collectors.toList()).get(0);
                }
                assignmentContainer.setSlots(assignmentSlots);
//                assignments.add(new Assignment(assignmentContainer, assignmentSlots));
            }

            for (Map<String, Double> craneMap: (List<Map>)map.get("cranes")){
                List<Crane> cranes = new ArrayList<>();
                cranes.add(new Crane(new Point(craneMap.get("x").doubleValue(), craneMap.get("y").doubleValue()), craneMap.get("xspeed").doubleValue(), craneMap.get("yspeed").doubleValue(), craneMap.get("xmin").doubleValue(), craneMap.get("xmax").doubleValue(), craneMap.get("ymin").doubleValue(), craneMap.get("ymax").doubleValue()));
            }
            Terminal terminal = new Terminal(map.get("name").toString(), containers, slots.toArray(new Slot[0]), new ArrayList<>(), ((Double)map.get("maxheight")).intValue(), ((Double)map.get("width")).intValue(), ((Double)map.get("length")).intValue());
            for(Container container : terminal.getContainers()){
                terminal.putContainerInSlots(container, container.getSlots());
            }
            reader.close();
            return terminal;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
