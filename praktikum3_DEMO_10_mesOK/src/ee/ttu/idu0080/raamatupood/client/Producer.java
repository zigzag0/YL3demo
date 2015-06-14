package ee.ttu.idu0080.raamatupood.client;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import ee.ttu.idu0080.raamatupood.server.EmbeddedBroker;
import ee.ttu.idu0080.raamatupood.types.Car;

/**
 * JMS sĆµnumite tootja. Ć�hendub brokeri url-ile
 *
 * @author Allar Tammik
 * @date 08.03.2010
 */
public class Producer {
	private static final Logger log = Logger.getLogger(Producer.class);
	public static final String SUBJECT = "Tekstide.saatmine"; // järjekorra nimi

	private String user = ActiveMQConnection.DEFAULT_USER;// brokeri jaoks vaja
	private String password = ActiveMQConnection.DEFAULT_PASSWORD;

	long sleepTime = 1000; // 1000ms

	private int messageCount = 10;
	private long timeToLive = 1000000;
	private String url = EmbeddedBroker.URL;	// JMS serveriasukoht

	public static void main(String[] args) {
		Producer producerTool = new Producer();
		producerTool.run();		// Sõnumi saatmine
	}

	public void run() {
		Connection connection = null;
		try {
			log.info("Connecting to URL: " + url);
			log.debug("Sleeping between publish " + sleepTime + " ms");
			if (timeToLive != 0) {
				log.debug("Messages time to live " + timeToLive + " ms");
			}

			// 1. Loome Ć¼henduse
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					user, password, url);
			connection = connectionFactory.createConnection();
			// KĆ¤ivitame yhenduse
			connection.start();

			// 2. Loome sessiooni
			/*
			 * createSession vĆµtab 2 argumenti: 1. kas saame kasutada
			 * transaktsioone 2. automaatne kinnitamine
			 */
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			// Loome teadete sihtkoha (jĆ¤rjekorra). Parameetriks jĆ¤rjekorra nimi
			Destination destination = session.createQueue(SUBJECT);

			// 3. Loome teadete saatja
			MessageProducer producer = session.createProducer(destination);

			// producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			producer.setTimeToLive(timeToLive);

			// 4. teadete saatmine
			sendLoop(session, producer);
			/*
			TextMessage message = session.createTextMessage("Tere SUVII!");
			log.debug("Saadan sõnumi: " + message.getText());
			producer.send(message);
			*/

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	protected void sendLoop(Session session, MessageProducer producer)
			throws Exception {

		for (int i = 0; i < messageCount || messageCount == 0; i++) {
			ObjectMessage objectMessage = session.createObjectMessage();
			objectMessage.setObject(new Car(5)); // peab olema Serializable
			producer.send(objectMessage);

			TextMessage message = session
					.createTextMessage(createMessageText(i));
			log.debug("Sending message: " + message.getText());
			producer.send(message);

			// ootab 1 sekundi
			Thread.sleep(sleepTime);
		}
	}

	private String createMessageText(int index) {
		return "Message: " + index + " sent at: " + (new Date()).toString();
	}
}


