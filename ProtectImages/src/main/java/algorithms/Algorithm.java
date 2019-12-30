package algorithms;

import javafx.scene.image.Image;
import org.opencv.core.Mat;
import utils.ImageUtils;
import utils.ShowImage;

import java.io.File;

public abstract class Algorithm {

    private Image sourceImage;
    private Mat sourceMat;

    public abstract void run();

    public void loadImage(String pathname) {
        File file = new File(pathname);
        String localUrl = file.toURI().toString();
        sourceImage = new Image(localUrl);
        sourceMat = ImageUtils.imageFXToMat(sourceImage);
        ShowImage.show(sourceImage);
    }

    public Image getSourceImage() {
        return sourceImage;
    }

    public Mat getSourceMat() {
        return sourceMat;
    }
}
