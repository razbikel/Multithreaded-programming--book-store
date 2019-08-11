package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

/*
checks if the customer have enough money for buying the book and if the book's amount in the inventory is bigger then 0
and if yes decrease the amount of the book by one and charge the customers credit card by the price of the book and returns the price
and if not returns -1
*/
public class OrderAbilityEvent implements Event<Integer> {
    private Customer c;
    private String book;

    public OrderAbilityEvent(Customer c, String nameOfBook){
        this.c = c;
        this.book = nameOfBook;
    }

    public Customer getCustomer (){
        return this.c;
    }

    public String getBook(){
        return this.book;
    }
}
