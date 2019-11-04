package app;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
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
        File file = new File("src/main/resources/img/1.png");
        String localUrl = file.toURI().toString();
        Image image = new Image(localUrl);
        Mat sourceMat = ImageUtils.imageFXToMat(image);

        //Вытаскиваем фрагмет 32х32 и конвертируем его в одноканальный
        Mat subMat = sourceMat;//sourceMat.submat(0, 31, 0, 31);
        Imgproc.cvtColor(subMat, subMat, Imgproc.COLOR_BGR2GRAY);
        //Конвертируем в float тип
        Mat floatSubMat = new Mat();
        subMat.convertTo(floatSubMat, CvType.CV_32FC1);

        //Выполянем дискретное косинусное преобразование
        Mat resultMat = new Mat();
        Core.dct(floatSubMat, resultMat);

        ShowImage.show(ImageUtils.matToImageFX(resultMat));
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
