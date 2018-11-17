import javafx.geometry.Pos;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HomeUI extends Application {
	private TableView transactionTable = new TableView();
	private Button importButton = new Button("Import");
	private Button trendButton = new Button("Trends");
	private Button transactionButton = new Button("Transactions");

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
		vBox.setPrefHeight(668);
		vBox.prefHeight(668);
		vBox.prefWidth(203);
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
		transactionTable.setPrefHeight(568);
		transactionTable.setPrefWidth(694);
		transactionTable.setLayoutX(247);
		transactionTable.setLayoutY(50);
		transactionTable.setStyle("-fx-background-color: CAC9CC;");
		transactionTable.setEditable(false);

		// Add components to anchorPane
		anchorPane.getChildren().addAll(vBox, transactionTable);

		// Add anchorPane to scene and show it
		primaryStage.setTitle(" Budget Tracker");
		primaryStage.setScene(new Scene(anchorPane));
		primaryStage.show();

		importButton.setOnMouseReleased(e -> {
			try {
				model.ImportFile();
				model.importData(model.ConnectToDb());
				model.autoResizeColumns(model.displayData(model.ConnectToDb(), transactionTable));
			} catch (Exception ex) {
				System.err.print(ex);
			}
		});

		trendButton.setOnMouseReleased(e -> {
			Stage stage = new Stage();
			TransactionUI transactionUI = new TransactionUI();
			try {
				transactionUI.start(stage);
			} catch (Exception ex) {
				System.err.print(ex);
			}
		});
	}
}