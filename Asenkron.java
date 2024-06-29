import java.util.concurrent.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

class Reservation {
    private List<String> seats = Collections.synchronizedList(new ArrayList<>(Arrays.asList(new String[5])));
    private SimpleDateFormat sdf;

    public Reservation() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul")); // Turkey's time zone
    }

    private String getCurrentTime() {
        return sdf.format(new Date());
    }

    public void makeReservation(int seatNumber, String customer) {
        seats.set(seatNumber - 1, customer);
        System.out.println("Time: " + getCurrentTime() + "\n" + customer + " tries to book the seat " + seatNumber);
        System.out.println("Time: " + getCurrentTime() + "\n" + customer + " booked seat number " + seatNumber + " successfully.\n");
    }

    public void cancelReservation(int seatNumber, String customer) {
        seats.set(seatNumber - 1, null);
        System.out.println("Seat " + seatNumber + " cancelled by " + customer);
    }

    public void queryReservations() {
        System.out.println("Time: " + getCurrentTime() + "\nReader looks for available seats. State of the seats are: ");
        for (int i = 0; i < seats.size(); i++) {
            System.out.println("Seat No " + (i + 1) + " : " + (seats.get(i) == null ? "0" : "1"));
        }
        System.out.println("---------------------------------------------");
    }
}

class Writer extends Thread {
    private Reservation reservation;
    private int seatNumber;
    private String customer;

    public Writer(Reservation reservation, int seatNumber, String customer) {
        this.reservation = reservation;
        this.seatNumber = seatNumber;
        this.customer = customer;
    }

    @Override
    public void run() {
        reservation.makeReservation(seatNumber, customer);
    }
}

class Reader extends Thread {
    private Reservation reservation;

    public Reader(Reservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public void run() {
        reservation.queryReservations();
    }
}

public class Asenkron  {
    public static void main(String[] args) {
        Reservation reservation = new Reservation();

        Thread writer1 = new Writer(reservation, 1, "Writer1");
        Thread writer2 = new Writer(reservation, 1, "Writer2");
        Thread writer3 = new Writer(reservation, 2, "Writer3");

        Thread reader1 = new Reader(reservation);
        Thread reader2 = new Reader(reservation);
        Thread reader3 = new Reader(reservation);

        writer1.start();
        reader1.start();
        writer2.start();
        reader2.start();
        writer3.start();
        reader3.start();
    }
}
