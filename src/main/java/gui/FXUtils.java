package gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class FXUtils {

	public static void setPane(Pane oldPane, Pane newPane) {
		oldPane.setManaged(false);
		oldPane.getChildren().clear();
		oldPane.getChildren().add(newPane);
	}

	public static void loadScene(Stage currentStage, PANE_REF pane, ClassLoader classLoader, boolean resizeable) {
		try {
			Parent parent = FXMLLoader.load(Objects.requireNonNull(classLoader.getResource(pane.getPane())));
			if (parent != null) {
				Scene scene = new Scene(parent);
				currentStage.close();
				currentStage.setScene(scene);
				currentStage.setResizable(resizeable);
				currentStage.show();
			}
		} catch (IOException e) {
			errorBox("Could not find: " + pane.getPane() ,"","Resource not found!");
		}
	}

	public static void errorBox(String infoMessage, String headerText, String title){
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setContentText(infoMessage);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.showAndWait();
	}

	public static void info(String infoMessage, String headerText, String title){
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setContentText(infoMessage);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.showAndWait();
	}

	public static class SceneSizeChangeListener implements ChangeListener<Number> {
		private final Scene scene;
		private final double ratio;
		private final double initHeight;
		private final double initWidth;
		private final Pane contentPane;

		public SceneSizeChangeListener(Scene scene, double ratio, double initHeight, double initWidth, Pane contentPane) {
			this.scene = scene;
			this.ratio = ratio;
			this.initHeight = initHeight;
			this.initWidth = initWidth;
			this.contentPane = contentPane;
		}

		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
			final double newWidth  = scene.getWidth();
			final double newHeight = scene.getHeight();

			double scaleFactor =
					newWidth / newHeight > ratio
							? newHeight / initHeight
							: newWidth / initWidth;

			if (scaleFactor >= 1) {
				Scale scale = new Scale(scaleFactor, scaleFactor);
				scale.setPivotX(0);
				scale.setPivotY(0);
				scene.getRoot().getTransforms().setAll(scale);

				contentPane.setPrefWidth (newWidth  / scaleFactor);
				contentPane.setPrefHeight(newHeight / scaleFactor);
			} else {
				contentPane.setPrefWidth (Math.max(initWidth,  newWidth));
				contentPane.setPrefHeight(Math.max(initHeight, newHeight));
			}
		}
	}
}
