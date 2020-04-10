import java.util.Arrays;
import java.util.concurrent.*;

public class MergeSort {

    public static int[] mergeSort(int[] array) {
        int length = array.length;

        // return if only one element left
        if (length == 1)
            return array;

        // divide
        int mid_index = length / 2;
        int[] leftHalf = Arrays.copyOfRange(array, 0, mid_index);
        int[] rightHalf = Arrays.copyOfRange(array, mid_index, length);

        // recursive call
        int[] sortedLeftHalf = mergeSort(leftHalf);
        int[] sortedRightHalf = mergeSort(rightHalf);

        return merge(sortedLeftHalf, sortedRightHalf);
    }

    /**
     * Use multi threading for merge sort
     * @param array the array which needs to be sorted
     * @param maxRecursionLevel the maximum number of recursion levels this call may use
     * @return
     */
    public static int[] parallelMergeSort(int[] array, int maxRecursionLevel) {

        int length = array.length;

        // return if only one element left
        if (length == 1)
            return array;

        // divide
        int mid_index = length / 2;
        int[] leftHalf = Arrays.copyOfRange(array, 0, mid_index);
        int[] rightHalf = Arrays.copyOfRange(array, mid_index, length);

        // fall back to single threaded merge sort if all threads are currently in use
        if(maxRecursionLevel <= 1) {
            return mergeSort(array);
        }

        //
        ExecutorService leftHalfArray = Executors.newSingleThreadExecutor();
        Callable<int[]> leftHalfArrayCallable = () -> parallelMergeSort(leftHalf, maxRecursionLevel - 1);

        ExecutorService rightHalfArray = Executors.newSingleThreadExecutor();
        Callable<int[]> rightHalfArrayCallable = () -> parallelMergeSort(rightHalf, maxRecursionLevel - 1);

        Future<int[]> leftHalfFuture = leftHalfArray.submit(leftHalfArrayCallable);
        Future<int[]> rightHalfFuture = rightHalfArray.submit(rightHalfArrayCallable);

        int[] sortedLeftHalf = null;
        int[] sortedRightHalf = null;
        try {
            sortedLeftHalf = leftHalfFuture.get();
            sortedRightHalf = rightHalfFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        leftHalfArray.shutdown();
        rightHalfArray.shutdown();

        assert sortedLeftHalf != null;
        assert sortedRightHalf != null;

        //
        return merge(sortedLeftHalf, sortedRightHalf);
    }

    private static int[] merge(int[] leftHalf, int[] rightHalf) {

        int endOfLeftHalf = leftHalf.length;
        int endOfRightHalf = rightHalf.length;
        int indexOfArray = 0, indexOfLeftHalf = 0, indexOfRightHalf = 0;

        int[] mergedArray = new int[endOfLeftHalf + endOfRightHalf];

        // until reaching the right bound from one of both halves
        while (indexOfLeftHalf < endOfLeftHalf && indexOfRightHalf < endOfRightHalf) {

            // setting current parent value to the smaller child value
            // every child is always sorted from the smallest to the biggest
            if (leftHalf[indexOfLeftHalf] <= rightHalf[indexOfRightHalf]) {
                mergedArray[indexOfArray] = leftHalf[indexOfLeftHalf];
                indexOfArray++;
                indexOfLeftHalf++;
            }
            else {
                mergedArray[indexOfArray] = rightHalf[indexOfRightHalf];
                indexOfArray++;
                indexOfRightHalf++;
            }

        }

        // add all remaining numbers in leftHalf to array
        while (indexOfLeftHalf < endOfLeftHalf) {
            mergedArray[indexOfArray] = leftHalf[indexOfLeftHalf];
            indexOfArray++;
            indexOfLeftHalf++;
        }

        // add all remaining numbers in rightHalf to array
        while (indexOfRightHalf < endOfRightHalf){
            mergedArray[indexOfArray] = rightHalf[indexOfRightHalf];
            indexOfArray++;
            indexOfRightHalf++;
        }

        return mergedArray;
    }


}
