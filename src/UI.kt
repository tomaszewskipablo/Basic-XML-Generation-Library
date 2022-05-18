import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.CompoundBorder

class ComponentSkeleton(val text: String) : JPanel() {

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.font = Font("Arial", Font.BOLD, 16)
        g.drawString(text, 10, 20)
    }

    init {
        layout = GridLayout(0, 1)
        border = CompoundBorder(
            BorderFactory.createEmptyBorder(30, 10, 10, 10),
            BorderFactory.createLineBorder(Color.BLACK, 2, true)
        )
        createPopupMenu()
    }

    private fun createPopupMenu() {
        val popupmenu = JPopupMenu("Actions")
        val a = JMenuItem("Add child")
        a.addActionListener {
            val text = JOptionPane.showInputDialog("text")
            add(ComponentSkeleton(text))
            revalidate()
        }
        popupmenu.add(a)

        val b = JMenuItem("Add child")
        b.addActionListener {
            val text = JOptionPane.showInputDialog("text")
            add(JLabel(text))
            revalidate()
        }
        popupmenu.add(b)


        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e))
                    popupmenu.show(this@ComponentSkeleton, e.x, e.y)
            }
        })
    }
}

class WindowSkeleton : JFrame("title") {
    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(300, 300)

        add(ComponentSkeleton("root"))
    }

    fun open() {
        isVisible = true
    }
}

fun main() {

    val w = WindowSkeleton()
    val s = ComponentSkeleton("dfassa")
    val s2 = ComponentSkeleton("Element")
    w.add(s)
    s.add(s2)
/*    var t1 = JTextField()
    t1.setBounds(50,100, 200,30)
    w.add(t1)
    var b1 = JButton("+");
    b1.setBounds(50,200,50,50)
    w.add(b1)
    var d = Canvas()
    w.add(d)*/
    w.open()
}