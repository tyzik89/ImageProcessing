package models.algorithms;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Фильтр Гаусса используется для снижения уровня шумов и даёт размытие
 */
public class GaussBlurAlgorithm implements Algorithm{

    private final static Logger LOGGER = LoggerFactory.getLogger(GaussBlurAlgorithm.class);

    /**
     * Размер ядра фильтра
     */
    private int sizeGaussFilter;

    public GaussBlurAlgorithm(int sizeGaussFilter) {
        this.sizeGaussFilter = sizeGaussFilter;
    }

    /**
     * @param frame исходная матрица изображения
     * @return размытую матрицу
     */
    public Mat doAlgorithm(Mat frame) {
        LOGGER.debug("Started processing");

        if (sizeGaussFilter > 0)
            Imgproc.blur(frame, frame, new Size(sizeGaussFilter, sizeGaussFilter));
        return frame;
    }
}
