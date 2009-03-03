package me.springframework.gwtsample.client.widgets;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class VerticalRadioGroup extends Composite implements SourcesChangeEvents {

    protected static int uniqueId = 0;
    protected String groupName = "v-radio-group-" + uniqueId++;
    protected VerticalPanel verticalPanel;
    protected int selectedItemIdx = -1;
    protected List<String> items;

    public VerticalRadioGroup() {
        verticalPanel = new VerticalPanel();
        initWidget(verticalPanel);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        if (!this.groupName.equals(groupName)) {
            clearItems();
            this.groupName = groupName;
        }
    }

    public void clearItems() {
        verticalPanel.clear();
    }

    public void addItem(final String item) {
        final RadioButton radioButton = new RadioButton(groupName);
        verticalPanel.add(radioButton);
        radioButton.setText(item);
        radioButton.addClickListener(new ClickListener() {
            public void onClick(final Widget arg0) {
                setSelectedItemIdx(verticalPanel.getWidgetIndex(arg0));
            }
        });
    }

    public int getSelectedItemIdx() {
        return selectedItemIdx;
    }

    public void setSelectedItemIdx(final int i) {
        if (i != selectedItemIdx) {
            if (i >= 0 && i < verticalPanel.getWidgetCount()) {
                ((RadioButton) verticalPanel.getWidget(i)).setChecked(true);
            } else {
                ((RadioButton) verticalPanel.getWidget(selectedItemIdx)).setChecked(false);
            }
            selectedItemIdx = i;

            fireChangeListeners();
        }
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(final List<String> items) {
        if (this.items == items) {
            return;
        }
        this.items = items;

        clearItems();

        final Iterator<String> it = this.items.iterator();
        while (it.hasNext()) {
            addItem(it.next());
        }
    }

    private ChangeListenerCollection changeListeners;

    public void addChangeListener(final ChangeListener listener) {
        if (changeListeners == null) {
            changeListeners = new ChangeListenerCollection();
        }
        changeListeners.add(listener);
    }

    public void removeChangeListener(final ChangeListener listener) {
        if (changeListeners != null) {
            changeListeners.remove(listener);
        }
    }

    void fireChangeListeners() {
        if (changeListeners != null) {
            changeListeners.fireChange(this);
        }
    }

    public int getSpacing() {
        return verticalPanel.getSpacing();
    }

    public void setSpacing(final int arg0) {
        verticalPanel.setSpacing(arg0);
    }
}
