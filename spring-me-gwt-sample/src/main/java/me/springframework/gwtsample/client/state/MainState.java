package me.springframework.gwtsample.client.state;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class MainState {

    public static final int VIEW_INPUT = 0;
    public static final int VIEW_SELECTION = 1;
    public static final int VIEW_MAP = 2;
    public static final int VIEW_DESCRIPTION = 3;
    public static final int VIEW_RESULT = 4;

    private int leftView = VIEW_INPUT;
    private int rightView = VIEW_MAP;

    public void setLeftView(final int leftView) {
        this.leftView = leftView;
    }

    public int getLeftView() {
        return leftView;
    }

    public void setRightView(final int rightView) {
        this.rightView = rightView;
    }

    public int getRightView() {
        return rightView;
    }

}
