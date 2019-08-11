package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.OrderAbilityEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private static Inventory inv;
	private CountDownLatch countDownLatch = BookStoreRunner.countDownLatch;

	public InventoryService(String name) {
		super(name);
		this.inv = Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeEvent(OrderAbilityEvent.class, new Callback<OrderAbilityEvent>() {
			public void call(OrderAbilityEvent c) {
				Integer price = inv.checkAvailabiltyAndGetPrice(c.getBook());
				if(price != -1){
					if(c.getCustomer().getAvailableCreditAmount() >= price) {
						inv.take(c.getBook());
						c.getCustomer().chargCraditCard(price);
					}
					else {
						price = -1;
					}
				}
				complete(c, price);
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
