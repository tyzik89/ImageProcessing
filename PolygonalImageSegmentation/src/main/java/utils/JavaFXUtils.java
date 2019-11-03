package utils;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;

/*
Platform.runLater: если вам нужно обновить компонент GUI из потока, не являющегося GUI, вы можете использовать его,
чтобы поместить свое обновление в очередь, и оно будет обработано потоком GUI как можно скорее.

Task реализует интерфейс Worker который используется, когда вам нужно запустить длинную задачу вне потока GUI
(чтобы избежать зависания вашего приложения), но все же необходимо взаимодействовать с GUI на некотором этапе.*/

public class JavaFXUtils {

    /**
     * Generic method for putting element running on a non-JavaFX thread on the
     * JavaFX thread, to properly update the UI
     *
     * @param property a {@link ObjectProperty}
     * @param value the value to set for the given {@link ObjectProperty}
     */
    public static <T> void onFXThread(final ObjectProperty<T> property, final T value)
    {
        Platform.runLater(() -> property.set(value));
    }
}
