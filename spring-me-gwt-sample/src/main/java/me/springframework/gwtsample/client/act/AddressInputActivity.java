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
