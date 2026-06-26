package gytis.courier.adapter.out.persistence.common;

public final class AddressFormatter {
    private AddressFormatter() {}

    public static String toFullAddress(String street, String houseNumber, String flatNumber,
                                       String city, String postCode) {
        String flatIfExists = (flatNumber != null && !flatNumber.isBlank())
                ? "-" + flatNumber + ", "
                : ", ";

        return street + " " + houseNumber + flatIfExists
                + city + " " + postCode;
    }
}
