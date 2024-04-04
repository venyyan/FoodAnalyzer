package bg.sofia.uni.fmi.mjt.analyzer.server.retriever.request.barcode;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BarcodeDecoder {
    public static String getQrCode(String filePath) throws IOException, NotFoundException {
        BinaryBitmap bitmap = getBitmap(filePath);
        MultiFormatReader reader = new MultiFormatReader();
        return reader.decode(bitmap).toString();
    }

    private static BinaryBitmap getBitmap(String filePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(filePath));
        LuminanceSource luminanceSource = new BufferedImageLuminanceSource(image);
        HybridBinarizer binarizer = new HybridBinarizer(luminanceSource);
        return new BinaryBitmap(binarizer);
    }
}
