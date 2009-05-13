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
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.springframework.di.Configuration;
import me.springframework.di.Instance;
import me.springframework.di.ListSource;
import me.springframework.di.MapSource;
import me.springframework.di.Sink;
import me.springframework.di.Source;
import me.springframework.di.MapSource.Entry;

public class MutableConfiguration implements Configuration {

    private Set<Instance> instanceSources = new HashSet<Instance>();
    private Set<ListSource> listSources = new HashSet<ListSource>();
    private Set<Instance> publicInstances = new HashSet<Instance>();
    private Map<String, ? extends Instance> instancesByName;
    private Set<MapSource> mapSources = new HashSet<MapSource>();

    public MutableConfiguration(Map<String, ? extends Instance> publicInstances) {
        for (Instance instance : publicInstances.values()) {
            processInstance(instance);
        }
        this.publicInstances.addAll(publicInstances.values());
        this.instancesByName = publicInstances;
    }

    public Set<Instance> getInstanceSources() {
        return instanceSources;
    }

    public Set<ListSource> getListSources() {
        return listSources;
    }
    
    public Set<MapSource> getMapSources() {
        return mapSources;
    }

    public Set<Instance> getPublicInstances() {
        return publicInstances;
    }

    public Instance get(String name) {
        return instancesByName.get(name);
    }

    private void processInstance(
            Instance instance) {
        Collection<Sink> sinks = getSinks(instance);
        for (Sink sink : sinks) {
            processSource(sink.getSource());
        }
    }

    private void processSource(Source source) {
        switch (source.getSourceType()) {
            case List:
                ListSource listSource = (ListSource) source;
                listSources.add(listSource);
                for (Source element : listSource.getElementSources()) {
                    processSource(element);
                }
                break;
            case Instance:
                Instance instance = (Instance) source;
                instanceSources.add(instance);
                processInstance(instance);
                break;
            case Map:
                MapSource mapSource = (MapSource) source;
                mapSources.add(mapSource);
                for (Entry entry : mapSource.getEntries()) {
                    processSource(entry.getKey());
                    processSource(entry.getValue());
                }
        }
    }

    private static Collection<Sink> getSinks(Instance instance) {
        Collection<Sink> sinks = new ArrayList<Sink>();
        if (instance.getConstructorArguments() != null) {
            sinks.addAll(instance.getConstructorArguments());
        }
        if (instance.getSetters() != null) {
            sinks.addAll(instance.getSetters());
        }
        return sinks;
    }

}
