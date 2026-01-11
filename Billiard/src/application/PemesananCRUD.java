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

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PemesananCRUD {
	private static TableView<Pemesanan> table = new TableView<>();
	private static ObservableList<Pemesanan> pemesananList = FXCollections.observableArrayList();
	private static ObservableList<Pelanggan> pelangganList = FXCollections.observableArrayList();
	private static ObservableList<MejaBilliard> mejaList = FXCollections.observableArrayList();
	private static ObservableList<Pegawai> pegawaiList = FXCollections.observableArrayList();

	private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	public static void show() {
		Stage stage = new Stage();
		VBox root = new VBox();
		root.setPadding(new Insets(10));
		root.setSpacing(10);

		table = new TableView<>();

		TableColumn<Pemesanan, Integer> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());

		TableColumn<Pemesanan, String> colPelanggan = new TableColumn<>("Pelanggan");
		colPelanggan.setCellValueFactory(data -> data.getValue().namaPelangganProperty());
		colPelanggan.setPrefWidth(120);

		TableColumn<Pemesanan, String> colMeja = new TableColumn<>("Meja");
		colMeja.setCellValueFactory(data -> data.getValue().namaMejaProperty());
		colMeja.setPrefWidth(100);

		TableColumn<Pemesanan, String> colPegawai = new TableColumn<>("Pegawai");
		colPegawai.setCellValueFactory(data -> data.getValue().namaPegawaiProperty());
		colPegawai.setPrefWidth(100);

		TableColumn<Pemesanan, String> colWaktuMulai = new TableColumn<>("Waktu Mulai");
		colWaktuMulai.setCellValueFactory(data -> {
			LocalDateTime dt = data.getValue().getWaktuMulai();
			return new javafx.beans.property.SimpleStringProperty(dt != null ? dt.format(dtf) : "");
		});
		colWaktuMulai.setPrefWidth(120);

		TableColumn<Pemesanan, Integer> colDurasi = new TableColumn<>("Durasi (jam)");
		colDurasi.setCellValueFactory(data -> data.getValue().durasiProperty().asObject());

		TableColumn<Pemesanan, String> colTotal = new TableColumn<>("Total Biaya");
		colTotal.setCellValueFactory(data -> data.getValue().totalBiayaProperty());
		colTotal.setPrefWidth(100);

		TableColumn<Pemesanan, String> colMetode = new TableColumn<>("Metode Bayar");
		colMetode.setCellValueFactory(data -> data.getValue().metodeBayarProperty());
		colMetode.setPrefWidth(100);

		table.getColumns().addAll(colId, colPelanggan, colMeja, colPegawai, colWaktuMulai, colDurasi, colTotal, colMetode);
		table.setItems(pemesananList);

		Button addButton = new Button("Tambah");
		Button editButton = new Button("Edit");
		Button deleteButton = new Button("Hapus");
		Button detailButton = new Button("Detail Pesanan");
		HBox buttonBox = new HBox(10, addButton, editButton, deleteButton, detailButton);

		addButton.setOnAction(e -> showAddForm());
		editButton.setOnAction(e -> showEditForm());
		deleteButton.setOnAction(e -> deleteData());
		detailButton.setOnAction(e -> showDetailPesanan());

		root.getChildren().addAll(table, buttonBox);

		Scene scene = new Scene(root, 900, 450);
		stage.setScene(scene);
		stage.setTitle("Data Pemesanan");
		stage.show();

		loadReferenceData();
		loadData();
	}

	private static void loadReferenceData() {
		// Load Pelanggan
		pelangganList.clear();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM pelanggan ORDER BY nama")) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				pelangganList.add(new Pelanggan(
						rs.getInt("id_pelanggan"),
						rs.getString("nama"),
						rs.getString("no_telepon")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Load Meja
		mejaList.clear();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM meja_billiard ORDER BY nomor_meja")) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				mejaList.add(new MejaBilliard(
						rs.getInt("id_meja"),
						rs.getInt("nomor_meja"),
						rs.getString("tipe"),
						rs.getString("status"),
						rs.getString("harga"),
						rs.getString("lokasi")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Load Pegawai
		pegawaiList.clear();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM pegawai ORDER BY nama")) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				pegawaiList.add(new Pegawai(
						rs.getInt("id_pegawai"),
						rs.getString("nama"),
						rs.getString("jabatan"),
						rs.getString("shift"),
						rs.getString("gaji")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void loadData() {
		pemesananList.clear();
		String sql = "SELECT p.*, " +
				"pel.nama as nama_pelanggan, " +
				"CONCAT('Meja ', m.nomor_meja, ' (', m.tipe, ')') as nama_meja, " +
				"peg.nama as nama_pegawai " +
				"FROM pemesanan p " +
				"LEFT JOIN pelanggan pel ON p.id_pelanggan = pel.id_pelanggan " +
				"LEFT JOIN meja_billiard m ON p.id_meja = m.id_meja " +
				"LEFT JOIN pegawai peg ON p.id_pegawai = peg.id_pegawai " +
				"ORDER BY p.waktu_mulai DESC";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				LocalDateTime waktuMulai = null;
				LocalDateTime waktuSelesai = null;
				Timestamp tsMulai = rs.getTimestamp("waktu_mulai");
				Timestamp tsSelesai = rs.getTimestamp("waktu_selesai");
				if (tsMulai != null) waktuMulai = tsMulai.toLocalDateTime();
				if (tsSelesai != null) waktuSelesai = tsSelesai.toLocalDateTime();

				Pemesanan p = new Pemesanan(
						rs.getInt("id_pemesanan"),
						rs.getInt("id_pelanggan"),
						rs.getInt("id_meja"),
						rs.getInt("id_pegawai"),
						waktuMulai,
						waktuSelesai,
						rs.getInt("durasi"),
						rs.getString("total_biaya"),
						rs.getString("metode_bayar"));
				p.setNamaPelanggan(rs.getString("nama_pelanggan"));
				p.setNamaMeja(rs.getString("nama_meja"));
				p.setNamaPegawai(rs.getString("nama_pegawai"));
				pemesananList.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Error", "Gagal memuat data: " + e.getMessage());
		}
	}

	private static void showAddForm() {
		loadReferenceData();

		Stage formStage = new Stage();
		formStage.setTitle("Tambah Pemesanan");

		GridPane grid = createFormLayout();

		ComboBox<Pelanggan> pelangganCombo = new ComboBox<>();
		pelangganCombo.setItems(pelangganList);

		ComboBox<MejaBilliard> mejaCombo = new ComboBox<>();
		mejaCombo.setItems(mejaList);

		ComboBox<Pegawai> pegawaiCombo = new ComboBox<>();
		pegawaiCombo.setItems(pegawaiList);

		TextField waktuMulaiField = new TextField(LocalDateTime.now().format(dtf));
		TextField durasiField = new TextField("1");
		TextField totalField = new TextField();
		ComboBox<String> metodeCombo = new ComboBox<>();
		metodeCombo.getItems().addAll("Cash", "Debit", "QRIS", "Transfer");

		// Auto calculate total when meja or durasi changes
		mejaCombo.setOnAction(e -> calculateTotal(mejaCombo, durasiField, totalField));
		durasiField.textProperty().addListener((obs, oldVal, newVal) -> 
			calculateTotal(mejaCombo, durasiField, totalField));

		grid.add(new Label("Pelanggan:"), 0, 0);
		grid.add(pelangganCombo, 1, 0);
		grid.add(new Label("Meja:"), 0, 1);
		grid.add(mejaCombo, 1, 1);
		grid.add(new Label("Pegawai:"), 0, 2);
		grid.add(pegawaiCombo, 1, 2);
		grid.add(new Label("Waktu Mulai (yyyy-MM-dd HH:mm):"), 0, 3);
		grid.add(waktuMulaiField, 1, 3);
		grid.add(new Label("Durasi (jam):"), 0, 4);
		grid.add(durasiField, 1, 4);
		grid.add(new Label("Total Biaya:"), 0, 5);
		grid.add(totalField, 1, 5);
		grid.add(new Label("Metode Bayar:"), 0, 6);
		grid.add(metodeCombo, 1, 6);

		Button saveButton = new Button("Simpan");
		saveButton.setOnAction(e -> {
			Pelanggan pel = pelangganCombo.getValue();
			MejaBilliard meja = mejaCombo.getValue();
			Pegawai peg = pegawaiCombo.getValue();
			String durasiStr = durasiField.getText().trim();
			String total = totalField.getText().trim();
			String metode = metodeCombo.getValue();

			if (pel == null || meja == null || peg == null) {
				showAlert("Validasi", "Pilih pelanggan, meja, dan pegawai!");
				return;
			}

			try {
				int durasi = Integer.parseInt(durasiStr);
				LocalDateTime waktuMulai = LocalDateTime.parse(waktuMulaiField.getText().trim(), dtf);
				LocalDateTime waktuSelesai = waktuMulai.plusHours(durasi);

				try (Connection conn = DBConnection.getConnection();
						PreparedStatement stmt = conn.prepareStatement(
								"INSERT INTO pemesanan (id_pelanggan, id_meja, id_pegawai, waktu_mulai, waktu_selesai, durasi, total_biaya, metode_bayar) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
					stmt.setInt(1, pel.getId());
					stmt.setInt(2, meja.getId());
					stmt.setInt(3, peg.getId());
					stmt.setTimestamp(4, Timestamp.valueOf(waktuMulai));
					stmt.setTimestamp(5, Timestamp.valueOf(waktuSelesai));
					stmt.setInt(6, durasi);
					stmt.setString(7, total);
					stmt.setString(8, metode);
					stmt.executeUpdate();

					// Update status meja
					try (PreparedStatement updateMeja = conn.prepareStatement(
							"UPDATE meja_billiard SET status = 'Terpakai' WHERE id_meja = ?")) {
						updateMeja.setInt(1, meja.getId());
						updateMeja.executeUpdate();
					}

					loadData();
					formStage.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert("Error", "Gagal menyimpan: " + ex.getMessage());
			}
		});

		VBox formRoot = new VBox(10, grid, saveButton);
		formRoot.setPadding(new Insets(10));
		Scene scene = new Scene(formRoot, 400, 350);
		formStage.setScene(scene);
		formStage.show();
	}

	private static void calculateTotal(ComboBox<MejaBilliard> mejaCombo, TextField durasiField, TextField totalField) {
		MejaBilliard meja = mejaCombo.getValue();
		if (meja != null) {
			try {
				int durasi = Integer.parseInt(durasiField.getText().trim());
				int harga = Integer.parseInt(meja.getHarga().replaceAll("[^0-9]", ""));
				int total = harga * durasi;
				totalField.setText(String.valueOf(total));
			} catch (NumberFormatException ex) {
				// ignore
			}
		}
	}

	private static void showEditForm() {
		Pemesanan selected = table.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Pilih Data", "Silakan pilih pemesanan yang akan diedit.");
			return;
		}

		loadReferenceData();

		Stage formStage = new Stage();
		formStage.setTitle("Edit Pemesanan");

		GridPane grid = createFormLayout();

		ComboBox<Pelanggan> pelangganCombo = new ComboBox<>();
		pelangganCombo.setItems(pelangganList);
		pelangganList.stream().filter(p -> p.getId() == selected.getIdPelanggan()).findFirst().ifPresent(pelangganCombo::setValue);

		ComboBox<MejaBilliard> mejaCombo = new ComboBox<>();
		mejaCombo.setItems(mejaList);
		mejaList.stream().filter(m -> m.getId() == selected.getIdMeja()).findFirst().ifPresent(mejaCombo::setValue);

		ComboBox<Pegawai> pegawaiCombo = new ComboBox<>();
		pegawaiCombo.setItems(pegawaiList);
		pegawaiList.stream().filter(p -> p.getId() == selected.getIdPegawai()).findFirst().ifPresent(pegawaiCombo::setValue);

		TextField waktuMulaiField = new TextField(selected.getWaktuMulai() != null ? selected.getWaktuMulai().format(dtf) : "");
		TextField durasiField = new TextField(String.valueOf(selected.getDurasi()));
		TextField totalField = new TextField(selected.getTotalBiaya());
		ComboBox<String> metodeCombo = new ComboBox<>();
		metodeCombo.getItems().addAll("Cash", "Debit", "QRIS", "Transfer");
		metodeCombo.setValue(selected.getMetodeBayar());

		grid.add(new Label("Pelanggan:"), 0, 0);
		grid.add(pelangganCombo, 1, 0);
		grid.add(new Label("Meja:"), 0, 1);
		grid.add(mejaCombo, 1, 1);
		grid.add(new Label("Pegawai:"), 0, 2);
		grid.add(pegawaiCombo, 1, 2);
		grid.add(new Label("Waktu Mulai:"), 0, 3);
		grid.add(waktuMulaiField, 1, 3);
		grid.add(new Label("Durasi (jam):"), 0, 4);
		grid.add(durasiField, 1, 4);
		grid.add(new Label("Total Biaya:"), 0, 5);
		grid.add(totalField, 1, 5);
		grid.add(new Label("Metode Bayar:"), 0, 6);
		grid.add(metodeCombo, 1, 6);

		Button saveButton = new Button("Simpan");
		saveButton.setOnAction(e -> {
			Pelanggan pel = pelangganCombo.getValue();
			MejaBilliard meja = mejaCombo.getValue();
			Pegawai peg = pegawaiCombo.getValue();
			String durasiStr = durasiField.getText().trim();
			String total = totalField.getText().trim();
			String metode = metodeCombo.getValue();

			try {
				int durasi = Integer.parseInt(durasiStr);
				LocalDateTime waktuMulai = LocalDateTime.parse(waktuMulaiField.getText().trim(), dtf);
				LocalDateTime waktuSelesai = waktuMulai.plusHours(durasi);

				try (Connection conn = DBConnection.getConnection();
						PreparedStatement stmt = conn.prepareStatement(
								"UPDATE pemesanan SET id_pelanggan = ?, id_meja = ?, id_pegawai = ?, waktu_mulai = ?, waktu_selesai = ?, durasi = ?, total_biaya = ?, metode_bayar = ? WHERE id_pemesanan = ?")) {
					stmt.setInt(1, pel.getId());
					stmt.setInt(2, meja.getId());
					stmt.setInt(3, peg.getId());
					stmt.setTimestamp(4, Timestamp.valueOf(waktuMulai));
					stmt.setTimestamp(5, Timestamp.valueOf(waktuSelesai));
					stmt.setInt(6, durasi);
					stmt.setString(7, total);
					stmt.setString(8, metode);
					stmt.setInt(9, selected.getId());
					stmt.executeUpdate();
					loadData();
					formStage.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				showAlert("Error", "Gagal menyimpan: " + ex.getMessage());
			}
		});

		VBox formRoot = new VBox(10, grid, saveButton);
		formRoot.setPadding(new Insets(10));
		Scene scene = new Scene(formRoot, 400, 350);
		formStage.setScene(scene);
		formStage.show();
	}

	private static void deleteData() {
		Pemesanan selected = table.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Pilih Data", "Silakan pilih pemesanan yang akan dihapus.");
			return;
		}

		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Konfirmasi Hapus");
		confirm.setContentText("Apakah Anda yakin ingin menghapus pemesanan ini?");
		confirm.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				try (Connection conn = DBConnection.getConnection();
						PreparedStatement stmt = conn.prepareStatement(
								"DELETE FROM pemesanan WHERE id_pemesanan = ?")) {
					stmt.setInt(1, selected.getId());
					stmt.executeUpdate();

					// Update meja status back to Tersedia
					try (PreparedStatement updateMeja = conn.prepareStatement(
							"UPDATE meja_billiard SET status = 'Tersedia' WHERE id_meja = ?")) {
						updateMeja.setInt(1, selected.getIdMeja());
						updateMeja.executeUpdate();
					}

					loadData();
				} catch (SQLException e) {
					e.printStackTrace();
					showAlert("Error", "Gagal menghapus: " + e.getMessage());
				}
			}
		});
	}

	private static void showDetailPesanan() {
		Pemesanan selected = table.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showAlert("Pilih Data", "Silakan pilih pemesanan untuk melihat detail.");
			return;
		}
		DetailPesananCRUD.show(selected.getId());
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
