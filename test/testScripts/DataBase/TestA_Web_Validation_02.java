package testScripts.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestA_Web_Validation_02 
{
	protected static WebDriver driver;
	protected static Connection conn= null;
	protected static Statement stmt = null;
	protected static ResultSetMetaData rsmd= null;
	protected static ResultSet rs1= null;
	protected static ResultSet rs2= null;
	protected static String query_Total_Row= "SELECT COUNT(*) AS TOTAL_ROW FROM TEST_A" ;
	protected static String query_ALL_RECORDS= "SELECT * FROM TEST_A";
	protected static int columnNumber ;
	protected static int rowNumber ;
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	@BeforeClass
	public static void setUp() 
	{
		System.out.println("********************************************************");
		System.out.println("Run started at :"+df.format(new Date()));
		System.out.println("Chrome Browser Test Environment created");
		System.setProperty("webdriver.chrome.driver", "D:/BrowserDriver/ChromeDriver/chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
	    options.addArguments("--start-maximized");
	    options.addArguments("--disable-web-security");
	    options.addArguments("--no-proxy-server");
	    
	    Map<String, Object> prefs = new HashMap<String, Object>();
	    prefs.put("credentials_enable_service", false);
	    prefs.put("profile.password_manager_enabled", false);
	    prefs.put("profile.default_content_setting_values.notifications", 2);
	    options.setExperimentalOption("prefs", prefs);
		driver = new ChromeDriver(options);
		driver.navigate().to("file:///E:/Eclipse_selenium/country.html");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}
	
	@Test
	public void webTableVerify() throws SQLException
	{
		int totalColumn = driver.findElements(By.xpath("//*[@id='t01']/tbody/tr[1]/th")).size();
		for(int i=1;i<=totalColumn;i++)
		{
			String columnName = driver.findElement(By.xpath("//*[@id='t01']/tbody/tr[1]/th["+i+"]")).getText();
			System.out.print(columnName+"   \t");
		}
		
		System.out.println("\n---------------------------------------------------------------------------");
		int totalRow = driver.findElements(By.xpath("//*[@id='t01']/tbody/tr")).size();
		for(int j=2;j<=totalRow;j++)
		{		
			String column1_data = driver.findElement(By.xpath("//*[@id='t01']/tbody/tr["+j+"]/td[1]")).getText();
			String column2_data = driver.findElement(By.xpath("//*[@id='t01']/tbody/tr["+j+"]/td[2]")).getText();
			String column3_data = driver.findElement(By.xpath("//*[@id='t01']/tbody/tr["+j+"]/td[3]")).getText();
			System.out.println(column1_data+"\t\t|\t"+column2_data+"\t|  \t"+column3_data);
		}
		System.out.println("---------------------------------------------------------------------------");
		System.out.println("Total Column Displayed in WebPage :"+totalColumn);
		System.out.println("Total Data Row Displayed in WebPage(skipping header):"+(totalRow-1));
		

/*collecting all data from webtable into ArrayList for later Comparison with DB arraylist, total row 6 and column 3
 * row index j = 1 (for column header row) so started from j = 2
 * Column index starts from i=1
 * //*[@id='t01']/tbody/tr["+j+"]/td["+i+"]") this Dynamic Xpath take parameter and count and store value for 
 * total 18(3*6) table cell available
 */

		List<String> webPage_data = new ArrayList<String>();
		for(int j=2;j<=totalRow;j++) 
		{
			for(int i=1;i<=totalColumn;i++)
			{
				String row_data= driver.findElement(By.xpath("//*[@id='t01']/tbody/tr["+j+"]/td["+i+"]")).getText();
				webPage_data.add(row_data);
			}			
		}
		
//doing database connection
		try
		{
			System.out.println("Connecting to Database..."+"\n"+df.format(new Date())+""
					+ "\n----------------------------------------------------------------------------------------");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","SYSTEM","Russia1234321");													
			System.out.println("Connected to Oracle11g Database");
			
		}
		catch(SQLException|ClassNotFoundException ex)
		{
			System.err.println("Failed to connect to DB/ Check properties file");
			ex.printStackTrace();
		}
		List<String> dataBase_data = new ArrayList<String>();
		stmt = conn.createStatement();
		rs1 = stmt.executeQuery(query_ALL_RECORDS);
		rsmd = rs1.getMetaData();
		columnNumber = rsmd.getColumnCount();
		while(rs1.next())
		{
			for(int i=1; i<= columnNumber; i++)
			{
				dataBase_data.add(rs1.getString(i));
			}
		}
		rs2 = stmt.executeQuery(query_Total_Row);
		while(rs2.next())
		{
			rowNumber = rs2.getInt("TOTAL_ROW");
		}
// To focus back to rsmd rs1 resultset else it will show only one column for rs2
		rs1 = stmt.executeQuery(query_ALL_RECORDS);
		rsmd = rs1.getMetaData(); 
// Asserting webtable content vs DataBase table Content
		Assert.assertEquals("Failed to Match", dataBase_data, webPage_data);
		
// Asserting webtable total row and columns against database		
		Assert.assertEquals("Row Mismatch", rowNumber, (totalRow-1));
		Assert.assertEquals("Column Mismatch", totalColumn, columnNumber);

		
		String columnName1 = driver.findElement(By.xpath("//*[@id='t01']/tbody/tr[1]/th[1]")).getText();
		String columnName2 = driver.findElement(By.xpath("//*[@id='t01']/tbody/tr[1]/th[2]")).getText();
		String columnName3 = driver.findElement(By.xpath("//*[@id='t01']/tbody/tr[1]/th[3]")).getText();
		
// Asserting all webpage column name against database column heading
		Assert.assertEquals("1st Column Name Mismatch",columnName1,rsmd.getColumnName(1));
		Assert.assertEquals("2nd Column Name Mismatch",columnName2,rsmd.getColumnName(2));
		Assert.assertEquals("3rd Column Name Mismatch",columnName3,rsmd.getColumnName(3));
		

// Closing DB connection
		if(conn!=null)
		{
			conn.commit();
			conn.close();
			System.out.println("\n----------------------------------------------------------------------------------------"+""
					+ "\n"+df.format(new Date()));
			System.out.println("Disconnected from Oracle11g Database");
		}
	}
	
	@AfterClass

	public static void tearDown() throws Exception
	{
		if(driver!=null)
		{
			System.out.println("Closing the Browser");
			System.out.println("Run ended at :"+df.format(new Date()));
			System.out.println("********************************************************");
			driver.quit();
		}
	}

}
