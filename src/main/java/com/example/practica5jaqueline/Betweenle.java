package com.example.practica5jaqueline;

import java.io.*;
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

    private int idioma;

    public Betweenle(int idioma,  int longitudPalabra, int intentos) throws IOException {
        this.longitudPalabra = longitudPalabra;
        this.intentosMaximos = intentos;
        this.intentosRestantes = intentos;
        this.idioma = idioma;

        if (idioma == 1) {
            this.archivoDiccionario = "BetweenleEspanol.txt";
        } else if (idioma == 2) {
            this.archivoDiccionario = "BetweenleEnglish.txt";
        } else {
            throw new IllegalArgumentException("Opción de idioma inválida.");
        }

        this.diccionario = new HashMap<>();
        this.letrasUsadas = new HashSet<>();
        this.historial = new ArrayList<>();

        this.limiteInferiorInicial = "a".repeat(longitudPalabra);
        this.limiteSuperiorInicial = "z".repeat(longitudPalabra);
        this.limiteInferior = limiteInferiorInicial;
        this.limiteSuperior = limiteSuperiorInicial;

        cargarDiccionario();
        configurarJuego();
    }

    public String getLimiteInferiorInicial(){
        return limiteInferiorInicial;
    }

    public String getLimiteSuperiorInicial(){
        return limiteSuperiorInicial;
    }

    private String limpiarAcentos(String texto) {
        if (texto == null) return null;
        String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        Pattern patron = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return patron.matcher(textoNormalizado).replaceAll("");
    }

    private void cargarDiccionario() throws IOException {
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
        } catch (NumberFormatException e) {
            throw new IOException("Error de formato numérico en el archivo: " + e.getMessage());
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

    public void agregarPalabra(String palabra) throws IOException {
        palabra = limpiarAcentos(palabra).toLowerCase();

        diccionario.put(palabra, palabra.length());

        if (palabra.length() == longitudPalabra && !palabrasValidasPorLongitud.contains(palabra)) {
            palabrasValidasPorLongitud.add(palabra);
            palabrasValidasPorLongitud.sort(String::compareTo);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoDiccionario))) {
            List<Map.Entry<String, Integer>> entradasOrdenadas = diccionario.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toList());

            for (Map.Entry<String, Integer> entrada : entradasOrdenadas) {
                bw.write(entrada.getKey() + "," + entrada.getValue());
                bw.newLine();
            }
        }
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

    public String obtenerPista(String opcion) {
        int indiceInferior = Math.max(0, palabrasValidasPorLongitud.indexOf(limiteInferior));
        int indiceSuperior = palabrasValidasPorLongitud.indexOf(limiteSuperior);
        if (indiceSuperior == -1) indiceSuperior = palabrasValidasPorLongitud.size() - 1;

        int rango = indiceSuperior - indiceInferior;

        switch (opcion.toLowerCase()) {
            case "a":
                if (limiteSuperior.equals(limiteSuperiorInicial)) {
                    return "No aplicable. Aún estás en el límite final sin modificar (" + limiteSuperiorInicial + ").";
                }
                int pasoA = Math.max(1, (int)(rango * 0.01));
                limiteSuperior = palabrasValidasPorLongitud.get(indiceSuperior - pasoA);
                return "Pista: Límite final recorrido alfabéticamente un 1%. Nuevo final: " + limiteSuperior;
            case "b":
                if (limiteInferior.equals(limiteInferiorInicial)) {
                    return "No aplicable. Aún estás en el límite inicial sin modificar (" + limiteInferiorInicial + ").";
                }
                int pasoB = Math.max(1, (int)(rango * 0.01));
                limiteInferior = palabrasValidasPorLongitud.get(indiceInferior + pasoB);
                return "Pista: Límite inicial recorrido alfabéticamente un 1%. Nuevo inicial: " + limiteInferior;
            case "c":
                return "Pista: La palabra secreta empieza con la letra '" + palabraSecreta.charAt(0) + "'.";
            default:
                return "Opción de pista inválida.";
        }
    }

    public boolean estaPalabraEnDiccionario(String palabra) {
        return diccionario.containsKey(limpiarAcentos(palabra).toLowerCase());
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

    public List<String> getHistorial() {
        return historial;
    }
    public Set<Character> getLetrasUsadas() {
        return letrasUsadas;
    }

    public boolean juegoTerminado() {
        return intentosRestantes <= 0 || historial.contains(palabraSecreta);
    }
}