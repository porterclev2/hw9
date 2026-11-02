package Flyweight;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FlyweightTest {

    CharacterPropertiesFactory factory = new CharacterPropertiesFactory();

    @Test
    public void testFactoryReturnsSharedProperties() {
        assertSame(factory.getProperties("Arial", "Red", 12), factory.getProperties("Arial", "Red", 12));
        assertEquals(1, factory.getPropertiesCount());
    }

    @Test
    public void testFactoryCreatesNewPropertiesWhenAnyFieldDiffers() {
        CharacterProperties arialRed12 = factory.getProperties("Arial", "Red", 12);
        assertNotSame(arialRed12, factory.getProperties("Verdana", "Red", 12));
        assertNotSame(arialRed12, factory.getProperties("Arial", "Blue", 16));
        assertNotSame(factory.getProperties("Arial", "Blue", 16), factory.getProperties("Verdana", "Black", 16));
        assertEquals(4, factory.getPropertiesCount());
    }

    @Test
    public void testGetPropertiesCount() {
        assertEquals(0, factory.getPropertiesCount());
        factory.getProperties("Calibri", "Blue", 14);
        factory.getProperties("Verdana", "Black", 16);
        factory.getProperties("Arial", "Red", 12);
        factory.getProperties("Arial", "Blue", 16);
        assertEquals(4, factory.getPropertiesCount());
        factory.getProperties("Verdana", "Red", 12);
        assertEquals(5, factory.getPropertiesCount());
        factory.getProperties("Arial", "Red", 12);
        assertEquals(5, factory.getPropertiesCount());
    }

    @Test
    public void testPropertiesGetters() {
        CharacterProperties properties = factory.getProperties("Verdana", "Black", 16);
        assertEquals("Verdana", properties.getFont());
        assertEquals("Black", properties.getColor());
        assertEquals(16, properties.getSize());
    }

    @Test
    public void testPropertiesToString() {
        assertEquals("Calibri, Blue, 14", factory.getProperties("Calibri", "Blue", 14).toString());
    }
}
