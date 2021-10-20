package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Series;
import model.services.CategoriaService;
import model.services.SeriesService;

public class SeriesListController implements Initializable, DataChangeListener {

	private SeriesService service;

	@FXML
	private TableView<Series> tableViewSeries;

	@FXML
	private TableColumn<Series, Integer> tableColumnId;

	@FXML
	private TableColumn<Series, String> tableColumnNome;

	@FXML
	private TableColumn<Series, Integer> tableColumnTemporadas;

	@FXML
	private TableColumn<Series, Date> tableColumnAdicionadoEm;

	@FXML
	private TableColumn<Series, Double> tableColumnNota;

	@FXML
	private TableColumn<Series, Series> tableColumnEDIT;

	@FXML
	private TableColumn<Series, Series> tableColumnREMOVER;

	@FXML
	private Button btNew;

	private ObservableList<Series> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Series obj = new Series();
		createDialogForm(obj, "/gui/SeriesForm.fxml", parentStage);
	}

	public void setSeriesService(SeriesService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnTemporadas.setCellValueFactory(new PropertyValueFactory<>("temporadas"));
		tableColumnAdicionadoEm.setCellValueFactory(new PropertyValueFactory<>("adicionadoEm"));
		Utils.formatTableColumnDate(tableColumnAdicionadoEm, "dd/MM/yyyy");
		tableColumnNota.setCellValueFactory(new PropertyValueFactory<>("nota"));
		Utils.formatTableColumnDouble(tableColumnNota, 1);

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeries.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		List<Series> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSeries.setItems(obsList);
		initEditButtons();
		initRemoverButtons();
	}

	private void createDialogForm(Series obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			SeriesFormController controller = loader.getController();
			controller.setSeries(obj);
			controller.setServices(new SeriesService(), new CategoriaService());
			controller.loadAssociatedObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Series data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Series, Series>() {
			private final Button button = new Button("editar");

			@Override
			protected void updateItem(Series obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj, "/gui/SeriesForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoverButtons() {
		tableColumnREMOVER.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVER.setCellFactory(param -> new TableCell<Series, Series>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Series obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removerEntity(obj));
			}
		});
	}

	private void removerEntity(Series obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza que deseja deletar?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover o objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
