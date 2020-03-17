import com.google.common.collect.Ordering;
import generation.FileHandler;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class MainTest {

    @Test
    public void givenIntArray_whenMainSort_thenSorted(){

        // given
        int[] numbers = new int[] {4,5,2,3,1};

        // when
        Main main = new Main(numbers);
        int[] sortedNumbers = main.sort();

        // then
        assertArrayEquals(new int[] {1,2,3,4,5}, sortedNumbers);
    }

    @Test
    public void givenSingleColumnCSV_whenMainSort_thenSorted() throws IOException {

        // given
        int[] numbers = FileHandler.readIntArrayFromFile("RandomIntegers.csv");

        //when
        Main main = new Main(numbers);
        int[] sortedNumbers = main.sort();

        // then
        List<Integer> list = Arrays.stream(sortedNumbers).boxed().collect(Collectors.toList());
        assertTrue(Ordering.natural().isOrdered(list));
    }

}
