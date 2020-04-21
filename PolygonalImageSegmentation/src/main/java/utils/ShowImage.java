package utils;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.opencv.core.Mat;

public class ShowImage {

    public static void show(Image image) {
        show(image, "");
    }

    public static void show(Image image, String title) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.NONE);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxWidth(1040.0);
        scrollPane.setMaxHeight(800.0);
        AnchorPane anchorPane = new AnchorPane();
        ImageView imageView = new ImageView();

        imageView.setImage(image);
        scrollPane.setContent(anchorPane);
        anchorPane.getChildren().add(imageView);

        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        stage.show();
    }

    public static void drawPointsBetweenTwoPoints(Mat m, int xstart, int ystart, int xend, int yend, double[] color) {

        int x, y, dx, dy, incx, incy, pdx, pdy, es, el, err;

        dx = xend - xstart;//проекция на ось X
        dy = yend - ystart;//проекция на ось Y

        incx = sign(dx);
        /*
         * Определяем, в какую сторону нужно будет сдвигаться. Если dx < 0, т.е. отрезок идёт
         * справа налево по иксу, то incx будет равен -1.
         * Это будет использоваться в цикле постороения.
         */
        incy = sign(dy);
        /*
         * Аналогично. Если рисуем отрезок снизу вверх -
         * это будет отрицательный сдвиг для y (иначе - положительный).
         */

        if (dx < 0) dx = -dx;//далее мы будем сравнивать: "if (dx < dy)"
        if (dy < 0) dy = -dy;//поэтому необходимо сделать dx = |dx|; dy = |dy|
        //эти две строчки можно записать и так: dx = Math.abs(dx); dy = Math.abs(dy);

        if (dx > dy)
        //определяем наклон отрезка:
        {
            /*
             * Если dx > dy, то значит отрезок "вытянут" вдоль оси икс, т.е. он скорее длинный, чем высокий.
             * Значит в цикле нужно будет идти по икс (строчка el = dx;), значит "протягивать" прямую по иксу
             * надо в соответствии с тем, слева направо и справа налево она идёт (pdx = incx;), при этом
             * по y сдвиг такой отсутствует.
             */
            pdx = incx;	pdy = 0;
            es = dy;	el = dx;
        }
        else//случай, когда прямая скорее "высокая", чем длинная, т.е. вытянута по оси y
        {
            pdx = 0;	pdy = incy;
            es = dx;	el = dy;//тогда в цикле будем двигаться по y
        }

        x = xstart;
        y = ystart;
        err = el/2;
        m.put(x, y, color);//ставим первую точку
        m.put(x+1, y+1, color);//ставим первую точку
        m.put(x-1, y-1, color);//ставим первую точку
        m.put(x+1, y-1, color);//ставим первую точку
        m.put(x-1, y+1, color);//ставим первую точку

        m.put(x+1, y, color);//ставим первую точку
        m.put(x-1, y, color);//ставим первую точку
        m.put(x, y-1, color);//ставим первую точку
        m.put(x, y+1, color);//ставим первую точку
        //все последующие точки возможно надо сдвигать, поэтому первую ставим вне цикла

        for (int t = 0; t < el; t++)//идём по всем точкам, начиная со второй и до последней
        {
            err -= es;
            if (err < 0)
            {
                err += el;
                x += incx;//сдвинуть прямую (сместить вверх или вниз, если цикл проходит по иксам)
                y += incy;//или сместить влево-вправо, если цикл проходит по y
            }
            else
            {
                x += pdx;//продолжить тянуть прямую дальше, т.е. сдвинуть влево или вправо, если
                y += pdy;//цикл идёт по иксу; сдвинуть вверх или вниз, если по y
            }

            m.put(x, y, color);
        }
    }

    private static int sign (int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
        //возвращает 0, если аргумент (x) равен нулю; -1, если x < 0 и 1, если x > 0.
    }

    public static void drawPointsBetweenTwoPoints(Mat m, double xstart, double ystart, double xend, double yend, double[] color) {
        drawPointsBetweenTwoPoints(m, (int)xstart, (int)ystart, (int)xend, (int)yend, color);
    }
}
