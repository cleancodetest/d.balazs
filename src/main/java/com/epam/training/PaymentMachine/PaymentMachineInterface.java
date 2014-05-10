package com.epam.training.PaymentMachine;

public interface PaymentMachineInterface {

	public boolean hasAnyAvailableCoins();
	
	public boolean hasOngoingPayment();
	
	public boolean isTicketAlreadyExists(Ticket ticket);

	public boolean isActualPaymentCoinsEnough();	

	
	public long getStorageAmount(); 

	public Ticket getActualTicket();
	
	
	public void startPayment(Ticket ticket) throws Exception;

	public void cancelPayment();

	public void closePayment();

	
	public long getPayedAmount();

	public long getChangedAmount();
	
	public long calculateChangeAmount();

	public void generateChangeCoins() throws Exception;
	
	public void addCoin(Coin coin);
	
	public Coin getCoinByValue(String coin) throws Exception;
	

	public void printAvailableCoins();

	public void printPayedCoins();
	
	public void printChangedCoins();

	public void printTicketPaymentInformations();
	
	public void printTicketPaymentInformations(Ticket ticket); 
	
}
