package ru.rarus.datacollectionterminal

import java.util.*

class DctFocument() {
    val header = DctDocumentHeader()
    val rows: MutableList<DctDocumentRow> =  ArrayList()
}