package br.com.gerqs.lunapptico.tools;

import java.util.Random;

public class FisherYates {
    public int[] shuffle(int[] arr) {
        Random rand = new Random();
        int m = arr.length;
        int tmp, i;

        while (m != 0) {
            i = rand.nextInt(m--);
            tmp = arr[m];
            arr[m] = arr[i];
            arr[i] = tmp;
        }
        return arr;
    }
}