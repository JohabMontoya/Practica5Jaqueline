package com.example.practica5jaqueline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
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
            this.archivoDiccionario = "BetweenleEnglish.txt";
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

    private String limpiarAcentos(String texto) {
        if (texto == null) return null;
        String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        Pattern patron = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return patron.matcher(textoNormalizado).replaceAll("");
    }

    private void cargarDiccionario() {
        try (BufferedReader br = new BufferedReader(new FileReader(archivoDiccionario))) {
            diccionario = br.lines()
                    .map(String::trim)
                    .filter(linea -> !linea.isEmpty())
                    .collect(Collectors.toMap(
                            linea -> {
                                String palabra = linea.contains(",") ? linea.split(",")[0] : linea;
                                return limpiarAcentos(palabra).toLowerCase();
                            },
                            linea -> {
                                if (linea.contains(",")) {
                                    return Integer.parseInt(linea.split(",")[1]);
                                } else {
                                    return limpiarAcentos(linea).length();
                                }
                            },
                            (existente, reemplazo) -> existente,
                            HashMap::new
                    ));
        } catch (IOException e) {
            System.out.println("Error al cargar el diccionario: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error de formato numérico en el archivo: " + e.getMessage());
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

    private String calcularPorcentajes() {
        int indiceInferior = palabrasValidasPorLongitud.indexOf(limiteInferior);
        int indiceSuperior = palabrasValidasPorLongitud.indexOf(limiteSuperior);
        int indiceSecreto = palabrasValidasPorLongitud.indexOf(palabraSecreta);

        if (indiceInferior == -1) indiceInferior = 0;
        if (indiceSuperior == -1) indiceSuperior = palabrasValidasPorLongitud.size() - 1;

        int rangoTotal = indiceSuperior - indiceInferior;
        if (rangoTotal <= 0) return "El rango se ha cerrado completamente.";

        int totalPalabras = palabrasValidasPorLongitud.size();

        double porcentajeInferior = (double)(indiceSecreto - indiceInferior) / totalPalabras * 100;
        double porcentajeSuperior = (double)(indiceSuperior - indiceSecreto) / totalPalabras * 100;

        return String.format("El límite inicial está a %.2f%% de la palabra secreta y el final a %.2f%%.",
                porcentajeInferior, porcentajeSuperior);
    }

    public String jugarTurno(String intento) {
        intento = intento.toLowerCase();

        for (char letra : intento.toCharArray()) {
            letrasUsadas.add(letra);
        }

        if (intento.compareTo(limiteInferior) <= 0) {
            return "La palabra está fuera de rango. La palabra '" + intento + "' está antes del límite inferior actual (" + limiteInferior + ").";
        }
        if (intento.compareTo(limiteSuperior) >= 0) {
            return "La palabra está fuera de rango. La palabra '" + intento + "' está después del límite superior actual (" + limiteSuperior + ").";
        }

        historial.add(intento);
        intentosRestantes--;

        if (intento.equals(palabraSecreta)) {
            return "Ganaste, la palabra secreta es: " + palabraSecreta;
        }

        if (intento.compareTo(palabraSecreta) < 0) {
            limiteInferior = intento;
            return "La palabra secreta está después de '" + intento + "'.\n" + calcularPorcentajes();
        } else {
            limiteSuperior = intento;
            return "La palabra secreta está antes de '" + intento +  "'.\n" + calcularPorcentajes();
        }
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
