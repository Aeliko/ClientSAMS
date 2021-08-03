import java.util.ArrayList;

/**
 * This class is for handling student names and marks
 * 
 * @author Aero
 *
 */
public class Student {
	private String name = "";

	// Using ArrayList to store varying numbers of marks, float because marks do not
	// need to have such a high precision
	private ArrayList<Float> marks;

	/**
	 * This is the default Student constructor
	 */
	public Student() {
		this.name = "";
		this.marks = new ArrayList<Float>();
	}

	/**
	 * This is a Student constructor
	 * 
	 * @param name  The name of the student
	 * @param marks The list of marks of the student
	 */
	public Student(String name, ArrayList<Float> marks) {
		this.name = name;
		this.marks = marks;
	}

	/**
	 * 
	 * @return the name of the student
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the student
	 * 
	 * @param name the name of the student
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return the list of marks of the student
	 */
	public ArrayList<Float> getMarks() {
		return marks;
	}

	/**
	 * Sets the marks of the student
	 * 
	 * @param marks the list of marks of the student
	 */
	public void setMarks(ArrayList<Float> marks) {
		this.marks = marks;
	}

	/**
	 * Adds a mark to the list of marks of the student
	 * 
	 * @param marks the mark of the student
	 */
	public void addMark(float marks) {
		this.marks.add(marks);
	}

	@Override
	public String toString() {
		String output = "";
		output += "Name: " + this.name + "\tMarks: ";
		for (float mark : this.marks) {
			output += mark + " ";
		}
		return output;
	}

}
