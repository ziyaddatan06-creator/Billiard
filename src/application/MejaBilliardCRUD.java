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

public class MejaBilliardCRUD {
	private static TableView<MejaBilliard> table = new TableView<>();
	private static ObservableList<MejaBilliard> mejaList = FXCollections.observableArrayList();

	public static void show() {
		Stage stage = new Stage();
		VBox root = new VBox();
		root.setPadding(new Insets(10));
		root.setSpacing(10);

		table = new TableView<>();

		TableColumn<MejaBilliard, Integer> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());

		TableColumn<MejaBilliard, Integer> colNomor = new TableColumn<>("Nomor Meja");
		colNomor.setCellValueFactory(data -> data.getValue().nomorMejaProperty().asObject());

		TableColumn<MejaBilliard, String> colTipe = new TableColumn<>("Tipe");
		colTipe.setCellValueFactory(data -> data.getValue().tipeProperty());
		colTipe.setPrefWidth(100);

		TableColumn<MejaBilliard, String> colStatus = new TableColumn<>("Status");
		colStatus.setCellValueFactory(data -> data.getValue().statusProperty());
		colStatus.setPrefWidth(100);

		TableColumn<MejaBilliard, String> colHarga = new TableColumn<>("Harga/Jam");
		colHarga.setCellValueFactory(data -> data.getValue().hargaProperty());
		colHarga.setPrefWidth(100);

		TableColumn<MejaBilliard, String> colLokasi = new TableColumn<>("Lokasi");
		colLokasi.setCellValueFactory(data -> data.getValue().lokasiProperty());
		colLokasi.setPrefWidth(100);

		table.getColumns().addAll(colId, colNomor, colTipe, colStatus, colHarga, colLokasi);
		table.setItems(mejaList);

		Button addButton = new Button("Tambah");
		Button editButton = new Button("Edit");
		Button deleteButton = new Button("Hapus");
		HBox buttonBox = new HBox(10, addButton, editButton, deleteButton);

		addButton.setOnAction(e -> showAddForm());
		editButton.setOnAction(e -> showEditForm());
		deleteButton.setOnAction(e -> deleteData());

		root.getChildren().addAll(table, buttonBox);

		Scene scene = new Scene(root, 700, 400);
		stage.setScene(scene);
		stage.setTitle("Data Meja Billiard");
		stage.show();

		loadData();
	}

	private static void loadData() {
		mejaList.clear();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM meja_billiard ORDER BY nomor_meja")) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				MejaBilliard m = new MejaBilliard(
						rs.getInt("id_meja"),
						rs.getInt("nomor_meja"),
						rs.getString("tipe"),
						rs.getString("status"),
						rs.getString("harga"),
						rs.getString("lokasi"));
				mejaList.add(m);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "Gagal memuat data: " + e.getMessage());
		}
	}

	private static void showAddForm() {
		Stage formStage = new Stage();
		formStage.setTitle("Tambah Meja Billiard");

		GridPane grid = createFormLayout();

		TextField nomorField = new TextField();
		ComboBox<String> tipeCombo = new ComboBox<>();
		tipeCombo.getItems().addAll("Standard", "VIP", "Premium");
		ComboBox<String> statusCombo = new ComboBox<>();
		statusCombo.getItems().addAll("Tersedia", "Terpakai", "Maintenance");
		statusCombo.setValue("Tersedia");
		TextField hargaField = new TextField();
		ComboBox<String> lokasiCombo = new ComboBox<>();
		lokasiCombo.getItems().addAll("Lantai 1", "Lantai 2", "Lantai 3");

		grid.add(new Label("Nomor Meja:"), 0, 0);
		grid.add(nomorField, 1, 0);
		grid.add(new Label("Tipe:"), 0, 1);
		grid.add(tipeCombo, 1, 1);
		grid.add(new Label("Status:"), 0, 2);
		grid.add(statusCombo, 1, 2);
		grid.add(new Label("Harga/Jam:"), 0, 3);
		grid.add(hargaField, 1, 3);
		grid.add(new Label("Lokasi:"), 0, 4);
		grid.add(lokasiCombo, 1, 4);

		Button saveButton = new Button("Simpan");
		saveButton.setOnAction(e -> {
			String nomorStr = nomorField.getText().trim();
			String tipe = tipeCombo.getValue();
			String status = statusCombo.getValue();
			String harga = hargaField.getText().trim();
			String lokasi = lokasiCombo.getValue();

			if (nomorStr.isEmpty()) {
				showAlert("Validasi", "Nomor meja tidak boleh kosong!");
				return;
			}

			try {
				int nomor = Integer.parseInt(nomorStr);
				try (Connection conn = DBConnection.getConnection();
						PreparedStatement stmt = conn.prepareStatement(
								"INSERT INTO meja_billiard (nomor_meja, tipe, status, harga, lokasi) VALUES (?, ?, ?, ?, ?)")) {
					stmt.setInt(1, nomor);
					stmt.setString(2, tipe);
					stmt.setString(3, status);
					stmt.setString(4, harga);
					stmt.setString(5, lokasi);
					stmt.executeUpdate();
					loadData();
					formStage.close();
				}
			} catch (NumberFormatException ex) {
				showAlert("Validasi", "Nomor meja harus berupa angka!");
			} catch (SQLException ex) {
				ex.printStackTrace();
				showAlert("Error", "Gagal menyimpan: " + ex.getMessage());
			}
		});

		VBox formRoot = new VBox(10, grid, saveButton);
		formRoot.setPadding(new Insets(10));
		Scene scene = new Scene(formRoot, 350, 250);
		formStage.setScene(scene);
		formStage.show();
	}

	private static void showEditForm() {
		MejaBilliard selected = table.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Pilih Data", "Silakan pilih meja yang akan diedit.");
			return;
		}

		Stage formStage = new Stage();
		formStage.setTitle("Edit Meja Billiard");

		GridPane grid = createFormLayout();

		TextField nomorField = new TextField(String.valueOf(selected.getNomorMeja()));
		ComboBox<String> tipeCombo = new ComboBox<>();
		tipeCombo.getItems().addAll("Standard", "VIP", "Premium");
		tipeCombo.setValue(selected.getTipe());
		ComboBox<String> statusCombo = new ComboBox<>();
		statusCombo.getItems().addAll("Tersedia", "Terpakai", "Maintenance");
		statusCombo.setValue(selected.getStatus());
		TextField hargaField = new TextField(selected.getHarga());
		ComboBox<String> lokasiCombo = new ComboBox<>();
		lokasiCombo.getItems().addAll("Lantai 1", "Lantai 2", "Lantai 3");
		lokasiCombo.setValue(selected.getLokasi());

		grid.add(new Label("Nomor Meja:"), 0, 0);
		grid.add(nomorField, 1, 0);
		grid.add(new Label("Tipe:"), 0, 1);
		grid.add(tipeCombo, 1, 1);
		grid.add(new Label("Status:"), 0, 2);
		grid.add(statusCombo, 1, 2);
		grid.add(new Label("Harga/Jam:"), 0, 3);
		grid.add(hargaField, 1, 3);
		grid.add(new Label("Lokasi:"), 0, 4);
		grid.add(lokasiCombo, 1, 4);

		Button saveButton = new Button("Simpan");
		saveButton.setOnAction(e -> {
			String nomorStr = nomorField.getText().trim();
			String tipe = tipeCombo.getValue();
			String status = statusCombo.getValue();
			String harga = hargaField.getText().trim();
			String lokasi = lokasiCombo.getValue();

			if (nomorStr.isEmpty()) {
				showAlert("Validasi", "Nomor meja tidak boleh kosong!");
				return;
			}

			try {
				int nomor = Integer.parseInt(nomorStr);
				try (Connection conn = DBConnection.getConnection();
						PreparedStatement stmt = conn.prepareStatement(
								"UPDATE meja_billiard SET nomor_meja = ?, tipe = ?, status = ?, harga = ?, lokasi = ? WHERE id_meja = ?")) {
					stmt.setInt(1, nomor);
					stmt.setString(2, tipe);
					stmt.setString(3, status);
					stmt.setString(4, harga);
					stmt.setString(5, lokasi);
					stmt.setInt(6, selected.getId());
					stmt.executeUpdate();
					loadData();
					formStage.close();
				}
			} catch (NumberFormatException ex) {
				showAlert("Validasi", "Nomor meja harus berupa angka!");
			} catch (SQLException ex) {
				ex.printStackTrace();
				showAlert("Error", "Gagal menyimpan: " + ex.getMessage());
			}
		});

		VBox formRoot = new VBox(10, grid, saveButton);
		formRoot.setPadding(new Insets(10));
		Scene scene = new Scene(formRoot, 350, 250);
		formStage.setScene(scene);
		formStage.show();
	}

	private static void deleteData() {
		MejaBilliard selected = table.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Pilih Data", "Silakan pilih meja yang akan dihapus.");
			return;
		}

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Konfirmasi Hapus");
		confirm.setContentText("Apakah Anda yakin ingin menghapus Meja " + selected.getNomorMeja() + "?");
		confirm.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				try (Connection conn = DBConnection.getConnection();
						PreparedStatement stmt = conn.prepareStatement(
								"DELETE FROM meja_billiard WHERE id_meja = ?")) {
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
