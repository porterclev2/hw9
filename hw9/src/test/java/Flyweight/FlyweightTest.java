package Flyweight;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlyweightTest {

    @Test
    public void testPropertiesGetters() {
        CharacterProperties properties = new CharacterProperties("Verdana", "Black", 16);
        assertEquals("Verdana", properties.getFont());
        assertEquals("Black", properties.getColor());
        assertEquals(16, properties.getSize());
    }

    @Test
    public void testPropertiesToString() {
        assertEquals("Calibri, Blue, 14", new CharacterProperties("Calibri", "Blue", 14).toString());
    }
}
