package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;



/** This is the Main class of the application. You should parse the input file, 
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static CountDownLatch countDownLatch;

    public static void main(String[] args) {
        Gson gson = new Gson();
        try {
            String path = args[0];
            FileReader file = new FileReader(path);
            JsonReader reader = new JsonReader(file);
            JsonParser parser = gson.fromJson(reader, JsonParser.class);
            Inventory inventory = Inventory.getInstance();
            BookInventoryInfo[] booksForLoad = new BookInventoryInfo[parser.initialInventory.length];
            for (int i = 0; i < parser.initialInventory.length; i++) {
                BookInventoryInfo newBook = new BookInventoryInfo(parser.initialInventory[i].bookTitle, parser.initialInventory[i].amount, parser.initialInventory[i].price);
                booksForLoad[i] = newBook;
            }
            inventory.load(booksForLoad);
            ResourcesHolder holder = ResourcesHolder.getInstance();
            holder.load(parser.initialResources[0].vehicles);

            Vector<Thread> threads = new Vector<>(parser.services.getNumOfServices());
            countDownLatch = new CountDownLatch(parser.services.getNumOfServices() - 1);

            int numOfSelling = parser.services.selling;
            for (int i = 1; i <= numOfSelling; i++) {
                SellingService seller = new SellingService("Seller" + i);
                Thread thread = new Thread(seller);
                thread.start();
                threads.add(thread);
            }

            int numOfInventory = parser.services.inventoryService;
            for (int i = 1; i <= numOfInventory; i++) {
                InventoryService inv = new InventoryService("inv" + i);
                Thread thread = new Thread(inv);
                thread.start();
                threads.add(thread);
            }

            int numOfLogistic = parser.services.logistics;
            for (int i = 1; i <= numOfLogistic; i++) {
                LogisticsService log = new LogisticsService("log" + i);
                Thread thread = new Thread(log);
                thread.start();
                threads.add(thread);
            }

            int numOfResources = parser.services.resourcesService;
            for (int i = 1; i <= numOfResources; i++) {
                ResourceService res = new ResourceService("res" + i);
                Thread thread = new Thread(res);
                thread.start();
                threads.add(thread);
            }

            for (int i = 0; i < parser.services.customers.length; i++) {
                APIService api = new APIService("api" + i, parser.services.customers[i]);
                Thread thread = new Thread(api);
                thread.start();
                threads.add(thread);
            }

            TimeService timeService = new TimeService(parser.services.time.speed, parser.services.time.duration);
            Thread time = new Thread(timeService);
            time.start();
            threads.add(time);

            for (Thread t : threads) {
                t.join();
            }

            HashMap<Integer, Customer> customerList = new HashMap<>();
            for (int i = 0; i < parser.services.customers.length; i++) {
                Integer id = parser.services.customers[i].getId();
                customerList.put(id, parser.services.customers[i]);
            }
            try {
                File fileOne = new File(args[1]);
                FileOutputStream fos = new FileOutputStream(fileOne);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(customerList);
                oos.flush();
                oos.close();
                fos.close();
            } catch (Exception e) {
            }

            inventory.printInventoryToFile(args[2]);

            MoneyRegister moneyRegister = MoneyRegister.getInstance();
            moneyRegister.printOrderReceipts(args[3]);

            try {
                File fileOne = new File(args[4]);
                FileOutputStream fos = new FileOutputStream(fileOne);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(moneyRegister);
                oos.flush();
                oos.close();
                fos.close();
            } catch (Exception e) {
            }
        }
        catch (Exception e) {}
    }
}
