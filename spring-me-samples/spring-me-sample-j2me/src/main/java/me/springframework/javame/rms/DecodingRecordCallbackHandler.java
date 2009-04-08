package me.springframework.javame.rms;

import java.util.Vector;

import me.springframework.codec.Codec;
import me.springframework.codec.CodecException;


public class DecodingRecordCallbackHandler implements RecordCallbackHandler {

    private Vector vector;

    private Codec codec;

    public DecodingRecordCallbackHandler(Codec codec, Vector vector) {
        this.codec = codec;
        this.vector = vector;
    }

    public void processRecord(byte[] record) {
        try {
            vector.addElement(codec.decode(record));
        } catch (CodecException ce) {
            throw new DataAccessException(ce.getMessage());
        }
    }

}
