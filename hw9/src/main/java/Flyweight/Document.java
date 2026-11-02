package Flyweight;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Document {
    private List<DocumentCharacter> characters = new ArrayList<>();
    private CharacterPropertiesFactory factory;

    public Document(CharacterPropertiesFactory factory) {
        this.factory = factory;
    }

    public void save(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        for(DocumentCharacter character : characters) {
            CharacterProperties properties = character.getProperties();
            lines.add((int) character.getValue() + "\t" + properties.getFont() + "\t"
                    + properties.getColor() + "\t" + properties.getSize());
        }
        Files.write(Path.of(path), lines);
    }

    public static Document load(String path, CharacterPropertiesFactory factory) throws IOException {
        Document document = new Document(factory);
        for(String line : Files.readAllLines(Path.of(path))) {
            String[] parts = line.split("\t");
            if(parts.length != 4) {
                throw new IOException("Invalid line in " + path + ": " + line);
            }
            try {
                document.addCharacter((char) Integer.parseInt(parts[0]), parts[1], parts[2],
                        Integer.parseInt(parts[3]));
            } catch (NumberFormatException e) {
                throw new IOException("Invalid line in " + path + ": " + line, e);
            }
        }
        return document;
    }
    
    public void addCharacter(char value, String font, String color, int size) {
        characters.add(new DocumentCharacter(value, factory.getProperties(font, color, size)));
    }

    public void addText(String text, String font, String color, int size) {
        for(char value : text.toCharArray()) {
            addCharacter(value, font, color, size);
        }
    }

    public void insertCharacter(int index, char value, String font, String color, int size) {
        characters.add(index, new DocumentCharacter(value, factory.getProperties(font, color, size)));
    }

    public void editCharacter(int index, char value) {
        characters.get(index).setValue(value);
    }

    public void editProperties(int index, String font, String color, int size) {
        characters.get(index).setProperties(factory.getProperties(font, color, size));
    }

    public void removeCharacter(int index) {
        characters.remove(index);
    }

    public DocumentCharacter getCharacter(int index) {
        return characters.get(index);
    }

    public int length() {
        return characters.size();
    }

    public String getText() {
        StringBuilder text = new StringBuilder();
        for(DocumentCharacter character : characters) {
            text.append(character.getValue());
        }
        return text.toString();
    }

    public void print() {
        for(DocumentCharacter character : characters) {
            System.out.println(character);
        }
    }

}
