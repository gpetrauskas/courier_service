package gytis.courier.domain.delivery;

import java.util.Arrays;

/**
 * Represents grouping of delivery methods for display or categorize purposes.
 *
 * <p>Groups are determined based on keywords found in the delivery methods name.</p>
 *
 * <ul>
 *     <li>{@code SIZE} - Methods related to size package</li>
 *     <li>{@code WEIGHT} - Methods related to weight package</li>
 *     <li>{@code PREFERENCE} - Delivery preferences (one-day, 3-day deliveries, etc.)</li>
 * </ul>*/
public enum DeliveryGroup {
    SIZE("size"),
    WEIGHT("weight"),
    PREFERENCE("preference");

    private final String keyword;

    DeliveryGroup(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    /**
     * Determines the delivery group from a method name.
     *
     * @param name the keyword in delivery name
     * @return matching DeliveryGroup based on keyword or PREFERENCE if none match
     * @throws IllegalArgumentException if keyword is null or blank
     */
    public static DeliveryGroup determineGroupFromName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Delivery method cannot be null");
        }

        String lower = name.toLowerCase();
        return Arrays.stream(values())
                .filter(group -> lower.contains(group.keyword))
                .findFirst()
                .orElse(PREFERENCE);
    }
}
