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
package me.springframework.gwtsample.client.state;

import java.util.ArrayList;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class AddressState {

    private String sourceQuery = "abc";
    private String destinationQuery = "def";

    private final ArrayList<String> sourceCandidates = new ArrayList<String>();
    private final ArrayList<String> destinationCandidates = new ArrayList<String>();

    private int selectedSource = -1;
    private int selectedDestination = -1;

    public String getSourceQuery() {
        return sourceQuery;
    }

    public void setSourceQuery(final String sourceQuery) {
        this.sourceQuery = sourceQuery;
    }

    public String getDestinationQuery() {
        return destinationQuery;
    }

    public void setDestinationQuery(final String destinationQuery) {
        this.destinationQuery = destinationQuery;
    }

    public ArrayList<String> getSourceCandidates() {
        return sourceCandidates;
    }

    public ArrayList<String> getDestinationCandidates() {
        return destinationCandidates;
    }

    public void setSelectedSource(final int selectedSource) {
        this.selectedSource = selectedSource;
    }

    public int getSelectedSource() {
        return selectedSource;
    }

    public void setSelectedDestination(final int selectedDestination) {
        this.selectedDestination = selectedDestination;
    }

    public int getSelectedDestination() {
        return selectedDestination;
    }

}
