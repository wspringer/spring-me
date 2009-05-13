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
import me.springframework.gwtsample.client.state.MainState;
import me.springframework.gwtsample.client.util.Holder;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class AddressInputActivity {

    private AddressSelectionActivity addressSelectionActivity;
    private Holder<AddressState> addressStateHolder;
    private Holder<MainState> mainStateHolder;

    public void start() {
        mainStateHolder.get().setLeftView(MainState.VIEW_INPUT);
        mainStateHolder.get().setRightView(MainState.VIEW_MAP);
        mainStateHolder.fireChangeEvent(this);

        final AddressState s = addressStateHolder.get();
        s.setSelectedSource(-1);
        s.setSelectedDestination(-1);
        addressStateHolder.fireChangeEvent(this);
    }

    public void okButtonClicked() {
        final AddressState s = addressStateHolder.get();

        s.getSourceCandidates().clear();
        s.getSourceCandidates().add("aaa");
        s.getSourceCandidates().add("bbb");
        s.getSourceCandidates().add("ccc");
        s.setSelectedSource(-1);

        s.getDestinationCandidates().clear();
        s.getDestinationCandidates().add("qqq");
        s.getDestinationCandidates().add("www");
        s.getDestinationCandidates().add("eee");
        s.setSelectedDestination(-1);

        addressStateHolder.fireChangeEvent(this);

        addressSelectionActivity.start();
    }

    public void setAddressStateHolder(final Holder<AddressState> addressStateHolder) {
        this.addressStateHolder = addressStateHolder;
    }

    public void setMainStateHolder(final Holder<MainState> mainState) {
        this.mainStateHolder = mainState;
    }

    public void setAddressSelectionActivity(final AddressSelectionActivity addressSelectionActivity) {
        this.addressSelectionActivity = addressSelectionActivity;
    }

}
