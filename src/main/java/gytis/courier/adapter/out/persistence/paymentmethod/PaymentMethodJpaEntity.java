package gytis.courier.adapter.out.persistence.paymentmethod;

import gytis.courier.adapter.out.persistence.person.user.UserJpaEntity;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "payment_method")
@DiscriminatorColumn(name = "payment_type")
public abstract class PaymentMethodJpaEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column
    private String token;

    @Column(nullable = false)
    private boolean saved = false;

    @Column(name = "payment_type", insertable = false, updatable = false)
    private String paymentType;

    protected PaymentMethodJpaEntity() {}

    public Long getId() { return id; }
    public UserJpaEntity getUser() { return user; }
    public boolean isSaved() { return saved; }
    public String getToken() { return token; }
    public String getPaymentType() { return paymentType; }
    public Long getUserId() { return userId; }

    public void setUser(UserJpaEntity user) { this.user = user; }
    public void setSaved(boolean saved) { this.saved = saved; }
    public void setToken(String token) { this.token = token; }
}
