package bg.sofia.uni.fmi.mjt.analyzer.server.retriever.request.barcode;

import com.google.zxing.NotFoundException;
import org.junit.jupiter.api.Test;

import javax.imageio.IIOException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BarcodeDecoderTest {
    @Test
    void testGetQrCodeWithValidImage() throws NotFoundException, IOException {
        String filePath = "test-barcode.gif";

        String expected = "009800800124";
        String actual = BarcodeDecoder.getQrCode(filePath);
        assertEquals(expected, actual, "Expected barcode is: " + expected + ", but was: " + actual);
    }

    @Test
    void testGetQrCodeWithIncorrectPath() {
        String filePath = "invalid-barcode.gif";

        assertThrows(IIOException.class, () -> BarcodeDecoder.getQrCode(filePath),
            "IIOException expected to be thrown!");
    }
}