package gytis.courier.adapter.out.persistence.delivery;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "delivery_options")
public class DeliveryOptionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String name;

    @Column(nullable = false, length = 50)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean disabled;

    public DeliveryOptionJpaEntity() {}

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public boolean isDisabled() { return disabled; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setDisabled(boolean disabled) { this.disabled = disabled; }
}
