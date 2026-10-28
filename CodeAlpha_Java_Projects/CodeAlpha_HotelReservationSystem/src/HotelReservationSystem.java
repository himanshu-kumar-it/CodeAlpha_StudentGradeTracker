import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * CodeAlpha Java Internship - Task 4: Hotel Reservation System (console).
 * Search rooms, book, cancel, simulated payment, view booking details, file storage.
 */
public class HotelReservationSystem {
    private static final Scanner sc = new Scanner(System.in);
    private static final Hotel hotel = new Hotel("bookings.csv");

    public static void main(String[] args) {
        System.out.println("=== Hotel Reservation System (CodeAlpha) ===");
        boolean running = true;
        while (running) {
            System.out.println("\n------------ MENU ------------");
            System.out.println("1. View all rooms & categories");
            System.out.println("2. Search available rooms");
            System.out.println("3. Book a room");
            System.out.println("4. View booking details");
            System.out.println("5. Cancel a booking");
            System.out.println("6. List all bookings");
            System.out.println("7. Exit");
            int c = readInt("Choice: ");
            switch (c) {
                case 1: viewRooms(); break;
                case 2: searchRooms(); break;
                case 3: bookRoom(); break;
                case 4: viewBooking(); break;
                case 5: cancelBooking(); break;
                case 6: listBookings(); break;
                case 7: running = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
        System.out.println("Thank you for using the system. Goodbye!");
    }

    private static void viewRooms() {
        System.out.println("\n--- Rooms ---");
        for (Room r : hotel.getRooms()) System.out.println(r);
    }

    private static void searchRooms() {
        LocalDate in = readDate("Check-in date (yyyy-MM-dd): ");
        LocalDate out = readDate("Check-out date (yyyy-MM-dd): ");
        if (!validDates(in, out)) return;
        RoomCategory cat = readCategory(true);
        List<Room> list = hotel.searchAvailable(in, out, cat);
        if (list.isEmpty()) {
            System.out.println("No rooms available for these dates.");
            return;
        }
        System.out.println("\nAvailable rooms:");
        for (Room r : list) {
            System.out.printf("%s  | total: Rs.%.2f%n", r, hotel.quote(r, in, out));
        }
    }

    private static void bookRoom() {
        LocalDate in = readDate("Check-in date (yyyy-MM-dd): ");
        LocalDate out = readDate("Check-out date (yyyy-MM-dd): ");
        if (!validDates(in, out)) return;
        RoomCategory cat = readCategory(false);
        List<Room> list = hotel.searchAvailable(in, out, cat);
        if (list.isEmpty()) {
            System.out.println("Sorry, no " + cat + " rooms available for these dates.");
            return;
        }
        for (Room r : list) System.out.println("  " + r);
        Room room = hotel.findRoom(readInt("Enter room number to book: "));
        if (room == null || !list.contains(room)) {
            System.out.println("That room is not in the available list.");
            return;
        }
        String name = readLine("Guest name: ");
        String phone = readLine("Phone number: ");
        if (name.isEmpty() || phone.isEmpty()) {
            System.out.println("Name and phone are required.");
            return;
        }
        double total = hotel.quote(room, in, out);
        System.out.printf("Total amount: Rs.%.2f for %d night(s)%n", total, java.time.temporal.ChronoUnit.DAYS.between(in, out));

        System.out.println("Payment method: 1) Card  2) UPI  3) Pay at hotel");
        int m = readInt("Choose: ");
        Payment.Method method = m == 1 ? Payment.Method.CARD : m == 2 ? Payment.Method.UPI : Payment.Method.CASH_AT_HOTEL;
        String ref = Payment.process(method, total);
        if (ref == null) {
            System.out.println("Payment failed (simulated). Booking not created. Please try again.");
            return;
        }
        System.out.println("Payment successful. Reference: " + ref);
        Booking b = hotel.book(name, phone, room, in, out, total, ref);
        System.out.println("Booking confirmed!");
        b.printDetails();
    }

    private static void viewBooking() {
        Booking b = hotel.findBooking(readLine("Booking ID: "));
        if (b == null) System.out.println("Booking not found.");
        else b.printDetails();
    }

    private static void cancelBooking() {
        Booking b = hotel.findBooking(readLine("Booking ID to cancel: "));
        if (b == null) { System.out.println("Booking not found."); return; }
        if (b.getStatus() == Booking.Status.CANCELLED) {
            System.out.println("This booking is already cancelled.");
            return;
        }
        b.printDetails();
        String yes = readLine("Confirm cancel? (y/n): ");
        if (!yes.equalsIgnoreCase("y")) { System.out.println("Cancellation aborted."); return; }
        double refund = hotel.cancel(b);
        System.out.printf("Booking %s cancelled. Refund: Rs.%.2f%n", b.getId(), refund);
        System.out.println("(Refund policy: 100% if 2+ days before check-in, 50% if 1 day, otherwise 0%)");
    }

    private static void listBookings() {
        if (hotel.getBookings().isEmpty()) { System.out.println("No bookings yet."); return; }
        System.out.printf("%-8s %-15s %-5s %-10s %-10s %-10s %-10s%n",
                "ID", "Guest", "Room", "Check-in", "Check-out", "Amount", "Status");
        for (Booking b : hotel.getBookings()) {
            System.out.printf("%-8s %-15s %-5d %-10s %-10s %-10.2f %-10s%n",
                    b.getId(), b.getGuestName(), b.getRoomNumber(), b.getCheckIn(),
                    b.getCheckOut(), b.getAmount(), b.getStatus());
        }
    }

    // ---------- input helpers ----------
    private static boolean validDates(LocalDate in, LocalDate out) {
        if (in.isBefore(LocalDate.now())) {
            System.out.println("Check-in cannot be in the past.");
            return false;
        }
        if (!out.isAfter(in)) {
            System.out.println("Check-out must be after check-in.");
            return false;
        }
        return true;
    }

    private static RoomCategory readCategory(boolean allowAny) {
        System.out.println("Category: 1) Standard  2) Deluxe  3) Suite" + (allowAny ? "  4) Any" : ""));
        while (true) {
            int c = readInt("Choose: ");
            if (c == 1) return RoomCategory.STANDARD;
            if (c == 2) return RoomCategory.DELUXE;
            if (c == 3) return RoomCategory.SUITE;
            if (c == 4 && allowAny) return null;
            System.out.println("Invalid category.");
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            String s = readLine(prompt);
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException e) {
                System.out.println("Use format yyyy-MM-dd, e.g. 2026-11-15");
            }
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!sc.hasNextLine()) { System.out.println("\n(input closed) Goodbye!"); System.exit(0); }
        return sc.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNextLine()) { System.out.println("\n(input closed) Goodbye!"); System.exit(0); }
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}
