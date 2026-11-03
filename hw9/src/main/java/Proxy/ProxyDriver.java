package Proxy;

import java.util.function.Supplier;

public class ProxyDriver {
    public static void main(String[] args) {
        SongServer server = new SongServer();
        server.addSong(new Song(1, "Bohemian Rhapsody", "Queen", "A Night at the Opera", 354));
        server.addSong(new Song(2, "Love of My Life", "Queen", "A Night at the Opera", 219));
        server.addSong(new Song(3, "Hotel California", "Eagles", "Hotel California", 391));
        server.addSong(new Song(4, "New Kid in Town", "Eagles", "Hotel California", 304));
        server.addSong(new Song(5, "Billie Jean", "Michael Jackson", "Thriller", 294));
        server.addSong(new Song(6, "Thriller", "Michael Jackson", "Thriller", 357));
        server.addSong(new Song(7, "Yesterday", "The Beatles", "Help!", 125));
        server.addSong(new Song(8, "Help!", "The Beatles", "Help!", 138));

        SongService songService = new SongServiceProxy(server);

        System.out.println("Search by id");
        timed("searchById(1)", () -> songService.searchById(1));
        timed("searchById(1)", () -> songService.searchById(1));
        timed("searchById(5)", () -> songService.searchById(5));
        timed("searchById(5)", () -> songService.searchById(5));

        System.out.println("\nSearch by title");
        timed("searchByTitle(\"Hotel California\")", () -> songService.searchByTitle("Hotel California"));
        timed("searchByTitle(\"hotel california\")", () -> songService.searchByTitle("hotel california"));
        timed("searchByTitle(\"Yesterday\")", () -> songService.searchByTitle("Yesterday"));
        timed("searchByTitle(\"Yesterday\")", () -> songService.searchByTitle("Yesterday"));

        System.out.println("\nSearch by album");
        timed("searchByAlbum(\"Thriller\")", () -> songService.searchByAlbum("Thriller"));
        timed("searchByAlbum(\"Thriller\")", () -> songService.searchByAlbum("Thriller"));
        timed("searchByAlbum(\"A Night at the Opera\")", () -> songService.searchByAlbum("A Night at the Opera"));
        timed("searchByAlbum(\"A Night at the Opera\")", () -> songService.searchByAlbum("A Night at the Opera"));

        System.out.println("\nSongs cached by album search no longer need the server");
        timed("searchById(6)", () -> songService.searchById(6));
        timed("searchById(2)", () -> songService.searchById(2));

        System.out.println("\nSongs not in the library");
        timed("searchById(99)", () -> songService.searchById(99));
        timed("searchByTitle(\"Imagine\")", () -> songService.searchByTitle("Imagine"));

        System.out.println("\nTotal server requests: " + server.getRequestCount());
    }

    private static void timed(String label, Supplier<Object> search) {
        long start = System.currentTimeMillis();
        Object result = search.get();
        long elapsed = System.currentTimeMillis() - start;
        System.out.println(label + " -> " + result + " (" + elapsed + " ms)");
    }
}
