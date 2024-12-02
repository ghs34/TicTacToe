package p1.client;

import utils.ComUtils;
import utils.Result;
import utils.TicTacToe;

import java.io.IOException;
import java.util.Scanner;

public class GameClient {

    /*
    TO DO.
    Class that encapsulates the game's logic. Sequence of states following the established protocol.
    */

    ComUtils comutils;
    TicTacToe partida = new TicTacToe();
    int idSessio;
    String nomJugador;
    String posicionCliente;
    String posicionServidor;
    int flagResult = -1;

    Scanner scanner = new Scanner(System.in);

    public GameClient(ComUtils comutils) {
        this.comutils = comutils;
    }

    // Método para iniciar el GameClient
    public void start() throws IOException {
        System.out.println("GameClient started");
        init();
        play();
    }

    // Método para solicitar al usuario que ingrese su nombre
    public void pedirNombre(){
        System.out.println("Por favor, ingresa tu nombre: ");

        // Llama al método para solicitar el nombre al usuario
        nomJugador = scanner.nextLine();
    }

    // Inicializa la conexión
    public void init() throws IOException {
        idSessio = 0;
        pedirNombre();

        // Escribe el comando "hello" para iniciar la partida
        comutils.writeHello(idSessio, nomJugador);
        System.out.println("writeHello hecho");

        // Lee la confirmación del servidor de que está listo para comenzar
        idSessio = comutils.readReady();
        System.out.println("readReady hecho");
    }

    // Inicia el juego
    public void play() throws IOException {
        System.out.println("play client");
        int flagAdmit;

        // Escribe el comando para comenzar la partida
        comutils.writePlay(idSessio);
        System.out.println("write play hecho");

        // Lee la admisión del servidor para jugar
        flagAdmit = comutils.readAdmit();
        System.out.println("read admit hecho");

        // Verifica si el idSessio es correcto
        if(flagAdmit == 1) {
            game(); // Inicia el juego
        }
        else {
            comutils.writeError(idSessio, 0, "ERROR"); // Escribe un error si no se admite el turno
        }
    }

    // Controla la partida
    public void game() throws IOException {
        String modo;
        System.out.println("game client");

        // Bucle del juego
        while(true) {
            partida.mostrarTablero(); // Muestra el tablero

            // Bucle para elegir modo de juego (manual o automático)
            while(true) {
                System.out.println("Quieres realizar la accion manualmente o automaticamente ? (M, A)");
                modo = scanner.nextLine();

                if (modo.equalsIgnoreCase("M") || modo.equalsIgnoreCase("A")) {
                    break;
                }
                else {
                    System.out.println("Entrada invalida.");
                }
            }

            // Realiza la acción de acuerdo al modo seleccionado
            if(modo.equalsIgnoreCase("M")) {
                while (true) {
                    pedirPosicion();
                    if (partida.realizarMovimiento(posicionCliente)) {
                        break;
                    } else {
                        System.out.println("Jugada invalida.");
                    }
                }
            }
            else {
                posicionCliente = partida.movimientoAleatorio();
                partida.realizarMovimiento(posicionCliente);
            }

            partida.cambiarJugador(); // Cambia el turno
            partida.mostrarTablero(); // Muestra el tablero actualizado

            // Escribe la acción realizada por el jugador y lee la respuesta del servidor
            comutils.writeAction(idSessio, posicionCliente);
            readResponse();

            partida.mostrarTablero(); // Muestra el tablero después de la respuesta del servidor

            // Verifica el resultado del juego
            if(flagResult == 0) {
                System.out.println("Has perdido.");
                break;
            }
            else if(flagResult == 1) {
                System.out.println("Has ganado!");
                break;
            }
            else if(flagResult == 2) {
                System.out.println("Empate.");
                break;
            }
        }
    }

    // Lee la respuesta del servidor
    public void readResponse() throws IOException {
        // Decisión basada en el opcode recibido
        switch (comutils.getDataInputStream().readByte()) {
            case 5: //ACTION
                posicionServidor = comutils.readAction2();
                partida.realizarMovimiento(posicionServidor);
                partida.cambiarJugador();
                break;
            case 6: //RESULT
                Result resultado = comutils.readResult2();
                posicionServidor = resultado.getStringValue();
                partida.realizarMovimiento(posicionServidor);
                flagResult = resultado.getByteValue();
                break;
            case 8: //ERROR
                //comutils.readError();
                System.out.println("error recieved");
                break;
            default:
                //throw new IOException("Error al leer la respuesta: opcode incorrecto");
                break;
        }
    }

    // Método para solicitar al usuario que ingrese una posición
    public void pedirPosicion(){
        System.out.println("Ingrese la coordenada (ejemplo: 0-0): ");
        posicionCliente = scanner.nextLine();
    }




}