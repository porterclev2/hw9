package Proxy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProxyTest {

    @Test
    public void testSongGetters() {
        Song song = new Song(1, "Bohemian Rhapsody", "Queen", "A Night at the Opera", 354);
        assertEquals(1, song.getId());
        assertEquals("Bohemian Rhapsody", song.getTitle());
        assertEquals("Queen", song.getArtist());
        assertEquals("A Night at the Opera", song.getAlbum());
        assertEquals(354, song.getDuration());
    }

    @Test
    public void testSongToString() {
        assertEquals("#1 Bohemian Rhapsody by Queen on A Night at the Opera (5:54)",
                new Song(1, "Bohemian Rhapsody", "Queen", "A Night at the Opera", 354).toString());
        assertEquals("#7 Yesterday by The Beatles on Help! (2:05)",
                new Song(7, "Yesterday", "The Beatles", "Help!", 125).toString());
    }
}
