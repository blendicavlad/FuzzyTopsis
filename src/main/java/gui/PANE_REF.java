package gui;

public enum PANE_REF {
	MAIN_PANE("main.fxml"),
	RESULT_PANE("result.fxml");

	private final String pane;
	PANE_REF(String s) {
		this.pane = s;
	}

	public String getPane() {
		return pane;
	}
}
