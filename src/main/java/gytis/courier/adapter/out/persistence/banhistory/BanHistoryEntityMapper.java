package gytis.courier.adapter.out.persistence.banhistory;

import gytis.courier.domain.banhistory.BanHistory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BanHistoryEntityMapper {
    BanHistoryJpaEntity toJpaEntity(BanHistory banHistory);
    BanHistory toDomain(BanHistoryJpaEntity entity);

    default List<BanHistory> toDomainList(List<BanHistoryJpaEntity> list) {
        return list.stream().map(this::toDomain).toList();
    }
}
