package gytis.courier.application.port.out.person;

import gytis.courier.application.readmodel.person.BanHistoryReadModel;
import gytis.courier.domain.banhistory.BanHistory;

import java.util.List;

public interface BanHistoryPort {
    void save(BanHistory banHistory);
    List<BanHistoryReadModel> getBanHistory(Long personId);
}
