package ee.ttu.idu0080.raamatupood.client;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import ee.ttu.idu0080.raamatupood.server.EmbeddedBroker;

/**
 * JMS sĆµnumite tarbija. Ć�hendub broker-i urlile
 * 
 * @author Allar Tammik
 * @date 08.03.2010
 */
public class Consumer {
	private static final Logger log = Logger.getLogger(Consumer.class);
	private String SUBJECT = "Tekstide.saatmine";
	private String user = ActiveMQConnection.DEFAULT_USER;
	private String password = ActiveMQConnection.DEFAULT_PASSWORD;
	private String url = EmbeddedBroker.URL;

	public static void main(String[] args) {
		Consumer consumerTool = new Consumer();
		consumerTool.run();
	}

	public void run() {
		Connection connection = null;
		try {
			log.info("Connecting to URL: " + url);
			log.info("Consuming queue : " + SUBJECT);

			// 1. Loome Ć¼henduse
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					user, password, url);
			connection = connectionFactory.createConnection();

			// Kui Ć¼hendus kaob, lĆµpetatakse Consumeri tĆ¶Ć¶ veateatega.
			connection.setExceptionListener(new ExceptionListenerImpl());

			// KĆ¤ivitame Ć¼henduse
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

			// 3. Teadete vastuvĆµtja
			MessageConsumer consumer = session.createConsumer(destination);

			// Kui teade vastu vĆµetakse kĆ¤ivitatakse onMessage()
			consumer.setMessageListener(new MessageListenerImpl());

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	/**
	 * KĆ¤ivitatakse, kui tuleb sĆµnum
	 */
	class MessageListenerImpl implements javax.jms.MessageListener {

		public void onMessage(Message message) {	//sõnumi töötlus
			try {
				if (message instanceof TextMessage) {
					TextMessage txtMsg = (TextMessage) message;
					String msg = txtMsg.getText();
					log.info("Received: " + msg);
				} else if (message instanceof ObjectMessage) {
					ObjectMessage objectMessage = (ObjectMessage) message;
					String msg = objectMessage.getObject().toString();
					log.info("Received: " + msg);

				} else {
					log.info("Received: " + message);
				}

			} catch (JMSException e) {
				log.warn("Caught: " + e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * KĆ¤ivitatakse, kui tuleb viga.
	 */
	class ExceptionListenerImpl implements javax.jms.ExceptionListener {

		public synchronized void onException(JMSException ex) {
			log.error("JMS Exception occured. Shutting down client.");
			ex.printStackTrace();
		}
	}

}