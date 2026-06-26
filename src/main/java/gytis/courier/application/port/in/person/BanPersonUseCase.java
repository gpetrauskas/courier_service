package gytis.courier.application.port.in.person;

import gytis.courier.application.readmodel.person.BanHistoryReadModel;

import java.util.List;

public interface BanPersonUseCase {
    String banUnban(Long id, String reason, String adminEmail);
    List<BanHistoryReadModel> getBanHistory(Long personId);
}
