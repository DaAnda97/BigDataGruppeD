package generation;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileHandlerTest {

    @Test
    public void givenRandomArray_whenSaveAndRead_thenSameArray() throws IOException {

        String fileName = "RandomIntegers.csv";

        //given
        int[] randomIntArray = NumberGenerator.randomIntegers(2000000);

        //when
        FileHandler.saveIntArrayToFile(fileName, randomIntArray);
        int[] intNumbersInFile = FileHandler.readIntArrayFromFile(fileName);

        //then
        Assert.assertArrayEquals(randomIntArray, intNumbersInFile);
    }
}
