package ru.rarus.datacollectionterminal.db.entities

import androidx.room.*
import java.util.*

@Entity(tableName = "good")
class Good() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var name: String = "Неизвестный товар"

    constructor(barcode: String) : this() {
        name = "Товар $barcode"
    }

    constructor(id: String, name: String) : this() {
        this.id = id
        this.name = name
    }
}

@Entity(
    tableName = "unit", foreignKeys = [ForeignKey(
        entity = Good::class,
        parentColumns = ["id"],
        childColumns = ["good"],
        onDelete = ForeignKey.CASCADE
    )]
)
class Unit() {
    @PrimaryKey
    var barcode: String = ""

    @ColumnInfo(index = true)
    var good: String = ""
    var name: String = "шт"
    var conversionFactor: Double = 1.0
    var price: Double = 0.0
    var baseUnit: Boolean = false

    constructor(barcode: String, good: Good, baseUnit: Boolean) : this() {
        this.barcode = barcode
        this.good = good.id
        this.baseUnit = baseUnit
    }

    constructor(barcode: String, name: String, price: Double, good: String) : this() {
        this.barcode = barcode
        this.name = name
        this.price = price
        this.good = good
        this.baseUnit = true
    }
}

/* additional classes */

class GoodAndUnit() {
    @Embedded
    var good: Good = Good()

    @Relation(
        parentColumn = "id",
        entityColumn = "good",
        entity = Unit::class
    )
    var unit: Unit = Unit()

    constructor(barcode: String) : this() {
        this.good = Good(barcode)
        this.unit = Unit(barcode, good, true)
    }

    constructor(good: Good, unit: Unit) : this() {
        this.good = good
        this.unit = unit
    }
}

class ViewGood() {
    var good: Good = Good()
    var units: MutableList<Unit> = ArrayList()
    var saved: Boolean = false

    constructor(good: Good, units: List<Unit>) : this() {
        this.good = good
        this.units = units.toMutableList()
    }

    constructor(good: Good, units: List<Unit>, saved: Boolean) : this(good, units) {
        this.saved = saved
    }
}