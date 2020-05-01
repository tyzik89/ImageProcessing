package models.image;

import fxelements.SingletonProcess;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import models.algorithms.Algorithm;
import models.algorithms.*;
import models.notifications.Observable;
import models.notifications.Observer;
import models.notifications.constants.NotifyConstants;
import models.services.MarkersFormer;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ImageUtils;
import utils.ShowImage;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        storageMatrix.setSourceMat(file.toString());

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

    public Mat getSourceMat() {
        return storageMatrix.getSourceMat();
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

    public void doMakeBlur(int sizeGaussFilter) {
        doMakeAlgorithm(new GaussBlurAlgorithm(sizeGaussFilter));
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

    public void doWatershedSegmentationManualMode(Map<Color, List<Line>> colorListMap) {
        Mat matCurr = ImageUtils.imageFXToMat(storageImages.getCurrentImage());
        //Создание маркерного изображения для алгоритма водоразделов
        Mat markers = Mat.zeros(matCurr.size(), CvType.CV_32S);

        for (Map.Entry<Color, List<Line>> colorListEntry : colorListMap.entrySet()) {
            //Получаем набор линий каждого цвета
            Color currentColor = colorListEntry.getKey();
            Scalar currentColorScalarGray = ImageUtils.colorRGB2GRAY(currentColor);
            List<Line> currentLines = colorListEntry.getValue();

            // Рисуем маркеры
            Mat mask = new Mat(matCurr.size(), CvType.CV_8U,  ImageUtils.COLOR_BLACK);
            for (Line line : currentLines) {
                Imgproc.line(mask,
                        new Point(line.getStartX(), line.getStartY()), new Point(line.getEndX(), line.getEndY()),
                        currentColorScalarGray, 1);
            }

            // Находим контуры маркеров
            ArrayList<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(mask, contours, new Mat(),
                    Imgproc.RETR_CCOMP,
                    Imgproc.CHAIN_APPROX_SIMPLE);

            // Отрисовываем контуры нужным цветом
            for (int i = 0; i < contours.size(); i++) {
                Imgproc.drawContours(markers, contours, i, currentColorScalarGray, 1);
            }
        }

        doMakeAlgorithm(new WatershedSegmentation(markers, matCurr));
    }

    public void doWatershedSegmentationAutoMode(int type, double ... params) {
        Mat vectorOfStraightLines = StorageMatrix.getInstance().getMatrixOfLines();
        Mat matCurr = getSourceMat();
        Mat maskOfMarkers = new Mat();

        double distanceBetweenLineAndMarkers = params[0];
        double ratioReductionMarkers = params[1];

        MarkersFormer markersFormer = new MarkersFormer(vectorOfStraightLines, matCurr, distanceBetweenLineAndMarkers, ratioReductionMarkers);
        if (type == 1) {
            double maxDistBetweenParallelLines = params[2];
            maskOfMarkers = markersFormer.prepareMaskOfMarkersByGradient(maxDistBetweenParallelLines);

        } else if (type == 2) {
            double brithnessPixelsThresholdKMeans = params[2];
            int countIterationsKMeans = (int) params[3];
            int countClustersKMeans = (int) params[4];
            maskOfMarkers = markersFormer.prepareMaskOfMarkersByKMeans(brithnessPixelsThresholdKMeans, countIterationsKMeans, countClustersKMeans);

        } else if (type == 3) {
            maskOfMarkers = markersFormer.prepareMaskOfMarkersByBarChart();
        }

        //отображаем все маркеры на картинке
        showMarkersWithContours(maskOfMarkers, vectorOfStraightLines, matCurr);

        //Методом водоразделов выделяем сегменты
        doMakeAlgorithm(new WatershedSegmentation(maskOfMarkers, matCurr));
    }

    private void showMarkersWithContours(Mat maskOfMarkers, Mat vectorOfStraightLines, Mat sourceMat) {
        Mat markers = new Mat();
        maskOfMarkers.convertTo(markers, CvType.CV_8UC1);
//        ShowImage.show(ImageUtils.matToImageFX(markers), "Markers");
        //Результирующая матрица
        Mat result = new Mat(sourceMat.size(), CvType.CV_8UC1, ImageUtils.COLOR_BLACK);
        for (int i = 0, r = vectorOfStraightLines.rows(); i < r; i++) {
            for (int j = 0, c = vectorOfStraightLines.cols(); j < c; j++) {
                double[] line = vectorOfStraightLines.get(i, j);
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
        ShowImage.show(ImageUtils.matToImageFX(result), "LinesWithOurMarkers");
    }
}