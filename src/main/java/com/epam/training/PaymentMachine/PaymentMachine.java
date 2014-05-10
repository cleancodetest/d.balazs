package com.epam.training.PaymentMachine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PaymentMachine implements PaymentMachineInterface {

	private Set<Ticket> tickets = new HashSet<Ticket>();
	private Ticket actualTicket = null;
	private Map<Coin, Long> moneyStorage = new HashMap<Coin, Long>();
	private Map<Coin, Long> bufferStorage = new HashMap<Coin, Long>();
	private Map<Coin, Long> changeStorage = new HashMap<Coin, Long>();
	
	public PaymentMachine() throws Exception {
		generateInitialMoneyStorage(19995);
	}
	
	public PaymentMachine(long initialAmount) throws Exception {
		generateInitialMoneyStorage(initialAmount);
	}
	
	public boolean hasAnyAvailableCoins() {
		boolean result = false;
		
		for (Map.Entry<Coin, Long> entry : moneyStorage.entrySet()) {
			if (entry.getValue() > 0) {
				result = true;
			}
		}
		
		return result;
	}

	public boolean hasOngoingPayment() {
		return (actualTicket == null) ? false : true;
	}

	public boolean isTicketAlreadyExists(Ticket ticket) {
		return tickets.contains(ticket);
	}

	public boolean isActualPaymentCoinsEnough() {
		return (this.calculateChangeAmount() >= 0) ? true : false;
	}
	

	public long getStorageAmount() {
		long storedAmount = 0;

		if (!moneyStorage.isEmpty()) {
			for (Map.Entry<Coin, Long> entry : moneyStorage.entrySet()) {
				if (entry.getValue() > 0) {
					long amount = entry.getKey().getValue() * entry.getValue();
					storedAmount += amount;
				}
			}
		}
		
		return storedAmount;
	}

	public Ticket getActualTicket() {
		return this.actualTicket;
	}

	
	public void startPayment(Ticket ticket) throws Exception {
		if (!isTicketAlreadyExists(ticket)) {
			this.actualTicket = ticket;
		} else {
			throw new TicketAlreadyExistException();
		}
	}

	public void cancelPayment() {
		if (hasOngoingPayment()) {
			clearTemporaryData();
		}
	}

	public void closePayment() {
		if (hasOngoingPayment()) {
			Ticket ticket = actualTicket;
			
			ticket.setChange(calculateChangeAmount());
			ticket.pay();

			tickets.add(ticket);

			movePayedCoinsToMoneyStorage();
			clearTemporaryData();
		}
	}


	public long getPayedAmount() {
		long payedAmount = 0;
		
		for (Map.Entry<Coin, Long> entry : bufferStorage.entrySet()) {
			if (entry.getValue() > 0) {
				long amount = entry.getKey().getValue() * entry.getValue();
				payedAmount += amount;
			}
		}
		
		return payedAmount;
	}

	public long getChangedAmount() {
		long changedAmount = 0;
		
		for (Map.Entry<Coin, Long> entry : changeStorage.entrySet()) {
			if (entry.getValue() > 0) {
				long amount = entry.getKey().getValue() * entry.getValue();
				changedAmount += amount;
			}
		}
		
		return changedAmount;
	}
	
	public long calculateChangeAmount() {
		return this.getPayedAmount() - this.getActualTicket().getAmount();
	}

	public void generateChangeCoins() throws Exception {
		long changeAmount = this.calculateChangeAmount();

		if (!changeStorage.isEmpty()) {
			throw new ChangeStorageIsNotEmptyException();
		}
		
		if ((changeAmount % Coin.MIN_VALUE.getValue()) != 0) {
			throw new InvalidAmountException();
		}
		
		long amount = changeAmount;
		
		while (getChangedAmount() < changeAmount) {
			for (Coin coin : Coin.values()) {
				if (amount - coin.getValue() >= 0) {
					amount = amount - coin.getValue();
					Long pieces = changeStorage.get(coin);
					changeStorage.put(coin, pieces == null ? 1 : ++pieces);
					removeCoinFromMoneyStorage(coin);
				}
			}
		}
	}

	public void addCoin(Coin coin) {
		long newPieces = 0;

		Long entryPieces = bufferStorage.get(coin); 
		
		if (entryPieces == null) {
			newPieces = 1;
			bufferStorage.put(coin, newPieces);
		} else {
			newPieces = entryPieces + 1;
			bufferStorage.put(coin, newPieces);
		}
	}

	public Coin getCoinByValue(String value) throws Exception {
		Coin coin = null;
		
		try {
			coin = Coin.valueOf("C" + value);
		} catch (Exception e) {
			if (coin == null) {
				throw new InvalidCoinException();
			}
		}
		
		return coin;
	}

	
	public void printAvailableCoins() {
		StringBuilder sb = new StringBuilder();

		if (moneyStorage.isEmpty()) {
			System.out.println("*** Available coins: - ***");
		} else {
			for (Map.Entry<Coin, Long> entry : moneyStorage.entrySet()) {
				sb.append(entry.getKey() + "(" + entry.getValue() + "), ");
			}
			
			System.out.println("*** Available coins: " + sb.substring(0, sb.length() - 2) + " ***");
		}
	}

	public void printPayedCoins() {
		StringBuilder sb = new StringBuilder();

		if (bufferStorage.isEmpty()) {
			System.out.println("*** Payed coins: - ***");
		} else {
			for (Map.Entry<Coin, Long> entry : bufferStorage.entrySet()) {
				sb.append(entry.getKey() + "(" + entry.getValue() + "), ");
			}
			
			System.out.println("*** Payed coins: " + sb.substring(0, sb.length() - 2) + " ***");
		}
	}

	public void printChangedCoins() {
		StringBuilder sb = new StringBuilder();

		if (changeStorage.isEmpty()) {
			System.out.println("*** Changed coins: - ***");
		} else {
			for (Map.Entry<Coin, Long> entry : changeStorage.entrySet()) {
				sb.append(entry.getKey() + "(" + entry.getValue() + "), ");
			}
			
			System.out.println("*** Changed coins: " + sb.substring(0, sb.length() - 2) + " ***");
		}
	}

	public void printTicketPaymentInformations() {
		if (!hasOngoingPayment()) {
			System.out.println("*** Ticket not available ***");
		} else {
			printTicketPaymentInformations(getActualTicket());
		}
	}

	public void printTicketPaymentInformations(Ticket ticket) {
		if (ticket == null) {
			System.out.println("*** Ticket not available ***");
		} else {
			System.out.println("*** Ticket: #" + ticket.getTicketNumber() + " (" + ticket.getTicketDate() + " - " + ticket.getAmount() + " Ft ***");
		}
	}
	
	
	private void movePayedCoinsToMoneyStorage() {
		for (Map.Entry<Coin, Long> entry : bufferStorage.entrySet()) {
			addCoinsToMoneyStorage(entry.getKey(), entry.getValue());
		}
		
		bufferStorage.clear();
	}
	
	private void addCoinsToMoneyStorage(Coin coin, Long pieces) {
		moneyStorage.put(coin, pieces == null ? 1 : pieces);
	}
	
	private void removeCoinFromMoneyStorage(Coin coin) {
		long newPieces = 0;
		
		for (Map.Entry<Coin, Long> entry : moneyStorage.entrySet()) {
			if (entry.getKey().equals(coin)) {
				Long entryPieces = entry.getValue();
				newPieces = --entryPieces;  
			}
		}

		if (newPieces >= 0) {
			bufferStorage.remove(coin);
			if (newPieces  > 0) {
				bufferStorage.put(coin, newPieces);
			}
		}
	}
	
	private void clearTemporaryData() {
		bufferStorage.clear();
		changeStorage.clear();
		actualTicket = null;
	}
	
	private void generateInitialMoneyStorage(long initialAmount) throws Exception {
		if ((initialAmount % Coin.MIN_VALUE.getValue()) != 0) {
			throw new InvalidAmountException();
		}
		
		long amount = initialAmount;
		
		while (getStorageAmount() < initialAmount) {
			for (Coin coin : Coin.values()) {
				if (amount - coin.getValue() >= 0) {
					amount = amount - coin.getValue();
					Long pieces = moneyStorage.get(coin);
					addCoinsToMoneyStorage(coin, pieces == null ? 1 : ++pieces);
				}
			}
		}
	}




}
