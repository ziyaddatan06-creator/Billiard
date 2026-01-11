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

public class MenuCRUD {
	private static TableView<Menu> table = new TableView<>();
	private static ObservableList<Menu> menuList = FXCollections.observableArrayList();

	public static void show() {
		Stage stage = new Stage();
		VBox root = new VBox();
		root.setPadding(new Insets(10));
		root.setSpacing(10);

		table = new TableView<>();

		TableColumn<Menu, Integer> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());

		TableColumn<Menu, String> colNama = new TableColumn<>("Nama Item");
		colNama.setCellValueFactory(data -> data.getValue().namaItemProperty());
		colNama.setPrefWidth(150);

		TableColumn<Menu, String> colKategori = new TableColumn<>("Kategori");
		colKategori.setCellValueFactory(data -> data.getValue().kategoriProperty());
		colKategori.setPrefWidth(100);

		TableColumn<Menu, String> colHarga = new TableColumn<>("Harga");
		colHarga.setCellValueFactory(data -> data.getValue().hargaProperty());
		colHarga.setPrefWidth(100);

		TableColumn<Menu, Integer> colStok = new TableColumn<>("Stok");
		colStok.setCellValueFactory(data -> data.getValue().stokProperty().asObject());

		table.getColumns().addAll(colId, colNama, colKategori, colHarga, colStok);
		table.setItems(menuList);

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
		stage.setTitle("Data Menu");
		stage.show();

		loadData();
	}

	private static void loadData() {
		menuList.clear();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM menu ORDER BY kategori, nama_item")) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Menu m = new Menu(
						rs.getInt("id_menu"),
						rs.getString("nama_item"),
						rs.getString("kategori"),
						rs.getString("harga"),
						rs.getInt("stok"));
				menuList.add(m);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "Gagal memuat data: " + e.getMessage());
		}
	}

	private static void showAddForm() {
		Stage formStage = new Stage();
		formStage.setTitle("Tambah Menu");

		GridPane grid = createFormLayout();

		TextField namaField = new TextField();
		ComboBox<String> kategoriCombo = new ComboBox<>();
		kategoriCombo.getItems().addAll("Makanan", "Minuman", "Snack");
		TextField hargaField = new TextField();
		TextField stokField = new TextField();

		grid.add(new Label("Nama Item:"), 0, 0);
		grid.add(namaField, 1, 0);
		grid.add(new Label("Kategori:"), 0, 1);
		grid.add(kategoriCombo, 1, 1);
		grid.add(new Label("Harga:"), 0, 2);
		grid.add(hargaField, 1, 2);
		grid.add(new Label("Stok:"), 0, 3);
		grid.add(stokField, 1, 3);

		Button saveButton = new Button("Simpan");
		saveButton.setOnAction(e -> {
			String nama = namaField.getText().trim();
			String kategori = kategoriCombo.getValue();
			String harga = hargaField.getText().trim();
			String stokStr = stokField.getText().trim();

			if (nama.isEmpty()) {
				showAlert("Validasi", "Nama item tidak boleh kosong!");
				return;
			}

			try {
				int stok = stokStr.isEmpty() ? 0 : Integer.parseInt(stokStr);
				try (Connection conn = DBConnection.getConnection();
						PreparedStatement stmt = conn.prepareStatement(
								"INSERT INTO menu (nama_item, kategori, harga, stok) VALUES (?, ?, ?, ?)")) {
					stmt.setString(1, nama);
					stmt.setString(2, kategori);
					stmt.setString(3, harga);
					stmt.setInt(4, stok);
					stmt.executeUpdate();
					loadData();
					formStage.close();
				}
			} catch (NumberFormatException ex) {
				showAlert("Validasi", "Stok harus berupa angka!");
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
		Menu selected = table.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Pilih Data", "Silakan pilih menu yang akan diedit.");
			return;
		}

		Stage formStage = new Stage();
		formStage.setTitle("Edit Menu");

		GridPane grid = createFormLayout();

		TextField namaField = new TextField(selected.getNamaItem());
		ComboBox<String> kategoriCombo = new ComboBox<>();
		kategoriCombo.getItems().addAll("Makanan", "Minuman", "Snack");
		kategoriCombo.setValue(selected.getKategori());
		TextField hargaField = new TextField(selected.getHarga());
		TextField stokField = new TextField(String.valueOf(selected.getStok()));

		grid.add(new Label("Nama Item:"), 0, 0);
		grid.add(namaField, 1, 0);
		grid.add(new Label("Kategori:"), 0, 1);
		grid.add(kategoriCombo, 1, 1);
		grid.add(new Label("Harga:"), 0, 2);
		grid.add(hargaField, 1, 2);
		grid.add(new Label("Stok:"), 0, 3);
		grid.add(stokField, 1, 3);

		Button saveButton = new Button("Simpan");
		saveButton.setOnAction(e -> {
			String nama = namaField.getText().trim();
			String kategori = kategoriCombo.getValue();
			String harga = hargaField.getText().trim();
			String stokStr = stokField.getText().trim();

			if (nama.isEmpty()) {
				showAlert("Validasi", "Nama item tidak boleh kosong!");
				return;
			}

			try {
				int stok = stokStr.isEmpty() ? 0 : Integer.parseInt(stokStr);
				try (Connection conn = DBConnection.getConnection();
						PreparedStatement stmt = conn.prepareStatement(
								"UPDATE menu SET nama_item = ?, kategori = ?, harga = ?, stok = ? WHERE id_menu = ?")) {
					stmt.setString(1, nama);
					stmt.setString(2, kategori);
					stmt.setString(3, harga);
					stmt.setInt(4, stok);
					stmt.setInt(5, selected.getId());
					stmt.executeUpdate();
					loadData();
					formStage.close();
				}
			} catch (NumberFormatException ex) {
				showAlert("Validasi", "Stok harus berupa angka!");
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
		Menu selected = table.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Pilih Data", "Silakan pilih menu yang akan dihapus.");
			return;
		}

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Konfirmasi Hapus");
		confirm.setContentText("Apakah Anda yakin ingin menghapus " + selected.getNamaItem() + "?");
		confirm.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				try (Connection conn = DBConnection.getConnection();
						PreparedStatement stmt = conn.prepareStatement(
								"DELETE FROM menu WHERE id_menu = ?")) {
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
