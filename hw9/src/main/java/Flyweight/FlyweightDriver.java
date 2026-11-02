package Flyweight;

import java.io.IOException;

public class FlyweightDriver {
    public static void main(String[] args) throws IOException {
        CharacterPropertiesFactory factory = new CharacterPropertiesFactory();
        Document document = new Document(factory);

        document.addText("Hello", "Arial", "Red", 12);
        document.addText("World", "Calibri", "Blue", 14);
        document.addText("CS", "Verdana", "Black", 16);
        document.addText("5800", "Arial", "Blue", 16);

        System.out.println("Created: " + document.getText());
        document.print();
        System.out.println(document.length() + " characters share " + factory.getPropertiesCount()
                + " character properties");

        System.out.println("\nEditing W to Verdana, Red, 12 and C to Arial, Red, 12");
        document.editProperties(5, "Verdana", "Red", 12);
        document.editProperties(10, "Arial", "Red", 12);
        document.print();
        System.out.println(document.length() + " characters share " + factory.getPropertiesCount()
                + " character properties");

        String path = "HelloWorldCS5800.txt";
        document.save(path);
        System.out.println("\nSaved to " + path);

        Document loaded = Document.load(path, factory);
        System.out.println("Loaded from " + path + ": " + loaded.getText());
        loaded.print();
        System.out.println(loaded.length() + " characters share " + factory.getPropertiesCount()
                + " character properties");
        System.out.println("Loaded 'H' shares properties with original 'H': "
                + (loaded.getCharacter(0).getProperties() == document.getCharacter(0).getProperties()));
    }
}
