package Proxy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProxyTest {

    SongServer server = new SongServer(0);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;

    @BeforeEach
    public void addSongs() {
        System.setOut(new PrintStream(output));
        addDriverSongs(server);
    }

    @AfterEach
    public void restoreOutput() {
        System.setOut(originalOut);
    }

    private static void addDriverSongs(SongServer server) {
        server.addSong(new Song(1, "Bohemian Rhapsody", "Queen", "A Night at the Opera", 354));
        server.addSong(new Song(2, "Love of My Life", "Queen", "A Night at the Opera", 219));
        server.addSong(new Song(3, "Hotel California", "Eagles", "Hotel California", 391));
        server.addSong(new Song(4, "New Kid in Town", "Eagles", "Hotel California", 304));
        server.addSong(new Song(5, "Billie Jean", "Michael Jackson", "Thriller", 294));
        server.addSong(new Song(6, "Thriller", "Michael Jackson", "Thriller", 357));
        server.addSong(new Song(7, "Yesterday", "The Beatles", "Help!", 125));
        server.addSong(new Song(8, "Help!", "The Beatles", "Help!", 138));
    }

    @Test
    public void testSongGetters() {
        Song song = server.searchById(1);
        assertEquals(1, song.getId());
        assertEquals("Bohemian Rhapsody", song.getTitle());
        assertEquals("Queen", song.getArtist());
        assertEquals("A Night at the Opera", song.getAlbum());
        assertEquals(354, song.getDuration());
    }

    @Test
    public void testSongToString() {
        assertEquals("#1 Bohemian Rhapsody by Queen on A Night at the Opera (5:54)", server.searchById(1).toString());
        assertEquals("#7 Yesterday by The Beatles on Help! (2:05)", server.searchById(7).toString());
    }

    @Test
    public void testServerAddSong() {
        server.addSong(new Song(99, "Imagine", "John Lennon", "Imagine", 183));
        assertEquals("Imagine", server.searchById(99).getTitle());
    }

    @Test
    public void testServerSearchById() {
        assertEquals("Billie Jean", server.searchById(5).getTitle());
        assertNull(server.searchById(99));
        assertTrue(output.toString().contains("Server: fetching song 5"));
    }

    @Test
    public void testServerSearchByTitle() {
        List<Song> songs = server.searchByTitle("hotel california");
        assertEquals(1, songs.size());
        assertEquals(3, songs.get(0).getId());
        assertTrue(output.toString().contains("Server: fetching songs titled \"hotel california\""));
    }

    @Test
    public void testServerSearchByAlbum() {
        List<Song> songs = server.searchByAlbum("Help!");
        assertEquals(2, songs.size());
        assertEquals("Yesterday", songs.get(0).getTitle());
        assertEquals("Help!", songs.get(1).getTitle());
        assertTrue(output.toString().contains("Server: fetching songs on album \"Help!\""));
    }

    @Test
    public void testServerIsNotCached() {
        server.searchById(1);
        server.searchById(1);
        assertEquals(2, server.getRequestCount());
    }

    @Test
    public void testGetRequestCount() {
        assertEquals(0, server.getRequestCount());
        server.searchById(1);
        server.searchByTitle("Yesterday");
        server.searchByAlbum("Thriller");
        assertEquals(3, server.getRequestCount());
    }
}
