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
    public static void main(String[] args) throws Exception {
        Terminal terminal = JsonReader.readInitialTerminal("./src/main/resources/instances/10t/TerminalC_10_10_3_2_100.json");
        Assignment[] targetAss = JsonReader.readAssignments("./src/main/resources/instances/10t/targetTerminalC_10_10_3_2_100.json", terminal);
        //terminal = JsonReader.readTerminal("./src/main/resources/examples/terminal22_1_100_1_10.json");
        //targetTerminal = JsonReader.readTerminal("./src/main/resources/examples/terminal22_1_100_1_10target.json");
        Terminal terminalToConvert = JsonReader.readInitialTerminal("./src/main/resources/instances/4mh/MH2Terminal_20_10_3_2_160.json");
        Target fixedTranshipmentsExample = new Target(terminal, targetAss);
//        Target terminalConversionExample = new Target(terminalToConvert);
    }
}