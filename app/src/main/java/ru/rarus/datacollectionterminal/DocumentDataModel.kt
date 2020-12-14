package ru.rarus.datacollectionterminal

import java.util.*

class ViewDocumentRow() {
    var goodName = ""
    var unitName = ""
    var unitBarcode = ""
    var addBarcode = ""
    var quantityDoc = 0.0
    var quantityActual = 0.0
    var difference = 0.0

    constructor(goodAndUnit: GoodAndUnit) : this() {
        goodName = goodAndUnit.good.name
        unitName = goodAndUnit.unit.name
        unitBarcode = goodAndUnit.unit.barcode
        quantityActual = 1.0
    }
}

class DctFocument() {
    val header = DctDocumentHeader()
    val rows: MutableList<ViewDocumentRow> =  ArrayList()
}