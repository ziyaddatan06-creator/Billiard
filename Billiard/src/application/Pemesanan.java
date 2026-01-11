package application;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Pemesanan {
	private final IntegerProperty id;
	private final IntegerProperty idPelanggan;
	private final IntegerProperty idMeja;
	private final IntegerProperty idPegawai;
	private final ObjectProperty<LocalDateTime> waktuMulai;
	private final ObjectProperty<LocalDateTime> waktuSelesai;
	private final IntegerProperty durasi;
	private final StringProperty totalBiaya;
	private final StringProperty metodeBayar;

	// For display purposes
	private final StringProperty namaPelanggan;
	private final StringProperty namaMeja;
	private final StringProperty namaPegawai;

	public Pemesanan(int id, int idPelanggan, int idMeja, int idPegawai,
			LocalDateTime waktuMulai, LocalDateTime waktuSelesai,
			int durasi, String totalBiaya, String metodeBayar) {
		this.id = new SimpleIntegerProperty(id);
		this.idPelanggan = new SimpleIntegerProperty(idPelanggan);
		this.idMeja = new SimpleIntegerProperty(idMeja);
		this.idPegawai = new SimpleIntegerProperty(idPegawai);
		this.waktuMulai = new SimpleObjectProperty<>(waktuMulai);
		this.waktuSelesai = new SimpleObjectProperty<>(waktuSelesai);
		this.durasi = new SimpleIntegerProperty(durasi);
		this.totalBiaya = new SimpleStringProperty(totalBiaya);
		this.metodeBayar = new SimpleStringProperty(metodeBayar);
		this.namaPelanggan = new SimpleStringProperty("");
		this.namaMeja = new SimpleStringProperty("");
		this.namaPegawai = new SimpleStringProperty("");
	}

	// --- Getters & Properties ---
	public int getId() {
		return id.get();
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public int getIdPelanggan() {
		return idPelanggan.get();
	}

	public IntegerProperty idPelangganProperty() {
		return idPelanggan;
	}

	public int getIdMeja() {
		return idMeja.get();
	}

	public IntegerProperty idMejaProperty() {
		return idMeja;
	}

	public int getIdPegawai() {
		return idPegawai.get();
	}

	public IntegerProperty idPegawaiProperty() {
		return idPegawai;
	}

	public LocalDateTime getWaktuMulai() {
		return waktuMulai.get();
	}

	public ObjectProperty<LocalDateTime> waktuMulaiProperty() {
		return waktuMulai;
	}

	public LocalDateTime getWaktuSelesai() {
		return waktuSelesai.get();
	}

	public ObjectProperty<LocalDateTime> waktuSelesaiProperty() {
		return waktuSelesai;
	}

	public int getDurasi() {
		return durasi.get();
	}

	public IntegerProperty durasiProperty() {
		return durasi;
	}

	public String getTotalBiaya() {
		return totalBiaya.get();
	}

	public StringProperty totalBiayaProperty() {
		return totalBiaya;
	}

	public String getMetodeBayar() {
		return metodeBayar.get();
	}

	public StringProperty metodeBayarProperty() {
		return metodeBayar;
	}

	public String getNamaPelanggan() {
		return namaPelanggan.get();
	}

	public void setNamaPelanggan(String nama) {
		this.namaPelanggan.set(nama);
	}

	public StringProperty namaPelangganProperty() {
		return namaPelanggan;
	}

	public String getNamaMeja() {
		return namaMeja.get();
	}

	public void setNamaMeja(String nama) {
		this.namaMeja.set(nama);
	}

	public StringProperty namaMejaProperty() {
		return namaMeja;
	}

	public String getNamaPegawai() {
		return namaPegawai.get();
	}

	public void setNamaPegawai(String nama) {
		this.namaPegawai.set(nama);
	}

	public StringProperty namaPegawaiProperty() {
		return namaPegawai;
	}
}
