package com.example.scanner_poc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.honeywell.aidc.AidcManager
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.BarcodeReader
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel

class MainActivity : FlutterActivity() {
    // private val CHANNEL_SCANNER_EVENT = "barcode/scanner"
    // private val PROFILE_INTENT_ACTION = "LODI"

    // private var eventSink: EventChannel.EventSink? = null
    // private var scanReceiver: BroadcastReceiver? = null

    // private var aidcManager: AidcManager? = null
    // private var honeywellReader: BarcodeReader? = null

    // override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
    //     super.configureFlutterEngine(flutterEngine)
    //     Log.d("MainActivity", "configureFlutterEngine called")

    //     EventChannel(flutterEngine.dartExecutor, CHANNEL_SCANNER_EVENT).setStreamHandler(object: EventChannel.StreamHandler {
    //         override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    //             Log.d("MainActivity", "EventChannel onListen triggered")
    //             eventSink = events
    //             initScanner()
    //         }

    //         override fun onCancel(arguments: Any?) {
    //             Log.d("MainActivity", "EventChannel onCancel triggered")
    //             eventSink = null

    //             try {
    //                 scanReceiver?.let {
    //                     unregisterReceiver(it)
    //                     scanReceiver = null
    //                 }
    //             } catch (e: IllegalArgumentException) {
    //                 Log.w("MainActivity", "Receiver not registered or already unregistered: ${e.message}")
    //             }

    //             // Release Honeywell reader
    //             honeywellReader?.release()
    //             aidcManager?.close()
    //         }
    //     })
    // }

    // private fun initScanner() {
    //     val manufacturer = Build.MANUFACTURER.lowercase()
    //     Log.d("MainActivity", "Detected manufacturer: $manufacturer")

    //     when {
    //         manufacturer.contains("zebra") -> {
    //             Log.d("MainActivity", "Zebra device detected, setting up scanner")
    //             createZebraProfile("LODI_APP")
    //             setupZebraScanner()
    //         }
    //         manufacturer.contains("honeywell") -> {
    //             Log.d("MainActivity", "Honeywell device detected, setting up scanner")
    //             setupHoneywellScanner()
    //         }
    //         else -> {
    //             Log.w("MainActivity", "Unsupported device manufacturer: $manufacturer")
    //         }
    //     }
    // }

    // private fun setupZebraScanner() {
    //     scanReceiver = object: BroadcastReceiver() {
    //         override fun onReceive(context: Context, intent: Intent) {
    //             Log.d("DataWedge", "Received scan intent: ${intent.extras}")

    //             if (intent.action.equals(PROFILE_INTENT_ACTION)) {
    //                 //  A barcode has been scanned
    //                 val scanData = intent.getStringExtra("com.symbol.datawedge.data_string") as String
    //                 eventSink?.success(scanData)
    //             }
    //         }
    //     }

    //     Log.d("MainActivity", "Registering Zebra scanReceiver")
    //     Log.d("Build.VERSION", Build.VERSION.SDK_INT.toString())

    //     val intentFilter = IntentFilter()
    //     intentFilter.addAction(PROFILE_INTENT_ACTION)
    //     intentFilter.addAction("com.symbol.datawedge.api.RESULT_ACTION")
    //     intentFilter.addCategory("android.intent.category.DEFAULT")

    //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    //         registerReceiver(scanReceiver, intentFilter, RECEIVER_EXPORTED)
    //     } else {
    //         registerReceiver(scanReceiver, intentFilter)
    //     }
    // }

    // private fun setupHoneywellScanner() {
    //     AidcManager.create(this) { manager ->
    //         aidcManager = manager
    //         honeywellReader = aidcManager?.createBarcodeReader()

    //         honeywellReader?.apply {
    //             addBarcodeListener(object : BarcodeReader.BarcodeListener {
    //                 override fun onBarcodeEvent(event: BarcodeReadEvent) {
    //                     val scanned = event.barcodeData
    //                     Log.d("Honeywell", "Scanned: $scanned")

    //                     runOnUiThread {
    //                         eventSink?.success(scanned)
    //                     }
    //                 }

    //                 override fun onFailureEvent(event: BarcodeFailureEvent) {
    //                     Log.e("Honeywell", "Scan failed: $event")
    //                 }
    //             })

    //             try {
    //                 claim()
    //             } catch (e: Exception) {
    //                 Log.e("Honeywell", "Failed to claim scanner: ${e.message}")
    //             }
    //         }
    //     }
    // }

    // private fun createZebraProfile(profileName: String) {
    //     try {
    //         // Create profile if it does not exist
    //         DWInterface().sendCommandString(
    //             this,
    //             DWInterface.DATAWEDGE_SEND_CREATE_PROFILE,
    //             profileName
    //         )

    //         // Common profile configuration
    //         val profileConfig = Bundle().apply {
    //             putString("PROFILE_NAME", profileName)
    //             putString("PROFILE_ENABLED", "true")
    //             putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST")
    //         }

    //         // Barcode configuration
    //         val barcodeConfig = Bundle().apply {
    //             putString("PLUGIN_NAME", "BARCODE")
    //             putBundle("PARAM_LIST", Bundle())
    //         }

    //         // Associated app configuration
    //         val appList = Bundle().apply {
    //             putString("PACKAGE_NAME", packageName)
    //             putStringArray("ACTIVITY_LIST", arrayOf("*"))
    //         }

    //         profileConfig.putParcelableArray("APP_LIST", arrayOf(appList))
    //         profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig)
    //         DWInterface().sendCommandBundle(
    //             this,
    //             DWInterface.DATAWEDGE_SEND_SET_CONFIG,
    //             profileConfig
    //         )

    //         // Intent output configuration
    //         val intentConfig = Bundle().apply {
    //             putString("PLUGIN_NAME", "INTENT")
    //             putBundle("PARAM_LIST", Bundle().apply {
    //                 putString("intent_output_enabled", "true")
    //                 putString("intent_action", PROFILE_INTENT_ACTION)
    //                 putString("intent_delivery", "2")
    //             })
    //         }
    //         profileConfig.putBundle("PLUGIN_CONFIG", intentConfig)
    //         DWInterface().sendCommandBundle(
    //             this,
    //             DWInterface.DATAWEDGE_SEND_SET_CONFIG,
    //             profileConfig
    //         )

    //         // basic data formatting keystroke
    //         val bdfConfigKeystroke = Bundle().apply {
    //             putString("PLUGIN_NAME", "BDF")
    //             putString("OUTPUT_PLUGIN_NAME", "KEYSTROKE")
    //             putBundle("PARAM_LIST", Bundle().apply {
    //                 putString("bdf_enabled", "true")
    //                 putString("bdf_send_data", "true")
    //                 putString("bdf_send_enter", "true")
    //             })
    //         }
    //         profileConfig.putBundle("PLUGIN_CONFIG", bdfConfigKeystroke)
    //         DWInterface().sendCommandBundle(
    //             this,
    //             DWInterface.DATAWEDGE_SEND_SET_CONFIG,
    //             profileConfig
    //         )

    //         // Keystroke output configuration
    //         val keystrokeConfig = Bundle().apply {
    //             putString("PLUGIN_NAME", "KEYSTROKE")
    //             putBundle("PARAM_LIST", Bundle().apply {
    //                 putString("keystroke_output_enabled", "true")
    //                 putString("keystroke_action_char", "10")
    //             })
    //         }
    //         profileConfig.putBundle("PLUGIN_CONFIG", keystrokeConfig)
    //         DWInterface().sendCommandBundle(
    //             this,
    //             DWInterface.DATAWEDGE_SEND_SET_CONFIG,
    //             profileConfig
    //         )

    //         println("Successfully configured profile: $profileName")
    //     } catch (e: Exception) {
    //         println("Error configuring DataWedge profile: ${e.message}")
    //     }
    // }
}
