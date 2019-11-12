package app;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import utils.ImageUtils;
import utils.ShowImage;

import java.io.File;
import java.util.ArrayList;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        File file = new File("src/main/resources/img/nature.png");
        String localUrl = file.toURI().toString();
        Image image = new Image(localUrl);

        Mat sourceMat = ImageUtils.imageFXToMat(image);

        Algorithm algorithm = new Algorithm();

        //Разбиваем изображение на сегменты
        ArrayList<Mat> segments = algorithm.doSegmentation(sourceMat, 8);

        for (Mat segment : segments) {
            //Каждый сегмент переводим в градацию серого
            Mat segmentGray = algorithm.doGrayscale(segment);
            //Бинаризируем сегмет
            Mat segmentBinary = algorithm.doBinary(segmentGray);
            //Получаем хэш-код каждого сегмента и встраиваем этот хэш код в изображение
            algorithm.doSteganography(segmentBinary, segment);

            break;
            //ShowImage.show(ImageUtils.matToImageFX(segmentBinary));
        }
    }

    public static void main(String[] args) throws Exception {
        // load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}
