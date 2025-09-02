package com.example.courier.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "delivery_options")
public class DeliveryMethod implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @NotBlank
    private String name;
    @Column
    @NotBlank
    private String description;
    @Column
    @NotNull
    private BigDecimal price;
    @Column
    @ColumnDefault("false")
    private boolean disabled;

    public DeliveryMethod() {
    }

    public DeliveryMethod(String name, String description, BigDecimal price, boolean disabled) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.disabled = disabled;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
