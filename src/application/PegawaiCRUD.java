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

public class PegawaiCRUD {
	private static TableView<Pegawai> table = new TableView<>();
	private static ObservableList<Pegawai> pegawaiList = FXCollections.observableArrayList();

	public static void show() {
		Stage stage = new Stage();
		VBox root = new VBox();
		root.setPadding(new Insets(10));
		root.setSpacing(10);

		table = new TableView<>();

		TableColumn<Pegawai, Integer> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());

		TableColumn<Pegawai, String> colNama = new TableColumn<>("Nama");
		colNama.setCellValueFactory(data -> data.getValue().namaProperty());
		colNama.setPrefWidth(150);

		TableColumn<Pegawai, String> colJabatan = new TableColumn<>("Jabatan");
		colJabatan.setCellValueFactory(data -> data.getValue().jabatanProperty());
		colJabatan.setPrefWidth(100);

		TableColumn<Pegawai, String> colShift = new TableColumn<>("Shift");
		colShift.setCellValueFactory(data -> data.getValue().shiftProperty());
		colShift.setPrefWidth(80);

		TableColumn<Pegawai, String> colGaji = new TableColumn<>("Gaji");
		colGaji.setCellValueFactory(data -> data.getValue().gajiProperty());
		colGaji.setPrefWidth(100);

		table.getColumns().addAll(colId, colNama, colJabatan, colShift, colGaji);
		table.setItems(pegawaiList);

		Button addButton = new Button("Tambah");
		Button editButton = new Button("Edit");
		Button deleteButton = new Button("Hapus");
		HBox buttonBox = new HBox(10, addButton, editButton, deleteButton);

		addButton.setOnAction(e -> showAddForm());
		editButton.setOnAction(e -> showEditForm());
		deleteButton.setOnAction(e -> deleteData());

		root.getChildren().addAll(table, buttonBox);

		Scene scene = new Scene(root, 600, 400);
		stage.setScene(scene);
		stage.setTitle("Data Pegawai");
		stage.show();

		loadData();
	}

	private static void loadData() {
		pegawaiList.clear();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM pegawai ORDER BY id_pegawai")) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Pegawai p = new Pegawai(
						rs.getInt("id_pegawai"),
						rs.getString("nama"),
						rs.getString("jabatan"),
						rs.getString("shift"),
						rs.getString("gaji"));
				pegawaiList.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "Gagal memuat data: " + e.getMessage());
		}
	}

	private static void showAddForm() {
		Stage formStage = new Stage();
		formStage.setTitle("Tambah Pegawai");

		GridPane grid = createFormLayout();

		TextField namaField = new TextField();
		ComboBox<String> jabatanCombo = new ComboBox<>();
		jabatanCombo.getItems().addAll("Kasir", "Pelayan", "Supervisor", "Manager");
		ComboBox<String> shiftCombo = new ComboBox<>();
		shiftCombo.getItems().addAll("Pagi", "Siang", "Malam");
		TextField gajiField = new TextField();

		grid.add(new Label("Nama:"), 0, 0);
		grid.add(namaField, 1, 0);
		grid.add(new Label("Jabatan:"), 0, 1);
		grid.add(jabatanCombo, 1, 1);
		grid.add(new Label("Shift:"), 0, 2);
		grid.add(shiftCombo, 1, 2);
		grid.add(new Label("Gaji:"), 0, 3);
		grid.add(gajiField, 1, 3);

		Button saveButton = new Button("Simpan");
		saveButton.setOnAction(e -> {
			String nama = namaField.getText().trim();
			String jabatan = jabatanCombo.getValue();
			String shift = shiftCombo.getValue();
			String gaji = gajiField.getText().trim();

			if (nama.isEmpty()) {
				showAlert("Validasi", "Nama tidak boleh kosong!");
				return;
			}

			try (Connection conn = DBConnection.getConnection();
					PreparedStatement stmt = conn.prepareStatement(
							"INSERT INTO pegawai (nama, jabatan, shift, gaji) VALUES (?, ?, ?, ?)")) {
				stmt.setString(1, nama);
				stmt.setString(2, jabatan);
				stmt.setString(3, shift);
				stmt.setString(4, gaji);
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
		Scene scene = new Scene(formRoot, 350, 220);
		formStage.setScene(scene);
		formStage.show();
	}

	private static void showEditForm() {
		Pegawai selected = table.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Pilih Data", "Silakan pilih pegawai yang akan diedit.");
			return;
		}

		Stage formStage = new Stage();
		formStage.setTitle("Edit Pegawai");

		GridPane grid = createFormLayout();

		TextField namaField = new TextField(selected.getNama());
		ComboBox<String> jabatanCombo = new ComboBox<>();
		jabatanCombo.getItems().addAll("Kasir", "Pelayan", "Supervisor", "Manager");
		jabatanCombo.setValue(selected.getJabatan());
		ComboBox<String> shiftCombo = new ComboBox<>();
		shiftCombo.getItems().addAll("Pagi", "Siang", "Malam");
		shiftCombo.setValue(selected.getShift());
		TextField gajiField = new TextField(selected.getGaji());

		grid.add(new Label("Nama:"), 0, 0);
		grid.add(namaField, 1, 0);
		grid.add(new Label("Jabatan:"), 0, 1);
		grid.add(jabatanCombo, 1, 1);
		grid.add(new Label("Shift:"), 0, 2);
		grid.add(shiftCombo, 1, 2);
		grid.add(new Label("Gaji:"), 0, 3);
		grid.add(gajiField, 1, 3);

		Button saveButton = new Button("Simpan");
		saveButton.setOnAction(e -> {
			String nama = namaField.getText().trim();
			String jabatan = jabatanCombo.getValue();
			String shift = shiftCombo.getValue();
			String gaji = gajiField.getText().trim();

			if (nama.isEmpty()) {
				showAlert("Validasi", "Nama tidak boleh kosong!");
				return;
			}

			try (Connection conn = DBConnection.getConnection();
					PreparedStatement stmt = conn.prepareStatement(
							"UPDATE pegawai SET nama = ?, jabatan = ?, shift = ?, gaji = ? WHERE id_pegawai = ?")) {
				stmt.setString(1, nama);
				stmt.setString(2, jabatan);
				stmt.setString(3, shift);
				stmt.setString(4, gaji);
				stmt.setInt(5, selected.getId());
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
		Scene scene = new Scene(formRoot, 350, 220);
		formStage.setScene(scene);
		formStage.show();
	}

	private static void deleteData() {
		Pegawai selected = table.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Pilih Data", "Silakan pilih pegawai yang akan dihapus.");
			return;
		}

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Konfirmasi Hapus");
		confirm.setContentText("Apakah Anda yakin ingin menghapus " + selected.getNama() + "?");
		confirm.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				try (Connection conn = DBConnection.getConnection();
						PreparedStatement stmt = conn.prepareStatement(
								"DELETE FROM pegawai WHERE id_pegawai = ?")) {
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
