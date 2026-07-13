package com.thecodeexperience.WithIteratorDesignPattern;

import java.util.Iterator;

public class Main {

    public static void main(String[] args) {

        ArrayPlaylist workout = new ArrayPlaylist();
        workout.add(new Song("Thunderstruck", "AC/DC", 292));
        workout.add(new Song("Lose Yourself", "Eminem", 326));
        workout.add(new Song("Stronger", "Kanye West", 312));

        LinkedPlaylist chill = new LinkedPlaylist();
        chill.add(new Song("Weightless", "Marconi Union", 485));
        chill.add(new Song("Intro", "The xx", 128));
        chill.add(new Song("Redbone", "Childish Gambino", 327));

        Player player = new Player();

        // ✅ ONE client method. Two completely different data structures.
        //    The Player has no idea one is an array and the other is a node chain.
        System.out.println("--- Workout (array-backed) ---");
        player.play(workout);

        System.out.println();
        System.out.println("--- Chill (linked-node-backed) ---");
        player.play(chill);

        System.out.println();
        System.out.println("--- same client method, same result: " + player.totalSeconds(workout)
                + "s vs " + player.totalSeconds(chill) + "s ---");

        // ✅ A SECOND traversal over the SAME collection.
        System.out.println();
        System.out.println("--- Workout, reversed (same data, different walk) ---");
        player.play(workout.reverseIterator());

        // ✅ A traversal POLICY that knows nothing about either playlist.
        System.out.println();
        System.out.println("--- Chill, songs over 5 minutes (filter composed onto any iterator) ---");
        player.play(new FilteringIterator(chill.iterator(), song -> song.getSeconds() > 300));

        // ✅ TWO INDEPENDENT CURSORS over one collection, at the same time.
        //    Impossible if the playlist held the position itself — which is exactly why
        //    the cursor belongs in the iterator, not in the collection.
        System.out.println();
        System.out.println("--- two simultaneous iterators, independent positions ---");
        Iterator<Song> ahead = workout.iterator();
        Iterator<Song> behind = workout.iterator();
        ahead.next();
        ahead.next();
        System.out.println("    ahead  is at: " + ahead.next().getTitle());
        System.out.println("    behind is at: " + behind.next().getTitle());
    }

}
