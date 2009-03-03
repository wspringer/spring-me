package me.springframework.gwtsample.client.state;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class ResultState {

    private String resultText = "";

    public void setResultText(final String resultText) {
        this.resultText = resultText;
    }

    public String getResultText() {
        return resultText;
    }

}
