package application;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseReader {
	static Map<String, List<Course>> courseCatalog;
	
	
	public static final Map<String, String> SUBJECT_CODES = Map.of(
	        "Computer Systems Engineering", "CSYE",
	        "Data Architecture Management", "DAMG",
	        "Information Systems Program", "INFO",
	        "Telecommunication Systems", "TELE");

    public static void main(String[] args) throws IOException {
    	// Read CSV files with courses for each subject and populate the course catalog
    		courseCatalog = new CourseReader().read(
                Path.of("csye_courses.csv"),
                Path.of("info_courses.csv"),
                Path.of("damg_courses.csv"),
                Path.of("tele_courses.csv"));

        // Display the course catalog
        for (Map.Entry<String, List<Course>> entry : courseCatalog.entrySet()) {
            String subject = entry.getKey();
            List<Course> courses = entry.getValue();

            System.out.println(subject + ": " + courses.size() + " courses");
            for (Course c : courses) {
                System.out.println(c);
            }
            System.out.println();
        }
        
        // TESTING: Getting one course
        List<Course> csye = courseCatalog.get("CSYE");
        Course first = csye.get(0);
        System.out.println("Getting one course...\n" + first);
    }

    /** Reads one or more CSV files and groups every course by its subject (e.g. "CSYE"). */
    public Map<String, List<Course>> read(Path... files) throws IOException {
        Map<String, List<Course>> subjectCourses = new HashMap<>();
        for (Path file : files) {
        	for (Course c : readFile(file)) {
        	    String code = SUBJECT_CODES.get(c.getSubject());
        	    
        	    List<Course> courseList = subjectCourses.get(code);
        	    if (courseList == null) {
        	    	courseList = new ArrayList<>();
        	        subjectCourses.put(code, courseList);
        	    }
        	    courseList.add(c);
        	}
        }
        return subjectCourses;
    }

    /** Read a CSV file and return an List of Course objects from courses in that file*/
    private List<Course> readFile(Path file) throws IOException {
        String text = readText(file).replace("\uFEFF", "").replace("\r\n", "\n");
        List<List<String>> rows = parseCSV(text);

        // Creating a lookup table mapping column name to column position
        Map<String, Integer> col = new HashMap<>();
        List<String> header = rows.get(0);
        for (int i = 0; i < header.size(); i++) {
            col.put(header.get(i), i);
        }

        List<Course> courses = new ArrayList<>();
        for (int r = 1; r < rows.size(); r++) {          // r = 1 skips the header row
            List<String> row = rows.get(r);
            if (String.join("", row).isBlank()) {
                continue;                                // skip empty rows
            }
            courses.add(new Course(
                    		get(row, col, "courseNumber"),
                    		get(row, col, "title"),
		                    get(row, col, "subject"),
		                    Double.parseDouble(get(row, col, "hours")),
		                    parseCampuses(get(row, col, "campus")),
		                    get(row, col, "description"),
		                    parsePrerequisites(get(row, col, "prerequisites"))));
        }
        
        return courses;
    }

    /** UTF-8, falling back to Windows-1252 (Excel's plain "CSV" export). */
    private static String readText(Path file) throws IOException {
        try {
            return Files.readString(file);
        } catch (CharacterCodingException e) {
            return new String(Files.readAllBytes(file), Charset.forName("windows-1252"));
        }
    }

    private static String get(List<String> row, Map<String, Integer> col, String name) {
        Integer i = col.get(name);
        if (i == null) {
            throw new IllegalArgumentException("CSV is missing column: " + name);
        }
        return i < row.size() ? row.get(i).trim() : "";
    }

    static List<String> parseCampuses(String value) {
        List<String> campuses = new ArrayList<>();
        for (String campus : value.split("\\|")) {
            if (!campus.isBlank()) {
                campuses.add(campus.trim());
            }
        }
        return campuses;
    }

    /** "A:B- | B:B- & C:B-" -> [[A:B-], [B:B-, C:B-]]  (outer = OR, inner = AND) */
    static List<List<Prerequisite>> parsePrerequisites(String value) {
        List<List<Prerequisite>> orOptions = new ArrayList<>();
        if (value.isBlank()) {
            return orOptions;  // no prerequisites
        }
        
        for (String orPart : value.split("\\|")) {
            List<Prerequisite> andGroup = new ArrayList<>();
            
            for (String item : orPart.split("&")) {
                String[] prerequisite = item.split(":");
                String courseNumber = prerequisite[0].trim();
                String minGrade = prerequisite[1].trim();
                andGroup.add(new Prerequisite(courseNumber, minGrade));
            }
            
            orOptions.add(andGroup);
        }
        return orOptions;
    }

    /** Turns the CSV text into a list of rows, where each row is a list of cell values */
    static List<List<String>> parseCSV(String text) {
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < text.length() && text.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                row.add(field.toString());
                field.setLength(0);
            } else if (ch == '\n' && !inQuotes) {
                row.add(field.toString());
                field.setLength(0);
                rows.add(row);
                row = new ArrayList<>();
            } else {
                field.append(ch);
            }
        }
        if (field.length() > 0 || !row.isEmpty()) {
            row.add(field.toString());
            rows.add(row);
        }
        return rows;
    }
}