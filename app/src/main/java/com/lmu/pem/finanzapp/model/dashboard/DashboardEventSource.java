package com.lmu.pem.finanzapp.model.dashboard;

import java.util.ArrayList;

public abstract class DashboardEventSource {

    private final ArrayList<DashboardEventListener> listeners = new ArrayList<>();


    public void addListener(DashboardEventListener listener) {
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    public void removeListener(DashboardEventListener listener) {
        if (listeners.contains(listener)) listeners.remove(listener);
    }

    void fireTransactionHistoryEvent(DashboardEvent event) {
        for (DashboardEventListener listener : listeners) {
            listener.handle(event);
        }
    }
}
