package gytis.courier.application.port.in.person;

public interface CourierCommandUseCase {
    void activate(Long courierId);
    void deactivate(Long courierId);
}
