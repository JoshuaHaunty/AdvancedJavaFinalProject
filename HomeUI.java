import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

import static javafx.geometry.Side.BOTTOM;
import static javafx.geometry.Side.LEFT;

public class HomeUI extends Application {
	private TableView transactionTable = new TableView();
	private TableView categoryTable = new TableView();
	private Button importButton = new Button("Import");
	private Button trendButton = new Button("Trends");
	private Button transactionButton = new Button("Transactions");
	private final CategoryAxis categoryAxis = new CategoryAxis();
	private final NumberAxis numberAxis = new NumberAxis();
	private BarChart<String, Number> barChart = new BarChart<>(categoryAxis, numberAxis);
	private Label newCategoryLabel = new Label("New Category:   ");
	private Label removeCategoryLabel = new Label("      Remove By Name:   ");
	private TextField newCategoryTextField = new TextField();
	private TextField removeCategoryTextField = new TextField();
	private Button newCategoryButton = new Button(" Submit ");
	private Button removeCategoryButton = new Button("Remove ");
	private ComboBox<String> comboBox = new ComboBox();

	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Create a Model object
		Model model = new Model();

		// Create Anchor pane
		AnchorPane anchorPane = new AnchorPane();
		anchorPane.setPrefHeight(668.0);
		anchorPane.setPrefWidth(1112.0);
		anchorPane.setStyle("-fx-background-color: #545e75");

		// VBox to hold all buttons
		VBox vBox = new VBox();
		vBox.setPrefWidth(195);
		vBox.setPrefHeight(698);
		vBox.prefHeight(668.0);
		vBox.prefWidth(203.0);
		vBox.setStyle("-fx-background-color: #82a0bc");
		vBox.setLayoutX(0);
		vBox.setLayoutY(0);
		vBox.setAlignment(Pos.CENTER);

		// importButton settings
		importButton.setMnemonicParsing(false);
		importButton.setPrefWidth(300);
		importButton.setPrefHeight(80);
		importButton.setStyle("-fx-background-color: #CAC9CC; -fx-background-radius: 0;");

		// trendButton settings
		trendButton.setPrefWidth(300);
		trendButton.setPrefHeight(80);
		trendButton.setStyle("-fx-background-color: #BFBDC1; -fx-background-radius: 0");

		// transactionButton settings
		transactionButton.setPrefWidth(300);
		transactionButton.setPrefHeight(80);
		transactionButton.setStyle("-fx-background-color: #aeacb0; -fx-background-radius: 0");

		// Add buttons to the vBox
		vBox.getChildren().addAll(importButton, trendButton, transactionButton);

		// TableView settings
		transactionTable.setPrefHeight(508);
		transactionTable.setPrefWidth(800);
		transactionTable.setLayoutX(247);
		transactionTable.setLayoutY(30);
		transactionTable.setPlaceholder(new Label("Please import a bank statement to view transactions."));
		transactionTable.setEditable(false);

		// Settings for comboBox
		comboBox.setMaxWidth(90);
		comboBox.setValue("Select...");

		// Add and remove category UI
		HBox newCategoryHBox = new HBox();
		newCategoryHBox.getChildren().addAll(newCategoryLabel, newCategoryTextField, newCategoryButton);
		HBox removeCategoryHBox = new HBox();
		removeCategoryHBox.getChildren().addAll(removeCategoryLabel, comboBox, removeCategoryButton);
		VBox addAndRemoveVBox = new VBox();
		addAndRemoveVBox.getChildren().addAll(newCategoryHBox, removeCategoryHBox);
		addAndRemoveVBox.setLayoutX(447);
		addAndRemoveVBox.setLayoutY(570);
		addAndRemoveVBox.setMargin(newCategoryHBox, new Insets(10, 10, 10, 10));
		addAndRemoveVBox.setMargin(removeCategoryHBox, new Insets(10, 10, 10, 10));

		// HBox to hold graph and category tableview
		HBox hBox = new HBox();
		hBox.setLayoutX(203.0);
		hBox.setPrefWidth(905.0);
		hBox.setPrefHeight(660.0);

		// BarChart to display categories
		categoryAxis.setSide(BOTTOM);
		numberAxis.setSide(LEFT);
		barChart.setPrefHeight(460.0);
		barChart.setPrefWidth(561.0);
		//barChart.setPadding(new Insets(150, 100, 100,50 ));
		barChart.setStyle("-fx-background-color: #63adf2");
		barChart.setTitle("Category Comparison Graph");

		// TableView for displaying categories and the totals
		categoryTable.setPlaceholder(new Label("Please import and/or categorize transactions"));
		categoryTable.setPrefHeight(496.0);
		categoryTable.setPrefWidth(497.0);

		hBox.getChildren().addAll(barChart, categoryTable);
		hBox.setMargin(barChart, new Insets(150, 100, 100,50 ));
		hBox.setMargin(categoryTable, new Insets(75.0, 50.0, 75.0, 0));

		// If there is data in the database, display it
		if (model.hasData(model.ConnectToDb()) == true) {
			model.autoResizeColumns(model.addComboBoxToTableView(
					model.displayData(model.ConnectToDb(), transactionTable)));
			model.getComboBoxValues(model.ConnectToDb());
			comboBox.getItems().addAll(model.returnComboBoxValues());
		}


		// Add components to anchorPane
		anchorPane.getChildren().addAll(vBox, transactionTable, hBox, addAndRemoveVBox);
		hBox.setVisible(false);

		// Add anchorPane to scene and show it
		primaryStage.setTitle(" Budget Tracker");
		primaryStage.setScene(new Scene(anchorPane, 1212.0, 688.0));
		primaryStage.show();
		primaryStage.setResizable(false);

		importButton.setOnMouseReleased(e -> {
			try {
				model.ImportFile();
				model.importData(model.ConnectToDb());
				model.autoResizeColumns(model.addComboBoxToTableView(model.displayData(model.ConnectToDb(), transactionTable)));
			} catch (Exception ex) {
				System.err.print(ex);
			}
		});

		trendButton.setOnMouseReleased(e -> {
			try {
				if (model.hasData(model.ConnectToDb()) == true && hBox.isVisible() == false) {
					model.autoResizeColumns(model.displayCategoryData(model.ConnectToDb(), categoryTable, model.getCategories(model.ConnectToDb())));
				}
				transactionTable.setVisible(false);
				hBox.setVisible(true);
				addAndRemoveVBox.setVisible(false);
			} catch (Exception ex){
				System.err.print(ex);
			}
		});

		transactionButton.setOnMouseReleased(e -> {
			try {
				if (model.hasData(model.ConnectToDb()) == true && transactionTable.isVisible() == false) {
					model.autoResizeColumns(model.addComboBoxToTableView(model.displayData(model.ConnectToDb(), transactionTable)));
				}
				hBox.setVisible(false);
				transactionTable.setVisible(true);
				addAndRemoveVBox.setVisible(true);
			} catch (Exception ex){
				System.err.print(ex);
			}

		});

		newCategoryButton.setOnMouseReleased(e ->{
			try {
				model.addCategory(model.ConnectToDb(), newCategoryTextField);
				model.getComboBoxValues(model.ConnectToDb());
				comboBox.getItems().removeAll(comboBox.getItems());
				comboBox.getItems().addAll(model.returnComboBoxValues());
			} catch (Exception ex){
				System.err.print(ex);
			}
		});

		removeCategoryButton.setOnMouseReleased(e ->{
			try {
				model.removeCategory(model.ConnectToDb(), comboBox);
				model.getComboBoxValues(model.ConnectToDb());
				comboBox.getItems().removeAll(comboBox.getItems());
				comboBox.getItems().addAll(model.returnComboBoxValues());
				comboBox.setValue("Select...");
			} catch (Exception ex){
				System.err.print(ex);
			}
		});
	}
}