package com.southtest.classes;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@ToString
public class Sale {
	String id;
	String saleid;
	List<SaleItem> saleItems;
	String salesmanName;
	
}
