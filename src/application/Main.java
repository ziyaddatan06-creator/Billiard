package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		// --- 1. Membuat Menu Bar ---
		MenuBar menuBar = new MenuBar();

		// Menu Master Data
		Menu menuMaster = new Menu("Master Data");
		MenuItem pelangganMenu = new MenuItem("Pelanggan");
		MenuItem pegawaiMenu = new MenuItem("Pegawai");
		MenuItem mejaMenu = new MenuItem("Meja Billiard");
		MenuItem menuFnb = new MenuItem("Menu F&B");
		menuMaster.getItems().addAll(pelangganMenu, pegawaiMenu, new SeparatorMenuItem(), mejaMenu, menuFnb);

		// Menu Transaksi
		Menu menuTransaksi = new Menu("Transaksi");
		MenuItem pemesananMenu = new MenuItem("Pemesanan");
		menuTransaksi.getItems().add(pemesananMenu);

		// Menu System
		Menu menuSystem = new Menu("System");
		MenuItem exitMenu = new MenuItem("Keluar");
		menuSystem.getItems().add(exitMenu);

		menuBar.getMenus().addAll(menuMaster, menuTransaksi, menuSystem);

		// --- 2. Layout Utama dengan Welcome Screen ---
		VBox contentArea = new VBox();
		contentArea.setAlignment(javafx.geometry.Pos.CENTER);
		contentArea.setSpacing(20);

		Label welcomeLabel = new Label("Selamat Datang di");
		welcomeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #555;");

		Label titleLabel = new Label("Sistem Manajemen Billiard");
		titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

		Label subtitleLabel = new Label("Pilih menu di atas untuk memulai");
		subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #777;");

		// Quick Access Buttons
		HBox quickButtons = new HBox(20);
		quickButtons.setAlignment(javafx.geometry.Pos.CENTER);

		Button btnPemesanan = createQuickButton("Pemesanan Baru", "#3498db");
		Button btnMeja = createQuickButton("Cek Meja", "#2ecc71");
		Button btnMenu = createQuickButton("Menu F&B", "#e74c3c");

		btnPemesanan.setOnAction(e -> PemesananCRUD.show());
		btnMeja.setOnAction(e -> MejaBilliardCRUD.show());
		btnMenu.setOnAction(e -> MenuCRUD.show());

		quickButtons.getChildren().addAll(btnPemesanan, btnMeja, btnMenu);

		contentArea.getChildren().addAll(welcomeLabel, titleLabel, subtitleLabel, quickButtons);

		BorderPane root = new BorderPane();
		root.setTop(menuBar);
		root.setCenter(contentArea);
		root.setStyle("-fx-background-color: linear-gradient(to bottom, #ecf0f1, #bdc3c7);");

		// --- 3. Event Handlers ---
		pelangganMenu.setOnAction(e -> {
			try {
				PelangganCRUD.show();
			} catch (Exception ex) {
				showAlert("Error", "Gagal membuka Pelanggan: " + ex.getMessage());
			}
		});

		pegawaiMenu.setOnAction(e -> {
			try {
				PegawaiCRUD.show();
			} catch (Exception ex) {
				showAlert("Error", "Gagal membuka Pegawai: " + ex.getMessage());
			}
		});

		mejaMenu.setOnAction(e -> {
			try {
				MejaBilliardCRUD.show();
			} catch (Exception ex) {
				showAlert("Error", "Gagal membuka Meja Billiard: " + ex.getMessage());
			}
		});

		menuFnb.setOnAction(e -> {
			try {
				MenuCRUD.show();
			} catch (Exception ex) {
				showAlert("Error", "Gagal membuka Menu: " + ex.getMessage());
			}
		});

		pemesananMenu.setOnAction(e -> {
			try {
				PemesananCRUD.show();
			} catch (Exception ex) {
				showAlert("Error", "Gagal membuka Pemesanan: " + ex.getMessage());
			}
		});

		exitMenu.setOnAction(e -> primaryStage.close());

		// --- 4. Tampilkan Scene ---
		Scene scene = new Scene(root, 900, 600);
		primaryStage.setTitle("Sistem Manajemen Billiard");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private Button createQuickButton(String text, String color) {
		Button btn = new Button(text);
		btn.setStyle(
				"-fx-background-color: " + color + ";" +
				"-fx-text-fill: white;" +
				"-fx-font-size: 14px;" +
				"-fx-padding: 15 30;" +
				"-fx-background-radius: 5;" +
				"-fx-cursor: hand;"
		);
		btn.setOnMouseEntered(e -> btn.setStyle(
				"-fx-background-color: derive(" + color + ", -20%);" +
				"-fx-text-fill: white;" +
				"-fx-font-size: 14px;" +
				"-fx-padding: 15 30;" +
				"-fx-background-radius: 5;" +
				"-fx-cursor: hand;"
		));
		btn.setOnMouseExited(e -> btn.setStyle(
				"-fx-background-color: " + color + ";" +
				"-fx-text-fill: white;" +
				"-fx-font-size: 14px;" +
				"-fx-padding: 15 30;" +
				"-fx-background-radius: 5;" +
				"-fx-cursor: hand;"
		));
		return btn;
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
