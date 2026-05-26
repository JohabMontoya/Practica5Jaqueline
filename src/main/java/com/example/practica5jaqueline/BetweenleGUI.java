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
import java.util.ArrayList;
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

    private final List<StackPane> bloquesSecreto = new ArrayList<>();
    private boolean ultimaJugadaFueVictoria = false;

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
    private SoundButton btnGuess;
    private ImageButton btnHint;
    private SoundButton btnGiveUp;
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

    private Scene crearEscenaConfig() {
        VBox root = new VBox(18);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f6f8fc;");

        Label titulo = new Label(t("Configuración del juego", "Game setup"));
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titulo.setStyle("-fx-text-fill: #1f2a44;");

        Label lblLong = new Label(t("Longitud de palabra (5 a 14)", "Word length (5 to 14)"));
        lblLong.setStyle("-fx-text-fill: #1f2a44; -fx-font-weight: bold;");

        ComboBox<Integer> comboLongitud = new ComboBox<>();
        for (int i = 5; i < 15; i++) comboLongitud.getItems().add(i);
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
                juegoApi = new Betweenle(idiomaSeleccionado, longitud, intentos);

                porcentajeInf = "?%";
                porcentajeSup = "?%";
                pistaUtilizada = false;
                if(!juegoApi.getLimiteInferior().equals(juegoApi.getLimiteInferiorInicial()) &&
                        !juegoApi.getLimiteSuperiorInicial().equals(juegoApi.getLimiteSuperiorInicial())) {
                    recalcularPorcentajes();
                }
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

    private Scene crearEscenaJuego() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #ffffff;");

        BorderPane topBar = new BorderPane();
        topBar.setPadding(new Insets(8));
        topBar.setStyle("-fx-background-color: transparent;");

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
        txtEntrada.textProperty().addListener((obs, oldV, newV) -> {
            ultimaJugadaFueVictoria = false;
            actualizarFilaSecretaEnVivo();
            actualizarTecladoYDinamica();
        });
        HBox acciones = new HBox(12);
        acciones.setAlignment(Pos.CENTER);

        btnGuess = new SoundButton(t("Adivinar", "Guess"));
        btnGuess.establecerSonidoClic(clickSound);
        btnGuess.setStyle(estiloBotonPrincipal());
        btnGuess.setOnAction(e -> procesarIntento());

        btnHint = new ImageButton("pista.png");
        btnHint.setText(t("Pista", "Hint"));
        btnHint.establecerImagen(hintImg);
        btnHint.establecerTamanoImagen(22, 22);
        btnHint.setStyle("-fx-background-color: #eef2ff; -fx-text-fill: #1f2a44; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 12; -fx-border-color: #c7d2fe; -fx-border-width: 2; -fx-border-radius: 12;");
        btnHint.setOnAction(e -> {
            if (pistaUtilizada) {
                popupInfo(t("Pista", "Hint"), t("Ya no tienes pistas disponibles.", "You have no hints left."));
                return;
            }
            abrirMenuPistas();
        });

        btnGiveUp = new SoundButton(t("Rendirse", "Give up"));
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
        actualizarFilaSecretaEnVivo();
        txtEntrada.requestFocus();

        return new Scene(root, 760, 900);
    }

    private Region spacer(double h) {
        Region r = new Region();
        r.setMinHeight(h);
        return r;
    }

    private void actualizarEstadoBotonesFinJuego() {
        if (juegoApi == null) return;
        boolean terminado = juegoApi.juegoTerminado();

        if (btnGuess != null) btnGuess.setDisable(terminado);
        if (btnHint != null) btnHint.setDisable(terminado);
        if (btnGiveUp != null) btnGiveUp.setDisable(terminado);
        if (txtEntrada != null) txtEntrada.setEditable(!terminado);
    }

    private void reconstruirFilas() {
        filaLimiteArriba.getChildren().clear();
        filaSecreta.getChildren().clear();
        filaLimiteAbajo.getChildren().clear();

        String inf = juegoApi.getLimiteInferior().toUpperCase();
        String sup = juegoApi.getLimiteSuperior().toUpperCase();

        filaLimiteArriba.getChildren().add(bloquePorcentaje(porcentajeInf));
        for (char c : inf.toCharArray()) filaLimiteArriba.getChildren().add(bloqueVerde(String.valueOf(c)));

        bloquesSecreto.clear();
        for (int i = 0; i < longitud; i++) {
            StackPane b = bloqueGris("_");
            bloquesSecreto.add(b);
            filaSecreta.getChildren().add(b);
        }

        filaLimiteAbajo.getChildren().add(bloquePorcentaje(porcentajeSup));
        for (char c : sup.toCharArray()) filaLimiteAbajo.getChildren().add(bloqueVerde(String.valueOf(c)));
    }

    private void actualizarFilaSecretaEnVivo() {
        if (bloquesSecreto.isEmpty() || txtEntrada == null) return;

        String intento = txtEntrada.getText() == null ? "" : txtEntrada.getText().trim().toUpperCase();

        for (int i = 0; i < bloquesSecreto.size(); i++) {
            StackPane box = bloquesSecreto.get(i);

            if (ultimaJugadaFueVictoria) {
                String sec = juegoApi.getPalabraSecreta().toUpperCase();
                String letra = (i < sec.length()) ? String.valueOf(sec.charAt(i)) : "_";
                aplicarLetraEnBox(box, letra, true);
                continue;
            }

            String letra = (i < intento.length()) ? String.valueOf(intento.charAt(i)) : "_";
            aplicarLetraEnBox(box, letra, false);
        }
    }

    private void aplicarLetraEnBox(StackPane box, String letra, boolean verde) {
        if (box.getChildren().isEmpty()) return;
        if (!(box.getChildren().get(0) instanceof Label l)) return;

        l.setText(letra);

        if (verde) {
            box.setStyle("-fx-background-color: #d1fae5; -fx-background-radius: 10; " +
                    "-fx-border-radius: 10; -fx-border-color: #6ee7b7; -fx-border-width: 2;");
            l.setStyle("-fx-text-fill: #0b3a2a;");
        } else {
            box.setStyle("-fx-background-color: #e5e7eb; -fx-background-radius: 10; " +
                    "-fx-border-radius: 10; -fx-border-color: #cbd5e1; -fx-border-width: 2;");
            l.setStyle("-fx-text-fill: #1f2a44;");
        }
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

    private FlowPane crearTecladoVisual() {
        FlowPane panel = new FlowPane(10, 10);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(680);

        String alfabeto = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        for (char letra : alfabeto.toCharArray()) {
            Button b = new Button(String.valueOf(letra));
            b.setPrefSize(44, 44);
            b.setFocusTraversable(false);
            b.setDisable(false);
            b.setOnAction(null);
            b.setStyle(teclaActiva());
            panel.getChildren().add(b);
        }
        return panel;
    }

    private String teclaActiva() {
        return "-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 100; -fx-border-radius: 100; -fx-border-color: #4338ca; -fx-border-width: 2;";
    }

    private String teclaInactiva() {
        return "-fx-background-color: #e5e7eb; -fx-text-fill: #9ca3af; -fx-font-weight: bold; " +
                "-fx-background-radius: 100; -fx-border-radius: 100; -fx-border-color: #d1d5db; -fx-border-width: 2;";
    }

    private void actualizarTecladoYDinamica() {
        if (juegoApi == null || tecladoPane == null) return;

        String input = (txtEntrada != null ? txtEntrada.getText() : "").toLowerCase();
        String inf = juegoApi.getLimiteInferior().toLowerCase();
        String sup = juegoApi.getLimiteSuperior().toLowerCase();

        if (input.length() >= longitud) {
            boolean victoria = ultimaJugadaFueVictoria;

            for (Node n : tecladoPane.getChildren()) {
                if (n instanceof Button btn && btn.getText().length() == 1) {
                    btn.setDisable(true);
                    btn.setStyle(victoria ? teclaActiva() : teclaInactiva());
                }
            }
            return;
        }

        char minChar = 'a';
        char maxChar = 'z';

        if (inf.startsWith(input) && input.length() < inf.length()) minChar = inf.charAt(input.length());
        if (sup.startsWith(input) && input.length() < sup.length()) maxChar = sup.charAt(input.length());

        boolean fueraPorPrefijo = false;
        if (!input.isEmpty()) {
            String subInf = inf.substring(0, Math.min(input.length(), inf.length()));
            String subSup = sup.substring(0, Math.min(input.length(), sup.length()));
            if (input.compareTo(subInf) < 0) fueraPorPrefijo = true;
            if (input.compareTo(subSup) > 0) fueraPorPrefijo = true;
        }

        for (Node n : tecladoPane.getChildren()) {
            if (!(n instanceof Button btn)) continue;
            if (btn.getText().length() != 1) continue;

            char letra = btn.getText().toLowerCase().charAt(0);
            boolean habilitar = !fueraPorPrefijo && (letra >= minChar && letra <= maxChar);

            btn.setDisable(!habilitar);
            btn.setStyle(habilitar ? teclaActiva() : teclaInactiva());
        }
    }

    private void actualizarUI() {
        lblAttempts.setText(t("Intentos: ", "Attempts: ") + juegoApi.getIntentosRestantes() + "/" + intentos);
        reconstruirFilas();
        actualizarTecladoYDinamica();
        actualizarEstadoBotonesFinJuego();
    }

    private void procesarIntento() {
        String intento = txtEntrada.getText().trim().toLowerCase();

        if (intento.length() != longitud) {
            lblMensajes.setText(t(
                    "La palabra debe tener exactamente " + longitud + " letras.",
                    "The word must be exactly " + longitud + " letters."
            ));
            txtEntrada.requestFocus();
            return;
        }

        boolean procesarTurno = true;

        if (!juegoApi.estaPalabraEnDiccionario(intento)) {
            Alert pregunta = new Alert(Alert.AlertType.CONFIRMATION);
            pregunta.setTitle(t("Palabra no encontrada", "Word not found"));
            pregunta.setHeaderText(t(
                    "La palabra \"" + intento + "\" no está en el diccionario.",
                    "The word \"" + intento + "\" is not in the dictionary."
            ));
            pregunta.setContentText(t("¿Deseas agregarla al diccionario?", "Do you want to add it to the dictionary?"));

            ButtonType si = new ButtonType(t("Sí", "Yes"));
            ButtonType no = new ButtonType(t("No", "No"), ButtonBar.ButtonData.CANCEL_CLOSE);
            pregunta.getButtonTypes().setAll(si, no);

            Optional<ButtonType> r = pregunta.showAndWait();
            if (r.isPresent() && r.get() == si) {
                TextInputDialog significadoDlg = new TextInputDialog();
                significadoDlg.setTitle(t("Agregar palabra", "Add word"));
                significadoDlg.setHeaderText(t("Escribe el significado de la palabra:", "Write the meaning of the word:"));
                significadoDlg.setContentText(t("Significado:", "Meaning:"));
                significadoDlg.showAndWait();

                try {
                    juegoApi.agregarPalabra(intento);
                    popupInfo(t("Diccionario", "Dictionary"),
                            t("Palabra agregada con éxito.", "Word added successfully."));
                } catch (Exception ex) {
                    popupWarn(t("Error", "Error"),
                            t("Error al guardar la palabra: ", "Failed to save word: ") + ex.getMessage());
                    procesarTurno = false;
                }
            } else {
                lblMensajes.setText(t("Intento cancelado. Intenta con otra palabra.",
                        "Attempt canceled. Try another word."));
                procesarTurno = false;
            }
        }

        if (!procesarTurno) {
            txtEntrada.requestFocus();
            return;
        }

        String resultado = juegoApi.jugarTurno(intento);

        boolean ganadoAhora = resultado.startsWith("Ganaste") || resultado.startsWith("You won");
        ultimaJugadaFueVictoria = ganadoAhora;

        actualizarFilaSecretaEnVivo();

        extraerPorcentajesDeTexto(resultado);
        lblMensajes.setText(uiMensaje(resultado));

        if (!ganadoAhora) {
            txtEntrada.clear();
        } else {
            txtEntrada.setEditable(false);
        }

        recalcularPorcentajes();
        actualizarUI();
        actualizarEstadoBotonesFinJuego();
        txtEntrada.requestFocus();

        recalcularPorcentajes();

        actualizarUI();
        txtEntrada.requestFocus();

        if (juegoApi.juegoTerminado()) {
            boolean ganado = juegoApi.getHistorial().contains(juegoApi.getPalabraSecreta());
            mostrarVentanaFin(ganado ? t("¡Ganaste!", "You won!") : t("Perdiste", "You lost"), ganado);
        }
    }

    private void abrirMenuPistas() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(t("Pistas", "Hints"));
        dialog.setHeaderText(t("Selecciona una pista", "Choose a hint"));

        ButtonType btnA = new ButtonType("a", ButtonBar.ButtonData.LEFT);
        ButtonType btnB = new ButtonType("b", ButtonBar.ButtonData.LEFT);
        ButtonType btnC = new ButtonType("c", ButtonBar.ButtonData.LEFT);
        ButtonType cancelar = new ButtonType(t("Cancelar", "Cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(btnA, btnB, btnC, cancelar);

        VBox contenido = new VBox(10);
        contenido.setPadding(new Insets(10));

        Label reglas = new Label(t(
                "a) Recorrer 1% el límite final (solo si ya cambiaste el límite final por defecto)\n" +
                        "b) Recorrer 1% el límite inicial (solo si ya cambiaste el límite inicial por defecto)\n" +
                        "c) Revelar la letra con la que empieza",
                "a) Move upper bound by 1% (only if you already changed the default upper bound)\n" +
                        "b) Move lower bound by 1% (only if you already changed the default lower bound)\n" +
                        "c) Reveal the first letter"
        ));
        contenido.getChildren().add(reglas);

        dialog.getDialogPane().setContent(contenido);

        dialog.setResultConverter(button -> {
            if (button == btnA) return "a";
            if (button == btnB) return "b";
            if (button == btnC) return "c";
            return null;
        });

        Optional<String> res = dialog.showAndWait();
        if (res.isEmpty()) return;

        String opcion = res.get();
        String resultado = juegoApi.obtenerPista(opcion);

        recalcularPorcentajes();

        String msg = uiMensaje(resultado);

        if (resultado.startsWith("Pista:")) {
            pistaUtilizada = true;
            popupInfo(t("Pista usada", "Hint used"), msg);
        } else {
            popupWarn(t("Pista no válida", "Invalid hint"), msg);
        }

        lblMensajes.setText(msg);
        actualizarUI();
        txtEntrada.requestFocus();
    }

    private void mostrarVentanaFin(String tituloStr, boolean ganado) {
        Stage ventanaFin = new Stage();
        ventanaFin.initModality(Modality.APPLICATION_MODAL);
        ventanaFin.setTitle(tituloStr);

        VBox layout = new VBox(18);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: #ffffff;");

        Label titulo = new Label(tituloStr);
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titulo.setStyle(ganado ? "-fx-text-fill: #27ae60;" : "-fx-text-fill: #c0392b;");

        Label secreto = new Label(t("La palabra secreta era: ", "The secret word was: ")
                + juegoApi.getPalabraSecreta().toUpperCase());
        secreto.setStyle("-fx-text-fill: #1f2a44;");

        SoundButton btnMenu = new SoundButton(t("Volver al menú", "Back to menu"));
        btnMenu.establecerSonidoClic(clickSound);
        btnMenu.setStyle(estiloBotonSecundario());
        btnMenu.setOnAction(e -> {
            ventanaFin.close();
            ventanaPrincipal.setScene(escenaConfig);
        });

        ImageButton btnEstadisticas = new ImageButton("estadisticas.png");
        btnEstadisticas.setText(t("Estadísticas", "Stats"));
        btnEstadisticas.establecerImagen(statsImg);
        btnEstadisticas.establecerTamanoImagen(22, 22);
        btnEstadisticas.setStyle("-fx-background-color: #eef2ff; -fx-text-fill: #1f2a44; -fx-padding: 10 18; -fx-background-radius: 12; -fx-border-color: #c7d2fe; -fx-border-width: 2; -fx-border-radius: 12; -fx-font-weight: bold;");
        btnEstadisticas.setOnAction(e -> mostrarEstadisticas());

        SoundButton btnSalir = new SoundButton(t("Salir", "Exit"));
        btnSalir.establecerSonidoClic(clickSound);
        btnSalir.setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #1f2a44; -fx-padding: 10 18; -fx-background-radius: 12; -fx-border-color: #e5e7eb; -fx-border-width: 2; -fx-border-radius: 12; -fx-font-weight: bold;");
        btnSalir.setOnAction(e -> Platform.exit());

        layout.getChildren().addAll(titulo, secreto, btnMenu, btnEstadisticas, btnSalir);
        ventanaFin.setScene(new Scene(layout, 420, 440));
        ventanaFin.showAndWait();
    }

    private void mostrarEstadisticas() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(t("Estadísticas", "Statistics"));
        alerta.setHeaderText(t("Resumen de la partida", "Match summary"));

        String historial = String.join(", ", juegoApi.getHistorial());
        String letras = juegoApi.getLetrasUsadas().toString();

        alerta.setContentText(
                t("Palabras jugadas:\n", "Words played:\n") + historial +
                        "\n\n" + t("Letras usadas:\n", "Letters used:\n") + letras
        );
        alerta.showAndWait();
    }

    private void popupInfo(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void popupWarn(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private String estiloBotonPrincipal() {
        return "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; " +
                "-fx-padding: 12 26; -fx-background-radius: 14;";
    }

    private String estiloBotonSecundario() {
        return "-fx-background-color: #eef2ff; -fx-text-fill: #1f2a44; -fx-font-weight: bold; -fx-font-size: 14px; " +
                "-fx-padding: 10 20; -fx-background-radius: 14; -fx-border-color: #c7d2fe; -fx-border-width: 2; -fx-border-radius: 14;";
    }

    private String t(String es, String en) {
        return (idiomaSeleccionado == 1) ? es : en;
    }

    private String uiMensaje(String resultadoApi) {
        if (resultadoApi == null) return "";

        if (idiomaSeleccionado == 1) {
            return resultadoApi;
        }

        String s = resultadoApi;

        s = s.replace("Ganaste, la palabra secreta es: ", "You won! The secret word is: ");

        s = s.replace("La palabra está fuera de rango. La palabra '", "Out of range. The word '");
        s = s.replace("' está antes del límite inferior actual (", "' is before the current lower bound (");
        s = s.replace("' está después del límite superior actual (", "' is after the current upper bound (");

        s = s.replace("La palabra secreta está después de '", "The secret word is after '");
        s = s.replace("La palabra secreta está antes de '", "The secret word is before '");

        s = s.replace("Pista: Límite final recorrido alfabéticamente un 1%. Nuevo final: ",
                "Hint: Upper bound moved by 1%. New upper bound: ");
        s = s.replace("Pista: Límite inicial recorrido alfabéticamente un 1%. Nuevo inicial: ",
                "Hint: Lower bound moved by 1%. New lower bound: ");
        s = s.replace("Pista: La palabra secreta empieza con la letra '",
                "Hint: The secret word starts with '");
        s = s.replace("'.", "'.");

        s = s.replace("No aplicable. Aún estás en el límite final sin modificar (", "Not applicable. Upper bound has not changed yet (");
        s = s.replace("No aplicable. Aún estás en el límite inicial sin modificar (", "Not applicable. Lower bound has not changed yet (");

        s = s.replace("Opción de pista inválida.", "Invalid hint option.");

        s = s.replace("El límite inicial está a ", "Lower bound is ");
        s = s.replace(" de la palabra secreta y el final a ", " away from the secret word, and upper bound is ");
        s = s.replace(".", ".");

        return s;
    }

    private void extraerPorcentajesDeTexto(String texto) {
        if (texto == null) return;
        Pattern patron = Pattern.compile("a (\\d+\\.\\d+)% .* a (\\d+\\.\\d+)%");
        Matcher matcher = patron.matcher(texto);
        if (matcher.find()) {
            porcentajeInf = matcher.group(1) + "%";
            porcentajeSup = matcher.group(2) + "%";
        }
    }

    private void recalcularPorcentajes() {
        try {
            if (juegoApi == null) return;

            Field f = Betweenle.class.getDeclaredField("palabrasValidasPorLongitud");
            f.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<String> lista = (List<String>) f.get(juegoApi);
            if (lista == null || lista.isEmpty()) return;

            String inf = juegoApi.getLimiteInferior();
            String sup = juegoApi.getLimiteSuperior();
            String secret = juegoApi.getPalabraSecreta();

            int idxInf = lista.indexOf(inf);
            int idxSup = lista.indexOf(sup);
            int idxSec = lista.indexOf(secret);

            if (idxInf == -1) idxInf = 0;
            if (idxSup == -1) idxSup = lista.size() - 1;

            int total = lista.size();

            double pInf = (double) (idxSec - idxInf) / total * 100.0;
            double pSup = (double) (idxSup - idxSec) / total * 100.0;

            porcentajeInf = String.format("%.2f%%", pInf);
            porcentajeSup = String.format("%.2f%%", pSup);

        } catch (Exception ignored) {
        }
    }
}