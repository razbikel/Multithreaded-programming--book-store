package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	private CountDownLatch countDownLatch = BookStoreRunner.countDownLatch;

	public LogisticsService(String name) {
		super(name);
	}

	@Override
	protected void initialize() {
		subscribeEvent(DeliveryEvent.class, new Callback<DeliveryEvent>() {
			@Override
			public void call(DeliveryEvent c) {
				AcquireVehicleEvent acquireVehicle = new AcquireVehicleEvent();
				Future<Future<DeliveryVehicle>> f = sendEvent(acquireVehicle);
				if(f == null){
					complete(c,null);
				}
				else {
					Future<DeliveryVehicle> f1 = f.get();
					if(f1 != null) {
						DeliveryVehicle vehicle = f.get().get();
						if (vehicle != null) {
							vehicle.deliver(c.getCustomer().getAddress(), c.getCustomer().getDistance());
							ReleaseEvent release = new ReleaseEvent(vehicle);
							sendEvent(release);
							complete(c, vehicle);
						}
						else{
							complete(c, null);
						}
					}
					else{
						complete(c,null);
					}
				}
			}
		});
		subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
			@Override
			public void call(TerminateBroadcast c) {
				terminate();
			}
		});
		countDownLatch.countDown();
	}

}
