package com.example.cod.model

enum class OffenseCategory(val label: String, val description: String) {
    A("Category A", "Minor infractions"),
    B("Category B", "Moderate infractions"),
    C("Category C", "Serious infractions"),
    D("Category D", "Major infractions"),
    E("Category E", "Grave infractions")
}

data class CategoryPenalty(
    val category: OffenseCategory,
    val firstOffense: String,
    val secondOffense: String,
    val thirdOffense: String,
    val fourthOffense: String,
    val fifthOffense: String
)

data class RuleItem(
    val id: String,
    val ruleNumber: String,
    val sectionId: String,
    val description: String,
    val category: OffenseCategory,
    val pageNumber: Int,
    val firstOffense: String,
    val secondOffense: String = "-",
    val thirdOffense: String = "-",
    val fourthOffense: String = "-",
    val fifthOffense: String = "-"
)

data class RuleSection(
    val id: String,
    val title: String,
    val description: String,
    val rules: List<RuleItem>
)

enum class ReportStatus(val label: String) {
    PENDING("Pending Review"),
    UNDER_INVESTIGATION("Under Investigation"),
    RESOLVED("Resolved"),
    DISMISSED("Dismissed")
}

enum class ReportValidity(val label: String) {
    VALID("Valid"),
    INVALID("Invalid"),
    PENDING_EVALUATION("Pending Evaluation")
}

data class IncidentReport(
    val id: String,
    val title: String,
    val offenderName: String = "",
    val offenderNames: List<String> = emptyList(),
    val department: String = "",
    val date: String,
    val incidentDate: String = date,
    val incidentTime: String = "12:00 PM",
    val incidentSource: String = "Store Complaint",
    val ruleId: String? = null,
    val ruleDescription: String = "None (No Rule Violation Selected)",
    val category: OffenseCategory? = null,
    val offenseCount: String = "None",
    val recommendedPenalty: String = "None",
    val details: String,
    val complaintFormUris: List<String> = emptyList(),
    val employeeIrPhotoUris: List<String> = emptyList(),
    val rtwFusionFormUris: List<String> = emptyList(),
    val status: ReportStatus = ReportStatus.PENDING,
    val validity: ReportValidity = ReportValidity.VALID,
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedOffenderNames: String
        get() = when {
            offenderNames.isNotEmpty() -> offenderNames.joinToString(", ")
            offenderName.isNotBlank() -> offenderName
            else -> "N/A"
        }
}

data class Employee(
    val id: String,
    val name: String,
    val department: String = "",
    val position: String = "",
    val photoUri: String? = null
)
