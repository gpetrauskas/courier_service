package gytis.courier.application.port.in.address;

public interface DeleteAddressUseCase {
    void deleteAddressById(Long addressId, Long userId);
}
