package me.springframework.gwtsample.client.act;

import me.springframework.gwtsample.client.state.ResultState;
import me.springframework.gwtsample.client.util.Holder;
import me.springframework.gwtsample.client.util.ObjectChangeListener;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class ResultView extends Composite {

    private final Label resultLabel;

    private Holder<ResultState> resultStateHolder;

    public ResultView() {

        final VerticalPanel verticalPanel = new VerticalPanel();
        initWidget(verticalPanel);
        verticalPanel.setWidth("500px");

        final Label resultTextLabel = new Label("Result");
        verticalPanel.add(resultTextLabel);

        resultLabel = new Label("result value");
        verticalPanel.add(resultLabel);
    }

    public void init() {
        onResultChanged(resultStateHolder.get());
    }

    public void setResultStateHolder(final Holder<ResultState> resultStateHolder) {
        this.resultStateHolder = resultStateHolder;
        resultStateHolder.addListener(new ObjectChangeListener<ResultState>() {
            public void onObjectChanged(final Object source, final ResultState object) {
                if (source != ResultView.this) {
                    onResultChanged(object);
                }
            }
        });
    }

    private void onResultChanged(final ResultState object) {
        resultLabel.setText(object.getResultText());
    }
}
