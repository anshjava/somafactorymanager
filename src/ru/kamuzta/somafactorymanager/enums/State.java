package ru.kamuzta.somafactorymanager.enums;

public enum State {
    ON("ON", true),
    OFF("OFF", false);

    private String stateName;
    private boolean stateBool;

    State(String stateName, boolean stateBool) {
        this.stateName = stateName;
        this.stateBool = stateBool;
    }

    public String getStateName() {
        return stateName;
    }
    public boolean isStateBool() {
        return stateBool;
    }

    @Override
    public String toString() {
        return getStateName();
    }
}
