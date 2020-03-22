package generation;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class FileHandlerTest {

    @Test
    public void givenRandomArray_whenSaveAndRead_thenSameArray() throws IOException {

        String fileName = "RandomIntegersBigger.csv";

        //given
        int[] randomIntArray = NumberGenerator.randomIntegers(50000000);

        //when
        FileHandler.saveIntArrayToFile(fileName, randomIntArray);
        int[] intNumbersInFile = FileHandler.readIntArrayFromFile(fileName);

        //then
        Assert.assertArrayEquals(randomIntArray, intNumbersInFile);
    }
}
