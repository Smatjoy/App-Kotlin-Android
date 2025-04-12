/*TO DO Add Fisher Yates Shuffle*/

package com.example.myapplication;

import java.util.Random;

    public class FisherYatesShuffle {
        public static void shuffle(String[] array) {
            Random rand = new Random();

            for (int i = array.length - 1; i > 0; i--) {
                int j = rand.nextInt(i + 1); // 0 <= j <= i
                // Swap array[i] with array[j]
                String temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
}
