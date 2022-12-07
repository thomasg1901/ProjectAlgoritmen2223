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
        Terminal terminal = JsonReader.readTerminal("src/main/resources/terminal22_1_100_1_10.json");
        System.out.println("test");
    }
}