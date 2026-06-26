package gytis.courier.application.port.in.address;

import gytis.courier.application.readmodel.address.AddressReadModel;

import java.util.List;

public interface AddressQueryUseCase {
    List<AddressReadModel> getAllMyAddresses(Long userId);
}
