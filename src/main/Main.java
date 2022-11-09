package main;
import org.json.*;
import com.google.gson.*;

import java.awt.*;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader

            Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/terminal_4_3.json"), StandardCharsets.UTF_8);

            // convert JSON file to map
            Map<?, ?> map = gson.fromJson(reader, Map.class);
            List<Container> containers = new ArrayList<>();
            List<Slot> slots = new ArrayList<>();
            List<Assignment> assignments = new ArrayList<>();
            for(Map<String, Double> containerMap : (List<Map>)map.get("containers")){
                containers.add(new Container(containerMap.get("id").intValue(), containerMap.get("length").intValue()));
            }

            for(Map<String, Double> slotMap : (List<Map>)map.get("slots")){
                slots.add(new Slot(slotMap.get("id").intValue(), new Point(slotMap.get("x").intValue(), slotMap.get("y").intValue())));
            }

            for (Map<String, ?> assignmentMap : (List<Map>)map.get("assignments")) {
                List<Slot> assignmentSlots = new ArrayList<>();
                for(double slotId : (List<Double>)assignmentMap.get("slot_id")){
                    assignmentSlots.add(slots.stream().filter(slot -> slot.getId() == (int)slotId).collect(Collectors.toList()).get(0));
                }
                Container assignmentContainer = containers.stream().filter(container -> container.getId() == ((Double)assignmentMap.get("container_id")).intValue()).collect(Collectors.toList()).get(0);
                assignments.add(new Assignment(assignmentContainer, assignmentSlots));
            }
            Collections.sort(assignments);

            // close reader
            reader.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}