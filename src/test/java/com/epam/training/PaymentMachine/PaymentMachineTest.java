package com.epam.training.PaymentMachine;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PaymentMachineTest {
	
	private PaymentMachine paymentMachine;
	
	@BeforeMethod
	public void setup() throws Exception {
		paymentMachine = new PaymentMachine(21125);
	}
	
	@Test
	public void testMachineHasAvailableCoins() {
		Assert.assertEquals(paymentMachine.hasAnyAvailableCoins(), true);
	}

	@Test(expectedExceptions = InvalidAmountException.class)
	public void testInvalidInitialAmount() throws Exception {
		paymentMachine = new PaymentMachine(111);
	}
	
	@Test
	public void testInitialStoredAmount() {
		Assert.assertEquals(paymentMachine.getStorageAmount(), 21125);
	}

	@Test
	public void testHasNoOngoingPayment() {
		Assert.assertEquals(paymentMachine.hasOngoingPayment(), false);
	}
	
	@Test
	public void testHasOngoingPayment() throws Exception {
		Ticket ticket = new Ticket(1);

		paymentMachine.startPayment(ticket);
		
		Assert.assertEquals(paymentMachine.hasOngoingPayment(), true);
	}
	
	@Test(expectedExceptions = TicketAlreadyExistException.class)
	public void testIsTicketAlreadyExists() throws Exception {
		Ticket ticket = new Ticket(1);
		
		paymentMachine.startPayment(ticket);
		paymentMachine.closePayment();
		
		paymentMachine.startPayment(ticket);
	}

	@Test
	public void testStartPayment() throws Exception {
		Ticket ticket = new Ticket(1);
		
		paymentMachine.startPayment(ticket);
		
		Assert.assertEquals(paymentMachine.hasOngoingPayment(), true);
		Assert.assertEquals(paymentMachine.getActualTicket(), ticket);
	}

	@Test
	public void testGetActualPaymentTicket() throws Exception {
		Ticket ticket = new Ticket(1);
		
		paymentMachine.startPayment(ticket);

		Assert.assertEquals(paymentMachine.getActualTicket().getTicketNumber(), 1);
	}
	
	@Test
	public void testValidCoinValue() throws Exception {
		Assert.assertEquals(paymentMachine.getCoinByValue("1000"), Coin.C1000);
	}

	@Test(expectedExceptions = InvalidCoinException.class)
	public void testInvalidCoinValue() throws Exception {
		Assert.assertEquals(paymentMachine.getCoinByValue("1500"), null);
	}
	
	@Test
	public void testAddCoin() throws Exception {
		Ticket ticket = new Ticket(1);
		
		paymentMachine.startPayment(ticket);
		
		Assert.assertEquals(paymentMachine.getPayedAmount(), 0);
		
		paymentMachine.addCoin(Coin.C2000);
		paymentMachine.addCoin(Coin.C2000);
		paymentMachine.addCoin(Coin.C2000);
		paymentMachine.addCoin(Coin.C1000);

		Assert.assertEquals(paymentMachine.getPayedAmount(), 7000);
		
		paymentMachine.addCoin(Coin.C500);

		Assert.assertEquals(paymentMachine.getPayedAmount(), 7500);
	}
	
	@Test
	public void testCancelPayment() throws Exception {
		Ticket ticket = new Ticket(1);
		
		paymentMachine.startPayment(ticket);
		paymentMachine.addCoin(Coin.C1000);
		
		Assert.assertEquals(paymentMachine.getPayedAmount(), 1000);
		
		paymentMachine.cancelPayment();

		Assert.assertEquals(paymentMachine.getPayedAmount(), 0);
		Assert.assertEquals(paymentMachine.getChangedAmount(), 0);
	}

	@Test
	public void testClosePayment() throws Exception {
		Ticket ticket = new Ticket(1);
		
		paymentMachine.startPayment(ticket);
		
		paymentMachine.addCoin(Coin.C1000);
		paymentMachine.addCoin(Coin.C1000);
		paymentMachine.addCoin(Coin.C500);

		Assert.assertEquals(paymentMachine.getPayedAmount(), 2500);

		paymentMachine.closePayment();

		Assert.assertEquals(paymentMachine.getPayedAmount(), 0);
		Assert.assertEquals(paymentMachine.getChangedAmount(), 0);
	}

	@Test
	public void testChangeAmount() throws Exception {
		Ticket ticket = new Ticket(1);
		ticket.setAmount(2200);
		
		paymentMachine.startPayment(ticket);
		
		paymentMachine.addCoin(Coin.C1000);
		paymentMachine.addCoin(Coin.C1000);
		paymentMachine.addCoin(Coin.C500);

		Assert.assertEquals(paymentMachine.getPayedAmount(), 2500);
		Assert.assertEquals(paymentMachine.calculateChangeAmount(), paymentMachine.getPayedAmount() - ticket.getAmount());
	}
	
	@Test
	public void testChangedAmount() throws Exception {
		Ticket ticket = new Ticket(1);
		ticket.setAmount(2200);
		
		paymentMachine.startPayment(ticket);
		
		paymentMachine.addCoin(Coin.C1000);
		paymentMachine.addCoin(Coin.C1000);
		paymentMachine.addCoin(Coin.C500);

		long payedAmount = paymentMachine.getPayedAmount();

		Assert.assertEquals(paymentMachine.getPayedAmount(), 2500);

		paymentMachine.executePayment();
		
		Assert.assertEquals(paymentMachine.getChangedAmount(), payedAmount - ticket.getAmount());
	}
	
	@Test
	public void testExecutePayment() throws Exception {
		long initialAmount = paymentMachine.getStorageAmount();
		
		Ticket ticket = new Ticket(1);
		ticket.setAmount(2200);
		
		paymentMachine.startPayment(ticket);
		
		paymentMachine.addCoin(Coin.C1000);
		paymentMachine.addCoin(Coin.C1000);
		paymentMachine.addCoin(Coin.C500);

		long payedAmount = paymentMachine.getPayedAmount();

		Assert.assertEquals(paymentMachine.getPayedAmount(), 2500);

		paymentMachine.executePayment();
		
		long changeAmount = payedAmount - ticket.getAmount();

		Assert.assertEquals(paymentMachine.getStorageAmount(), initialAmount + payedAmount - changeAmount);
		Assert.assertEquals(paymentMachine.getChangedAmount(), changeAmount);
		Assert.assertEquals(paymentMachine.getPayedAmount(), 0);
	}
	
}
