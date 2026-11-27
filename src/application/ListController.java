package application;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ListController extends CourseReader {

	@FXML
	public ListView<Course> myCoursesListView;
	
	

	public void onLoadCLicked() throws IOException {
		// Clear the View of Courses
		myCoursesListView.getItems().clear();
	
		// Read all CSV files
		Map<String, List<Course>> catalog = new CourseReader().read(Path.of("csye_courses.csv"),
				Path.of("info_courses.csv"), Path.of("damg_courses.csv"), Path.of("tele_courses.csv"));

		CourseReader.courseCatalog = catalog;

		
		// Add Courses to Course Catalog with "Fetch Courses" Button
		for (List<Course> courses : catalog.values()) {
			myCoursesListView.getItems().addAll(courses);
		}

	}

	// Click the Clear Button to Clear courses 
	public void clearList() {
		myCoursesListView.getItems().clear();
	}

}
