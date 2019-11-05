package utils;

import javafx.scene.image.*;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public final class ImageUtils {

    public static final Scalar COLOR_BLACK = colorRGB(0, 0, 0);
    public static final Scalar COLOR_WHITE = colorRGB(255, 255, 255);
    public static final Scalar COLOR_RED = colorRGB(255, 0, 0);
    public static final Scalar COLOR_BLUE = colorRGB(0, 0, 255);
    public static final Scalar COLOR_GREEN = colorRGB(0, 128, 0);
    public static final Scalar COLOR_YELLOW = colorRGB(255, 255, 0);
    public static final Scalar COLOR_GRAY = colorRGB(128, 128, 128);

    private static Scalar colorRGB(double red, double green, double blue) {
        return new Scalar(blue, green, red);
    }

    public static Scalar colorRGBA(double red, double green, double blue,
                                   double alpha) {
        return new Scalar(blue, green, red, alpha);
    }

    public static Scalar colorRGB(javafx.scene.paint.Color c) {
        return new Scalar((double) Math.round(c.getBlue() * 255),
                (double) Math.round(c.getGreen() * 255),
                (double) Math.round(c.getRed() * 255));
    }
    public static Scalar colorRGBA(javafx.scene.paint.Color c) {
        return new Scalar((double) Math.round(c.getBlue() * 255),
                (double) Math.round(c.getGreen() * 255),
                (double) Math.round(c.getRed() * 255),
                (double) Math.round(c.getOpacity() * 255));
    }

    /**
     * Конвертация матрицы {@link Mat} в объект Image {@link Image} для JavaFX
     * @param m матрица
     * @return объект WitableImage {@link WritableImage}
     */
    public static WritableImage matToImageFX(Mat m) {
        if (m == null || m.empty()) return null;

        switch (m.depth()) {
            case CvType.CV_8U:
                break;
            case CvType.CV_16U:
                Mat m_16 = new Mat();
                m.convertTo(m_16, CvType.CV_8U, 255.0 / 65535);
                m = m_16;
                break;
            case CvType.CV_32F:
                Mat m_32 = new Mat();
                m.convertTo(m_32, CvType.CV_8U, 255);
                m = m_32;
                break;
            default:
                return null;
        }

        switch (m.channels()) {
            case 1: {
                Mat m_bgra = new Mat();
                Imgproc.cvtColor(m, m_bgra, Imgproc.COLOR_GRAY2BGRA);
                m = m_bgra;
                break;
            }
            case 3: {
                Mat m_bgra = new Mat();
                Imgproc.cvtColor(m, m_bgra, Imgproc.COLOR_BGR2BGRA);
                m = m_bgra;
                break;
            }
            case 4:
                break;
            default:
                return null;
        }

        byte[] buf = new byte[m.channels() * m.cols() * m.rows()];
        m.get(0, 0, buf);

        WritableImage image = new WritableImage(m.cols(), m.rows());
        PixelWriter pixelWriter = image.getPixelWriter();
        pixelWriter.setPixels(
                0 ,0, m.cols(), m.rows(),
                WritablePixelFormat.getByteBgraInstance(),
                buf, 0, m.cols() * 4);

        return image;
    }

    /**
     * Конвертация объекта Image {@link Image} в матрицу {@link Mat}
     * @param image входное изображение
     * @return матрицу
     */
    public static Mat imageFXToMat(Image image) {
        if (image == null) return new Mat();

        PixelReader pixelReader = image.getPixelReader();
        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        byte[] buf = new byte[4 * w * h];

        pixelReader.getPixels(
                0, 0, w, h,
                WritablePixelFormat.getByteBgraInstance(),
                buf, 0, w * 4);
        Mat m = new Mat(h, w, CvType.CV_8UC4);
        m.put(0, 0, buf);
        return m;
    }
}
