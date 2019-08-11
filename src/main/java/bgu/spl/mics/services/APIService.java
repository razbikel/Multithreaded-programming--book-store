package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private Customer customer;
	private ConcurrentHashMap<Integer, LinkedBlockingQueue<String>> orderScheduales;
	private CountDownLatch countDownLatch = BookStoreRunner.countDownLatch;

	public APIService(String name, Customer c) {
		super(name);
		this.orderScheduales = new ConcurrentHashMap<>();
		for (OrderSchedule order: c.getOrderScheduales()) {
			if(this.orderScheduales.containsKey(order.getCorrespondingTick())){
				this.orderScheduales.get(order.getCorrespondingTick()).add(order.getBookTitle());
			}
			else {
				LinkedBlockingQueue<String> q = new LinkedBlockingQueue<>();
				this.orderScheduales.put(order.getCorrespondingTick(),q);
				this.orderScheduales.get(order.getCorrespondingTick()).add(order.getBookTitle());
			}
		}
		this.customer = c;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			public void call(TickBroadcast c) {
				if(orderScheduales.containsKey(c.getCurrentTick())){
					while(!orderScheduales.get(c.getCurrentTick()).isEmpty()){
						BookOrderEvent newOrder = new BookOrderEvent(customer, orderScheduales.get(c.getCurrentTick()).poll(), c.getCurrentTick());
						Future<OrderReceipt> f = sendEvent(newOrder);
						if (f.get() != null) {
							DeliveryEvent delivery = new DeliveryEvent(customer);
							sendEvent(delivery);
						}
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
