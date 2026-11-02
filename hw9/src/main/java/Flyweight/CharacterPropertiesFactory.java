package Flyweight;

import java.util.HashMap;

public class CharacterPropertiesFactory {
    private HashMap<String, CharacterProperties> properties = new HashMap<>();

    public CharacterProperties getProperties(String font, String color, int size) {
        String key = font + "|" + color + "|" + size;
        if(properties.containsKey(key)) {
            return properties.get(key);
        } else {
            CharacterProperties property = new CharacterProperties(font, color, size);
            properties.put(key, property);
            return property;
        }
    }

    public int getPropertiesCount() {
        return properties.size();
    }
}
