package gytis.courier.application.service.address;

import gytis.courier.application.port.in.address.AddressQueryUseCase;
import gytis.courier.application.port.out.address.AddressQueryPort;
import gytis.courier.application.readmodel.address.AddressReadModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressQueryService implements AddressQueryUseCase {
    private final AddressQueryPort port;

    public AddressQueryService(AddressQueryPort port) {
        this.port = port;
    }

    public List<AddressReadModel> getAllMyAddresses(Long userId) {
        return port.findByUserId(userId);
    }
}
