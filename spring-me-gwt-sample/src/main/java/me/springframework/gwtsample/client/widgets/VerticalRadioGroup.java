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
package me.springframework.gwtsample.client.widgets;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class VerticalRadioGroup extends Composite implements SourcesChangeEvents {

    protected static int uniqueId = 0;
    protected String groupName = "v-radio-group-" + uniqueId++;
    protected VerticalPanel verticalPanel;
    protected int selectedItemIdx = -1;
    protected List<String> items;

    public VerticalRadioGroup() {
        verticalPanel = new VerticalPanel();
        initWidget(verticalPanel);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        if (!this.groupName.equals(groupName)) {
            clearItems();
            this.groupName = groupName;
        }
    }

    public void clearItems() {
        verticalPanel.clear();
    }

    public void addItem(final String item) {
        final RadioButton radioButton = new RadioButton(groupName);
        verticalPanel.add(radioButton);
        radioButton.setText(item);
        radioButton.addClickListener(new ClickListener() {
            public void onClick(final Widget arg0) {
                setSelectedItemIdx(verticalPanel.getWidgetIndex(arg0));
            }
        });
    }

    public int getSelectedItemIdx() {
        return selectedItemIdx;
    }

    public void setSelectedItemIdx(final int i) {
        if (i != selectedItemIdx) {
            if (i >= 0 && i < verticalPanel.getWidgetCount()) {
                ((RadioButton) verticalPanel.getWidget(i)).setChecked(true);
            } else {
                ((RadioButton) verticalPanel.getWidget(selectedItemIdx)).setChecked(false);
            }
            selectedItemIdx = i;

            fireChangeListeners();
        }
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(final List<String> items) {
        if (this.items == items) {
            return;
        }
        this.items = items;

        clearItems();

        final Iterator<String> it = this.items.iterator();
        while (it.hasNext()) {
            addItem(it.next());
        }
    }

    private ChangeListenerCollection changeListeners;

    public void addChangeListener(final ChangeListener listener) {
        if (changeListeners == null) {
            changeListeners = new ChangeListenerCollection();
        }
        changeListeners.add(listener);
    }

    public void removeChangeListener(final ChangeListener listener) {
        if (changeListeners != null) {
            changeListeners.remove(listener);
        }
    }

    void fireChangeListeners() {
        if (changeListeners != null) {
            changeListeners.fireChange(this);
        }
    }

    public int getSpacing() {
        return verticalPanel.getSpacing();
    }

    public void setSpacing(final int arg0) {
        verticalPanel.setSpacing(arg0);
    }
}
