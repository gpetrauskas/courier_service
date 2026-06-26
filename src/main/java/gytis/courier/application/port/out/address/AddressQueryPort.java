package gytis.courier.application.port.out.address;

import gytis.courier.application.readmodel.address.AddressReadModel;

import java.util.List;

public interface AddressQueryPort {
    List<AddressReadModel> findByUserId(Long userId);
}
