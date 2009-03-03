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
