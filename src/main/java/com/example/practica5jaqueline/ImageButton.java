package com.example.practica5jaqueline;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageButton extends Button {

    private final ImageView vistaImagen = new ImageView();

    public ImageButton(String image) {
        super();
        setFocusTraversable(false);
        setGraphic(vistaImagen);
        setText(null);
        vistaImagen.setPreserveRatio(true);
        vistaImagen.setSmooth(true);

        setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;");

        if (image != null && !image.trim().isEmpty()) {
            establecerImagen(image);
        }
    }

    public boolean establecerImagen(File archivo) {
        if (archivo == null || !archivo.exists()) {
            vistaImagen.setImage(null);
            return false;
        }
        try (FileInputStream fis = new FileInputStream(archivo)) {
            vistaImagen.setImage(new Image(fis));
            return true;
        } catch (IOException e) {
            vistaImagen.setImage(null);
            return false;
        }
    }

    public boolean establecerImagen(String rutaArchivo) {
        return establecerImagen(rutaArchivo != null ? new File(rutaArchivo) : null);
    }

    public void establecerTamanoImagen(double ancho, double alto) {
        vistaImagen.setFitWidth(ancho);
        vistaImagen.setFitHeight(alto);
    }
}