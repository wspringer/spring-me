package me.springframework.gwtsample.client;

import me.springframework.gwtsample.client.beanfactory.PolokoBeanFactory;

import com.google.gwt.core.client.EntryPoint;
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

        final PolokoBeanFactory beanFactory = new PolokoBeanFactory();

        final Widget mainView = (Widget) beanFactory.getBean("mainView");
        rootPanel.add(mainView);
    }
}
