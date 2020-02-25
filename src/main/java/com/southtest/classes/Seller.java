package com.southtest.classes;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Seller {
	String id;
	String cpf;
	String name;
	BigDecimal salary;
	
}
