package models.image;

import javafx.scene.image.Image;

/**
 * Хранилище изображений. Синглтон
 * Хранит текущее, предыдущее и изначальное изображение
 */
public class StorageImages {

    private static Image SourceImage;
    private static Image CurrentImage;
    private static Image PreviousImage;
    private static Image TempImage;

    private static volatile StorageImages INSTANCE = new StorageImages();

    private StorageImages(){}

    public static StorageImages getInstance()
    {
        return INSTANCE;
    }


    public void init(Image image) {
        SourceImage = image;
        CurrentImage = image;
        PreviousImage = image;
        TempImage = image;
    }

    public void switchImagesOnNextStep(Image newImage) {
        PreviousImage = CurrentImage;
        CurrentImage = newImage;
    }

    public Image getSourceImage() {
        return SourceImage;
    }

    public Image getCurrentImage() {
        return CurrentImage;
    }

    public void setCurrentImage(Image currentImage) {
        CurrentImage = currentImage;
    }

    public Image getPreviousImage() {
        return PreviousImage;
    }

    public void setPreviousImage(Image previousImage) {
        PreviousImage = previousImage;
    }

    public Image getTempImage() {
        return TempImage;
    }

    public void setTempImage(Image tempImage) {
        TempImage = tempImage;
    }
}
