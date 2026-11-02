package Flyweight;

public class DocumentCharacter {
    private char value;
    private CharacterProperties properties;

    public DocumentCharacter(char value, CharacterProperties properties) {
        this.value = value;
        this.properties = properties;
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    public CharacterProperties getProperties() {
        return properties;
    }

    public void setProperties(CharacterProperties properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "'" + value + "' [" + properties + "]";
    }
}
