package models.image;

import constants.NotifyConstants;
import fxelements.SingletonProcess;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.shape.Line;
import models.algorithms.*;
import models.notification.Observable;
import models.notification.Observer;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ImageUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Получает запрос на образобку от контроллеров
 * вызывает нужный алгоритм
 * получает результат работы
 * отправляет уведомление контроллерам о готовности
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
        // An image file on the hard drive.
//        File file = new File("C:/MyImages/myphoto.jpg");
        // --> file:/C:/MyImages/myphoto.jpg

        String localUrl = file.toURI().toString();
        Image image = new Image(localUrl);

//        Mat mat = Imgcodecs.imread("src/main/resources/img/image1.png", Imgcodecs.IMREAD_UNCHANGED);
//        Image image = ImageUtils.matToImageFX(mat);

        storageImages.init(image);

        notifyObservers(NotifyConstants.IMAGE_LOADED);
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

    public void doMakeBinary(int threshold, boolean isOtsu) {
        doMakeAlgorithm(new BinaryImageAlgorithm(threshold, isOtsu));
    }

    public void doCannyEdgeDetection(int sizeGaussFilter, int threshold, int sizeSobelKernel, boolean isUseL2Gradient) {
        doMakeAlgorithm(new CannyEdgeDetectorAlgorithm(sizeGaussFilter, threshold, sizeSobelKernel, isUseL2Gradient));
    }

    public void doHoughConversion(boolean typeHoughMethodClassic, double distance,  double angle, int threshold, double ... params) {
        doMakeAlgorithm(new HoughConversionAlgorithm(typeHoughMethodClassic, distance, angle, threshold, params));
    }

    public void doWatershedSegmentationManualMode(List<Line> lineList) {
        // Рисуем маркеры
        Mat matCurr = ImageUtils.imageFXToMat(storageImages.getCurrentImage());
        Mat mask = new Mat(matCurr.size(), CvType.CV_8UC1,  ImageUtils.COLOR_BLACK);

        for (Line line : lineList) {
            Imgproc.line(mask,
                    new Point(line.getStartX(), line.getStartY()), new Point(line.getEndX(), line.getEndY()),
                    ImageUtils.COLOR_WHITE, 3);
        }
        storageImages.setTempImage(ImageUtils.matToImageFX(mask));
        notifyObservers(NotifyConstants.TEMP_IMAGE_READY);

        doMakeAlgorithm(new WatershedSegmentation(mask));
    }

    public void doWatershedSegmentationAutoMode(Mat detectedLines) {
        Mat matCurr = ImageUtils.imageFXToMat(storageImages.getSourceImage());
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

    private synchronized void switchImagesOnNextStep(Image newImage) {
        storageImages.switchImagesOnNextStep(newImage);
        notifyObservers(NotifyConstants.IMAGE_READY);
    }
}
