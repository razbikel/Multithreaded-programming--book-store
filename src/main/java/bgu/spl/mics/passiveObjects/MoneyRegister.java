package bgu.spl.mics.application.passiveObjects;


import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {

	private LinkedList<OrderReceipt> orderReceipts;


	private static class MoneyRegisterHolder{
		private static MoneyRegister instance = new MoneyRegister();
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
		return MoneyRegisterHolder.instance;
	}

	private MoneyRegister(){
		this.orderReceipts = new LinkedList<>();
	}


	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		this.orderReceipts.add(r);
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		int output = 0;
		for (OrderReceipt r:orderReceipts) {
			output = output + r.getPrice();
		}
		return output;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		c.chargCraditCard(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		try{
			File fileOne=new File(filename);
			FileOutputStream fos2= new FileOutputStream(fileOne);
			ObjectOutputStream oos2= new ObjectOutputStream(fos2);
			oos2.writeObject(orderReceipts);
			oos2.flush();
			oos2.close();
			fos2.close();
		}catch(Exception e){}
	}
}
