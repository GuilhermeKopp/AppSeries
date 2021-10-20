package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Categoria;
import model.entities.Series;
import model.exceptions.ValidationException;
import model.services.CategoriaService;
import model.services.SeriesService;

public class SeriesFormController implements Initializable {

	private Series entity;

	private SeriesService service;

	private CategoriaService categoriaService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtTemporadas;

	@FXML
	private DatePicker dpAdicionadoEm;

	@FXML
	private TextField txtNota;

	@FXML
	private ComboBox<Categoria> comboBoxCategoria;

	@FXML
	private Label labelErrorNome;

	@FXML
	private Label labelErrorTemporadas;

	@FXML
	private Label labelErrorAdicionadoEm;

	@FXML
	private Label labelErrorNota;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	private ObservableList<Categoria> obsList;

	public void setSeries(Series entity) {
		this.entity = entity;
	}

	public void setServices(SeriesService service, CategoriaService categoriaService) {
		this.service = service;
		this.categoriaService = categoriaService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Series getFormData() {
		Series obj = new Series();

		ValidationException exception = new ValidationException("Validation error");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("nome", "Field can't be empty");
		}
		obj.setNome(txtNome.getText());

		if (txtTemporadas.getText() == null || txtTemporadas.getText().trim().equals("")) {
			exception.addError("temporadas", "Field can't be empty");
		}
		obj.setTemporadas(Utils.tryParseToInt(txtTemporadas.getText()));

		if (dpAdicionadoEm.getValue() == null) {
			exception.addError("adicionadoEm", "Field can't be empty");
		} 
		else {
			Instant instant = Instant.from(dpAdicionadoEm.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setAdicionadoEm(Date.from(instant));
		}

		if (txtNota.getText() == null || txtNota.getText().trim().equals("")) {
			exception.addError("Nota", "Field can't be empty");
		}
		obj.setNota(Utils.tryParseToDouble(txtNota.getText()));
		
		obj.setCategoria(comboBoxCategoria.getValue());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 70);
		Constraints.setTextFieldDouble(txtNota);
		Constraints.setTextFieldInteger(txtTemporadas);
		Utils.formatDatePicker(dpAdicionadoEm, "dd/MM/yyyy");

		initializeComboBoxCategoria();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getNome());
		txtTemporadas.setText(String.valueOf(entity.getTemporadas()));
		Locale.setDefault(Locale.US);
		txtNota.setText(String.format("%.2f", entity.getNota()));
		if (entity.getAdicionadoEm() != null) {
			dpAdicionadoEm.setValue(LocalDate.ofInstant(entity.getAdicionadoEm().toInstant(), ZoneId.systemDefault()));
		}
		if (entity.getCategoria() == null) {
			comboBoxCategoria.getSelectionModel().selectFirst();
		} else {
			comboBoxCategoria.setValue(entity.getCategoria());
		}
	}

	public void loadAssociatedObjects() {
		if (categoriaService == null) {
			throw new IllegalStateException("CategoriaService was null");
		}
		List<Categoria> list = categoriaService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxCategoria.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		labelErrorNome.setText((fields.contains("nome") ? errors.get("nome"): ""));
		labelErrorTemporadas.setText((fields.contains("temporadas") ? errors.get("temporadas"): ""));
		labelErrorAdicionadoEm.setText((fields.contains("adicionadoEm") ? errors.get("adicionadoEm"): ""));
		labelErrorNota.setText((fields.contains("nota") ? errors.get("nota"): ""));

	}

	private void initializeComboBoxCategoria() {
		Callback<ListView<Categoria>, ListCell<Categoria>> factory = lv -> new ListCell<Categoria>() {
			@Override
			protected void updateItem(Categoria item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxCategoria.setCellFactory(factory);
		comboBoxCategoria.setButtonCell(factory.call(null));
	}

}
