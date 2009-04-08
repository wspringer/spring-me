package me.springframework.javame.rms;

public interface RecordCallbackHandler {

    void processRecord(byte[] record);
    
}
