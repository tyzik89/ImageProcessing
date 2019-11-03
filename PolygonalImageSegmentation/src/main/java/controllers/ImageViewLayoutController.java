package controllers;

import constants.NotifyConstants;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import models.image.ImagesHandler;
import models.notification.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JavaFXUtils;

/**
 * Контроллер отображающий изображение
 * Получает уведомление о готовности изображения от {@link ImagesHandler} и отображает его
 */
public class ImageViewLayoutController implements Observer {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageViewLayoutController.class);

    //Ref on model class - ImagesHandler
    private ImagesHandler imagesHandler;

    @FXML
    private ImageView imgView;

    public ImageViewLayoutController(ImagesHandler imagesHandler) {
        // контролер должен знать модель
        // в модели все вычисления и внутренние данные
        this.imagesHandler = imagesHandler;
        //Регистрация в качестве наблюдателя сообщений от модели
        imagesHandler.registerObserver(this);
    }

    @FXML
    private void initialize() {}

    @Override
    public void notification(String message) {
        switch (message) {
            case NotifyConstants.IMAGE_LOADED:
            case NotifyConstants.IMAGE_READY: {
                JavaFXUtils.onFXThread(imgView.imageProperty(), imagesHandler.getCurrentImage());
                //imgView.setImage(imagesHandler.getCurrentImage());
                break;
            }
        }
    }
}
