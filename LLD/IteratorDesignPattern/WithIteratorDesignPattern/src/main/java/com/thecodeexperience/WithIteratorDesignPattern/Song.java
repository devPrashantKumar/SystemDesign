package com.thecodeexperience.WithIteratorDesignPattern;

public class Song {

    private final String title;
    private final String artist;
    private final int seconds;

    public Song(String title, String artist, int seconds) {
        this.title = title;
        this.artist = artist;
        this.seconds = seconds;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getSeconds() {
        return seconds;
    }

    @Override
    public String toString() {
        return title + " - " + artist + " (" + seconds + "s)";
    }

}
