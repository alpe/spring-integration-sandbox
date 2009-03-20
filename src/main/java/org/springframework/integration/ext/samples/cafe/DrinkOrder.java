package org.springframework.integration.ext.samples.cafe;

import java.util.List;

import org.springframework.integration.samples.cafe.DrinkType;
import org.springframework.integration.samples.cafe.Order;
import org.springframework.integration.samples.cafe.OrderItem;

/**
 * see link {@link Order}
 * 
 * @author ap
 */
public class DrinkOrder extends org.springframework.integration.samples.cafe.Order {

	private final long dateCreated;

	public DrinkOrder(int number) {
		super(number);
		this.dateCreated = System.currentTimeMillis();
	}

	public long getDateCreated() {
		return dateCreated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addItem(DrinkType drinkType, int shots, boolean iced) {
		// TODO Auto-generated method stub
		super.addItem(drinkType, shots, iced);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OrderItem> getItems() {
		// TODO Auto-generated method stub
		return super.getItems();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumber() {
		// TODO Auto-generated method stub
		return super.getNumber();
	}
	
	

}
