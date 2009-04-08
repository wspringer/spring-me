package me.springframework.javame.rms;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import me.springframework.codec.Codec;


public class RecordStoreTemplate {

    private String name;

    private Codec defaultCodec;

    private boolean autoCreate = true;

    private Vector defaultSource;

    public Vector query() throws DataAccessException {
        Assert.notNull(defaultCodec);
        return query(defaultCodec);
    }

    public Vector query(Codec codec) throws DataAccessException {
        final Vector result = new Vector();
        query(new DecodingRecordCallbackHandler(codec, result));
        return result;
    }

    public void query(final RecordCallbackHandler handler) throws DataAccessException {
        execute(new RecordStoreCallback() {
            public void doInRecordStore(RecordStore recordStore) throws RecordStoreException {
                RecordEnumeration enumeration = recordStore.enumerateRecords(null, null, false);
                while (enumeration.hasNextElement()) {
                    handler.processRecord(enumeration.nextRecord());
                }
            }
        });
    }

    public void execute(RecordStoreCallback callback) throws DataAccessException {
        RecordStore recordStore = null;
        try {
            recordStore = openRecordStore();
            callback.doInRecordStore(recordStore);
        } catch (RecordStoreException rse) {
            throw new DataAccessException(rse.getMessage());
        } finally {
            if (recordStore != null) {
                try {
                    recordStore.closeRecordStore();
                } catch (RecordStoreNotOpenException e) {
                    // Absorb this
                } catch (RecordStoreException e) {
                    // Absorb this
                }
            }
        }
    }

    private RecordStore openRecordStore() throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
        Assert.notNull(name);
        RecordStore recordStore = null;
        if (autoCreate) {
            boolean existing = false;
            try {
                recordStore = RecordStore.openRecordStore(name, false);
                existing = true;
            } catch (RecordStoreNotFoundException rsnfe) {
                recordStore = RecordStore.openRecordStore(name, true);
            }
            if (!existing && defaultSource != null) {
                populate(recordStore);
            }
        } else {
            recordStore = RecordStore.openRecordStore(name, false);
        }
        return recordStore;
    }

    private void populate(RecordStore recordStore) throws RecordStoreNotOpenException, RecordStoreFullException, RecordStoreException {
        Assert.notNull(defaultSource);
        Assert.notNull(defaultCodec);
        System.out.println("Populating Record Store with " + defaultSource.size() + " elements.");
        Enumeration enumeration = defaultSource.elements();
        while (enumeration.hasMoreElements()) {
            byte[] buffer = defaultCodec.encode(enumeration.nextElement());
            recordStore.addRecord(buffer, 0, buffer.length);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    public boolean getAutoCreate() {
        return autoCreate;
    }

    public void setDefaultCodec(Codec defaultCodec) {
        this.defaultCodec = defaultCodec;
    }

    public Codec getDefaultCodec() {
        return defaultCodec;
    }

    public Vector getDefaultSource() {
        return defaultSource;
    }

    public void setDefaultSource(Vector defaultSource) {
        this.defaultSource = defaultSource;
    }

}
