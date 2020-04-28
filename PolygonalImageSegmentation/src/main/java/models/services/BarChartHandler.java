package models.services;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import utils.ImageUtils;
import utils.ShowImage;

import java.util.ArrayList;

public class BarChartHandler {

    private Mat originalMat;

    public BarChartHandler(Mat originalMat) {
        this.originalMat = originalMat;
    }

    public Mat createBarChart(Mat maskWithMarker) {
        //Получаем оригинальную матрицу в градациях серого
        Mat grayOriginalMat = new Mat();
        Imgproc.cvtColor(originalMat, grayOriginalMat, Imgproc.COLOR_BGR2GRAY);
        //В массив изображений добавляем оригинальную матрицу в градациях серого
        ArrayList<Mat> images = new ArrayList<Mat>();
        images.add(grayOriginalMat);
        // Вычисляем гистограммы по единственному серому каналу
        Mat histGray = new Mat();
        //Вычисляем гистограмму
        Imgproc.calcHist(images, new MatOfInt(0), maskWithMarker, histGray, new MatOfInt(256), new MatOfFloat(0, 256));
        // Нормализация диапазона
        Core.normalize(histGray, histGray, 0, 128, Core.NORM_MINMAX);
        // Отрисовка гистограмм
        double v = 0;
        int h = 150;
        //Палитра белого цвета для отображения гистограммы
        Mat imgHistGray = new Mat(h, 256, CvType.CV_8UC1, ImageUtils.COLOR_WHITE);
        for (int i = 0, j = histGray.rows(); i < j; i++) {
            v = Math.round(histGray.get(i, 0)[0]);
            if (v != 0) {
                Imgproc.line(imgHistGray, new Point(i, h - 1),
                        new Point(i, h - 1 - v), ImageUtils.COLOR_BLACK);
            }
        }
        ShowImage.show(ImageUtils.matToImageFX(imgHistGray), "BarChart");

//        System.out.println(histGray.dump());
        return histGray;
    }

}
