import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

public class Model {
	private List<String[]> transactionList = new ArrayList<>();
	private String user = "root";
	private String pswd = "";
	private String url = "jdbc:mysql://localhost/AdvancedDBFinal";
	private ObservableList<ObservableList> data;
	private ObservableList<String> comboBoxValues = FXCollections.observableArrayList(
			"Rent", "Utilities", "Groceries", "Eating Out", "Gifts"
	);

	public void ImportFile() throws Exception {
		FileChooser csvFileChooser = new FileChooser();
		csvFileChooser.setTitle("Select a CSV File");
		csvFileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
		File selectedFile = csvFileChooser.showOpenDialog(null);
		if (selectedFile != null) {
			System.out.println("File Successfully Imported");
			readFile(selectedFile);
		} else {
			System.out.print("Unable to import file");
		}
	}

	public void readFile(File csvFile) throws Exception {
		String line;
		String delimiter = ",";

		BufferedReader reader = new BufferedReader(new FileReader(csvFile));
		while ((line = reader.readLine()) != null) {
			String[] entry = line.split(delimiter);
			entry[0] = entry[0].replace("\"", "");
			entry[2] = entry[2].replace("\"", "");
			entry[4] = entry[4].replace("\"", "");
			transactionList.add(entry);
			//System.out.println("Date: " + entry[0] + "   Memo: " + entry[3] + "   Amount: " + entry[4]);
		}

		ConnectToDb();
	}

	public Connection ConnectToDb() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");

		Connection connection = DriverManager.getConnection(url, user, pswd);

		System.out.println("Connection Successful");

		return connection;
	}

	public void importData(Connection connection) {
		// Output that the program is attempting to send queries
		System.out.println("Attempting to send queries...");
		// Create a string array to hold row data. This will be placed inside the List<String[]>
		String[] entry;

		// Create a prepared statement and a query to be used
		PreparedStatement statement;
		String query = "INSERT INTO advanceddbfinal.finalproject (ID, Date, Name, Amount) VALUES (?, ?, ?, ?)";

		// Iterate through each item in the list
		for (int i = 1; i < transactionList.size(); i++) {
			entry = transactionList.get(i);
			String temp = "DEBIT PURCHASE";
			String temp2 = "-";
			String temp3 = "VISA";
			String temp4 = " ";

			// Remove repetitive strings from transaction names
			if (entry[2].length() > 14) {
				String compareTemp = entry[2].substring(0, 14);
				// Remove "DEBIT PURCHASE"
				if (temp.equals(compareTemp)) {
					entry[2] = entry[2].substring(15);
				}

				// Remove "-"
				if (temp2.equals(entry[2].substring(0, 1))){
					entry[2] = entry[2].substring(1);
				}

				// Remove "VISA"
				if (temp3.equals(entry[2].substring(0, 4))){
					entry[2] = entry[2].substring(4);
				}

				// Remove " "
				if (temp4.equals(entry[2].substring(0, 1))){
					entry[2] = entry[2].substring(1);
				}
			}

			// Remove excess zeros from amounts if present
			for (int j = 0; j < entry[4].length() - 1; j++){
				if (entry[4].charAt(j) == '.'){
					entry[4] = entry[4].substring(0, j + 3);
				}

			}

			try {
				// Execute queries on refined transactionList
				statement = connection.prepareStatement(query);
				statement.setInt(1, i);
				statement.setString(2, entry[0]);
				statement.setString(3, entry[2]);
				statement.setString(4, entry[4]);

				statement.execute();

			} catch (Exception ex) {
				System.err.print(ex);
			}
		}

		System.out.println("Queries Sent");
	}

	public boolean hasData() throws Exception {
		String SQL = "SELECT * FROM finalproject";
		boolean returnStatement = false;

		try {
			Connection connection = DriverManager.getConnection(url, user, pswd);
			ResultSet rs = connection.createStatement().executeQuery(SQL);

			if (rs.next() == false){
				returnStatement = false;
			} else {
				returnStatement = true;
			}
		} catch (Exception ex){
			System.err.print(ex);
		}
		return returnStatement;
	}

	// Display the data in the table by getting each tuple in the database. Code built off of Narayan G. Maharjan's version.
	public TableView displayData(Connection connection, TableView tableView) throws Exception {

		String SQL = "SELECT * FROM finalproject";
		data = FXCollections.observableArrayList();

		try {
			ResultSet rs = connection.createStatement().executeQuery(SQL);

			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				final int j = i;
				TableColumn column = new TableColumn(rs.getMetaData().getColumnName(i + 1));
				column.setCellValueFactory((Callback<CellDataFeatures<ObservableList, String>,
						ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
				tableView.getColumns().addAll(column);
			}

			while (rs.next()) {
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					row.add(rs.getString(i));
				}
				data.add(row);
			}

			tableView.setItems(data);
		} catch (Exception ex) {
			System.err.print(ex);
		}
		return tableView;
	}

	public TableView autoResizeColumns(TableView<?> table) {
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		table.getColumns().stream().forEach((column) -> {
			Text t = new Text(column.getText());
			double max = t.getLayoutBounds().getWidth();
			for (int i = 0; i < table.getItems().size(); i++){
				if (column.getCellData(i) != null){
					t = new Text(column.getCellData(i).toString());
					double calcwidth = t.getLayoutBounds().getWidth();
					if (calcwidth > max) {
						max = calcwidth;
					}
				}
			}
			if (column.getText().equals("Category")){
				column.setPrefWidth(max + 60.0d);
			} else {
				column.setPrefWidth(max + 30.0d);
			}
		});
		return table;
	}

	public void updateComboBox(){


    }

    public TableView addComboBoxToTableView(TableView tableView){

		TableColumn<String, StringProperty> column = new TableColumn<>("Category");
		column.setCellValueFactory(new PropertyValueFactory<>("category"));

		column.setCellFactory(col -> {
			TableCell<String, StringProperty> c = new TableCell<>();
			final ComboBox<String> comboBox = new ComboBox<>(comboBoxValues);
			c.itemProperty().addListener((observable, oldValue, newValue) -> {
				if (oldValue != null) {
					comboBox.valueProperty().unbindBidirectional(oldValue);
				}
				if (newValue != null) {
					comboBox.valueProperty().bindBidirectional(newValue);
				}
			});
			c.graphicProperty().bind(Bindings.when(c.emptyProperty()).then((Node) null).otherwise(comboBox));
			comboBox.setValue("Select...");
			return c;

		});
		tableView.getColumns().add(column);
		//tableView.setEditable(true);

    	return tableView;
	}
}

