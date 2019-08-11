package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;


/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private static ResourcesHolder holder;
	private CountDownLatch countDownLatch = BookStoreRunner.countDownLatch;

	public ResourceService(String name) {
		super(name);
		this.holder = ResourcesHolder.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeEvent(AcquireVehicleEvent.class, new Callback<AcquireVehicleEvent>() {
			@Override
			public void call(AcquireVehicleEvent c) {
				Future<DeliveryVehicle> f = holder.acquireVehicle();
				complete(c,f);
			}
		});
		subscribeEvent(ReleaseEvent.class, new Callback<ReleaseEvent>() {
			@Override
			public void call(ReleaseEvent c) {
				holder.releaseVehicle(c.getVehicle());
				complete(c,true);
			}
		});
		subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
			@Override
			public void call(TerminateBroadcast c) {
				holder.releaseVehicle(null);
				terminate();
			}
		});
		countDownLatch.countDown();
	}

}
