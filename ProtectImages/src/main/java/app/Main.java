package app;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import utils.ColorScaleUtils;
import utils.ImageUtils;
import utils.ShowImage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        File file = new File("src/main/resources/img/example.jpg");
        String localUrl = file.toURI().toString();
        Image image = new Image(localUrl);
        Mat sourceMat = ImageUtils.imageFXToMat(image);
        Mat grayMat = ColorScaleUtils.doGrayscale(sourceMat);
        Mat binaryMat = ColorScaleUtils.doBinary(grayMat);

/*        ShowImage.show(ImageUtils.matToImageFX(sourceMat));
        ShowImage.show(ImageUtils.matToImageFX(grayMat));
        ShowImage.show(ImageUtils.matToImageFX(binaryMat));*/

        System.out.println(sourceMat.get(0, 0).length);
/*        System.out.println(sourceMat.get(0, 1)[0]);
        System.out.println(sourceMat.get(0, 2)[0]);
        System.out.println(sourceMat.get(0, 3)[0]);
        System.out.println(sourceMat.get(0, 4)[0]);
        System.out.println(sourceMat.get(0, 5)[0]);
        System.out.println(sourceMat.get(0, 6)[0]);
        System.out.println(sourceMat.get(0, 7)[0]);*/

        /*int red = 0, green = 0, blue = 0, r = 0, g = 0, b = 0;
        byte[] bytes = new byte[sourceMat.cols() * sourceMat.rows() * sourceMat.channels()];
        sourceMat.get(0, 0, bytes);
        for (int i = 0, j = bytes.length; i < j; i+=sourceMat.channels()) {
            b = bytes[i] & 0xFF;
            g = bytes[i + 1] & 0xFF;
            r = bytes[i + 2] & 0xFF;
            System.out.println(b + " " + g + " " + r);

            // now, clear the least significant bit (LSB) from each pixel element
            b = bytes[i] - bytes[i] % 2;
            System.out.println(Integer.toBinaryString(bytes[i]));*/
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        /*String text = "lol";
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getEncoder().encodeToString(hash);
        System.out.println(encoded);*/

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}
