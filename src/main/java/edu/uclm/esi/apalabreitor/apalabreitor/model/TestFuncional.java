package edu.uclm.esi.apalabreitor.apalabreitor.model;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class TestFuncional {
  private WebDriver driverCarlos;
  private WebDriver driverAna;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
	System.setProperty("webdriver.chrome.driver", "C:\\Users\\Miguel Angel\\Desktop\\Universidad\\TyS Web\\chromedriver.exe");
    driverCarlos = new ChromeDriver();
    driverAna = new ChromeDriver();
    baseUrl = "https://www.google.com/";
    driverCarlos.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    driverAna.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }
  
  @Test
  public void testPartida() {
	  try {
		login(this.driverCarlos, "carlos", "carlos");
		pausa();
		driverCarlos.findElement(By.id("btnNuevaPartida")).click();
	    
		
		login(this.driverAna, "miguel", "miguel");
		pausa();
		driverAna.findElement(By.id("btnUnirmeAPartida")).click();
		
		pausa();
		
		clickEn(driverCarlos, 8, 8);
		clickEnLetra(driverCarlos, 1);
		clickEn(driverCarlos, 8, 9);
		clickEnLetra(driverCarlos, 1);
		clickEn(driverCarlos, 8, 10);
		clickEnLetra(driverCarlos, 1);
		clickEn(driverCarlos, 8, 11);
		clickEnLetra(driverCarlos, 1);
		
		driverCarlos.findElement(By.id("jugar")).click();
		
		
	} catch (Exception e) {
		fail("Error inesperado");
	}
	  
  }

private void clickEn(WebDriver driver, int fila, int columna) {
	driver.findElement(By.xpath("/html/body/div[1]/div[2]/table/tbody/tr[" + fila + "]/td[" + columna + "]")).click();
}

private void clickEnLetra(WebDriver driver, int n) {
	driver.findElement(By.xpath("/html/body/div[1]/div[1]/button[" + n + "]")).click();
}

private void pausa() {
	try { Thread.sleep(1000); }
	catch (Exception e) {
		// TODO: handle exception
	}
}

  public void login(WebDriver driver, String nombre, String pwd) throws Exception {
    driver.get("http://localhost:8080/");
    driver.findElement(By.id("loginUserName")).click();
    driver.findElement(By.id("loginUserName")).clear();
    driver.findElement(By.id("loginUserName")).sendKeys(nombre);
    driver.findElement(By.id("cajaPassword")).click();
    driver.findElement(By.id("cajaPassword")).clear();
    driver.findElement(By.id("cajaPassword")).sendKeys(pwd);
    // ERROR: Caught exception [ERROR: Unsupported command [setTimeout |  | ]]
    driver.findElement(By.id("btnLogin")).click();
  }

  @After
  public void tearDown() throws Exception {
   // driverCarlos.quit();
    //driverAna.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(WebDriver driver, By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent(WebDriver driver) {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText(WebDriver driver) {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}