package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pegawai {
	private final IntegerProperty id;
	private final StringProperty nama;
	private final StringProperty jabatan;
	private final StringProperty shift;
	private final StringProperty gaji;

	public Pegawai(int id, String nama, String jabatan, String shift, String gaji) {
		this.id = new SimpleIntegerProperty(id);
		this.nama = new SimpleStringProperty(nama);
		this.jabatan = new SimpleStringProperty(jabatan);
		this.shift = new SimpleStringProperty(shift);
		this.gaji = new SimpleStringProperty(gaji);
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

	public String getJabatan() {
		return jabatan.get();
	}

	public StringProperty jabatanProperty() {
		return jabatan;
	}

	public String getShift() {
		return shift.get();
	}

	public StringProperty shiftProperty() {
		return shift;
	}

	public String getGaji() {
		return gaji.get();
	}

	public StringProperty gajiProperty() {
		return gaji;
	}

	@Override
	public String toString() {
		return getNama();
	}
}
