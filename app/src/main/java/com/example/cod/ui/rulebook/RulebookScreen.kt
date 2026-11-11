package com.example.cod.ui.rulebook

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cod.data.CODRepository
import com.example.cod.model.CategoryPenalty
import com.example.cod.model.OffenseCategory
import com.example.cod.model.RuleItem
import com.example.cod.model.RuleSection

@Composable
fun RulebookScreen(
    onSelectRuleToReport: ((RuleItem) -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<OffenseCategory?>(null) }
    var showTableOfOffenses by remember { mutableStateOf(false) }

    val filteredSections = remember(searchQuery, selectedCategoryFilter) {
        CODRepository.ruleSections.mapNotNull { section ->
            val filteredRules = section.rules.filter { rule ->
                val matchesCategory = selectedCategoryFilter == null || rule.category == selectedCategoryFilter
                val matchesSearch = searchQuery.isBlank() ||
                        rule.description.contains(searchQuery, ignoreCase = true) ||
                        section.title.contains(searchQuery, ignoreCase = true) ||
                        rule.category.name.contains(searchQuery, ignoreCase = true)

                matchesCategory && matchesSearch
            }
            if (filteredRules.isNotEmpty()) {
                section.copy(rules = filteredRules)
            } else null
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "FISC CODE OF DISCIPLINE",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Official Rulebook & Penalty Matrix",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search rules, offense descriptions, categories...") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter by Category:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(onClick = { showTableOfOffenses = !showTableOfOffenses }) {
                        Text(if (showTableOfOffenses) "Hide Matrix" else "View Penalty Matrix")
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text("All") }
                        )
                    }
                    items(OffenseCategory.entries.toTypedArray()) { category ->
                        FilterChip(
                            selected = selectedCategoryFilter == category,
                            onClick = {
                                selectedCategoryFilter = if (selectedCategoryFilter == category) null else category
                            },
                            label = { Text(category.label) }
                        )
                    }
                }
            }
        }

        item {
            AnimatedVisibility(visible = showTableOfOffenses) {
                TableOfOffensesMatrixCard(penalties = CODRepository.categoryPenalties)
            }
        }

        if (filteredSections.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matching rules found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredSections, key = { it.id }) { section ->
                SectionCard(
                    section = section,
                    onSelectRuleToReport = onSelectRuleToReport
                )
            }
        }
    }
}

@Composable
fun TableOfOffensesMatrixCard(penalties: List<CategoryPenalty>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "TABLE OF OFFENSES (PENALTY MATRIX)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(8.dp)
            ) {
                Text("CAT", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("1st", modifier = Modifier.weight(1.8f), fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center)
                Text("2nd", modifier = Modifier.weight(1.8f), fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center)
                Text("3rd", modifier = Modifier.weight(1.8f), fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center)
                Text("4th", modifier = Modifier.weight(1.8f), fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center)
                Text("5th", modifier = Modifier.weight(1.8f), fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(4.dp))

            penalties.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.category.name,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.ExtraBold,
                        color = getCategoryColor(item.category),
                        fontSize = 13.sp
                    )
                    Text(item.firstOffense, modifier = Modifier.weight(1.8f), fontSize = 10.sp, textAlign = TextAlign.Center, lineHeight = 11.sp)
                    Text(item.secondOffense, modifier = Modifier.weight(1.8f), fontSize = 10.sp, textAlign = TextAlign.Center, lineHeight = 11.sp)
                    Text(item.thirdOffense, modifier = Modifier.weight(1.8f), fontSize = 10.sp, textAlign = TextAlign.Center, lineHeight = 11.sp)
                    Text(item.fourthOffense, modifier = Modifier.weight(1.8f), fontSize = 10.sp, textAlign = TextAlign.Center, lineHeight = 11.sp)
                    Text(item.fifthOffense, modifier = Modifier.weight(1.8f), fontSize = 10.sp, textAlign = TextAlign.Center, lineHeight = 11.sp)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
fun SectionCard(
    section: RuleSection,
    onSelectRuleToReport: ((RuleItem) -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = section.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            section.rules.forEachIndexed { index, rule ->
                RuleItemCard(
                    rule = rule,
                    onSelectRuleToReport = onSelectRuleToReport
                )
                if (index < section.rules.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun RuleItemCard(
    rule: RuleItem,
    onSelectRuleToReport: ((RuleItem) -> Unit)?
) {
    val categoryColor = getCategoryColor(rule.category)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Rule ${rule.ruleNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Page ${rule.pageNumber}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Surface(
                color = categoryColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, categoryColor)
            ) {
                Text(
                    text = rule.category.label,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = categoryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = rule.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Penalty Progression:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PenaltyBadgeRow("1st Offense", rule.firstOffense)
            if (rule.secondOffense != "-") PenaltyBadgeRow("2nd Offense", rule.secondOffense)
            if (rule.thirdOffense != "-") PenaltyBadgeRow("3rd Offense", rule.thirdOffense)
            if (rule.fourthOffense != "-") PenaltyBadgeRow("4th Offense", rule.fourthOffense)
            if (rule.fifthOffense != "-") PenaltyBadgeRow("5th Offense", rule.fifthOffense)
        }

        if (onSelectRuleToReport != null) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = { onSelectRuleToReport(rule) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Report Violation")
            }
        }
    }
}

@Composable
fun PenaltyBadgeRow(offenseLabel: String, penalty: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = offenseLabel,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = penalty,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (penalty.contains("TERMINATION")) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface
        )
    }
}

fun getCategoryColor(category: OffenseCategory?): Color {
    return when (category) {
        OffenseCategory.A -> Color(0xFF388E3C)
        OffenseCategory.B -> Color(0xFF0288D1)
        OffenseCategory.C -> Color(0xFFF57C00)
        OffenseCategory.D -> Color(0xFFE64A19)
        OffenseCategory.E -> Color(0xFFD32F2F)
        null -> Color(0xFF757575)
    }
}
