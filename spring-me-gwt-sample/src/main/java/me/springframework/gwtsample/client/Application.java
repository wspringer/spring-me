package me.springframework.gwtsample.client;

import me.springframework.gwtsample.client.beanfactory.PolokoBeanFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class Application implements EntryPoint {

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        final RootPanel rootPanel = RootPanel.get();

        final PolokoBeanFactory beanFactory = PolokoBeanFactory.getInstance();

        final String message = (String) beanFactory.getBean("testBean");

        final Button clickMeButton = new Button();
        rootPanel.add(clickMeButton);
        clickMeButton.addClickListener(new ClickListener() {
            public void onClick(final Widget arg0) {
                Window.alert(message);
            }
        });
        clickMeButton.setText("Click me");
    }
}
