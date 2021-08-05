
import gui.FXUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Objects;

public class GUIApp extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override public void start(Stage stage) throws Exception {
		Pane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main.fxml")));
		Scene scene = new Scene(root);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth() / 1.3;
		double height = screenSize.getHeight() / 1.3;
		final double ratio = width / height;
		root.prefHeight(height);
		root.setPrefWidth(width);
		FXUtils.SceneSizeChangeListener sceneSizeListener = new FXUtils.SceneSizeChangeListener(scene, ratio, height, width, root);

		scene.widthProperty().addListener(sceneSizeListener);
		scene.heightProperty().addListener(sceneSizeListener);
		stage.setTitle("TOPSIS IFS");
		stage.setScene(scene);
		stage.setResizable(true);

		stage.show();
	}
}
