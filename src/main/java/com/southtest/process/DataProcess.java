package com.southtest.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.southtest.classes.Client;
import com.southtest.classes.Sale;
import com.southtest.classes.SaleItem;
import com.southtest.classes.Seller;

public abstract class DataProcess {
	
	public static final Logger log = LoggerFactory.getLogger(DataProcess.class);
	static List<Seller> sellerList = new ArrayList<>();
	static List<Client> clientList = new ArrayList<>();
	static List<Sale> saleList = new ArrayList<>();
	
	public static void processData() {
		log.info("Iniciando processamento dos dados.");
		String homePath = System.getenv("HOME_PATH");		
		File fileList = new File(homePath.concat("/data/in"));
		FilenameFilter filter = (dir, name) -> name.endsWith(".dat");
		File[] files = fileList.listFiles(filter);
		Integer i = 0;
	
		if (fileList.exists()) {
			for (File fileTmp : files) {
				
				i++;
				log.info("Processando arquivo {} de {}.", i, files.length);				
				try {
					if (fileTmp.getName().endsWith(".dat")) {
						List<String> data = FileUtils.readLines(fileTmp, StandardCharsets.UTF_8);
						readFile(data);
						createOutput(fileTmp.getName());
					}
				} catch (IOException e) {
					log.error("Erro ao abrir arquivo. Erro: {}", e.getMessage());
				}
			}
		} else {
			log.error("Diretório {} não encontrado.", fileList.getAbsolutePath());
		}
		log.info("Processamento concluído.");		
	}
	

	private static void createOutput(String fileName) throws IOException {
		String homePath = System.getenv("HOME_PATH");		
		File filePath = new File(homePath.concat("/data/out"));
		
		if (!filePath.exists()) {
			filePath.mkdir();
		}
		
		File file = new File(filePath.getAbsolutePath().concat("/").concat(fileName.replace(".dat", ".done.dat")));
		setData(file);		
	}

	private static void setData(File file) throws IOException {
		Integer qtySeller = sellerList.size();
		Integer qtyClient = clientList.size();
		String idMostValueableSale = getMostValuableSale();
		String worstSeller = getWorstSeller();
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        try {
		
	        bw.write("Quantidade de Clientes: ".concat(qtyClient.toString())
	        			.concat("\nQuantidade de vendedores: ").concat(qtySeller.toString())
	        			.concat("\nID venda mais cara: ").concat(idMostValueableSale)
	        			.concat("\nPior vendedor: ").concat(worstSeller));
        } finally {
	        bw.close();	
	        fw.close();
		} 
	}
	
	private static String getWorstSeller() {
		String sellerName = "";
		BigDecimal worst = new BigDecimal(0);
	    for (Seller seller: sellerList) {
			BigDecimal totalSeller = new BigDecimal(0);
		    for (Sale sale : saleList) {
				BigDecimal totalSale = new BigDecimal(0);
		    	if (sale.getSalesmanName().equals(seller.getName())) {
			    	for (SaleItem saleItem : sale.getSaleItems()) {
			    		totalSale = totalSale.add(saleItem.getQuantity().multiply(saleItem.getPrice()));
				    }	
			    	totalSeller = totalSeller.add(totalSale);
		    	}
		    }
		    if (worst.equals(new BigDecimal(0))) {
		    	worst = totalSeller;
		    }
	    	if (totalSeller.compareTo(worst) < 0) {
	    		sellerName = seller.getName();
	    		worst = totalSeller;
	    	}
		    
	    }
		
		return sellerName;
	}
	
	private static String getMostValuableSale() {
		String idSale = "";
		BigDecimal totalSale = new BigDecimal(0);
		BigDecimal mostValuable = new BigDecimal(0);
	    for (Sale sale : saleList) {
	    	for (SaleItem saleItem : sale.getSaleItems()) {
	    		totalSale = totalSale.add(saleItem.getQuantity().multiply(saleItem.getPrice()));
		    }	
	    	if (totalSale.compareTo(mostValuable) > 0) {
	    		idSale = sale.getSaleid();
	    		mostValuable = totalSale;
	    	}
	    }
		
		return idSale;
	}
	
	private static void readFile(List<String> list) {
		for (String lineStr : list) {
	    	List<String> line = Splitter.on('ç').splitToList(lineStr);

	    	if (line.iterator().next().equals("001")) {
	    		Seller se = new Seller(line.get(0), line.get(1), line.get(2), new BigDecimal(line.get(3)));
	    		sellerList.add(se);
	    	} else if (line.iterator().next().equals("002")) {
	    		Client cl = new Client(line.get(0), line.get(1), line.get(2), line.get(3));
	    		clientList.add(cl);
	    	} else if (line.iterator().next().equals("003")) {
    			List<SaleItem> saleItemList = new ArrayList<>();
		    	List<String> item = Splitter.on(',').splitToList( line.get(2));
			    for (String saleItemLine : item) {
			    	List<String> saleItem = Splitter.on('-').splitToList(saleItemLine.replaceAll("\\[|]", ""));
			    	SaleItem si = new SaleItem(saleItem.get(0), new BigDecimal(saleItem.get(1)), new BigDecimal(saleItem.get(2)));
			    	saleItemList.add(si);
		    	}
	    		Sale cl = new Sale(line.get(0), line.get(1), saleItemList, line.get(3));
	    		saleList.add(cl);
	    	}
         }
	}
}
