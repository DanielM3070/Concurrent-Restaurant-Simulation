//import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
public class Customer extends Thread{
    private int customerID;
    //need tables to select random table
    private Table[] tables;
    private Door[] doors;
    private Cashier cashier;
    private Semaphore semaphore;
    private boolean hasBeenServed;

    public Customer(int customerID, Table[] tables, Door[] doors, Cashier cashier) {
        this.customerID = customerID;
        this.tables = tables;
        this.doors = doors;
        this.cashier = cashier;
        this.semaphore = new Semaphore(0);
        this.hasBeenServed = false;
    }

    //will be used by waiter to check if customer has been served
    public boolean getHasBeenServed() {
        return this.hasBeenServed;
    }
    //will be used by waiter to set customer serve status
    public void setHasBeenServed(boolean hasBeenServed) {
        this.hasBeenServed = hasBeenServed;
    }

    //method for waiter signal next customer to stop waiting
    public void stopWaiting() {
        this.semaphore.release();
    }

    public int getCustomerID() {
        return this.customerID;
    }

    public void run() {
        try {
            Random rand = new Random();
            //customer will enter a random door
            Door door = doors[rand.nextInt(doors.length)];
            //customer will enter the restaurant
            door.enter(this);
            /* Customer will choose a random table
             *  If their first choice has a long line (7 or more) they 
             * will choose their second choice (if they have one) if it is not also long. 
             * Otherwise they will always choose their first choice.
             */

            //customer will choose a random table
            Table table = tables[rand.nextInt(tables.length)];
            if(table.getLineLength() >= 7) {
                System.out.println("Customer " + customerID + " sees that table " + table.getTableID() + " has a long line and will choose another table.");
                //select another random table (their second choice)

                Table secondChoice = tables[rand.nextInt(tables.length)];

                //if second choice is not long, choose it
                //otherwise choose first choice
                if(secondChoice.getLineLength() < 7) {
                    table = secondChoice;
                    System.out.println("Customer " + customerID + " sees that table " + table.getTableID() + " does not have a long line and will choose it.");
                }
                else{
                    System.out.println("Customer " + customerID + " sees that table " + secondChoice.getTableID() + " also has a long line and will choose table " + table.getTableID() + ".");
                }
            }
            //now that the customer has chosen a table, they will get in line
            table.addToLine(this);
            semaphore.acquire();

            //table will handle the waiter selection and signaling the waiter

            //once the customer has been served, they will eat
            //This will take 200 milliseconds to 1 second.

            System.out.println("Customer " + customerID + " is now eating.");
            Thread.sleep(rand.nextInt(800) + 200);
            System.out.println("Customer " + customerID + " is now done eating.");

            //now the customemr will leave the table
            table.removeCustomer(this);

            //now the customer will pay the bill
            cashier.payBill(this);

            //and finally the customer will leave the restaurant
            //door.exit(this);
            System.out.println("Customer " + customerID + " has left the restaurant.");
            //now we signal to the main thread that we have exited
            Main.signalCustomerExited();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
