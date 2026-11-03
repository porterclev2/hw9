package Proxy;

import java.util.HashMap;
import java.util.List;

public class SongServiceProxy implements SongService {
    private HashMap<String, List<Song>> albumCache = new HashMap<>();
    private HashMap<String, List<Song>> titleCache = new HashMap<>();
    private HashMap<Integer, Song> songCache = new HashMap<>();
    private SongService server;

    public SongServiceProxy(SongService server) {
        this.server = server;
    }

    public Song searchById(Integer songID) {
        if(!songCache.containsKey(songID)) {
            Song song = server.searchById(songID);
            if(song == null) {
                return null;
            }
            songCache.put(songID, song);
        }
        return songCache.get(songID);
    }

    public List<Song> searchByTitle(String title) {
        String key = title.toLowerCase();
        if(!titleCache.containsKey(key)) {
            List<Song> songs = List.copyOf(server.searchByTitle(title));
            if(songs.isEmpty()) {
                return songs;
            }
            titleCache.put(key, songs);
            cacheById(songs);
        }
        return titleCache.get(key);
    }

    public List<Song> searchByAlbum(String album) {
        String key = album.toLowerCase();
        if(!albumCache.containsKey(key)) {
            List<Song> songs = List.copyOf(server.searchByAlbum(album));
            if(songs.isEmpty()) {
                return songs;
            }
            albumCache.put(key, songs);
            cacheById(songs);
        }
        return albumCache.get(key);
    }

    private void cacheById(List<Song> songs) {
        for(Song song : songs) {
            songCache.put(song.getId(), song);
        }
    }
}
