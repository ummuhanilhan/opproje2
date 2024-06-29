import java.util.concurrent.locks.*;
import java.util.*;
import java.text.SimpleDateFormat;

class SynchronizedReservation {
    private List<String> seats = new ArrayList<>(Arrays.asList(new String[5]));
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private SimpleDateFormat sdf;

    public SynchronizedReservation() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Istanbul")); // Turkey's time zone
    }

    private String getCurrentTime() {
        return sdf.format(new Date());
    }

    public void makeReservation(int seatNumber, String customer) {
        lock.writeLock().lock();
        try {
            System.out.println("Time: " + getCurrentTime() + "\n" + customer + " tries to book the seat " + seatNumber +"\n");
            if (seats.get(seatNumber - 1) != null) {
                System.out.println("Time: " + getCurrentTime() + "\n" + customer + " could not book seat number " + seatNumber + " since it has been already booked.\n");
            } else {
                seats.set(seatNumber - 1, customer);
                System.out.println("Time: " + getCurrentTime() + "\n" + customer + " booked seat number " + seatNumber + " successfully.\n");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void cancelReservation(int seatNumber, String customer) {
        lock.writeLock().lock();
        try {
            seats.set(seatNumber - 1, null);
            System.out.println("Seat " + seatNumber + " cancelled by " + customer);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void queryReservations() {
        lock.readLock().lock();
        try {
            System.out.println("Time: " + getCurrentTime() + "\nReader looks for available seats. State of the seats are: ");
            for (int i = 0; i < seats.size(); i++) {
                System.out.println("Seat No " + (i + 1) + " : " + (seats.get(i) == null ? "0" : "1"));
            }
            System.out.println("---------------------------------------------");
        } finally {
            lock.readLock().unlock();
        }
    }
}

class SynchronizedWriter extends Thread {
    private SynchronizedReservation reservation;
    private int seatNumber;
    private String customer;

    public SynchronizedWriter(SynchronizedReservation reservation, int seatNumber, String customer) {
        this.reservation = reservation;
        this.seatNumber = seatNumber;
        this.customer = customer;
    }

    @Override
    public void run() {
        reservation.makeReservation(seatNumber, customer);
    }
}

class SynchronizedReader extends Thread {
    private SynchronizedReservation reservation;

    public SynchronizedReader(SynchronizedReservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public void run() {
        reservation.queryReservations();
    }
}

public class Senkron  {
    public static void main(String[] args) {
        SynchronizedReservation reservation = new SynchronizedReservation();

        Thread writer1 = new SynchronizedWriter(reservation, 1, "Writer1");
        Thread writer2 = new SynchronizedWriter(reservation, 1, "Writer2");
        Thread writer3 = new SynchronizedWriter(reservation, 2, "Writer3");

        Thread reader1 = new SynchronizedReader(reservation);
        Thread reader2 = new SynchronizedReader(reservation);
        Thread reader3 = new SynchronizedReader(reservation);

        writer1.start();
        reader1.start();
        writer2.start();
        reader2.start();
        writer3.start();
        reader3.start();
    }
}
