package Proxy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProxyTest {

    SongServer server = new SongServer(0);
    SongService songService = new SongServiceProxy(server);
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
        Song song = songService.searchById(1);
        assertEquals(1, song.getId());
        assertEquals("Bohemian Rhapsody", song.getTitle());
        assertEquals("Queen", song.getArtist());
        assertEquals("A Night at the Opera", song.getAlbum());
        assertEquals(354, song.getDuration());
    }

    @Test
    public void testSongToString() {
        assertEquals("#1 Bohemian Rhapsody by Queen on A Night at the Opera (5:54)", songService.searchById(1).toString());
        assertEquals("#7 Yesterday by The Beatles on Help! (2:05)", songService.searchById(7).toString());
    }

    @Test
    public void testSearchByIdIsCached() {
        Song first = songService.searchById(1);
        Song second = songService.searchById(1);
        assertSame(first, second);
        assertEquals(1, server.getRequestCount());
        assertEquals("Server: fetching song 1" + System.lineSeparator(), output.toString());
    }

    @Test
    public void testSearchByIdFetchesOnDemand() {
        assertEquals(0, server.getRequestCount());
        songService.searchById(1);
        songService.searchById(5);
        songService.searchById(5);
        assertEquals(2, server.getRequestCount());
        assertEquals("Billie Jean", songService.searchById(5).getTitle());
    }

    @Test
    public void testSearchByTitle() {
        List<Song> songs = songService.searchByTitle("Hotel California");
        assertEquals(1, songs.size());
        assertEquals(3, songs.get(0).getId());
    }

    @Test
    public void testSearchByTitleIsCached() {
        List<Song> first = songService.searchByTitle("Yesterday");
        List<Song> second = songService.searchByTitle("Yesterday");
        assertSame(first, second);
        assertEquals(1, server.getRequestCount());
    }

    @Test
    public void testSearchByTitleIgnoresCase() {
        List<Song> first = songService.searchByTitle("Hotel California");
        List<Song> second = songService.searchByTitle("hotel california");
        assertSame(first, second);
        assertEquals(1, server.getRequestCount());
    }

    @Test
    public void testSearchByAlbum() {
        List<Song> songs = songService.searchByAlbum("A Night at the Opera");
        assertEquals(2, songs.size());
        assertEquals("Bohemian Rhapsody", songs.get(0).getTitle());
        assertEquals("Love of My Life", songs.get(1).getTitle());
    }

    @Test
    public void testSearchByAlbumIsCached() {
        List<Song> first = songService.searchByAlbum("Thriller");
        List<Song> second = songService.searchByAlbum("Thriller");
        assertSame(first, second);
        assertEquals(1, server.getRequestCount());
    }

    @Test
    public void testTitleAndAlbumAreCachedSeparately() {
        assertEquals(1, songService.searchByTitle("Thriller").size());
        assertEquals(2, songService.searchByAlbum("Thriller").size());
        assertEquals(2, server.getRequestCount());
    }

    @Test
    public void testAlbumSearchCachesSongsById() {
        List<Song> thriller = songService.searchByAlbum("Thriller");
        List<Song> opera = songService.searchByAlbum("A Night at the Opera");
        assertSame(thriller.get(1), songService.searchById(6));
        assertSame(opera.get(1), songService.searchById(2));
        assertEquals(2, server.getRequestCount());
    }

    @Test
    public void testTitleSearchCachesSongsById() {
        List<Song> songs = songService.searchByTitle("Hotel California");
        assertSame(songs.get(0), songService.searchById(3));
        assertEquals(1, server.getRequestCount());
    }

    @Test
    public void testUnknownIdReturnsNull() {
        assertNull(songService.searchById(99));
    }

    @Test
    public void testUnknownTitleReturnsEmptyList() {
        assertTrue(songService.searchByTitle("Imagine").isEmpty());
    }

    @Test
    public void testUnknownAlbumReturnsEmptyList() {
        assertTrue(songService.searchByAlbum("Imagine").isEmpty());
    }

    @Test
    public void testMissesAreNotCached() {
        assertNull(songService.searchById(99));
        assertTrue(songService.searchByTitle("Imagine").isEmpty());
        server.addSong(new Song(99, "Imagine", "John Lennon", "Imagine", 183));
        assertEquals("Imagine", songService.searchById(99).getTitle());
        assertEquals(1, songService.searchByTitle("Imagine").size());
        assertEquals(4, server.getRequestCount());
    }

    @Test
    public void testCachedListCannotBeModified() {
        List<Song> songs = songService.searchByAlbum("Thriller");
        assertThrows(UnsupportedOperationException.class, () -> songs.clear());
        assertEquals(2, songService.searchByAlbum("Thriller").size());
    }

    @Test
    public void testDriverSearches() {
        songService.searchById(1);
        songService.searchById(1);
        songService.searchById(5);
        songService.searchById(5);
        songService.searchByTitle("Hotel California");
        songService.searchByTitle("hotel california");
        songService.searchByTitle("Yesterday");
        songService.searchByTitle("Yesterday");
        songService.searchByAlbum("Thriller");
        songService.searchByAlbum("Thriller");
        songService.searchByAlbum("A Night at the Opera");
        songService.searchByAlbum("A Night at the Opera");
        songService.searchById(6);
        songService.searchById(2);
        songService.searchById(99);
        songService.searchByTitle("Imagine");
        assertEquals(8, server.getRequestCount());
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

    @Test
    public void testCachedSearchIsFaster() {
        SongServer slowServer = new SongServer();
        addDriverSongs(slowServer);
        SongService slowService = new SongServiceProxy(slowServer);

        long start = System.currentTimeMillis();
        slowService.searchById(1);
        long firstCall = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        slowService.searchById(1);
        long cachedCall = System.currentTimeMillis() - start;

        assertTrue(firstCall >= 1000, "First call should wait on the server");
        assertTrue(cachedCall < 1000, "Cached call should not wait on the server");
    }
}
