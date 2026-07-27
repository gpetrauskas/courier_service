package gytis.courier.adapter.out.persistence.activitylog;

import gytis.courier.application.readmodel.activitylog.ActivityLogReadModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityLogMapper {
    ActivityLogReadModel toReadModel(ActivityLogJpaEntity entity);
}
