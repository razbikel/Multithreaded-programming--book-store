package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Time;

public class ServicesInformation {

    public Time time;
    public int selling;
    public int inventoryService;
    public int logistics;
    public int resourcesService;
    public Customer[] customers;

    public int getNumOfServices(){
        return 1 + selling + inventoryService + logistics + resourcesService + customers.length;
    }


}
