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

package org.springframework.integration.ext.samples.cafe;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mark Fisher
 * @author Marius Bogoevici
 */
public class DrinkOrder {

    private final List<OrderItem> orderItems = new ArrayList<OrderItem>();

    private final int number;

    private final long dateCreated;

    public DrinkOrder(int number) {
	this.number = number;
	this.dateCreated = System.currentTimeMillis();
    }

    public long getDateCreated() {
	return dateCreated;
    }

    public void addItem(DrinkType drinkType, int shots, boolean iced) {
	this.orderItems.add(new OrderItem(this, drinkType, shots, iced));
    }

    public int getNumber() {
	return number;
    }

    public List<OrderItem> getItems() {
	return this.orderItems;
    }

}
