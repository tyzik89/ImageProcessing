package models.algorithms;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ImageUtils;
import utils.ShowImage;

/**
 * Выравнивание фона изображения
 *
 * Состоит из двух операций:
 * 1) Дилатация (размыкание) - расширение светлых областей и сужение тёмных
 * 2) Вычитание из изображения результата шага 1
 */
public class TopHat implements Algorithm {

    private final static Logger LOGGER = LoggerFactory.getLogger(TopHat.class);

    //Размер ядра, по умолчанию - 3х3
    private Size kSize;
    //Форма ядра, по умолчанию - 0
    // MORPH_RECT = 0,
    // MORPH_CROSS = 1,
    // MORPH_ELLIPSE = 2;
    private int shape;

    public TopHat() {
        this.kSize = new Size(3.0, 3.0);
        this.shape = Imgproc.MORPH_RECT;
    }

    public TopHat(Size kSize) {
        this.kSize = kSize;
        this.shape = Imgproc.MORPH_RECT;
    }

    public TopHat(Size kSize, int shape) {
        this.kSize = kSize;
        this.shape = shape;
    }

    @Override
    public Mat doAlgorithm(Mat frame) {
        LOGGER.debug("Started processing");
        if (frame.channels() != 3 || frame.channels() != 1) {
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGRA2BGR);
        }
        Mat result = new Mat();
        Mat kernel = Imgproc.getStructuringElement(shape, kSize);
        Mat resultDilate = new Mat();
        //Первый шаг - операция дилатации
        Imgproc.dilate(frame, resultDilate, kernel);
        //Второй шаг - операция вычитания
        Core.subtract(resultDilate, frame, result);
        return result;
    }
}
