-- =====================================================
-- BILLIARD MANAGEMENT SYSTEM DATABASE
-- =====================================================

CREATE DATABASE IF NOT EXISTS billiard_db;
USE billiard_db;

-- Tabel Pelanggan
CREATE TABLE IF NOT EXISTS pelanggan (
    id_pelanggan INT AUTO_INCREMENT PRIMARY KEY,
    nama CHAR(50) NOT NULL,
    no_telepon VARCHAR(15)
);

-- Tabel Pegawai
CREATE TABLE IF NOT EXISTS pegawai (
    id_pegawai INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(50),
    jabatan VARCHAR(20),
    shift VARCHAR(10),
    gaji VARCHAR(10)
);

-- Tabel Meja Billiard
CREATE TABLE IF NOT EXISTS meja_billiard (
    id_meja INT AUTO_INCREMENT PRIMARY KEY,
    nomor_meja INT NOT NULL,
    tipe VARCHAR(20),
    status VARCHAR(15) DEFAULT 'Tersedia',
    harga VARCHAR(10),
    lokasi VARCHAR(20)
);

-- Tabel Menu
CREATE TABLE IF NOT EXISTS menu (
    id_menu INT AUTO_INCREMENT PRIMARY KEY,
    nama_item VARCHAR(50),
    kategori VARCHAR(20),
    harga VARCHAR(20),
    stok INT NOT NULL DEFAULT 0
);

-- Tabel Pemesanan (dengan foreign keys)
CREATE TABLE IF NOT EXISTS pemesanan (
    id_pemesanan INT AUTO_INCREMENT PRIMARY KEY,
    id_pelanggan INT,
    id_meja INT,
    id_pegawai INT,
    waktu_mulai DATETIME NOT NULL,
    waktu_selesai DATETIME,
    durasi INT NOT NULL,
    total_biaya DECIMAL(12,2),
    metode_bayar VARCHAR(20),
    FOREIGN KEY (id_pelanggan) REFERENCES pelanggan(id_pelanggan),
    FOREIGN KEY (id_meja) REFERENCES meja_billiard(id_meja),
    FOREIGN KEY (id_pegawai) REFERENCES pegawai(id_pegawai)
);

-- Tabel Detail Pesanan (Composite Primary Key)
CREATE TABLE IF NOT EXISTS detail_pesanan (
    id_pemesanan INT NOT NULL,
    id_menu INT NOT NULL,
    jumlah INT NOT NULL,
    subtotal VARCHAR(20),
    PRIMARY KEY (id_pemesanan, id_menu),
    FOREIGN KEY (id_pemesanan) REFERENCES pemesanan(id_pemesanan) ON DELETE CASCADE,
    FOREIGN KEY (id_menu) REFERENCES menu(id_menu)
);

-- =====================================================
-- SAMPLE DATA
-- =====================================================

-- Sample Pelanggan
INSERT INTO pelanggan (nama, no_telepon) VALUES 
('Ahmad Rizki', '081234567890'),
('Budi Santoso', '082345678901'),
('Citra Dewi', '083456789012');

-- Sample Pegawai
INSERT INTO pegawai (nama, jabatan, shift, gaji) VALUES 
('Doni Pratama', 'Kasir', 'Pagi', '3500000'),
('Eka Sari', 'Pelayan', 'Siang', '3000000'),
('Fajar Hidayat', 'Supervisor', 'Malam', '5000000');

-- Sample Meja Billiard
INSERT INTO meja_billiard (nomor_meja, tipe, status, harga, lokasi) VALUES 
(1, 'Standard', 'Tersedia', '50000', 'Lantai 1'),
(2, 'Standard', 'Tersedia', '50000', 'Lantai 1'),
(3, 'VIP', 'Tersedia', '80000', 'Lantai 2'),
(4, 'VIP', 'Terpakai', '80000', 'Lantai 2'),
(5, 'Premium', 'Tersedia', '100000', 'Lantai 2');

-- Sample Menu
INSERT INTO menu (nama_item, kategori, harga, stok) VALUES 
('Es Teh Manis', 'Minuman', '8000', 100),
('Kopi Hitam', 'Minuman', '10000', 50),
('Nasi Goreng', 'Makanan', '25000', 30),
('Mie Goreng', 'Makanan', '22000', 30),
('Kentang Goreng', 'Snack', '15000', 40);
