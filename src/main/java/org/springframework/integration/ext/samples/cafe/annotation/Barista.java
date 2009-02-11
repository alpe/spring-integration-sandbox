/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.ext.samples.cafe.annotation;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ext.samples.cafe.Drink;
import org.springframework.integration.ext.samples.cafe.OrderItem;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

/**
 * @author Mark Fisher
 * @author Marius Bogoevici
 */
@ManagedResource(description = "Barista, creating Drinks", objectName = "demo:cafe=Barista")
@Component()
public class Barista {

    private long hotDrinkDelay = 5000;

    private long coldDrinkDelay = 1000;

    private final AtomicInteger hotDrinkCounter = new AtomicInteger();

    private final AtomicInteger coldDrinkCounter = new AtomicInteger();

    /**
     * @return the hotDrinkDelay
     */
    @ManagedAttribute()
    public long getHotDrinkDelay() {
	return hotDrinkDelay;
    }

    /**
     * @return the coldDrinkDelay
     */
    @ManagedAttribute
    public long getColdDrinkDelay() {
	return coldDrinkDelay;
    }

    /**
     * @return the hotDrinkCounter
     */
    @ManagedAttribute
    public int getHotDrinkCounter() {
	return hotDrinkCounter.get();
    }

    /**
     * @return the coldDrinkCounter
     */
    @ManagedAttribute
    public int getColdDrinkCounter() {
	return coldDrinkCounter.get();
    }

    public void setHotDrinkDelay(long hotDrinkDelay) {
	this.hotDrinkDelay = hotDrinkDelay;
    }

    public void setColdDrinkDelay(long coldDrinkDelay) {
	this.coldDrinkDelay = coldDrinkDelay;
    }

    @ServiceActivator(inputChannel = "hotDrinkBarista", outputChannel = "preparedDrinks")
    public Drink prepareHotDrink(OrderItem orderItem) {
	try {
	    Thread.sleep(this.hotDrinkDelay);
	    System.out.println(Thread.currentThread().getName()
		    + " prepared hot drink #"
		    + hotDrinkCounter.incrementAndGet() + " for order #"
		    + orderItem.getOrder().getNumber() + ": " + orderItem);
	    return new Drink(orderItem.getOrder().getNumber(), orderItem
		    .getDrinkType(), orderItem.isIced(), orderItem.getShots());
	} catch (InterruptedException e) {
	    Thread.currentThread().interrupt();
	    return null;
	}
    }

    @ServiceActivator(inputChannel = "coldDrinkBarista", outputChannel = "preparedDrinks")
    public Drink prepareColdDrink(OrderItem orderItem) {
	try {
	    Thread.sleep(this.coldDrinkDelay);
	    System.out.println(Thread.currentThread().getName()
		    + " prepared cold drink #"
		    + coldDrinkCounter.incrementAndGet() + " for order #"
		    + orderItem.getOrder().getNumber() + ": " + orderItem);
	    return new Drink(orderItem.getOrder().getNumber(), orderItem
		    .getDrinkType(), orderItem.isIced(), orderItem.getShots());
	} catch (InterruptedException e) {
	    Thread.currentThread().interrupt();
	    return null;
	}
    }

}
