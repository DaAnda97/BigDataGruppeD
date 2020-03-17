package generation;

import org.junit.Assert;
import org.junit.Test;

public class NumberGeneratorTest {

    @Test
    public void testNumberGenerator(){

        int[] randomIntArray = NumberGenerator.randomIntegers(20);

        Assert.assertEquals(20, randomIntArray.length);

    }

}
