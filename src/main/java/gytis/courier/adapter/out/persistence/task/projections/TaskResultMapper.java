package gytis.courier.adapter.out.persistence.task.projections;

import gytis.courier.application.result.AdminTaskCardResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskResultMapper {
    AdminTaskCardResult toCardResult(AdminTaskCardProjection projection);
}
