package Proxy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SongServer implements SongService {
    private Map<Integer, Song> songs = new LinkedHashMap<>();
    private long delayMillis;
    private int requestCount = 0;

    public SongServer() {
        this(1000);
    }

    public SongServer(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    public void addSong(Song song) {
        songs.put(song.getId(), song);
    }

    public Song searchById(Integer songID) {
        delay();
        System.out.println("Server: fetching song " + songID);
        return songs.get(songID);
    }

    public List<Song> searchByTitle(String title) {
        delay();
        System.out.println("Server: fetching songs titled \"" + title + "\"");
        List<Song> results = new ArrayList<>();
        for(Song song : songs.values()) {
            if(song.getTitle().equalsIgnoreCase(title)) {
                results.add(song);
            }
        }
        return results;
    }

    public List<Song> searchByAlbum(String album) {
        delay();
        System.out.println("Server: fetching songs on album \"" + album + "\"");
        List<Song> results = new ArrayList<>();
        for(Song song : songs.values()) {
            if(song.getAlbum().equalsIgnoreCase(album)) {
                results.add(song);
            }
        }
        return results;
    }

    public int getRequestCount() {
        return requestCount;
    }

    private void delay() {
        requestCount++;
        try {
            Thread.sleep(delayMillis);
        } catch (Exception e) {}
    }
}
