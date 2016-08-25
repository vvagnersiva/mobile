package Chapter08JavaIOFundamentals.Listing82;

/*------------------------------------------------------------------------------
 * Oracle Certified Professional Java SE 7 Programmer Exams 1Z0-804 and 1Z0-805: 
 * A Comprehensive OCPJP 7 Certification Guide
 * by SG Ganesh and Tushar Sharma
------------------------------------------------------------------------------*/
 import java.io.Console;
import java.util.Arrays;

// code to illustrate the use of readPassword method 
class Login {
	public static void main(String []args) {
		Console console = System.console();
		if(console != null) {
			String userName = null; 
			char[] password = null; 
			userName = console.readLine("Enter your username: "); 
			// typed characters for password will not be displayed in the screen 
			password = console.readPassword("Enter password: ");
// password is a char[]: convert it to a String first before comparing contents
if(userName.equals("scrat") && new String(password).equals("nuts")) {
// we're hardcoding username and password here for 
// illustration, don’t do such hardcoding in pratice! 
console.printf("login successful!");
			}
			else {
				console.printf("restart application and try again"); 
			}
			// "empty" the password since its use is over 
			Arrays.fill(password, ' '); 
		}
	}
}