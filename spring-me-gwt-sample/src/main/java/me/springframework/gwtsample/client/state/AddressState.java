package me.springframework.gwtsample.client.state;

import java.util.ArrayList;

/**
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 */
public class AddressState {

    private String sourceQuery = "abc";
    private String destinationQuery = "def";

    private final ArrayList<String> sourceCandidates = new ArrayList<String>();
    private final ArrayList<String> destinationCandidates = new ArrayList<String>();

    private int selectedSource = -1;
    private int selectedDestination = -1;

    public String getSourceQuery() {
        return sourceQuery;
    }

    public void setSourceQuery(final String sourceQuery) {
        this.sourceQuery = sourceQuery;
    }

    public String getDestinationQuery() {
        return destinationQuery;
    }

    public void setDestinationQuery(final String destinationQuery) {
        this.destinationQuery = destinationQuery;
    }

    public ArrayList<String> getSourceCandidates() {
        return sourceCandidates;
    }

    public ArrayList<String> getDestinationCandidates() {
        return destinationCandidates;
    }

    public void setSelectedSource(final int selectedSource) {
        this.selectedSource = selectedSource;
    }

    public int getSelectedSource() {
        return selectedSource;
    }

    public void setSelectedDestination(final int selectedDestination) {
        this.selectedDestination = selectedDestination;
    }

    public int getSelectedDestination() {
        return selectedDestination;
    }

}
