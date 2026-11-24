package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

	@Override
	public void start(Stage stage) {

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

		// ================= MAIN CONTENT =================

		VBox content = new VBox(20);
		content.setPadding(new Insets(22, 28, 25, 28));

		// BACK BUTTON: Connect to Nana's CourseListPage

		Button backButton = new Button("← Back to results");
		backButton.setStyle("-fx-background-color: white;" + "-fx-border-color: #D1D5DB;" + "-fx-border-radius: 7;"
				+ "-fx-background-radius: 7;" + "-fx-text-fill: " + DARK + ";" + "-fx-font-size: 12px;"
				+ "-fx-padding: 5 12;" + "-fx-cursor: hand;");

		backButton.setOnAction(e -> openCourseList(stage));

		// ================= COURSE INFORMATION =================

		Label courseName = new Label("INFO 7390: Advances in Data Sciences");
		courseName.setStyle("-fx-font-size: 18px;" + "-fx-font-weight: bold;" + "-fx-text-fill: " + DARK + ";");

		Label eligibility = new Label("Not eligible yet");
		eligibility.setStyle("-fx-background-color: #FEE2E2;" + "-fx-text-fill: #B91C1C;" + "-fx-background-radius: 7;"
				+ "-fx-font-size: 11px;" + "-fx-padding: 5 9;");

		HBox courseTitleRow = new HBox(12, courseName, eligibility);
		courseTitleRow.setAlignment(Pos.CENTER_LEFT);

		Label summary = new Label("You're 1 course away. Take INFO 6105 first, then this course.");
		summary.setWrapText(true);
		summary.setStyle("-fx-font-size: 12px;" + "-fx-text-fill: #475569;");

		VBox courseInfo = new VBox(9, courseTitleRow, summary);

		// ================= PREREQUISITE RULE =================

		Label ruleTitle = new Label("Prerequisite rule");
		ruleTitle.setStyle("-fx-font-size: 12px;" + "-fx-text-fill: " + GRAY + ";");

		Label rule = new Label("INFO 6105 with a minimum grade of B-");
		rule.setStyle("-fx-font-size: 13px;" + "-fx-text-fill: " + DARK + ";");

		VBox ruleCard = new VBox(6, ruleTitle, rule);
		ruleCard.setPadding(new Insets(16));
		ruleCard.setStyle("-fx-background-color: #F8F9FA;" + "-fx-background-radius: 8;");

		// ================= YOUR PATH =================

		Label pathTitle = new Label("Your path");
		pathTitle.setStyle("-fx-font-size: 14px;" + "-fx-font-weight: bold;");

		// STEP 1: COMPLETED

		Label check = new Label("✓");
		check.setAlignment(Pos.CENTER);
		check.setMinSize(25, 25);
		check.setMaxSize(25, 25);
		check.setStyle("-fx-background-color: #BBF7D0;" + "-fx-background-radius: 50%;" + "-fx-text-fill: #15803D;"
				+ "-fx-font-weight: bold;" + "-fx-font-size: 15px;");

		Label completedName = new Label("INFO 5100");
		completedName.setStyle("-fx-font-size: 13px;" + "-fx-font-weight: bold;");

		Label completedTitle = new Label("Application Engineering and Development");
		completedTitle.setStyle("-fx-font-size: 12px;" + "-fx-text-fill: " + GRAY + ";");

		HBox completedCourse = new HBox(9, completedName, completedTitle);
		completedCourse.setAlignment(Pos.CENTER_LEFT);

		Label completedNote = new Label("Needed for INFO 6105");
		completedNote.setStyle("-fx-font-size: 11px;" + "-fx-text-fill: #9CA3AF;");

		VBox completedText = new VBox(5, completedCourse, completedNote);

		Label completedBadge = new Label("Completed, A");
		completedBadge.setStyle("-fx-background-color: #DCFCE7;" + "-fx-text-fill: #166534;"
				+ "-fx-background-radius: 7;" + "-fx-padding: 4 9;" + "-fx-font-size: 11px;");

		Region completedSpace = new Region();
		HBox.setHgrow(completedSpace, Priority.ALWAYS);

		HBox completedRow = new HBox(14, completedText, completedSpace, completedBadge);
		completedRow.setAlignment(Pos.TOP_LEFT);

		// STEP 2: TAKE NEXT

		Label number = new Label("1");
		number.setAlignment(Pos.CENTER);
		number.setMinSize(25, 25);
		number.setMaxSize(25, 25);
		number.setStyle("-fx-background-color: #DBEAFE;" + "-fx-border-color: #60A5FA;" + "-fx-border-width: 1.5;"
				+ "-fx-border-radius: 50%;" + "-fx-background-radius: 50%;" + "-fx-text-fill: #2563EB;"
				+ "-fx-font-weight: bold;");

		Label nextName = new Label("INFO 6105");
		nextName.setStyle("-fx-font-size: 13px;" + "-fx-font-weight: bold;");

		Label nextTitle = new Label("Data Science Engineering Methods and Tools");
		nextTitle.setStyle("-fx-font-size: 12px;" + "-fx-text-fill: " + GRAY + ";");

		HBox nextCourse = new HBox(9, nextName, nextTitle);
		nextCourse.setAlignment(Pos.CENTER_LEFT);

		Label nextBadge = new Label("Take next");
		nextBadge.setStyle("-fx-background-color: #DBEAFE;" + "-fx-text-fill: #1D4ED8;" + "-fx-padding: 4 9;"
				+ "-fx-background-radius: 7;" + "-fx-font-size: 11px;");

		Region nextSpace = new Region();
		HBox.setHgrow(nextSpace, Priority.ALWAYS);

		HBox nextHeader = new HBox(10, nextCourse, nextSpace, nextBadge);
		nextHeader.setAlignment(Pos.CENTER_LEFT);

		Text prerequisiteText = new Text("Prerequisite: INFO 5100 with a minimum grade of B-. ");
		prerequisiteText.setFill(Color.web("#475569"));

		Text meetText = new Text("You meet this.");
		meetText.setFill(Color.web("#16A34A"));
		meetText.setStyle("-fx-font-weight: bold;");

		TextFlow nextRule = new TextFlow(prerequisiteText, meetText);
		nextRule.setStyle("-fx-font-size: 12px;");

		Label credits = new Label("4 credits");
		credits.setStyle("-fx-font-size: 12px;" + "-fx-text-fill: #475569;");

		VBox nextCard = new VBox(9, nextHeader, nextRule, credits);
		nextCard.setPadding(new Insets(16));
		nextCard.setMinHeight(Region.USE_PREF_SIZE);
		nextCard.setMaxHeight(Region.USE_PREF_SIZE);
		nextCard.setStyle("-fx-background-color: white;" + "-fx-border-color: #93C5FD;" + "-fx-border-radius: 10;"
				+ "-fx-background-radius: 10;");

		// STEP 3: GOAL

		SVGPath flagShape = new SVGPath();
		flagShape.setContent("M5 3 V21 " + "M5 4 Q9 2 12 5 Q16 8 20 5 " + "V14 Q16 17 12 14 Q9 11 5 13");
		flagShape.setFill(Color.TRANSPARENT);
		flagShape.setStroke(Color.WHITE);
		flagShape.setStrokeWidth(1.5);
		flagShape.setScaleX(0.70);
		flagShape.setScaleY(0.70);

		StackPane flag = new StackPane(flagShape);
		flag.setMinSize(25, 25);
		flag.setPrefSize(25, 25);
		flag.setMaxSize(25, 25);
		flag.setStyle("-fx-background-color: " + DARK + ";" + "-fx-background-radius: 50%;");

		Label goalName = new Label("INFO 7390");
		goalName.setStyle("-fx-font-size: 13px;" + "-fx-font-weight: bold;");

		Label goalTitle = new Label("Advances in Data Sciences");
		goalTitle.setStyle("-fx-font-size: 12px;" + "-fx-text-fill: " + GRAY + ";");

		HBox goalCourse = new HBox(9, goalName, goalTitle);
		goalCourse.setAlignment(Pos.CENTER_LEFT);

		Label goalBadge = new Label("Your goal");
		goalBadge.setStyle("-fx-background-color: #F3F4F6;" + "-fx-background-radius: 7;" + "-fx-padding: 4 9;"
				+ "-fx-font-size: 11px;");

		Region goalSpace = new Region();
		HBox.setHgrow(goalSpace, Priority.ALWAYS);

		HBox goalHeader = new HBox(10, goalCourse, goalSpace, goalBadge);
		goalHeader.setAlignment(Pos.CENTER_LEFT);

		Label matched = new Label("Matched on: machine learning, models, data");
		matched.setStyle("-fx-font-size: 12px;" + "-fx-text-fill: " + GRAY + ";");

		VBox goalCard = new VBox(10, goalHeader, matched);
		goalCard.setPadding(new Insets(16));
		goalCard.setStyle("-fx-background-color: white;" + "-fx-border-color: #E5E7EB;" + "-fx-border-radius: 10;"
				+ "-fx-background-radius: 10;");

		// ================= TIMELINE =================

		double step1Y = 0;
		double step2Y = 80;
		double step3Y = 235;

		Pane iconColumn = new Pane();
		iconColumn.setMinWidth(25);
		iconColumn.setPrefWidth(25);
		iconColumn.setMaxWidth(25);
		iconColumn.setPrefHeight(265);

		// Line behind the three icons
		Region timelineLine = new Region();
		timelineLine.setStyle("-fx-background-color: #D1D5DB;");
		timelineLine.setLayoutX(11.5);
		timelineLine.setLayoutY(step1Y + 12);
		timelineLine.setPrefWidth(2);
		timelineLine.setPrefHeight(step3Y - step1Y);

		iconColumn.getChildren().add(timelineLine);

		check.relocate(0, step1Y);
		number.relocate(0, step2Y);
		flag.relocate(0, step3Y);

		iconColumn.getChildren().addAll(check, number, flag);

		Pane cardsColumn = new Pane();
		cardsColumn.setMinHeight(330);
		HBox.setHgrow(cardsColumn, Priority.ALWAYS);

		completedRow.setLayoutY(step1Y);
		nextCard.setLayoutY(step2Y);
		goalCard.setLayoutY(step3Y);

		cardsColumn.getChildren().addAll(completedRow, nextCard, goalCard);

		cardsColumn.widthProperty().addListener((obs, oldWidth, newWidth) -> {
			double width = newWidth.doubleValue();
			completedRow.setPrefWidth(width);
			nextCard.setPrefWidth(width);
			goalCard.setPrefWidth(width);
		});

		HBox timeline = new HBox(14, iconColumn, cardsColumn);
		timeline.setAlignment(Pos.TOP_LEFT);

		// ================= FOOTER =================

		Separator footerLine = new Separator();

		Label footer = new Label("ⓘ  This is a planning aid. Confirm prerequisites, waivers, "
				+ "and which terms each course is offered with the official " + "catalog or your advisor.");
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

	// ================= BACK NAVIGATION =================

	private void openCourseList(Stage stage) {

		try {
			Class<?> pageClass = Class.forName("application.CourseListPage");

			Object page = pageClass.getDeclaredConstructor().newInstance();

			// If Nana's class extends JavaFX Application
			if (page instanceof Application) {
				((Application) page).start(stage);
				return;
			}

			// If Nana's class is itself a JavaFX layout
			if (page instanceof Parent) {
				Scene scene = new Scene((Parent) page, 850, 720);
				stage.setScene(scene);
				return;
			}

			// Other page structures need Nana's actual API
			showNavigationMessage("CourseListPage was found, but its " + "navigation method needs to be connected.");

		} catch (ClassNotFoundException ex) {

			showNavigationMessage("CourseListPage is not available yet. " + "Please add Nana's Java file to "
					+ "the application package.");

		} catch (Exception ex) {

			showNavigationMessage("Unable to open CourseListPage. " + "Its constructor or page structure "
					+ "may require additional parameters.");
		}
	}

	private void showNavigationMessage(String message) {

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Course List Page");
		alert.setHeaderText("Back to results");
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
