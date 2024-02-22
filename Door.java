import java.util.concurrent.Semaphore;

public class Door {
    private int doorID;
    private Semaphore semaphore;

    public Door(int doorID) {
        this.doorID = doorID;
        this.semaphore = new Semaphore(1);
    }

    public void enter(Customer customer) {
        try {
            this.semaphore.acquire();
            System.out.println("Customer " + customer.getCustomerID() + " enters the restaurant through door " + this.doorID + ".");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.semaphore.release();
        }
    }

}
