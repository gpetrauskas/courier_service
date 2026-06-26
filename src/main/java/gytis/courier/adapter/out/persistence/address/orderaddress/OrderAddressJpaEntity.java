package gytis.courier.adapter.out.persistence.address.orderaddress;

import gytis.courier.adapter.out.persistence.address.AddressDetailsJpa;
import jakarta.persistence.*;

@Entity
@Table(name = "order_addresses")
public class OrderAddressJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private AddressDetailsJpa detailsJpa;

    protected OrderAddressJpaEntity() {}

    public OrderAddressJpaEntity(AddressDetailsJpa detailsJpa) {
        this.detailsJpa = detailsJpa;
    }

    public Long getId() { return id; }
    public AddressDetailsJpa getDetailsJpa() { return detailsJpa; }

    public void setId(Long id) { this.id = id; }
    public void setDetailsJpa(AddressDetailsJpa detailsJpa) { this.detailsJpa = detailsJpa; }
}
