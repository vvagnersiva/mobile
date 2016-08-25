package Chapter05OODesignPrinciplesAndPatterns.Listing512;

/*------------------------------------------------------------------------------
 * Oracle Certified Professional Java SE 7 Programmer Exams 1Z0-804 and 1Z0-805: 
 * A Comprehensive OCPJP 7 Certification Guide
 * by SG Ganesh and Tushar Sharma
------------------------------------------------------------------------------*/

public class ShapeFactory {
	 public static Shape getShape(String sourceType) {
		switch(sourceType) {
		case "Circle":
			return new Circle(10, 10, 20);
		case "Rectangle":
			return new Rectangle(10, 20);
		}
		return null;
	}
}