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
package me.springframework.gwtsample.client.act;

import me.springframework.gwtsample.client.state.AddressState;
import me.springframework.gwtsample.client.util.Holder;
import me.springframework.gwtsample.client.util.ObjectChangeListener;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class MapView extends Composite {

    private final Label selecteddestinationLabel;
    private final Label selectedsourceLabel;

    private Holder<AddressState> addressStateHolder;

    public MapView() {

        final FlexTable flexTable = new FlexTable();
        initWidget(flexTable);

        final Image image = new Image();
        flexTable.setWidget(1, 0, image);
        flexTable.getFlexCellFormatter().setColSpan(1, 0, 2);
        image.setUrl("map2.JPG");

        selectedsourceLabel = new Label("selectedSource");
        selectedsourceLabel.setWidth("225px");
        flexTable.setWidget(0, 0, selectedsourceLabel);

        selecteddestinationLabel = new Label("selectedDestination");
        selecteddestinationLabel.setWidth("225px");
        flexTable.setWidget(0, 1, selecteddestinationLabel);
    }

    public void init() {
        addressStateChanged(addressStateHolder.get());
    }

    public void setAddressStateHolder(final Holder<AddressState> addressStateHolder) {
        this.addressStateHolder = addressStateHolder;
        addressStateHolder.addListener(new ObjectChangeListener<AddressState>() {
            public void onObjectChanged(final Object source, final AddressState object) {
                if (source != MapView.this) {
                    addressStateChanged(object);
                }
            }
        });
    }

    private void addressStateChanged(final AddressState object) {
        if (object.getSelectedSource() == -1) {
            selectedsourceLabel.setText("no source is selected");
        } else {
            selectedsourceLabel.setText("selected "
                    + object.getSourceCandidates().get(object.getSelectedSource()));
        }
        if (object.getSelectedDestination() == -1) {
            selecteddestinationLabel.setText("no destination is selected");
        } else {
            selecteddestinationLabel.setText("selected "
                    + object.getDestinationCandidates().get(object.getSelectedDestination()));
        }
    }

}
