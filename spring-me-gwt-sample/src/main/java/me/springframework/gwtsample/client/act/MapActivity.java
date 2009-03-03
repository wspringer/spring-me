package me.springframework.gwtsample.client.act;

import me.springframework.gwtsample.client.state.MainState;
import me.springframework.gwtsample.client.util.Holder;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class MapActivity {

    private Holder<MainState> mainStateHolder;

    public void start() {
        mainStateHolder.get().setRightView(MainState.VIEW_MAP);
        mainStateHolder.fireChangeEvent(this);
    }

}
