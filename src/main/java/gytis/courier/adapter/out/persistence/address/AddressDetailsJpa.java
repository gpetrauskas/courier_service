package gytis.courier.adapter.out.persistence.address;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AddressDetailsJpa {
    @Column(name = "name") private String name;
    @Column(name = "street") private String street;
    @Column(name = "house_number") private String houseNumber;
    @Column(name = "flat_number") private String flatNumber;
    @Column(name = "city") private String city;
    @Column(name = "post_code") private String postCode;
    @Column(name = "phone_number") private String phoneNumber;

    public String getName() { return name; }
    public String getStreet() { return street; }
    public String getHouseNumber() { return houseNumber; }
    public String getFlatNumber() { return flatNumber; }
    public String getCity() { return city; }
    public String getPostCode() { return postCode; }
    public String getPhoneNumber() { return phoneNumber; }

    public void setName(String name) { this.name = name; }
    public void setStreet(String street) { this.street = street; }
    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }
    public void setCity(String city) { this.city = city; }
    public void setPostCode(String postCode) { this.postCode = postCode; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
