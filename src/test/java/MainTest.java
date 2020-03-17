import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void testSort(){

        int[] numbers = new int[] {4,5,2,3,1};

        Main main = new Main(numbers);
        int[] sortedNumbers = main.sort();

        assertArrayEquals(new int[] {1,2,3,4,5}, sortedNumbers);
    }

}
