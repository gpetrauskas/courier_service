package gytis.courier.application.port.in.person;

import gytis.courier.application.command.UserSelfEditCommand;

public interface EditMyInfoUseCase {
    void editMyInfo(UserSelfEditCommand command);
}
