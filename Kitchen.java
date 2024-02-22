import java.util.Random;
import java.util.concurrent.Semaphore;
public class Kitchen {
    private Semaphore semaphore;
    public Kitchen() {
        this.semaphore = new Semaphore(1);
    }

    public void use(Waiter waiter) {
        try {
            this.semaphore.acquire();
            System.out.println("Waiter " + waiter.getWaiterID() + " enters the kitchen to pick up customer" 
            + waiter.getCustomerID() + "'s order.");
            Random rand = new Random();
            Thread.sleep(rand.nextInt(400) + 100);
            //waiter exits the kitchen
            System.out.println("Waiter " + waiter.getWaiterID() + " exits the kitchen with customer " + waiter.getCustomerID() + "'s order.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.semaphore.release();
        }
    }

}
