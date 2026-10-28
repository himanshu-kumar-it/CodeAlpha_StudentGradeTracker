import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** A reservation made by a guest for a room and date range. */
public class Booking {
    public enum Status { CONFIRMED, CANCELLED }

    private final String id;
    private final String guestName;
    private final String phone;
    private final int roomNumber;
    private final RoomCategory category;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final double amount;
    private final String paymentRef;
    private Status status;

    public Booking(String id, String guestName, String phone, int roomNumber, RoomCategory category,
                   LocalDate checkIn, LocalDate checkOut, double amount, String paymentRef, Status status) {
        this.id = id;
        this.guestName = guestName;
        this.phone = phone;
        this.roomNumber = roomNumber;
        this.category = category;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.amount = amount;
        this.paymentRef = paymentRef;
        this.status = status;
    }

    public String getId() { return id; }
    public String getGuestName() { return guestName; }
    public String getPhone() { return phone; }
    public int getRoomNumber() { return roomNumber; }
    public RoomCategory getCategory() { return category; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public double getAmount() { return amount; }
    public String getPaymentRef() { return paymentRef; }
    public Status getStatus() { return status; }
    public void cancel() { this.status = Status.CANCELLED; }
    public long nights() { return ChronoUnit.DAYS.between(checkIn, checkOut); }

    /** Two date ranges overlap if one starts before the other ends (check-out day is free). */
    public boolean overlaps(LocalDate in, LocalDate out) {
        return checkIn.isBefore(out) && in.isBefore(checkOut);
    }

    public void printDetails() {
        System.out.println("-------- BOOKING DETAILS --------");
        System.out.println("Booking ID  : " + id);
        System.out.println("Guest       : " + guestName + " (" + phone + ")");
        System.out.println("Room        : " + roomNumber + " (" + category + ")");
        System.out.println("Check-in    : " + checkIn);
        System.out.println("Check-out   : " + checkOut + "  (" + nights() + " night(s))");
        System.out.printf("Amount      : Rs.%.2f%n", amount);
        System.out.println("Payment ref : " + paymentRef);
        System.out.println("Status      : " + status);
        System.out.println("---------------------------------");
    }

    /** CSV line used for file storage. */
    public String toCsv() {
        return String.join(",", id, guestName.replace(",", " "), phone, String.valueOf(roomNumber),
                category.name(), checkIn.toString(), checkOut.toString(),
                String.valueOf(amount), paymentRef, status.name());
    }

    public static Booking fromCsv(String line) {
        String[] f = line.split(",");
        return new Booking(f[0], f[1], f[2], Integer.parseInt(f[3]), RoomCategory.valueOf(f[4]),
                LocalDate.parse(f[5]), LocalDate.parse(f[6]), Double.parseDouble(f[7]), f[8],
                Status.valueOf(f[9]));
    }
}
