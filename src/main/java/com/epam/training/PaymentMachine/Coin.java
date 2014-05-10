package com.epam.training.PaymentMachine;

public enum Coin {

	C20000(20000),
	C10000(10000),
	C5000(5000),
	C2000(2000),
	C1000(1000),
	C500(500),
	C200(200),
	C100(100),
	C20(20),
	C50(50),
	C10(10),
	C5(5);
	
	
	public static final Coin MIN_VALUE = C5;

	private int value;
	
	Coin() {
	}
	
	Coin(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
}
