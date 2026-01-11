package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pelanggan {
	private final IntegerProperty id;
	private final StringProperty nama;
	private final StringProperty noTelepon;

	public Pelanggan(int id, String nama, String noTelepon) {
		this.id = new SimpleIntegerProperty(id);
		this.nama = new SimpleStringProperty(nama);
		this.noTelepon = new SimpleStringProperty(noTelepon);
	}

	// --- Getters & Properties ---
	public int getId() {
		return id.get();
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public String getNama() {
		return nama.get();
	}

	public StringProperty namaProperty() {
		return nama;
	}

	public String getNoTelepon() {
		return noTelepon.get();
	}

	public StringProperty noTeleponProperty() {
		return noTelepon;
	}

	@Override
	public String toString() {
		return getNama();
	}
}
