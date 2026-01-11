package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PelangganCRUD {
	private static TableView<Pelanggan> table = new TableView<>();
	private static ObservableList<Pelanggan> pelangganList = FXCollections.observableArrayList();

	public static void show() {
		Stage stage = new Stage();
		VBox root = new VBox();
		root.setPadding(new Insets(10));
		root.setSpacing(10);

		// Tabel untuk menampilkan data pelanggan
		table = new TableView<>();

		TableColumn<Pelanggan, Integer> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());

		TableColumn<Pelanggan, String> colNama = new TableColumn<>("Nama");
		colNama.setCellValueFactory(data -> data.getValue().namaProperty());
		colNama.setPrefWidth(200);

		TableColumn<Pelanggan, String> colTelp = new TableColumn<>("No. Telepon");
		colTelp.setCellValueFactory(data -> data.getValue().noTeleponProperty());
		colTelp.setPrefWidth(150);

		table.getColumns().addAll(colId, colNama, colTelp);
		table.setItems(pelangganList);

		// Tombol CRUD
		Button addButton = new Button("Tambah");
		Button editButton = new Button("Edit");
		Button deleteButton = new Button("Hapus");
		HBox buttonBox = new HBox(10, addButton, editButton, deleteButton);

		// Event handlers
		addButton.setOnAction(e -> showAddForm());
		editButton.setOnAction(e -> showEditForm());
		deleteButton.setOnAction(e -> deleteData());

		root.getChildren().addAll(table, buttonBox);

		Scene scene = new Scene(root, 500, 400);
		stage.setScene(scene);
		stage.setTitle("Data Pelanggan");
		stage.show();

		loadData();
	}

	private static void loadData() {
		pelangganList.clear();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM pelanggan ORDER BY id_pelanggan")) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Pelanggan p = new Pelanggan(
						rs.getInt("id_pelanggan"),
						rs.getString("nama"),
						rs.getString("no_telepon"));
				pelangganList.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "Gagal memuat data: " + e.getMessage());
		}
	}

	private static void showAddForm() {
		Stage formStage = new Stage();
		formStage.setTitle("Tambah Pelanggan");

		GridPane grid = createFormLayout();

		TextField namaField = new TextField();
		TextField telpField = new TextField();

		grid.add(new Label("Nama:"), 0, 0);
		grid.add(namaField, 1, 0);
		grid.add(new Label("No. Telepon:"), 0, 1);
		grid.add(telpField, 1, 1);

		Button saveButton = new Button("Simpan");
		saveButton.setOnAction(e -> {
			String nama = namaField.getText().trim();
			String telp = telpField.getText().trim();

			if (nama.isEmpty()) {
				showAlert("Validasi", "Nama tidak boleh kosong!");
				return;
			}

			try (Connection conn = DBConnection.getConnection();
					PreparedStatement stmt = conn.prepareStatement(
							"INSERT INTO pelanggan (nama, no_telepon) VALUES (?, ?)")) {
				stmt.setString(1, nama);
				stmt.setString(2, telp);
				stmt.executeUpdate();
				loadData();
				formStage.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				showAlert("Error", "Gagal menyimpan: " + ex.getMessage());
			}
		});

		VBox formRoot = new VBox(10, grid, saveButton);
		formRoot.setPadding(new Insets(10));
		Scene scene = new Scene(formRoot, 300, 150);
		formStage.setScene(scene);
		formStage.show();
	}

	private static void showEditForm() {
		Pelanggan selected = table.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Pilih Data", "Silakan pilih pelanggan yang akan diedit.");
			return;
		}

		Stage formStage = new Stage();
		formStage.setTitle("Edit Pelanggan");

		GridPane grid = createFormLayout();

		TextField namaField = new TextField(selected.getNama());
		TextField telpField = new TextField(selected.getNoTelepon());

		grid.add(new Label("Nama:"), 0, 0);
		grid.add(namaField, 1, 0);
		grid.add(new Label("No. Telepon:"), 0, 1);
		grid.add(telpField, 1, 1);

		Button saveButton = new Button("Simpan");
		saveButton.setOnAction(e -> {
			String nama = namaField.getText().trim();
			String telp = telpField.getText().trim();

			if (nama.isEmpty()) {
				showAlert("Validasi", "Nama tidak boleh kosong!");
				return;
			}

			try (Connection conn = DBConnection.getConnection();
					PreparedStatement stmt = conn.prepareStatement(
							"UPDATE pelanggan SET nama = ?, no_telepon = ? WHERE id_pelanggan = ?")) {
				stmt.setString(1, nama);
				stmt.setString(2, telp);
				stmt.setInt(3, selected.getId());
				stmt.executeUpdate();
				loadData();
				formStage.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				showAlert("Error", "Gagal menyimpan: " + ex.getMessage());
			}
		});

		VBox formRoot = new VBox(10, grid, saveButton);
		formRoot.setPadding(new Insets(10));
		Scene scene = new Scene(formRoot, 300, 150);
		formStage.setScene(scene);
		formStage.show();
	}

	private static void deleteData() {
		Pelanggan selected = table.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Pilih Data", "Silakan pilih pelanggan yang akan dihapus.");
			return;
		}

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Konfirmasi Hapus");
		confirm.setContentText("Apakah Anda yakin ingin menghapus " + selected.getNama() + "?");
		confirm.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				try (Connection conn = DBConnection.getConnection();
						PreparedStatement stmt = conn.prepareStatement(
								"DELETE FROM pelanggan WHERE id_pelanggan = ?")) {
					stmt.setInt(1, selected.getId());
					stmt.executeUpdate();
					loadData();
				} catch (SQLException e) {
					e.printStackTrace();
					showAlert("Error", "Gagal menghapus: " + e.getMessage());
				}
			}
		});
	}

	private static GridPane createFormLayout() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		return grid;
	}

	private static void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
