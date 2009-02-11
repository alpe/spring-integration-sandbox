package org.springframework.integration.ext.samples.cafe.jmx;

import javax.management.MBeanServer;
import javax.management.modelmbean.ModelMBean;

import org.apache.log4j.Logger;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.jmx.export.SpringModelMBean;
import org.springframework.jmx.export.assembler.MBeanInfoAssembler;
import org.springframework.jmx.export.assembler.SimpleReflectiveMBeanInfoAssembler;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.util.Assert;

/**
 * 
 * @author ap
 * 
 */
public class JmxToAllBeansPostprocessor implements BeanPostProcessor,
	InitializingBean {

    private static final Logger log = Logger
	    .getLogger(JmxToAllBeansPostprocessor.class);

    @Autowired
    private MBeanServer mbeanServer;

    private ObjectNamingStrategy naming;

    private MBeanInfoAssembler assembler;

    /**
     * @param mbeanServer
     *            the mbeanServer to set
     */
    public void setMbeanServer(MBeanServer mbeanServer) {
	this.mbeanServer = mbeanServer;
    }

    /**
     * @param naming
     *            the naming to set
     */
    public void setNaming(ObjectNamingStrategy naming) {
	this.naming = naming;
    }

    /**
     * @param assembler
     *            the assembler to set
     */
    public void setAssembler(MBeanInfoAssembler assembler) {
	this.assembler = assembler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
	Assert.notNull(mbeanServer);
	if (naming == null) {
	    naming = new IdentityNamingStrategy();
	}
	if (assembler == null) {
	    assembler = new SimpleReflectiveMBeanInfoAssembler();
	}
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName)
	    throws BeansException {
	return bean;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Object postProcessAfterInitialization(Object bean, String beanName)
	    throws BeansException {
	ModelMBean mbean = createMBean(bean, beanName);
	if (mbean != null) {
	    try {
		mbeanServer.registerMBean(mbean, naming.getObjectName(bean,
			beanName));
		log.debug("Added jmx support for bean: " + beanName);
	    } catch (Exception e) {
		log.error("Failed to register jmx mbean: " + mbean, e);
	    }
	}
	return bean;
    }

    /**
     * Create a mbean for given bean.
     * 
     * @param bean
     * @param beanName
     * @return mbean instance
     */
    ModelMBean createMBean(Object bean, String beanName) {
	Assert.notNull(bean);
	Assert.notNull(beanName);
	Assert.notNull(assembler);
	try {
	    if (AopUtils.getTargetClass(bean)==null){
		// Would fail 
		log.debug(String
			.format("Skipping jmx support for gateway bean: %s. ",
				beanName));
		return null;
	    }
	    ModelMBean mbean = new SpringModelMBean();
	    mbean.setModelMBeanInfo(assembler.getMBeanInfo(bean, beanName));
	    mbean.setManagedResource(bean, "ObjectReference");
	    return mbean;
	} catch (Exception e) {
	    log.error("Failed to create jmx mbean for: " + bean, e);
	}
	return null;
    }

}
