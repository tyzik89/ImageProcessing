package app;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import utils.ImageUtils;

import java.io.File;
import java.util.ArrayList;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        File file = new File("src/main/resources/img/order.jpg");
        String localUrl = file.toURI().toString();
        Image image = new Image(localUrl);

        Mat sourceMat = ImageUtils.imageFXToMat(image);

        Algorithm algorithm = new Algorithm();

        //Разбиваем изображение на сегменты
        ArrayList<Mat> segments = algorithm.doSegmentation(sourceMat, 8);
        ArrayList<Mat> segmentsWithHash = new ArrayList<>();

        for (Mat originalSegment : segments) {
            //Каждый сегмент переводим в градацию серого
            Mat segmentGray = algorithm.doGrayscale(originalSegment);
            //Бинаризируем сегмет
            Mat segmentBinary = algorithm.doBinary(segmentGray);
            //Получаем хэш-код каждого сегмента и встраиваем этот хэш код в сегмент
            //ShowImage.show(ImageUtils.matToImageFX(originalSegment));
            algorithm.doSteganography(segmentBinary, originalSegment);
            //ShowImage.show(ImageUtils.matToImageFX(originalSegment));
           // break;
        }

        //Собираем изображение из сегментов со встроеным хэш-кодом
        algorithm.doSynthesis(segments, sourceMat);
        image = ImageUtils.matToImageFX(sourceMat);
        //ShowImage.show(image);
    }

    public static void main(String[] args) throws Exception {
        // load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}
