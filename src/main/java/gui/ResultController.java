package gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ResultController implements Initializable {

	public JFXButton backBtn;
	public JFXListView resultList;

	@Override public void initialize(URL url, ResourceBundle resourceBundle) {

		var resulArr = ResultState.getState();
		for (int i = 0; i < ResultState.getState().length; i++) {
			resultList.getItems().add("Rank: " + i + " Alternative: "
					+ resulArr[i].getAlternative().getName() + " " + " Score:"
					+ resulArr[i].getRelativeCloseness());
		}
	}

	public void backAction(ActionEvent actionEvent) {
		Node node = (Node) actionEvent.getSource();
		FXUtils.loadScene((Stage) node.getScene().getWindow(), PANE_REF.MAIN_PANE,
				getClass().getClassLoader(),true);
	}
}
