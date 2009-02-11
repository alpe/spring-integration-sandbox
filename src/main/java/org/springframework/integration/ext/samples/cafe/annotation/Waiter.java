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

import java.util.List;

import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.ext.samples.cafe.Delivery;
import org.springframework.integration.ext.samples.cafe.Drink;

/**
 * @author Marius Bogoevici
 */
@MessageEndpoint
public class Waiter {

    @Aggregator(inputChannel = "preparedDrinks", outputChannel = "deliveries", timeout = 5 * 60 * 1000)
    public Delivery prepareDelivery(List<Drink> drinks) {
	return new Delivery(drinks);
    }

    // ap @CompletionStrategy
    // public boolean completionChecker(List<Drink> drinks) {
    // return drinks.size()==2;
    // }

}
