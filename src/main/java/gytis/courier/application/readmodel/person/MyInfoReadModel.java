package gytis.courier.application.readmodel.person;

import java.time.LocalDateTime;

public sealed interface MyInfoReadModel permits MyUserInfoReadModel, MyCourierInfoReadModel, MyAdminInfoReadModel {
    String name();
    String email();
    LocalDateTime createdDate();
}
