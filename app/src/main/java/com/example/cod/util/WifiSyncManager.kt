package com.example.cod.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket

object WifiSyncManager {

    private var serverSocket: ServerSocket? = null
    var isServerRunning = false
        private set

    fun getLocalIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        val ip = addr.hostAddress
                        if (!ip.isNullOrBlank() && !ip.startsWith("127.")) {
                            return ip
                        }
                    }
                }
            }
        } catch (_: Exception) {}
        return "127.0.0.1"
    }

    suspend fun startServer(
        context: Context,
        port: Int = 8888,
        onStatusChange: (String) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                stopServer()
                serverSocket = ServerSocket(port)
                isServerRunning = true
                val ip = getLocalIpAddress()
                onStatusChange("Server active on IP $ip:$port. Waiting for receiving phone to connect...")

                while (isServerRunning && serverSocket != null && !serverSocket!!.isClosed) {
                    val clientSocket: Socket = try {
                        serverSocket!!.accept()
                    } catch (_: Exception) {
                        break
                    }

                    onStatusChange("Connected to receiving phone (${clientSocket.inetAddress.hostAddress}). Transmitting data...")

                    val writer = PrintWriter(clientSocket.getOutputStream(), true)
                    val jsonPayload = BackupManager.createBackupJson(context)

                    writer.println("COD_TRANSFER_START")
                    writer.println(jsonPayload)
                    writer.println("COD_TRANSFER_END")
                    writer.flush()

                    clientSocket.close()
                    onStatusChange("Data successfully transferred to receiving phone! Server still active on $ip:$port.")
                }
            } catch (e: Exception) {
                isServerRunning = false
                onStatusChange("Server stopped: ${e.localizedMessage}")
            }
        }
    }

    fun stopServer() {
        try {
            isServerRunning = false
            serverSocket?.close()
            serverSocket = null
        } catch (_: Exception) {}
    }

    suspend fun receiveDataFromSender(
        context: Context,
        senderIp: String,
        port: Int = 8888,
        onProgress: (String) -> Unit
    ): Pair<Boolean, String> {
        return withContext(Dispatchers.IO) {
            try {
                onProgress("Connecting to $senderIp:$port...")
                val socket = Socket(senderIp.trim(), port)
                socket.soTimeout = 15000

                onProgress("Connected! Receiving company reports & photos...")
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                val jsonBuilder = StringBuilder()
                var receiving = false

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val currentLine = line ?: break
                    if (currentLine == "COD_TRANSFER_START") {
                        receiving = true
                        continue
                    }
                    if (currentLine == "COD_TRANSFER_END") {
                        break
                    }
                    if (receiving) {
                        jsonBuilder.append(currentLine).append("\n")
                    }
                }
                socket.close()

                val jsonString = jsonBuilder.toString()
                if (jsonString.isBlank()) {
                    return@withContext false to "Received empty payload from sender."
                }

                onProgress("Restoring received records into local storage...")
                val restored = BackupManager.restoreBackupFromJson(context, jsonString)
                if (restored) {
                    true to "All company reports, employees, and photos successfully transferred & restored!"
                } else {
                    false to "Failed to parse transferred backup JSON."
                }
            } catch (e: Exception) {
                false to "Connection error: ${e.localizedMessage}"
            }
        }
    }
}
