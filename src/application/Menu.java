package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Menu {
	private final IntegerProperty id;
	private final StringProperty namaItem;
	private final StringProperty kategori;
	private final StringProperty harga;
	private final IntegerProperty stok;

	public Menu(int id, String namaItem, String kategori, String harga, int stok) {
		this.id = new SimpleIntegerProperty(id);
		this.namaItem = new SimpleStringProperty(namaItem);
		this.kategori = new SimpleStringProperty(kategori);
		this.harga = new SimpleStringProperty(harga);
		this.stok = new SimpleIntegerProperty(stok);
	}

	// --- Getters & Properties ---
	public int getId() {
		return id.get();
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public String getNamaItem() {
		return namaItem.get();
	}

	public StringProperty namaItemProperty() {
		return namaItem;
	}

	public String getKategori() {
		return kategori.get();
	}

	public StringProperty kategoriProperty() {
		return kategori;
	}

	public String getHarga() {
		return harga.get();
	}

	public StringProperty hargaProperty() {
		return harga;
	}

	public int getStok() {
		return stok.get();
	}

	public IntegerProperty stokProperty() {
		return stok;
	}

	@Override
	public String toString() {
		return getNamaItem() + " - Rp " + getHarga();
	}
}
