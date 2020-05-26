package io.edgerun.emma.controller.model;

import java.time.Instant;

/**
 * Client.
 */
public class Client extends Host {

    private Broker connectedTo;
    private Instant lastReconnect;

    public Client(String host, int port) {
        super(host, port);
    }

    public Broker getConnectedTo() {
        return connectedTo;
    }

    public void setConnectedTo(Broker connectedTo) {
        this.connectedTo = connectedTo;
    }

    public Instant getLastReconnect() {
        return lastReconnect;
    }

    public void setLastReconnect(Instant lastReconnect) {
        this.lastReconnect = lastReconnect;
    }
}
