package org.example

enum class Tile(val symbol: Char) {
    WALL('#'),
    FLOOR(' '),
    GOAL('.'),
    BOX('$'),
    BOX_ON_GOAL('*'),
    PLAYER('@'),
    PLAYER_ON_GOAL('+');

    companion object {
        fun fromSymbol(symbol: Char): Tile = values().find { it.symbol == symbol } ?: FLOOR
    }
}

class GameModel {
    var grid: Array<Array<Tile>> = emptyArray()
    var energy: Int = 10
    var playerRow: Int = 0
    var playerCol: Int = 0
    private var originalLevel: String = ""

    fun loadLevel(levelData: String) {
        originalLevel = levelData
        val lines = levelData.trimIndent().lines()
        val height = lines.size
        val width = lines.maxOf { it.length }
        
        grid = Array(height) { r ->
            Array(width) { c ->
                val symbol = if (c < lines[r].length) lines[r][c] else ' '
                val tile = Tile.fromSymbol(symbol)
                if (tile == Tile.PLAYER || tile == Tile.PLAYER_ON_GOAL) {
                    playerRow = r
                    playerCol = c
                }
                tile
            }
        }
    }

    fun reset() {
        if (originalLevel.isNotEmpty()) {
            loadLevel(originalLevel)
        }
    }

    fun move(dr: Int, dc: Int): Boolean {
        val nextR = playerRow + dr
        val nextC = playerCol + dc

        if (!isValid(nextR, nextC)) return false

        val targetTile = grid[nextR][nextC]

        // Case 1: Walk into floor or goal
        if (targetTile == Tile.FLOOR || targetTile == Tile.GOAL) {
            updatePlayerPos(nextR, nextC)
            energy = energy - 1
            return true
        }

        // Case 2: Push box
        if (targetTile == Tile.BOX || targetTile == Tile.BOX_ON_GOAL) {
            val boxNextR = nextR + dr
            val boxNextC = nextC + dc

            if (isValid(boxNextR, boxNextC)) {
                val boxTargetTile = grid[boxNextR][boxNextC]
                if (boxTargetTile == Tile.FLOOR || boxTargetTile == Tile.GOAL) {
                    // Move box
                    grid[boxNextR][boxNextC] = if (boxTargetTile == Tile.GOAL) Tile.BOX_ON_GOAL else Tile.BOX
                    // Move player
                    updatePlayerPos(nextR, nextC)
                    energy = energy - 1
                    return true
                }
            }
        }

        return false
    }

    private fun updatePlayerPos(newR: Int, newC: Int) {
        // Leave previous tile
        grid[playerRow][playerCol] = if (grid[playerRow][playerCol] == Tile.PLAYER_ON_GOAL) Tile.GOAL else Tile.FLOOR
        
        // Enter new tile
        playerRow = newR
        playerCol = newC
        grid[playerRow][playerCol] = if (grid[playerRow][playerCol] == Tile.GOAL) Tile.PLAYER_ON_GOAL else Tile.PLAYER
    }

    private fun isValid(r: Int, c: Int): Boolean = r in grid.indices && c in grid[0].indices

    fun isWin(): Boolean {
        return grid.none { row -> row.any { it == Tile.BOX || it == Tile.GOAL } }
    }
}
