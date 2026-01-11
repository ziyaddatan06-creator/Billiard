package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MejaBilliard {
	private final IntegerProperty id;
	private final IntegerProperty nomorMeja;
	private final StringProperty tipe;
	private final StringProperty status;
	private final StringProperty harga;
	private final StringProperty lokasi;

	public MejaBilliard(int id, int nomorMeja, String tipe, String status, String harga, String lokasi) {
		this.id = new SimpleIntegerProperty(id);
		this.nomorMeja = new SimpleIntegerProperty(nomorMeja);
		this.tipe = new SimpleStringProperty(tipe);
		this.status = new SimpleStringProperty(status);
		this.harga = new SimpleStringProperty(harga);
		this.lokasi = new SimpleStringProperty(lokasi);
	}

	// --- Getters & Properties ---
	public int getId() {
		return id.get();
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public int getNomorMeja() {
		return nomorMeja.get();
	}

	public IntegerProperty nomorMejaProperty() {
		return nomorMeja;
	}

	public String getTipe() {
		return tipe.get();
	}

	public StringProperty tipeProperty() {
		return tipe;
	}

	public String getStatus() {
		return status.get();
	}

	public StringProperty statusProperty() {
		return status;
	}

	public String getHarga() {
		return harga.get();
	}

	public StringProperty hargaProperty() {
		return harga;
	}

	public String getLokasi() {
		return lokasi.get();
	}

	public StringProperty lokasiProperty() {
		return lokasi;
	}

	@Override
	public String toString() {
		return "Meja " + getNomorMeja() + " (" + getTipe() + ")";
	}
}
