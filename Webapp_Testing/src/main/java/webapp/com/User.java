package webapp.com;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import org.yaml.snakeyaml.Yaml;

import io.github.bonigarcia.wdm.WebDriverManager;

public class User 
{
	public SeleniumRemote sc;
	public Logger log;
	public Map sh;
	public WebDriver driver;
	
	@BeforeTest(groups = {"smoke"})
	@Parameters({"yamlConfigurationPath"})
	public void loadConfig(String yamlConfigurationPath)
	{
		try 
		{
			File fl= new File(yamlConfigurationPath);
			FileReader fr= new FileReader(fl);
			Yaml yl = new Yaml();
			this.sh=yl.load(fr);
			PropertyConfigurator pc= new PropertyConfigurator();
			pc.configure(this.sh.get("Path").toString());
		} 
		catch (NullPointerException e2)
		{ 
			System.out.println(e2.getMessage());
			System.exit(0);
		}
		catch (FileNotFoundException e) 
		{
			System.out.println("Missing Configuration File" +e.getMessage());
			System.exit(0);
		}
		this.log= Logger.getLogger(User.class.getName());
		
	}
	
	@BeforeTest(dependsOnMethods = {"loadConfig"})
	@Parameters({"url"})
	public void driver_setup(String url)
	{
		try 
		{
			WebDriverManager.chromedriver().setup();
			this.driver= new ChromeDriver();
			this.driver.manage().window().maximize();
			this.driver.get(url);
			this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(40));	
			this.sc= new SeleniumRemote(this.driver, this.log, this.sh);
		} 
		catch (WebDriverException e) 
		{
			this.log.error("Driver initiation failed due to "+e.getMessage());
			System.exit(0);
		}
		
	}
	
	@AfterTest
	public void driver_quit()
	{
		this.driver.quit();
	}
	
	@BeforeClass
	public void login_page()
	{
		String mode="standard";
		String user_validation = this.sc.validate_configureData("LoginStatus", "userNameSelector");
		String password_val= this.sc.validate_configureData("LoginStatus", "passwordSelector");
		ArrayList al=this.sc.xpath(user_validation, password_val, 20);
		try 
		{
			if(al.size()>2)
			{
				boolean userName = false;
				if (mode.equals("standard"))
				{
					this.log.info("Extracted Username = "+al.get(0).toString());
					String user_nameStandard=this.sc.validate_configureData("LoginStatus", "sendkeysUsernameSelector");
					userName=this.sc.sendKeys(user_nameStandard, al.get(0).toString(), 30);
				}
				else if (mode.equals("glitch"))
				{
					this.log.info("Extracted Username = "+al.get(3).toString());
					String user_nameGlitch=this.sc.validate_configureData("LoginStatus", "sendkeysUsernameSelector");
					userName=this.sc.sendKeys(user_nameGlitch, al.get(3).toString(), 40);
				}
				if(userName==true)
				{
					this.log.info("User name Entered successfully");
					this.log.info("Extracted password = "+al.get(4).toString());
					String passwordSend=this.sc.validate_configureData("LoginStatus", "sendkeysPasswordSelector");
					boolean password=this.sc.sendKeys(passwordSend, al.get(4).toString(), 10);				
					if(password==true)
					{
						this.log.info("Password Entered Successfully");
						String loginButton_selector=this.sc.validate_configureData("LoginStatus", "loginButton");
						boolean loginButtn=this.sc.buttonClick(loginButton_selector, 10);
						if(loginButtn==true)
						{
							this.log.info("Login button clicked successfully");
							String homepage_selector=this.sc.validate_configureData("LoginStatus", "homePageValidate");
							boolean homePage=this.sc.msgValidation(homepage_selector, 13);
							if(homePage==true)
							{
								this.log.info("Home page successfully login");
								this.log.info("TEST CASE PASSED!!");
								Assert.assertTrue(true);
							}
							else
							{
								this.log.error("Home Page failed to login or loading");
								this.log.error("TEST CASE FAILED!!");
								Assert.fail("Home Page failed to login or loading");
							}
						}
						else
						{
							this.log.error("Login button not clicked");
							Assert.fail("Login button not clicked");
						}
					}
					else
					{
						this.log.error("Password not entered in input box");
						Assert.fail("Password not entered in input box");
					}
				}
				else
				{
					this.log.error("User name not entered in input box");
					Assert.fail("User name not entered in input box");
				}
			}
			else
			{
				this.log.error("ArrayList size is empty, Not abled to extract");
				Assert.fail("ArrayList size is empty, Not abled to extract");
			}
				
		} 
		catch (IndexOutOfBoundsException e) 
		{
			this.log.error(e.getMessage());
		}
	}
	
	@AfterClass
	public void logout_page()
	{
		String slidebar_selector= this.sc.validate_configureData("logoutStatus", "sideButton");
		boolean slide_tap=this.sc.buttonClick(slidebar_selector, 10);
		if(slide_tap==true)
		{
			this.log.info("Slide bar clicked");
			String logout_selector= this.sc.validate_configureData("logoutStatus", "logoutButton");
			boolean logout_button=this.sc.buttonClick(logout_selector, 10);
			if(logout_button==true)
			{
				this.log.info("Logout button clicked");
				String logoutvalidation_selector= this.sc.validate_configureData("logoutStatus", "logoutValddate");
				boolean logout_validation=this.sc.msgValidation(logoutvalidation_selector, 10);
				if(logout_validation==true)
				{
					this.log.info("successfully Logged out");
					this.log.info("TEST CASE PASSED For logout status");
					Assert.assertTrue(true);
				}
				else
				{
					this.log.error("Failed to logout");	
					this.log.info("TEST CASE FAILED For logout status");
					Assert.fail("TEST CASE FAILED For logout status");;
				}
			}
			else
			{
				this.log.error("Faild to click Logout option");
				Assert.fail("Faild to click Logout option");
			}
		}
		else
		{
			this.log.error("Failed to click slide bar icon");
			Assert.fail("Failed to click slide bar icon");
		}
	}
	
	@Test(priority = 1)
	public void poductSelection()
	{
		String productSelector=this.sc.validate_configureData("productSelection", "productsSelector");
		LinkedHashMap products=this.sc.product(productSelector, 30);
		if(products!=null)
		{
			for(Object select: products.keySet())
			{			
				LinkedHashMap data=(LinkedHashMap) products.get(select);
				if(data!=null)
				{
					String beforeTitle = data.get("Title").toString();
					String beforePrice=data.get("Price").toString();
					String beforeDescription=data.get("Description").toString();
					if (this.sc.productClick("//*[contains(text(), \""+beforeTitle+"\")]", 60) == true)
					{
						// validate title, description and price
						this.log.info(beforeTitle+" - selected successfuly");
						String afterProductDet_selector=this.sc.validate_configureData("productSelection", "afterProductdet");
						LinkedHashMap afterProductDet=this.sc.product(afterProductDet_selector, 50);
						if(afterProductDet!=null)
						{
							for(Object sel:afterProductDet.keySet())
							{
								LinkedHashMap afterData=(LinkedHashMap) afterProductDet.get(sel);
								if(afterData!=null)
								{
									String afterTitle=afterData.get("Title").toString();
									String afterPrice=afterData.get("Price").toString();
									String afterDescription=afterData.get("Description").toString();
									if(beforeTitle.equalsIgnoreCase(afterTitle) && beforePrice.equalsIgnoreCase(afterPrice) && beforeDescription.equalsIgnoreCase(afterDescription))
									{
										
										this.log.info("Before product details after selecting the product details both are same");
										Assert.assertTrue(true);
									}
									else
									{
										this.log.error("before and after product datails not matched");
										Assert.fail("before and after product datails not matched");
									}
								}
								else
								{
									this.log.error("Products data is empty, not able to extract");
									Assert.fail("Products data is empty, not able to extract");
								}
							}
						}
						else
						{
							this.log.error("After Product details are empty not able to extract");
							Assert.fail("After Product details are empty not able to extract");
						}
						String backButton_selector=this.sc.validate_configureData("productSelection", "backButtonClick");
						this.sc.buttonClick(backButton_selector, 10);
					}
					else
					{
						this.log.error(beforeTitle+" -  failed to select");
						Assert.fail(beforeTitle+" -  failed to select");
					}
				}
				else
				{
					this.log.error(" Product information is empty, not able to extract");
					Assert.fail("Product information is empty, not able to extract");
				}
			}
		}
		else
		{
			this.log.error("Product information is empty, not able to extract");
			Assert.fail("Product information is empty, not able to extract");
		
		}
	}
	
	@Test(priority = 2)
	
	public void addtoCartTrueScenario()
	{
		String cartInfo_selector=this.sc.validate_configureData("addtoCartTrueScenario", "products");
		String productName_selector=this.sc.validate_configureData("addtoCartTrueScenario", "productName");
		String productDec_selector=this.sc.validate_configureData("addtoCartTrueScenario", "productDesc");
		String productPrice_selector=this.sc.validate_configureData("addtoCartTrueScenario", "productprice");
		String productAddtoCart_selector=this.sc.validate_configureData("addtoCartTrueScenario", "productAddtoCart");
		LinkedHashMap cartInfo=this.sc.random(cartInfo_selector, productName_selector, productDec_selector, productPrice_selector, productAddtoCart_selector, 20);
		if(cartInfo!=null)
		{
			this.log.info("Successfully clicked 4 Random products");
			String beforeCartIcon_selector=this.sc.validate_configureData("addtoCartTrueScenario", "beforeCartIcon");
			boolean cardIcon=this.sc.buttonClick(beforeCartIcon_selector, 20);
			if(cardIcon==true)
			{
				this.log.info("card icon clicked successful");
				String afterCartIcon_selector=this.sc.validate_configureData("addtoCartTrueScenario", "afterCartIcon");
				LinkedHashMap afterCart=this.sc.cartProductValidation(afterCartIcon_selector, 30);
				if(afterCart!=null)
				{
					for(Object sel:afterCart.keySet())
					{
						LinkedHashMap afterData=(LinkedHashMap) afterCart.get(sel);
						if(afterData!=null)
						{
							String afterTitle=afterData.get("Title").toString();
							String afterDescription=afterData.get("Description").toString();					
							Double afterPrice=Double.parseDouble(afterData.get("Price").toString().replace("$", "").strip());					
							if (cartInfo.containsKey(afterTitle) == true)
							{
								HashMap desc_price_hm = (HashMap) cartInfo.get(afterTitle);
								if(desc_price_hm!=null)
								{
									if (desc_price_hm.get("Description").toString().strip().equalsIgnoreCase(afterDescription) && Double.compare((Double) desc_price_hm.get("Price"), afterPrice) == 0)                           
			 						{
										this.log.info("Product description  and Price matched !!");
										this.log.info("Test case PASSED");
										Assert.assertTrue(true);
									}
									else
									{
										this.log.error("Before and after product datails not matched");
										this.log.error("Test Case Failed");
										Assert.fail("Before and after product datails not matched");
									}
								}
								else
								{
									this.log.error("desc_price_hm inforamtion is empty, not able to extract");
									Assert.fail("desc_price_hm inforamtion is empty, not able to extract");
								}
							}
							else
							{
								this.log.error("Product title, Description, Price not Matched");
								Assert.fail("Product title, Description, Price not Matched");
							}
						}
						else
						{
							this.log.error("after data information is empty, not able to extract");
							Assert.fail("after data information is empty, not able to extract");
						}
					}
				}
				else
				{
					this.log.error("cart information is empty, not able to extract");
					Assert.fail("cart information is empty, not able to extract");
				}
			}
			else
			{
				this.log.error("Failed to select card icon");
				Assert.fail("Failed to select card icon");
			}
		}
		else
		{
			this.log.error("Cart inforamation is empty, not able to extract");
			Assert.fail("cart information is empty, not able to extract");
		}
		
	}

	@Test(priority = 3, dependsOnMethods = {"addtoCartTrueScenario"})
	public void checkOut()
	{
		String checkButton_selector=this.sc.validate_configureData("checkOut", "checkoutButton");
		boolean checkClick=this.sc.buttonClick(checkButton_selector, 30);
		if(checkClick==true)
		{
			this.log.info("Checkout button clicked successfully completed");
			String firstName_selector=this.sc.validate_configureData("checkOut", "firstNameSel");
			boolean firstName=this.sc.sendKeys(firstName_selector, "Saikumar", 30);
			if(firstName==true)
			{
				this.log.info("First Name entered success");
				String lastName_selector=this.sc.validate_configureData("checkOut", "lastNameSel");
				boolean lastName=this.sc.sendKeys(lastName_selector, "Rudra", 40);
				if(lastName==true)
				{
					this.log.info("Last name entered success");
					String postal_selector=this.sc.validate_configureData("checkOut", "postCodeSel");
					boolean postCode=this.sc.sendKeys(postal_selector, "506381", 30);
					if(postCode==true)
					{
						this.log.info("post code entered");
						String contiButton_selector=this.sc.validate_configureData("checkOut", "continueButton");
						boolean contiButton=this.sc.buttonClick(contiButton_selector, 40);
						if(contiButton==true)
						{
							this.log.info("Continue button clicked success");
							String cartProducts_selector=this.sc.validate_configureData("checkOut", "cartProducts");
							LinkedHashMap lm=this.sc.product(cartProducts_selector, 20);
							double eachPrice=0.00;
							for(Object data:lm.keySet())
							{
								LinkedHashMap info=(LinkedHashMap)lm.get(data);
								eachPrice=eachPrice+Double.parseDouble(info.get("Price").toString().replace("$", "").strip());
							}					
							String itemTotal_selector=this.sc.validate_configureData("checkOut", "itemTotal");
							double itemTotal=this.sc.priceSeparation(itemTotal_selector, 20);
							String taxPrice_selector=this.sc.validate_configureData("checkOut", "taxPrice");
							double Tax=this.sc.priceSeparation(taxPrice_selector, 20);
							
							if(Double.compare(Math.round(eachPrice), Math.round(itemTotal))==0)
							{
								this.log.info("all products price and item total price both are matched");
								double taxtotal=itemTotal+Tax;
								String totalPrice_selector=this.sc.validate_configureData("checkOut", "totalPrice");
								double total=this.sc.priceSeparation(totalPrice_selector, 20);
								if(Double.compare(Math.round(taxtotal), Math.round(total))==0)
								{
									this.log.info("the adding of tax price and iteam price matched to total price");
									String finishButton_selector=this.sc.validate_configureData("checkOut", "finishButton");
									boolean finish=this.sc.buttonClick(finishButton_selector, 10);
									if(finish==true)
									{											
										this.log.info("Finish button working properly");	
										String orderPlaced_selector=this.sc.validate_configureData("checkOut", "orderPlaced");
										boolean orderPlaced=this.sc.msgValidation(orderPlaced_selector, 20);
										if(orderPlaced==true)
										{
											this.log.info("Your order successfully placed !!");
											this.log.info("TEST CASE PASSED For checkedout the product");
											String back_selector=this.sc.validate_configureData("checkOut", "back");
											this.sc.buttonClick(back_selector, 10);
											Assert.assertTrue(true);
										}
										else
										{
											this.log.error("TEST CASE FAILED");
											Assert.fail("TEST CASE FAILED");
										}
									}
									else
									{
										this.log.error("Finish button not working");
										Assert.fail("Finish button not working");
									}
								}
								else
								{
									this.log.error("The adding of tax and item price not matched with total price");
									Assert.fail("The adding of tax and item price not matched with total price");
								}
							}
							else
							{
								this.log.error("all total prices not matched");
								Assert.fail("all total prices not matched");
							}								
						}
						else
						{
							this.log.error("Failed to click Continue button");
							Assert.fail("Failed to click Continue button");
						}
					}
					else
					{
						this.log.error("Failed to entered postcode");
						Assert.fail("Failed to entered postcode");
					}
				}
				else
				{
					this.log.error("Failed to entered last name");
					Assert.fail("Failed to entered last name");
				}
			}
			else
			{
				this.log.error("Failed to entered firstName");
				Assert.fail("Failed to entered firstName");
			}
		}
	}
	
	@Test(priority = 4)
	public void addToCartFalseScenario()
	{
		String cartInfoProducts_selector=this.sc.validate_configureData("addToCartFalseScenario", "products");
		String ProductsName_selector=this.sc.validate_configureData("addToCartFalseScenario", "productName");
		String ProductsDec_selector=this.sc.validate_configureData("addToCartFalseScenario", "productDesc");
		String ProductsPrice_selector=this.sc.validate_configureData("addToCartFalseScenario", "productprice");
		String ProductsAddtoCart_selector=this.sc.validate_configureData("addToCartFalseScenario", "productAddtoCart");
		LinkedHashMap cartInfo=this.sc.random(cartInfoProducts_selector, ProductsName_selector, ProductsDec_selector, ProductsPrice_selector, ProductsAddtoCart_selector,20);
		this.log.info("Successfully clicked 4 Random products");
		String cartIcon_selector=this.sc.validate_configureData("addToCartFalseScenario", "cartIcon");
		boolean cardIcon=this.sc.buttonClick(cartIcon_selector, 20);
		if(cardIcon==true)
		{
			String cartCount_selector=this.sc.validate_configureData("addToCartFalseScenario", "cartCount");
			String count=this.sc.getText(cartCount_selector, 30);
			int cardCount=Integer.parseInt(count);
			String productCount_selector=this.sc.validate_configureData("addToCartFalseScenario", "productCount");
			int productCount=this.sc.productsCount(productCount_selector, 20);
			if(cardCount==productCount)
			{
				this.log.info("Cart feature is working as we expected ");
				String cartItem1_selector=this.sc.validate_configureData("addToCartFalseScenario", "cartItem1");
				String cartButton_selector=this.sc.validate_configureData("addToCartFalseScenario", "cart_button");
				String cartItem2_selector=this.sc.validate_configureData("addToCartFalseScenario", "cartItem2");
				
				String  item_status = this.sc.removeProduct(cartItem1_selector, cartButton_selector, cartItem2_selector, 5);
				
				String iconStatus_selector=this.sc.validate_configureData("addToCartFalseScenario", "icon_status");
				String icon_status=this.sc.getText(iconStatus_selector, 5);
				// icon icon_status
				// compare item_status with icon_status
				if(item_status==null && icon_status == null)
				{
					this.log.info("TEST CASE PASSED for add to cart false scenario");
					String back_selector=this.sc.validate_configureData("addToCartFalseScenario", "back");
					boolean homePage=this.sc.buttonClick(back_selector, 10);
					if(homePage==true)
					{
						Assert.assertTrue(true);
						
					}
					else
					{
						this.log.info("Continue to Shopping button not clicked");
						Assert.fail("Continue to Shopping button not clicked");
					}
				}
				else
				{
					this.log.error("TEST CASE FAILED  For add to cart false scenario");
					Assert.fail("TEST CASE FAILED  For add to cart false scenario");
				}
			}
			else
			{
				this.log.error("Cart feature is  not working as we expected ");
				Assert.fail("Cart feature is  not working as we expected");
			}
		}
		else
		{
			this.log.error("Cart Icon failed to click");
			Assert.fail("Cart Icon failed to click");
		}	
	}

	@Test(priority = 5)
	public void validateCheckoutFalseScenario()
	{
		String cartIconButton_selector=this.sc.validate_configureData("validateCheckoutFalseScenario", "cartIconButton");
		boolean cartIcon_click=this.sc.buttonClick(cartIconButton_selector, 20);
		if(cartIcon_click==true)
		{
			this.log.info("Cart icon clicked directly without adding any items to cart");
			
			String checkOutButton_selector=this.sc.validate_configureData("validateCheckoutFalseScenario", "checkOutButton");
			boolean checkOut_click=this.sc.buttonClick(checkOutButton_selector, 20);
			if(checkOut_click==true)
			{
				this.log.info("Check out buttton clicked successfully");
				String firstName_selector=this.sc.validate_configureData("validateCheckoutFalseScenario", "firstName");
				boolean firstName=this.sc.sendKeys(firstName_selector, "Srinivas", 10);
				if(firstName==true)
				{
					String lastName_selector=this.sc.validate_configureData("validateCheckoutFalseScenario", "lastName");
					boolean lastName=this.sc.sendKeys(lastName_selector, "kondapally", 20);
					if(lastName==true)
					{
						String postal_selector=this.sc.validate_configureData("validateCheckoutFalseScenario", "postCode");
						boolean postCode=this.sc.sendKeys(postal_selector, "5000085", 10);
						if(postCode==true)
						{
							String continueButton_selector=this.sc.validate_configureData("validateCheckoutFalseScenario", "continueButton");
							boolean continueButton=this.sc.buttonClick(continueButton_selector, 10);
							if(continueButton==true)
							{
								String total_selector=this.sc.validate_configureData("validateCheckoutFalseScenario", "totalPrice");
								double total=this.sc.priceSeparation(total_selector, 20);
								if(total==0.00)
								{
									String finishButton_selector=this.sc.validate_configureData("validateCheckoutFalseScenario", "finishButton");
									boolean finish=this.sc.buttonClick(finishButton_selector, 30);
									if(finish==true)
									{											
										this.log.info("Finish button successfully clicked");	
										String orderPlaced_selector=this.sc.validate_configureData("validateCheckoutFalseScenario", "orderPlaced");
										boolean orderPlaced=this.sc.msgValidation(orderPlaced_selector, 20);
										if(orderPlaced==true)
										{
											this.log.info("Your order successfully placed !!");
											this.log.info("TEST CASE FAILED for validate Checkout False Scenario ");
											String homePage_selector=this.sc.validate_configureData("validateCheckoutFalseScenario", "homePage");
											boolean navigation_main_page=this.sc.buttonClick(homePage_selector, 20);
											if(navigation_main_page==true)
											{
//												return navigation_main_page;
												Assert.assertTrue(navigation_main_page);
											}
											else
											{
												Assert.fail("Failed to navigated to home page");
//												return false;
											}
										}
										else
										{
											this.log.error("TEST CASE PASSED for validate Checkout False Scenario");
											Assert.fail("TEST CASE PASSED for validate Checkout False Scenario");
										}
									}
									else
									{
										this.log.error("Finish button failed to login");
										Assert.fail("Finish button failed to login");
									}										
								}
								else
								{
									this.log.error("Total is not zero");
									Assert.fail("Total is not zero");
								}
							}
							else
							{
								this.log.error("Continue button failed to clicked");
								Assert.fail("Continue button failed to clicked");
							}
						}
						else
						{
							this.log.error("Postal code not entered");
							Assert.fail("Postal code not entered");
						}
					}
					else
					{
						this.log.error("Last name not entered");
						Assert.fail("Last name not entered");
					}
				}
				else
				{
					this.log.error("First name not entered");
					Assert.fail("First name not entered");
				}
			}
			else
			{
				this.log.error("Check out button failed to clicked");
				Assert.fail("Check out button failed to clicked");
			}
		}
		else
		{
			this.log.error("Cart Icon failed to clicked");
			Assert.fail("Cart Icon failed to clicked");
		}
	}
	
	@Test(priority = 6)
	public void validateCheckoutInformation()
	{
		String cartIconButton_selector=this.sc.validate_configureData("validateCheckoutInformation", "cartIconButton");
		boolean cartIcon_click=this.sc.buttonClick(cartIconButton_selector, 20);
		if(cartIcon_click==true)
		{
			this.log.info("Cart icon clicked directly without adding any items to cart");
			String checkeoutButton_selector=this.sc.validate_configureData("validateCheckoutInformation", "checkoutButton");
			boolean checkOut_click=this.sc.buttonClick(checkeoutButton_selector, 20);
			if(checkOut_click==true)
			{
				this.log.info("Check out buttton clicked successfully");
				String continueButton_selector=this.sc.validate_configureData("validateCheckoutInformation", "continueButton");
				boolean continueButton=this.sc.buttonClick(continueButton_selector, 10);
				if(continueButton==true)
				{
					this.log.info("Continue button clicked successfully");
					String errorValidation_selector=this.sc.validate_configureData("validateCheckoutInformation", "errorValidation");
					boolean validation=this.sc.msgValidation(errorValidation_selector, 20);
					if(validation==true)
					{
						this.log.info("Its shows error, TEST CASE PASSED for validate Checkout Information");
						Assert.assertTrue(true);
						String cancelButton_selector=this.sc.validate_configureData("validateCheckoutInformation", "cancelButton");
						this.sc.buttonClick(cancelButton_selector, 10);
						
						String continueShopButton_selector=this.sc.validate_configureData("validateCheckoutInformation", "continueShopButton");
						Assert.assertTrue(this.sc.buttonClick(continueShopButton_selector, 10));
					}
					else
					{
						this.log.error("TEST CASE FAILED for validate Checkout Information");
						Assert.fail("TEST CASE FAILED for validate Checkout Information");
					}
				}
				else
				{
					this.log.error("Continue button not clicked");
					Assert.fail("Continue button not clicked");
				}					
			}
			else
			{
				this.log.error("Check out button not clicked");
				Assert.fail("Check out button not clicked");
			}
		}
		else
		{
			this.log.error("Cart icon not clicked");
			Assert.fail("Cart icon not clicked");
		}
	}
	
	@Test(priority = 7)
	public void validateProductsSorting()
	{
		String sortingProducts_selector=this.sc.validate_configureData("validateProductsSorting", "sortingProducts");
		LinkedHashMap before_sorting_products=this.sc.product(sortingProducts_selector, 40);
		for(int i =1; i < 4; i++)
		{
			String sortingOption_selector=this.sc.validate_configureData("validateProductsSorting", "sortOptions");
			String sort_option=this.sc.sortingDropDown(sortingOption_selector, 10, i); 
			if(sort_option != null)
			{
				String afProducts_selector=this.sc.validate_configureData("validateProductsSorting", "products");
				LinkedHashMap afproducts=this.sc.product(afProducts_selector, 40);
				boolean status = false;
				for(Object afselect: afproducts.keySet())
				{			
					LinkedHashMap afdata=(LinkedHashMap) afproducts.get(afselect);
					LinkedHashMap bfdata = (LinkedHashMap) before_sorting_products.get(afselect);
				    if (!afdata.equals(bfdata))
				    {
				    	status = true;
				    }
				    else
				    {
				    	status = false;
				    }
				}
				if (status == true)
				{
					this.log.info("Successfully sorted the product names based on "+sort_option);
					this.log.info("TEST CASE PASSED For validate Products Sorting");
					Assert.assertTrue(true);
				}
				else
				{
					this.log.error("Failed to sorted the product names based on "+sort_option);
					this.log.error("TEST CASE FAILED For validate Products Sorting");
					Assert.fail("TEST CASE FAILED For validate Products Sorting");;
				}
			}
			else
			{
				this.log.error("Not selected product option "+sort_option);
				Assert.fail("Not selected product option "+sort_option);
				
			}
		}
	}
	
	@Test(priority = 8)
	public void validateSocialIcons()
	{
		String twitter_selector= this.sc.validate_configureData("validateSocialIcons", "twitter");
		String facebook_selector=this.sc.validate_configureData("validateSocialIcons", "facebook");
		String linkedIn_selector=this.sc.validate_configureData("validateSocialIcons", "linkedIn");
		ArrayList al= new ArrayList();
		
		al.add(twitter_selector); al.add(facebook_selector); al.add(linkedIn_selector);
		if(al.size()>0)
		{
			for(Object paths: al)			
			{
				boolean twitter_icon=this.sc.buttonClick(paths.toString(), 20);
				if(twitter_icon==true)
				{
					this.log.info(paths+" icon clicked");
					Assert.assertTrue(true);
				}
				else
				{
					this.log.error(paths+" icon failed to clicking");
					Assert.fail(paths+" icon failed to clicking");
				}
			}
		}
		else
		{
			this.log.error("ArrayList is empty, not able to extract");
			Assert.fail("ArrayList is empty, not able to extract");
		}
		
		boolean twitter_page=this.sc.navigateToOtherSite(al);
		if(twitter_page==true)
		{
			this.log.info("successfully redirected to new window");
			this.log.info("TEST CASE PASSED For validate Social Icons");
			Assert.assertTrue(true);
		}
		else
		{
			this.log.error("Failed to open new window");
			this.log.info("TEST CASE FAILED For validate Social Icons");
			Assert.fail("TEST CASE FAILED For validate Social Icons");
		}
	}
	
	
	
	
	
	
	
}
