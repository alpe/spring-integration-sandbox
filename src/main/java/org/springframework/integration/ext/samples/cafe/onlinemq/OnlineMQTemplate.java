package org.springframework.integration.ext.samples.cafe.onlinemq;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import omq.api.OMQConnection;
import omq.api.OMQConnectionFactory;
import omq.api.OMQMessage;
import omq.api.OMQQueue;
import omq.api.OMQQueueManager;
import omq.api.OMQSession;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

/**
 * Template style onlineMQ.com access.
 * 
 * @author Alex Peters
 */
public class OnlineMQTemplate {

	private static final Logger log = Logger.getLogger(OnlineMQTemplate.class);

	private String userLogin;

	private String password;

	private String queueManager;

	private boolean transacted = true;

	/**
	 * @param userLogin the userLogin to set
	 */
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param queueManager the queueManager to set
	 */
	public void setQueueManager(String queueManager) {
		this.queueManager = queueManager;
	}

	/**
	 * @param transacted the transacted to set
	 */
	public void setTransacted(boolean transacted) {
		this.transacted = transacted;
	}

	/**
	 * @return
	 * @throws ServiceException
	 */
	OMQConnection openConnection() throws ServiceException {
		return OMQConnectionFactory.getConnection(userLogin, password, new OMQQueueManager(
				queueManager));
	}

	/**
	 * @param payload
	 * @param queueName
	 * @return
	 * @throws OnlineMQException
	 */
	public int send(final String queueName, Object payload) throws OnlineMQException {
		try {
			return send(queueName, new OMQMessage(payload));
		}
		catch (IOException e) {
			throw new OnlineMQException("Failed to create message for payload: " + payload);
		}
	}

	/**
	 * @param queueName
	 * @param msgToSent
	 * @return
	 * @throws OnlineMQException
	 */
	int send(final String queueName, final OMQMessage msgToSent) throws OnlineMQException {
		Assert.hasLength(queueName, "Queue must not be empty");
		return execute(new SessionCallback<Integer>() {
			@Override
			public Integer doInSession(OMQSession session) throws OnlineMQException {
				try {
					return session.sendMessage(createQueue(queueName), msgToSent);
				}
				catch (RemoteException e) {
					throw new OnlineMQException("Failed to send message to queue: " + queueName, e);
				}
			}

		});
	}

	/**
	 * @param queueName
	 * @return
	 */
	OMQQueue createQueue(final String queueName) {
		return new OMQQueue(queueName);
	}

	/**
	 * @param queueName
	 * @return
	 * @throws OnlineMQException
	 */
	public OMQMessage recieve(final String queueName) throws OnlineMQException {
		Assert.hasLength(queueName, "Queue must not be empty");
		return execute(new SessionCallback<OMQMessage>() {
			@Override
			public OMQMessage doInSession(OMQSession session) throws OnlineMQException {
				try {
					OMQMessage receiveMessage = session.receiveMessage(createQueue(queueName));
					int rc = session.getLastErrorCode();
					if (rc == 0) {
						return receiveMessage;
					}
					return null;
				}
				catch (Exception e) {
					throw new OnlineMQException("Failed to receive message from queue: "
							+ queueName, e);
				}
			}
		});
	}

	/**
	 * @param <T>
	 * @param action
	 * @return
	 */
	public <T> T execute(SessionCallback<T> action) throws OnlineMQException {
		return execute(action, transacted);
	}

	/**
	 * @param <T>
	 * @param action
	 * @param inTransaction
	 * @return
	 * @throws OnlineMQException
	 */
	public <T> T execute(SessionCallback<T> action, boolean inTransaction) throws OnlineMQException {
		Assert.notNull(action, "Callback object must not be null");
		OMQConnection conToClose = null;
		OMQSession sessionToClose = null;

		try {
			conToClose = openConnection();
			sessionToClose = conToClose.createSession(inTransaction);
			return action.doInSession(sessionToClose);
		}
		catch (ServiceException e) {
			throw new IllegalStateException("Failed to open connection: " + e.getMessage());
		}
		finally {
			// close connection + session but how?
		}
	}

	/**
	 * @author Alex Peters
	 * 
	 * @param <T>
	 */
	public static interface SessionCallback<T> {

		public T doInSession(OMQSession session) throws OnlineMQException;
	}
}
