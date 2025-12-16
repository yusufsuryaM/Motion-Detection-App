Smart Door Monitor (IoT Motion Detection System)
Sistem keamanan pintu berbasis IoT yang mengintegrasikan sensor gerak, kamera pengawas, notifikasi Telegram, dan aplikasi Android Realtime. Sistem ini dirancang dengan fitur Zero-Config untuk kemudahan penggunaan.


Deskripsi
Proyek ini menggabungkan ESP32-CAM dan NodeMCU (ESP8266) untuk mendeteksi gerakan di area pintu. Saat sensor mendeteksi intrusi, sistem otomatis mengambil foto, menyalakan alarm, mengirim notifikasi ke Telegram, dan menampilkan live capture di aplikasi Android secara realtime.


Masalah yang Dipecahkan
Keterbatasan Pemantauan Pasif: CCTV biasa hanya merekam tanpa memberi notifikasi instan. Sistem ini memberi peringatan detik itu juga.
Kendala Konektivitas (IP Dinamis): Mengatasi masalah IP Address kamera yang sering berubah (DHCP) sehingga aplikasi Android tidak perlu di-coding ulang setiap ganti jaringan.
Fleksibilitas Jaringan: Mengatasi kerepotan hardcode SSID/Password WiFi. Alat bisa berpindah lokasi (rumah/kos/kampus) tanpa perlu upload ulang kode.


Fitur Utama & Alasan Pemilihan
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


Alat dan Bahan
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