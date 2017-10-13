package testScripts.DataBase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestA_DB_Validation_01
{
	protected static Connection conn= null;
	protected static Statement stmt= null;
	protected static ResultSetMetaData rsmd= null;
	protected static ResultSet rs1= null;
	protected static ResultSet rs2= null;
	protected static String query_Total_Row= "SELECT COUNT(*) AS TOTAL_ROW FROM TEST_A" ;
	protected static String query_ALL_RECORDS= "SELECT * FROM TEST_A";
	protected static int columnNumber ;
	protected static int rowNumber ;
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	

	public static void main(String[] args) throws SQLException, IOException 
	{
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


		
		List<String> data = new ArrayList<String>();
			stmt = conn.createStatement();
			rs1 = stmt.executeQuery(query_ALL_RECORDS);
			rsmd = rs1.getMetaData();
			columnNumber = rsmd.getColumnCount();
			while(rs1.next())
			{
				for(int i=1; i<= columnNumber; i++)
				{
					data.add(rs1.getString(i));
				}
			}
			System.out.println("\t   "+rsmd.getColumnName(1)+"   "+rsmd.getColumnName(2)+"\t\t   "+rsmd.getColumnName(3));
			System.out.println("\t   ......................................................");
			rs2 = stmt.executeQuery(query_Total_Row);
			while(rs2.next())
			{
				rowNumber = rs2.getInt("TOTAL_ROW");
			}
				
// fetching total row wise data| if row = 3 and column = 4 then total fields will be =3*4 = 12
				
			for(int m=0;m<=rowNumber*columnNumber;m++)
			{
				for(int i=1; i<= rowNumber; i++)
				{
				System.out.println(i+" no Row : "+data.get(m)+"\t\t|  "+data.get(m+1)+"\t\t|  "+data.get(m+2)+"\t|");
				++m;
				++m;
				++m;
				}
				System.out.println("\t   ......................................................");
			}
			
			System.out.println("Total Columns :"+columnNumber);
			System.out.println("Total Rows :"+rowNumber);
			

			if(conn!=null)
			{
				conn.commit();
				conn.close();
				System.out.println("\n----------------------------------------------------------------------------------------"+""
						+ "\n"+df.format(new Date()));
				System.out.println("Disconnected from Oracle11g Database");
			}
/**
 * remember after adding data to list, now
 * data.get(0) = will return (1st Row)-1st Column 
 * data.get(1) = will return (1st Row)-2nd Column
 * will print row wise from starting to end row 
 */
		
	}
}
