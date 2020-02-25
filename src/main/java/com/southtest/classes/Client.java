package com.southtest.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@ToString
public class Client {
	String id;
	String cnpj;
	String name;
	String businessArea;
	
}
