import java.util.concurrent.Semaphore;
import java.util.Random;
public class Waiter extends Thread{
    private int waiterID;
    private Table table;
    private Semaphore semaphore;
    private Kitchen kitchen;
    private Customer currCustomer;


    public Waiter(int waiterID, Kitchen kitchen) {
        this.waiterID = waiterID;
        this.semaphore = new Semaphore(0);
        this.kitchen = kitchen;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public void signalOrder() {
        this.semaphore.release();
    }

    //return customerID of customer being served
    
    
    public int getCustomerID() {
        return currCustomer.getCustomerID();
       // return 99;// this.customer.getCustomerID();
    }
    

    public int getWaiterID() {
        return this.waiterID;
    }

    public void run() {
        try {
            while (true) {
                //waiter will wait for customer to signal them to stop waiting
                this.semaphore.acquire();
                //waiter will serve next customer
                Customer customer = table.serveNextCustomer();
                currCustomer = customer;

                //if no more customers break out of loop
                if (customer == null) {
                    break;
                }
                /*
                 * 5. The waiter goes to the kitchen. Only one waiter can use the kitchen at a time.
                 *  He will spend 100 to 500 milliseconds in the kitchen to deliver the order.
                 * 6. The waiter waits outside the kitchen for the order to be ready (this will be between 300 milliseconds to 1 second)
                 * 7. The waiter will go to the kitchen to get the order. He will spend 100 to 500 milliseconds in the kitchen.

                 */
                //print that waiter is now serving customer
                System.out.println("Waiter " + this.waiterID + " is now serving customer " + customer.getCustomerID() );
                //kitchen.use(this);
                //waiter will spend 100 to 500 milliseconds in the kitchen to deliver the order
                //print that waiter is delivering order to kitchen
                System.out.println("Waiter " + this.waiterID + " goes to the kitchen to deliver customer " + customer.getCustomerID() + "'s order.");
                Random rand = new Random();
                Thread.sleep(rand.nextInt(400) + 100);
                //waiter will wait outside the kitchen for the order to be ready
                System.out.println("Waiter " + this.waiterID + " exits the kitchen and waits");
                rand = new Random();
                Thread.sleep(rand.nextInt(700) + 300);
                System.out.println("Waiter " + this.waiterID + " goes to the kitchen to get customer " + customer.getCustomerID() + "'s order.");
                kitchen.use(this);

                customer.setHasBeenServed(true);
                System.out.println("Waiter " + this.waiterID + " brings food to customer " + customer.getCustomerID() + ".");
                customer.stopWaiting();
            }
            System.out.println("Waiter " + this.waiterID + " exits the restaurant.");
        } catch(Exception e) {
            System.out.println("Exception in Waiter.run(): " + e);
        }
    }
}
