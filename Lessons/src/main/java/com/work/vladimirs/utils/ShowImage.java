package com.work.vladimirs.utils;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class ShowImage {

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
}
