package utils;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class TicTacToe {
    private char[][] tablero;
    private char jugadorActual;

    public TicTacToe() {
        tablero = new char[3][3];
        jugadorActual = 'X'; // Empieza el jugador X
        inicializarTablero();
        //mostrarTablero();
    }

    // Inicializa el tablero con espacios en blanco
    private void inicializarTablero() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tablero[i][j] = ' ';
            }
        }
    }

    // Método para pedir un movimiento al usuario
    public String pedirMovimiento() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la coordenada (ejemplo: 0-0): ");
        String coordenada = scanner.nextLine();
        return coordenada;
    }

    public String movimientoAleatorio() {
        Random random = new Random();
        int fila, columna;
        do {
            fila = random.nextInt(3);
            columna = random.nextInt(3);
        } while (tablero[fila][columna] != ' ');
        return fila + "-" + columna;
    }

    // Muestra el tablero actual
    public void mostrarTablero() {
        //System.out.println(" 0-0 | 0-1 | 0-2");
        System.out.println("------------");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(" " + tablero[i][j] + " |");
            }
            System.out.println("\n------------");
        }
    }

    // Realiza un movimiento en el tablero
    public boolean realizarMovimiento(String coordenada) {
        // Verificar si la cadena tiene el formato correcto
        if (!coordenada.matches("\\d-\\d")) {
            return false; // Formato incorrecto
        }

        // Extraer fila y columna de la cadena
        String[] partes = coordenada.split("-");
        int fila = Integer.parseInt(partes[0]);
        int columna = Integer.parseInt(partes[1]);

        // Verificar si las coordenadas están dentro de los límites y si la casilla está vacía
        if (fila < 0 || fila >= 3 || columna < 0 || columna >= 3 || tablero[fila][columna] != ' ') {
            return false; // Movimiento inválido
        }

        // Realizar el movimiento
        tablero[fila][columna] = jugadorActual;
        return true;
    }

    // Cambia al siguiente jugador
    public void cambiarJugador() {
        jugadorActual = (jugadorActual == 'X') ? 'O' : 'X';
    }

    // Verifica si hay un ganador o si hay empate
    public char verificarGanador() {
        // Verificar filas y columnas
        for (int i = 0; i < 3; i++) {
            if (tablero[i][0] == tablero[i][1] && tablero[i][1] == tablero[i][2] && tablero[i][0] != ' ') {
                return tablero[i][0]; // Ganador en la fila i
            }
            if (tablero[0][i] == tablero[1][i] && tablero[1][i] == tablero[2][i] && tablero[0][i] != ' ') {
                return tablero[0][i]; // Ganador en la columna i
            }
        }

        // Verificar diagonales
        if (tablero[0][0] == tablero[1][1] && tablero[1][1] == tablero[2][2] && tablero[0][0] != ' ') {
            return tablero[0][0]; // Ganador en la diagonal principal
        }
        if (tablero[0][2] == tablero[1][1] && tablero[1][1] == tablero[2][0] && tablero[0][2] != ' ') {
            return tablero[0][2]; // Ganador en la diagonal secundaria
        }

        // Verificar si hay empate
        boolean empate = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tablero[i][j] == ' ') {
                    empate = false;
                    break;
                }
            }
            if (!empate) {
                break;
            }
        }
        if (empate) {
            return 'E'; // Empate
        }

        // Si no hay ganador ni empate, retorna espacio en blanco
        return ' ';
    }

    // Método principal para ejecutar el juego
    public void jugar() {
        mostrarTablero();
        Scanner scanner = new Scanner(System.in);
        while (verificarGanador() == ' ') {
            System.out.println("Turno del jugador " + jugadorActual);
            String coordenada;
            do {
                System.out.print("Ingrese la coordenada (ejemplo: 0-0): ");
                coordenada = scanner.nextLine();
            } while (!realizarMovimiento(coordenada));
            mostrarTablero();
            cambiarJugador();
        }
        scanner.close();

        char resultado = verificarGanador();
        if (resultado == 'E') {
            System.out.println("¡Empate!");
        } else {
            System.out.println("¡El jugador " + resultado + " ha ganado!");
        }
    }



}
