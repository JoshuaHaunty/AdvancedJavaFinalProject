import javafx.geometry.Pos;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomeUI extends Application {
	private Button importButton = new Button();
	private Button trendButton = new Button();
	private Button transactionButton = new Button();
	private TableView transactionTable = new TableView();

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Import the model
		Model model = new Model();
		// Set the text of defined fields
		primaryStage.setTitle(" Budget Tracker");
		importButton.setText("Import");
		trendButton.setText("Trends");
		transactionButton.setText("Transactions");

		transactionTable.setEditable(false);

		transactionTable = model.displayData(model.ConnectToDb(), transactionTable);
		model.autoResizeColumns(transactionTable);

		final HBox transactionHBox = new HBox();
		transactionHBox.setSpacing(5);
		transactionHBox.setPadding(new Insets(10, 0, 0, 10));
		transactionHBox.getChildren().addAll(transactionTable);

		VBox buttonVBox = new VBox();
		buttonVBox.getChildren().addAll(importButton, trendButton, transactionButton);
		buttonVBox.setAlignment(Pos.CENTER);

		HBox stageHBox = new HBox();
		stageHBox.getChildren().addAll(buttonVBox, transactionHBox);
		stageHBox.setAlignment(Pos.CENTER);

		primaryStage.setScene(new Scene(stageHBox, 500, 350));
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