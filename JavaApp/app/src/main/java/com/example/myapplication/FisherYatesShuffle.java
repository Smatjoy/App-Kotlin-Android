/*TO DO Add Fisher Yates Shuffle*/

package com.example.myapplication;

import java.util.ArrayList;
import java.util.Random;

    public class FisherYatesShuffle {
        public static ArrayList<MusicFiles> shuffle(ArrayList<MusicFiles> oldArray) {
            Random rand = new Random();
            ArrayList<MusicFiles> array = new ArrayList<MusicFiles>(oldArray);

            for (int i = array.size() - 1; i > 0; i--) {
                int j = rand.nextInt(i + 1); // 0 <= j <= i
                // Swap array[i] with array[j]
                MusicFiles temp = array.get(i);
                array.set(i,array.get(j));
                array.set(j, temp);
            }
            return array;
        }
}
