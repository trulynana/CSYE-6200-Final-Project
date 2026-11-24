package application;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class CoursePathView extends Application {

	private static final String DARK = "#111827";
	private static final String GRAY = "#6B7280";

	// The course currently selected for demonstration.
	// Nana's page can pass a different course number later.
	private String selectedCourseNumber = "INFO 7390";

	// DEMO DATA ONLY: Student's completed courses and grades.
	// Replace this with actual student input during integration.
	private final Map<String, String> completedCourses = new HashMap<>();

	private final Map<String, Course> courseIndex = new HashMap<>();

	private Map<String, List<Course>> courseCatalog;

	private static class PathStep {
		Course course;
		String requiredGrade;

		PathStep(Course course, String requiredGrade) {
			this.course = course;
			this.requiredGrade = requiredGrade;
		}
	}

	// Optional constructor for future page integration.
	public CoursePathView() {
	}

	public CoursePathView(String courseNumber) {
		this.selectedCourseNumber = courseNumber;
	}

	@Override
	public void start(Stage stage) {

		// Temporary sample student record
		completedCourses.put("INFO 5100", "A");

		try {
			loadCourseData();
		} catch (IOException | RuntimeException ex) {
			showMessage("Could not read course data", ex.getMessage());
			return;
		}

		Course target = findCourse(selectedCourseNumber);

		if (target == null) {
			showMessage("Course not found", "Cannot find " + selectedCourseNumber + " in the CSV course catalog.");
			return;
		}

		List<PathStep> path = buildCoursePath(target);

		boolean eligible = prerequisitesSatisfied(target);

		// ================= PAGE =================

		VBox page = new VBox(0);
		page.setStyle("-fx-background-color: white;");

		// ================= HEADER =================

		SVGPath hat = new SVGPath();
		hat.setContent("M2 8 L12 3 L22 8 L12 13 Z " + "M6 10 V16 Q12 20 18 16 V10 " + "M22 8 V16");
		hat.setFill(Color.TRANSPARENT);
		hat.setStroke(Color.web("#2563EB"));
		hat.setStrokeWidth(1.6);
		hat.setScaleX(1.0);

		Label logoText = new Label("SEIS course recommender");
		logoText.setStyle("-fx-font-size: 14px;" + "-fx-font-weight: bold;");

		HBox logo = new HBox(11, hat, logoText);
		logo.setAlignment(Pos.CENTER_LEFT);

		Label pageTitle = new Label("Path to a course");
		pageTitle.setStyle("-fx-font-size: 12px;" + "-fx-text-fill: #64748B;");

		Region headerSpace = new Region();
		HBox.setHgrow(headerSpace, Priority.ALWAYS);

		HBox header = new HBox(10, logo, headerSpace, pageTitle);
		header.setAlignment(Pos.CENTER_LEFT);
		header.setPadding(new Insets(18, 26, 18, 26));

		Separator headerLine = new Separator();

		// ================= CONTENT =================

		VBox content = new VBox(20);
		content.setPadding(new Insets(22, 28, 25, 28));

		Button backButton = new Button("← Back to results");

		backButton.setStyle("-fx-background-color: white;" + "-fx-border-color: #D1D5DB;" + "-fx-border-radius: 7;"
				+ "-fx-background-radius: 7;" + "-fx-text-fill: " + DARK + ";" + "-fx-font-size: 12px;"
				+ "-fx-padding: 5 12;" + "-fx-cursor: hand;");

		backButton.setOnAction(e -> openCourseList(stage));

		// ================= TARGET COURSE =================

		Label courseName = new Label(fullNumber(target) + ": " + target.getTitle());

		courseName.setWrapText(true);
		courseName.setStyle("-fx-font-size: 18px;" + "-fx-font-weight: bold;" + "-fx-text-fill: " + DARK + ";");

		Label eligibility = new Label(eligible ? "Eligible" : "Not eligible yet");

		eligibility.setStyle(eligible ? badgeStyle("#DCFCE7", "#166534") : badgeStyle("#FEE2E2", "#B91C1C"));

		Region titleSpacer = new Region();
		HBox.setHgrow(titleSpacer, Priority.ALWAYS);

		HBox courseTitleRow = new HBox(12, courseName, titleSpacer, eligibility);
		courseTitleRow.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(courseName, Priority.ALWAYS);

		int remaining = countRemaining(path, target);

		String summaryText;

		if (eligible) {
			summaryText = "You meet the listed prerequisites for this course.";
		} else if (remaining == 1 && path.size() >= 2) {
			String next = fullNumber(path.get(path.size() - 2).course);

			summaryText = "You're 1 course away. Take " + next + " first, then this course.";
		} else {
			summaryText = "Complete the required courses in the path " + "below before taking this course.";
		}

		Label summary = new Label(summaryText);
		summary.setWrapText(true);
		summary.setStyle("-fx-font-size: 12px;" + "-fx-text-fill: #475569;");

		VBox courseInfo = new VBox(9, courseTitleRow, summary);

		// ================= PREREQUISITE RULE =================

		Label ruleTitle = new Label("Prerequisite rule");
		ruleTitle.setStyle("-fx-font-size: 12px;" + "-fx-text-fill: " + GRAY + ";");

		Label rule = new Label(formatPrerequisites(target));
		rule.setWrapText(true);
		rule.setStyle("-fx-font-size: 13px;" + "-fx-text-fill: " + DARK + ";");

		VBox ruleCard = new VBox(6, ruleTitle, rule);
		ruleCard.setPadding(new Insets(16));
		ruleCard.setStyle("-fx-background-color: #F8F9FA;" + "-fx-background-radius: 8;");

		// ================= PATH TITLE =================

		Label pathTitle = new Label("Your path");
		pathTitle.setStyle("-fx-font-size: 14px;" + "-fx-font-weight: bold;");

		// ================= TIMELINE =================

		Pane iconColumn = new Pane();
		iconColumn.setMinWidth(25);
		iconColumn.setPrefWidth(25);
		iconColumn.setMaxWidth(25);

		Pane cardsColumn = new Pane();
		HBox.setHgrow(cardsColumn, Priority.ALWAYS);

		List<Double> positions = new ArrayList<>();

		for (int i = 0; i < path.size(); i++) {
			if (i == 0) {
				positions.add(0.0);
			} else if (i == 1) {
				positions.add(80.0);
			} else {
				positions.add(80.0 + (i - 1) * 155.0);
			}
		}

		// Line behind the icons.
		if (positions.size() > 1) {
			double lastY = positions.get(positions.size() - 1);

			Region timelineLine = new Region();
			timelineLine.setStyle("-fx-background-color: #D1D5DB;");

			timelineLine.resizeRelocate(11.5, 12.5, 2, lastY);

			iconColumn.getChildren().add(timelineLine);
		}

		int stepNumber = 1;

		for (int i = 0; i < path.size(); i++) {

			PathStep step = path.get(i);
			Course course = step.course;

			boolean isGoal = i == path.size() - 1;

			String code = fullNumber(course);

			boolean completed = hasRequiredGrade(code, step.requiredGrade);

			double y = positions.get(i);

			Node icon;
			Node card;

			if (isGoal) {
				icon = createGoalIcon();
				card = createGoalCard(course);

			} else if (completed) {
				icon = createCompletedIcon();

				String neededFor = i + 1 < path.size() ? fullNumber(path.get(i + 1).course) : fullNumber(target);

				card = createCompletedRow(course, completedCourses.get(code), neededFor);

			} else {
				icon = createNumberIcon(stepNumber++);
				card = createNextCard(course);
			}

			icon.relocate(0, y);
			card.relocate(0, y);

			iconColumn.getChildren().add(icon);
			cardsColumn.getChildren().add(card);

			if (card instanceof Region) {
				Region cardRegion = (Region) card;

				cardRegion.prefWidthProperty().bind(cardsColumn.widthProperty());
			}
		}

		double finalHeight = positions.isEmpty() ? 100 : positions.get(positions.size() - 1) + 110;

		iconColumn.setPrefHeight(finalHeight);
		cardsColumn.setPrefHeight(finalHeight);

		HBox timeline = new HBox(14, iconColumn, cardsColumn);
		timeline.setAlignment(Pos.TOP_LEFT);

		// ================= FOOTER =================

		Separator footerLine = new Separator();

		Label footer = new Label("ⓘ  This is a planning aid. Confirm prerequisites, "
				+ "waivers, and course availability with the " + "official catalog or your advisor.");

		footer.setWrapText(true);
		footer.setStyle("-fx-text-fill: #9CA3AF;" + "-fx-font-size: 11px;");

		VBox footerBox = new VBox(12, footerLine, footer);

		content.getChildren().addAll(backButton, courseInfo, ruleCard, pathTitle, timeline, footerBox);

		page.getChildren().addAll(header, headerLine, content);

		ScrollPane scrollPane = new ScrollPane(page);
		scrollPane.setFitToWidth(true);
		scrollPane.setStyle("-fx-background-color: white;" + "-fx-background: white;");

		Scene scene = new Scene(scrollPane, 850, 720);

		stage.setTitle("SEIS Course Recommender");
		stage.setScene(scene);
		stage.show();
	}

	// ================= CSV DATA =================

	private void loadCourseData() throws IOException {

		CourseReader reader = new CourseReader();

		courseCatalog = reader.read(Path.of("csye_courses.csv"), Path.of("info_courses.csv"));

		courseIndex.clear();

		for (Map.Entry<String, List<Course>> entry : courseCatalog.entrySet()) {

			String subjectCode = entry.getKey();

			if (subjectCode == null) {
				continue;
			}

			for (Course course : entry.getValue()) {
				String key = subjectCode + " " + course.getCourseNumber();

				courseIndex.put(key.trim().toUpperCase(), course);
			}
		}
	}

	private Course findCourse(String courseNumber) {
		if (courseNumber == null) {
			return null;
		}

		return courseIndex.get(courseNumber.trim().toUpperCase());
	}

	private String fullNumber(Course course) {

		String subjectCode = CourseReader.SUBJECT_CODES.get(course.getSubject());

		return subjectCode + " " + course.getCourseNumber();
	}

	// ================= PREREQUISITE LOGIC =================

	private boolean hasRequiredGrade(String courseNumber, String requiredGrade) {

		String actual = completedCourses.get(courseNumber);

		if (actual == null) {
			return false;
		}

		return gradeValue(actual) >= gradeValue(requiredGrade);
	}

	private int gradeValue(String grade) {

		if (grade == null || grade.isBlank()) {
			return 0;
		}

		switch (grade.trim().toUpperCase()) {
		case "A+":
			return 12;
		case "A":
			return 11;
		case "A-":
			return 10;
		case "B+":
			return 9;
		case "B":
			return 8;
		case "B-":
			return 7;
		case "C+":
			return 6;
		case "C":
			return 5;
		case "C-":
			return 4;
		case "D+":
			return 3;
		case "D":
			return 2;
		case "D-":
			return 1;
		default:
			return 0;
		}
	}

	private boolean prerequisitesSatisfied(Course course) {

		List<List<Prerequisite>> options = course.getPrerequisites();

		if (options == null || options.isEmpty()) {
			return true;
		}

		// Outer list = OR, inner list = AND.
		for (List<Prerequisite> group : options) {

			boolean groupSatisfied = true;

			for (Prerequisite prerequisite : group) {
				if (!hasRequiredGrade(prerequisite.getCourseNumber(), prerequisite.getMinGrade())) {

					groupSatisfied = false;
					break;
				}
			}

			if (groupSatisfied) {
				return true;
			}
		}

		return false;
	}

	private String formatPrerequisites(Course course) {

		List<List<Prerequisite>> options = course.getPrerequisites();

		if (options == null || options.isEmpty()) {
			return "No prerequisites required";
		}

		List<String> orParts = new ArrayList<>();

		for (List<Prerequisite> group : options) {

			List<String> andParts = new ArrayList<>();

			for (Prerequisite prerequisite : group) {
				andParts.add(prerequisite.getCourseNumber() + " with a minimum grade of " + prerequisite.getMinGrade());
			}

			String text = String.join(" AND ", andParts);

			if (group.size() > 1) {
				text = "(" + text + ")";
			}

			orParts.add(text);
		}

		return String.join(" OR ", orParts);
	}

	// ================= COURSE PATH =================

	private List<PathStep> buildCoursePath(Course target) {

		LinkedHashMap<String, PathStep> steps = new LinkedHashMap<>();

		collectPath(target, "", steps, new HashSet<>());

		return new ArrayList<>(steps.values());
	}

	private void collectPath(Course course, String requiredGrade, LinkedHashMap<String, PathStep> steps,
			Set<String> visiting) {

		String code = fullNumber(course);

		if (!visiting.add(code)) {
			return;
		}

		if (!hasRequiredGrade(code, requiredGrade)) {

			List<Prerequisite> bestGroup = chooseBestGroup(course, new HashSet<>(visiting));

			if (bestGroup != null) {

				for (Prerequisite prerequisite : bestGroup) {

					Course needed = findCourse(prerequisite.getCourseNumber());

					if (needed != null) {
						collectPath(needed, prerequisite.getMinGrade(), steps, visiting);
					}
				}
			}
		}

		steps.putIfAbsent(code, new PathStep(course, requiredGrade));

		visiting.remove(code);
	}

	private List<Prerequisite> chooseBestGroup(Course course, Set<String> visiting) {

		List<List<Prerequisite>> options = course.getPrerequisites();

		if (options == null || options.isEmpty()) {
			return null;
		}

		List<Prerequisite> best = null;
		int bestCost = Integer.MAX_VALUE;

		for (List<Prerequisite> group : options) {

			int cost = 0;

			for (Prerequisite prerequisite : group) {

				String number = prerequisite.getCourseNumber();

				if (hasRequiredGrade(number, prerequisite.getMinGrade())) {
					continue;
				}

				Course needed = findCourse(number);

				if (needed == null) {
					cost += 100;
				} else {
					cost += estimateCost(needed, prerequisite.getMinGrade(), new HashSet<>(visiting));
				}
			}

			if (cost < bestCost) {
				bestCost = cost;
				best = group;
			}
		}

		return best;
	}

	private int estimateCost(Course course, String requiredGrade, Set<String> visiting) {

		String code = fullNumber(course);

		if (hasRequiredGrade(code, requiredGrade)) {
			return 0;
		}

		if (!visiting.add(code)) {
			return 100;
		}

		List<Prerequisite> best = chooseBestGroup(course, visiting);

		int cost = 1;

		if (best != null) {
			for (Prerequisite prerequisite : best) {

				Course needed = findCourse(prerequisite.getCourseNumber());

				if (hasRequiredGrade(prerequisite.getCourseNumber(), prerequisite.getMinGrade())) {
					continue;
				}

				if (needed == null) {
					cost += 100;
				} else {
					cost += estimateCost(needed, prerequisite.getMinGrade(), new HashSet<>(visiting));
				}
			}
		}

		return cost;
	}

	private int countRemaining(List<PathStep> path, Course target) {

		int count = 0;

		for (PathStep step : path) {

			if (fullNumber(step.course).equals(fullNumber(target))) {
				continue;
			}

			if (!hasRequiredGrade(fullNumber(step.course), step.requiredGrade)) {
				count++;
			}
		}

		return count;
	}

	// ================= ICONS =================

	private Node createCompletedIcon() {

		Label check = new Label("✓");
		check.setAlignment(Pos.CENTER);
		check.setMinSize(25, 25);
		check.setMaxSize(25, 25);

		check.setStyle("-fx-background-color: #BBF7D0;" + "-fx-background-radius: 50%;" + "-fx-text-fill: #15803D;"
				+ "-fx-font-size: 15px;" + "-fx-font-weight: bold;");

		return check;
	}

	private Node createNumberIcon(int number) {

		Label label = new Label(String.valueOf(number));

		label.setAlignment(Pos.CENTER);
		label.setMinSize(25, 25);
		label.setMaxSize(25, 25);

		label.setStyle("-fx-background-color: #DBEAFE;" + "-fx-border-color: #60A5FA;" + "-fx-border-width: 1.5;"
				+ "-fx-border-radius: 50%;" + "-fx-background-radius: 50%;" + "-fx-text-fill: #2563EB;"
				+ "-fx-font-weight: bold;");

		return label;
	}

	private Node createGoalIcon() {

		SVGPath shape = new SVGPath();

		shape.setContent("M5 3 V21 " + "M5 4 Q9 2 12 5 Q16 8 20 5 " + "V14 Q16 17 12 14 Q9 11 5 13");

		shape.setFill(Color.TRANSPARENT);
		shape.setStroke(Color.WHITE);
		shape.setStrokeWidth(1.5);
		shape.setScaleX(0.70);
		shape.setScaleY(0.70);

		StackPane flag = new StackPane(shape);
		flag.setMinSize(25, 25);
		flag.setPrefSize(25, 25);
		flag.setMaxSize(25, 25);

		flag.setStyle("-fx-background-color: " + DARK + ";" + "-fx-background-radius: 50%;");

		return flag;
	}

	// ================= PATH CARDS =================

	private HBox createCompletedRow(Course course, String grade, String neededFor) {

		Label code = boldLabel(fullNumber(course));
		Label title = grayLabel(course.getTitle());

		HBox nameRow = new HBox(9, code, title);
		nameRow.setAlignment(Pos.CENTER_LEFT);

		Label note = new Label("Needed for " + neededFor);

		note.setStyle("-fx-font-size: 11px;" + "-fx-text-fill: #9CA3AF;");

		VBox text = new VBox(5, nameRow, note);

		Label badge = new Label("Completed, " + grade);
		badge.setStyle(badgeStyle("#DCFCE7", "#166534"));

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		HBox row = new HBox(14, text, spacer, badge);
		row.setAlignment(Pos.TOP_LEFT);

		return row;
	}

	private VBox createNextCard(Course course) {

		Label code = boldLabel(fullNumber(course));
		Label title = grayLabel(course.getTitle());

		HBox courseName = new HBox(9, code, title);
		courseName.setAlignment(Pos.CENTER_LEFT);

		Label badge = new Label("Take next");
		badge.setStyle(badgeStyle("#DBEAFE", "#1D4ED8"));

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		HBox topRow = new HBox(10, courseName, spacer, badge);
		topRow.setAlignment(Pos.CENTER_LEFT);

		Text prerequisite = new Text("Prerequisite: " + formatPrerequisites(course) + ". ");

		prerequisite.setFill(Color.web("#475569"));

		Text satisfied = new Text(prerequisitesSatisfied(course) ? "You meet this." : "Requirements not yet met.");

		satisfied.setFill(prerequisitesSatisfied(course) ? Color.web("#16A34A") : Color.web("#B91C1C"));

		satisfied.setStyle("-fx-font-weight: bold;");

		TextFlow rule = new TextFlow(prerequisite, satisfied);
		rule.setStyle("-fx-font-size: 12px;");

		Label credits = grayLabel(formatCredits(course.getHours()));

		VBox card = new VBox(9, topRow, rule, credits);

		card.setPadding(new Insets(16));
		card.setMinHeight(Region.USE_PREF_SIZE);
		card.setMaxHeight(Region.USE_PREF_SIZE);

		card.setStyle("-fx-background-color: white;" + "-fx-border-color: #93C5FD;" + "-fx-border-radius: 10;"
				+ "-fx-background-radius: 10;");

		return card;
	}

	private VBox createGoalCard(Course course) {

		Label code = boldLabel(fullNumber(course));
		Label title = grayLabel(course.getTitle());

		HBox courseName = new HBox(9, code, title);
		courseName.setAlignment(Pos.CENTER_LEFT);

		Label badge = new Label("Your goal");
		badge.setStyle(badgeStyle("#F3F4F6", DARK));

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		HBox topRow = new HBox(10, courseName, spacer, badge);
		topRow.setAlignment(Pos.CENTER_LEFT);

		String description = course.getDescription();

		if (description == null || description.isBlank()) {
			description = "Target course";
		}

		Label details = new Label(description);
		details.setWrapText(true);
		details.setMaxHeight(45);
		details.setStyle("-fx-font-size: 12px;" + "-fx-text-fill: " + GRAY + ";");

		VBox card = new VBox(10, topRow, details);

		card.setPadding(new Insets(16));
		card.setMinHeight(Region.USE_PREF_SIZE);
		card.setMaxHeight(Region.USE_PREF_SIZE);

		card.setStyle("-fx-background-color: white;" + "-fx-border-color: #E5E7EB;" + "-fx-border-radius: 10;"
				+ "-fx-background-radius: 10;");

		return card;
	}

	// ================= STYLING HELPERS =================

	private Label boldLabel(String value) {

		Label label = new Label(value);
		label.setStyle("-fx-font-size: 13px;" + "-fx-font-weight: bold;");
		return label;
	}

	private Label grayLabel(String value) {

		Label label = new Label(value);
		label.setStyle("-fx-font-size: 12px;" + "-fx-text-fill: " + GRAY + ";");
		return label;
	}

	private String badgeStyle(String background, String textColor) {

		return "-fx-background-color: " + background + ";" + "-fx-text-fill: " + textColor + ";"
				+ "-fx-background-radius: 7;" + "-fx-padding: 4 9;" + "-fx-font-size: 11px;";
	}

	private String formatCredits(double hours) {

		if (hours == Math.floor(hours)) {
			return (int) hours + " credits";
		}

		return hours + " credits";
	}

	// ================= BACK NAVIGATION =================

	private void openCourseList(Stage stage) {

		try {

			Class<?> pageClass = Class.forName("application.CourseListPage");

			Object page = pageClass.getDeclaredConstructor().newInstance();

			if (page instanceof Application) {

				((Application) page).start(stage);
				return;
			}

			if (page instanceof Parent) {

				Scene scene = new Scene((Parent) page, 850, 720);

				stage.setScene(scene);
				return;
			}

			showMessage("Course List Page",
					"CourseListPage was found, but its " + "navigation method still needs " + "to be connected.");

		} catch (Exception ex) {

			showMessage("Course List Page", "Nana's CourseListPage is not " + "connected yet.");
		}
	}

	private void showMessage(String title, String message) {

		Alert alert = new Alert(Alert.AlertType.INFORMATION);

		alert.setTitle(title);
		alert.setHeaderText(title);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
