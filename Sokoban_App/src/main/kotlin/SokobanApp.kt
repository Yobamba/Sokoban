package org.example

import javax.swing.JFrame
import javax.swing.SwingUtilities

class SokobanApp : JFrame() {
    private val model = GameModel()
    private val board: Board

    init {
        title = "Sokoban Kotlin"
        defaultCloseOperation = EXIT_ON_CLOSE

        // Load a sample level
        val level1 = """
            #######
            #  .  #
            #  $  #
            # @   #
            #######
        """.trimIndent()
        
        // A slightly more complex level
        val level2 = """
            #######
            #     #
            # $.  #
            # @   #
            #######
        """.trimIndent()

        // Classic Level 1
        val classicLevel1 = """
              #####
              #   #
              #$  #
            ###  $##
            #  $ $.#
            ### #@##
              #    #
              ######
        """.trimIndent()

        model.loadLevel(classicLevel1)

        board = Board(model) {
            // Callback after each move
        }

        add(board)
        pack()
        setLocationRelativeTo(null)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater {
                SokobanApp().isVisible = true
            }
        }
    }
}
