package com.example.cod.data

import com.example.cod.model.CategoryPenalty
import com.example.cod.model.Employee
import com.example.cod.model.IncidentReport
import com.example.cod.model.OffenseCategory
import com.example.cod.model.ReportStatus
import com.example.cod.model.RuleItem
import com.example.cod.model.RuleSection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CODRepository {

    val categoryPenalties = listOf(
        CategoryPenalty(
            category = OffenseCategory.A,
            firstOffense = "COUNSELING",
            secondOffense = "WRITTEN REPRIMAND",
            thirdOffense = "5 DAYS SUSPENSION",
            fourthOffense = "15 DAYS SUSPENSION",
            fifthOffense = "TERMINATION"
        ),
        CategoryPenalty(
            category = OffenseCategory.B,
            firstOffense = "WRITTEN REPRIMAND",
            secondOffense = "5 DAYS SUSPENSION",
            thirdOffense = "15 DAYS SUSPENSION",
            fourthOffense = "TERMINATION",
            fifthOffense = "-"
        ),
        CategoryPenalty(
            category = OffenseCategory.C,
            firstOffense = "5 DAYS SUSPENSION",
            secondOffense = "15 DAYS SUSPENSION",
            thirdOffense = "TERMINATION",
            fourthOffense = "-",
            fifthOffense = "-"
        ),
        CategoryPenalty(
            category = OffenseCategory.D,
            firstOffense = "15 DAYS SUSPENSION",
            secondOffense = "TERMINATION",
            thirdOffense = "-",
            fourthOffense = "-",
            fifthOffense = "-"
        ),
        CategoryPenalty(
            category = OffenseCategory.E,
            firstOffense = "TERMINATION",
            secondOffense = "-",
            thirdOffense = "-",
            fourthOffense = "-",
            fifthOffense = "-"
        )
    )

    val ruleSections = listOf(
        // SECTION I
        RuleSection(
            id = "SEC_1",
            title = "I. ON CLIENT'S CUSTOMERS",
            description = "REFERS TO ANY ACTS/OMISSIONS COMMITTED AGAINST OR IN THE PRESENCE OF CLIENT'S CUSTOMERS",
            rules = listOf(
                RuleItem(
                    id = "SEC_1_RULE_1",
                    ruleNumber = "1",
                    sectionId = "SEC_1",
                    pageNumber = 4,
                    description = "1. Any unacceptable behavior toward or involving the client's customer including but not limited to arguing, debating, or fighting with the client's customer for any reason.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_1_RULE_2",
                    ruleNumber = "2",
                    sectionId = "SEC_1",
                    pageNumber = 4,
                    description = "2. Using offensive language or exhibiting rude behavior in the immediate presence of, or directed at the client's customer.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                )
            )
        ),

        // SECTION II
        RuleSection(
            id = "SEC_2",
            title = "II. ON CO-WORKERS, SUPERIOR RELATIONSHIPS",
            description = "REFERS TO ANY ACTS/OMISSIONS THAT COULD AFFECT RELATIONS WITH THE SUPERIOR AND/OR WITHIN THE TEAM",
            rules = listOf(
                RuleItem(
                    id = "SEC_2_RULE_1",
                    ruleNumber = "1",
                    sectionId = "SEC_2",
                    pageNumber = 4,
                    description = "1. Habitual tardiness; coming in late for shift/ duty after meals/ coffee break for 7 times or more within a calendar month.",
                    category = OffenseCategory.A,
                    firstOffense = "COUNSELING",
                    secondOffense = "WRITTEN REPRIMAND",
                    thirdOffense = "5 DAYS SUSPENSION",
                    fourthOffense = "15 DAYS SUSPENSION",
                    fifthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_2",
                    ruleNumber = "2",
                    sectionId = "SEC_2",
                    pageNumber = 4,
                    description = "2. Logging in or out when not in a prescribed uniform or dress code.",
                    category = OffenseCategory.A,
                    firstOffense = "COUNSELING",
                    secondOffense = "WRITTEN REPRIMAND",
                    thirdOffense = "5 DAYS SUSPENSION",
                    fourthOffense = "15 DAYS SUSPENSION",
                    fifthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_3",
                    ruleNumber = "3",
                    sectionId = "SEC_2",
                    pageNumber = 4,
                    description = "3. Taking meals or coffee breaks outside of authorized or scheduled time.",
                    category = OffenseCategory.A,
                    firstOffense = "COUNSELING",
                    secondOffense = "WRITTEN REPRIMAND",
                    thirdOffense = "5 DAYS SUSPENSION",
                    fourthOffense = "15 DAYS SUSPENSION",
                    fifthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_4",
                    ruleNumber = "4",
                    sectionId = "SEC_2",
                    pageNumber = 4,
                    description = "4. Taking off or not reporting for work without any justifiable reason in spite of FISC superior's specific instructions to report, or where no suitable reliever has been secured. Including changing shift or rest day without superior's permission.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_5",
                    ruleNumber = "5",
                    sectionId = "SEC_2",
                    pageNumber = 4,
                    description = "5. Failure to log in/out more than once within a 15-day period.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_6",
                    ruleNumber = "6",
                    sectionId = "SEC_2",
                    pageNumber = 6,
                    description = "6. Failure to file leaves or field work on time through the timekeeping system or any prescribed form within 30 days from date of reporting to office.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_7",
                    ruleNumber = "7",
                    sectionId = "SEC_2",
                    pageNumber = 6,
                    description = "7. Absence without official leave (AWOL) or failure to advise one's immediate superior of one's absence at least two hours before scheduled duty.",
                    category = OffenseCategory.A,
                    firstOffense = "COUNSELING",
                    secondOffense = "WRITTEN REPRIMAND",
                    thirdOffense = "5 DAYS SUSPENSION",
                    fourthOffense = "15 DAYS SUSPENSION",
                    fifthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_8",
                    ruleNumber = "8",
                    sectionId = "SEC_2",
                    pageNumber = 6,
                    description = "8. Pursuing any personal interest on compensable time, including sleeping or loitering on the job; carrying out side jobs within cooperative or client' premises.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_9",
                    ruleNumber = "9",
                    sectionId = "SEC_2",
                    pageNumber = 6,
                    description = "9. Intentionally misleading a Client's representative or co-worker (including the giving of erroneous instructions or information, withholding of information, or making untrue statements) which causes an established standard to be violated or wrong action to be taken.",
                    category = OffenseCategory.C,
                    firstOffense = "5 DAYS SUSPENSION",
                    secondOffense = "15 DAYS SUSPENSION",
                    thirdOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_10",
                    ruleNumber = "10",
                    sectionId = "SEC_2",
                    pageNumber = 7,
                    description = "10. Taking undue advantage of one's position or authority.",
                    category = OffenseCategory.C,
                    firstOffense = "5 DAYS SUSPENSION",
                    secondOffense = "15 DAYS SUSPENSION",
                    thirdOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_11",
                    ruleNumber = "11",
                    sectionId = "SEC_2",
                    pageNumber = 7,
                    description = "11. Discrimination in any form in reference to co-worker's race, religion, ethnicity, sexual preference or orientation.",
                    category = OffenseCategory.C,
                    firstOffense = "5 DAYS SUSPENSION",
                    secondOffense = "15 DAYS SUSPENSION",
                    thirdOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_12",
                    ruleNumber = "12",
                    sectionId = "SEC_2",
                    pageNumber = 7,
                    description = "12. Leaving one's work or place of work while on duty; deviating from an assigned or usual route without proper authorization.",
                    category = OffenseCategory.C,
                    firstOffense = "5 DAYS SUSPENSION",
                    secondOffense = "15 DAYS SUSPENSION",
                    thirdOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_13",
                    ruleNumber = "13",
                    sectionId = "SEC_2",
                    pageNumber = 7,
                    description = "13. Disrespect of co-workers. Using profane, obscene, vile or abusive language or gestures, or using provoking/ insulting remarks directed to a superior or coworker.",
                    category = OffenseCategory.C,
                    firstOffense = "5 DAYS SUSPENSION",
                    secondOffense = "15 DAYS SUSPENSION",
                    thirdOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_14",
                    ruleNumber = "14",
                    sectionId = "SEC_2",
                    pageNumber = 7,
                    description = "14. Hampering operations through excessive unauthorized absences. (NB: 3 days unauthorized leaves of any kind within a calendar month shall be considered excessive.)",
                    category = OffenseCategory.C,
                    firstOffense = "5 DAYS SUSPENSION",
                    secondOffense = "15 DAYS SUSPENSION",
                    thirdOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_15",
                    ruleNumber = "15",
                    sectionId = "SEC_2",
                    pageNumber = 8,
                    description = "15. Moonlighting or holding a second job without proper authorization.",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_16",
                    ruleNumber = "16",
                    sectionId = "SEC_2",
                    pageNumber = 8,
                    description = "16. Threatening, intimidating or coercing co-workers, or interfering with their work that may result to failure to observe/ maintain operation standards.",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_17",
                    ruleNumber = "17",
                    sectionId = "SEC_2",
                    pageNumber = 8,
                    description = "17. Allowing or causing an unauthorized or non-eligible person to avail of one's own cooperative or client privilege or benefit.",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_18",
                    ruleNumber = "18",
                    sectionId = "SEC_2",
                    pageNumber = 8,
                    description = "18. Betting or gambling in any form within cooperative or client premises, whether on duty or not.",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_19",
                    ruleNumber = "19",
                    sectionId = "SEC_2",
                    pageNumber = 8,
                    description = "19. Insubordination. Disrespect of authority or willful disregard of the lawful orders of a person in authority, refusal to obey superior's instructions, or to perform an assigned work.",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_20",
                    ruleNumber = "20",
                    sectionId = "SEC_2",
                    pageNumber = 9,
                    description = "20. Logging in/out for another employee; allowing another employee to log in/out on one's behalf.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_21",
                    ruleNumber = "21",
                    sectionId = "SEC_2",
                    pageNumber = 9,
                    description = "21. Planting evidence against another employee in order to impute the commission of an offense against the latter; or bearing false witness against a co-employee or fellow service provider or failure to cooperate in an investigation to the detriment of another employee.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_22",
                    ruleNumber = "22",
                    sectionId = "SEC_2",
                    pageNumber = 9,
                    description = "22. Willfully holding back, slowing down, hindering, or limiting work output.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_23",
                    ruleNumber = "23",
                    sectionId = "SEC_2",
                    pageNumber = 9,
                    description = "23. Using, attempting, or threatening to use physical force on any person within cooperative or client's premises, whether on duty or not, or in a cooperative or client-sponsored social functions. Or, if outside the cooperative or client premises, when one is clearly identifiable as a service provider.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_24",
                    ruleNumber = "24",
                    sectionId = "SEC_2",
                    pageNumber = 10,
                    description = "24. Falsifying, attempting to falsify, or tampering with Cooperative records and documents including sales receipts, time records, employment records, Professional credentials, references, computer records etc.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_25",
                    ruleNumber = "25",
                    sectionId = "SEC_2",
                    pageNumber = 10,
                    description = "25. Commission of a crime or offense against the person of Cooperative CEO, Board, ManCom or Project Managers, or any immediate member of their family, supervisor, or co-workers. (This shall be subject to disciplinary action without prejudice to any criminal and/or civil liability against the service provider).",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_26",
                    ruleNumber = "26",
                    sectionId = "SEC_2",
                    pageNumber = 10,
                    description = "26. Conviction of a crime involving any act of baseness, vileness, or depravity.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_27A",
                    ruleNumber = "27.a",
                    sectionId = "SEC_2",
                    pageNumber = 10,
                    description = "27. Commission of sexual harassment acts: a. Giving smutty jokes",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_27B",
                    ruleNumber = "27.b",
                    sectionId = "SEC_2",
                    pageNumber = 11,
                    description = "27.b. Displaying pornographic or obscene pictures, materials or publications in the workplace",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_27C",
                    ruleNumber = "27.c",
                    sectionId = "SEC_2",
                    pageNumber = 11,
                    description = "27.c. Leering or ogling; unsolicited or unwanted suggestive flirtations",
                    category = OffenseCategory.C,
                    firstOffense = "5 DAYS SUSPENSION",
                    secondOffense = "15 DAYS SUSPENSION",
                    thirdOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_27D",
                    ruleNumber = "27.d",
                    sectionId = "SEC_2",
                    pageNumber = 11,
                    description = "27.d. Derogatory, degrading, or suggestive remarks directed toward or against members of one sex or one's sexual orientation.",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_27E",
                    ruleNumber = "27.e",
                    sectionId = "SEC_2",
                    pageNumber = 11,
                    description = "27.e. Unnecessary touching or brushing against victim's body",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_27F",
                    ruleNumber = "27.f",
                    sectionId = "SEC_2",
                    pageNumber = 11,
                    description = "27.f. Suggestive gestures, expressions or lewd insinuations.",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_27H",
                    ruleNumber = "27.h",
                    sectionId = "SEC_2",
                    pageNumber = 11,
                    description = "27.h. Threats which are sexually oriented.",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_27I",
                    ruleNumber = "27.i",
                    sectionId = "SEC_2",
                    pageNumber = 11,
                    description = "27.i. Stalking",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_27J",
                    ruleNumber = "27.j",
                    sectionId = "SEC_2",
                    pageNumber = 11,
                    description = "27.j. Requesting for dates or sexual favors in exchange for employment, promotion, continued employment and any scenario that may influence a report or performance rating",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_2_RULE_27K",
                    ruleNumber = "27.k",
                    sectionId = "SEC_2",
                    pageNumber = 11,
                    description = "27.k. Rape; sexual assault, unwanted touching of private parts or any other overt sexual advances",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                )
            )
        ),

        // SECTION III
        RuleSection(
            id = "SEC_3",
            title = "III. ON COMPANY IMAGE",
            description = "REFERS TO ACTS/OMISSIONS COMMITTED THAT MAY ADVERSELY AFFECT SERVICES AND/OR PUBLIC IMAGE OF THE COOPERATIVE OR CLIENT",
            rules = listOf(
                RuleItem(
                    id = "SEC_3_RULE_1",
                    ruleNumber = "1",
                    sectionId = "SEC_3",
                    pageNumber = 12,
                    description = "1. Carrying money within one's person while on duty.",
                    category = OffenseCategory.A,
                    firstOffense = "COUNSELING",
                    secondOffense = "WRITTEN REPRIMAND",
                    thirdOffense = "5 DAYS SUSPENSION",
                    fourthOffense = "15 DAYS SUSPENSION",
                    fifthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_3_RULE_2",
                    ruleNumber = "2",
                    sectionId = "SEC_3",
                    pageNumber = 12,
                    description = "2. Habitual non-observance of Client's outlet or unit's internal regulation (i.e., the service provider's attention has been called once before on the same matter with no corrective action on the service provider's part). DA is due on the 2nd instance.",
                    category = OffenseCategory.A,
                    firstOffense = "COUNSELING",
                    secondOffense = "WRITTEN REPRIMAND",
                    thirdOffense = "5 DAYS SUSPENSION",
                    fourthOffense = "15 DAYS SUSPENSION",
                    fifthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_3_RULE_3",
                    ruleNumber = "3",
                    sectionId = "SEC_3",
                    pageNumber = 12,
                    description = "3. Failure to observe personal hygiene practices which affect or may affect the cooperative or client interest and/or processes.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_3_RULE_4",
                    ruleNumber = "4",
                    sectionId = "SEC_3",
                    pageNumber = 12,
                    description = "4. Failure to immediately report serious concerns or issues to one's immediate superior especially when it affects or may affect the cooperative or client, cause delay in business operations, or grievance against any person.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_3_RULE_5",
                    ruleNumber = "5",
                    sectionId = "SEC_3",
                    pageNumber = 13,
                    description = "5. Making false, vicious, profane or malicious statements concerning any employee or fellow service providers, the cooperative/client, or its services.",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_3_RULE_6",
                    ruleNumber = "6",
                    sectionId = "SEC_3",
                    pageNumber = 13,
                    description = "6. Solicitation, direct or indirect, or acceptance of any gift or privilege of whatever kind, the value of which exceeds the allowable amount, from third parties who deal with the cooperative or client.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_3_RULE_7",
                    ruleNumber = "7",
                    sectionId = "SEC_3",
                    pageNumber = 13,
                    description = "7. Product Tampering",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_3_RULE_8",
                    ruleNumber = "8",
                    sectionId = "SEC_3",
                    pageNumber = 13,
                    description = "8. Immoral behavior or carrying on a scandalous relationship especially when it affects or may affect the cooperative's image, team relationship or productivity/ performance at work (i.e., at least one is a married employee)",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_3_RULE_9",
                    ruleNumber = "9",
                    sectionId = "SEC_3",
                    pageNumber = 13,
                    description = "9. Receiving commissions or undisclosed profits in the course of transaction(s) in which one represents the cooperative",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_3_RULE_10",
                    ruleNumber = "10",
                    sectionId = "SEC_3",
                    pageNumber = 13,
                    description = "10. Failure to comply with regulatory requirements of Department of Labor.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                )
            )
        ),

        // SECTION IV
        RuleSection(
            id = "SEC_4",
            title = "IV. ON COMPANY PROPERTY/RESOURCES",
            description = "REFERS TO ACTS/OMISSIONS THAT RESULT IN LOSS, DAMAGE OR WASTAGE OF COOPERATIVE OR CLIENT RESOURCES",
            rules = listOf(
                RuleItem(
                    id = "SEC_4_RULE_1",
                    ruleNumber = "1",
                    sectionId = "SEC_4",
                    pageNumber = 14,
                    description = "1. Failure to liquidate cash advances without any justifiable reason within 30 days after date of intended use.",
                    category = OffenseCategory.A,
                    firstOffense = "COUNSELING",
                    secondOffense = "WRITTEN REPRIMAND",
                    thirdOffense = "5 DAYS SUSPENSION",
                    fourthOffense = "15 DAYS SUSPENSION",
                    fifthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_4_RULE_2",
                    ruleNumber = "2",
                    sectionId = "SEC_4",
                    pageNumber = 14,
                    description = "2. Defacing or marking of Cooperative/client's walls, materials, or other property",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_4_RULE_3",
                    ruleNumber = "3",
                    sectionId = "SEC_4",
                    pageNumber = 14,
                    description = "3. Use of cooperative or client's electronic mail services for personal financial gain, non-business activities (such as, but not limited to, chain letters, junk mail, spam or unsolicited commercial e-mails, social networks, and network games), and commercial purposes not related to the business of the Cooperative/Client.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_4_RULE_4",
                    ruleNumber = "4",
                    sectionId = "SEC_4",
                    pageNumber = 14,
                    description = "4. Storing non-business related or unauthorized computer programs or files.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_4_RULE_5",
                    ruleNumber = "5",
                    sectionId = "SEC_4",
                    pageNumber = 14,
                    description = "5. Failure to observe reasonable diligence exposing the cooperative/client to unnecessary risk or expenses.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_4_RULE_6",
                    ruleNumber = "6",
                    sectionId = "SEC_4",
                    pageNumber = 15,
                    description = "6. Unauthorized or negligent handling of cooperative/company property leading to loss or damage.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_4_RULE_7",
                    ruleNumber = "7",
                    sectionId = "SEC_4",
                    pageNumber = 15,
                    description = "7. Eating any client's unpurchased outlet food item, including portions, byproducts, condiments, and garnishing; eating and/or obtaining leftovers.",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_4_RULE_8",
                    ruleNumber = "8",
                    sectionId = "SEC_4",
                    pageNumber = 15,
                    description = "8. Unauthorized use of Cooperative/client property (i.e., email, cellphone, instant messaging system etc.) to malign, or direct rude remarks to coworker/s or the cooperative/ client.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_4_RULE_9",
                    ruleNumber = "9",
                    sectionId = "SEC_4",
                    pageNumber = 15,
                    description = "9. Gross and habitual negligence leading to destruction, damage, or wastage of cooperative/client property or materials.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_4_RULE_10",
                    ruleNumber = "10",
                    sectionId = "SEC_4",
                    pageNumber = 15,
                    description = "10. Any form of theft within Cooperative/ client premises.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_4_RULE_11",
                    ruleNumber = "11",
                    sectionId = "SEC_4",
                    pageNumber = 15,
                    description = "11. Unauthorized use or keeping of cooperative/ client funds and/or property.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_4_RULE_12",
                    ruleNumber = "12",
                    sectionId = "SEC_4",
                    pageNumber = 15,
                    description = "12. Sabotage. Intentionally or knowingly damaging or attempting to damage cooperative/client property including equipment, furniture, utilities, and facilities.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_4_RULE_13",
                    ruleNumber = "13",
                    sectionId = "SEC_4",
                    pageNumber = 16,
                    description = "13. Obtaining, giving or passing out, or keeping any food item (including unserved food) or any cooperative/ client property without payment or proper authorization.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                )
            )
        ),

        // SECTION V
        RuleSection(
            id = "SEC_5",
            title = "V. ON HEALTH, SAFETY, AND SECURITY",
            description = "REFER TO ACTS/OMISSIONS THAT ENDANGER THE HEALTH, SAFETY, AND SECURITY OF CO-WORKERS AND THE COOPERATIVE OR CLIENT",
            rules = listOf(
                RuleItem(
                    id = "SEC_5_RULE_1",
                    ruleNumber = "1",
                    sectionId = "SEC_5",
                    pageNumber = 16,
                    description = "1. Entering restricted area, whether on duty or not, without proper authorization; allowing an unauthorized person to enter a restricted area.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_2",
                    ruleNumber = "2",
                    sectionId = "SEC_5",
                    pageNumber = 16,
                    description = "2. Failure to report chronic ailments, communicable diseases, or previous injuries that may be aggravated by existing working conditions.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_3",
                    ruleNumber = "3",
                    sectionId = "SEC_5",
                    pageNumber = 16,
                    description = "3. Engaging in horseplay or disorderly behavior within cooperative/ client premises, whether on duty or not, that endangers safety of service providers, causes damages or wastage, or considerable annoyance to customers or co-workers.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_4",
                    ruleNumber = "4",
                    sectionId = "SEC_5",
                    pageNumber = 17,
                    description = "4. Failure to comply with the cooperative scheduled annual medical examination, submission of worker's health permit or failure to adhere with any government regulatory standards.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_5",
                    ruleNumber = "5",
                    sectionId = "SEC_5",
                    pageNumber = 17,
                    description = "5. Violation of safety rules and regulations or causing another person to violate a safety rule or regulation, which does not endanger life, does not lead to serious injury, and/or loss to the cooperative/client except when the injury happens to oneself.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_6",
                    ruleNumber = "6",
                    sectionId = "SEC_5",
                    pageNumber = 17,
                    description = "6. Smoking when on field assignment while one is identifiable as a service provider of the cooperative.",
                    category = OffenseCategory.B,
                    firstOffense = "WRITTEN REPRIMAND",
                    secondOffense = "5 DAYS SUSPENSION",
                    thirdOffense = "15 DAYS SUSPENSION",
                    fourthOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_7",
                    ruleNumber = "7",
                    sectionId = "SEC_5",
                    pageNumber = 17,
                    description = "7. Refusal or failure to comply with or evasion of security rules such as searches etc.",
                    category = OffenseCategory.C,
                    firstOffense = "5 DAYS SUSPENSION",
                    secondOffense = "15 DAYS SUSPENSION",
                    thirdOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_8",
                    ruleNumber = "8",
                    sectionId = "SEC_5",
                    pageNumber = 17,
                    description = "8. Refusal to undergo drug test and any other similar medical exams or procedures as may be required by law or the cooperative to ensure safety in the work place.",
                    category = OffenseCategory.C,
                    firstOffense = "5 DAYS SUSPENSION",
                    secondOffense = "15 DAYS SUSPENSION",
                    thirdOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_9",
                    ruleNumber = "9",
                    sectionId = "SEC_5",
                    pageNumber = 18,
                    description = "9. Failure to report immediately a personal injury incurred on cooperative time or accident involving cooperative/client property or an service provider that resulted to further safety and operational risks.",
                    category = OffenseCategory.C,
                    firstOffense = "5 DAYS SUSPENSION",
                    secondOffense = "15 DAYS SUSPENSION",
                    thirdOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_10",
                    ruleNumber = "10",
                    sectionId = "SEC_5",
                    pageNumber = 18,
                    description = "10. Smoking in the work area or any unauthorized smoking area at any time.",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_11",
                    ruleNumber = "11",
                    sectionId = "SEC_5",
                    pageNumber = 18,
                    description = "11. Violation of safety rules and regulations or causing another person to violate a safety rule regulation which: endangers life or which leads to serious injury except to oneself and/or causes major loss(es) to the cooperative or client.",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_12",
                    ruleNumber = "12",
                    sectionId = "SEC_5",
                    pageNumber = 18,
                    description = "12. Unauthorized reproduction of cooperative/client records, books, plans, and other official documents.",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_13",
                    ruleNumber = "13",
                    sectionId = "SEC_5",
                    pageNumber = 19,
                    description = "13. Keeping, passing on or partaking of any alcoholic drinks within cooperative/ client premises, whether on duty or not, or when on field assignment or cooperative/client-sponsored activity; reporting for work after having taken such drinks.",
                    category = OffenseCategory.D,
                    firstOffense = "15 DAYS SUSPENSION",
                    secondOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_14",
                    ruleNumber = "14",
                    sectionId = "SEC_5",
                    pageNumber = 19,
                    description = "14. Working with or assisting any business competitor, whether with or without pay, while employed with the cooperative.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_15",
                    ruleNumber = "15",
                    sectionId = "SEC_5",
                    pageNumber = 19,
                    description = "15. Participation or involvement in any business transaction involving any person or business enterprise with which the cooperative has commercial transaction.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_16",
                    ruleNumber = "16",
                    sectionId = "SEC_5",
                    pageNumber = 19,
                    description = "16. Revealing or passing on to any person or agency cooperative/ client trade secrets or any information about its business transactions and finances at any time during one's employment.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_17",
                    ruleNumber = "17",
                    sectionId = "SEC_5",
                    pageNumber = 19,
                    description = "17. Failure to turn in cash receipts, or other collections and \"lost and found\" articles.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_18",
                    ruleNumber = "18",
                    sectionId = "SEC_5",
                    pageNumber = 20,
                    description = "18. Keeping, passing on or partaking of any prohibited drugs within cooperative/client premises, whether on duty or not, or when on field assignment, or cooperative/client-sponsored activity; reporting for work after having taken such drugs.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                ),
                RuleItem(
                    id = "SEC_5_RULE_19",
                    ruleNumber = "19",
                    sectionId = "SEC_5",
                    pageNumber = 20,
                    description = "19. Keeping/bringing in any deadly weapon within cooperative/ client premises.",
                    category = OffenseCategory.E,
                    firstOffense = "TERMINATION"
                )
            )
        )
    )

    private val sampleReports = listOf(
        IncidentReport(
            id = "REP-2025-001",
            title = "Customer Argument at Front Desk",
            offenderName = "John Doe (Customer Representative)",
            offenderNames = listOf("John Doe (Customer Representative)"),
            incidentSource = "Store Complaint",
            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            incidentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            incidentTime = "09:30 AM",
            ruleId = "SEC_1_RULE_1",
            ruleDescription = "1. Any unacceptable behavior toward or involving the client's customer including but not limited to arguing, debating, or fighting with the client's customer for any reason.",
            category = OffenseCategory.E,
            offenseCount = "1st Offense",
            recommendedPenalty = "TERMINATION",
            details = "Employee engaged in a verbal altercation with a client customer regarding service delays.",
            status = ReportStatus.UNDER_INVESTIGATION
        )
    )

    private val sampleEmployees = listOf(
        Employee(id = "EMP-001", name = "John Doe", position = "Customer Representative"),
        Employee(id = "EMP-002", name = "Jane Miller", position = "Shift Lead")
    )

    private val _reportsFlow = MutableStateFlow<List<IncidentReport>>(sampleReports)
    val reportsFlow: StateFlow<List<IncidentReport>> = _reportsFlow.asStateFlow()

    private val _employeesFlow = MutableStateFlow<List<Employee>>(sampleEmployees)
    val employeesFlow: StateFlow<List<Employee>> = _employeesFlow.asStateFlow()

    private var appContext: android.content.Context? = null

    fun setAllData(reports: List<IncidentReport>, employees: List<Employee>) {
        _reportsFlow.value = reports
        if (employees.isNotEmpty()) {
            _employeesFlow.value = employees
        }
    }

    fun initPersistence(context: android.content.Context) {
        appContext = context.applicationContext
    }

    fun saveDataToDisk(context: android.content.Context? = null) {
        val targetContext = context ?: appContext
    }

    var onReportSavedListener: ((IncidentReport) -> Unit)? = null
    var onReportDeletedListener: ((String) -> Unit)? = null

    fun addReport(report: IncidentReport, context: android.content.Context? = null) {
        _reportsFlow.value = listOf(report) + _reportsFlow.value
        saveDataToDisk(context)
        onReportSavedListener?.invoke(report)
    }

    fun updateReport(updatedReport: IncidentReport, context: android.content.Context? = null) {
        _reportsFlow.value = _reportsFlow.value.map { report ->
            if (report.id == updatedReport.id) updatedReport else report
        }
        saveDataToDisk(context)
        onReportSavedListener?.invoke(updatedReport)
    }

    fun updateReportStatus(reportId: String, newStatus: ReportStatus, context: android.content.Context? = null) {
        _reportsFlow.value = _reportsFlow.value.map { report ->
            if (report.id == reportId) {
                val updated = report.copy(status = newStatus)
                onReportSavedListener?.invoke(updated)
                updated
            } else report
        }
        saveDataToDisk(context)
    }

    fun deleteReport(reportId: String, context: android.content.Context? = null) {
        _reportsFlow.value = _reportsFlow.value.filter { it.id != reportId }
        saveDataToDisk(context)
        onReportDeletedListener?.invoke(reportId)
    }

    fun getAllRules(): List<RuleItem> {
        return ruleSections.flatMap { it.rules }
    }

    fun getPenaltyForOffense(rule: RuleItem?, offenseCount: String): String {
        if (rule == null || offenseCount == "None") return "None"
        return when (offenseCount) {
            "1st Offense" -> rule.firstOffense
            "2nd Offense" -> rule.secondOffense
            "3rd Offense" -> rule.thirdOffense
            "4th Offense" -> rule.fourthOffense
            "5th Offense" -> rule.fifthOffense
            else -> rule.firstOffense
        }
    }

    fun addEmployee(employee: Employee, context: android.content.Context? = null) {
        _employeesFlow.value = listOf(employee) + _employeesFlow.value
    }

    fun updateEmployee(updatedEmployee: Employee, context: android.content.Context? = null) {
        _employeesFlow.value = _employeesFlow.value.map { emp ->
            if (emp.id == updatedEmployee.id) updatedEmployee else emp
        }
    }

    fun deleteEmployee(employeeId: String, context: android.content.Context? = null) {
        _employeesFlow.value = _employeesFlow.value.filter { it.id != employeeId }
    }
}
