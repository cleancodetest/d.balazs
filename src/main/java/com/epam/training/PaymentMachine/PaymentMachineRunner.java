package com.epam.training.PaymentMachine;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PaymentMachineRunner {

	public static void run(long initialAmount) throws Exception {
		PaymentMachine paymentMachine = new PaymentMachine();
		
		String line = "";

		BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));

		while (true) {

			if (!paymentMachine.hasAnyAvailableCoins()) {
				System.out.println("Machine out of order (no available coins)");
				break;
			}
			
			paymentMachine.printAvailableCoins();
			System.out.println("Please enter a ticket number: ");
			
			line = buffer.readLine();
			
			if (line.equals("exit")) {
				break;
			}
			else if (line.equals("")) {
				paymentMachine.cancelPayment();
				System.out.println("Payment cancelled");
			}
			else {
				
				int ticketNumber = Integer.parseInt(line);
				Ticket ticket = new Ticket(ticketNumber);

				paymentMachine.printTicketPaymentInformations(ticket);
				
				paymentMachine.startPayment(ticket);
				
				while (ticket.getAmount() > paymentMachine.getPayedAmount()) {
					System.out.println("Enter coin: ");
					
					line = buffer.readLine();
					
					try {
						Coin coin = paymentMachine.getCoinByValue(line);
						paymentMachine.addCoin(coin);
					} catch (InvalidCoinException e) {
						System.out.println("Invalid coin");
					}
				}
				
				paymentMachine.printPayedCoins();

				paymentMachine.generateChangeCoins();
				
				paymentMachine.printChangedCoins();
								
				paymentMachine.closePayment();
			}
		}

		buffer.close();
		System.out.println("Payment terminated");
	}
	
}
