import java.util.Arrays;

public class MergeSort {

    public static int[] sort(int[] array) {
        int length = array.length;

        // return if only one element left
        if (length == 1)
            return array;

        // divide
        int mid_index = length / 2;
        int[] leftHalf = Arrays.copyOfRange(array, 0, mid_index);
        int[] rightHalf = Arrays.copyOfRange(array, mid_index, length);

        // recursive call
        int[] sortedLeftHalf = sort(leftHalf);
        int[] sortedRightHalf = sort(rightHalf);

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
