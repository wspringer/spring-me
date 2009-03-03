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
