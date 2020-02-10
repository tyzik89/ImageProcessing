package com.work.vladimirs.algorithm;

import com.work.vladimirs.algorithm.methods.Algorithm;
import com.work.vladimirs.algorithm.methods.CannyEdgeDetectorAlgorithm;
import com.work.vladimirs.algorithm.methods.HoughConversionAlgorithm;
import com.work.vladimirs.algorithm.methods.WatershedSegmentation;
import com.work.vladimirs.utils.ImageUtils;
import com.work.vladimirs.utils.ShowImage;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class PolygonalSegmentationImpl extends PolygonalSegmentation {

    public PolygonalSegmentationImpl() {
        setPathname("src/resources/");
        setFilename("test_image_3.bmp");
    }



    @Override
    public void run() {
        loadImage(getPathname(), getFilename());

        Algorithm algorithm;
        //Применяем метод Кэнни
        algorithm = new CannyEdgeDetectorAlgorithm(3, 50,3, false);
        Mat bordersHighlight = algorithm.doAlgorithm(getSourceMat());
        ShowImage.show(ImageUtils.matToImageFX(bordersHighlight), "Canny");

        //Применяем метод Хафа и находим все прямые линии на картинке
        algorithm = new HoughConversionAlgorithm(false, 0.1, 0.1, 25, 5, 0);
        Mat vectorOfLines = algorithm.doAlgorithm(bordersHighlight);

        //Класс формирующий маску с маркерами.
        MarkersFormer markersFormer = new MarkersFormer(vectorOfLines, getSourceMat());
        Mat maskOfMarkers = markersFormer.prepareMaskOfMarkers();
        //отображаем все маркеры на картинке
        showMarkersWithContours(maskOfMarkers, vectorOfLines);

        //Методом водоразделов выделяем сегменты
        algorithm = new WatershedSegmentation(maskOfMarkers);
        Mat result = algorithm.doAlgorithm(getSourceMat());
        ShowImage.show(ImageUtils.matToImageFX(result), "Watershed");
    }

    private void showMarkersWithContours(Mat maskOfMarkers, Mat vectorOfLines) {
        Mat markers = new Mat();
        maskOfMarkers.convertTo(markers, CvType.CV_8UC1);
        //ShowImage.show(ImageUtils.matToImageFX(markers), "Markers");
        //Результирующая матрица
        Mat result = new Mat(getSourceMat().size(), CvType.CV_8UC1, ImageUtils.COLOR_BLACK);
        for (int i = 0, r = vectorOfLines.rows(); i < r; i++) {
            for (int j = 0, c = vectorOfLines.cols(); j < c; j++) {
                double[] line = vectorOfLines.get(i, j);
                Imgproc.line(
                        result,
                        new Point(line[0], line[1]),
                        new Point(line[2], line[3]),
                        new Scalar(80, 80, 80),
                        1,
                        4);
            }
        }
        markers.copyTo(result, markers);
        ShowImage.show(ImageUtils.matToImageFX(result), "Markers");
    }
}
