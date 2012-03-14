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
package me.springframework.di.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import me.springframework.di.MapSource;
import me.springframework.di.PropertiesSource;
import me.springframework.di.Sink;

/**
 * A mutable implementation of {@link PropertiesSource}.
 */
public class MutablePropertiesSource extends AbstractTyped implements PropertiesSource, MutableSource {

    /**
     * The {@link Sink} to which the source is connected.
     */
    private Sink sink;

    /**
     * The id of the {@link MapSource}.
     */
    private String id;

    /**
     * The entries in the map.
     */
    private List<MapSource.Entry> entries;

    /**
     * Constructs a new instance.
     */
    public MutablePropertiesSource(Sink sink, Properties properties) {
        this.sink = sink;
        this.entries = new ArrayList<MapSource.Entry>();
    }

    public List<MapSource.Entry> getEntries() {
        return entries;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
       return "a Properties injected in " + sink.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see me.springframework.di.Source#getId()
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id to a new value.
     *
     * @param id The new id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     *
     * @see me.springframework.di.Source#getSourceType()
     */
    public SourceType getSourceType() {
        return SourceType.Properties;
    }

}
