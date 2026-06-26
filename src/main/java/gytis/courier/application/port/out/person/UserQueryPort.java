package gytis.courier.application.port.out.person;

import gytis.courier.domain.person.Email;

import java.util.List;

public interface UserQueryPort {
    List<Long> findAllActiveIds(int page, int size);
    boolean existsByEmail(Email email);
}
