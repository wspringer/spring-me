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
