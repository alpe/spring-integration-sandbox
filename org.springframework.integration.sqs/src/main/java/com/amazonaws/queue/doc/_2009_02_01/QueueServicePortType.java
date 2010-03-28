
package com.amazonaws.queue.doc._2009_02_01;

import java.math.BigInteger;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "QueueServicePortType", targetNamespace = "http://queue.amazonaws.com/doc/2009-02-01/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface QueueServicePortType {


    /**
     *  
     * The CreateQueue action creates a new queue, or returns the URL of an existing one.  When you request CreateQueue, you provide a name for the queue. To successfully create a new queue, you must provide a name that is unique within the scope of your own queues. If you provide the name of an existing queue, a new queue isn't created and an error isn't returned. Instead, the request succeeds and the queue URL for the existing queue is returned. Exception: if you provide a value for DefaultVisibilityTimeout that is different from the value for the existing queue, you receive an error.      
     * 
     * @param createQueueResult
     * @param attribute
     * @param queueName
     * @param responseMetadata
     * @param defaultVisibilityTimeout
     */
    @WebMethod(operationName = "CreateQueue", action = "CreateQueue")
    @RequestWrapper(localName = "CreateQueue", targetNamespace = "http://queue.amazonaws.com/doc/2009-02-01/", className = "com.amazonaws.queue.doc._2009_02_01.CreateQueue")
    @ResponseWrapper(localName = "CreateQueueResponse", targetNamespace = "http://queue.amazonaws.com/doc/2009-02-01/", className = "com.amazonaws.queue.doc._2009_02_01.CreateQueueResponse")
    public void createQueue(
        @WebParam(name = "QueueName", targetNamespace = "http://queue.amazonaws.com/doc/2009-02-01/")
        String queueName,
        @WebParam(name = "DefaultVisibilityTimeout", targetNamespace = "http://queue.amazonaws.com/doc/2009-02-01/")
        BigInteger defaultVisibilityTimeout,
        @WebParam(name = "Attribute", targetNamespace = "http://queue.amazonaws.com/doc/2009-02-01/")
        List<Attribute> attribute,
        @WebParam(name = "CreateQueueResult", targetNamespace = "http://queue.amazonaws.com/doc/2009-02-01/", mode = WebParam.Mode.OUT)
        Holder<CreateQueueResult> createQueueResult,
        @WebParam(name = "ResponseMetadata", targetNamespace = "http://queue.amazonaws.com/doc/2009-02-01/", mode = WebParam.Mode.OUT)
        Holder<ResponseMetadata> responseMetadata);

    /**
     *  
     * The ListQueues action returns a list of your queues.
     *       
     * 
     * @param listQueuesResult
     * @param attribute
     * @param responseMetadata
     * @param queueNamePrefix
     */
    @WebMethod(operationName = "ListQueues", action = "ListQueues")
    @RequestWrapper(localName = "ListQueues", targetNamespace = "http://queue.amazonaws.com/doc/2009-02-01/", className = "com.amazonaws.queue.doc._2009_02_01.ListQueues")
    @ResponseWrapper(localName = "ListQueuesResponse", targetNamespace = "http://queue.amazonaws.com/doc/2009-02-01/", className = "com.amazonaws.queue.doc._2009_02_01.ListQueuesResponse")
    public void listQueues(
        @WebParam(name = "QueueNamePrefix", targetNamespace = "http://queue.amazonaws.com/doc/2009-02-01/")
        String queueNamePrefix,
        @WebParam(name = "Attribute", targetNamespace = "http://queue.amazonaws.com/doc/2009-02-01/")
        List<Attribute> attribute,
        @WebParam(name = "ListQueuesResult", targetNamespace = "http://queue.amazonaws.com/doc/2009-02-01/", mode = WebParam.Mode.OUT)
        Holder<ListQueuesResult> listQueuesResult,
        @WebParam(name = "ResponseMetadata", targetNamespace = "http://queue.amazonaws.com/doc/2009-02-01/", mode = WebParam.Mode.OUT)
        Holder<ResponseMetadata> responseMetadata);

}
