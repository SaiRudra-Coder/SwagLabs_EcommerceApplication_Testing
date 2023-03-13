package webapp.com;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumRemote 
{
	public Logger log;
	public Map sh;
	public WebDriver driver;
	public SeleniumRemote(WebDriver driver, Logger log, Map sh)
	{
		this.log=log;
		this.sh=sh;
		this.driver=driver;
	}
	
	public WebElement explicitlyWaits(String xpath, boolean css, int wait_sec)
	{
		try 
		{
			if(css ==false)
			{
				WebDriverWait wt = new WebDriverWait(this.driver, Duration.ofSeconds(wait_sec));
				wt.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
				return driver.findElement(By.xpath(xpath));
			}
			else
			{
				WebDriverWait wt = new WebDriverWait(this.driver, Duration.ofSeconds(wait_sec));
				wt.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(xpath)));
				return driver.findElement(By.cssSelector(xpath));
			}			
		} 
		catch (IllegalArgumentException e3) 
		{
			this.log.error("Noticed null as a selector");
		}
		catch (InvalidSelectorException  e) 
		{
			this.log.error("Selecting Incorrect Selector");
		}
		catch (NoSuchElementException e1)
		{
			this.log.error("The locators are unable to find or access elements on the web page");
		}
		catch (TimeoutException e2) 
		{
			this.log.error("The command did not execute or complete within wait time");
		}
		return null;
	}
	public String getText(String selector, int wait_sec)
	{
		String count = null;
		try 
		{
			WebElement msg=this.explicitlyWaits(selector, true, wait_sec);
			if(msg!=null)
			{
				count=msg.getText();
				return count;
			}
		} 
		catch (NoSuchElementException e1)
		{
			this.log.info("Cart icon number count is empty!!");
			count=null;
		}
		catch (TimeoutException e1)
		{
			this.log.info("Cart icon number count is empty!!");
			count = null;
		}
		return count;
	}
	
	public int productsCount(String pselector, int wait_sec)
	{
		WebDriverWait wt = new WebDriverWait(this.driver, Duration.ofSeconds(wait_sec));
		wt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(pselector)));
		List<WebElement> products=this.driver.findElements(By.cssSelector(pselector));
		int productCount=products.size();
		return productCount;
	}
	
	public ArrayList xpath(String userXpath, String passwordXpath, int wait_sec)
	{
		ArrayList al= new ArrayList();
		WebElement ele=explicitlyWaits(userXpath, true, wait_sec);
		if(ele!=null)
		{
			try 
			{
				String[] ar =ele.getText().split("\n");
				if(ar.length>0)
				{
					
					String userName1=ar[1].strip();
					String userName2=ar[2].strip();
					String userName3=ar[3].strip();
					String userName4=ar[4].strip();

					al.add(userName1); al.add(userName2); al.add(userName3); al.add(userName4); 
				}
				else
				{
					this.log.error("String array is empty, Not able to extract");
				}
			} 
			catch (NullPointerException e) 
			{
				this.log.error("User Name is empty, Not able to extract ");
			}			
		}
		else
		{
			this.log.error("Element is null");
			
		}
		WebElement ele1=explicitlyWaits(passwordXpath, true, wait_sec);
		if(ele1!=null)
		{
			try 
			{
				String[] ar1=ele1.getText().split("\n");
				if(ar1.length>0)
				{
					String password=ar1[1].strip();
					al.add(password);
				}
				else
				{
					this.log.error("String array is empty, Not able to extract");
				}
			} 
			catch (NullPointerException e) 
			{
				this.log.error("Password is empty, Not able to extract ");
			}		
		}
		else
		{
			this.log.error("Element is null");
		}
		return al;
	}
	
	public boolean sendKeys(String Selector, String sendkeys, int wait_sec)
	{		
		WebElement ele=explicitlyWaits(Selector, true, wait_sec);
		if(ele!=null)
		{
			ele.sendKeys(sendkeys);
			return true;
		}
		return false;
	}
	
	public boolean buttonClick(String selector, int wait_sec)
	{
		WebElement ele=explicitlyWaits(selector, true, wait_sec);
		if(ele!=null)
		{
			ele.click();
			return true;
		}
		return false;
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public boolean navigateToOtherSite(ArrayList al)
	{
		Set window=this.driver.getWindowHandles();
		ArrayList website= new ArrayList();
		ArrayList new_window= new ArrayList(window);
		for(Object win:new_window)
		{
			try
			{
				this.driver.switchTo().window(win.toString());			
				String window_title = this.driver.getCurrentUrl();
				if(window_title!=null || window_title!="")
				{
					website.add(window_title.split("/")[2].replace(".com", "").split("\\.")[0].replace("ww", "").strip().toLowerCase());
				}
				else
				{
					this.log.error("Url is null or Empty");
				}
			} 
			catch (NoSuchWindowException e)
			{
				this.log.error(e.getMessage());
			}
			
			catch (UnsupportedOperationException e)
			{
				this.log.error(e.getMessage());
			}
			
		}
		boolean status=false;
		for(Object path: al)
		{
			try
			{
				String val=path.toString().split("_")[1].strip().toLowerCase();
				website.contains(val);
				status = true;
			} 
			catch (IndexOutOfBoundsException e) 
			{
				this.log.error(path+" is null or empty");
				status=false;
			}
			
			Set windows =this.driver.getWindowHandles();
			for(Object wind: windows)
			{
				if (!this.driver.getTitle().toLowerCase().equals("swag labs"))
				{
					try 
					{
						this.driver.switchTo().window(wind.toString());
						status= true;
					} 
					catch (NoSuchWindowException e4) 
					{
						this.log.error(e4.getMessage());
					}
					catch (UnsupportedOperationException e) 
					{
						this.log.error(e.getMessage());
					}
				}
				else 
				{
					break;
				}
			}
			status=true;
		}
		return status;
	}
	
	public boolean driverClose()
	{
		try 
		{
			this.driver.quit();
			return true;
		} 
		catch (Exception e) 
		{
			this.log.error("Browser not closed");
			return false;
		}
	}
	
	public boolean productClick(String selector, int wait_sec)
	{		
		boolean status=true;
		try 
		{
			WebElement ele=explicitlyWaits(selector, false, wait_sec);
			Actions ac=new Actions(this.driver);
			ac.moveToElement(ele).click().perform();
			status=true;
		} 
		catch (NoSuchElementException e1)
		{
			this.log.error("The locators are unable to find or access elements on the web page");
			status=false;
		}
		catch (TimeoutException e2) 
		{
			this.log.error("The command did not execute or complete within wait time");
			status= false;
		}
		return status;
	}
	
	public void scrollDown(String addItem, int wait_sec)
	{
		try 
		{
			WebElement item=explicitlyWaits(addItem, true, wait_sec);
			JavascriptExecutor js=(JavascriptExecutor)this.driver;		
			js.executeScript("arguments[0].scrollIntoview(true)", item);
		} 
		catch (JavascriptException e) 
		{
			this.log.error("JavascriptExecutor class object is not working");
		}
	}
	
	public LinkedHashMap product(String pselector, int wait_sec)
	{
		try 
		{
			WebDriverWait wt = new WebDriverWait(this.driver, Duration.ofSeconds(wait_sec));
			wt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(pselector)));
			List<WebElement> products=this.driver.findElements(By.cssSelector(pselector));
			
			LinkedHashMap main= new LinkedHashMap();
			int count=1;		
			for(WebElement p_info: products)
			{
				LinkedHashMap hm = new LinkedHashMap();
				try 
				{
					String[] list=p_info.getText().split("\n");
					if(list.length>0)
					{
						String productName=list[0].strip();
						String productInfo=list[1].strip();
						String productPrice=list[2].strip();
						hm.put("Title", productName);
						hm.put("Description", productInfo);
						hm.put("Price", productPrice);
						
						main.put("product_"+count, hm);
						count++;
					}
					else
					{
						this.log.error("String Array list is empty");
					}
					
				} 
				catch (NullPointerException e) 
				{
					this.log.error("User Name is empty, Not able to extract ");
				}				
			}
			return main;
		}
		
		catch (NoSuchElementException e1)
		{
			this.log.error("The locators are unable to find or access elements on the web page");
		}
		catch (TimeoutException e2) 
		{
			this.log.error("The command did not execute or complete within wait time");
		}
		catch (IllegalArgumentException e3) 
		{
			this.log.error("Noticed null as a selector");
		}
		return null;
	}
	
	public String removeProduct(String pselector, String removeSelector, String countSelector, int wait_sec)
	{
		try
		{
			WebDriverWait wt = new WebDriverWait(this.driver, Duration.ofSeconds(wait_sec));
			wt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(pselector)));
			List<WebElement> products=this.driver.findElements(By.cssSelector(pselector));		
			String status = "";
			for(Object data:products)
			{
				LinkedHashMap lhm = new LinkedHashMap();
				WebElement elements=(WebElement)data;
				elements.findElement(By.cssSelector(removeSelector)).click();
			}
			try 
			{
				this.productsCount(countSelector, wait_sec);
			} 
			catch (NoSuchElementException e1)
			{
				this.log.info("Cart Items are empty ");
				status = null;
			}
			catch (TimeoutException e1)
			{
				this.log.info("Cart Items are empty ");
				status = null;
			}
			catch (IllegalArgumentException e3) 
			{
				this.log.error("Noticed null as a selector");
				status=null;
			}
			return status;	
			
		}
		catch (NoSuchElementException e1)
		{
			this.log.error("The locators are unable to find or access elements on the web page");
		}
		catch (TimeoutException e2) 
		{
			this.log.error("The command did not execute or complete within wait time");
		}
		catch (IllegalArgumentException e3) 
		{
			this.log.error("Noticed null as a selector");
		}
		return null;
	}
	
	public LinkedHashMap cartProductValidation(String pselector, int wait_sec)
	{
		try
		{
			WebDriverWait wt = new WebDriverWait(this.driver, Duration.ofSeconds(wait_sec));
			wt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(pselector)));
			List<WebElement> products=this.driver.findElements(By.cssSelector(pselector));
			
			LinkedHashMap main= new LinkedHashMap();
			int count=1;		
			for(WebElement p_info: products)
			{
				LinkedHashMap hm = new LinkedHashMap();
				try
				{
					String[] list=p_info.getText().split("\n");
					if(list.length>0)
					{
						String productName=list[1].strip();
						
						String productInfo=list[2].strip();
						String productPrice=list[3].strip();
						hm.put("Title", productName);
						hm.put("Description", productInfo);
						hm.put("Price", productPrice);
						
						main.put("product_"+count, hm);
						count++;
					}
					else
					{
						this.log.error("String array list is empty");
					}
				} 
				catch (NullPointerException e) 
				{
					this.log.error("User Name is empty, Not able to extract ");
				}
			}
			return main;
		} 
		catch (NoSuchElementException e1)
		{
			this.log.error("The locators are unable to find or access elements on the web page");
		}
		catch (TimeoutException e2) 
		{
			this.log.error("The command did not execute or complete within wait time");
		}
		catch (IllegalArgumentException e3) 
		{
			this.log.error("Noticed null as a selector");
		}
		return null;
	}
	
	public ArrayList getRandomInfo(List products, int num)
	{
		Random ar = new Random();
		ArrayList index_size = new ArrayList();
		ArrayList data_info= new ArrayList();
		
		while(index_size.size()<num)
		{
			int index=ar.nextInt(products.size());
			if(!index_size.contains(index))
			{
				index_size.add(index);
			}
		}
		for(Object k: index_size)
		{
			data_info.add(products.get(Integer.parseInt(k.toString())));
		}
		return data_info;
	}
	
	public LinkedHashMap random(String pselector, String titleSelector, String desSelector, String priceSelector, String cartSelector, int wait_sec)
	{
		try
		{
			LinkedHashMap lhm= new LinkedHashMap();
			int count=1;
			WebDriverWait wt = new WebDriverWait(this.driver, Duration.ofSeconds(wait_sec));
			wt.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(pselector)));
			List<WebElement> products=this.driver.findElements(By.cssSelector(pselector));
			ArrayList data=this.getRandomInfo(products, 4);
			for(Object info: data)
			{
				LinkedHashMap lh= new LinkedHashMap();
				WebElement ele=(WebElement)info;
				lh.put("Description", ele.findElement(By.cssSelector(desSelector)).getText());
				lh.put("Price", Double.parseDouble(ele.findElement(By.cssSelector(priceSelector)).getText().replace("$", "")));
				ele.findElement(By.cssSelector(cartSelector)).click();
				lhm.put(ele.findElement(By.cssSelector(titleSelector)).getText(), lh);	
				// {product_1={title='', desc=''}}
				//{title_text={desc: '', price: ''
			}		
			return lhm;
		}
		catch (NoSuchElementException e1)
		{
			this.log.error("The locators are unable to find or access elements on the web page");
			return null;
		}
		catch (TimeoutException e2) 
		{
			this.log.error("The command did not execute or complete within wait time");
			return null;
		}
		catch (IllegalArgumentException e3) 
		{
			this.log.error("Noticed null as a selector");
			return null;
		}
	}
	
	public double priceSeparation(String selector, int wait_sec)
	{
		double finalTotalPrice=0.00;
		WebElement ele=this.explicitlyWaits(selector, true, wait_sec);
		if(ele!= null)
		{
			try 
			{
				String[] res=ele.getText().split(": ");
				if(res.length>0)
				{
					double totalPrice=Double.parseDouble(res[1].replace("$", "").strip());
					return finalTotalPrice+totalPrice;
				}
				else
				{
					this.log.error("String array is empty, Not able to extract");
				}
			} 
			catch (NullPointerException e) 
			{
				this.log.error("Price  is empty, Not able to extract ");
			}
		}
		else
		{
			this.log.error("Element is null");
		}
		return finalTotalPrice;
	}
	
	public boolean msgValidation(String error, int wait_sec)
	{
		boolean status=true;
		WebElement msg=this.explicitlyWaits(error, true, wait_sec);
		if(msg!=null)
		{
			if(msg.getText().equalsIgnoreCase("error") || msg.getText().equalsIgnoreCase("Login") || msg.getText().equalsIgnoreCase("Sauce Labs Backpack")
					|| msg.getText().equalsIgnoreCase("Sorry, this user has been locked out.") || msg.getText().equalsIgnoreCase("THANK YOU FOR YOUR ORDER"))
			{
				status= true;
			}
		}
		else
		{
			this.log.error("Element is null not loadded");
			status=false;
		}
		return status;
	}
	
	public String sortingDropDown(String cssSelector, int wait_sec, int select_index)
	{
		WebElement sorting=this.explicitlyWaits(cssSelector, true, wait_sec);
		String sort_option = null;
		if(sorting!=null)
		{
			Select se= new Select(sorting);
			se.selectByIndex(select_index);
			try 
			{
				String sorting_selector=validate_configureData("sortingSelector", null);
				sort_option = this.driver.findElement(By.className(sorting_selector)).getText();
				this.log.info("sorting based on product "+sort_option+" option is clicked ");
				return sort_option;
			} 
			catch (NoSuchElementException e1)
			{
				this.log.error("The locators are unable to find or access elements on the web page");
			}
			catch (TimeoutException e2) 
			{
				this.log.error("The command did not execute or complete within wait time");
			}
		}
		else
		{
			this.log.error("Element is null not loadded");
		}
		return sort_option;
	}
	
	public String validate_configureData(String key, String  key2)
	{
		if(this.sh.get(key)!=null)
		{
			if(key2!=null)
			{
				if(((Map)this.sh.get(key)).containsKey(key2))
				{
					if(((Map)this.sh.get(key)).get(key2)!=null)
					{
						return ((Map)this.sh.get(key)).get(key2).toString();
					}
					else
					{
						this.log.error(key2 +"value is null inthe configure file ");
					}
				}
				else
				{
					this.log.error(key2+" is null in configure file inthe section of "+ key);
					
				}
			}
			else
			{
//				this.log.error(key2 + " is  null in the configure file");
				return this.sh.get(key).toString();
			}
			
		}
		else
		{
			this.log.error(key + " is null in the configure file ");
		}
		return null;
	}
	
	
	
	
	
	
}
