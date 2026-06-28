package utilities;

import java.io.IOException;
import org.testng.annotations.DataProvider;

/**
 * Shared Data Provider Suite for TestNG Execution Frameworks.
 * Centralizes the data extraction pipeline from external Excel workbooks
 * to drive data-driven tests.
 */
public class DataProviders {

	// ==========================================
	// DATA PROVIDER METHODS
	// ==========================================

	@DataProvider(name = "LoginData")
	public String[][] getData() throws IOException {

		// Establish relative pathway to Excel matrix storage source
		String path = ".//testData//Logindata.xlsx";

		// Initialize custom processing utility to scan workbook
		ExcelUtility xlutil = new ExcelUtility(path);

		// Calculate dimensional size of target table grid
		int totalrows = xlutil.getRowCount("Sheet1");
		int totalcols = xlutil.getCellCount("Sheet1", 1);

		// Instantiate two-dimensional array to store parsed cell rows
		String[][] logindata = new String[totalrows][totalcols];

		// Core extraction loop - reads file row by row, cell by cell
		for (int i = 1; i <= totalrows; i++) {
			for (int j = 0; j < totalcols; j++) {
				// Shift array index down by 1 since excel rows start at index 1
				logindata[i - 1][j] = xlutil.getCellData("Sheet1", i, j);
			}
		}

		return logindata;
	}
}