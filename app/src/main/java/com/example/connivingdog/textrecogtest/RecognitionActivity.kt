package com.example.connivingdog.textrecogtest

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.vision.text.TextRecognizer
import android.widget.Toast
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.support.annotation.RequiresPermission
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.SurfaceHolder
import com.example.connivingdog.textrecogtest.R.id.camera_view
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import kotlinx.android.synthetic.main.activity_recognition.*
import java.io.IOException
import java.util.jar.Manifest
import android.graphics.RectF
import android.speech.tts.TextToSpeech
import java.util.*

    private var tts: TextToSpeech? = null

class RecognitionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognition)


        // Text To Speech engine start.
        val listener = TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                Log.d("TTS", "Text to speech engine started successfully.")
                tts?.setLanguage(Locale.US)
            } else {
                Log.d("TTS", "Error starting the text to speech engine.")
            }
        }
        tts = TextToSpeech(applicationContext, listener)
        // Text To Speech engine end.

        //camera preview setup start

        val textRecognizer = TextRecognizer.Builder(this).build()

        if (!textRecognizer.isOperational()) {
            Toast.makeText(this@RecognitionActivity,"Dependencies are not loaded yet...please try after few moment!!",Toast.LENGTH_SHORT).show()
            Log.w("RecognitionActivity","Dependecies not available")
        }
        else{

            var cameraSource = CameraSource.Builder(applicationContext,textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280,1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build()

            camera_view.holder.addCallback(object : SurfaceHolder.Callback{
                override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

                }

                override fun surfaceDestroyed(p0: SurfaceHolder?) {
                    cameraSource.stop()
                }

                @SuppressLint("MissingPermission")
                override fun surfaceCreated(p0: SurfaceHolder?) {
                    if(isCameraPermissionGranted()){
                        cameraSource.start(camera_view.holder)
                    }
                    else{
                        requestForPermission()
                    }
                }
            })

            //camera preview setup end

            //detector processor start

            textRecognizer.setProcessor(object : Detector.Processor<TextBlock>{
                override fun release() {

                }

                override fun receiveDetections(detections: Detector.Detections<TextBlock>?) {
                    val items = detections!!.detectedItems

                    if(items.size() <= 0){
                        return
                    }

                    captured_text.post {
                        val stringBuilder = StringBuilder()

                        for(i in 0 until items.size()){
                            val item = items.valueAt(i)
                            stringBuilder.append(item.value)
                            stringBuilder.append("\n")
                        }

                        captured_text.text = stringBuilder.toString()
            //detector processor end

            //text to speech application start

                        captured_text.setOnClickListener {
                            if(captured_text.text != null){
                                tts!!.speak(captured_text.text, TextToSpeech.QUEUE_ADD, null, "DEFAULT")
                            }
                        }

            //text to speech application end

                    }

                }

            })
        }
    }

    fun isCameraPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestForPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), PackageManager.PERMISSION_GRANTED)
    }

}
