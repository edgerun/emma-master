package at.ac.tuwien.dsg.emma.bridge;

import java.io.IOException;
import java.util.Scanner;

/**
 * XenqttBridgeClientTest.
 */
public class XenqttBridgeClientTest {

    /**
     * FIXME remove this, this is a test to connect two local brokers
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        XenqttBridgeClient bridgeClient1 = new XenqttBridgeClient("c1883", "tcp://localhost:1883");
        XenqttBridgeClient bridgeClient2 = new XenqttBridgeClient("c1884", "tcp://localhost:1884");

        Thread bridgeClient1Thread = new Thread(bridgeClient1);
        Thread bridgeClient2Thread = new Thread(bridgeClient2);

        bridgeClient1Thread.start();
        bridgeClient2Thread.start();

        Thread.sleep(1000);
        bridgeClient1.connect("tcp://localhost:1884", "testTopic");
        bridgeClient2.connect("tcp://localhost:1883", "testTopic");

        System.out.println("Press enter to exit");
        new Scanner(System.in).nextLine();

        System.out.println("Closing BC1");
        bridgeClient1.close();
        System.out.println("Closing BC2");
        bridgeClient2.close();
        System.out.println("Waiting for BC1 thread to end");
        bridgeClient1Thread.join();
        System.out.println("Waiting for BC2 thread to end");
        bridgeClient2Thread.join();

        System.out.println("Done, bye!");
    }
}