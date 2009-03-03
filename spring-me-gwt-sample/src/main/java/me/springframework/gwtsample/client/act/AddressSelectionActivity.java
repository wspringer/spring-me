package me.springframework.gwtsample.client.act;

import me.springframework.gwtsample.client.state.AddressState;
import me.springframework.gwtsample.client.state.MainState;
import me.springframework.gwtsample.client.state.ResultState;
import me.springframework.gwtsample.client.util.Holder;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class AddressSelectionActivity {

    private Holder<MainState> mainStateHolder;
    private Holder<AddressState> addressStateHolder;
    private Holder<ResultState> resultStateHolder;

    private AddressInputActivity addressInputActivity;
    private ResultActivity resultActivity;

    public void start() {
        mainStateHolder.get().setLeftView(MainState.VIEW_SELECTION);
        mainStateHolder.get().setRightView(MainState.VIEW_MAP);
        mainStateHolder.fireChangeEvent(this);
    }

    public void onBackButtonClicked() {
        addressInputActivity.start();
    }

    public void onOkButtonClicked() {
        resultStateHolder.get().setResultText(
                "Some example directions, go left then straight ahead");
        resultStateHolder.fireChangeEvent(this);

        resultActivity.start();
    }

    public void setAddressStateHolder(final Holder<AddressState> addressStateHolder) {
        this.addressStateHolder = addressStateHolder;
    }

    public void setMainStateHolder(final Holder<MainState> mainState) {
        this.mainStateHolder = mainState;
    }

    public void setResultStateHolder(final Holder<ResultState> resultStateHolder) {
        this.resultStateHolder = resultStateHolder;
    }

    public void setAddressInputActivity(final AddressInputActivity addressInputActivity) {
        this.addressInputActivity = addressInputActivity;
    }

    public void setResultActivity(final ResultActivity resultActivity) {
        this.resultActivity = resultActivity;
    }

}
