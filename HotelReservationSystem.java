import java.io.*;
import java.util.*;

class Room implements Serializable {
    private int roomNumber;
    private String category; // Standard, Deluxe, Suite
    private double price;
    private boolean available = true;

    public Room(int roomNumber, String category, double price) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.price = price;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + category + "), Rs." + price +
                ", " + (available ? "Available" : "Booked");
    }
}

class Reservation implements Serializable {
    private String guestName;
    private int roomNumber;
    private String paymentStatus; // Paid / Pending

    public Reservation(String guestName, int roomNumber, String paymentStatus) {
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.paymentStatus = paymentStatus;
    }

    public String getGuestName() { return guestName; }
    public int getRoomNumber() { return roomNumber; }

    @Override
    public String toString() {
        return "Guest: " + guestName + ", Room: " + roomNumber +
                ", Payment: " + paymentStatus;
    }
}

public class HotelReservationSystem {
    private static final String ROOMS_FILE = "rooms.dat";
    private static final String RES_FILE = "reservations.dat";

    private List<Room> rooms = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();

    public static void main(String[] args) {
        HotelReservationSystem app = new HotelReservationSystem();
        app.loadData();
        app.seedRooms();
        app.menu();
        app.saveData();
    }

    // ----- Menu -----
    private void menu() {
        Scanner sc = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n=== Hotel Reservation System ===");
            System.out.println("1. View available rooms");
            System.out.println("2. Search rooms by category");
            System.out.println("3. Book room");
            System.out.println("4. Cancel reservation");
            System.out.println("5. View all reservations");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> showAvailableRooms();
                case 2 -> {
                    System.out.print("Category (Standard/Deluxe/Suite): ");
                    String cat = sc.nextLine();
                    searchByCategory(cat);
                }
                case 3 -> bookRoom(sc);
                case 4 -> cancelReservation(sc);
                case 5 -> showReservations();
                case 6 -> System.out.println("Saving data and exiting...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 6);
        sc.close();
    }

    // ----- Core features -----
    private void showAvailableRooms() {
        System.out.println("\nAvailable rooms:");
        for (Room r : rooms) {
            if (r.isAvailable()) {
                System.out.println(r);
            }
        }
    }

    private void searchByCategory(String category) {
        System.out.println("\nRooms in category: " + category);
        for (Room r : rooms) {
            if (r.getCategory().equalsIgnoreCase(category) && r.isAvailable()) {
                System.out.println(r);
            }
        }
    }

    private void bookRoom(Scanner sc) {
        System.out.print("Enter room number to book: ");
        int number = sc.nextInt();
        sc.nextLine();
        Room room = findRoom(number);
        if (room == null || !room.isAvailable()) {
            System.out.println("Room not available.");
            return;
        }

        System.out.print("Guest name: ");
        String name = sc.nextLine();

        // Simple payment simulation
        System.out.println("Room price Rs." + room.getPrice() + ". Simulating payment...");
        System.out.println("Payment successful.");

        room.setAvailable(false);
        reservations.add(new Reservation(name, number, "Paid"));
        System.out.println("Reservation confirmed for " + name + " in room " + number);
    }

    private void cancelReservation(Scanner sc) {
        System.out.print("Enter guest name to cancel: ");
        String name = sc.nextLine();

        Reservation target = null;
        for (Reservation r : reservations) {
            if (r.getGuestName().equalsIgnoreCase(name)) {
                target = r;
                break;
            }
        }

        if (target == null) {
            System.out.println("Reservation not found.");
            return;
        }

        Room room = findRoom(target.getRoomNumber());
        if (room != null) room.setAvailable(true);
        reservations.remove(target);
        System.out.println("Reservation cancelled for " + name);
    }

    private void showReservations() {
        System.out.println("\nCurrent reservations:");
        if (reservations.isEmpty()) {
            System.out.println("No reservations.");
        } else {
            for (Reservation r : reservations) {
                System.out.println(r);
            }
        }
    }

    private Room findRoom(int number) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == number) return r;
        }
        return null;
    }

    // ----- File I/O -----
    @SuppressWarnings("unchecked")
    private void loadData() {
        rooms = readList(ROOMS_FILE);
        reservations = readList(RES_FILE);
        if (rooms == null) rooms = new ArrayList<>();
        if (reservations == null) reservations = new ArrayList<>();
    }

    private void saveData() {
        writeList(ROOMS_FILE, rooms);
        writeList(RES_FILE, reservations);
    }

    private <T> List<T> readList(String file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    private void writeList(String file, List<?> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(list);
        } catch (IOException e) {
            System.out.println("Could not save " + file);
        }
    }

    // create default rooms only first time
    private void seedRooms() {
        if (!rooms.isEmpty()) return;
        rooms.add(new Room(101, "Standard", 2000));
        rooms.add(new Room(102, "Standard", 2000));
        rooms.add(new Room(201, "Deluxe", 3500));
        rooms.add(new Room(202, "Deluxe", 3500));
        rooms.add(new Room(301, "Suite", 5000));
    }
}
