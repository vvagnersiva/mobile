package Chapter05OODesignPrinciplesAndPatterns.Listing514;

/*------------------------------------------------------------------------------
 * Oracle Certified Professional Java SE 7 Programmer Exams 1Z0-804 and 1Z0-805: 
 * A Comprehensive OCPJP 7 Certification Guide
 * by SG Ganesh and Tushar Sharma
------------------------------------------------------------------------------*/

public interface ShapeFactory {
	 public Shape getShape(String sourceType);
}