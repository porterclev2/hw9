package Flyweight;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FlyweightTest {

    CharacterPropertiesFactory factory = new CharacterPropertiesFactory();
    Document document = new Document(factory);

    @TempDir
    Path tempDir;

    @BeforeEach
    public void createHelloWorldCS5800() {
        document.addText("Hello", "Arial", "Red", 12);
        document.addText("World", "Calibri", "Blue", 14);
        document.addText("CS", "Verdana", "Black", 16);
        document.addText("5800", "Arial", "Blue", 16);
    }

    private String documentPath() {
        return tempDir.resolve("HelloWorldCS5800.txt").toString();
    }

    @Test
    public void testFactoryReturnsSharedProperties() {
        assertSame(factory.getProperties("Arial", "Red", 12), factory.getProperties("Arial", "Red", 12));
        assertEquals(4, factory.getPropertiesCount());
    }

    @Test
    public void testFactoryCreatesNewPropertiesWhenAnyFieldDiffers() {
        CharacterProperties arialRed12 = factory.getProperties("Arial", "Red", 12);
        assertNotSame(arialRed12, factory.getProperties("Verdana", "Red", 12));
        assertNotSame(arialRed12, factory.getProperties("Arial", "Blue", 16));
        assertNotSame(factory.getProperties("Arial", "Blue", 16), factory.getProperties("Verdana", "Black", 16));
        assertEquals(5, factory.getPropertiesCount());
    }

    @Test
    public void testGetPropertiesCount() {
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

    @Test
    public void testDocumentCharacterGetters() {
        DocumentCharacter character = document.getCharacter(0);
        assertEquals('H', character.getValue());
        assertSame(factory.getProperties("Arial", "Red", 12), character.getProperties());
    }

    @Test
    public void testDocumentCharacterSetters() {
        DocumentCharacter character = new DocumentCharacter('W', factory.getProperties("Calibri", "Blue", 14));
        character.setValue('C');
        character.setProperties(factory.getProperties("Arial", "Red", 12));
        assertEquals('C', character.getValue());
        assertSame(factory.getProperties("Arial", "Red", 12), character.getProperties());
    }

    @Test
    public void testDocumentCharacterToString() {
        assertEquals("'H' [Arial, Red, 12]", document.getCharacter(0).toString());
        assertEquals("'5' [Arial, Blue, 16]", document.getCharacter(12).toString());
    }

    @Test
    public void testHelloWorldCS5800() {
        assertEquals("HelloWorldCS5800", document.getText());
        assertEquals(16, document.length());
        assertEquals(4, factory.getPropertiesCount());
    }

    @Test
    public void testAddCharacter() {
        document.addCharacter('!', "Verdana", "Red", 12);
        assertEquals("HelloWorldCS5800!", document.getText());
        assertSame(factory.getProperties("Verdana", "Red", 12), document.getCharacter(16).getProperties());
    }

    @Test
    public void testAddTextSharesProperties() {
        assertSame(document.getCharacter(0).getProperties(), document.getCharacter(4).getProperties());
        assertSame(document.getCharacter(5).getProperties(), document.getCharacter(9).getProperties());
        assertSame(document.getCharacter(10).getProperties(), document.getCharacter(11).getProperties());
        assertSame(document.getCharacter(12).getProperties(), document.getCharacter(15).getProperties());
        assertNotSame(document.getCharacter(0).getProperties(), document.getCharacter(5).getProperties());
    }

    @Test
    public void testInsertCharacter() {
        document.insertCharacter(5, ' ', "Calibri", "Blue", 14);
        assertEquals("Hello WorldCS5800", document.getText());
        assertSame(factory.getProperties("Calibri", "Blue", 14), document.getCharacter(5).getProperties());
        assertEquals(4, factory.getPropertiesCount());
    }

    @Test
    public void testEditCharacter() {
        document.editCharacter(0, 'J');
        assertEquals("JelloWorldCS5800", document.getText());
        assertSame(factory.getProperties("Arial", "Red", 12), document.getCharacter(0).getProperties());
    }

    @Test
    public void testEditPropertiesOnlyChangesOneCharacter() {
        document.editProperties(5, "Verdana", "Red", 12);
        assertEquals("'W' [Verdana, Red, 12]", document.getCharacter(5).toString());
        assertEquals("'o' [Calibri, Blue, 14]", document.getCharacter(6).toString());
        assertEquals(5, factory.getPropertiesCount());
    }

    @Test
    public void testEditPropertiesReusesExistingProperties() {
        document.editProperties(10, "Arial", "Red", 12);
        assertSame(document.getCharacter(0).getProperties(), document.getCharacter(10).getProperties());
        assertEquals(4, factory.getPropertiesCount());
    }

    @Test
    public void testDriverEdits() {
        document.editProperties(5, "Verdana", "Red", 12);
        document.editProperties(10, "Arial", "Red", 12);
        assertEquals("HelloWorldCS5800", document.getText());
        assertEquals(5, factory.getPropertiesCount());
    }

    @Test
    public void testRemoveCharacterKeepsProperties() {
        document.removeCharacter(11);
        document.removeCharacter(10);
        assertEquals("HelloWorld5800", document.getText());
        assertEquals(4, factory.getPropertiesCount());
    }

    @Test
    public void testGetCharacter() {
        assertEquals('W', document.getCharacter(5).getValue());
        assertEquals('0', document.getCharacter(15).getValue());
    }

    @Test
    public void testLength() {
        assertEquals(16, document.length());
        document.removeCharacter(0);
        assertEquals(15, document.length());
        assertEquals(0, new Document(factory).length());
    }

    @Test
    public void testGetText() {
        assertEquals("HelloWorldCS5800", document.getText());
        assertEquals("", new Document(factory).getText());
    }

    @Test
    public void testInvalidIndexThrowsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> document.getCharacter(16));
        assertThrows(IndexOutOfBoundsException.class, () -> document.editCharacter(-1, 'x'));
        assertThrows(IndexOutOfBoundsException.class, () -> document.editProperties(16, "Arial", "Red", 12));
        assertThrows(IndexOutOfBoundsException.class, () -> document.removeCharacter(16));
    }

    @Test
    public void testPrint() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        try {
            document.print();
        } finally {
            System.setOut(originalOut);
        }
        String[] lines = output.toString().split(System.lineSeparator());
        assertEquals(16, lines.length);
        assertEquals("'H' [Arial, Red, 12]", lines[0]);
        assertEquals("'W' [Calibri, Blue, 14]", lines[5]);
        assertEquals("'C' [Verdana, Black, 16]", lines[10]);
        assertEquals("'5' [Arial, Blue, 16]", lines[12]);
    }

    @Test
    public void testSave() throws IOException {
        document.save(documentPath());
        List<String> lines = Files.readAllLines(Path.of(documentPath()));
        assertEquals(16, lines.size());
        assertEquals("72\tArial\tRed\t12", lines.get(0));
        assertEquals("87\tCalibri\tBlue\t14", lines.get(5));
        assertEquals("48\tArial\tBlue\t16", lines.get(15));
    }

    @Test
    public void testSaveAndLoad() throws IOException {
        document.editProperties(5, "Verdana", "Red", 12);
        document.editProperties(10, "Arial", "Red", 12);
        document.save(documentPath());

        Document loaded = Document.load(documentPath(), new CharacterPropertiesFactory());
        assertEquals("HelloWorldCS5800", loaded.getText());
        for(int i = 0; i < document.length(); i++) {
            assertEquals(document.getCharacter(i).toString(), loaded.getCharacter(i).toString());
        }
    }

    @Test
    public void testLoadSharesProperties() throws IOException {
        document.save(documentPath());
        CharacterPropertiesFactory loadFactory = new CharacterPropertiesFactory();
        Document loaded = Document.load(documentPath(), loadFactory);
        assertEquals(4, loadFactory.getPropertiesCount());
        assertSame(loaded.getCharacter(0).getProperties(), loaded.getCharacter(4).getProperties());
    }

    @Test
    public void testLoadWithSameFactoryReusesProperties() throws IOException {
        document.editProperties(5, "Verdana", "Red", 12);
        document.editProperties(10, "Arial", "Red", 12);
        document.save(documentPath());

        Document loaded = Document.load(documentPath(), factory);
        assertEquals(5, factory.getPropertiesCount());
        assertSame(document.getCharacter(0).getProperties(), loaded.getCharacter(0).getProperties());
    }

    @Test
    public void testSaveAndLoadSpecialCharacters() throws IOException {
        document.insertCharacter(5, '\t', "Calibri", "Blue", 14);
        document.insertCharacter(11, '\n', "Verdana", "Black", 16);
        document.save(documentPath());

        assertEquals("Hello\tWorld\nCS5800", Document.load(documentPath(), factory).getText());
    }

    @Test
    public void testSaveAndLoadEmptyDocument() throws IOException {
        new Document(factory).save(documentPath());
        assertEquals(0, Document.load(documentPath(), factory).length());
    }

    @Test
    public void testLoadInvalidFileThrowsException() throws IOException {
        Path path = Path.of(documentPath());
        Files.write(path, List.of("72\tArial\tRed"));
        Exception exception = assertThrows(IOException.class, () -> {
            Document.load(documentPath(), factory);
        });
        assertEquals("Invalid line in " + path + ": 72\tArial\tRed", exception.getMessage());

        Files.write(path, List.of("H\tArial\tRed\t12"));
        assertThrows(IOException.class, () -> Document.load(documentPath(), factory));
    }

    @Test
    public void testLoadMissingFileThrowsException() {
        assertThrows(IOException.class, () -> Document.load(documentPath(), factory));
    }
}
