import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Core hotel logic: rooms, availability search, booking, cancellation and persistence. */
public class Hotel {
    private final List<Room> rooms = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final Path file;
    private int nextId = 1001;

    public Hotel(String dataFile) {
        this.file = Paths.get(dataFile);
        // 5 Standard (101-105), 3 Deluxe (201-203), 2 Suites (301-302)
        for (int i = 101; i <= 105; i++) rooms.add(new Room(i, RoomCategory.STANDARD));
        for (int i = 201; i <= 203; i++) rooms.add(new Room(i, RoomCategory.DELUXE));
        for (int i = 301; i <= 302; i++) rooms.add(new Room(i, RoomCategory.SUITE));
        load();
    }

    public List<Room> getRooms() { return rooms; }

    public List<Room> searchAvailable(LocalDate in, LocalDate out, RoomCategory cat) {
        List<Room> result = new ArrayList<>();
        for (Room r : rooms) {
            if (cat != null && r.getCategory() != cat) continue;
            if (isAvailable(r.getNumber(), in, out)) result.add(r);
        }
        return result;
    }

    public boolean isAvailable(int roomNumber, LocalDate in, LocalDate out) {
        for (Booking b : bookings) {
            if (b.getStatus() == Booking.Status.CONFIRMED && b.getRoomNumber() == roomNumber
                    && b.overlaps(in, out)) {
                return false;
            }
        }
        return true;
    }

    public Room findRoom(int number) {
        for (Room r : rooms) if (r.getNumber() == number) return r;
        return null;
    }

    public double quote(Room r, LocalDate in, LocalDate out) {
        long nights = java.time.temporal.ChronoUnit.DAYS.between(in, out);
        return nights * r.getPrice();
    }

    /** Creates a confirmed booking (payment must already be done). */
    public Booking book(String guest, String phone, Room room, LocalDate in, LocalDate out,
                        double amount, String paymentRef) {
        String id = "BK" + (nextId++);
        Booking b = new Booking(id, guest, phone, room.getNumber(), room.getCategory(),
                in, out, amount, paymentRef, Booking.Status.CONFIRMED);
        bookings.add(b);
        save();
        return b;
    }

    public Booking findBooking(String id) {
        for (Booking b : bookings) if (b.getId().equalsIgnoreCase(id.trim())) return b;
        return null;
    }

    public List<Booking> findByGuest(String name) {
        List<Booking> res = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getGuestName().toLowerCase().contains(name.trim().toLowerCase())) res.add(b);
        }
        return res;
    }

    public List<Booking> getBookings() { return bookings; }

    /**
     * Cancels a booking and returns the refund amount.
     * Refund policy: 100% if 2+ days before check-in, 50% if 1 day before, 0% otherwise.
     */
    public double cancel(Booking b) {
        long daysBefore = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), b.getCheckIn());
        double pct = daysBefore >= 2 ? 1.0 : (daysBefore == 1 ? 0.5 : 0.0);
        b.cancel();
        save();
        return b.getAmount() * pct;
    }

    // ---------- file persistence ----------
    private void save() {
        List<String> lines = new ArrayList<>();
        for (Booking b : bookings) lines.add(b.toCsv());
        try {
            Files.write(file, lines);
        } catch (IOException e) {
            System.out.println("Warning: could not save bookings: " + e.getMessage());
        }
    }

    private void load() {
        if (!Files.exists(file)) return;
        try {
            for (String line : Files.readAllLines(file)) {
                if (line.trim().isEmpty()) continue;
                Booking b = Booking.fromCsv(line);
                bookings.add(b);
                int num = Integer.parseInt(b.getId().substring(2));
                if (num >= nextId) nextId = num + 1;
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Warning: could not read bookings file: " + e.getMessage());
        }
    }
}
