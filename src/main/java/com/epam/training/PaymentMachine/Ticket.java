package com.epam.training.PaymentMachine;

import java.util.Date;
import java.util.Random;

public class Ticket {
	
	private int ticketNumber = 0;
	private Date ticketDate;
	private long amount = 0;
	private long change = 0;
	private boolean payed = false;

	
	public Ticket(int ticketNumber) {
		this.ticketNumber = ticketNumber;
		this.ticketDate = new Date();

		// Ticket amount
		Random rand = new Random();
		int amount = (rand.nextInt(10) + 1) * 50;
		
		this.amount = amount;
	}

	
	public int getTicketNumber() {
		return ticketNumber;
	}

	public Date getTicketDate() {
		return ticketDate;
	}
	
	public long getAmount() {
		return amount;
	}

	public long getChange() {
		return change;
	}
	
	public void setChange(long change) {
		this.change = change;
	}
	
	public boolean isPayed() {
		return payed;
	}

	public void pay() {
		this.payed = true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ticketNumber;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ticket other = (Ticket) obj;
		if (ticketNumber != other.ticketNumber)
			return false;
		return true;
	}
	
}
