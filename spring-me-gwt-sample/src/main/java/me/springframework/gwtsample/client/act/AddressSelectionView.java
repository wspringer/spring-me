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
import me.springframework.gwtsample.client.widgets.VerticalRadioGroup;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class AddressSelectionView extends Composite {

    private final VerticalRadioGroup destinationVerticalRadioGroup;
    private final VerticalRadioGroup sourceVerticalRadioGroup;

    private Holder<AddressState> addressStateHolder;
    private AddressSelectionActivity addressSelectionActivity;
    private final Button okButton;
    private final Label selectSourceCandidateLabel;
    private final Label selectDestinationCandidateLabel;

    public AddressSelectionView() {

        final VerticalPanel verticalPanel = new VerticalPanel();
        initWidget(verticalPanel);
        verticalPanel.setWidth("300px");

        selectSourceCandidateLabel = new Label("Select source candidate");
        verticalPanel.add(selectSourceCandidateLabel);

        sourceVerticalRadioGroup = new VerticalRadioGroup();
        verticalPanel.add(sourceVerticalRadioGroup);
        sourceVerticalRadioGroup.addChangeListener(new ChangeListener() {
            public void onChange(final Widget arg0) {
                onSelectionChanged();
            }
        });

        selectDestinationCandidateLabel = new Label("Select destination candidate");
        verticalPanel.add(selectDestinationCandidateLabel);

        destinationVerticalRadioGroup = new VerticalRadioGroup();
        verticalPanel.add(destinationVerticalRadioGroup);
        destinationVerticalRadioGroup.addChangeListener(new ChangeListener() {
            public void onChange(final Widget arg0) {
                onSelectionChanged();
            }
        });

        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        verticalPanel.add(horizontalPanel);

        final Button backButton = new Button();
        horizontalPanel.add(backButton);
        backButton.addClickListener(new ClickListener() {
            public void onClick(final Widget arg0) {
                addressSelectionActivity.onBackButtonClicked();
            }
        });
        backButton.setText("Back");

        okButton = new Button();
        horizontalPanel.add(okButton);
        okButton.addClickListener(new ClickListener() {
            public void onClick(final Widget arg0) {
                addressSelectionActivity.onOkButtonClicked();
            }
        });
        okButton.setText("Ok");
    }

    public void init() {
        addressStateChanged(addressStateHolder.get());
    }

    public void setAddressSelectionActivity(final AddressSelectionActivity addressSelectionActivity) {
        this.addressSelectionActivity = addressSelectionActivity;
    }

    public void setAddressStateHolder(final Holder<AddressState> addressStateHolder) {
        this.addressStateHolder = addressStateHolder;
        addressStateHolder.addListener(new ObjectChangeListener<AddressState>() {
            public void onObjectChanged(final Object source, final AddressState object) {
                if (source != AddressSelectionView.this) {
                    addressStateChanged(object);
                }
            }
        });
    }

    private void onSelectionChanged() {
        addressStateHolder.get().setSelectedSource(sourceVerticalRadioGroup.getSelectedItemIdx());
        addressStateHolder.get().setSelectedDestination(
                destinationVerticalRadioGroup.getSelectedItemIdx());
        addressStateHolder.fireChangeEvent(this);

        enableDisableOkButton();
    }

    private void enableDisableOkButton() {
        okButton.setEnabled(addressStateHolder.get().getSelectedSource() != -1
                && addressStateHolder.get().getSelectedDestination() != -1);

    }

    private void addressStateChanged(final AddressState object) {
        // update label text
        final String src = addressStateHolder.get().getSourceQuery();
        final String dst = addressStateHolder.get().getDestinationQuery();
        selectSourceCandidateLabel.setText("Please clarify source address " + src);
        selectDestinationCandidateLabel.setText("Please clarify destination address " + dst);

        // update choicegroups
        sourceVerticalRadioGroup.clearItems();
        for (final String item : object.getSourceCandidates()) {
            sourceVerticalRadioGroup.addItem(item);
        }
        sourceVerticalRadioGroup.setSelectedItemIdx(object.getSelectedSource());

        destinationVerticalRadioGroup.clearItems();
        for (final String item : object.getDestinationCandidates()) {
            destinationVerticalRadioGroup.addItem(item);
        }
        destinationVerticalRadioGroup.setSelectedItemIdx(object.getSelectedDestination());

        enableDisableOkButton();
    }

}
