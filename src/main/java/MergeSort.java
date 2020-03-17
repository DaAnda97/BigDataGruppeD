import java.util.Arrays;

public class MergeSort {

    int[] numbers;

    public MergeSort(int[] numbers) {
        this.numbers = numbers;
    }

    public int[] mergeSort(){

        // that's necessary because of recursion
        // all operations are processed on the given array - there will be no copy
        sort(this.numbers, numbers.length);

        return this.numbers;
    }


    private void sort(int[] array, int length) {
        // return if only one element left
        if (length <= 1)
            return;

        // divide
        int mid_index = length / 2;
        int[] leftHalf = Arrays.copyOfRange(array, 0, mid_index);
        int[] rightHalf = Arrays.copyOfRange(array, mid_index, length);

        // recursive call
        sort(leftHalf, mid_index);
        sort(rightHalf, length - mid_index);

        merge(array, leftHalf, rightHalf, mid_index,length - mid_index);
    }


    private void merge(int[] array, int[] leftHalf, int[] rightHalf, int endOfLeftHalf, int endOfRightHalf) {

        int indexOfArray = 0, indexOfLeftHalf = 0, indexOfRightHalf = 0;

        // until reaching the right bound from one of both halfs
        while (indexOfLeftHalf < endOfLeftHalf && indexOfRightHalf < endOfRightHalf) {

            // overwriting the current parent value with the smaller child value
            // every child is always sorted from the smallest to the biggest
            if (leftHalf[indexOfLeftHalf] <= rightHalf[indexOfRightHalf]) {
                array[indexOfArray] = leftHalf[indexOfLeftHalf];
                indexOfArray++;
                indexOfLeftHalf++;
            }
            else {
                array[indexOfArray] = rightHalf[indexOfRightHalf];
                indexOfArray++;
                indexOfRightHalf++;
            }

        }

        // add all remaining numbers in leftHalf to array
        while (indexOfLeftHalf < endOfLeftHalf) {
            array[indexOfArray] = leftHalf[indexOfLeftHalf];
            indexOfArray++;
            indexOfLeftHalf++;
        }

        // add all remaining numbers in rightHalf to array
        while (indexOfRightHalf < endOfRightHalf){
            array[indexOfArray] = rightHalf[indexOfRightHalf];
            indexOfArray++;
            indexOfRightHalf++;
        }

    }


}
