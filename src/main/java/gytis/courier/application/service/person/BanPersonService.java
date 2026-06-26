package gytis.courier.application.service.person;

import gytis.courier.application.port.in.person.BanPersonUseCase;
import gytis.courier.application.port.out.person.BanHistoryPort;
import gytis.courier.application.port.out.person.PersonCommandPort;
import gytis.courier.application.readmodel.person.BanHistoryReadModel;
import gytis.courier.domain.banhistory.BanHistory;
import gytis.courier.domain.person.Person;
import gytis.courier.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BanPersonService implements BanPersonUseCase {
    private final PersonCommandPort managementPort;
    private final BanHistoryPort banPort;

    public BanPersonService(PersonCommandPort managementPort, BanHistoryPort banPort) {
        this.managementPort = managementPort;
        this.banPort = banPort;
    }

    @Override
    public String banUnban(Long id, String reason, String adminEmail) {
        Person p = managementPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        p.banUnban();
        managementPort.save(p);

        banPort.save(BanHistory.of(p.getId(), p.isBlocked(), adminEmail, reason));

        return "Person successfully " + (p.isBlocked() ? "banned" : "unbanned");
    }

    @Override
    public List<BanHistoryReadModel> getBanHistory(Long personId) {
        return banPort.getBanHistory(personId);
    }
}
