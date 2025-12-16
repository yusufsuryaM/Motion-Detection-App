### Smart Door Monitor (IoT Motion Detection System)

Sistem keamanan pintu cerdas berbasis IoT dengan fitur Zero-Config, notifikasi Telegram, dan monitoring Android Realtime.

### Deskripsi Project

Smart Door Monitor adalah solusi keamanan rumah pintar yang mengintegrasikan ESP32-CAM (Kamera) dan NodeMCU ESP8266 (Sensor) melalui cloud Firebase.

Sistem ini dirancang khusus untuk mengatasi masalah IP Address Dinamis dan konektivitas WiFi yang sering berubah-ubah. Dengan fitur sinkronisasi IP otomatis, aplikasi Android dapat berjalan secara Plug-and-Play tanpa perlu konfigurasi ulang kodingan (hardcode) setiap kali perangkat berpindah jaringan.

### Masalah yang Terpecahkan

Masalah Umum

Solusi di Project Ini

CCTV Pasif

Mengubah sistem rekam biasa menjadi sistem peringatan dini (early warning) yang aktif detik itu juga via Telegram & App.

IP Berubah (DHCP)

Menggunakan Dynamic IP Sync. ESP32 setor IP ke Firebase, Android otomatis membaca IP baru tersebut.

Ganti WiFi Repot

Menggunakan WiFiManager. Ganti SSID/Password cukup lewat HP (Hotspot Portal) tanpa perlu flashing ulang kodingan.

### Fitur Unggulan

1. Motion Triggered Capture
Kamera hanya aktif dan mengambil gambar saat sensor PIR mendeteksi aktivitas nyata. Hemat daya & penyimpanan.

2. Dynamic IP Synchronization (Fitur Kunci)
Aplikasi Android otomatis mengenali alamat IP ESP32-CAM yang baru dimanapun alat dipasang. Zero Configuration di sisi user.

3. WiFi Manager (Auto Connect)
Fitur Self-Healing: Jika WiFi putus atau alat dipindah lokasi, alat akan membuat Hotspot Portal untuk pengaturan ulang WiFi yang mudah.

3. Auto-Save Evidence
Bukti foto tersimpan otomatis ke Galeri HP dan dikirim ke Chat Telegram setiap 5 detik selama gerakan terdeteksi.

4. Realtime Control
Latensi rendah untuk mematikan/menyalakan sistem (Arm/Disarm) langsung dari saklar di aplikasi Android.


### Cara Kerja Sistem

Sistem bekerja secara terintegrasi melalui Cloud Database dengan alur sebagai berikut:

Inisialisasi: Saat menyala, ESP32-CAM mengirim IP Address Lokal terbarunya ke Firebase path /door1/cam_ip.

Deteksi: Sensor PIR mendeteksi gerakan -> NodeMCU menyalakan Buzzer -> Update status /door1/motion ke true.

Respon: ESP32-CAM membaca status motion -> Mengambil foto -> Mengirim ke Telegram Bot.

Monitoring: Aplikasi Android menyinkronkan IP kamera dari Firebase -> Menampilkan Live Stream -> Auto-save gambar ke galeri.


### Alat dan Bahan
Hardware
1. ESP32-CAM (Modul Kamera OV2640)

2. NodeMCU ESP8266 (Controller Sensor)

3. Sensor PIR HC-SR501 (Motion Detector)

4. Active Buzzer (Alarm Suara)

5. FTDI Programmer (Untuk Upload Program)

Software & Tech Stack
1. Android Studio (Kotlin + Jetpack Compose)

2. Arduino IDE (C++)

3. Firebase Realtime Database (Sinkronisasi Data)

4. Telegram Bot API (Notifikasi Jarak Jauh)

5. Library: WiFiManager, UniversalTelegramBot, Firebase_ESP_Client, Coil (Android Image Loader).
