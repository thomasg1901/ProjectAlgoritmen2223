package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length == 2){
            // Max height conversion

            String inputFile = args[0];
            String outputFile = args[1];

            Terminal terminalToConvert = JsonReader.readInitialTerminal("./src/main/resources/instances/2mh/MH2Terminal_20_10_3_2_100.json");
            Target terminalConversionExample = new Target(terminalToConvert);


        } else if (args.length == 3) {
            // Target conversion

            String initialStateFile = args[0];
            String targetStateFile = args[1];
            String outputFile = args[2];

            Terminal terminal = JsonReader.readInitialTerminal(initialStateFile);
            Assignment[] targetAssignments = JsonReader.readAssignments(targetStateFile, terminal);
            Target fixedTranshipmentsExample = new Target(terminal, targetAssignments);
            writeTerminalToOutput(terminal, args[2]);
        }

        Terminal terminal = JsonReader.readInitialTerminal("./src/main/resources/instances/5t/TerminalB_20_10_3_2_160.json");
        Assignment[] targetAss = JsonReader.readAssignments("./src/main/resources/instances/5t/targetTerminalB_20_10_3_2_160.json", terminal);
        Terminal terminalToConvert = JsonReader.readInitialTerminal("./src/main/resources/instances/2mh/MH2Terminal_20_10_3_2_100.json");
        Target fixedTranshipmentsExample = new Target(terminal, targetAss);
        Target terminalConversionExample = new Target(terminalToConvert);


        writeTerminalToOutput(terminalToConvert, "./src/main/resources/test2.csv");
    }


    private static void writeTerminalToOutput(Terminal terminal, String outputFile){
        String result = craneMovementsToCSV(terminal.getCranes());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String craneMovementsToCSV(List<Crane> cranes){
        StringBuilder result = new StringBuilder();
        result.append("craneId;posX;posY;time\n");

        for (Crane crane:cranes) {
            for (Double time :crane.getTrajectory().keySet()) {
                Point p = crane.getTrajectory().get(time);
                result.append(String.format("%x;%.01f;%.01f;%.1f%n",crane.getId(), p.getX(), p.getY(), time));
            }
        }
        return result.toString();
    }

}