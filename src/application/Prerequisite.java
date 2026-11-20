package application;

public class Prerequisite {
    private final String courseNumber;
    private final String minGrade;

    public Prerequisite(String courseNumber, String minGrade) {
        this.courseNumber = courseNumber;
        this.minGrade = minGrade;
    }

    public String getCourseNumber() {
    	return courseNumber;
    }
    
    public String getMinGrade() {
    	return minGrade;	
    }

    @Override
    public String toString() {
        return courseNumber + ": " + minGrade;
    }
}