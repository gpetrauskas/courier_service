package gytis.courier.adapter.out.persistence.person.user;

import gytis.courier.adapter.out.persistence.paymentmethod.PaymentMethodJpaEntity;
import gytis.courier.adapter.out.persistence.person.common.PersonJpaEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class UserJpaEntity extends PersonJpaEntity {
    @Column(name = "default_address_id")
    private Long defaultAddressId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PaymentMethodJpaEntity> paymentMethods = new ArrayList<>();

    @Column
    @ColumnDefault("false")
    private Boolean subscribed;

    protected UserJpaEntity() {}

    public UserJpaEntity(String name, String email, String password) {
        super(name, email, password);
    }

    @Override
    public String getRole() { return "USER"; }
    public Long getDefaultAddressId() { return defaultAddressId; }
    public List<PaymentMethodJpaEntity> getPaymentMethods() { return paymentMethods; }
    public Boolean getSubscribed() { return subscribed; }

    public void setDefaultAddressId(Long defaultAddressId) { this.defaultAddressId =defaultAddressId; }
    public void setPaymentMethods(List<PaymentMethodJpaEntity> paymentMethods) { this.paymentMethods = paymentMethods; }
    public void setSubscribed(Boolean subscribed) { this.subscribed = subscribed; }
}

