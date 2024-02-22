import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Table {
    private String tableID;
    private Waiter waiter;
    private Semaphore semaphore;
    //hold the used seats
    private List<Customer> seats;
    private Queue<Customer> customerQueue;

    //we initialize the table
    public Table(String tableID) {
        this.tableID = tableID;
        this.waiter = null;
        this.semaphore = new Semaphore(1);
        this.seats = new ArrayList<Customer>();
        this.customerQueue = new LinkedList<Customer>();
    }

    //define setWaiter
    public void setWaiter(Waiter waiter) {
        this.waiter = waiter;
    }

    //define getLineLength for customer to check if line is long
    public int getLineLength() {
        return this.customerQueue.size();
    }

    public String getTableID() {
        return this.tableID;
    }
    
    //waiter will call this method to signal next customer to stop waiting
    public Customer serveNextCustomer() {
        try {
            for(int i = 0; i < seats.size(); i++) {
                Customer customer = seats.get(i);
                //if customer is not served, serve them
                if(!customer.getHasBeenServed()) {
                    return customer;
                }
            }
        } catch(Exception e) {
            System.out.println("Exception in Table.serveNextCustomer(): " + e);
        }
        return null;
    }

    //now we need a way to remove a customer from the table
    public void removeCustomer(Customer customer) {
        try {
            //acquire the semaphore
            this.semaphore.acquire();
            //remove the customer from the table
            this.seats.remove(customer);
            System.out.println("Customer " + customer.getCustomerID() + " sits up from table " + this.tableID + ".");

            if(customerQueue.size() > 0) {
                //add a new customer to the table
                seats.add(customerQueue.poll());

                //print custoemr sits on table
                //System.out.println("Customer " + customer.getCustomerID() + " sits up from table " + this.tableID + ".");
                //signal waiter to serve next customer
                waiter.signalOrder();
            }
            /* 
            Customer nextCustomer = customerQueue.poll();
            //add a new customer to the table
            seats.add(nextCustomer);
            //signal waiter to serve next customer
            waiter.signalOrder();
            */

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.semaphore.release();
        }
    }

    //now we need a way to add a customer to the table
    public void addToLine(Customer customer) {
        try {
            //acquire the semaphore
            this.semaphore.acquire();

            if(seats.size() < 4) {
                //add the customer to the table
                seats.add(customer);
                //customer # sits on table #
                System.out.println("Customer " + customer.getCustomerID() + " sits at table " + this.tableID + ".");
                //signal waiter to serve next customer
                waiter.signalOrder();
            }
            else {
                //add the customer to the queue
                customerQueue.add(customer);
                //customer # stands in line for table #
                System.out.println("Customer " + customer.getCustomerID() + " stands in line for table " + this.tableID + ".");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.semaphore.release();
        }
    }


    

}
