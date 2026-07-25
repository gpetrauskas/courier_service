package gytis.courier.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gytis.courier.domain.event.DomainEvent;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonMixinConfig {
    public JacksonMixinConfig(ObjectMapper mapper) {
        mapper.addMixIn(DomainEvent.class, DomainEventJacksonMapping.class);
    }
}
