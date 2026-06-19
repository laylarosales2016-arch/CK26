package com.sam.myapplication.sync

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import com.sam.myapplication.data.AttendanceRepository
import com.sam.myapplication.data.Employee
import com.sam.myapplication.data.EmployeeBackup
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.ServerSocket

@Serializable
data class SyncData(
    val employees: List<EmployeeBackup>
)

class WifiSyncManager(
    private val context: Context,
    private val repository: AttendanceRepository
) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val serviceType = "_attendance_sync._tcp"
    private val serviceName = "AttendanceSync_${android.os.Build.MODEL}"
    
    private var server: io.ktor.server.engine.EmbeddedServer<*, *>? = null
    private var localPort: Int = 0

    private val _discoveredDevices = MutableStateFlow<List<NsdServiceInfo>>(emptyList())
    val discoveredDevices: StateFlow<List<NsdServiceInfo>> = _discoveredDevices.asStateFlow()

    private val client = HttpClient {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun startServer() {
        if (server != null) return
        
        localPort = findFreePort()
        server = embeddedServer(Netty, port = localPort) {
            install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
            routing {
                get("/employees") {
                    val employees = repository.allEmployees.first()
                    val backups = employees.map { employee ->
                        var imageBase64: String? = null
                        employee.profileImageUri?.let { uriString: String ->
                            try {
                                val uri = Uri.parse(uriString)
                                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                    val bytes = inputStream.readBytes()
                                    imageBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                                }
                            } catch (e: Exception) {
                                Log.e("WifiSyncManager", "Failed to read image for sync", e)
                            }
                        }
                        EmployeeBackup(employee, imageBase64)
                    }
                    call.respond(SyncData(backups))
                }
                post("/sync") {
                    val incomingData = call.receive<SyncData>()
                    syncLocalData(incomingData.employees)
                    call.respond(mapOf("status" to "success"))
                }
            }
        }.start(wait = false)
        
        registerService(localPort)
    }

    fun stopServer() {
        server?.stop(1000, 2000)
        server = null
        unregisterService()
    }

    private fun findFreePort(): Int {
        return ServerSocket(0).use { it.localPort }
    }

    private fun registerService(port: Int) {
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = this@WifiSyncManager.serviceName
            serviceType = this@WifiSyncManager.serviceType
            setPort(port)
        }
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
    }

    private fun unregisterService() {
        try {
            nsdManager.unregisterService(registrationListener)
        } catch (e: Exception) {
            Log.e("WifiSyncManager", "Error unregistering service", e)
        }
    }

    fun discoverPeers() {
        _discoveredDevices.value = emptyList()
        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun stopDiscovery() {
        nsdManager.stopServiceDiscovery(discoveryListener)
    }

    fun syncWithPeer(serviceInfo: NsdServiceInfo, onComplete: (Boolean) -> Unit) {
        nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e("WifiSyncManager", "Resolve failed: $errorCode")
                onComplete(false)
            }

            override fun onServiceResolved(resolvedInfo: NsdServiceInfo) {
                scope.launch {
                    try {
                        val host = resolvedInfo.host.hostAddress
                        val port = resolvedInfo.port
                        
                        // 1. Get employees from peer
                        val peerData: SyncData = client.get("http://$host:$port/employees").body()
                        syncLocalData(peerData.employees)
                        
                        // 2. Send our employees to peer
                        val ourEmployees = repository.allEmployees.first()
                        val ourBackups = ourEmployees.map { employee ->
                            var imageBase64: String? = null
                            employee.profileImageUri?.let { uriString ->
                                try {
                                    val uri = Uri.parse(uriString)
                                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                        val bytes = inputStream.readBytes()
                                        imageBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                                    }
                                } catch (e: Exception) {
                                    Log.e("WifiSyncManager", "Failed to read image for sync", e)
                                }
                            }
                            EmployeeBackup(employee, imageBase64)
                        }
                        client.post("http://$host:$port/sync") {
                            setBody(SyncData(ourBackups))
                            header(io.ktor.http.HttpHeaders.ContentType, io.ktor.http.ContentType.Application.Json)
                        }
                        
                        onComplete(true)
                    } catch (e: Exception) {
                        Log.e("WifiSyncManager", "Sync failed", e)
                        onComplete(false)
                    }
                }
            }
        })
    }

    private suspend fun syncLocalData(remoteBackups: List<EmployeeBackup>) {
        val localEmployees = repository.allEmployees.first()
        remoteBackups.forEach { backup ->
            val remote = backup.employee
            
            // Restore image if present
            var restoredUri = remote.profileImageUri
            backup.profileImageBase64?.let { base64 ->
                try {
                    val bytes = Base64.decode(base64, Base64.NO_WRAP)
                    val imagesDir = java.io.File(context.filesDir, "profile_images")
                    if (!imagesDir.exists()) imagesDir.mkdirs()
                    
                    val fileName = "sync_${System.currentTimeMillis()}_${remote.lastName ?: "unknown"}.jpg"
                    val file = java.io.File(imagesDir, fileName)
                    java.io.FileOutputStream(file).use { it.write(bytes) }
                    restoredUri = Uri.fromFile(file).toString()
                } catch (e: Exception) {
                    Log.e("WifiSyncManager", "Image restore failed during sync", e)
                }
            }

            val remoteWithImage = remote.copy(profileImageUri = restoredUri)

            val empNo = remoteWithImage.employeeNo
            val existing = if (!empNo.isNullOrBlank()) {
                repository.getEmployeeByNo(empNo)
            } else {
                localEmployees.find { 
                    it.firstName == remoteWithImage.firstName && 
                    it.lastName == remoteWithImage.lastName &&
                    it.department == remoteWithImage.department
                }
            }

            if (existing != null) {
                repository.updateEmployee(remoteWithImage.copy(id = existing.id))
            } else {
                val newId = empNo ?: "${System.currentTimeMillis()}"
                repository.insertEmployee(remoteWithImage.copy(id = newId))
            }
        }
    }

    private val registrationListener = object : NsdManager.RegistrationListener {
        override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
            Log.d("WifiSyncManager", "Service registered: ${serviceInfo.serviceName}")
        }
        override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            Log.e("WifiSyncManager", "Registration failed: $errorCode")
        }
        override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {}
        override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
    }

    private val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            nsdManager.stopServiceDiscovery(this)
        }
        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            nsdManager.stopServiceDiscovery(this)
        }
        override fun onDiscoveryStarted(serviceType: String) {}
        override fun onDiscoveryStopped(serviceType: String) {}
        override fun onServiceFound(serviceInfo: NsdServiceInfo) {
            if (serviceInfo.serviceType != serviceType) return
            if (serviceInfo.serviceName == serviceName) return
            
            val currentList = _discoveredDevices.value
            if (currentList.none { it.serviceName == serviceInfo.serviceName }) {
                _discoveredDevices.value = currentList + serviceInfo
            }
        }
        override fun onServiceLost(serviceInfo: NsdServiceInfo) {
            _discoveredDevices.value = _discoveredDevices.value.filter { it.serviceName != serviceInfo.serviceName }
        }
    }
}
