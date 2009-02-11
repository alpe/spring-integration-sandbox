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

/**
 * @author Marius Bogoevici
 */
public class Drink {

    private boolean iced;

    private int shots;

    private DrinkType drinkType;

    private int orderNumber;

    public Drink(int orderNumber, DrinkType drinkType, boolean hot, int shots) {
	this.orderNumber = orderNumber;
	this.drinkType = drinkType;
	this.iced = hot;
	this.shots = shots;
    }

    public int getOrderNumber() {
	return orderNumber;
    }

    @Override
    public String toString() {
	return (iced ? "Iced" : "Hot") + " " + drinkType.toString() + ", "
		+ shots + " shots.";
    }

}
