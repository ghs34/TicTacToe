package utils;


public class Result {
    private byte byteValue;
    private String stringValue;

    public Result(String stringValue, byte byteValue) {
        this.byteValue = byteValue;
        this.stringValue = stringValue;
    }

    public byte getByteValue() {
        return byteValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}

