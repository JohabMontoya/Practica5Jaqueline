package com.example.practica5jaqueline;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Betweenle");

        // 1. Validación estricta para el IDIOMA (Usando bandera booleana en lugar de break)
        int idiomaSeleccionado = 0;
        boolean idiomaValido = false;
        while (!idiomaValido) {
            System.out.println("Selecciona el idioma (1. Español, 2. Inglés): ");
            try {
                idiomaSeleccionado = Integer.parseInt(sc.nextLine().trim());
                if (idiomaSeleccionado == 1 || idiomaSeleccionado == 2) {
                    idiomaValido = true;
                } else {
                    System.out.println("Error: Por favor, selecciona la opción 1 o 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Entrada inválida. Debes ingresar un número entero.");
            }
        }

        // 2. Validación estricta para la LONGITUD (Dificultad)
        int longitud = 0;
        boolean longitudValida = false;
        while (!longitudValida) {
            System.out.println("Selecciona la dificultad (ingrese la longitud de la palabra). Fácil=5, Intermedio=6, Difícil=N: ");
            try {
                longitud = Integer.parseInt(sc.nextLine().trim());
                if (longitud > 6 || longitud == 5 || longitud == 6) {
                    longitudValida = true;
                } else {
                    System.out.println("Error: La longitud debe ser mayor a 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Entrada inválida. Debes ingresar un número entero.");
            }
        }

        int intentos = 0;
        boolean intentosValidos = false;
        while (!intentosValidos) {
            System.out.println("¿Cuántas oportunidades quieres? (10, 12, o 14): ");
            try {
                intentos = Integer.parseInt(sc.nextLine().trim());
                if (intentos == 10 || intentos == 12 || intentos == 14) {
                    intentosValidos = true;
                } else {
                    System.out.println("Error: Debes elegir exactamente 10, 12 o 14.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Entrada inválida. Debes ingresar un número entero.");
            }
        }

        Betweenle juegoApi;
        try {
            juegoApi = new Betweenle(idiomaSeleccionado, longitud, intentos);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Asegúrate de que existan palabras en el archivo para esa longitud.");
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
                } else {
                    System.out.println("Opciones de pista:");
                    System.out.println("a) Recorrer un 1% la palabra de final (solo si ya cambiaste el límite final default)");
                    System.out.println("b) Recorrer un 1% la palabra de inicial (solo si ya cambiaste el límite inicial default)");
                    System.out.println("c) Revelar la letra con la que empieza");
                    System.out.print("Elige una opción (a, b, c): ");
                    String opcionPista = sc.nextLine();

                    String resultadoPista = juegoApi.obtenerPista(opcionPista);
                    System.out.println(resultadoPista);

                    if (resultadoPista.startsWith("Pista:")) {
                        pistaUtilizada = true;
                    } else {
                        System.out.println("No se ha consumido tu pista");
                    }
                }
            } else if (entrada.length() != longitud) {
                System.out.println("La palabra debe tener exactamente " + longitud + " letras.");
            } else {
                boolean procesarTurno = true;

                if (!juegoApi.estaPalabraEnDiccionario(entrada)) {
                    System.out.println("La palabra '" + entrada + "' no está en el diccionario.");
                    System.out.print("¿Deseas agregarla al diccionario? (s/n): ");
                    if (sc.nextLine().trim().equalsIgnoreCase("s")) {
                        juegoApi.agregarPalabra(entrada);
                        System.out.println("Escribe el signficado de la palabra: ");
                        sc.nextLine();
                        System.out.println("Palabra agregada con éxito al diccionario.");
                    } else {
                        System.out.println("Intento cancelado. Intenta con otra palabra.");
                        procesarTurno = false;
                    }
                }

                if (procesarTurno) {
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