package ee.ttu.idu0080.raamatupood.server;

import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;

/**
 * Broker - vahendaja. Brokeri külge ühenduvad sõnumi saatjad ja vastuvõtjad. 
 * 
 * @author Allar Tammik
 * @date 08.03.2010
 */
public final class EmbeddedBroker {
    private static final Logger log = Logger.getLogger(EmbeddedBroker.class);
    public static final String PORT = "61618";
    public static final String URL = "tcp://localhost:" + PORT;

    private EmbeddedBroker() {
    }

    public static void main(String[] args) throws Exception {
        BrokerService broker = new BrokerService();
        // Lets set JMS name
        broker.setBrokerName("JMS_BROKER");
        broker.addConnector(URL);
        broker.start();
        log.info("Start JMS Broker on " + URL);
        
        // now lets wait forever to avoid the JVM terminating immediately
        Object lock = new Object();
        // synchronize so the only one thread can lock object.
        synchronized (lock) {
            //wait for ever
            lock.wait();
        }
    }
}
