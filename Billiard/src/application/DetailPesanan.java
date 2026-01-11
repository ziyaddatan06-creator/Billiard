package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DetailPesanan {
	private final IntegerProperty idPemesanan;
	private final IntegerProperty idMenu;
	private final IntegerProperty jumlah;
	private final StringProperty subtotal;

	// For display purposes
	private final StringProperty namaMenu;

	public DetailPesanan(int idPemesanan, int idMenu, int jumlah, String subtotal) {
		this.idPemesanan = new SimpleIntegerProperty(idPemesanan);
		this.idMenu = new SimpleIntegerProperty(idMenu);
		this.jumlah = new SimpleIntegerProperty(jumlah);
		this.subtotal = new SimpleStringProperty(subtotal);
		this.namaMenu = new SimpleStringProperty("");
	}

	// --- Getters & Properties ---
	public int getIdPemesanan() {
		return idPemesanan.get();
	}

	public IntegerProperty idPemesananProperty() {
		return idPemesanan;
	}

	public int getIdMenu() {
		return idMenu.get();
	}

	public IntegerProperty idMenuProperty() {
		return idMenu;
	}

	public int getJumlah() {
		return jumlah.get();
	}

	public IntegerProperty jumlahProperty() {
		return jumlah;
	}

	public String getSubtotal() {
		return subtotal.get();
	}

	public StringProperty subtotalProperty() {
		return subtotal;
	}

	public String getNamaMenu() {
		return namaMenu.get();
	}

	public void setNamaMenu(String nama) {
		this.namaMenu.set(nama);
	}

	public StringProperty namaMenuProperty() {
		return namaMenu;
	}
}
