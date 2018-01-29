import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MyClass {
	/*
	 * Uses Selenium automated library for automatically downloading files from data.gov.in
	 * Intructions while running :-
	 * 1) Donnot maximize the screen of browser window (IMP)
	 * 2) Donnot interact with the browser, let it run in the background
	 * 3) Need a chromedriver.exe to be present Download from https://sites.google.com/a/chromium.org/chromedriver/
	 */

	public static void main(String[] args) throws InterruptedException {

		WebDriverWait wait;
		WebDriver driver;
		ArrayList<Integer> failed = new ArrayList<>();
		int totalResource = 0;
		
		// Used for running Selenium library
		String chromePath = "D:\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", chromePath);

		// create a new instance of the Chrome driver
		driver = new ChromeDriver();

		// Open the page we want to open
		driver.get("https://data.gov.in/catalog/hmis-sub-district-level-item-wise-monthly-report-" + Constants.STATE);

		// search pune
		WebElement search = driver.findElement(By.id("edit-title"));
		search.sendKeys(Constants.DISTRICT);
		// click search
		WebElement searchClick = driver.findElement(By.id("edit-submit-resource-detail-popup"));
		searchClick.click();

		// wait till page is loaded i.e 7secs
		Thread.sleep(7000);

		try {
			WebElement resourceNo = driver.findElement(By.className("view-header"));

			// give to total resources found and convert into int
			System.out.println(resourceNo.getText());
			String[] temp = resourceNo.getText().split(" ");
			totalResource = Integer.parseInt(temp[0]);
			
			int k = 0;

			// search all the blocks that need to be clicked
			do {

				// search and store all the blocks found within the page into a
				// list
				List<WebElement> blocks = null;
				try {
					blocks = driver.findElements(By.xpath("//div[@class='field-content ogpl-processed']"));
				} catch (Exception e) {
					System.out.println("No blocks found");
				}

				/*
				 * iterate through all blocks in each iteration the program
				 * clicks on the 'CSV' button which shows a form. The program
				 * then fills the form
				 */
				for (WebElement b : blocks) {
					k++;

					// click on the CSV button
					try {
						b.click();
						System.out.println(k);
					} catch (Exception e) {

					}

					// wait till the form show up
					Thread.sleep(2000);
					try {
						wait = new WebDriverWait(driver, 1);//
						wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmation_popup")));

						// fill the form i.e radio button,check box,name and
						// email
						try {
							WebElement radio = driver.findElement(
									By.xpath("//*[@id=\"edit-download-reasons\"]/div[2]/label/div"));
							radio.click();

							WebElement check = driver.findElement(
									By.cssSelector(".form-item.form-type-checkbox.form-item-reasons-d-Academia"));
							check.click();

							WebElement name = driver.findElement(By.xpath("//*[@id=\"edit-name-d\"]"));
							name.sendKeys(Constants.NAME);
							WebElement email = driver.findElement(By.xpath("//*[@id=\"edit-mail-d\"]"));
							email.sendKeys(Constants.USERNAME);

						} catch (Exception e) {
							System.out.println("Form fail");
							failed.add(k);
						}

						// click on submit button
						try {
							WebElement submit = driver.findElement(By.id("edit-submit"));
							submit.click();
						} catch (Exception e) {
							System.out.println("submit fail");
						}

					} catch (Exception e) {
						System.out.println("popup failed to launch");
						failed.add(k);
					}
					// switch window tabs
					ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
					driver.switchTo().window(tabs2.get(0));
				}
				////*[@id="edit-download-reasons"]/div[1]/label/div
				////*[@id="edit-download-reasons"]/div[2]/label/div
				

				// goto next page and repeat the same process
				// ---each page has 6 blocks so after downloading 6 files it
				// goes to next page
				WebElement next = driver.findElement(By.xpath("//a[@title='Go to next page']"));
				next.click();

				// wait 5 seconds to wait until next page opens
				Thread.sleep(5000);
			} while (true);

		} catch (Exception e) {
			System.out.println("Complete Downloading");
			
			driver.close();
			//driver.quit();
			
			System.out.println("Datasets failed to download are : "+failed.size());
			for(int i : failed){
				System.out.println("Page : "+i/6+" dataset : "+(i%totalResource) + 1);
			}
		}

	}
}
