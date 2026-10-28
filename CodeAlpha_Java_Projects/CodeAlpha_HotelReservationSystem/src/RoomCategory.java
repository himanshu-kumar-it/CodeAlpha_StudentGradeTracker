/** Room categories with their nightly price and amenities. */
public enum RoomCategory {
    STANDARD(2000.0, "1 double bed, Wi-Fi, TV"),
    DELUXE(3500.0, "King bed, Wi-Fi, TV, mini-bar, city view"),
    SUITE(6000.0, "Bedroom + living area, Wi-Fi, TV, mini-bar, bathtub");

    private final double pricePerNight;
    private final String amenities;

    RoomCategory(double pricePerNight, String amenities) {
        this.pricePerNight = pricePerNight;
        this.amenities = amenities;
    }

    public double getPricePerNight() { return pricePerNight; }
    public String getAmenities() { return amenities; }
}
