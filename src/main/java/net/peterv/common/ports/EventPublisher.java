package net.peterv.common.ports;

import net.peterv.common.events.DomainEvent;

public interface EventPublisher {

    void publish(DomainEvent event);
}
