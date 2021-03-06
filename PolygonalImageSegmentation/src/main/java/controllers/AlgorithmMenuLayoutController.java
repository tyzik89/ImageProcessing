package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.image.ImagesHandler;
import models.notifications.Observer;
import models.notifications.constants.NotifyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Извлечение всех параметров, введённых пользователм, из UI
 */
public class AlgorithmMenuLayoutController implements Observer {

    private final static Logger LOGGER = LoggerFactory.getLogger(AlgorithmMenuLayoutController.class);

    //Ref on model class - ImagesHandler
    private ImagesHandler imagesHandler;

    @FXML
    private TitledPane commonTools;
    @FXML
    private TitledPane cannyTools;
    @FXML
    private TitledPane houghTools;
    @FXML
    private TitledPane watershedTools;
    @FXML
    private Slider binarySlider;
    @FXML
    private Label showBinaryThreshold;
    @FXML
    private CheckBox otsu;
    @FXML
    private Slider sizeGaussFilterCommon;
    @FXML
    private Label showSizeGaussFilterCommon;
    @FXML
    public Spinner sigmaSpace;
    @FXML
    public Spinner sigmaColor;
    @FXML
    public Spinner pixelNeighborhoodDiameter;
    @FXML
    public Spinner topHatKernelSize;
    @FXML
    private Slider thresholdCanny;
    @FXML
    private Label showThresholdCanny;
    @FXML
    private TextField sizeGaussFilter;
    @FXML
    private CheckBox gradientL2;
    @FXML
    private Spinner sizeSobelKernel;
    @FXML
    private ToggleGroup toggleGroupHough;
    @FXML
    private RadioButton radiobHoughProbably;
    @FXML
    private RadioButton radiobHoughClassic;
    @FXML
    private TitledPane titledPaneHoughProbably;
    @FXML
    private TitledPane titledPaneHoughClassic;
    @FXML
    private Label thresholdHoughLabel;
    @FXML
    private Spinner spinnerHoughAngle;
    @FXML
    private Spinner spinnerHoughDistance;
    @FXML
    private Slider thresholdHoughSlider;
    @FXML
    private TextField maxThetaTextField;
    @FXML
    private TextField minThetaTextField;
    @FXML
    private TextField stnTextField;
    @FXML
    private TextField srnTextField;
    @FXML
    private Spinner spinnerHoughProbablyMaxLineGap;
    @FXML
    private Spinner spinnerHoughProbablyMinLineLength;
    @FXML
    private ToggleButton watershedMode;

    //Формирование маркеров для алгоритма водоразделов
    @FXML
    private RadioButton radiobGradient;
    @FXML
    private RadioButton radiobKMeans;
    @FXML
    private RadioButton radiobHistogramm;

    @FXML
    private ToggleGroup toggleGroupMarker;

    @FXML
    public TitledPane titledPaneHistogrammParams;
    @FXML
    public TitledPane titledPaneKMeansParams;
    @FXML
    public TitledPane titledPaneGradientParams;

    @FXML
    public Accordion accordionMarkerMethodsParams;
    @FXML
    public AnchorPane anchorPaneRadioButtonMarker;

    @FXML
    public Label thresholdKMeansLabel;
    @FXML
    public Slider sliderThresholdKMeans;
    @FXML
    public Spinner spinnerDistBetweenMarkerAndLine;
    @FXML
    public Spinner spinnerRatioReductionLength;
    @FXML
    public Spinner spinnerDistBetweenNearLines;
    @FXML
    public Spinner spinnerCountIterationsKMeans;
    @FXML
    public Spinner spinnerCountClustersKMeans;

    //Формирование маркеров для алгоритма водоразделов


    public AlgorithmMenuLayoutController(ImagesHandler imagesHandler) {
        // контролер должен знать модель
        // в модели все вычисления и внутренние данные
        this.imagesHandler = imagesHandler;
        //Регистрация в качестве наблюдателя сообщений от модели
        imagesHandler.registerObserver(this);
    }

    @FXML
    private void initialize() {
        commonTools.setDisable(true);
        cannyTools.setDisable(true);
        houghTools.setDisable(true);
        watershedTools.setDisable(true);


        commonTools.setExpanded(false);
        cannyTools.setExpanded(false);
        houghTools.setExpanded(false);
        watershedTools.setExpanded(false);

        maxThetaTextField.setText(String.valueOf(Math.PI));
    }

    @FXML
    private void handleMakeBinary(ActionEvent event){
        imagesHandler.doMakeBinary((int)binarySlider.getValue(), otsu.isSelected());
    }

    @FXML
    private void makeBlur(ActionEvent event) {
        imagesHandler.doMakeBlur((int) sizeGaussFilterCommon.getValue());
    }

    @FXML
    public void makeBilateralFilter(ActionEvent event) {
        imagesHandler.doMakeBilateralFilter(
                (int) pixelNeighborhoodDiameter.getValue(),
                (double) sigmaColor.getValue(),
                (double) sigmaSpace.getValue());
    }

    @FXML
    public void makeTopHat(ActionEvent event) {
        imagesHandler.doMakeTopHat(
                (double) topHatKernelSize.getValue()
        );
    }

    @FXML
    private void selectOtsu(ActionEvent event) {
        binarySlider.setDisable(otsu.isSelected());
    }

    @FXML
    private void handleApplyCanny(ActionEvent event) {
        //Извлечения размера фильтра Гаусса
        String sizeGFString = sizeGaussFilter.getText();
        int sizeGF = 0;
        if (sizeGFString != null || !Objects.equals(sizeGFString, "")) sizeGF = Integer.parseInt(sizeGFString);

        //Извлечение значений ядра Собеля
        int sizeSK = (int) sizeSobelKernel.getValue();

        //Нужно ли использовать L2 градиент
        boolean gL2 = gradientL2.isSelected();

        imagesHandler.doCannyEdgeDetection(sizeGF, (int) thresholdCanny.getValue(), sizeSK, gL2);
    }

    @FXML
    private void changeBinarySlider() {
        showBinaryThreshold.setText(String.valueOf((int) binarySlider.getValue()));
    }

    @FXML
    private void changeSizeGaussFilterCommon() {
        showSizeGaussFilterCommon.setText(String.valueOf((int) sizeGaussFilterCommon.getValue()));
    }

    @FXML
    private void changeThresholdCanny() {
        showThresholdCanny.setText(String.valueOf((int) thresholdCanny.getValue()));
    }

    @FXML
    private void handleApplyHough(ActionEvent event) {
        double distance = (double) spinnerHoughDistance.getValue();
        double angle = (double) spinnerHoughAngle.getValue();
        int threshold = (int) thresholdHoughSlider.getValue();

        RadioButton button = (RadioButton) toggleGroupHough.getSelectedToggle();
        if (button.getId().equals("radiobHoughClassic")) {
            double srn = Double.parseDouble(srnTextField.getText());
            double stn = Double.parseDouble(stnTextField.getText());
            double minTheta = Double.parseDouble(minThetaTextField.getText());
            double maxTheta = Double.parseDouble(maxThetaTextField.getText());
            imagesHandler.doHoughConversion(true, distance, angle, threshold, srn, stn, minTheta, maxTheta);
            return;
        }
        if (button.getId().equals("radiobHoughProbably")) {
            double maxLineGap = (double) spinnerHoughProbablyMaxLineGap.getValue();
            double minLineLength = (double) spinnerHoughProbablyMinLineLength.getValue();
            imagesHandler.doHoughConversion(false, distance, angle, threshold, minLineLength, maxLineGap);
        }
    }

    @FXML
    private void handleApplyWatershed (ActionEvent event) {
        if (watershedMode.isSelected()) {
            autoHandleWatershed();
        } else {
            manualHandleWatershed();
        }
    }

    @FXML
    private void changeRadioButtonHoughClassic(ActionEvent event) {
        radiobHoughClassic.setSelected(true);
        titledPaneHoughClassic.setDisable(false);
        titledPaneHoughClassic.setExpanded(true);

        radiobHoughProbably.setSelected(false);
        titledPaneHoughProbably.setDisable(true);
        titledPaneHoughProbably.setExpanded(false);
    }

    @FXML
    private void changeRadioButtonHoughProbably(ActionEvent event) {
        radiobHoughProbably.setSelected(true);
        titledPaneHoughProbably.setDisable(false);
        titledPaneHoughProbably.setExpanded(true);

        radiobHoughClassic.setSelected(false);
        titledPaneHoughClassic.setDisable(true);
        titledPaneHoughClassic.setExpanded(false);
    }

    @FXML
    private void changeRadioButtonGradient(ActionEvent event) {
        radiobGradient.setSelected(true);
        titledPaneGradientParams.setDisable(false);
        titledPaneGradientParams.setExpanded(true);

        radiobHistogramm.setSelected(false);
        titledPaneHistogrammParams.setDisable(true);
        titledPaneHistogrammParams.setExpanded(false);

        radiobKMeans.setSelected(false);
        titledPaneKMeansParams.setDisable(true);
        titledPaneKMeansParams.setExpanded(false);
    }

    @FXML
    private void changeRadioButtonKMeans(ActionEvent event) {
        radiobGradient.setSelected(false);
        titledPaneGradientParams.setDisable(true);
        titledPaneGradientParams.setExpanded(false);

        radiobHistogramm.setSelected(false);
        titledPaneHistogrammParams.setDisable(true);
        titledPaneHistogrammParams.setExpanded(false);

        radiobKMeans.setSelected(true);
        titledPaneKMeansParams.setDisable(false);
        titledPaneKMeansParams.setExpanded(true);
    }

    @FXML
    private void changeRadioButtonHistogramm(ActionEvent event) {
        radiobGradient.setSelected(false);
        titledPaneGradientParams.setDisable(true);
        titledPaneGradientParams.setExpanded(false);

        radiobHistogramm.setSelected(true);
        titledPaneHistogrammParams.setDisable(false);
        titledPaneHistogrammParams.setExpanded(true);

        radiobKMeans.setSelected(false);
        titledPaneKMeansParams.setDisable(true);
        titledPaneKMeansParams.setExpanded(false);
    }

    @FXML
    private void changeThresholdHough() {
        thresholdHoughLabel.setText(String.valueOf((int) thresholdHoughSlider.getValue()));
    }

    @FXML
    public void changeThresholdKMeansLabel(MouseEvent mouseEvent) {
        thresholdKMeansLabel.setText(String.valueOf((int) sliderThresholdKMeans.getValue()));
    }

    @FXML
    private void handleWatershedMode(ActionEvent event) {
        if (watershedMode.isSelected()) {
            watershedMode.setText("Автоматический режим");
            accordionMarkerMethodsParams.setDisable(false);
            anchorPaneRadioButtonMarker.setDisable(false);
        } else {
            watershedMode.setText("Ручной режим");
            accordionMarkerMethodsParams.setDisable(true);
            anchorPaneRadioButtonMarker.setDisable(true);
        }
    }

    @Override
    public void notification(String message) {
        switch (message) {
            case NotifyConstants.IMAGE_LOADED: {
                commonTools.setDisable(false);
                cannyTools.setDisable(false);
                cannyTools.setExpanded(true);
                houghTools.setDisable(false);
                watershedTools.setDisable(false);
                break;
            }
            case NotifyConstants.TEMP_IMAGE_READY: {
                Stage stage = new Stage();
                stage.initModality(Modality.NONE);
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setFitToHeight(true);
                scrollPane.setFitToWidth(true);
                scrollPane.setMaxWidth(1040.0);
                scrollPane.setMaxHeight(800.0);
                AnchorPane anchorPane = new AnchorPane();
                ImageView imageView = new ImageView();
                Image image = imagesHandler.getTempImage();

                imageView.setImage(image);
                scrollPane.setContent(anchorPane);
                anchorPane.getChildren().add(imageView);

                Scene scene = new Scene(scrollPane);
                stage.setScene(scene);
                stage.show();
                break;
            }
        }
    }

    private void manualHandleWatershed() {
//        1	Modelity.NONE	Когда вы открываете новое окно с этой модальностью (modelity), новое окно будет независимым по отношению к родительскому окну.
//            Вы можете интерактировать с родительским окном, или закрыть его не влияя на новое окно.
//        2	Modelity.WINDOW_MODAL	Когда вы открываете новое окно с этой модальностью (modelity), новое окно блокирует родительское окно.
//            Вы не можете интерактировать с родительским окном, до тех пор, пока это окно не закроется.
//        3	Modelity.APPLICATION_MODAL	Когда вы открываете новое окно с этой модальностью (modelity), оно блокирует все другие окна приложения.
//            Вы не можете интерактировать ни с каким окном, до тех пор пока это окно не закроется.
        Stage stageWatershed = new Stage();
        stageWatershed.initModality(Modality.APPLICATION_MODAL);

        BorderPane borderPane = new BorderPane();
        borderPane.maxHeight(800);
        borderPane.maxWidth(1300);

        Button run = new Button("Запустить обработку");
        ColorPicker colorPicker = new ColorPicker(Color.RED);
        HBox hBoxPanelTop = new HBox();
        hBoxPanelTop.getChildren().addAll(run, colorPicker);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxWidth(1040.0);
        scrollPane.setMaxHeight(800.0);

        AnchorPane anchorPane = new AnchorPane();

        ImageView imageView = new ImageView();
        Image image = imagesHandler.getCurrentImage();
        imageView.setImage(image);

        borderPane.setTop(hBoxPanelTop);
        borderPane.setCenter(scrollPane);
        scrollPane.setContent(anchorPane);
        anchorPane.getChildren().add(imageView);

        final double[] initX = new double[1];
        final double[] initY = new double[1];

        final double maxX = imageView.getImage().getWidth();
        final double maxY = imageView.getImage().getHeight();

        imageView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                //the event will be passed only to the circle which is on front
                initX[0] = me.getX();
                initY[0] = me.getY();
                me.consume();
            }
        });

        //Карта цвета и соответсвующих ему линий
        Map<Color, List<Line>> colorListMap = new HashMap<Color, List<Line>>();

        imageView.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                Color color = colorPicker.getValue();
                if (me.getSceneX() < maxX && me.getSceneY() < maxY) {
                    Line line = new Line(initX[0], initY[0], me.getX(), me.getY());
                    line.setFill(null);
                    line.setStroke(color);
                    line.setStrokeWidth(2);
                    anchorPane.getChildren().add(line);

                    List<Line> lineList = colorListMap.get(color);
                    if (lineList != null) {
                        lineList.add(line);
                    } else {
                        lineList = new ArrayList<>();
                        lineList.add(line);
                        colorListMap.put(color, lineList);
                    }

//                    System.out.println(line.getStartX() + " " + line.getEndX());
//                    System.out.println(line.getEndX() + " " + line.getEndY());
                }

                initX[0] = me.getX() > maxX ? maxX : me.getX();
                initY[0] = me.getY() > maxY ? maxY : me.getY();
            }
        });

        run.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                imagesHandler.doWatershedSegmentationManualMode(colorListMap);
                //Закрываем текущее окно
                stageWatershed.close();
            }
        });

        Scene scene = new Scene(borderPane);
        stageWatershed.setScene(scene);
        stageWatershed.show();
    }

    private void autoHandleWatershed() {
        double distanceBetweenLineAndMarkers = (double) spinnerDistBetweenMarkerAndLine.getValue();
        double ratioReductionMarkers = (double) spinnerRatioReductionLength.getValue();

        RadioButton button = (RadioButton) toggleGroupMarker.getSelectedToggle();
        if (button.getId().equals("radiobGradient")) {
            double maxDistBetweenParallelLines = (double) spinnerDistBetweenNearLines.getValue();
            imagesHandler.doWatershedSegmentationAutoMode(1, distanceBetweenLineAndMarkers, ratioReductionMarkers, maxDistBetweenParallelLines);

        } else if (button.getId().equals("radiobKMeans")) {
            double brithnessPixelsThresholdKMeans = (double) sliderThresholdKMeans.getValue();
            double countIterationsKMeans = (double) spinnerCountIterationsKMeans.getValue();
            double countClustersKMeans = (double) spinnerCountClustersKMeans.getValue();
            imagesHandler.doWatershedSegmentationAutoMode(2, distanceBetweenLineAndMarkers, ratioReductionMarkers, brithnessPixelsThresholdKMeans, countIterationsKMeans, countClustersKMeans);

        } else if (button.getId().equals("radiobHistogramm")) {
            imagesHandler.doWatershedSegmentationAutoMode(3, distanceBetweenLineAndMarkers, ratioReductionMarkers);
        }
    }
}
