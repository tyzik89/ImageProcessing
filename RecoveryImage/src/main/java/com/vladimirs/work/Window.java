package com.vladimirs.work;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Window extends JFrame {
    private int width = 550;
    private int height = 800;

    public Window(BufferedImage src, String text){
        JPanel imagesPanel = new JPanel();
        JLabel imageLabel = new JLabel();

        setTitle(text);
        //Остановка приложения при закрытии приложения
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);
        //Разрешение менять размер окна
        setResizable(true);
        //Окно открывается по середине
        setLocationRelativeTo(null);

        imageLabel = new JLabel( new ImageIcon(src));
        imagesPanel.add(imageLabel);
        getContentPane().add(imagesPanel);

        //Отображение всех элементов
        setVisible(true);
    }
}
