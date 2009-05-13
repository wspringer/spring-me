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

import me.springframework.gwtsample.client.state.MainState;
import me.springframework.gwtsample.client.util.Holder;
import me.springframework.gwtsample.client.util.ObjectChangeListener;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class MainView extends Composite {

    private Widget inputWidget;
    private Widget selectionWidget;
    private Widget descritpionWidget;
    private Widget mapWidget;
    private Widget resultWidget;

    private final HorizontalPanel horizontalPanel;

    private Holder<MainState> mainStateHolder;

    public MainView() {
        final VerticalPanel verticalPanel = new VerticalPanel();
        initWidget(verticalPanel);

        final HTML html = new HTML("<H2>Find directions</H2>");
        verticalPanel.add(html);

        horizontalPanel = new HorizontalPanel();
        verticalPanel.add(horizontalPanel);
    }

    public void init() {
        onMainStateChanged(mainStateHolder.get());
    }

    public void setMainStateHolder(final Holder<MainState> mainStateHolder) {
        this.mainStateHolder = mainStateHolder;
        mainStateHolder.addListener(new ObjectChangeListener<MainState>() {
            public void onObjectChanged(final Object source, final MainState object) {
                if (source != MainView.this) {
                    onMainStateChanged(object);
                }
            }
        });
    }

    private void onMainStateChanged(final MainState object) {
        horizontalPanel.clear();

        switch (object.getLeftView()) {
            case MainState.VIEW_INPUT:
                horizontalPanel.add(inputWidget);
                break;
            case MainState.VIEW_SELECTION:
                horizontalPanel.add(selectionWidget);
                break;
            case MainState.VIEW_DESCRIPTION:
                horizontalPanel.add(descritpionWidget);
                break;
        }

        switch (object.getRightView()) {
            case MainState.VIEW_MAP:
                horizontalPanel.add(mapWidget);
                break;
            case MainState.VIEW_RESULT:
                horizontalPanel.add(resultWidget);
                break;
        }
    }

    public void setInputWidget(final Widget inputWidget) {
        this.inputWidget = inputWidget;
    }

    public void setSelectionWidget(final Widget selectionWidget) {
        this.selectionWidget = selectionWidget;
    }

    public void setMapWidget(final Widget mapWidget) {
        this.mapWidget = mapWidget;
    }

    public void setResultWidget(final Widget resultWidget) {
        this.resultWidget = resultWidget;
    }

    public void setDescritpionWidget(final Widget descritpionWidget) {
        this.descritpionWidget = descritpionWidget;
    }

}
