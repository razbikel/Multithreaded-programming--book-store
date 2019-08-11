package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;


/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor ssignatures and even add new public constructors.
 */


public class TimeService extends MicroService{

	private int speed;
	private int duration;
	private int currentTick;
	private final Object lock;
	private boolean sendTer;
	private CountDownLatch countDownLatch = BookStoreRunner.countDownLatch;

	public TimeService(int speed, int duration) {
		super("Time Service");
		this.speed = speed;
		this.duration = duration;
		currentTick = 1;
		this.lock = new Object();
		this.sendTer = false;
	}

	@Override
	protected void initialize() {
		try {countDownLatch.await();}
		catch (InterruptedException ignored) {}
		subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
			public void call(TerminateBroadcast c) {
				terminate();
			}
		});
		TimerTask task = new TimerTask() {
			public void run() {
				if (currentTick <= duration){
					sendBroadcast(new TickBroadcast(currentTick));
					currentTick++;
				}
				else {
					synchronized (lock) {
						sendBroadcast(new TerminateBroadcast());
						sendTer = true;
						lock.notifyAll();
						cancel();
					}
				}
			}
		};
		Timer timer = new Timer("timer");
		timer.scheduleAtFixedRate(task,0, speed);
		synchronized (lock){
			while (!sendTer) {
				try {
					lock.wait();
				} catch (InterruptedException ignored) {}
			}
			timer.cancel();
		}
	}

}
