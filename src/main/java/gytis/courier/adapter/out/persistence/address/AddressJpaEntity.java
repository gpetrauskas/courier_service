package gytis.courier.adapter.out.persistence.address;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class AddressJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Embedded
    private AddressDetailsJpa details;

    protected AddressJpaEntity() {}

    public AddressJpaEntity(Long userId, AddressDetailsJpa details) {
        this.userId = userId;
        this.details = details;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUser(Long userId) {
        this.userId = userId;
    }

    public AddressDetailsJpa getDetails() {
        return details;
    }

    public void setDetails(AddressDetailsJpa details) {
        this.details = details;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
