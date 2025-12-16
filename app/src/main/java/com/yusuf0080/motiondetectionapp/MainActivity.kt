package com.yusuf0080.motiondetectionapp

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yusuf0080.motiondetectionapp.ui.theme.MotionDetectionAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.OutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MotionDetectionAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MotionScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MotionScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val esp32Ip = "10.165.74.53"
    val captureUrl = "http://$esp32Ip/capture"

    var systemOn by remember { mutableStateOf(true) }
    var motionDetected by remember { mutableStateOf(false) }

    var imageUrl by remember { mutableStateOf(captureUrl) }

    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val database = FirebaseDatabase.getInstance()
    val doorRef = database.getReference("door1")

    LaunchedEffect(Unit) {
        doorRef.child("system_on").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                systemOn = snapshot.getValue(Boolean::class.java) ?: true
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        doorRef.child("motion").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isMotion = snapshot.getValue(Boolean::class.java) ?: false
                motionDetected = isMotion

                if (isMotion && systemOn) {
                    imageUrl = "$captureUrl?t=${System.currentTimeMillis()}"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    LaunchedEffect(motionDetected, systemOn) {
        if (motionDetected && systemOn) {
            while (isActive) {
                val request = ImageRequest.Builder(context)
                    .data("$captureUrl?t=${System.currentTimeMillis()}")
                    .allowHardware(false)
                    .build()

                try {
                    val result = context.imageLoader.execute(request)
                    if (result is SuccessResult) {
                        val bitmap = (result.drawable as BitmapDrawable).bitmap

                        withContext(Dispatchers.IO) {
                            saveImageToGallery(context, bitmap, auto = true)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                delay(2000)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Smart Door Monitor",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "System Active", fontSize = 18.sp)
                Switch(
                    checked = systemOn,
                    onCheckedChange = { isChecked ->
                        doorRef.child("system_on").setValue(isChecked)
                    }
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (motionDetected && systemOn) Color.Red else Color(0xFF4CAF50)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (!systemOn) "SYSTEM OFF" else if (motionDetected) "GERAKAN TERDETEKSI!" else "AMAN",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    if (motionDetected && systemOn) {
                        Text(
                            text = "(Auto Recording...)",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Camera Stream",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onSuccess = { result ->
                    currentBitmap = (result.result.drawable as BitmapDrawable).bitmap
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (systemOn) {
                        imageUrl = "$captureUrl?t=${System.currentTimeMillis()}"
                    } else {
                        Toast.makeText(context, "System is OFF. Cannot Capture.", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(systemOn) MaterialTheme.colorScheme.primary else Color.Gray
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("CAPTURE")
            }

            Button(
                onClick = {
                    currentBitmap?.let { bmp ->
                        saveImageToGallery(context, bmp, auto = false)
                    } ?: Toast.makeText(context, "No image to save", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("SAVE")
            }
        }
    }
}

fun saveImageToGallery(context: Context, bitmap: Bitmap, auto: Boolean) {
    val filename = "DoorCam_${System.currentTimeMillis()}.jpg"
    var fos: OutputStream? = null
    var imageUri: android.net.Uri? = null

    try {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DoorMonitor")
        }

        val contentResolver = context.contentResolver
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            fos = contentResolver.openOutputStream(uri)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos!!)

            if (!auto) {
                Toast.makeText(context, "Image Saved Manually!", Toast.LENGTH_SHORT).show()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        fos?.close()
    }
}