package gytis.courier.application.readmodel.person;

public sealed interface MyInfoReadModel permits MyUserInfoReadModel, MyCourierInfoReadModel, MyAdminInfoReadModel {
    String name();
    String email();
}
