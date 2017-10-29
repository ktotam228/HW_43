package core;

import org.openqa.selenium.*;
import org.openqa.selenium.safari.SafariDriver;
import java.math.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import java.util.regex.*;

public class Safari {

	public static void main(String[] args) throws InterruptedException {
		Logger.getLogger("").setLevel(Level.OFF);
		String url = "http://alex.academy/exe/payment_tax/index3.html";

		if (!System.getProperty("os.name").contains("Mac")) throw new IllegalArgumentException("Safari is available only on Mac");

		WebDriver driver = new SafariDriver();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		driver.manage().window().maximize();

		driver.get(url);
		String string_monthly_payment_and_tax = driver.findElement(By.id("id_monthly_payment_and_tax")).getText();

	    String regex = "^"                                                         // ^ Start of llline
		           + "Payment: \\$(91.21), Tax: (8.25)%"
		           + "(?:.*?)?"
		           + "(?:\\$*)?"
		           + "(?:\\s*)?"
		           + "((?:\\d*)|(?:\\d*)(?:\\.)(?:\\d*))"
		           + "(?:\\s*)?"
		           + "(?:[/]*|,\\s*[A-Z]*[a-z]*\\s*[:]*)?"
		           + "(?:\\s*)?"
		           + "((?:\\d*)|(?:\\d*)(?:\\.)(?:\\d*))"
		           + "(?:\\s*)?"
		           + "(?:%)?"
		           + "(?:\\s*)?"
		       + "$";                                   // $ End of line

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(string_monthly_payment_and_tax); m.find();

		double monthly_payment = Double.parseDouble(m.group(1));
		double tax = Double.parseDouble(m.group(2));
		// (91.21 * 8.25) / 100 = 7.524825
		double monthly_and_tax_amount = new BigDecimal((monthly_payment * tax) / 100).setScale(2, RoundingMode.HALF_UP).doubleValue();
		// 91.21 + 7.52 = 98.72999999999999
		double monthly_payment_with_tax = new BigDecimal(monthly_payment + monthly_and_tax_amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
		double annual_payment_with_tax = new BigDecimal(monthly_payment_with_tax * 12).setScale(2, RoundingMode.HALF_UP).doubleValue();
		
		driver.findElement(By.id("id_annual_payment_with_tax")).sendKeys(String.valueOf(annual_payment_with_tax));
		
		WebElement settings = driver.findElement(By.id("id_validate_button"));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", settings);
		
		String actual_result = driver.findElement(By.id("id_result")).getText();
		System.out.println("String: \t" + string_monthly_payment_and_tax);
		System.out.println("Annual Payment with Tax: " + annual_payment_with_tax);
		System.out.println("Result: \t" + actual_result);
		driver.quit();
	}
}