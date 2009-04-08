package me.springframework.javame.rms;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public interface RecordStoreCallback {

    void doInRecordStore(RecordStore recordStore) throws RecordStoreException;
    
}
