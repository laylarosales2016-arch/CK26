package com.example.cod.util

import android.content.Context
import com.example.cod.data.CODRepository
import com.example.cod.model.Employee
import com.example.cod.model.IncidentReport
import com.example.cod.model.OffenseCategory
import com.example.cod.model.ReportStatus
import com.example.cod.model.ReportValidity
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object BackupManager {

    fun createBackupJson(context: Context): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())

        val employeesArray = JSONArray()
        val employees = CODRepository.employeesFlow.value
        for (emp in employees) {
            val empObj = JSONObject()
            empObj.put("id", emp.id)
            empObj.put("name", emp.name)
            empObj.put("department", emp.department)
            empObj.put("position", emp.position)
            if (!emp.photoUri.isNullOrBlank()) {
                val photoBase64 = PhotoStorageUtil.photoUriToBase64(context, emp.photoUri)
                empObj.put("photoBase64", photoBase64 ?: "")
            }
            employeesArray.put(empObj)
        }
        root.put("employees", employeesArray)

        val reportsArray = JSONArray()
        val reports = CODRepository.reportsFlow.value
        for (rep in reports) {
            val repObj = JSONObject()
            repObj.put("id", rep.id)
            repObj.put("title", rep.title)
            repObj.put("offenderName", rep.offenderName)
            val namesArr = JSONArray()
            rep.offenderNames.forEach { namesArr.put(it) }
            repObj.put("offenderNames", namesArr)
            repObj.put("department", rep.department)
            repObj.put("date", rep.date)
            repObj.put("incidentDate", rep.incidentDate)
            repObj.put("incidentTime", rep.incidentTime)
            repObj.put("incidentSource", rep.incidentSource)
            repObj.put("ruleId", rep.ruleId ?: "")
            repObj.put("ruleDescription", rep.ruleDescription)
            repObj.put("category", rep.category?.name ?: "")
            repObj.put("offenseCount", rep.offenseCount)
            repObj.put("recommendedPenalty", rep.recommendedPenalty)
            repObj.put("details", rep.details)
            repObj.put("status", rep.status.name)
            repObj.put("validity", rep.validity.name)
            repObj.put("timestamp", rep.timestamp)

            val complaintArray = JSONArray()
            for (uri in rep.complaintFormUris) {
                if (uri.isNotBlank()) complaintArray.put(uri)
            }
            repObj.put("complaintFormUris", complaintArray)

            val employeeIrArray = JSONArray()
            for (uri in rep.employeeIrPhotoUris) {
                if (uri.isNotBlank()) employeeIrArray.put(uri)
            }
            repObj.put("employeeIrPhotoUris", employeeIrArray)

            val rtwFusionArray = JSONArray()
            for (uri in rep.rtwFusionFormUris) {
                if (uri.isNotBlank()) rtwFusionArray.put(uri)
            }
            repObj.put("rtwFusionFormUris", rtwFusionArray)

            reportsArray.put(repObj)
        }
        root.put("reports", reportsArray)

        return root.toString(2)
    }

    fun exportBackupToZipStream(context: Context, outputStream: OutputStream) {
        ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
            val jsonPayload = createBackupJson(context)
            val jsonEntry = ZipEntry("data.json")
            zipOut.putNextEntry(jsonEntry)
            zipOut.write(jsonPayload.toByteArray(Charsets.UTF_8))
            zipOut.closeEntry()

            val photosDir = File(context.filesDir, "photos")
            if (photosDir.exists()) {
                photosDir.listFiles()?.forEach { file ->
                    if (file.isFile && file.length() > 0) {
                        try {
                            val photoEntry = ZipEntry("photos/${file.name}")
                            zipOut.putNextEntry(photoEntry)
                            file.inputStream().use { input ->
                                input.copyTo(zipOut)
                            }
                            zipOut.closeEntry()
                        } catch (_: Exception) {}
                    }
                }
            }
        }
    }

    fun restoreBackupFromZipStream(context: Context, inputStream: InputStream): Boolean {
        var jsonContent: String? = null
        val photosDir = File(context.filesDir, "photos")
        if (!photosDir.exists()) photosDir.mkdirs()

        try {
            val bytes = inputStream.readBytes()
            val inputForZip = bytes.inputStream()

            ZipInputStream(BufferedInputStream(inputForZip)).use { zipIn ->
                var entry: ZipEntry? = zipIn.nextEntry
                while (entry != null) {
                    val entryName = entry.name
                    if (entryName == "data.json") {
                        jsonContent = zipIn.bufferedReader().readText()
                    } else if (entryName.startsWith("photos/")) {
                        val fileName = File(entryName).name
                        if (fileName.isNotBlank()) {
                            val destFile = File(photosDir, fileName)
                            FileOutputStream(destFile).use { out ->
                                zipIn.copyTo(out)
                            }
                        }
                    }
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }

            if (!jsonContent.isNullOrBlank()) {
                return restoreBackupFromJson(context, jsonContent)
            }

            val rawString = String(bytes, Charsets.UTF_8)
            if (rawString.trim().startsWith("{")) {
                return restoreBackupFromJson(context, rawString)
            }
        } catch (_: Exception) {}

        return false
    }

    fun restoreBackupFromJson(context: Context, jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            val restoredEmployees = mutableListOf<Employee>()
            val restoredReports = mutableListOf<IncidentReport>()

            if (root.has("employees")) {
                val employeesArray = root.getJSONArray("employees")
                for (i in 0 until employeesArray.length()) {
                    val empObj = employeesArray.getJSONObject(i)
                    val id = empObj.optString("id", "EMP-${System.currentTimeMillis()}")
                    val name = empObj.optString("name", "")
                    val department = empObj.optString("department", "")
                    val position = empObj.optString("position", "")

                    var photoUri: String? = null
                    if (empObj.has("photoBase64")) {
                        val base64 = empObj.getString("photoBase64")
                        if (base64.isNotBlank()) {
                            photoUri = PhotoStorageUtil.base64ToPhotoUri(context, base64)
                        }
                    }

                    if (name.isNotBlank()) {
                        restoredEmployees.add(Employee(id, name, department, position, photoUri))
                    }
                }
            }

            if (root.has("reports")) {
                val reportsArray = root.getJSONArray("reports")
                for (i in 0 until reportsArray.length()) {
                    val repObj = reportsArray.getJSONObject(i)
                    val id = repObj.optString("id", "REP-${System.currentTimeMillis()}")
                    val title = repObj.optString("title", "")
                    val offenderName = repObj.optString("offenderName", "")
                    val offenderNames = mutableListOf<String>()
                    if (repObj.has("offenderNames")) {
                        val arr = repObj.getJSONArray("offenderNames")
                        for (k in 0 until arr.length()) {
                            offenderNames.add(arr.getString(k))
                        }
                    } else if (offenderName.isNotBlank()) {
                        offenderNames.add(offenderName)
                    }

                    val incidentSource = repObj.optString("incidentSource", "Store Complaint")
                    val department = repObj.optString("department", "")
                    val date = repObj.optString("date", "")
                    val incidentDate = repObj.optString("incidentDate", date)
                    val incidentTime = repObj.optString("incidentTime", "12:00 PM")
                    val ruleIdStr = repObj.optString("ruleId", "")
                    val ruleId = if (ruleIdStr.isBlank()) null else ruleIdStr
                    val ruleDescription = repObj.optString("ruleDescription", "None")
                    val categoryStr = repObj.optString("category", "")
                    val category = try { OffenseCategory.valueOf(categoryStr) } catch (_: Exception) { null }
                    val offenseCount = repObj.optString("offenseCount", "None")
                    val recommendedPenalty = repObj.optString("recommendedPenalty", "None")
                    val details = repObj.optString("details", "")
                    val statusStr = repObj.optString("status", "PENDING")
                    val status = try { ReportStatus.valueOf(statusStr) } catch (_: Exception) { ReportStatus.PENDING }
                    val validityStr = repObj.optString("validity", "VALID")
                    val validity = try { ReportValidity.valueOf(validityStr) } catch (_: Exception) { ReportValidity.VALID }
                    val timestamp = repObj.optLong("timestamp", System.currentTimeMillis())

                    val complaintFormUris = mutableListOf<String>()
                    if (repObj.has("complaintFormUris")) {
                        val arr = repObj.getJSONArray("complaintFormUris")
                        for (j in 0 until arr.length()) {
                            val uriStr = arr.getString(j)
                            if (uriStr.isNotBlank()) complaintFormUris.add(uriStr)
                        }
                    } else if (repObj.has("complaintFormPhotos")) {
                        val arr = repObj.getJSONArray("complaintFormPhotos")
                        for (j in 0 until arr.length()) {
                            val base64 = arr.getString(j)
                            val restoredUri = PhotoStorageUtil.base64ToPhotoUri(context, base64)
                            if (restoredUri != null) complaintFormUris.add(restoredUri)
                        }
                    }

                    val employeeIrPhotoUris = mutableListOf<String>()
                    if (repObj.has("employeeIrPhotoUris")) {
                        val arr = repObj.getJSONArray("employeeIrPhotoUris")
                        for (j in 0 until arr.length()) {
                            val uriStr = arr.getString(j)
                            if (uriStr.isNotBlank()) employeeIrPhotoUris.add(uriStr)
                        }
                    } else if (repObj.has("employeeIrPhotos")) {
                        val arr = repObj.getJSONArray("employeeIrPhotos")
                        for (j in 0 until arr.length()) {
                            val base64 = arr.getString(j)
                            val restoredUri = PhotoStorageUtil.base64ToPhotoUri(context, base64)
                            if (restoredUri != null) employeeIrPhotoUris.add(restoredUri)
                        }
                    }

                    val rtwFusionFormUris = mutableListOf<String>()
                    if (repObj.has("rtwFusionFormUris")) {
                        val arr = repObj.getJSONArray("rtwFusionFormUris")
                        for (j in 0 until arr.length()) {
                            val uriStr = arr.getString(j)
                            if (uriStr.isNotBlank()) rtwFusionFormUris.add(uriStr)
                        }
                    } else if (repObj.has("rtwFusionPhotos")) {
                        val arr = repObj.getJSONArray("rtwFusionPhotos")
                        for (j in 0 until arr.length()) {
                            val base64 = arr.getString(j)
                            val restoredUri = PhotoStorageUtil.base64ToPhotoUri(context, base64)
                            if (restoredUri != null) rtwFusionFormUris.add(restoredUri)
                        }
                    }

                    if (title.isNotBlank()) {
                        restoredReports.add(
                            IncidentReport(
                                id = id,
                                title = title,
                                offenderName = if (offenderNames.isNotEmpty()) offenderNames.joinToString(", ") else offenderName,
                                offenderNames = offenderNames,
                                incidentSource = incidentSource,
                                department = department,
                                date = date,
                                incidentDate = incidentDate,
                                incidentTime = incidentTime,
                                ruleId = ruleId,
                                ruleDescription = ruleDescription,
                                category = category,
                                offenseCount = offenseCount,
                                recommendedPenalty = recommendedPenalty,
                                details = details,
                                complaintFormUris = complaintFormUris,
                                employeeIrPhotoUris = employeeIrPhotoUris,
                                rtwFusionFormUris = rtwFusionFormUris,
                                status = status,
                                validity = validity,
                                timestamp = timestamp
                            )
                        )
                    }
                }
            }

            CODRepository.setAllData(restoredReports, restoredEmployees)
            true
        } catch (_: Exception) {
            false
        }
    }
}
