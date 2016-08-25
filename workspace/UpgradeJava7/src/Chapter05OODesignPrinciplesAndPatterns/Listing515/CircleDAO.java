package Chapter05OODesignPrinciplesAndPatterns.Listing515;

/*------------------------------------------------------------------------------
 * Oracle Certified Professional Java SE 7 Programmer Exams 1Z0-804 and 1Z0-805: 
 * A Comprehensive OCPJP 7 Certification Guide
 * by SG Ganesh and Tushar Sharma
------------------------------------------------------------------------------*/
public interface CircleDAO {
	public void insertCircle(CircleTransfer circle);
	public CircleTransfer findCircle(int id);
	public void deleteCircle(int id);
}