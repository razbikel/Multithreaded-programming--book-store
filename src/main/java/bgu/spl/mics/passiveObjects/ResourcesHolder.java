package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder implements Serializable {

	private LinkedBlockingQueue<DeliveryVehicle> deliveryVehicles;
	private LinkedBlockingQueue<Future<DeliveryVehicle>> futures;


	private static class ResourcesHolderHolder{
		private static ResourcesHolder instance = new ResourcesHolder();
	}
	
	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return ResourcesHolderHolder.instance;
	}

	private ResourcesHolder(){
		this.deliveryVehicles = new LinkedBlockingQueue<>();
		this.futures = new LinkedBlockingQueue<>();
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		synchronized (this) {
			Future<DeliveryVehicle> f = new Future<>();
			if (!deliveryVehicles.isEmpty()) {
				f.resolve(deliveryVehicles.poll());
			} else {
				futures.add(f);
			}
			return f;
		}
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		synchronized (this) {
			if(vehicle == null){
				resolveAllNull();
			}
			else if (!futures.isEmpty()) {
				futures.poll().resolve(vehicle);
			} else {
				deliveryVehicles.add(vehicle);
			}
		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		deliveryVehicles.addAll(Arrays.asList(vehicles));
	}

	private void resolveAllNull (){
		synchronized (this){
			while (!futures.isEmpty()){
				futures.poll().resolve(null);
			}
		}
	}

}
