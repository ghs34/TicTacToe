package p1.server;
import utils.ComUtils;
import utils.TicTacToe;

import java.net.Socket;
import java.util.Random;

import java.io.IOException;
import java.util.Scanner;

public class GameHandler implements Runnable {
    /*
    TO DO
    Protocol dynamics from Server.
    Methods: run(), init(), play().
    */
    ComUtils comutils;
    TicTacToe partida = new TicTacToe();
    int idSessio;
    Random random = new Random();

    String posicionCliente;
    String posicionServidor;
    boolean end = false;
    int flagResult;

    public GameHandler(Socket socket) throws IOException {
        //this.comutils = comutils;
        this.comutils = new ComUtils(socket.getInputStream(),socket.getOutputStream());
    }

    // Método para iniciar el GameHandler
    public void start() throws IOException {
        System.out.println("GameHandler started");
        init();
    }

    // Inicializa la conexión
    public void init() throws IOException {
        // Lee el mensaje de inicio del cliente
        idSessio = comutils.readHello();
        System.out.println("readHello hecho");

        // Genera un ID de sesión de cinco dígitos que no comience por 0 si el recibido es 0
        if(idSessio == 0){
            idSessio = random.nextInt(90000) + 10000;
        }

        // Envía la confirmación de que está listo para jugar al cliente
        comutils.writeReady(idSessio);
        System.out.println("writeReady hecho");
    }

    // Inicia el juego
    public void play() throws IOException{
        System.out.println("play handler");
        int idSessioPlay;
        int flagAdmit;

        // Lee el comando de juego del cliente
        idSessioPlay = comutils.readPlay();
        System.out.println("read play hecho");

        // Verifica si el ID de sesión recibido coincide con el propio y admite el juego en consecuencia
        if(idSessio == idSessioPlay) {
            flagAdmit = 1;
        }
        else {
            flagAdmit = 0;
        }

        // Envía la admisión al cliente
        comutils.writeAdmit(idSessio, flagAdmit);
        System.out.println("write admit hecho");
    }

    // Controla la partida
    public void game() throws IOException {
        System.out.println("game handler");
        while(true) {
            System.out.println("game handler while true");
            posicionCliente = comutils.readAction(); // Lee la acción del cliente
            partida.realizarMovimiento(posicionCliente); // Realiza el movimiento del cliente
            partida.mostrarTablero(); // Muestra el tablero
            partida.cambiarJugador(); // Cambia el turno

            verificarResultado(); // Verifica el resultado del juego
            if(end) {
                break;
            }

            posicionServidor = partida.movimientoAleatorio(); // Realiza un movimiento aleatorio del servidor
            partida.realizarMovimiento(posicionServidor); // Realiza el movimiento del servidor
            partida.cambiarJugador(); // Cambia el turno
            partida.mostrarTablero(); // Muestra el tablero

            verificarResultado(); // Verifica el resultado del juego
            if(end) {
                break;
            }

            comutils.writeAction(idSessio, posicionServidor); // Envía la acción del servidor
        }
    }

    // Verifica el resultado
    public void verificarResultado() throws IOException {
        switch(partida.verificarGanador()) {
            case 'X':
                posicionServidor = "---";
                flagResult = 1;
                comutils.writeResult(idSessio, posicionServidor, flagResult); // Envía el resultado al cliente
                end = true; // Establece el fin del juego
                break;
            case 'O':
                flagResult = 0;
                comutils.writeResult(idSessio, posicionServidor, flagResult); // Envía el resultado al cliente
                end = true; // Establece el fin del juego
                break;
            case 'E':
                flagResult = 2;
                comutils.writeResult(idSessio, posicionServidor, flagResult); // Envía el resultado al cliente
                end = true; // Establece el fin del juego
                break;
            default:
                break;
        }
    }

    // Método para ejecutar el GameHandler del servidor en un hilo
    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            play();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            game();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
