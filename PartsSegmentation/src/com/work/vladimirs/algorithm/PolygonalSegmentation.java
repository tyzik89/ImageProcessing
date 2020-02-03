package com.work.vladimirs.algorithm;

import com.work.vladimirs.utils.ImageUtils;
import com.work.vladimirs.utils.ShowImage;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public abstract class PolygonalSegmentation {

    private Image sourceImage;
    private Mat sourceMat;
    private int sizeSegment;
    private String pathname;
    private String filename;

    public abstract void run();

    public void loadImage(String pathname, String filename) {
        sourceMat = Imgcodecs.imread(pathname + filename);
        if (sourceMat.empty()) {
            System.out.println("Не удалось загрузить изображение");
            return;
        }
        sourceImage = ImageUtils.matToImageFX(sourceMat);
        ShowImage.show(sourceImage);
//        File file = new File(pathname + filename);
//        String localUrl = file.toURI().toString();
//        sourceImage = new Image(localUrl);
//        sourceMat = ImageUtils.imageFXToMat(sourceImage);
//        ShowImage.show(sourceImage);
    }

    public void saveImage(Mat mat, String pathname, String filename) {
        boolean st = Imgcodecs.imwrite(pathname + filename, mat);
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

    public int getSizeSegment() {
        return sizeSegment;
    }

    public void setSizeSegment(int sizeSegment) {
        this.sizeSegment = sizeSegment;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
