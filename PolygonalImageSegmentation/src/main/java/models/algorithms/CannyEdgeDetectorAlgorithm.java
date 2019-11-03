package models.algorithms;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Алгоритм Кэнни по обнаружению границ состоит из четырём этапов:
 * 1) Размытие по Гауссу, для очистки любых пятен и шумов на изображении.
 * 2) Оператор градиента применяется, для получения интенсивности и направления градиента.
 * 3) Определение факта что пиксель является лучшим кандитатом на ребро, чем его соседи.
 * 4) Hysteresis thresholding находит где края начинаются и заканчиваются.
 */
public class CannyEdgeDetectorAlgorithm implements Algorithm{

    private final static Logger LOGGER = LoggerFactory.getLogger(CannyEdgeDetectorAlgorithm.class);

    private int sizeGaussFilter;
    private int threshold;
    private boolean needToUseL2Gradient = false;
    private int apertureSize = 3;

    public CannyEdgeDetectorAlgorithm(int sizeGaussFilter, int threshold) {
        this.sizeGaussFilter = sizeGaussFilter;
        this.threshold = threshold;
    }

    public CannyEdgeDetectorAlgorithm(int sizeGaussFilter, int threshold, boolean needToUseL2Gradient) {
        this(sizeGaussFilter, threshold);
        this.needToUseL2Gradient = needToUseL2Gradient;
    }

    public CannyEdgeDetectorAlgorithm(int sizeGaussFilter, int threshold, int apertureSize) {
        this(sizeGaussFilter, threshold);
        this.apertureSize = apertureSize;
    }

    public CannyEdgeDetectorAlgorithm(int sizeGaussFilter, int threshold, int apertureSize, boolean needToUseL2Gradient) {
        this(sizeGaussFilter, threshold);
        this.apertureSize = apertureSize;
        this.needToUseL2Gradient = needToUseL2Gradient;
    }

    public Mat doAlgorithm(Mat frame) {
        LOGGER.debug("Started processing");

        //Инициализация
        Mat grayMat = new Mat();
        Mat detectedEdgesMat = new Mat();

        //Конвертируем изображение в одноканальное
        Imgproc.cvtColor(frame, grayMat, Imgproc.COLOR_BGR2GRAY);

        //Применяем размытие по Гауссу
        GaussBlurAlgorithm gaussBlurAlgorithm = new GaussBlurAlgorithm(sizeGaussFilter);
        detectedEdgesMat = gaussBlurAlgorithm.doAlgorithm(grayMat);

        /*detectedEdges: Входное изображение, полутоновое
          detectedEdges: Выходная матрица
          threshold: Значение порога, заданное пользователем
          threshold * 3: Установка трёхкратного нижнего порога (following Canny’s recommendation)
          apertureSize(3): Размер ядра Собеля для внутреннего использования
          needToUseL2Gradient(false): флаг, указывающий на возможность использования более точного расчёта величины градиента.*/

        Imgproc.Canny(detectedEdgesMat, detectedEdgesMat, threshold,threshold * 3, apertureSize, needToUseL2Gradient);

        //Используя результат Кэнни в качестве маски - выводим результат
        //Создаём матрицу
        Mat dest = new Mat();
        //Заполняем её нулями. Получаем совершенно чёрное изображение
        Core.add(dest, Scalar.all(0), dest);
        frame.copyTo(dest, detectedEdgesMat);

        return dest;
    }
}
