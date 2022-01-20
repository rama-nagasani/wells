package cusip;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessCusip {
	
	private static final Pattern CUSIP_PATTERN_CHECK = Pattern.compile("[A-Za-z0-9]{9}");
	private static final Pattern PRICE_PATTERN_CHECK = Pattern.compile("[0-9]*.[0-9]*");
	
	private static StringBuffer sb = new StringBuffer();
	
	
	public static void main(String[] args) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		Scanner scanner = null;
		try
		{
			String path = Paths.get("").toAbsolutePath().toString();	
			try 
			{
				fis = new FileInputStream(path+"\\src\\main\\resources\\cusips.txt");
			} 
			catch (FileNotFoundException e) {
				System.err.println("Please update the file path. You can even give absoute file path also. For Example C:\\users\\cusips.txt");
				System.err.println(e.getMessage());
			}
			scanner = new Scanner(fis,"UTF-8");
			boolean processCusipFlag = false;
			boolean firstCusip = false;
			List<BigDecimal> lst = new ArrayList<BigDecimal>();
			String cusipLast="";
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				Matcher matcherCusip = CUSIP_PATTERN_CHECK.matcher(line);
				if(matcherCusip.matches()) {
					if(!firstCusip) {
						firstCusip = true;
						processCusipFlag = false;
						cusipLast = line;
					}
					else {
						processCusipFlag = true;
					}
				}
				else {
					processCusipFlag = false;
					Matcher matcherPrice = PRICE_PATTERN_CHECK.matcher(line);
					if(matcherPrice.matches()) {
						lst.add(new BigDecimal(line));
					}
					else
					{
						System.err.println("Invalid Line : "+line);
					}
				}
					
				if(firstCusip && processCusipFlag) {
					sb.append(cusipLast);
					if(!lst.isEmpty()) {
						processCusip(cusipLast, lst);
					}
					lst = new ArrayList<BigDecimal>();
					cusipLast = line;
				}
			}
			if(!lst.isEmpty() && !cusipLast.equals("")) {
				sb.append(cusipLast);
				processCusip(cusipLast, lst);
				lst = new ArrayList<BigDecimal>();
			}
			if(sb != null && !sb.toString().equals(""))
			{
				try {	
					fos = new FileOutputStream(path+"\\src\\main\\resources\\cusips_out.txt");
					try {
						System.out.println(sb.toString());
						fos.write(sb.toString().getBytes());
					} catch (IOException e) {
						System.err.println(e.getMessage());
					}
				} catch (FileNotFoundException e) {
					System.err.println("Please update the file path. You can even give absoute file path also. For Example C:\\users\\cusips_out.txt");
					System.err.println(e.getMessage());
				}
				finally {
					if(fos != null) {try {
						fos.close();
					} catch (IOException e) {
						System.err.println(e.getMessage());
					}}
				}
			}
		}
		finally
		{
			try {
				if(fis != null) {fis.close();}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			try{
				if(scanner != null) {scanner.close();}
			}
			catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	public static void processCusip(String cusip, List<BigDecimal> prices) {
		sb.append(" Current Price : "+prices.get(prices.size()-1).toString());
		List<BigDecimal> sortedList = prices.stream().sorted().collect(java.util.stream.Collectors.toList());
		sb.append(" Min Price : "+sortedList.get(0).toString());
		sb.append(" Max Price : "+sortedList.get(prices.size()-1).toString()+"\n");
	}
}
