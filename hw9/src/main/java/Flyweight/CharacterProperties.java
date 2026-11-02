package Flyweight;

public class CharacterProperties {
    private int size;
    private String font;
    private String color;

    CharacterProperties(String font, String color, int size) {
        this.font = font;
        this.color = color;
        this.size = size;
    }
    
    public int getSize() {
        return size;
    }

    public String getFont() {
        return font;
    }

    public String getColor() {
        return color;
    }


    @Override
    public String toString() {
        return font + ", " + color + ", " + size;
    }
}
