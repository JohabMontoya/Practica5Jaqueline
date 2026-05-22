package com.example.practica5jaqueline;

public class Betweenle {
    private String palabraSecreta;
    private String limiteInferior;
    private String limiteSuperior;
    private String limiteInferiorInicial;
    private String limiteSuperiorInicial;

    private int intentosRestantes;
    private int intentosMaximos;
    private int longitudPalabra;

    private String idioma;

    public Betweenle(int longitudPalabra, int intentos, String idioma) {
        this.longitudPalabra = longitudPalabra;
        this.intentosMaximos = intentos;
        this.intentosRestantes = intentos;
        this.idioma = idioma.toLowerCase();

        this.limiteInferiorInicial = "a".repeat(longitudPalabra);
        this.limiteSuperiorInicial = "z".repeat(longitudPalabra);
        this.limiteInferior = limiteInferiorInicial;
        this.limiteSuperior = limiteSuperiorInicial;
    }

}
