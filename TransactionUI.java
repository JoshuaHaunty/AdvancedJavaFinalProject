import javafx.geometry.Pos;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static javafx.application.Platform.exit;

public class TransactionUI extends Application {
	private Button importButton = new Button();
	private Button trendButton = new Button();
	private Button transactionButton = new Button();
	private Label entryHeader = new Label();

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle(" Budget Tracker");
		importButton.setText("Import");
		trendButton.setText("Trends");
		transactionButton.setText("Transactions");

		GridPane gridPane = new GridPane();
		gridPane.setHgap(5);
		gridPane.setVgap(5);
		gridPane.setPadding(new Insets(20));
		gridPane.add(importButton, 0, 0);
		gridPane.add(trendButton, 0,1);
		gridPane.add(transactionButton, 0, 2);

		primaryStage.setScene(new Scene(gridPane, 500, 350));
		primaryStage.show();

		/*importButton.setOnMouseReleased(e -> {
					try {
						importUI.start(stage);
					} catch (Exception ex) {
						System.err.print(ex);
					}
				}

		);

		trendButton.setOnMouseReleased(e -> {
			Stage stage = new Stage();
			TransactionUI transactionUI = new TransactionUI();
			try {
				transactionUI.start(stage);
			} catch (Exception ex){
				System.err.print(ex);
			}

		}); */
	}
}