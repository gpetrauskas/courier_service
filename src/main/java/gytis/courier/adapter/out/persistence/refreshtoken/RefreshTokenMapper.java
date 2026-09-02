package gytis.courier.adapter.out.persistence.refreshtoken;

import gytis.courier.domain.refresh.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    @Mapping(source = "id", target = "id", ignore = true)
    RefreshTokenEntity toEntity(RefreshToken domain);

    RefreshToken toDomain(RefreshTokenEntity entity);
}
