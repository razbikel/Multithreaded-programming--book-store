package java;

import bgu.spl.mics.MessageBusImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MessageBusImplTest {

    private MessageBusImpl mb;

    @Before
    public void setUp() throws Exception {
        mb = MessageBusImpl.getInstance();
    }


    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void subscribeEvent() {

    }

    @Test
    public void subscribeBroadcast() {
    }

    @Test
    public void complete() {
    }

    @Test
    public void sendBroadcast() {
    }

    @Test
    public void sendEvent() {
    }

//    @Test
 //   public void register() {
 //       APIService a = new APIService("API1");
 //       mb.register(a);
 //   }

    @Test
    public void unregister() {
    }

    @Test
    public void awaitMessage() {
    }
}