import java.util.concurrent.Semaphore;

public class Cashier {
    private Semaphore semaphore;

    public Cashier() {
        this.semaphore = new Semaphore(1);
    }

    public void payBill(Customer customer) {
        try {
            this.semaphore.acquire();
            System.out.println("Customer " + customer.getCustomerID() + " has paid their bill.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.semaphore.release();
        }
    }
}
