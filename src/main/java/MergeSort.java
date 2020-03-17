public class MergeSort {

    int[] numbers;

    public MergeSort(int[] numbers) {
        this.numbers = numbers;
    }

    public int[] mergeSort(){

        sort(this.numbers, numbers.length);
        return this.numbers;

    }


    private void sort(int[] a, int length) {
        if (length < 2)
            return;
        int mid = length / 2;
        int[] l = new int[mid];
        int[] r = new int[length - mid];

        for (int i = 0; i < mid; i++) {
            l[i] = a[i];
        }
        for (int i = mid; i < length; i++) {
            r[i - mid] = a[i];
        }
        sort(l, mid);
        sort(r, length - mid);

        merge(a, l, r, mid, length - mid);
    }


    private void merge(int[] a, int[] l, int[] r, int left, int right) {

        int i = 0, j = 0, k = 0;

        while (i < left && j < right) {

            if (l[i] <= r[j])
                a[k++] = l[i++];
            else
                a[k++] = r[j++];

        }

        while (i < left)
            a[k++] = l[i++];

        while (j < right)
            a[k++] = r[j++];
    }


}
