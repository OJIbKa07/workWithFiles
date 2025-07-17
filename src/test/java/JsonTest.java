import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class JsonTest {
    private ClassLoader cl = JsonTest.class.getClassLoader();

    @TestFactory
    void testJsonStructure() throws Exception {
        JsonFactory factory = new JsonFactory();

        try (InputStream is = cl.getResourceAsStream("example.json");
             JsonParser parser = factory.createParser(is)) {

            Assertions.assertNotNull(is, "Файл sample.json не найден в ресурсах");

            Assertions.assertEquals(JsonToken.START_OBJECT, parser.nextToken());

            Assertions.assertEquals("array", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            int[] expectedArray = {1, 2, 3};
            int idx = 0;
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                Assertions.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.currentToken());
                Assertions.assertEquals(expectedArray[idx++], parser.getIntValue());
            }
            Assertions.assertEquals(expectedArray.length, idx);

            Assertions.assertEquals("boolean", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());

            Assertions.assertEquals("color", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.START_OBJECT, parser.nextToken());

            Assertions.assertEquals("main_color", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("yellow", parser.getText());

            Assertions.assertEquals("shade", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("lemony", parser.getText());

            Assertions.assertEquals(JsonToken.END_OBJECT, parser.nextToken());

            Assertions.assertEquals("null", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.VALUE_NULL, parser.nextToken());

            Assertions.assertEquals("number", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            Assertions.assertEquals(123, parser.getIntValue());

            Assertions.assertEquals("string", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("Hello World", parser.getText());

            Assertions.assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }
}
