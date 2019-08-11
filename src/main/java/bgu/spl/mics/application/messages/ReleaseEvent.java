package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseEvent implements Event<Boolean> {

    DeliveryVehicle v;

    public ReleaseEvent(DeliveryVehicle v){
        this.v = v;
    }

    public DeliveryVehicle getVehicle(){
        return this.v;
    }
}
