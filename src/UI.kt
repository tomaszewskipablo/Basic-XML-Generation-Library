import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.CompoundBorder
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf

class ComponentSkeleton(var text: String,var entity: Entity? = null) : JPanel() {
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

    fun removeChild(componentToBeRemoved: ComponentSkeleton){
        this.remove(componentToBeRemoved)
        repaint()
    }

    private fun createPopupMenu() {
        val popupmenu = JPopupMenu("Actions")
        val a = JMenuItem("Add Tag")
        a.addActionListener {
            val text = JOptionPane.showInputDialog("Tag name")
            add(ComponentSkeleton(text, Entity(text,entity)))
            revalidate()
        }
        popupmenu.add(a)

        val b = JMenuItem("Add attribute")
        b.addActionListener {
            val text = JOptionPane.showInputDialog("attribute name")
            add(JLabel(text))
            EntityConcrete(text, text, entity)
            revalidate()
        }
        popupmenu.add(b)

        val c = JMenuItem("Rename")
        c.addActionListener {
            val text = JOptionPane.showInputDialog("Rename")
            this.text = text
            this.entity!!.name = text
            repaint()
        }
        popupmenu.add(c)

        val d = JMenuItem("delete")
        d.addActionListener {
            if(JOptionPane.showConfirmDialog(null,"Are you sure?") == 0) {
                    if(this.entity!!.parent == null){ //ROOT TO be removed
                        val c = this.parent.parent.parent as JScrollPane
                        c.viewport.remove(this)
                    }
                else {
                        this.entity!!.parent!!.children.remove(this.entity)
                        val c = this.parent as ComponentSkeleton
                        c.removeChild(this)
                    }
            }
        }
        popupmenu.add(d)

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e))
                    popupmenu.show(this@ComponentSkeleton, e.x, e.y)
            }
        })
    }
}

class WindowSkeleton(var root: Entity?=null) : JFrame("title") {
    var componentSkeleton = ComponentSkeleton("")
    var jScrollPane = JScrollPane()
    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(700, 1000)
        layout = BorderLayout()
        add(jScrollPane)

        var serializeButton = JButton("Serialize")
        serializeButton.setBounds(0,230,50,20)
        serializeButton.addActionListener {
            println(root!!.serialization()) }

        var loadButton = JButton("Load")
        loadButton.addActionListener {
            val b = Book("sds","sda")
            val s1 = Student(7, b,"Cristiano", "Ronaldo", StudentType.Doctoral)
            createXMLObject(s1)
        }

        var createRootElementButton = JButton("Create new root")
        createRootElementButton.addActionListener {
            val text = JOptionPane.showInputDialog("text")
            root = Entity(text, null)
            componentSkeleton = ComponentSkeleton(text,root)
            jScrollPane.viewport.add(componentSkeleton)
            repaint()
        }
        add(createRootElementButton, BorderLayout.WEST)
        add(loadButton, BorderLayout.SOUTH)
        add(serializeButton, BorderLayout.NORTH)

        }

    fun open() {
        isVisible = true
    }

    fun createXMLObject(o: Any, parentComponentSkeleton: ComponentSkeleton?=null){

        val obj = o::class
        if(parentComponentSkeleton == null) {
            root = Entity(tableName(obj).toString(), null)
            componentSkeleton = ComponentSkeleton(tableName(obj).toString(),root)
            jScrollPane.viewport.add(componentSkeleton)
            repaint()
            createXMLObject(o, componentSkeleton)
        }
        else {
            obj.declaredMemberProperties.forEach {
                if (!Ignore(it)) {
                    if (it.returnType.classifier.isCollection()) // is dataClass or List
                    {
                        if(innerText(it,o)) {
                            var s = it.name
                            val e = Entity(it.name, parentComponentSkeleton!!.entity)
                            add(ComponentSkeleton(it.name, e))
                            val coll = it.call(o) as Collection<*>
                            coll.forEach {
                                if (it != null){
                                    parentComponentSkeleton.add(JLabel(it.toString()))
                                    revalidate()
                                    EntityConcrete(s, it.toString(), e)
                                }
                            }
                        }
                        else{
                            val coll = it.call(o) as Collection<*>
                            //parent!!.attributes[it.name] = coll.toString()
                        }
                    } else if (it.returnType.classifier.isEnum())    // Enum
                    {
                        if(innerText(it,o)) {
                            //parent!!.attributes[fieldName(it)] = it.call(o).toString()
                        }
                        else{
                            parentComponentSkeleton.add(JLabel(fieldName(it)))
                            revalidate()
                            EntityConcrete(fieldName(it), it.call(o).toString(), parentComponentSkeleton.entity)
                        }
                    }
                    else if(it.call(o)!!::class.isData)
                    {
                        var e = Entity(it.name,parentComponentSkeleton.entity)
                        parentComponentSkeleton!!.add(ComponentSkeleton(it.name, e))
                        revalidate()
                        createXMLObject(it.call(o)!!::class.javaObjectType.cast(it.call(o)), parentComponentSkeleton)
                    }
                    else    // Primitive type
                    {
                        if(innerText(it,o)) {
                            //parent!!.attributes[fieldName(it)] = it.call(o).toString()
                        }
                        else{
                            EntityConcrete(fieldName(it), it.call(o).toString(), parentComponentSkeleton.entity)
                            parentComponentSkeleton.add(JLabel(fieldName(it)))
                            revalidate()
                        }
                    }
                }
            }
        }
    }

    private fun tableName(c: KClass<*>) =
        if(c.hasAnnotation<XmlName>()) c.findAnnotation<XmlName>()!!.text
        else c.simpleName

    private fun fieldName(c: KProperty<*>) =
        if(c.hasAnnotation<XmlName>()) c.findAnnotation<XmlName>()!!.text
        else c.name

    private fun innerText(c: KProperty<*>, o:Any) =
        if(c.hasAnnotation<XmlTagContent>()) true
        else false

    private fun Ignore(c: KProperty<*>) =
        c.hasAnnotation<XmlIgnore>()


    fun KClassifier?.isEnum() = this is KClass<*> && this.isSubclassOf(Enum::class)
    fun KClassifier?.isCollection() = this is KClass<*> && this.isSubclassOf(Collection::class)
}



fun main() {
    var xml = Xml("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>")
    //var root = Entity("rootsad",null)
    val w = WindowSkeleton()

    val b = Book("title","JK ROwling")
    val s1 = Student(7, b,"Cristiano", "Ronaldo", StudentType.Doctoral)
    //w.createXMLObject(s1, null)
    w.open()
}