package com.example.practica5jaqueline;

import javafx.scene.control.Button;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import java.util.Arrays;
import java.util.List;


public class ShapeButton extends Button {


    private double tamano = 80;


    private static final String FORMA_CIRCULO   = "circulo";
    private static final String FORMA_HEXAGONO  = "hexagono";
    private static final String FORMA_TRIANGULO = "triangulo";
    private static final String FORMA_ESTRELLA  = "estrella";
    private static final String FORMA_PERSONALIZADA = "personalizada";


    private String formaActual = FORMA_CIRCULO;
    private static final List<String> FORMAS_ORDEN = Arrays.asList(
            FORMA_CIRCULO, FORMA_HEXAGONO, FORMA_TRIANGULO, FORMA_ESTRELLA
    );


    private int indiceForma = 0;


    public ShapeButton() {
        this(80);
    }


    public ShapeButton(double tamano) {
        super();
        setFocusTraversable(false);
        establecerTamanoBoton(tamano);
        hacerCirculo();
        establecerColorFondo("#3498db");
        setOnAction(e -> cambiarFormaSiguiente());
    }


    public void establecerTamanoBoton(double tamano) {
        this.tamano = tamano;
        setPrefSize(tamano, tamano);
        setMinSize(tamano, tamano);
        setMaxSize(tamano, tamano);
    }


    public double obtenerTamanoBoton() {
        return tamano;
    }


    public void hacerCirculo() {
        formaActual = FORMA_CIRCULO;
        indiceForma = FORMAS_ORDEN.indexOf(FORMA_CIRCULO);
        aplicarForma();
    }


    public void hacerHexagono() {
        formaActual = FORMA_HEXAGONO;
        indiceForma = FORMAS_ORDEN.indexOf(FORMA_HEXAGONO);
        aplicarForma();
    }


    public void hacerTriangulo() {
        formaActual = FORMA_TRIANGULO;
        indiceForma = FORMAS_ORDEN.indexOf(FORMA_TRIANGULO);
        aplicarForma();
    }


    public void hacerEstrella() {
        formaActual = FORMA_ESTRELLA;
        indiceForma = FORMAS_ORDEN.indexOf(FORMA_ESTRELLA);
        aplicarForma();
    }

    public void establecerColorFondo(String colorCss) {
        String estilo = String.format(
                "-fx-background-color: %s; -fx-background-insets: 0; -fx-padding: 0; -fx-cursor: hand;",
                colorCss
        );
        setStyle(estilo);
    }

    public void cambiarFormaSiguiente() {
        if (indiceForma < 0 || indiceForma >= FORMAS_ORDEN.size()) {
            indiceForma = 0;
        }
        indiceForma = (indiceForma + 1) % FORMAS_ORDEN.size();
        formaActual = FORMAS_ORDEN.get(indiceForma);
        aplicarForma();
    }


    private void aplicarForma() {
        Shape forma;
        switch (formaActual) {
            case FORMA_CIRCULO:
                forma = new Circle(tamano / 2.0);
                break;
            case FORMA_HEXAGONO:
                forma = crearPoligonoRegular(6, tamano / 2.0);
                break;
            case FORMA_TRIANGULO:
                forma = crearTriangulo(tamano);
                break;
            case FORMA_ESTRELLA:
                forma = crearEstrella(5, tamano * 0.45, tamano * 0.2);
                break;
            default:
                forma = new Circle(tamano / 2.0);
        }
        setShape(forma);
        setPickOnBounds(false);
    }


    private Polygon crearPoligonoRegular(int lados, double radio) {
        Polygon poligono = new Polygon();
        for (int i = 0; i < lados; i++) {
            double angulo = Math.toRadians((360.0 / lados) * i - 90);
            double x = radio + radio * Math.cos(angulo);
            double y = radio + radio * Math.sin(angulo);
            poligono.getPoints().addAll(x, y);
        }
        return poligono;
    }


    private Polygon crearTriangulo(double tamano) {
        double w = tamano, h = tamano;
        Polygon p = new Polygon();
        p.getPoints().addAll(
                w / 2.0, 0.0,
                0.0, h,
                w, h
        );
        return p;
    }


    private Polygon crearEstrella(int puntas, double radioExterno, double radioInterno) {
        Polygon estrella = new Polygon();
        double centro = tamano / 2.0;
        int numPuntos = puntas * 2;
        for (int i = 0; i < numPuntos; i++) {
            double angulo = Math.PI / puntas * i - Math.PI / 2.0;
            double r = (i % 2 == 0) ? radioExterno : radioInterno;
            double x = centro + Math.cos(angulo) * r;
            double y = centro + Math.sin(angulo) * r;
            estrella.getPoints().addAll(x, y);
        }
        return estrella;
    }
}
