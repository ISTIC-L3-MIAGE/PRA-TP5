package bah.tahi.morpion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainMorpion extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		try {
			Pane root = (Pane) FXMLLoader.load(getClass().getResource("morpionScene.fxml"));
			Scene scene = new Scene(root);

			stage.setTitle("Morpion");
			stage.setScene(scene);
			stage.sizeToScene();
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
