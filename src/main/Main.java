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
        Terminal terminal = JsonReader.readTerminal("./src/main/resources/instances/1t/TerminalA_20_10_3_2_100.json");
        Terminal targetTerminal = JsonReader.readTerminal("./src/main/resources/instances/1t/targetTerminalA_20_10_3_2_100.json");
        Terminal terminalToConvert = JsonReader.readTerminal("./src/main/resources/instances/2mh/MH2Terminal_20_10_3_2_100.json");
        Target fixedTranshipmentsExample = new Target(terminal, targetTerminal);
        Target terminalConversionExample = new Target(terminalToConvert);
    }
}