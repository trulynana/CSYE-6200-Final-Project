package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;


public class Main extends Application {
	
	public static final int SCENE_WIDTH = 700;
	public static final int SCENE_HEIGHT = 600;

	@Override
	public void start(Stage primaryStage) {
		try {
			Pane pane = new Pane();
			Text text = new Text("Hello World!");
			pane.getChildren().add(text);
			
			// Center the text
			text.setX((SCENE_WIDTH - text.getLayoutBounds().getWidth()) / 2.0);
			text.setY((SCENE_HEIGHT - text.getLayoutBounds().getHeight()) / 2.0);
			
			
			Scene scene = new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT);
			
			primaryStage.setTitle("SEIS Course Recommender");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
