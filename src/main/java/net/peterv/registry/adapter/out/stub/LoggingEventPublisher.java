package net.peterv.registry.adapter.out.stub;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import net.peterv.common.events.DomainEvent;
import net.peterv.common.ports.EventPublisher;
import org.jboss.logging.Logger;

@DefaultBean
@ApplicationScoped
public class LoggingEventPublisher implements EventPublisher {

    private static final Logger LOG = Logger.getLogger(LoggingEventPublisher.class);

    @Override
    public void publish(DomainEvent event) {
        LOG.infof("Domain event: %s [id=%s, functionId=%s]",
                event.getClass().getSimpleName(),
                event.eventId(),
                event.functionId().value());
    }
}
