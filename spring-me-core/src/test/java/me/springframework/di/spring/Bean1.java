package me.springframework.di.spring;

public class Bean1 {

    private int dummy;
    private String dummy2;
    private Bean2 bean2;
    private Bean3 bean3;

    public Bean1() {
    }

    public Bean1(final int dummy, final String dummy2, final Bean2 bean2, final Bean3 bean3) {
        this.dummy = dummy;
        this.dummy2 = dummy2;
        this.bean2 = bean2;
        this.bean3 = bean3;
    }

    public void setDummy(final int dummy) {
        this.dummy = dummy;
    }

    public int getDummy() {
        return dummy;
    }

    public void setDummy2(final String dummy2) {
        this.dummy2 = dummy2;
    }

    public String getDummy2() {
        return dummy2;
    }

    public Bean2 getBean2() {
        return bean2;
    }

    public void setBean2(final Bean2 bean2) {
        this.bean2 = bean2;
    }

    public Bean3 getBean3() {
        return bean3;
    }

    public void setBean3(final Bean3 bean3) {
        this.bean3 = bean3;
    }

}
