package models.image;

import constants.NotifyConstants;
import fxelements.SingletonProcess;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import models.algorithms.*;
import models.algorithms.Algorithm;
import models.notification.Observable;
import models.notification.Observer;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ImageUtils;

import java.io.File;
import java.util.*;

/**
 * 1. Получает запрос с параметрами от контроллеров
 * 2. Вызывает нужный алгоритм
 * 3. Получает результат работы алгоритма
 * 4. Отправляет уведомление контроллерам о выполнении запроса
 *
 * Работает с хранилищем изображений {@link StorageImages}
 * Работает с хранилищем матриц {@link StorageMatrix}
 */
public class ImagesHandler implements Observable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImagesHandler.class);

    private StorageImages storageImages;
    private StorageMatrix storageMatrix;
    private List<Observer> observers;

    public ImagesHandler() {
        this.storageImages = StorageImages.getInstance();
        this.storageMatrix = StorageMatrix.getInstance();
        //Список наблюдателей о результатах работы
        this.observers = new LinkedList<>();
    }

    public void load(File file) {
        String localUrl = file.toURI().toString();
        Image image = new Image(localUrl);

        storageImages.init(image);

        notifyObservers(NotifyConstants.IMAGE_LOADED);
    }

    /**
     * Общий метод запуска алгоритмов
     * @param algorithm нужный алгоритм
     */
    private void doMakeAlgorithm(Algorithm algorithm){
        /*Platform.runLater: если вам нужно обновить компонент GUI из потока, не являющегося GUI, вы можете использовать его,
        чтобы поместить свое обновление в очередь, и оно будет обработано потоком GUI как можно скорее.

        Task реализует интерфейс Worker который используется, когда вам нужно запустить длинную задачу вне потока GUI
        (чтобы избежать зависания вашего приложения), но все же необходимо взаимодействовать с GUI на некотором этапе.*/

        Task task = new Task<Void>() {
            @Override public Void call() {
                Mat mat = ImageUtils.imageFXToMat(storageImages.getCurrentImage());
                updateProgress(0.3, 1.0);
                Mat result = algorithm.doAlgorithm(mat);
                updateProgress(0.8, 1.0);
                switchImagesOnNextStep(ImageUtils.matToImageFX(result));
                updateProgress(1.0, 1.0);
                return null;
            }
        };
        SingletonProcess.getInstance().getProgressBar().progressProperty().bind(task.progressProperty());
        SingletonProcess.getInstance().getProgressIndicator().progressProperty().bind(task.progressProperty());
        //Запускаем обработку алгоритма в отдельном потоке
        new Thread(task).start();
    }

    private synchronized void switchImagesOnNextStep(Image newImage) {
        storageImages.switchImagesOnNextStep(newImage);
        notifyObservers(NotifyConstants.IMAGE_READY);
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.notification(message);
        }
    }

    public Image getCurrentImage() {
        return storageImages.getCurrentImage();
    }

    public Image getSourceImage() {
        return storageImages.getSourceImage();
    }

    public Image getTempImage() {
        return storageImages.getTempImage();
    }

    /**
     * Отмена алгоритма, т.е. возврат предыдущего состояния объекта Image
     * Возврат алгоритма, т.е. возврат обработанного состояния объекта Image
     */
    public void doCancelRedo() {
        Image temp = storageImages.getCurrentImage();
        storageImages.setCurrentImage(storageImages.getPreviousImage());
        storageImages.setPreviousImage(temp);
        notifyObservers(NotifyConstants.IMAGE_READY);
    }

/*====================================================================================================================*/

    public void doMakeBinary(int threshold, boolean isOtsu) {
        doMakeAlgorithm(new BinaryImageAlgorithm(threshold, isOtsu));
    }

    public void doCannyEdgeDetection(int sizeGaussFilter, int threshold, int sizeSobelKernel, boolean isUseL2Gradient) {
        doMakeAlgorithm(new CannyEdgeDetectorAlgorithm(sizeGaussFilter, threshold, sizeSobelKernel, isUseL2Gradient));
    }

    public void doHoughConversion(boolean typeHoughMethodClassic, double distance,  double angle, int threshold, double ... params) {
        doMakeAlgorithm(new HoughConversionAlgorithm(typeHoughMethodClassic, distance, angle, threshold, params));
    }

    public void doWatershedSegmentationManualMode(Map<Color, List<Line>> colorListMap) {
        Mat matCurr = ImageUtils.imageFXToMat(storageImages.getCurrentImage());
        //Создание маркерного изображения для алгоритма водоразделов
        Mat markers = Mat.zeros(matCurr.size(), CvType.CV_32S);

        //Для рисования цветной заливки областей. Цвет и список контуров этого цвета
        Map<Scalar, ArrayList<MatOfPoint>> scalarContoursMap = this.convertColor2ScalarBetweenMap(colorListMap);

        for (Map.Entry<Color, List<Line>> colorListEntry : colorListMap.entrySet()) {
            //Получаем набор линий каждого цвета
            Color currentColor = colorListEntry.getKey();
            Scalar currentColorScalar = ImageUtils.colorRGB2GRAY(currentColor);
            List<Line> currentLines = colorListEntry.getValue();

            // Рисуем маркеры
            Mat mask = new Mat(matCurr.size(), CvType.CV_8U,  ImageUtils.COLOR_BLACK);
            for (Line line : currentLines) {
                Imgproc.line(mask,
                        new Point(line.getStartX(), line.getStartY()), new Point(line.getEndX(), line.getEndY()),
                        currentColorScalar, 1);
            }

            // Находим контуры маркеров
            ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(mask, contours, new Mat(),
                    Imgproc.RETR_CCOMP,
                    Imgproc.CHAIN_APPROX_SIMPLE);

            // Отрисовываем контуры нужным цветом
            for (int i = 0; i < contours.size(); i++) {
                Imgproc.drawContours(markers, contours, i, currentColorScalar, 1);
            }

            ArrayList<MatOfPoint> matOfPoints = scalarContoursMap.get(currentColorScalar);
            matOfPoints.addAll(contours);
            scalarContoursMap.put(currentColorScalar, matOfPoints);
        }

//        storageImages.setTempImage(ImageUtils.matToImageFX(mask));
//        notifyObservers(NotifyConstants.TEMP_IMAGE_READY);

        doMakeAlgorithm(new WatershedSegmentation(markers));
    }

    /**
     * Конфертация цвета Color.JavaFx --> Scalar.OpenCV
     *
     * @param colorListMap карта с цветами javafx и списком объектов Line
     * @return карта с ключами Scalar цвета и пустыми значениями контуров
     */
    private Map<Scalar, ArrayList<MatOfPoint>> convertColor2ScalarBetweenMap(Map<Color, List<Line>> colorListMap) {
        Map<Scalar, ArrayList<MatOfPoint>> scalarPointsMap = new HashMap<>();

        for (Map.Entry<Color, List<Line>> colorListEntry : colorListMap.entrySet()) {
            Color currentColor = colorListEntry.getKey();
            scalarPointsMap.put(ImageUtils.colorRGB(currentColor), new ArrayList<MatOfPoint>());
        }
        return scalarPointsMap;
    }


    public void doWatershedSegmentationAutoMode(Mat detectedLines) {
        Mat matCurr = ImageUtils.imageFXToMat(this.getSourceImage());
        Mat mask = new Mat(matCurr.size(), CvType.CV_8UC1,  ImageUtils.COLOR_BLACK);

        for (int i = 0, r = detectedLines.rows(); i < r; i++) {
            for (int j = 0, c = detectedLines.cols(); j < c; j++) {
                double[] line = detectedLines.get(i, j);
                Imgproc.line(
                        mask,
                        new Point(line[0], line[1]), new Point(line[2], line[3]),
                        ImageUtils.COLOR_WHITE,3);
                break;
            }
            break;
        }

        storageImages.setTempImage(ImageUtils.matToImageFX(mask));
        notifyObservers(NotifyConstants.TEMP_IMAGE_READY);
    }

    public void doMakeBlur(int sizeGaussFilter) {
        doMakeAlgorithm(new GaussBlurAlgorithm(sizeGaussFilter));
    }
}
