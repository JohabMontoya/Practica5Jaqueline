package com.example.practica5jaqueline;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Betweenle {

    private Map<String, Integer> diccionario;
    private Set<Character> letrasUsadas;
    private List<String> palabrasValidasPorLongitud;
    private List<String> historial;

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

    public int getIntentosRestantes(){
        return intentosRestantes;
    }

    public String getLimiteInferior(){
        return limiteInferior;
    }

    public String getLimiteSuperior(){
        return limiteSuperior;
    }

    public String getPalabraSecreta(){
        return palabraSecreta;
    }

}
