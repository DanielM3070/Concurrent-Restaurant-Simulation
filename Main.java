//import java.util.ArrayList;
//import java.util.List;
import java.util.concurrent.Semaphore;


public class Main {
    // Create a new semaphore initialized to 1
	// Operations:
	//   gLock.acquire()  -- This is the same thing as sem_wait
	//        - Decrements the value, and blocks it value is less than zero
	//        - A initial value of 1, means the first thread to call acquire will not block
	//   gLock.release() -- This is the same thing as sem_signal
	//        - Increments the value, and unblocks a blocked thread if the new value is zero or less
	

    //create const variable to hold number of customers
    private static final int NUM_OF_CUSTOMERS = 40;
    //semaphore for customers to signal to main that they have exited aka finished
    private static Semaphore customerExitSemaphore = new Semaphore(1);

    //also keep track of number of customers that have exited
    private static int numOfCustomersExited = 0;

    //also want a semaphore for the last customer to signal to main that they have exited
    //will be initialized to 0, so that the last customer will block until the main thread releases it
    private static Semaphore lastCustomerExitSemaphore = new Semaphore(0);
    
    public static void signalCustomerExited() {
    	//acquire the semaphore
    	try {
            customerExitSemaphore.acquire();
            numOfCustomersExited++;
            //if this is the last customer, release the lastCustomerExitSemaphore
            if (numOfCustomersExited >= NUM_OF_CUSTOMERS) {
                lastCustomerExitSemaphore.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            customerExitSemaphore.release();
        }
    }


    public static void main(String[] args) throws Exception {

        //we will declare all parts of the restaurant here
        //beginning with the kitchen class itself
        Kitchen kitchen = new Kitchen();

        //now we create the three tables that will be in the restaurant
        //we will use a table array to hold the tables
        Table[] tables = new Table[3];
        //now we label the tables, A, B, and C
        tables[0] = new Table("A");
        tables[1] = new Table("B");
        tables[2] = new Table("C");

        //now we need the three waiters, 1, 2, and 3
        //we will use a waiter array to hold the waiters
        Waiter[] waiters = new Waiter[3];
        //now we label the waiters, 1, 2, and 3
        waiters[0] = new Waiter(1, kitchen);
        waiters[1] = new Waiter(2, kitchen);
        waiters[2] = new Waiter(3, kitchen);
        //now we will assign each waiter to a table, and each table to a waiter
        //waiter 1 will be assigned to table A
        waiters[0].setTable(tables[0]);
        tables[0].setWaiter(waiters[0]);
        //waiter 2 will be assigned to table B
        waiters[1].setTable(tables[1]);
        tables[1].setWaiter(waiters[1]);
        //waiter 3 will be assigned to table C
        waiters[2].setTable(tables[2]);
        tables[2].setWaiter(waiters[2]);

        //we know that there will only be two doors, so we will create two door objects
        Door[] doors = new Door[2];
        //now we label the doors, 1 and 2
        doors[0] = new Door(1);
        doors[1] = new Door(2);

        //The customer will then pay the bill. Only one customer can pay at a time.
        //to handle the 'one at a time' paying part, we will use a cashier class with a semaphore
        Cashier cashier = new Cashier();

        //now we only need to create the customers
        //we will use a customer array to hold the customers
        //we send the customer constructor the tables, doors, and cashier objects
        Customer[] customers = new Customer[NUM_OF_CUSTOMERS];
        for(int i = 0; i < NUM_OF_CUSTOMERS; i++) {
            customers[i] = new Customer(i+1, tables, doors, cashier);
        }

        //now we will start the threads
        //start all the waiters
        for(int i = 0; i < waiters.length; i++) {
            waiters[i].start();;
            //waiter.start();
        }
        //start all the customers
        for(int i = 0; i < customers.length; i++) {
            //Customer customer = customers[i];
            customers[i].start();
        }


        //now we will wait for all the customers to exit
        //we will use a semaphore to do this
        //we will have each customer signal to the main thread that they have exited
        //the main thread will keep track of the number of customers that have exited
        //when the last customer exits, the main thread will release the lastCustomerExitSemaphore
        //the last customer will block until the main thread releases it
        lastCustomerExitSemaphore.acquire();

        //now we signal all the waiters to exit/stop waiting
        for(int i = 0; i < waiters.length; i++) {
            waiters[i].signalOrder();
        }

    

    }



}

