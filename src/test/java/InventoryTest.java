package java;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {

    Inventory inv;

    @Before
    public void setUp() throws Exception {
        inv = Inventory.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
        assertEquals(inv,Inventory.getInstance());
        assertNotNull(Inventory.getInstance());
    }

    @Test
    public void load() {
        BookInventoryInfo[] forLoad1 = new BookInventoryInfo[0];
        inv.load(forLoad1);
        assertEquals(-1,inv.checkAvailabiltyAndGetPrice("Harry Poter"));
        assertEquals(-1,inv.checkAvailabiltyAndGetPrice("Lion King"));
        BookInventoryInfo book1 = new BookInventoryInfo("Harry Poter", 2,100);
        BookInventoryInfo book2 = new BookInventoryInfo("Lion King", 3,200);
        BookInventoryInfo [] forLoad2 = {book1,book2};
        inv.load(forLoad2);
        assertEquals(100,inv.checkAvailabiltyAndGetPrice("Harry Poter"));
        assertEquals(200,inv.checkAvailabiltyAndGetPrice("Lion King"));
    }


    @Test
    public void take() {
        OrderResult result =  inv.take("Lion King");
        assertEquals(OrderResult.NOT_IN_STOCK, result);
        BookInventoryInfo book1 = new BookInventoryInfo("Harry Poter", 1,100);
        BookInventoryInfo[] forLoad = {book1};
        inv.load(forLoad);
        assertEquals(OrderResult.SUCCESSFULLY_TAKEN, inv.take("Harry Poter"));
        assertEquals(-1,inv.checkAvailabiltyAndGetPrice("Harry Poter"));
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {//checked in loadtest and taketest
    }

}