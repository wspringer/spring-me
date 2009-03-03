package me.springframework.gwtsample.client.act;

import me.springframework.gwtsample.client.state.MainState;
import me.springframework.gwtsample.client.state.ResultState;
import me.springframework.gwtsample.client.util.Holder;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class ResultActivity {

    private AddressInputActivity addressInputActivity;
    private Holder<ResultState> resultStateHolder;
    private Holder<MainState> mainStateHolder;

    public void start() {
        mainStateHolder.get().setLeftView(MainState.VIEW_DESCRIPTION);
        mainStateHolder.get().setRightView(MainState.VIEW_RESULT);
        mainStateHolder.fireChangeEvent(this);
    }

    public void onRestartButtonClicked() {
        addressInputActivity.start();
    }

    public void setAddressInputActivity(final AddressInputActivity addressInputActivity) {
        this.addressInputActivity = addressInputActivity;
    }

    public void setResultStateHolder(final Holder<ResultState> resultStateHolder) {
        this.resultStateHolder = resultStateHolder;
    }

    public void setMainStateHolder(final Holder<MainState> mainStateHolder) {
        this.mainStateHolder = mainStateHolder;
    }
}
