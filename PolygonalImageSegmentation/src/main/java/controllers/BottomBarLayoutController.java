package controllers;

import constants.NotifyConstants;
import fxelements.SingletonProcess;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import models.image.ImagesHandler;
import models.notification.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Контроллер, обрабатывающий слой с прогресбаром и кнопка отменить/вернуть
 * Отправляет в модель {@link ImagesHandler} данные о нажатии кнопок
 */
public class BottomBarLayoutController implements Observer {

    private final static Logger LOGGER = LoggerFactory.getLogger(BottomBarLayoutController.class);

    //Ref on model class - ImagesHandler
    private ImagesHandler imagesHandler;

    @FXML
    private ProgressBar prBar;
    @FXML
    private ProgressIndicator prInd;
    @FXML
    private Button cancel;
    @FXML
    private Button redo;

    public BottomBarLayoutController(ImagesHandler imagesHandler) {
        // контролер должен знать модель
        // в модели все вычисления и внутренние данные
        this.imagesHandler = imagesHandler;
        //Регистрация в качестве наблюдателя сообщений от модели
        imagesHandler.registerObserver(this);
    }

    @FXML
    private void initialize() {
        cancel.setDisable(true);
        redo.setDisable(true);
        //Инициализиурем синглтон процессбара
        SingletonProcess.getInstance().setProgressBar(prBar);
        SingletonProcess.getInstance().setProgressIndicator(prInd);

    }

    @FXML
    private void handleCancel(ActionEvent event) {
        imagesHandler.doCancelRedo();
        cancel.setDisable(true);
        redo.setDisable(false);
    }

    @FXML
    private void handleRedo(ActionEvent event) {
        imagesHandler.doCancelRedo();
        redo.setDisable(true);
        cancel.setDisable(false);
    }

    @Override
    public void notification(String message) {
        switch (message) {
            case NotifyConstants.IMAGE_READY: {
                cancel.setDisable(false);
                break;
            }
        }
    }
}
