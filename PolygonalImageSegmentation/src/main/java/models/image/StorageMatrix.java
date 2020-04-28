package models.image;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class StorageMatrix {

    private Mat matrixOfLines;
    private Mat sourceMat;

    private static volatile StorageMatrix INSTANCE = new StorageMatrix();

    private StorageMatrix(){
    }

    public static StorageMatrix getInstance()
    {
        return INSTANCE;
    }

    public Mat getMatrixOfLines() {
        return  this.matrixOfLines != null ?  this.matrixOfLines : new Mat();
    }

    public void setMatrixOfLines(Mat matrixOfLines) {
        this.matrixOfLines = matrixOfLines;
    }

    public Mat getSourceMat() {
        return sourceMat;
    }

    public void setSourceMat(Mat sourceMat) {
        this.sourceMat = sourceMat;
    }

    public void setSourceMat(String path) {
        sourceMat = Imgcodecs.imread(path);
        if (sourceMat.empty()) {
            System.out.println("Не удалось загрузить изображение");
        }
    }
}
