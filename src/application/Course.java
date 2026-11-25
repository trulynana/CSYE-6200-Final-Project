package application;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private final String courseNumber;
    private final String title;
    private final String subject;
    private final double hours;
    private final List<String> campuses;
    private final String description;
    private final List<List<Prerequisite>> prerequisites;

    public Course(String courseNumber, String title, String subject, double hours,
                  List<String> campuses, String description,
                  List<List<Prerequisite>> prerequisites) {
        this.courseNumber = courseNumber;
        this.title = title;
        this.subject = subject;
        this.hours = hours;
        this.campuses = new ArrayList<>(campuses);
        this.description = description;
        this.prerequisites = new ArrayList<>(prerequisites);
    }

    public String getCourseNumber() {
    	return courseNumber;
    }
    
    public String getTitle() {
    	return title;	
    }
    
    public String getSubject() {
    	return subject;
    }
    
    public double getHours() {
    	return hours;
    }
    
    public List<String> getCampuses() {
    	return campuses;
    }
    
    public String getDescription() {
    	return description;
    }
    
    public List<List<Prerequisite>> getPrerequisites() {
    	return prerequisites;
    }

    @Override
    public String toString() {
        return CourseReader.SUBJECT_CODES.get(subject) + ' ' + courseNumber + " - " + title
        		+ " (" + hours + " hrs) campuses=" + campuses + " prerequisites=" + prerequisites;
    }
}
