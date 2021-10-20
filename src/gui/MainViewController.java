package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.CategoriaService;
import model.services.SeriesService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeries;

	@FXML
	private MenuItem menuItemCategoria;

	@FXML
	private MenuItem menuItemSobreOApp;

	@FXML
	public void onMenuItemSeriesAction() {
		loadView("/gui/SeriesList.fxml", (SeriesListController controller) -> {
			controller.setSeriesService(new SeriesService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemCategoriaAction() {
		loadView("/gui/CategoriaList.fxml", (CategoriatListController controller) -> {
			controller.setCategoriaService(new CategoriaService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemSobreOAppAction() {
		loadView("/gui/SobreOApp.fxml", x -> {});
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {

	}

	private synchronized <T> void loadView(String absolutName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
			VBox newVBox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			T controller = loader.getController();
		    initializingAction.accept(controller);
		}
		catch (IOException e) {
			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
		
	}
	
}
