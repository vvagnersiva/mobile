package Chapter11ExceptionsAndAssertions.Listing1119;

/*------------------------------------------------------------------------------
 * Oracle Certified Professional Java SE 7 Programmer Exams 1Z0-804 and 1Z0-805: 
 * A Comprehensive OCPJP 7 Certification Guide
 * by SG Ganesh and Tushar Sharma
------------------------------------------------------------------------------*/
import java.io.*;
import java.util.*;
class ThrowsClause1 {
	public static void main(String []args) {
		System.out.println("Reading an integer from the file 'integer.txt': ");
		Scanner consoleScanner = null;
		try {
			consoleScanner = new Scanner(new File("integer.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
System.out.println("You typed the integer value: " + consoleScanner.nextInt()); 
	}
}