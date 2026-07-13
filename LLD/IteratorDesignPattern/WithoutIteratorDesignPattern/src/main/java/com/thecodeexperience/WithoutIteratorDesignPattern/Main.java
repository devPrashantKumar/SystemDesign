package com.thecodeexperience.WithoutIteratorDesignPattern;

public class Main {

    public static void main(String[] args) {

        ArrayPlaylist workout = new ArrayPlaylist();
        workout.add(new Song("Thunderstruck", "AC/DC"));
        workout.add(new Song("Lose Yourself", "Eminem"));
        workout.add(new Song("Stronger", "Kanye West"));

        LinkedPlaylist chill = new LinkedPlaylist();
        chill.add(new Song("Weightless", "Marconi Union"));
        chill.add(new Song("Intro", "The xx"));
        chill.add(new Song("Redbone", "Childish Gambino"));

        // ⚠ THE PROBLEM: the client cannot say "play the playlist".
        //    It must know HOW each playlist stores its songs, and write a different
        //    loop for each. printArrayPlaylist / printLinkedPlaylist do the SAME job.

        System.out.println("--- Workout (array-backed) ---");
        printArrayPlaylist(workout);

        System.out.println();
        System.out.println("--- Chill (linked-node-backed) ---");
        printLinkedPlaylist(chill);

        // ⚠ THE PROBLEM: the internals are not just visible, they are WRITABLE.
        System.out.println();
        System.out.println("--- a caller reaches into the array and breaks the playlist ---");
        workout.getSongs()[0] = null;
        printArrayPlaylist(workout);
    }

    // one traversal per storage type...
    private static void printArrayPlaylist(ArrayPlaylist playlist) {
        Song[] songs = playlist.getSongs();
        for (int i = 0; i < playlist.getCount(); i++) {
            System.out.println("    " + songs[i]);
        }
    }

    // ...and the SAME logic again, written differently only because the storage differs
    private static void printLinkedPlaylist(LinkedPlaylist playlist) {
        for (LinkedPlaylist.Node node = playlist.getHead(); node != null; node = node.next) {
            System.out.println("    " + node.song);
        }
    }

    // Add a HashSet-backed playlist, or a tree, or a paged remote playlist,
    // and every client writes a third loop. The clients grow with the STORAGE types.

}
