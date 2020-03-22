import com.google.common.collect.Ordering;
import generation.FileHandler;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class MergeSortTest {

    @Test
    public void givenIntArray_whenMainSort_thenSorted(){

        // given
        int[] numbers = new int[] {4,5,2,3,1};

        // when
        int[] sortedNumbers = MergeSort.mergeSort(numbers);

        // then
        assertArrayEquals(new int[] {1,2,3,4,5}, sortedNumbers);
    }

    @Test
    public void givenSingleColumnCSV_whenMainSort_thenSorted() throws IOException {

        // given
        int[] numbers = FileHandler.readIntArrayFromFile("RandomIntegers.csv");

        //when
        int[] sortedNumbers = MergeSort.mergeSort(numbers);

        // then
        List<Integer> list = Arrays.stream(sortedNumbers).boxed().collect(Collectors.toList());
        assertTrue(Ordering.natural().isOrdered(list));
    }

    @Test
    public void multiThreadedSorting() throws IOException {

        int availableThreads = 4;

        // given
        int[] numbers = FileHandler.readIntArrayFromFile("RandomIntegersBigger.csv");

        // when
        Instant startMultiThreaded = Instant.now();
        int[] sortedNumbersMultiThreaded = MergeSort.parallelMergeSort(numbers, 4);
        Instant endMultiThreaded = Instant.now();

        // then
        List<Integer> list = Arrays.stream(sortedNumbersMultiThreaded).boxed().collect(Collectors.toList());
        assertTrue(Ordering.natural().isOrdered(list));
    }

    @Test
    public void multiThreadedSortingShouldBeFasterThanSingleThreaded() throws IOException {

        int availableThreads = 4;

        // given
        int[] numbers = FileHandler.readIntArrayFromFile("RandomIntegersBigger.csv");

        // when single threaded
        Instant startSingleThread = Instant.now();
        int[] sortedNumbersSingleThreaded = MergeSort.mergeSort(numbers);
        Instant endSingleThread = Instant.now();

        // when multi threaded
        Instant startMultiThreaded = Instant.now();
        int[] sortedNumbersMultiThreaded = MergeSort.parallelMergeSort(numbers, 4);
        Instant endMultiThreaded = Instant.now();

        // then assert that multi threaded is faster
        assert(Duration.between(startSingleThread, endSingleThread).getSeconds() > Duration.between(startMultiThreaded, endMultiThreaded).getSeconds());

    }

}
