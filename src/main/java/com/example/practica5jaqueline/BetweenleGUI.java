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


        ventanaPrincipal.show();
    }
}