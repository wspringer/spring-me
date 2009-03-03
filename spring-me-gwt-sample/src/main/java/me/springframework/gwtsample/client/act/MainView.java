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
