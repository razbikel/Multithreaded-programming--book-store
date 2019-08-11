package bgu.spl.mics;


import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
@SuppressWarnings("ALL")
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<MicroService,LinkedBlockingQueue<Message>> microServices;
	private ConcurrentHashMap<Class<? extends Event>,LinkedBlockingQueue<MicroService>> eventList;
	private ConcurrentHashMap<Class<? extends Broadcast>,LinkedList<MicroService>> broadcastList;
	private ConcurrentHashMap<Event,Future> results;


	private static class MessageBusImplHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	public static MessageBusImpl getInstance(){
		return MessageBusImplHolder.instance;
	}


	private MessageBusImpl(){
		microServices = new ConcurrentHashMap<>();
		eventList = new ConcurrentHashMap<>();
		broadcastList = new ConcurrentHashMap<>();
		results = new ConcurrentHashMap<>();
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(!eventList.containsKey(type)) {
			LinkedBlockingQueue<MicroService> q = new LinkedBlockingQueue<>();
			eventList.put(type, q);
		}
		eventList.get(type).add(m);

	}


	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(!broadcastList.containsKey(type)) {
			LinkedList<MicroService> l = new LinkedList<>();
			broadcastList.put(type, l);
		}
		synchronized (broadcastList.get(type)) {
			broadcastList.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		results.get(e).resolve(result);
		results.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if (broadcastList.get(b.getClass()) != null) {
			for (int i = 0; i < broadcastList.get(b.getClass()).size(); i++) {
				MicroService m = broadcastList.get(b.getClass()).get(i);
				if (microServices.get(m) != null) {
					microServices.get(m).add(b);
				}
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if (eventList.get(e.getClass()) != null && !(eventList.get(e.getClass()).isEmpty())) {
			Future<T> f = new Future<>();
			results.put(e, f);
			MicroService m;
			synchronized (eventList.get(e.getClass())) {
				m = eventList.get(e.getClass()).poll();
				eventList.get(e.getClass()).add(m);
				microServices.get(m).add(e);
			}
			return f;
		} else {
			return null;
		}

	}

	@Override
	public void register(MicroService m) {
		LinkedBlockingQueue<Message> q = new LinkedBlockingQueue<>();
		microServices.putIfAbsent(m, q);
	}

	@Override
	public void unregister(MicroService m) {
		// remove the microService m from the queue of the event type if its existe
		for (Map.Entry<Class<? extends Event>, LinkedBlockingQueue<MicroService>> entry : eventList.entrySet()) {
			Class<? extends Event> e = entry.getKey();
			synchronized (eventList.get(e)) {
				eventList.get(e).remove(m);
			}
		}
		// remove the microService m from the list of the broadcast type if its existe
		for (Map.Entry<Class<? extends Broadcast>, LinkedList<MicroService>> entry : broadcastList.entrySet()) {
			Class<? extends Broadcast> b = entry.getKey();
			synchronized (broadcastList.get(b)) {
				broadcastList.get(b).remove(m);
			}
		}
		synchronized (microServices.get(m)) {
			while (!microServices.get(m).isEmpty()) {
				Message message = microServices.get(m).poll();
				if (message instanceof Event) {
					m.complete((Event) message, null);
				}
			}
		}
		microServices.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(microServices.get(m) == null){
			throw new IllegalStateException("micro service was never register");
		}
		return microServices.get(m).take();
	}

	

}
