package models.image;

import org.opencv.core.Mat;

public class StorageMatrix {

    private Mat matrixOfLines;

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
}
