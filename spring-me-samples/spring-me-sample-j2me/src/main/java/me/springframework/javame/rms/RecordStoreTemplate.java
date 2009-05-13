/**
 * Copyright (C) 2009 Original Authors
 *
 * This file is part of Spring ME.
 *
 * Spring ME is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2, or (at your option) any
 * later version.
 *
 * Spring ME is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Spring ME; see the file COPYING. If not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library. Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module. An independent module is a module which is not derived from or
 * based on this library. If you modify this library, you may extend this
 * exception to your version of the library, but you are not obligated to
 * do so. If you do not wish to do so, delete this exception statement
 * from your version.
 */
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
