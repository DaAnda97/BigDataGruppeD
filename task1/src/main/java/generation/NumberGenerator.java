package generation;

import java.util.concurrent.ThreadLocalRandom;

public class NumberGenerator {

    public static int[] randomIntegers(int amount){

        int[] randomIntegers = new int[amount];

        for(int i = 0; i < amount; i++){
            int randomInt = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            randomIntegers[i] = randomInt;
        }

        return randomIntegers;
    }

}
