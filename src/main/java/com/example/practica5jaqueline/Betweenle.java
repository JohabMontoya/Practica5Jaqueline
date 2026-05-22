package com.example.practica5jaqueline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    private String archivoDiccionario;

    private String idioma;

    public Betweenle(int longitudPalabra, int intentos, String idioma) {
        this.longitudPalabra = longitudPalabra;
        this.intentosMaximos = intentos;
        this.intentosRestantes = intentos;
        this.idioma = idioma.toLowerCase();

        if (idioma.equalsIgnoreCase("es")) {
            this.archivoDiccionario = "BetweenleEspanol.txt";
        } else {
            this.archivoDiccionario = "BetweenleIngles.txt";
        }

        this.diccionario = new HashMap<>();
        this.letrasUsadas = new HashSet<>();
        this.historial = new ArrayList<>();

        this.limiteInferiorInicial = "a".repeat(longitudPalabra);
        this.limiteSuperiorInicial = "z".repeat(longitudPalabra);
        this.limiteInferior = limiteInferiorInicial;
        this.limiteSuperior = limiteSuperiorInicial;

        cargarDiccionario();
    }

    private void cargarDiccionario() {
        try (BufferedReader br = new BufferedReader(new FileReader(archivoDiccionario))) {
            diccionario = br.lines()
                    .map(String::trim)
                    .filter(linea -> !linea.isEmpty())
                    .collect(Collectors.toMap(
                            palabra -> palabra.toLowerCase(),
                            palabra -> palabra.length(),
                            (existente, reemplazo) -> existente,
                            HashMap::new
                    ));
        } catch (IOException e) {
            System.out.println("Error al cargar el diccionario: " + e.getMessage());
        }
    }

    private void configurarJuego() {
        palabrasValidasPorLongitud = diccionario.entrySet().stream()
                .filter(entrada -> entrada.getValue() == longitudPalabra)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());

        if (palabrasValidasPorLongitud.isEmpty()) {
            throw new IllegalStateException("No hay palabras de longitud " + longitudPalabra + " en el archivo " + archivoDiccionario);
        }

        int indiceAleatorio = new Random().nextInt(palabrasValidasPorLongitud.size());
        palabraSecreta = palabrasValidasPorLongitud.get(indiceAleatorio);
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
