package com.example.practica5jaqueline;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BetweenleGUI extends Application {

    private final File homeImg = new File("homePNG.png");
    private final File clickSound = new File("Click_Sound.mp3");
    private final File statsImg = new File("estadisticas.png");
    private final File hintImg = new File("pista.png");

    private Stage ventanaPrincipal;

    private Scene escenaIdioma;
    private Scene escenaConfig;
    private Scene escenaJuego;

    private Betweenle juegoApi;

    private int idiomaSeleccionado = 1;

    private int longitud = 5;
    private int intentos = 10;

    private boolean pistaUtilizada = false;

    private String porcentajeInf = "?%";
    private String porcentajeSup = "?%";

    private Label lblTitulo;
    private Label lblAttempts;
    private Label lblMensajes;

    private HBox filaLimiteArriba;
    private HBox filaSecreta;
    private HBox filaLimiteAbajo;

    private TextField txtEntrada;

    private FlowPane tecladoPane;

    @Override
    public void start(Stage primaryStage) {
        this.ventanaPrincipal = primaryStage;
        this.ventanaPrincipal.setTitle("Betweenle");

        escenaIdioma = crearEscenaIdioma();
        ventanaPrincipal.setScene(escenaIdioma);

        ventanaPrincipal.show();
    }

    private Scene crearEscenaIdioma() {
        VBox root = new VBox(22);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f7fbff, #eef4ff);");

        Label titulo = new Label("BETWEENLE");
        titulo.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 54));
        titulo.setStyle("-fx-text-fill: #1f2a44;");

        Label subtitulo = new Label("Selecciona un idioma / Choose a language");
        subtitulo.setStyle("-fx-text-fill: #4c5a7a; -fx-font-size: 14px;");

        SoundButton btnES = new SoundButton("Español");
        btnES.establecerSonidoClic(clickSound);
        btnES.setStyle(estiloBotonPrincipal());
        btnES.setOnAction(e -> {
            idiomaSeleccionado = 1;
            escenaConfig = crearEscenaConfig();
            ventanaPrincipal.setScene(escenaConfig);
        });

        SoundButton btnEN = new SoundButton("English");
        btnEN.establecerSonidoClic(clickSound);
        btnEN.setStyle(estiloBotonPrincipal());
        btnEN.setOnAction(e -> {
            idiomaSeleccionado = 2;
            escenaConfig = crearEscenaConfig();
            ventanaPrincipal.setScene(escenaConfig);
        });

        HBox botones = new HBox(14, btnES, btnEN);
        botones.setAlignment(Pos.CENTER);

        root.getChildren().addAll(titulo, subtitulo, botones);
        return new Scene(root, 520, 680);
    }

    private String t(String es, String en) {
        return (idiomaSeleccionado == 1) ? es : en;
    }

    private String estiloBotonPrincipal() {
        return "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold;";
    }

    private Scene crearEscenaConfig() {
        VBox dummy = new VBox(new Label(""));
        return new Scene(dummy, 520, 680);
    }

}