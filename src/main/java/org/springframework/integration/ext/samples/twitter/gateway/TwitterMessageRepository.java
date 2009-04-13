package org.springframework.integration.ext.samples.twitter.gateway;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alex Peters
 * 
 */
@Repository
public class TwitterMessageRepository {

	private static final Logger log = Logger.getLogger(TwitterMessageRepository.class);

	@Autowired
	private HibernateTemplate template;

	@Transactional(propagation = Propagation.SUPPORTS)
	public IncomingDirectMessage findIncomingMessage(long twitterMessageId) {
		List<?> resultList = template.findByNamedQuery(
				IncomingDirectMessage.QUERY_FIND_BY_TWITTER_ID, twitterMessageId);
		if (resultList.isEmpty()) {
			return null;
		}
		return (IncomingDirectMessage) resultList.get(0);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public long store(IncomingDirectMessage message) {
		log.debug("Persisting: " + message);
		return (Long) template.save(message);
	}
}
