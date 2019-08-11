package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.OrderAbilityEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;


/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private static MoneyRegister mn;
	private int currentTick;
	private CountDownLatch countDownLatch = BookStoreRunner.countDownLatch;

	public SellingService(String name) {
		super(name);
		this.mn = MoneyRegister.getInstance();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			public void call(TickBroadcast c) {
				currentTick = c.getCurrentTick();
			}
		});
		subscribeEvent(BookOrderEvent.class, new Callback<BookOrderEvent>() {
			@Override
			public void call(BookOrderEvent c) {
				int proccessTick = currentTick;
				OrderAbilityEvent Ability = new OrderAbilityEvent(c.getCustomer(),c.getBook());
				Future<Integer> f = sendEvent(Ability);
				if (f.get() != -1) {
					OrderReceipt receipt = new OrderReceipt(0, getName(), c.getCustomer().getId(), c.getBook(), f.get(), currentTick, c.getOrderTick(), proccessTick);
					mn.file(receipt);
					c.getCustomer().addReciept(receipt);
					complete(c, receipt);
				}
				else {
					complete(c,null);
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
