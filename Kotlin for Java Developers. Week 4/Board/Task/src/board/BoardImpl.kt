package board

import board.Direction.*

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(width)

/*
* has to be open because GameBoardImpl extend this class
*
* https://kotlinlang.org/docs/reference/classes.html#inheritance
* > By default, Kotlin classes are final: they can’t be inherited.
* > To make a class inheritable, mark it with the open keyword.
* */
open class SquareBoardImpl(override val width: Int) : SquareBoard { //Class is open for inheritance

    open var cells = create(width) //listOf<Cell>()

    companion object {
        fun create(width: Int): List<Cell> {
            return (1..width).flatMap { i: Int ->
                (1..width).map { j: Int ->
                    Cell(i, j)
                }
            }
        }
    }

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        if (isValidIndex(i) && isValidIndex(j)) {
            return getItem(i, j)
        } else {
            return null
        }
    }

    override fun getCell(i: Int, j: Int): Cell {
        if (isValidIndex(i) && isValidIndex(j)) {
            return getItem(i, j)
        } else {
            throw IllegalArgumentException("Incorrect index")
        }
    }

    override fun getAllCells(): Collection<Cell> {
        return cells
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        if (isValidIndex(i)) {
            return jRange.toList().filter { isValidIndex(it) }.map { j -> getItem(i, j) }
        } else {
            return listOf()
        }
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        if (isValidIndex(j)) {
            return iRange.toList().filter { isValidIndex(it) }.map { i -> getItem(i, j) }
        } else {
            return listOf()
        }
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        return when (direction) {
            UP -> getCellOrNull(i - 1, j)
            DOWN -> getCellOrNull(i + 1, j)
            LEFT -> getCellOrNull(i, j - 1)
            RIGHT -> getCellOrNull(i, j + 1)
        }
    }

    private fun isValidIndex(i: Int): Boolean = i in 1..width

    private fun getItem(i: Int, j: Int): Cell = cells[(i - 1) * width + j - 1]
}

class GameBoardImpl<T>(override val width: Int) : GameBoard<T>, SquareBoardImpl(width) {
    var cellValues: MutableMap<Cell, T?> = mutableMapOf()
    override var cells = create(width)

    init {
        cells.forEach {
            cellValues[it] = null
        }
    }

    override fun get(cell: Cell): T? {
        return cellValues[cell]
    }

    override fun set(cell: Cell, value: T?) {
        cellValues[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        return cellValues.filter { predicate(it.value) }.keys
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
        return cellValues.filter { predicate(it.value) }.keys.first()
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        return cellValues.values.any(predicate)
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        return cellValues.values.all(predicate)
    }
}
