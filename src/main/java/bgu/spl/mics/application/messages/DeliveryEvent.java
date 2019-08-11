package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class DeliveryEvent implements Event<DeliveryVehicle> {

    private Customer c;

    public  DeliveryEvent(Customer customer){
        this.c = customer;
    }

    public Customer getCustomer(){
        return this.c;
    }

}
