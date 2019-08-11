package bgu.spl.mics.application.passiveObjects;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;


/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {

	private BookInventoryInfo [] booksInfo;

	private static class InventoryHolder{
		private static Inventory instance = new Inventory();
	}

	private Inventory(){
		this.booksInfo = null;
	}

	/**
     * Retrieves the single instance of this class.
	 * @pre: none
	 * @post: instance != null
     */
	public static Inventory getInstance() {
		return InventoryHolder.instance;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
	 * @pre: instance != null
	 * @post: none
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		this.booksInfo = inventory;
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
	 * @pre: instance != null
	 * @post: none
     */
	public OrderResult take (String book) {
		int index = this.checkAvailabilty(book);
		OrderResult output = OrderResult.NOT_IN_STOCK;
		if(index != -1){
			if(booksInfo[index].getSemaphore().tryAcquire()){
				booksInfo[index].setAmountInInventory(booksInfo[index].getAmountInInventory() - 1);
				output = OrderResult.SUCCESSFULLY_TAKEN;
			}
		}
		return output;
	}
	
	
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
	 * @pre: instance != null
	 * @post: none
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		int index = checkAvailabilty(book);
		if(index != -1){
			return booksInfo[index].getPrice();
		}
		return index;
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename) {
		HashMap<String, Integer> output = new HashMap<>();
		for (int i = 0; i < booksInfo.length; i++) {
			Integer amount = booksInfo[i].getAmountInInventory();
			output.put(booksInfo[i].getBookTitle(),amount);
		}


		try{
			File fileOne = new File(filename);
			FileOutputStream fos = new FileOutputStream(fileOne);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(output);
			oos.flush();
			oos.close();
			fos.close();
		}catch(Exception e){
		}

	}
	//checks if the book available: if yes returns the index of the book in booksInfo and if not returns -1
	private int checkAvailabilty (String book){
		int index = -1;
		for (int i = 0 ; i < booksInfo.length ; i++){
			if (booksInfo[i].getBookTitle().equals(book)){
				if (booksInfo[i].getAmountInInventory()>0)
					index = i;
			}
		}
		return index;
	}
}
