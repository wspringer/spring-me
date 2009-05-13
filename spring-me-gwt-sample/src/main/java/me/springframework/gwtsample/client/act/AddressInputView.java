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

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class AddressInputView extends Composite {

    private AddressInputActivity addressInputActivity;
    private Holder<AddressState> addressStateHolder;

    private final TextBox sourceTextBox;
    private final TextBox destinationTextBox;

    public AddressInputView() {

        final FlexTable flexTable = new FlexTable();
        initWidget(flexTable);
        flexTable.setWidth("200px");

        final Label sourceLabel = new Label("Source");
        flexTable.setWidget(1, 0, sourceLabel);

        final Label destinationLabel = new Label("Destination");
        flexTable.setWidget(2, 0, destinationLabel);

        sourceTextBox = new TextBox();
        flexTable.setWidget(1, 1, sourceTextBox);
        flexTable.getCellFormatter().setWidth(1, 1, "100%");
        sourceTextBox.setWidth("100%");

        destinationTextBox = new TextBox();
        flexTable.setWidget(2, 1, destinationTextBox);
        flexTable.getCellFormatter().setWidth(2, 1, "100%");
        destinationTextBox.setWidth("100%");

        final Button searchButton = new Button();
        flexTable.setWidget(3, 0, searchButton);
        searchButton.addClickListener(new ClickListener() {
            public void onClick(final Widget arg0) {
                updateState();
                addressInputActivity.okButtonClicked();
            }
        });
        flexTable.getFlexCellFormatter().setColSpan(3, 0, 2);
        searchButton.setText("Search");

        final Label enterAddressLabel = new Label("Enter address:");
        flexTable.setWidget(0, 0, enterAddressLabel);
        flexTable.getFlexCellFormatter().setColSpan(0, 0, 2);
    }

    public void init() {
        addressStateChanged(addressStateHolder.get());
    }

    public void setAddressInputActivity(final AddressInputActivity addressInputActivity) {
        this.addressInputActivity = addressInputActivity;
    }

    public void setAddressStateHolder(final Holder<AddressState> addressStateHolder) {
        this.addressStateHolder = addressStateHolder;
        addressStateHolder.addListener(new ObjectChangeListener<AddressState>() {
            public void onObjectChanged(final Object source, final AddressState object) {
                if (source != AddressInputView.this) {
                    addressStateChanged(object);
                }
            }
        });
    }

    private void addressStateChanged(final AddressState object) {
        sourceTextBox.setText(object.getSourceQuery());
        destinationTextBox.setText(object.getDestinationQuery());
    }

    private void updateState() {
        addressStateHolder.get().setSourceQuery(sourceTextBox.getText());
        addressStateHolder.get().setDestinationQuery(destinationTextBox.getText());
    }
}
