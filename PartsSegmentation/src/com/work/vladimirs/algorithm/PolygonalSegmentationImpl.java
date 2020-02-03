package com.work.vladimirs.algorithm;

import com.work.vladimirs.algorithm.methods.Algorithm;
import com.work.vladimirs.algorithm.methods.CannyEdgeDetectorAlgorithm;
import com.work.vladimirs.algorithm.methods.HoughConversionAlgorithm;
import com.work.vladimirs.algorithm.methods.WatershedSegmentation;
import com.work.vladimirs.utils.ImageUtils;
import com.work.vladimirs.utils.ShowImage;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class PolygonalSegmentationImpl extends PolygonalSegmentation {

    public PolygonalSegmentationImpl(String pathname, String filename) {
        setPathname(pathname);
        setFilename(filename);
    }

    @Override
    public void run() {
        loadImage(getPathname(), getFilename());

        Algorithm algorithm;
        //Применяем метод Кэнни
        algorithm = new CannyEdgeDetectorAlgorithm(3, 150,3, false);
        Mat bordersHighlight = algorithm.doAlgorithm(getSourceMat());
        //ShowImage.show(ImageUtils.matToImageFX(bordersHighlight), "Canny");

        //Применяем метод Хафа и находим все прямые линии на картинке
        algorithm = new HoughConversionAlgorithm(false, 0.1, 0.1, 8, 0, 0);
        Mat vectorOfLines = algorithm.doAlgorithm(bordersHighlight);

        //Класс формирующий маску с маркерами.
        MarkersFormer markersFormer = new MarkersFormer(vectorOfLines, getSourceMat());
        Mat maskOfMarkers = markersFormer.prepareMaskOfMarkers();
        //отображаем все маркеры на картинке
        Mat markers = new Mat();
        maskOfMarkers.convertTo(markers, CvType.CV_8U);
        ShowImage.show(ImageUtils.matToImageFX(markers), "Markers");

        //Методом водоразделов выделяем сегменты
        algorithm = new WatershedSegmentation(maskOfMarkers);
        Mat result = algorithm.doAlgorithm(getSourceMat());
        ShowImage.show(ImageUtils.matToImageFX(result), "Watershed");
    }
}
