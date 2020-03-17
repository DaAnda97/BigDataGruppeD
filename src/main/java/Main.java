import java.util.Arrays;

public class Main {

    int[] numbers;

    public Main(int[] numbers) {
        this.numbers = numbers;
    }

    public int[] sort(){

        Arrays.sort(this.numbers);
        return this.numbers;

    }
}
