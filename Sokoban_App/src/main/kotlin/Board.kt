package org.example

import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.ImageIcon
import javax.swing.JPanel
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

class Board(
    private val model: GameModel,
    private val iconSize: Int = 64,
    private val onAction: () -> Unit
) : JPanel() {

    private val assetPath = "Assets/PNG/Default size/"
    private val images = mutableMapOf<Tile, Image>()

    init {
        loadImages()
        val height = model.grid.size * iconSize
        val width = if (model.grid.isNotEmpty()) model.grid[0].size * iconSize else 0
        preferredSize = Dimension(width, height)
        isFocusable = true
        
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                println("Key pressed: ${KeyEvent.getKeyText(e.keyCode)}")
                val moved = when (e.keyCode) {
                    KeyEvent.VK_UP -> model.move(-1, 0)
                    KeyEvent.VK_DOWN -> model.move(1, 0)
                    KeyEvent.VK_LEFT -> model.move(0, -1)
                    KeyEvent.VK_RIGHT -> model.move(0, 1)
                    KeyEvent.VK_R -> {
                        println("Resetting level...")
                        model.reset()
                        true
                    }
                    else -> false
                }
                
                if (moved) {
                    println("Move successful! New position: ${model.playerRow}, ${model.playerCol}")
                    repaint()
                    onAction()
                    if (model.isWin()) {
                        JOptionPane.showMessageDialog(this@Board, "You Won!")
                    }
                } else {
                    println("Move failed or ignored.")
                }
            }
        })
        
        // Ensure the board gets focus to capture keys
        SwingUtilities.invokeLater {
            requestFocusInWindow()
        }
    }

    private fun loadImages() {
        val mapping = mapOf(
            Tile.WALL to "Blocks/block_06.png",
            Tile.FLOOR to "Ground/ground_05.png",
            Tile.GOAL to "Environment/environment_02.png",
            Tile.BOX to "Crates/crate_07.png",
            Tile.BOX_ON_GOAL to "Crates/crate_27.png",
            Tile.PLAYER to "Player/player_05.png",
            Tile.PLAYER_ON_GOAL to "Player/player_05.png"
        )

        mapping.forEach { (tile, path) ->
            val fullPath = assetPath + path
            val icon = ImageIcon(fullPath)
            if (icon.imageLoadStatus == java.awt.MediaTracker.ERRORED) {
                println("Failed to load image: $fullPath")
            }
            images[tile] = icon.image
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        
        // Draw floors everywhere first for a nice background
        val floorImg = images[Tile.FLOOR]
        for (r in model.grid.indices) {
            for (c in model.grid[r].indices) {
                g.drawImage(floorImg, c * iconSize, r * iconSize, iconSize, iconSize, this)
            }
        }

        // Draw specific tiles
        for (r in model.grid.indices) {
            for (c in model.grid[r].indices) {
                val tile = model.grid[r][c]
                if (tile == Tile.FLOOR) continue // Already drawn

                // For goals, we draw the goal then the player if they are on it
                if (tile == Tile.PLAYER_ON_GOAL) {
                    g.drawImage(images[Tile.GOAL], c * iconSize, r * iconSize, iconSize, iconSize, this)
                    g.drawImage(images[Tile.PLAYER], c * iconSize, r * iconSize, iconSize, iconSize, this)
                } else {
                    g.drawImage(images[tile], c * iconSize, r * iconSize, iconSize, iconSize, this)
                }
            }
        }
    }

    fun updateBoardSize() {
        val height = model.grid.size * iconSize
        val width = if (model.grid.isNotEmpty()) model.grid[0].size * iconSize else 0
        preferredSize = Dimension(width, height)
        revalidate()
    }
}
