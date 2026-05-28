import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.ImageIcon
import javax.swing.JPanel

enum class ArrowKey {
    UP, DOWN, LEFT, RIGHT
}

class Board(val lines: Int, val columns: Int,
            val iconSize: Int = 48,
            val keyObserver: (ArrowKey) -> Unit = { }
) : JPanel() {
    private val board: Array<Array<Image?>> =
        Array(lines) { Array(columns) { null } }

    init {
        val width = columns * iconSize
        val height = lines * iconSize
        preferredSize = Dimension(width, height)
        setFocusable(true)
        requestFocusInWindow()
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when(e.keyCode) {
                    KeyEvent.VK_UP -> keyObserver(ArrowKey.UP)
                    KeyEvent.VK_DOWN -> keyObserver(ArrowKey.DOWN)
                    KeyEvent.VK_LEFT -> keyObserver(ArrowKey.LEFT)
                    KeyEvent.VK_RIGHT -> keyObserver(ArrowKey.RIGHT)
                }
            }
        })
    }

    fun set(line: Int, column: Int, iconPath: String?) {
        if (line !in 0..<lines || column !in 0..<columns)
            throw RuntimeException("invalid position: $line, $column")
        board[line][column] = iconPath?.let { ImageIcon(iconPath).image }
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        for (l in board.indices) {
            for (c in board[l].indices) {
                g.drawImage(
                    board[l][c],
                    c * iconSize,
                    l * iconSize,
                    iconSize,
                    iconSize,
                    this
                )
            }
        }
    }
}