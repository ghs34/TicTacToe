package utils;

import java.io.*;

public class ComUtils {


    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public ComUtils(InputStream inputStream, OutputStream outputStream) throws IOException {
        dataInputStream = new DataInputStream(inputStream);
        dataOutputStream = new DataOutputStream(outputStream);
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public int read_int32() throws IOException {
        byte bytes[] = read_bytes(4);

        return bytesToInt32(bytes,Endianness.BIG_ENNDIAN);
    }

    public void write_int32(int number) throws IOException {
        byte bytes[] = int32ToBytes(number, Endianness.BIG_ENNDIAN);

        dataOutputStream.write(bytes, 0, 4);
    }

    public String read_string(int size) throws IOException {
        String result;
        byte[] bStr = new byte[size];
        char[] cStr = new char[size];

        bStr = read_bytes(size);

        for(int i = 0; i < size;i++)
            cStr[i]= (char) bStr[i];

        result = String.valueOf(cStr);

        return result.trim();
    }

    public void write_string(String str) throws IOException {
       
        int size = str.length();
        byte bStr[] = new byte[size];
        for(int i = 0; i < size; i++)
            bStr[i] = (byte) str.charAt(i);



        dataOutputStream.write(bStr, 0,size);
    }

    private byte[] int32ToBytes(int number, Endianness endianness) {
        byte[] bytes = new byte[4];

        if(Endianness.BIG_ENNDIAN == endianness) {
            bytes[0] = (byte)((number >> 24) & 0xFF);
            bytes[1] = (byte)((number >> 16) & 0xFF);
            bytes[2] = (byte)((number >> 8) & 0xFF);
            bytes[3] = (byte)(number & 0xFF);
        }
        else {
            bytes[0] = (byte)(number & 0xFF);
            bytes[1] = (byte)((number >> 8) & 0xFF);
            bytes[2] = (byte)((number >> 16) & 0xFF);
            bytes[3] = (byte)((number >> 24) & 0xFF);
        }
        return bytes;
    }

    /* Passar de bytes a enters */
    private int bytesToInt32(byte bytes[], Endianness endianness) {
        int number;

        if(Endianness.BIG_ENNDIAN == endianness) {
            number=((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        }
        else {
            number=(bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
                    ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        }
        return number;
    }
    //llegir bytes.
    private byte[] read_bytes(int numBytes) throws IOException {
        int len = 0;
        byte bStr[] = new byte[numBytes];
        int bytesread = 0;
        do {
            bytesread = dataInputStream.read(bStr, len, numBytes-len);
            if (bytesread == -1)
                throw new IOException("Broken Pipe");
            len += bytesread;
        } while (len < numBytes);
        return bStr;
    }

    public String read_variable_string() throws IOException {
        byte readByte;
        byte previousByte = -1;
        StringBuilder output = new StringBuilder();

        while(true) {
            readByte = read_bytes(1)[0];

            if (previousByte == 0 && readByte == 0)
                break;

            if(readByte != 0)
                output.append((char) readByte);

            previousByte = readByte;
        }
        return output.toString();
    }

    public enum Endianness {
        BIG_ENNDIAN,
        LITTLE_ENDIAN
    }

    public void writeHello(int idSessio, String nomJugador) throws IOException {
        // Escribe el opcode para "hello"
        this.dataOutputStream.writeByte(1);
        // Escribe el ID de la sesión
        this.dataOutputStream.writeInt(idSessio);
        // Escribe el nombre del jugador
        this.write_string(nomJugador);
        // Escribe bytes nulos
        this.dataOutputStream.writeByte(0);
        this.dataOutputStream.writeByte(0);
        // Limpia el flujo de salida
        this.dataOutputStream.flush();
    }

    public void writeReady(int idSessio) throws IOException {
        // Escribe el opcode para "ready"
        this.dataOutputStream.writeByte(2);
        // Escribe el ID de la sesión
        this.dataOutputStream.writeInt(idSessio);
        // Limpia el flujo de salida
        this.dataOutputStream.flush();
    }

    public void writePlay(int idSessio) throws IOException {
        // Escribe el opcode para "play"
        this.dataOutputStream.writeByte(3);
        // Escribe el ID de la sesión
        this.dataOutputStream.writeInt(idSessio);
        // Limpia el flujo de salida
        this.dataOutputStream.flush();
    }

    public void writeAdmit(int idSessio, int flag) throws IOException {
        // Escribe el opcode para "admit"
        this.dataOutputStream.writeByte(4);
        // Escribe el ID de la sesión
        this.dataOutputStream.writeInt(idSessio);
        // Escribe la bandera de admisión
        this.dataOutputStream.writeByte(flag);
        // Limpia el flujo de salida
        this.dataOutputStream.flush();
    }

    public void writeAction(int idSessio, String posicio) throws IOException {
        // Escribe el opcode para "action"
        this.dataOutputStream.writeByte(5);
        // Escribe el ID de la sesión
        this.dataOutputStream.writeInt(idSessio);
        // Escribe la posición de la acción
        this.write_string(posicio);
        // Limpia el flujo de salida
        this.dataOutputStream.flush();
    }

    public void writeResult(int idSessio, String posicio, int flag) throws IOException {
        // Escribe el opcode para "result"
        this.dataOutputStream.writeByte(6);
        // Escribe el ID de la sesión
        this.dataOutputStream.writeInt(idSessio);
        // Escribe la posición de la acción
        this.write_string(posicio);
        // Escribe la bandera de resultado
        this.dataOutputStream.writeByte(flag);
        // Limpia el flujo de salida
        this.dataOutputStream.flush();
    }

    public void writeError(int idSessio, int errCodi, String errMsg) throws IOException {
        // Escribe el opcode para "error"
        this.dataOutputStream.writeByte(8);
        // Escribe el ID de la sesión
        this.dataOutputStream.writeInt(idSessio);
        // Escribe el código de error
        this.dataOutputStream.writeByte(errCodi);
        // Escribe el mensaje de error
        this.write_string(errMsg);
        // Escribe bytes nulos
        this.dataOutputStream.writeByte(0);
        this.dataOutputStream.writeByte(0);
        // Limpia el flujo de salida
        this.dataOutputStream.flush();
        // Imprime el mensaje de error en consola
        System.out.println("ERROR: " + errCodi + ", " + errMsg + ".");
    }

    public int readHello() throws IOException {
        // Lee el opcode recibido
        byte opcode = dataInputStream.readByte();

        // Verifica si el opcode es correcto para "hello"
        if(opcode != 1) {
            throw new IOException("Error al leer HELLO: opcode incorrecto");
        }

        // Lee el ID de la sesión
        int id = read_int32();
        // Lee el nombre del jugador
        String nom = read_variable_string();
        // Imprime el nombre del jugador en consola
        System.out.println(nom);

        // Retorna el ID de la sesión
        return id;
    }

    public int readReady() throws IOException {
        // Lee el opcode recibido
        byte opcode = dataInputStream.readByte();

        // Verifica si el opcode es correcto para "ready"
        if(opcode != 2) {
            throw new IOException("Error al leer READY: opcode incorrecto");
        }

        // Lee el ID de la sesión
        return read_int32();
    }

    public int readPlay() throws IOException {
        // Lee el opcode recibido
        byte opcode = dataInputStream.readByte();
        // Lee el ID de la sesión
        int id = read_int32();

        // Verifica si el opcode es correcto para "play"
        if(opcode != 3) {
            throw new IOException("Error al leer PLAY: opcode incorrecto");
        }

        // Retorna el ID de la sesión
        return id;
    }

    public byte readAdmit() throws IOException {
        // Lee el opcode recibido
        byte opcode = dataInputStream.readByte();

        // Verifica si el opcode es correcto para "admit"
        if(opcode != 4) {
            throw new IOException("Error al leer ADMIT: opcode incorrecto");
        }

        // Lee el ID de la sesión
        int id = read_int32();

        // Retorna la bandera de admisión
        return dataInputStream.readByte();
    }

    public String readAction() throws IOException {
        // Lee el opcode recibido
        byte opcode = dataInputStream.readByte();

        // Verifica si el opcode es correcto para "action"
        if(opcode != 5) {
            throw new IOException("Error al leer ACTION: opcode incorrecto");
        }
        // Lee el ID de la sesión
        int id = read_int32();

        // Lee la acción realizada
        String act = read_string(3);
        // Retorna la acción
        return act;
    }

    public Result readResult() throws IOException {
        // Lee el opcode recibido
        byte opcode = dataInputStream.readByte();

        // Verifica si el opcode es correcto para "result"
        if(opcode != 6) {
            throw new IOException("Error al leer RESULT: opcode incorrecto");
        }
        // Lee el ID de la sesión
        int id = read_int32();

        // Retorna el resultado de la acción
        return new Result(read_string(3), dataInputStream.readByte());
    }

    public void readError() throws IOException {
        // Lee el opcode recibido
        byte opcode = dataInputStream.readByte();

        // Verifica si el opcode es correcto para "error"
        if(opcode != 8) {
            throw new IOException("Error al leer ERROR: opcode incorrecto");
        }

    }

    public Object readResponse() throws IOException {
        // Decide entre los diferentes opcodes recibidos
        switch (dataInputStream.readByte()) {
            case 5:
                return readAction();
            case 6:
                return readResult();
            case 8:
                readError();
            default:
                throw new IOException("Error al leer la respuesta: opcode incorrecto");
        }
    }

    public String readAction2() throws IOException {
    /*byte opcode = dataInputStream.readByte();

    if(opcode != 5) {
        throw new IOException("Error al leer ACTION: opcode incorrecto");
    }*/
        // Lee el ID de la sesión
        int id = read_int32();

        // Retorna la acción realizada
        return read_string(3);
    }

    public Result readResult2() throws IOException {
    /*//byte opcode = dataInputStream.readByte();

    if(opcode != 6) {
        throw new IOException("Error al leer RESULT: opcode incorrecto");
    }*/
        // Lee el ID de la sesión
        int id = read_int32();

        // Retorna el resultado de la acción
        return new Result(read_string(3), dataInputStream.readByte());
    }


}




