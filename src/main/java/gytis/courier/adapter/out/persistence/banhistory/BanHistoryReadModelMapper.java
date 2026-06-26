package gytis.courier.adapter.out.persistence.banhistory;

import gytis.courier.adapter.out.persistence.banhistory.projection.BanHistoryProjection;
import gytis.courier.application.readmodel.person.BanHistoryReadModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BanHistoryReadModelMapper {
    BanHistoryReadModel toReadModel(BanHistoryProjection projection);
}
