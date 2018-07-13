package com.lmu.pem.finanzapp.model.dashboard;

public class DashboardEvent {

    public enum EventType {
        UPDATED
    }

    private DashboardEventSource source;
    private EventType type;

    public DashboardEvent(DashboardEventSource source, EventType type) {
        this.source = source;
        this.type = type;
    }

    public DashboardEventSource getSource() {
        return source;
    }

    public EventType getType() {
        return type;
    }
}
