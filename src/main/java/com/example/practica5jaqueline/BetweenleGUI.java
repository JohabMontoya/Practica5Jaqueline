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
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #ffffff;");

        BorderPane topBar = new BorderPane();
        topBar.setPadding(new Insets(8));
        topBar.setStyle("-fx-background-color: transparent;");

        // NOTA: Recuerda que ImageButton debe estar implementado en tu proyecto
        ImageButton btnHome = new ImageButton("homePNG.png");
        btnHome.setText(t("Menú", "Menu"));
        btnHome.establecerImagen(homeImg);
        btnHome.establecerTamanoImagen(28, 28);
        btnHome.setStyle("-fx-background-color: transparent; -fx-text-fill: #1f2a44; -fx-font-weight: bold;");
        btnHome.setOnAction(e -> ventanaPrincipal.setScene(escenaConfig));

        ImageButton btnStats = new ImageButton("estadisticas.png");
        btnStats.setText(t("Estadísticas", "Stats"));
        btnStats.establecerImagen(statsImg);
        btnStats.establecerTamanoImagen(28, 28);
        btnStats.setStyle("-fx-background-color: transparent; -fx-text-fill: #1f2a44; -fx-font-weight: bold;");
        btnStats.setOnAction(e -> mostrarEstadisticas());

        topBar.setLeft(btnHome);
        topBar.setRight(btnStats);
        root.setTop(topBar);

        VBox centro = new VBox(14);
        centro.setAlignment(Pos.TOP_CENTER);
        centro.setPadding(new Insets(6));

        lblTitulo = new Label(t("BETWEENLE - Español", "BETWEENLE - English"));
        lblTitulo.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 36));
        lblTitulo.setStyle("-fx-text-fill: #1f2a44;");

        lblAttempts = new Label();
        lblAttempts.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
        lblAttempts.setStyle("-fx-text-fill: #1f2a44;");

        filaLimiteArriba = new HBox(8);
        filaLimiteArriba.setAlignment(Pos.CENTER);

        filaSecreta = new HBox(8);
        filaSecreta.setAlignment(Pos.CENTER);

        filaLimiteAbajo = new HBox(8);
        filaLimiteAbajo.setAlignment(Pos.CENTER);

        lblMensajes = new Label(t("Escribe tu intento y presiona “Adivinar”.", "Type your guess and press “Guess”."));
        lblMensajes.setStyle("-fx-text-fill: #34495e; -fx-font-weight: bold;");

        txtEntrada = new TextField();
        txtEntrada.setEditable(true);
        txtEntrada.setAlignment(Pos.CENTER);
        txtEntrada.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        txtEntrada.setMaxWidth(320);
        txtEntrada.setStyle("-fx-background-color: #f3f6ff; -fx-text-fill: #1f2a44; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #c7d2fe; -fx-border-width: 2;");

        txtEntrada.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) return change;
            if (!newText.matches("[a-zA-ZñÑ]*")) return null;
            if (newText.length() > longitud) return null;
            return change;
        }));
        txtEntrada.textProperty().addListener((obs, oldV, newV) -> actualizarTecladoYDinamica());

        HBox acciones = new HBox(12);
        acciones.setAlignment(Pos.CENTER);

        SoundButton btnGuess = new SoundButton(t("Adivinar", "Guess"));
        btnGuess.establecerSonidoClic(clickSound);
        btnGuess.setStyle(estiloBotonPrincipal());
        btnGuess.setOnAction(e -> procesarIntento());

        ImageButton btnHint = new ImageButton("pista.png");
        btnHint.setText(t("Pista", "Hint"));
        btnHint.establecerImagen(hintImg);
        btnHint.establecerTamanoImagen(22, 22);
        btnHint.setStyle("-fx-background-color: #eef2ff; -fx-text-fill: #1f2a44; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 12; -fx-border-color: #c7d2fe; -fx-border-width: 2; -fx-border-radius: 12;");
        btnHint.setOnAction(e -> abrirMenuPistas());

        SoundButton btnGiveUp = new SoundButton(t("Rendirse", "Give up"));
        btnGiveUp.establecerSonidoClic(clickSound);
        btnGiveUp.setStyle("-fx-background-color: #ffecec; -fx-text-fill: #c0392b; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 18; -fx-background-radius: 14; -fx-border-color: #ffb3b3; -fx-border-width: 2; -fx-border-radius: 14;");
        btnGiveUp.setOnAction(e -> mostrarVentanaFin(t("Te has rendido", "You gave up"), false));

        acciones.getChildren().addAll(btnGuess, btnHint, btnGiveUp);

        tecladoPane = crearTecladoVisual();
        tecladoPane.setPadding(new Insets(8));

        centro.getChildren().addAll(
                lblTitulo,
                lblAttempts,
                spacer(6),
                filaLimiteArriba,
                spacer(6),
                filaSecreta,
                spacer(6),
                filaLimiteAbajo,
                spacer(12),
                lblMensajes,
                txtEntrada,
                acciones,
                spacer(10),
                tecladoPane
        );

        root.setCenter(centro);

        actualizarUI();
        reconstruirFilas();
        txtEntrada.requestFocus();

        return new Scene(root, 760, 900);
    }

    private Region spacer(double h) {
        Region r = new Region();
        r.setMinHeight(h);
        return r;
    }


    private void reconstruirFilas() {
        if (juegoApi == null) return;

        filaLimiteArriba.getChildren().clear();
        filaSecreta.getChildren().clear();
        filaLimiteAbajo.getChildren().clear();

        String inf = juegoApi.getLimiteInferior().toUpperCase();
        String sup = juegoApi.getLimiteSuperior().toUpperCase();

        filaLimiteArriba.getChildren().add(bloquePorcentaje(porcentajeInf));
        for (char c : inf.toCharArray()) filaLimiteArriba.getChildren().add(bloqueVerde(String.valueOf(c)));

        for (int i = 0; i < longitud; i++) filaSecreta.getChildren().add(bloqueGris("_"));

        filaLimiteAbajo.getChildren().add(bloquePorcentaje(porcentajeSup));
        for (char c : sup.toCharArray()) filaLimiteAbajo.getChildren().add(bloqueVerde(String.valueOf(c)));
    }

    private StackPane bloquePorcentaje(String pct) {
        Label l = new Label(pct == null ? "?%" : pct);
        l.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 14));
        l.setStyle("-fx-text-fill: #1f2a44;");

        StackPane box = new StackPane(l);
        box.setPrefSize(78, 54);
        box.setStyle("-fx-background-color: #eef2ff; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #c7d2fe; -fx-border-width: 2;");
        return box;
    }

    private StackPane bloqueVerde(String letra) {
        Label l = new Label(letra);
        l.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
        l.setStyle("-fx-text-fill: #0b3a2a;");

        StackPane box = new StackPane(l);
        box.setPrefSize(64, 54);
        box.setStyle("-fx-background-color: #d1fae5; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #6ee7b7; -fx-border-width: 2;");
        return box;
    }

    private StackPane bloqueGris(String letra) {
        Label l = new Label(letra);
        l.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
        l.setStyle("-fx-text-fill: #1f2a44;");

        StackPane box = new StackPane(l);
        box.setPrefSize(64, 54);
        box.setStyle("-fx-background-color: #e5e7eb; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #cbd5e1; -fx-border-width: 2;");
        return box;
    }

    private void mostrarEstadisticas() {}
    private void actualizarTecladoYDinamica() {}
    private void procesarIntento() {}
    private void abrirMenuPistas() {}
    private void mostrarVentanaFin(String tituloStr, boolean ganado) {}
    private void actualizarUI() {}
    private FlowPane crearTecladoVisual() { return new FlowPane(); }



}