**Smart Door Monitor (IoT Motion Detection System)**

Sistem keamanan pintu berbasis IoT yang mengintegrasikan sensor gerak, kamera pengawas, notifikasi Telegram, dan aplikasi Android Realtime. Sistem ini dirancang dengan fitur Zero-Config untuk kemudahan penggunaan.

**Deskripsi**
Proyek ini menggabungkan ESP32-CAM dan NodeMCU (ESP8266) untuk mendeteksi gerakan di area pintu. Saat sensor mendeteksi intrusi, sistem otomatis mengambil foto, menyalakan alarm, mengirim notifikasi ke Telegram, dan menampilkan live capture di aplikasi Android secara realtime.

**Masalah yang Dipecahkan**
1. Keterbatasan Pemantauan Pasif: CCTV biasa hanya merekam tanpa memberi notifikasi instan. Sistem ini memberi peringatan detik itu juga.

2. Kendala Konektivitas (IP Dinamis): Mengatasi masalah IP Address kamera yang sering berubah (DHCP) sehingga aplikasi Android tidak perlu di-coding ulang setiap ganti jaringan.

3. Fleksibilitas Jaringan: Mengatasi kerepotan hardcode SSID/Password WiFi. Alat bisa berpindah lokasi (rumah/kos/kampus) tanpa perlu upload ulang kode.


**Fitur Utama & Alasan Pemilihan**
Motion Triggered Capture (PIR Sensor):

Alasan: Hemat daya dan penyimpanan. Kamera hanya aktif dan memfoto ketika ada aktivitas nyata.

Continuous Auto-Save (Android & Telegram):

Alasan: Bukti visual tersimpan otomatis di Galeri HP dan Chat Telegram setiap 5 detik selama gerakan terdeteksi, memastikan tidak ada momen yang terlewat.

WiFiManager (Auto Connect):

Alasan: Memungkinkan pengguna mengganti koneksi WiFi langsung lewat HP (Portal Hotspot) tanpa perlu laptop/coding ulang.

Dynamic IP Synchronization (via Firebase):

Alasan: Agar aplikasi Android otomatis mengenali alamat IP ESP32-CAM yang baru, menjadikan sistem Plug-and-Play.

Telegram Bot Notification:

Alasan: Notifikasi jarak jauh yang cepat dan gratis, bisa diakses meskipun aplikasi Android sedang tertutup.

Realtime Control (Firebase):

Alasan: Latensi rendah untuk mematikan/menyalakan sistem (Arm/Disarm) dari jarak jauh.


**Cara Kerja Sistem**
Sistem bekerja secara terintegrasi melalui Cloud Database dengan alur sebagai berikut:

Inisialisasi & Sinkronisasi IP

Saat dinyalakan, ESP32-CAM dan NodeMCU terhubung ke WiFi menggunakan profil yang tersimpan (atau via Hotspot Portal jika WiFi berubah).

ESP32-CAM secara otomatis mengirimkan IP Address Lokal terbarunya ke Firebase (/door1/cam_ip).

Deteksi Intrusi

Sensor PIR pada NodeMCU mendeteksi gerakan.

NodeMCU menyalakan Buzzer (Alarm) dan mengubah status /door1/motion menjadi true di Firebase Realtime Database.

Respon Kamera & Notifikasi

ESP32-CAM memantau status /door1/motion. Saat bernilai true, kamera mengambil gambar.

Gambar dikirim ke Bot Telegram pemilik rumah sebagai peringatan dini.

Sistem menerapkan jeda 5 detik antar pengiriman foto untuk stabilitas.

Monitoring Android (Realtime)

Aplikasi Android membaca IP Kamera terbaru dari Firebase (tanpa perlu setting manual).

Saat gerakan terdeteksi, aplikasi otomatis menampilkan Live View dari kamera.

Fitur Auto-Save berjalan di latar belakang, menyimpan bukti foto ke Galeri HP setiap 2 detik selama gerakan masih ada.


**Alat dan Bahan**
Hardware:

ESP32-CAM (Modul Kamera)

NodeMCU ESP8266 (Mikrokontroler Sensor)

Sensor PIR HC-SR501 (Pendeteksi Gerak)

Buzzer (Alarm Suara)

Kabel Jumper & Breadboard

Software & Cloud:

Android Studio (Kotlin + Jetpack Compose)

Arduino IDE (C++)

Firebase Realtime Database (Sinkronisasi Data)

Telegram Bot API (Notifikasi)

Library: WiFiManager, UniversalTelegramBot, Firebase_ESP_Client, Coil (Android).
