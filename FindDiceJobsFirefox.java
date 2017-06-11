package FirefoxScripts;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class FindDiceJobsFirefox {
	
	static String [] linkList;
	static int validLinks = 0;
	
	public static void main(String[] args) throws InterruptedException {
		System.setProperty("webdriver.gecko.driver", "C:\\Users\\Craskan Avenger\\Downloads\\geckodriver.exe");
		
		WebDriver driver = new FirefoxDriver();
		
		driver.get("http://dice.com");
		driver.findElement(By.id("search-field-keyword")).sendKeys("QA");
		driver.findElement(By.id("search-field-location")).clear();
		driver.findElement(By.id("search-field-location")).sendKeys("Raleigh, NC");
		driver.findElement(By.id("search-field-location")).sendKeys(Keys.ENTER);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		WebElement temp = (driver.findElement(By.id("posiCountId")));
		int totalPositions = Integer.parseInt(temp.getText());
		linkList = new String[totalPositions];
		
		System.out.println("there are "+ totalPositions + " QA postions in Raleigh!");
		
		int currentLink = 0;
		int linkId = 0;
		int linksPerPage = 30;
		String searchPage = driver.getCurrentUrl();
		while(currentLink  < totalPositions)
		{
			if(linkId >= linksPerPage)
			{
				
				driver.findElement(By.className("icon-filled-arrow-66")).click();
				driver.findElement(By.className("icon-filled-arrow-66")).sendKeys(Keys.ENTER);
				Thread.sleep(5000);
				searchPage=driver.getCurrentUrl();
				linkId = 0;
			}
			else
			{
			inspectLink(driver,linkId,searchPage);	
			linkId++;
			currentLink++;		
			}
		}
		
		// TODO Auto-generated method stub

	}
	public static String grabText(WebDriver driver)
	{
		//"jobdescSec" is element needed
		
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		WebElement temp = (driver.findElement(By.id("jobdescSec")));
		return temp.getText();
		
	}
	public static boolean findYears(String [] words)
	{
		//right now, we don't know if this will pass, so it is set to false
		boolean returnable = true;
		
		int counter = 0;
		int len = words.length;
		
		String sentence = "";
		
		while(counter < len && returnable == true)
		{
			if(words[counter].length() >= 4)
			{	
				if(words[counter].substring(0, 4).equalsIgnoreCase("year") )
				{
					//find the year
					for(int i = 3; i > 0; i--)
					{
							sentence = sentence+words[counter-i];
					}
					for(int i = 0; i < 4; i++)
					{
						sentence = sentence+words[counter+i];
					}
					
					if(validateTrueOrFalse(sentence) == false)
					{
						returnable = false;
						System.out.println("This is False");
					}
					sentence = "";
					
				}
			}	
			counter++;
		}
		System.out.println("return from find years " + returnable);
		return returnable;
	}
	public static boolean validateTrueOrFalse(String sentence)
	{
		//we assume that this job will be a good fit unless it fails a later test
		boolean returnable = true;
		//is it important?
		if(sentence.contains("minimum") || sentence.contains("atleast") || sentence.contains("required") || sentence.contains("experience") || sentence.contains("requirements"))
		{
			System.out.println("gained access 1 ");
			//this is important
			//now to check if number greater than 3
			int len = sentence.length();
			for (int i = 0; i < len; i++)
			{
				if(Character.isDigit(sentence.charAt(i)))
				{
					
					if(Integer.parseInt(sentence.substring(i,i+1)) > 3)
					{
						//this test just failed, needed greater than 3\
						returnable = false;
					}
					else if(Integer.parseInt(sentence.substring(i,i+1)) == 1)
					{
						//let's make sure this isn't a 10
						//first make sure it isn't last character
						if(i < len)
						{
							if(Character.isDigit(sentence.charAt(i+1)))
							{
								//number is 10 or greater, fail
								returnable = false;
							}
						}
					}
				}	
			}
			System.out.println("this was " + returnable + sentence);
		}
		
		return returnable;
	}
	
	public static void inspectLink(WebDriver driver, int x, String searchPage)
	{
		System.out.println("inside the inspector");
		
		
		driver.findElement(By.id("position"+x)).click();
		
		
		String description = grabText(driver);

		String [] words = description.split("\\s");//splits the string based on whitespace  
			
		if(findYears(words) == true)
		{
			//it passes, collect link
			linkCollector(driver);
		}
		
		driver.get(searchPage);
		
	}
	public static void linkCollector (WebDriver driver)
	{
		linkList[validLinks] =  driver.getCurrentUrl();
		System.out.println("we have a valid link " + linkList[validLinks]);
		validLinks++; 
	}

}
