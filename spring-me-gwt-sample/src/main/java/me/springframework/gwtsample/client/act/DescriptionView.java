package me.springframework.gwtsample.client.act;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class DescriptionView extends Composite {

    private ResultActivity resultActivity;

    public DescriptionView() {

        final VerticalPanel verticalPanel = new VerticalPanel();
        initWidget(verticalPanel);
        verticalPanel.setWidth("200px");

        final Button restartButton = new Button();
        verticalPanel.add(restartButton);
        restartButton.addClickListener(new ClickListener() {
            public void onClick(final Widget arg0) {
                resultActivity.onRestartButtonClicked();
            }
        });
        restartButton.setText("Restart");
    }

    public void init() {
    }

    public void setResultActivity(final ResultActivity resultActivity) {
        this.resultActivity = resultActivity;
    }

}
