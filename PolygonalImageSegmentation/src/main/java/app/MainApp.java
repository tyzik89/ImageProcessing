package app;

import controllers.RootLayoutController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Polygonal Image Segmentation");

        initRootLayout();

        primaryStage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Инициализация корневого макета.
     */
    private void initRootLayout() {
        try {
            // Загружаем корневой макет из fxml файла.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/RootLayout.fxml"));
            this.rootLayout = loader.load();

            // Даём контроллеру доступ к главному приложению.
            RootLayoutController rootLayoutController = loader.getController();
            rootLayoutController.setMainApp(this);

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            //primaryStage.setMaximized(true);
            primaryStage.setResizable(false);
            primaryStage.show();

            //Инициализация остальных элементов
            rootLayoutController.initializeOtherElements();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Возвращает главную сцену.
     * @return primaryStage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Возврат корневого макета
     * @return rootLayout
     */
    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public static void main(String[] args) throws Exception {
        // load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        launch(args);
    }
}