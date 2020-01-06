package algorithms;

import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import utils.ImageUtils;
import utils.ShowImage;

import java.io.File;
import java.io.IOException;

public abstract class Algorithm {

    private Image sourceImage;
    private Mat sourceMat;

    public abstract void run();

    public abstract void check();

    public void loadImage(String pathname) {
        File file = new File(pathname);
        String localUrl = file.toURI().toString();
        sourceImage = new Image(localUrl);
        sourceMat = ImageUtils.imageFXToMat(sourceImage);
        ShowImage.show(sourceImage);
    }

    public void saveImage(Mat mat, String pathname, String postfix_file_name) {
        String newName = pathname.substring(0, 28);
        newName = newName + postfix_file_name;
        boolean st = Imgcodecs.imwrite(newName + ".jpg", mat);
        if (!st) {
            System.out.println("Не удалось сохранить изображение");
        }
    }

    public Image getSourceImage() {
        return sourceImage;
    }

    public Mat getSourceMat() {
        return sourceMat;
    }
}
