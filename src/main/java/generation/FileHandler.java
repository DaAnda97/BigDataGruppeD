package generation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class FileHandler {

    public static void saveIntArrayToFile(String fileName, int[] array) throws IOException {
        FileWriter csvWriter = new FileWriter(fileName);

        for (int i = 0; i < array.length; i++) {
            csvWriter.append(array[i] + "");
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();

    }

    public static int[] readIntArrayFromFile(String fileName) throws IOException {

        return Files.readAllLines(Paths.get(fileName))
                .stream()
                .mapToInt(Integer::parseInt)
                .toArray();

    }
}
