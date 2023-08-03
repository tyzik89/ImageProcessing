package com.work.vladimirs.klette_concise_computer_vision.lessons_1_4;

import com.work.vladimirs.utils.ImageUtils;
import com.work.vladimirs.utils.ShowImage;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class Lesson_1_4 extends Application {

    private static final String FILENAME = "src/main/resources/klette_concise_computer_vision/lessons_1_4/gagarin.png";

    @Override
    public void start(Stage primaryStage) {
        Mat img = Imgcodecs.imread(FILENAME);
        if (img.empty()) {
            System.out.println("Не удалось загрузить изображение");
            return;
        }
        ShowImage.show(ImageUtils.matToImageFX(img), "Оригинал");
        Mat imgGray = new Mat();
        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
// Вычисляем гистограммы по каналам
        ArrayList<Mat> images = new ArrayList<Mat>();
        images.add(img);
        images.add(imgGray);
        Mat histGray = new Mat();
        Mat histRed = new Mat();
        Mat histGreen = new Mat();
        Mat histBlue = new Mat();
        Imgproc.calcHist(images, new MatOfInt(3), new Mat(),
                histGray, new MatOfInt(256), new MatOfFloat(0, 256));
        Imgproc.calcHist(images, new MatOfInt(2), new Mat(),
                histRed, new MatOfInt(256), new MatOfFloat(0, 256));
        Imgproc.calcHist(images, new MatOfInt(1), new Mat(),
                histGreen, new MatOfInt(256), new MatOfFloat(0, 256));
        Imgproc.calcHist(images, new MatOfInt(0), new Mat(),
                histBlue, new MatOfInt(256), new MatOfFloat(0, 256));
// Нормализация диапазона
        Core.normalize(histGray, histGray, 0, 128, Core.NORM_MINMAX);
        Core.normalize(histRed, histRed, 0, 128, Core.NORM_MINMAX);
        Core.normalize(histGreen, histGreen, 0, 128, Core.NORM_MINMAX);
        Core.normalize(histBlue, histBlue, 0, 128, Core.NORM_MINMAX);
// Отрисовка гистограмм
        double v = 0;
        int h = 150;
        Mat imgHistRed = new Mat(h, 256, CvType.CV_8UC3, ImageUtils.COLOR_WHITE);
        Mat imgHistGreen = new Mat(h, 256, CvType.CV_8UC3, ImageUtils.COLOR_WHITE);
        Mat imgHistBlue = new Mat(h, 256, CvType.CV_8UC3, ImageUtils.COLOR_WHITE);
        Mat imgHistGray = new Mat(h, 256, CvType.CV_8UC3, ImageUtils.COLOR_WHITE);
        for (int i = 0, j = histGray.rows(); i < j; i++) {
            v = Math.round(histRed.get(i, 0)[0]);
            if (v != 0) {
                Imgproc.line(imgHistRed, new Point(i, h - 1),
                        new Point(i, h - 1 - v), ImageUtils.COLOR_RED);
            }
            v = Math.round(histGreen.get(i, 0)[0]);
            if (v != 0) {
                Imgproc.line(imgHistGreen, new Point(i, h - 1),
                        new Point(i, h - 1 - v), ImageUtils.COLOR_GREEN);
            }
            v = Math.round(histBlue.get(i, 0)[0]);
            if (v != 0) {
                Imgproc.line(imgHistBlue, new Point(i, h - 1),
                        new Point(i, h - 1 - v), ImageUtils.COLOR_BLUE);
            }
            v = Math.round(histGray.get(i, 0)[0]);
            if (v != 0) {
                Imgproc.line(imgHistGray, new Point(i, h - 1),
                        new Point(i, h - 1 - v), ImageUtils.COLOR_GRAY);
            }
        }
        ShowImage.show(ImageUtils.matToImageFX(imgHistRed), "Red");
        ShowImage.show(ImageUtils.matToImageFX(imgHistGreen), "Green");
        ShowImage.show(ImageUtils.matToImageFX(imgHistBlue), "Blue");
        ShowImage.show(ImageUtils.matToImageFX(imgHistGray), "Gray");
        img.release(); imgGray.release();
        imgHistRed.release(); imgHistGreen.release();
        imgHistBlue.release(); imgHistGray.release();
        histGray.release(); histRed.release();
        histGreen.release(); histBlue.release();
    }

    public static void main(String[] args) {
        // load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}
