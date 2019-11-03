package controllers;

import app.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.image.ImagesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Главный контроллер
 * Содержит главное меню
 * Инициализирует остальные слои
 * Имеет доступ к главному приложению для подгрузки дополнительных слоёв на главную сцену {@link javafx.stage.Stage}
 */
public class RootLayoutController {

    private final static Logger LOGGER = LoggerFactory.getLogger(RootLayoutController.class);

    // Ref on app.MainApp
    private MainApp mainApp;
    //Ref on model class - ImagesHandler
    private ImagesHandler imagesHandler;


    @FXML
    private MenuItem itmLoad;
    @FXML
    private MenuItem itmExit;


    public RootLayoutController() {
        // контролер должен знать модель
        // в модели все вычисления и внутренние данные
        this.imagesHandler = new ImagesHandler();
    }

    @FXML
    private void initialize () {

    }

    public void initializeOtherElements() {
        // Загрузчик макетов из fxml файла.
        FXMLLoader loader = new FXMLLoader();
        //Загружаем слой, отображающий изображение
        loader.setLocation(MainApp.class.getResource("/fxml/ImageViewLayout.fxml"));
        loader.setControllerFactory(clazz -> {
            if (clazz == ImageViewLayoutController.class) {
                return new ImageViewLayoutController(imagesHandler);
            } else {
                // default behavior:
                try {
                    return clazz.newInstance();
                } catch (Exception exc) {
                    throw new RuntimeException(exc);
                }
            }
        });
        try {
            Parent parent = loader.load();
            mainApp.getRootLayout().setLeft(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Загрузчик макетов из fxml файла.
        loader = new FXMLLoader();
        //Загружаем слой, отображающий нижнюю панель с прогресбаром и кнопками отмены/возврата
        loader.setLocation(MainApp.class.getResource("/fxml/BottomBarLayout.fxml"));
        loader.setControllerFactory(clazz -> {
            if (clazz == BottomBarLayoutController.class) {
                return new BottomBarLayoutController(imagesHandler);
            } else {
                // default behavior:
                try {
                    return clazz.newInstance();
                } catch (Exception exc) {
                    throw new RuntimeException(exc);
                }
            }
        });
        try {
            Parent parent = loader.load();
            mainApp.getRootLayout().setBottom(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Загрузчик макетов из fxml файла.
        loader = new FXMLLoader();
        //Загружаем слой, отображающий панель с алгоритмами
        loader.setLocation(MainApp.class.getResource("/fxml/AlgorithmMenuLayout.fxml"));
        loader.setControllerFactory(clazz -> {
            if (clazz == AlgorithmMenuLayoutController.class) {
                return new AlgorithmMenuLayoutController(imagesHandler);
            } else {
                // default behavior:
                try {
                    return clazz.newInstance();
                } catch (Exception exc) {
                    throw new RuntimeException(exc);
                }
            }
        });
        try {
            Parent parent = loader.load();
            mainApp.getRootLayout().setRight(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Вызывается главным приложением, которое даёт на себя ссылку.
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleItmExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleLoad(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбрать изображение для обработки");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.bmp"),
                new FileChooser.ExtensionFilter("Все файлы", "*.*"));


        File selectedFile = fileChooser.showOpenDialog(mainApp.getPrimaryStage().getOwner());
        if (selectedFile != null && selectedFile.exists())
            imagesHandler.load(selectedFile);
    }

    @FXML
    private void handleSourceImage(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String type = menuItem.getUserData().toString();

        Stage stage = new Stage();
        /*
            1	Modelity.NONE	Когда вы открываете новое окно с этой модальностью (modelity), новое окно будет независимым по отношению к родительскому окну. Вы можете интерактировать с родительским окном, или закрыть его не влияя на новое окно.
            2	Modelity.WINDOW_MODAL	Когда вы открываете новое окно с этой модальностью (modelity), новое окно блокирует родительское окно. Вы не можете интерактировать с родительским окном, до тех пор, пока это окно не закроется.
            3	Modelity.APPLICATION_MODAL	Когда вы открываете новое окно с этой модальностью (modelity), оно блокирует все другие окна приложения. Вы не можете интерактировать ни с каким окном, до тех пор пока это окно не закроется.
            */
        stage.initModality(Modality.NONE);
        //Настройка родительского окна
        //stage.initOwner(mainApp.getPrimaryStage());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPannable(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxWidth(1024.0);
        scrollPane.setMaxHeight(768.0);
        ImageView imageView = new ImageView();
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        scrollPane.setContent(imageView);

        Image image = type.equals("SOURCE") ? imagesHandler.getSourceImage() : imagesHandler.getCurrentImage();
        imageView.setImage(image);

        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        stage.show();
    }
}
