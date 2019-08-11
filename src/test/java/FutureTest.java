package java;

import bgu.spl.mics.Future;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {


    private Future<Object> f;

    @Before
    public void setUp() throws Exception {
        f = new Future<>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
        f.get();
        assertTrue(f.isDone());
    }

    @Test
    public void resolve() {
        Integer i;
        i = 2;
        f.resolve(i);
        assertEquals(i,f.get());
        assertEquals(i,f.get(3, TimeUnit.SECONDS));
    }

    @Test
    public void isDone() {
        assertFalse(f.isDone());
        Integer i;
        i = 2;
        f.resolve(i);
        assertTrue(f.isDone());
    }

    @Test
    public void get1() {
        assertEquals(null,f.get(2,TimeUnit.SECONDS));
    }
}