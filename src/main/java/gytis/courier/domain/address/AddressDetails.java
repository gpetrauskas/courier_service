package gytis.courier.domain.address;

public class AddressDetails {
    private String name;
    private String street;
    private String houseNumber;
    private String flatNumber;
    private String city;
    private String postCode;
    private String phoneNumber;

    protected AddressDetails() {}

    public AddressDetails(String name, String street, String houseNumber, String flatNumber, String city, String postCode, String phoneNumber) {
        this.name = name;
        this.street = street;
        this.houseNumber = houseNumber;
        this.flatNumber = flatNumber;
        this.city = city;
        this.postCode = postCode;
        this.phoneNumber = phoneNumber;
    }

    public static AddressDetails createValidated(String name, String street, String houseNumber, String flatNumber, String city, String postCode, String phoneNumber) {
        AddressRules.validateName(name);
        AddressRules.validateStreet(street);
        AddressRules.validateHouseNumber(houseNumber);
        AddressRules.validateFlatNumber(flatNumber);
        AddressRules.validateCity(city);
        AddressRules.validatePostCode(postCode);
        AddressRules.validatePhone(phoneNumber);

        return new AddressDetails(name, street, houseNumber, flatNumber, city, postCode, phoneNumber);
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public String getCity() {
        return city;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
