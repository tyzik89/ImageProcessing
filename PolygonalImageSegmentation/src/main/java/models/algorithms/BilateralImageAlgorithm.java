package models.algorithms;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Билатеральный фильтр - двустороннее сглаживание. Удаление шума и сглаживание границ объектов.
 *
 * Сигма значения: для простоты вы можете установить 2 значения сигмы одинаковыми.
 * Если они маленькие (<10), фильтр не будет иметь большого эффекта, тогда как если они большие (> 150), они будут иметь очень сильный эффект, делая изображение мультяшным.
 */
public class BilateralImageAlgorithm implements Algorithm {

    private final static Logger LOGGER = LoggerFactory.getLogger(BilateralImageAlgorithm.class);

    //Диаметр каждого пикселя окрестности, которая используется в процессе фильтрации. Если значение не является положительным, оно вычисляется из sigmaSpace
    private int pixelNeighborhoodDiameter;
    //Определяет стандартное отклонение в цветовом пространстве
    private double sigmaColor;
    //Стандартное отклонение в пространстве координат
    private double sigmaSpace;

    public BilateralImageAlgorithm(int pixelNeighborhoodDiameter, double sigmaColor, double sigmaSpace) {
        this.pixelNeighborhoodDiameter = pixelNeighborhoodDiameter;
        this.sigmaColor = sigmaColor;
        this.sigmaSpace = sigmaSpace;
    }

    @Override
    public Mat doAlgorithm(Mat frame) {
        LOGGER.debug("Started processing");
        if (frame.channels() != 3 || frame.channels() != 1) {
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGRA2BGR);
        }
        LOGGER.debug(" frame.channels: {}, frame.depth: {}, frame.type: {}", frame.channels(), frame.depth(), CvType.typeToString(frame.type()));
        Mat resultImg = new Mat();
        LOGGER.debug("pixelNeighborhoodDiameter: {}, sigmaColor: {}, sigmaSpace: {}",pixelNeighborhoodDiameter, sigmaColor , sigmaSpace);

        Imgproc.bilateralFilter(frame, resultImg, pixelNeighborhoodDiameter, sigmaColor, sigmaSpace, Core.BORDER_DEFAULT);
        return resultImg;
    }
}
