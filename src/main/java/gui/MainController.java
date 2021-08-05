package gui;


import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import topsisifs.Alternative;
import topsisifs.Criteria;
import topsisifs.IFSInitialValue;
import topsisifs.TopsisIFS;

import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class MainController implements Initializable {

	public TableView<AlternativeRow> alternativeTable;
	public TableColumn<AlternativeRow,Integer> alternativeIndex;
	public TableColumn<AlternativeRow, String> alternativeName;
	public JFXButton addAlternativeBtn;
	public JFXButton removeAlternativeBtn;

	public TableView<CriteriaRow> criteriaTable;
	public TableColumn<CriteriaRow, Integer> criteriaIndex;
	public TableColumn<CriteriaRow, String> criteriaName;
	public TableColumn<CriteriaRow, Double> criteriaWeight;
	public JFXButton addCriteriaBtn;
	public JFXButton removeCriteriaBtn;

	public TableView<String> matrixAlternatives;
	public TableColumn<String,String> alternativeIndexColumn;
	public TableView<double[]> decisionMatrix;
	private ObservableList<TableColumn<double[],Double>> criteriaColumns;

	public JFXButton processBtn;
	public JFXButton generateDataBtn;

	private ObservableList<AlternativeRow> alternativeRows = null;
	private ObservableList<CriteriaRow> criteriaRows = null;
	private ObservableList<double[]> ifsRows = null;


	@Override public void initialize(URL url, ResourceBundle resourceBundle) {

		handleAlternativesAnchorCreation();
		handleCriteriaAnchorCreation();
		handleDecisionMatrixAnchorCreation();
	}

	private void handleDecisionMatrixAnchorCreation() {
		matrixAlternatives.setEditable(true);
		this.ifsRows = FXCollections.observableArrayList();

		alternativeIndexColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
		alternativeIndexColumn.setMinWidth(matrixAlternatives.getWidth());
		matrixAlternatives
				.setItems(FXCollections.observableArrayList(alternativeRows
						.stream()
						.map(AlternativeRow::getName)
				.toArray(String[]::new)));

		this.criteriaColumns = FXCollections.observableArrayList();
		for (int i = 0; i < criteriaRows.size(); i++) {
			TableColumn<double[], Double> criteriaColumn = buildMatrixIFSCriteriaColumn(i, criteriaRows.get(i).getName());
			criteriaColumns.add(criteriaColumn);
			decisionMatrix.getColumns().add(criteriaColumn);
		}
		this.ifsRows.add(new double[] {0.0, 0.0});
		this.ifsRows.add(new double[] {0.0, 0.0});
		decisionMatrix.setItems(this.ifsRows);
	}

	private void handleCriteriaAnchorCreation() {
		this.criteriaRows = FXCollections.observableArrayList(
				new CriteriaRow(1, "C1", 0.0),
				new CriteriaRow(2,"C2", 0.0)
		);

		criteriaRows.addListener((ListChangeListener<CriteriaRow>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					TableColumn<double[], Double> criteriaColumn = buildMatrixIFSCriteriaColumn(0, criteriaRows.get(criteriaRows.size() - 1).getName());
					criteriaColumns.add(criteriaColumn);
					decisionMatrix.getColumns().add(criteriaColumn);
				}
				if (change.wasRemoved()) {
					criteriaColumns.remove(criteriaColumns.size() - 1);
				}
				this.decisionMatrix.getColumns().clear();
				this.criteriaColumns.forEach(this.decisionMatrix.getColumns()::add);
			}
		});

		criteriaTable.setEditable(true);
		criteriaIndex.setReorderable(false);
		criteriaIndex.setCellValueFactory(new PropertyValueFactory<>("index"));
		criteriaName.setCellValueFactory(new PropertyValueFactory<>("name"));
		criteriaWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
		Callback<TableColumn<CriteriaRow,String>, TableCell<CriteriaRow,String>> cellFactoryCriteriaString =
				tableColumn -> new EditingCell<>();
		Callback<TableColumn<CriteriaRow,Double>, TableCell<CriteriaRow,Double>> cellFactoryCriteriaDouble =
				tableColumn -> new EditingCell<>();
		criteriaName.setCellFactory(cellFactoryCriteriaString);
		criteriaName.setReorderable(false);
		criteriaWeight.setCellFactory(cellFactoryCriteriaDouble);
		criteriaWeight.setReorderable(false);
		criteriaName.setOnEditCommit(alternativeRowStringCellEditEvent ->
				alternativeRowStringCellEditEvent
						.getTableView().getItems()
						.get(alternativeRowStringCellEditEvent.getTablePosition().getRow())
						.setName(alternativeRowStringCellEditEvent.getNewValue()));
		criteriaWeight.setOnEditCommit(alternativeRowStringCellEditEvent ->
				alternativeRowStringCellEditEvent
						.getTableView().getItems()
						.get(alternativeRowStringCellEditEvent.getTablePosition().getRow())
						.setWeight(alternativeRowStringCellEditEvent.getNewValue()));

		criteriaTable.setItems(this.criteriaRows);
	}

	private void handleAlternativesAnchorCreation() {
		this.alternativeRows = FXCollections.observableArrayList(
				new AlternativeRow(1, "A1"),
				new AlternativeRow(2, "A2")
		);
		alternativeTable.setEditable(true);

		alternativeRows.addListener((ListChangeListener<AlternativeRow>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					matrixAlternatives.getItems().add(alternativeRows.get(alternativeRows.size() - 1).getName());
					decisionMatrix.getItems().add(new double[] {0.0,0.0});
					decisionMatrix.getItems().forEach(item -> System.out.println(Arrays.toString(item)));
				}
				if (change.wasRemoved()) {
					matrixAlternatives.getItems().remove(matrixAlternatives.getItems().size() - 1);
					decisionMatrix.getItems().remove(decisionMatrix.getItems().size() - 1);
					decisionMatrix.getItems().forEach(item -> System.out.println(Arrays.toString(item)));
				}
			}
		});

		alternativeIndex.setCellValueFactory(new PropertyValueFactory<>("index"));
		alternativeName.setCellValueFactory(new PropertyValueFactory<>("name"));
		Callback<TableColumn<AlternativeRow,String>, TableCell<AlternativeRow,String>> cellFactoryAlternative =
				tableColumn -> new EditingCell<>();
		alternativeName.setCellFactory(cellFactoryAlternative);
		alternativeName.setOnEditCommit(alternativeRowStringCellEditEvent ->
				alternativeRowStringCellEditEvent
						.getTableView().getItems()
						.get(alternativeRowStringCellEditEvent.getTablePosition().getRow())
				.setName(alternativeRowStringCellEditEvent.getNewValue()));

		alternativeTable.setItems(this.alternativeRows);
	}

	private TableColumn<double[], Double> buildMatrixIFSCriteriaColumn(int columnIndex, String criteriaName) {
		var criteriaColumn = new TableColumn();
		criteriaColumn.setPrefWidth(200);
		criteriaColumn.setReorderable(false);
		var uAcolumn = new TableColumn<Double, Number>("uA");
		var vAcolumn = new TableColumn<Double, Number>("vA");
		uAcolumn.setSortable(false);
		vAcolumn.setSortable(false);
		uAcolumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(0.0));
		vAcolumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(0.0));
		Callback<TableColumn<Double, Number>, TableCell<Double, Number>> cellFactoryCriteriaDoubleIFS = tableColumn -> new EditingCell<>();
		uAcolumn.setCellFactory(cellFactoryCriteriaDoubleIFS);
		uAcolumn.setPrefWidth(100);
		uAcolumn.setReorderable(false);
		vAcolumn.setPrefWidth(100);
		vAcolumn.setReorderable(false);
		vAcolumn.setCellFactory(cellFactoryCriteriaDoubleIFS);
//		uAcolumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Double, Number>>() {
//			@Override public void handle(TableColumn.CellEditEvent<Double, Number> rowEditEvent) {
//
//				rowEditEvent.getTableView().getItems()
//						.get(rowEditEvent.getTablePosition().getRow()). = (double) rowEditEvent.getNewValue();
//				//										System.out.println(Arrays.toString(alternativeRowStringCellEditEvent.getTableView().getItems()
//				//												.get(alternativeRowStringCellEditEvent.getTablePosition().getRow())));
//			}
//		});
//		vAcolumn.setOnEditCommit(
//				alternativeRowStringCellEditEvent -> {
//					alternativeRowStringCellEditEvent.getTableView().getItems()
//							.get(alternativeRowStringCellEditEvent.getTablePosition().getRow())[columnIndex] = (double) alternativeRowStringCellEditEvent.getNewValue();
//										this.ifsRows.forEach(row -> System.out.println(Arrays.toString(row)));
//				});

		criteriaColumn.setText(criteriaName);
		criteriaColumn.getColumns().add(uAcolumn);
		criteriaColumn.getColumns().add(vAcolumn);
		return criteriaColumn;
	}

	private void initMinimumValues() {

	}

	static <S, T> Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> createArrayValueFactory(
			Function<S, T[]> arrayExtractor, final int index) {
		if (index < 0) {
			return cd -> null;
		}
		return cd -> {
			T[] array = arrayExtractor.apply(cd.getValue());
			return array == null || array.length <= index ? null : new SimpleObjectProperty<>(array[index]);
		};
	}

	public void generateData(ActionEvent actionEvent) {
		this.criteriaRows.removeAll();
		this.alternativeRows.removeAll();
		this.criteriaRows = FXCollections.observableArrayList(
				new CriteriaRow(1, "C1", 0.2),
				new CriteriaRow(2,"C2", 0.3),
				new CriteriaRow(3,"C3", 0.2),
				new CriteriaRow(4,"C4", 0.3)
		);

		this.criteriaTable.setItems(this.criteriaRows);
		this.alternativeRows = FXCollections.observableArrayList(
				new AlternativeRow(1, "A1"),
				new AlternativeRow(2, "A2"),
				new AlternativeRow(3, "A3"),
				new AlternativeRow(4, "A4"),
				new AlternativeRow(5, "A5")
		);

		this.alternativeTable.setItems(this.alternativeRows);
		handleDecisionMatrixAnchorCreation();

		for (Node r : decisionMatrix.lookupAll(".table-row-cell")) {
			var cellArr =  r.lookupAll(".table-cell").toArray(Node[]::new);
			for (int i = 0; i < cellArr.length; i++) {
				TableCell<?, ?> tc = (TableCell<?, ?>) cellArr[i];
				if (tc.getText() != null) {
					switch (i) {
					case 0:
						tc.setText("0.8");
						break;
					case 1:
						tc.setText("0.1");
						break;
					}
				}
			}
		}
	}

	public void addAlternative(ActionEvent actionEvent) {
		this.alternativeRows.add(new AlternativeRow(alternativeRows.size() + 1, "A" + (alternativeRows.size() + 1)));
	}

	public void removeAlternative(ActionEvent actionEvent) {
		if (this.alternativeRows.size() > 2) {
			this.alternativeRows.remove(alternativeRows.size() - 1);
		}
	}

	public void addCriteria(ActionEvent actionEvent) {
		this.criteriaRows.add(new CriteriaRow(criteriaRows.size() + 1, "C" + (criteriaRows.size() + 1), 0.0));
	}

	public void removeCriteria(ActionEvent actionEvent) {
		if (this.criteriaRows.size() > 2) {
			this.criteriaRows.remove(criteriaRows.size() - 1);
		}

	}

	public void process(ActionEvent actionEvent) {
//		for (TableColumn<double[], ?> column : decisionMatrix.getColumns()) {
//			for (int i = 0; i < alternativeRows.size(); i++) {
//				System.out.println(column.getCellData(i));
//			}
//		}
//		for (double[] item : decisionMatrix.getItems()) {
//			System.out.println("\n");
//			System.out.println(Arrays.toString(item));
//
//		}

		var alternatives = new Alternative[alternativeRows.size()];
		for (int i = 0; i < alternativeRows.size(); i++) {
			alternatives[i] = new Alternative(alternativeRows.get(i).getName());
//			System.out.println(alternatives[i].toString());
		}
		int array2d[][] = new int[10][3];


//		for(int i=0; i<10;i++)
//			for(int j=0;j<3;j++)
//				array2d[i][j] = array1d[(j*10) + i];

		var criterias = new Criteria[criteriaRows.size()];
		CRITERIAS: for (int i = 0; i < criteriaRows.size(); i++) {
			criterias[i] = new Criteria(criteriaRows.get(i).getName(), criteriaRows.get(i).getWeight());
			Alternative lastAlternative = null;
			for (int j = 0; j < alternatives.length; j++) {
				var uA = ThreadLocalRandom.current().nextDouble(0.1, 0.7);
				double vA = 0.9 - uA;
				System.out.println("uA:" + uA + "vA" + vA);
				alternatives[j].addInitialValue(new IFSInitialValue(criterias[i],new BigDecimal(uA, new MathContext(1)).doubleValue(),new BigDecimal(vA, new MathContext(1)).doubleValue()));
//				var alternative = alternatives[j];
//				for (Node r : decisionMatrix.lookupAll(".table-row-cell")) {
//					var cellArr =  r.lookupAll(".table-cell").toArray(Node[]::new);
//					Double uAt = null, vAt = null;
//					if (lastAlternative == null) {
//						lastAlternative = alternative;
//					} else if (lastAlternative.equals(alternative)) {
//						continhttps://www.youtube.com/channel/UCF_v2nxwpho_5oc43ujotkAue;
//					} else {
//						lastAlternative = alternative;
//					}
//
//					int index = (j ==0 && i > 0) ? 1 : i * j;
//					for (int i1 = 0; i1 < cellArr.length; i1++) {
//						System.out.println(((TableCell<?, ?>) cellArr[i1]).getText());
//					}
//					Arrays.stream(cellArr).forEach(row-> System.out.println(row.getAccessibleText()));
//					var cell = ((TableCell<?, ?>) cellArr[index]);
//					if (cell.getText() != null) {
//						try {
//							uA = Double.parseDouble(cell.getText());
//							vA = Double.parseDouble(((TableCell<?, ?>) cellArr[index]).getText());
//							System.out.println(alternative.getName() + " " + criteriaRows.get(i).getName() + " uA: " + uAt + " vA: " + vAt);
//						} catch (NumberFormatException e) {
//							FXUtils.errorBox("Invalid number format", "Invalid number", null);
//							break CRITERIAS;
//						}
//						alternative.addInitialValue(new IFSInitialValue(criterias[i], uA, vA));
//					} else {
//						break;
//					}
//				}
			}
		}


//		Criteria c1 = new Criteria("C1", 0.2);
//		Criteria c2 = new Criteria("C2", 0.3);
//		Criteria c3 = new Criteria("C3", 0.2);
//		Criteria c4 = new Criteria("C4", 0.3);
//
//		Alternative a1 = new Alternative("A1");
//		a1.addInitialValue(new IFSInitialValue(c1, 0.8, 0.1));
//		a1.addInitialValue(new IFSInitialValue(c2, 0.7, 0.1));
//		a1.addInitialValue(new IFSInitialValue(c3, 0.7, 0));
//		a1.addInitialValue(new IFSInitialValue(c4, 0.4, 0.3));
//
//		Alternative a2 = new Alternative("A2");
//		a2.addInitialValue(new IFSInitialValue(c1, 0.6, 0.3));
//		a2.addInitialValue(new IFSInitialValue(c2, 0.6, 0.1));
//		a2.addInitialValue(new IFSInitialValue(c3, 0.8, 0.1));
//		a2.addInitialValue(new IFSInitialValue(c4, 0.7, 0.2));
//
//		Alternative a3 = new Alternative("A3");
//		a3.addInitialValue(new IFSInitialValue(c1, 0.3, 0.5));
//		a3.addInitialValue(new IFSInitialValue(c2, 0.8, 0.1));
//		a3.addInitialValue(new IFSInitialValue(c3, 0.5, 0.3));
//		a3.addInitialValue(new IFSInitialValue(c4, 0.6, 0.3));
//
//		Alternative a4 = new Alternative("A4");
//		a4.addInitialValue(new IFSInitialValue(c1, 0.7, 0.1));
//		a4.addInitialValue(new IFSInitialValue(c2, 0.2, 0.5));
//		a4.addInitialValue(new IFSInitialValue(c3, 0.5, 0.3));
//		a4.addInitialValue(new IFSInitialValue(c4, 0.8, 0.1));
//
//		Alternative a5 = new Alternative("A5");
//		a5.addInitialValue(new IFSInitialValue(c1, 0.5, 0.2));
//		a5.addInitialValue(new IFSInitialValue(c2, 0.3, 0.6));
//		a5.addInitialValue(new IFSInitialValue(c3, 0.4, 0.2));
//		a5.addInitialValue(new IFSInitialValue(c4, 0.9, 0));
//
//		Alternative[] alternatives1 = new Alternative[] {
//				a1,a2,a3,a4,a5
//		};

		for (Alternative alternative : alternatives) {
			System.out.println(alternative);
			alternative.getValues().forEach(System.out::println);
		}
		TopsisIFS.ComputedAlternative[] topsisIFS = TopsisIFS.getInstance().computeResult(alternatives);
		ResultState.setState(topsisIFS);
		Node node = (Node) actionEvent.getSource();
		FXUtils.loadScene((Stage) node.getScene().getWindow(), PANE_REF.RESULT_PANE,
				getClass().getClassLoader(),true);
	}

	public void initDecisionMatrix(MouseEvent mouseEvent) {
	}

	public static class IFSRow {
		private SimpleDoubleProperty uA;
		private SimpleDoubleProperty vA;

		public IFSRow(double uA, double vA) {
			this.uA = new SimpleDoubleProperty(uA);
			this.vA = new SimpleDoubleProperty(vA);;
		}

		public double getuA() {
			return uA.get();
		}

		public SimpleDoubleProperty uAProperty() {
			return uA;
		}

		public void setuA(double uA) {
			this.uA.set(uA);
		}

		public double getvA() {
			return vA.get();
		}

		public SimpleDoubleProperty vAProperty() {
			return vA;
		}

		public void setvA(double vA) {
			this.vA.set(vA);
		}

		@Override public String toString() {
			return "IFSRow{" + "uA=" + uA + ", vA=" + vA + '}';
		}
	}

	public static class AlternativeRow {
		private final SimpleIntegerProperty index;
		private final SimpleStringProperty name;

		public AlternativeRow(int index, String name) {
			this.index = new SimpleIntegerProperty(index);
			this.name = new SimpleStringProperty(name);
		}

		public int getIndex() {
			return index.get();
		}

		public SimpleIntegerProperty indexProperty() {
			return index;
		}

		public void setIndex(int index) {
			this.index.set(index);
		}

		public String getName() {
			return name.get();
		}

		public SimpleStringProperty nameProperty() {
			return name;
		}

		public void setName(String name) {
			this.name.set(name);
		}
	}

	public static class CriteriaRow {
		private final SimpleIntegerProperty index;
		private final SimpleStringProperty name;
		private final SimpleDoubleProperty weight;

		public CriteriaRow(int index, String name, double weight) {
			this.index = new SimpleIntegerProperty(index);
			this.name = new SimpleStringProperty(name);
			this.weight = new SimpleDoubleProperty(weight);
		}

		public int getIndex() {
			return index.get();
		}

		public SimpleIntegerProperty indexProperty() {
			return index;
		}

		public void setIndex(int index) {
			this.index.set(index);
		}

		public String getName() {
			return name.get();
		}

		public SimpleStringProperty nameProperty() {
			return name;
		}

		public void setName(String name) {
			this.name.set(name);
		}

		public double getWeight() {
			return weight.get();
		}

		public SimpleDoubleProperty weightProperty() {
			return weight;
		}

		public void setWeight(double weight) {
			this.weight.set(weight);
		}
	}

	static class EditingCell<T,U> extends TableCell<T,U> {
		private TextField textField;

		public EditingCell() {
		}

		@Override
		public void startEdit() {
			if (!isEmpty()) {
				super.startEdit();
				createTextField();
				setText(null);
				setGraphic(textField);
				textField.selectAll();
			}
		}

		@Override
		public void cancelEdit() {
			super.cancelEdit();

			try {
				setText((String) getItem());
			} catch (ClassCastException e) {
				setText(String.valueOf(getItem()));
			}
			setGraphic(null);
		}


		@Override
		public void updateItem(U item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null) {
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					setText(getString());
					setGraphic(null);
				}
			}
		}

		private void createTextField() {
			textField = new TextField(getString());
			textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
			textField.focusedProperty().addListener((arg0, arg1, arg2) -> {
				if (!arg2) {
					//very very ugly, but i had a deadline
					try {
						commitEdit((U) textField.getText());
					} catch (ClassCastException e) {
						try {
							commitEdit((U) Double.valueOf(Double.parseDouble(textField.getText())));
						} catch (NumberFormatException e2) {
							FXUtils.errorBox("Invalid number format", "Invalid number", null);
						}
					}
				}
			});
		}

		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}
	}


}

