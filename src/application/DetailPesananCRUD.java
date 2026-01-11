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

public class DetailPesananCRUD {
	private static TableView<DetailPesanan> table = new TableView<>();
	private static ObservableList<DetailPesanan> detailList = FXCollections.observableArrayList();
	private static ObservableList<Menu> menuList = FXCollections.observableArrayList();
	private static int currentPemesananId;

	public static void show(int idPemesanan) {
		currentPemesananId = idPemesanan;

		Stage stage = new Stage();
		VBox root = new VBox();
		root.setPadding(new Insets(10));
		root.setSpacing(10);

		Label titleLabel = new Label("Detail Pesanan untuk Pemesanan ID: " + idPemesanan);
		titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

		table = new TableView<>();

		TableColumn<DetailPesanan, String> colMenu = new TableColumn<>("Menu");
		colMenu.setCellValueFactory(data -> data.getValue().namaMenuProperty());
		colMenu.setPrefWidth(200);

		TableColumn<DetailPesanan, Integer> colJumlah = new TableColumn<>("Jumlah");
		colJumlah.setCellValueFactory(data -> data.getValue().jumlahProperty().asObject());

		TableColumn<DetailPesanan, String> colSubtotal = new TableColumn<>("Subtotal");
		colSubtotal.setCellValueFactory(data -> data.getValue().subtotalProperty());
		colSubtotal.setPrefWidth(120);

		table.getColumns().addAll(colMenu, colJumlah, colSubtotal);
		table.setItems(detailList);

		Button addButton = new Button("Tambah Item");
		Button deleteButton = new Button("Hapus Item");
		HBox buttonBox = new HBox(10, addButton, deleteButton);

		addButton.setOnAction(e -> showAddForm());
		deleteButton.setOnAction(e -> deleteData());

		root.getChildren().addAll(titleLabel, table, buttonBox);

		Scene scene = new Scene(root, 500, 350);
		stage.setScene(scene);
		stage.setTitle("Detail Pesanan");
		stage.show();

		loadMenuData();
		loadData();
	}

	private static void loadMenuData() {
		menuList.clear();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM menu WHERE stok > 0 ORDER BY kategori, nama_item")) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				menuList.add(new Menu(
						rs.getInt("id_menu"),
						rs.getString("nama_item"),
						rs.getString("kategori"),
						rs.getString("harga"),
						rs.getInt("stok")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void loadData() {
		detailList.clear();
		String sql = "SELECT dp.*, m.nama_item FROM detail_pesanan dp " +
				"JOIN menu m ON dp.id_menu = m.id_menu " +
				"WHERE dp.id_pemesanan = ?";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, currentPemesananId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				DetailPesanan dp = new DetailPesanan(
						rs.getInt("id_pemesanan"),
						rs.getInt("id_menu"),
						rs.getInt("jumlah"),
						rs.getString("subtotal"));
				dp.setNamaMenu(rs.getString("nama_item"));
				detailList.add(dp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "Gagal memuat data: " + e.getMessage());
		}
	}

	private static void showAddForm() {
		Stage formStage = new Stage();
		formStage.setTitle("Tambah Item Pesanan");

		GridPane grid = createFormLayout();

		ComboBox<Menu> menuCombo = new ComboBox<>();
		menuCombo.setItems(menuList);

		TextField jumlahField = new TextField("1");
		TextField subtotalField = new TextField();
		subtotalField.setEditable(false);

		// Auto calculate subtotal
		menuCombo.setOnAction(e -> calculateSubtotal(menuCombo, jumlahField, subtotalField));
		jumlahField.textProperty().addListener((obs, oldVal, newVal) ->
			calculateSubtotal(menuCombo, jumlahField, subtotalField));

		grid.add(new Label("Menu:"), 0, 0);
		grid.add(menuCombo, 1, 0);
		grid.add(new Label("Jumlah:"), 0, 1);
		grid.add(jumlahField, 1, 1);
		grid.add(new Label("Subtotal:"), 0, 2);
		grid.add(subtotalField, 1, 2);

		Button saveButton = new Button("Simpan");
		saveButton.setOnAction(e -> {
			Menu selectedMenu = menuCombo.getValue();
			String jumlahStr = jumlahField.getText().trim();
			String subtotal = subtotalField.getText().trim();

			if (selectedMenu == null) {
				showAlert("Validasi", "Pilih menu!");
				return;
			}

			try {
				int jumlah = Integer.parseInt(jumlahStr);

				if (jumlah > selectedMenu.getStok()) {
					showAlert("Validasi", "Stok tidak mencukupi! Stok tersedia: " + selectedMenu.getStok());
					return;
				}

				try (Connection conn = DBConnection.getConnection()) {
					// Insert or update detail
					PreparedStatement checkStmt = conn.prepareStatement(
							"SELECT * FROM detail_pesanan WHERE id_pemesanan = ? AND id_menu = ?");
					checkStmt.setInt(1, currentPemesananId);
					checkStmt.setInt(2, selectedMenu.getId());
					ResultSet rs = checkStmt.executeQuery();

					if (rs.next()) {
						// Update existing
						int existingJumlah = rs.getInt("jumlah");
						int newJumlah = existingJumlah + jumlah;
						int newSubtotal = Integer.parseInt(selectedMenu.getHarga().replaceAll("[^0-9]", "")) * newJumlah;

						PreparedStatement updateStmt = conn.prepareStatement(
								"UPDATE detail_pesanan SET jumlah = ?, subtotal = ? WHERE id_pemesanan = ? AND id_menu = ?");
						updateStmt.setInt(1, newJumlah);
						updateStmt.setString(2, String.valueOf(newSubtotal));
						updateStmt.setInt(3, currentPemesananId);
						updateStmt.setInt(4, selectedMenu.getId());
						updateStmt.executeUpdate();
					} else {
						// Insert new
						PreparedStatement insertStmt = conn.prepareStatement(
								"INSERT INTO detail_pesanan (id_pemesanan, id_menu, jumlah, subtotal) VALUES (?, ?, ?, ?)");
						insertStmt.setInt(1, currentPemesananId);
						insertStmt.setInt(2, selectedMenu.getId());
						insertStmt.setInt(3, jumlah);
						insertStmt.setString(4, subtotal);
						insertStmt.executeUpdate();
					}

					// Update stok menu
					PreparedStatement updateStok = conn.prepareStatement(
							"UPDATE menu SET stok = stok - ? WHERE id_menu = ?");
					updateStok.setInt(1, jumlah);
					updateStok.setInt(2, selectedMenu.getId());
					updateStok.executeUpdate();

					loadMenuData();
					loadData();
					formStage.close();
				}
			} catch (NumberFormatException ex) {
				showAlert("Validasi", "Jumlah harus berupa angka!");
			} catch (SQLException ex) {
				ex.printStackTrace();
				showAlert("Error", "Gagal menyimpan: " + ex.getMessage());
			}
		});

		VBox formRoot = new VBox(10, grid, saveButton);
		formRoot.setPadding(new Insets(10));
		Scene scene = new Scene(formRoot, 350, 180);
		formStage.setScene(scene);
		formStage.show();
	}

	private static void calculateSubtotal(ComboBox<Menu> menuCombo, TextField jumlahField, TextField subtotalField) {
		Menu menu = menuCombo.getValue();
		if (menu != null) {
			try {
				int jumlah = Integer.parseInt(jumlahField.getText().trim());
				int harga = Integer.parseInt(menu.getHarga().replaceAll("[^0-9]", ""));
				int subtotal = harga * jumlah;
				subtotalField.setText(String.valueOf(subtotal));
			} catch (NumberFormatException ex) {
				// ignore
			}
		}
	}

	private static void deleteData() {
		DetailPesanan selected = table.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Pilih Data", "Silakan pilih item yang akan dihapus.");
			return;
		}

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Konfirmasi Hapus");
		confirm.setContentText("Apakah Anda yakin ingin menghapus item ini?");
		confirm.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				try (Connection conn = DBConnection.getConnection()) {
					// Return stok
					PreparedStatement updateStok = conn.prepareStatement(
							"UPDATE menu SET stok = stok + ? WHERE id_menu = ?");
					updateStok.setInt(1, selected.getJumlah());
					updateStok.setInt(2, selected.getIdMenu());
					updateStok.executeUpdate();

					// Delete detail
					PreparedStatement stmt = conn.prepareStatement(
							"DELETE FROM detail_pesanan WHERE id_pemesanan = ? AND id_menu = ?");
					stmt.setInt(1, selected.getIdPemesanan());
					stmt.setInt(2, selected.getIdMenu());
					stmt.executeUpdate();

					loadMenuData();
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
