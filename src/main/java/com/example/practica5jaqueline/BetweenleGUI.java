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

    private Scene crearEscenaConfig() {
        VBox root = new VBox(18);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f6f8fc;");

        Label titulo = new Label(t("Configuración del juego", "Game setup"));
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titulo.setStyle("-fx-text-fill: #1f2a44;");

        Label lblLong = new Label(t("Longitud de palabra (5 a 13)", "Word length (5 to 13)"));
        lblLong.setStyle("-fx-text-fill: #1f2a44; -fx-font-weight: bold;");

        ComboBox<Integer> comboLongitud = new ComboBox<>();
        for (int i = 5; i < 14; i++) comboLongitud.getItems().add(i);
        comboLongitud.setValue(longitud);

        Label lblIntentos = new Label(t("Oportunidades (10, 12 o 14)", "Attempts (10, 12 or 14)"));
        lblIntentos.setStyle("-fx-text-fill: #1f2a44; -fx-font-weight: bold;");

        ComboBox<Integer> comboIntentos = new ComboBox<>();
        comboIntentos.getItems().addAll(10, 12, 14);
        comboIntentos.setValue(intentos);

        Label lblError = new Label("");
        lblError.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");

        HBox filaBotones = new HBox(12);
        filaBotones.setAlignment(Pos.CENTER);

        SoundButton btnVolver = new SoundButton(t("Volver", "Back"));
        btnVolver.establecerSonidoClic(clickSound);
        btnVolver.setStyle(estiloBotonSecundario());
        btnVolver.setOnAction(e -> ventanaPrincipal.setScene(escenaIdioma));

        SoundButton btnIniciar = new SoundButton(t("Iniciar", "Start"));
        btnIniciar.establecerSonidoClic(clickSound);
        btnIniciar.setStyle(estiloBotonPrincipal());
        btnIniciar.setOnAction(e -> {
            Integer L = comboLongitud.getValue();
            Integer I = comboIntentos.getValue();

            if (L == null || L < 5 || L >= 14) {
                lblError.setText(t("Error: la longitud debe estar entre 5 y 13.",
                        "Error: length must be between 5 and 13."));
                return;
            }
            if (I == null || !(I == 10 || I == 12 || I == 14)) {
                lblError.setText(t("Error: debes elegir 10, 12 o 14 oportunidades.",
                        "Error: you must choose 10, 12 or 14 attempts."));
                return;
            }

            longitud = L;
            intentos = I;

            try {
                // Instanciamos el backend del juego (asegúrate de tener la clase Betweenle en tu proyecto)
                juegoApi = new Betweenle(idiomaSeleccionado, longitud, intentos);

                porcentajeInf = "?%";
                porcentajeSup = "?%";
                pistaUtilizada = false;

                recalcularPorcentajes();

                escenaJuego = crearEscenaJuego();
                ventanaPrincipal.setScene(escenaJuego);
            } catch (Exception ex) {
                lblError.setText(t("Error al cargar diccionario: ", "Failed to load dictionary: ") + ex.getMessage());
            }
        });

        filaBotones.getChildren().addAll(btnVolver, btnIniciar);
        root.getChildren().addAll(titulo, lblLong, comboLongitud, lblIntentos, comboIntentos, lblError, filaBotones);
        return new Scene(root, 520, 680);
    }

    private String estiloBotonPrincipal() {
        return "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; " +
                "-fx-padding: 12 26; -fx-background-radius: 14;";
    }

    private String estiloBotonSecundario() {
        return "-fx-background-color: #eef2ff; -fx-text-fill: #1f2a44; -fx-font-weight: bold; -fx-font-size: 14px; " +
                "-fx-padding: 10 20; -fx-background-radius: 14; -fx-border-color: #c7d2fe; -fx-border-width: 2; -fx-border-radius: 14;";
    }

    private void recalcularPorcentajes() {
    }

    private Scene crearEscenaJuego() {
        VBox dummy = new VBox(new Label(""));
        return new Scene(dummy, 760, 900);
    }



}