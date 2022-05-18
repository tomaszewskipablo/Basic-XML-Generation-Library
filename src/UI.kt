import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.CompoundBorder

class ComponentSkeleton(val text: String,val entity: Entity? = null) : JPanel() {
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
            add(ComponentSkeleton(text, Entity(text,entity)))
            revalidate()
        }
        popupmenu.add(a)

        val b = JMenuItem("Add child")
        b.addActionListener {
            val text = JOptionPane.showInputDialog("text")
            add(JLabel(text))
            EntityConcrete(text, text, entity)
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

class WindowSkeleton(var root: Entity) : JFrame("title") {

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(300, 300)

        var serializeButton = JButton("Serialize")
        serializeButton.setBounds(0,230,100,20)
        serializeButton.addActionListener {
            println(root.serialization()) }
        add(serializeButton)
        var loadButton = JButton("Load")
        loadButton.setBounds(100,230,100,20)


        loadButton.addActionListener {
            println("root.load()") }
        add(loadButton)
        add(ComponentSkeleton("root", root))
        }

    fun open() {
        isVisible = true
    }
}

fun main() {
    var xml = Xml("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>")
    var root = Entity("root",null)
    val w = WindowSkeleton(root)

    val b = Book("sds","sda")
    val s1 = Student(7, b,"Cristiano", "Ronaldo", StudentType.Doctoral)

    //xml.createXMLObject(s1, null)
    w.open()
}