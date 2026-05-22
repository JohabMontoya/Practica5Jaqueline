package com.example.practica5jaqueline;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Betweenle");

        System.out.println("Selecciona el idioma (es/en): ");
        String idiomaSeleccionado = sc.nextLine().trim();

        System.out.println("Selecciona la dificultad (ingrese la longitud de la palabra). Facil=5, Intermedio=6, Dificil=N: ");
        int longitud = Integer.parseInt(sc.nextLine().trim());

        System.out.println("¿Cuántas oportunidades quieres? (10, 12, o 14): ");
        int intentos = Integer.parseInt(sc.nextLine().trim());

        Betweenle juegoApi;
        try {
            juegoApi = new Betweenle(longitud, intentos, idiomaSeleccionado);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Asegúrate de que existan palabras en el archivo.");
            return;
        }

        System.out.println("\nEncuentra la palabra secreta de " + longitud + " letras.");
        System.out.println("Tus límites alfabéticos inician en " + juegoApi.getLimiteInferior() + " a " + juegoApi.getLimiteSuperior());

        boolean pistaUtilizada = false;

        while (!juegoApi.juegoTerminado()) {
            System.out.println("\n-------------------------------------------------");
            System.out.println("Intentos restantes: " + juegoApi.getIntentosRestantes());
            System.out.println("Rango actual: " + juegoApi.getLimiteInferior() + " < ? < " + juegoApi.getLimiteSuperior());
            System.out.println("Letras utilizadas en esta ronda: " + juegoApi.getLetrasUsadas());

            if (!pistaUtilizada) {
                System.out.print("Ingresa tu palabra (o escribe '1' para pedir ayuda): ");
            } else {
                System.out.print("Ingresa tu palabra: ");
            }

            String entrada = sc.nextLine().trim().toLowerCase();

            if (entrada.equals("1")) {
                if (pistaUtilizada) {
                    System.out.println("Ya no tienes pistas disponibles");
                    continue;
                }

                System.out.println("Opciones de pista:");
                System.out.println("a) Recorrer un 1% la palabra de final (solo si ya cambiaste el límite final default)");
                System.out.println("b) Recorrer un 1% la palabra de inicial (solo si ya cambiaste el límite inicial default)");
                System.out.println("c) Revelar la letra con la que empieza");
                System.out.print("Elige una opción (a, b, c): ");
                String opcionPista = sc.nextLine();
                System.out.println(juegoApi.obtenerPista(opcionPista));

                pistaUtilizada = true;
                continue;
            }

            if (entrada.length() != longitud) {
                System.out.println("La palabra debe tener exactamente " + longitud + " letras.");
                continue;
            }

            if (!juegoApi.estaPalabraEnDiccionario(entrada)) {
                System.out.println("La palabra '" + entrada + "' no está en el diccionario.");
                System.out.print("¿Deseas agregarla al diccionario? (s/n): ");
                if (sc.nextLine().trim().equalsIgnoreCase("s")) {
                    juegoApi.agregarPalabra(entrada);
                    System.out.println("Palabra agregada con éxito al diccionario.");
                } else {
                    System.out.println("Intento cancelado. Intenta con otra palabra.");
                    continue;
                }
            }

            String resultadoTurno = juegoApi.jugarTurno(entrada);
            System.out.println("\n=> " + resultadoTurno);
            List<String> historial = juegoApi.getHistorial();
            if (!historial.isEmpty()) {
                System.out.print("Historial de intentos: [");
                Iterator<String> iterador = historial.iterator();
                int contador = 1;
                while (iterador.hasNext()) {
                    System.out.print(contador + ".-" + iterador.next());
                    if (iterador.hasNext()) System.out.print(", ");
                    contador++;
                }
                System.out.println("]");
            }
        }

        if (juegoApi.getHistorial().contains(juegoApi.getPalabraSecreta())) {
            System.out.println("\nFelicidades has adivinado la palabra.");
        } else {
            System.out.println("\nTe has quedado sin intentos, la palabra secreta era: " + juegoApi.getPalabraSecreta());
        }

        sc.close();
    }
}