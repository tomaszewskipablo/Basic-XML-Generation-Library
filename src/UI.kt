import com.sun.xml.internal.ws.api.ha.StickyFeature
import jdk.jfr.EventType
import java.awt.*
import java.awt.SystemColor.text
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
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

// any event UI can do
interface GUIEvent{
    fun renameEntity(entity: Entity, newName:String)
    fun addEntity(newEntityName:String, parentEntity: Entity)
    fun deleteEntity(entity: Entity, removeEntity:String)
    fun addAttribute(entity: Entity, newEntityName:String)
    fun removeAttribute(entity: Entity, removeAttribute: String)
    fun renameAttribute(entity: Entity, name: String, nameNew:String)
    fun changeAttributeText(entity: Entity, name:String, nameNew:String)
    fun addSection(entity: Entity, sectionName:String)
    fun removeSection(entity: Entity, sectionName:String)
    fun renameSection(entity: Entity, name:String, newName: String)
    fun changeSectionText(entity: Entity, name:String, insideText:String)
}




class ComponentSkeleton(var entity: Entity, val controller: Controller) : JPanel(), IObservable<GUIEvent> {
    override val observers: MutableList<GUIEvent> = mutableListOf<GUIEvent>()
    var nameEntity = entity.name

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.font = Font("Arial", Font.BOLD, 16)
        g.drawString(nameEntity, 10, 20)
    }

    init {
        layout = GridLayout(0, 1)
        border = CompoundBorder(
            BorderFactory.createEmptyBorder(30, 10, 10, 10),
            BorderFactory.createLineBorder(Color.BLACK, 2, true)
        )
        createPopupMenu()


        entity.addObserver {Event, value, name, entity -> handleThisEvent(Event, value, name, entity) }
        addObserver(controller)
    }

    // Update View
    fun handleThisEvent(typeEvent: TypeEvent, name: String?, value: String?, child: Entity?){
        if(typeEvent == TypeEvent.RenameEntity) {
            nameEntity = value!!
        }
        else if(typeEvent == TypeEvent.AddEntity) {
            add(ComponentSkeleton(child!!,controller))
        }

        else if(typeEvent == TypeEvent.RemoveEntity) {
            val toBeRemoved = components.find { it is ComponentSkeleton && name == it.nameEntity}
            if(toBeRemoved != null)
                remove(toBeRemoved as ComponentSkeleton)
        }
        else if(typeEvent == TypeEvent.AddAttribute) {
            add(AttributeComponent(name!!,value!!))
        }
        else if(typeEvent == TypeEvent.RemoveAttribute) {
            val toBeRemoved = components.find { it is AttributeComponent && name == it.nameAttribute }
            if(toBeRemoved != null)
                remove(toBeRemoved as AttributeComponent)
            //s.jText.text = value
        }
        else if(typeEvent == TypeEvent.AddSection) {
            add(ConcreteEntityComponent(name!!,value!!))
        }
        else if(typeEvent == TypeEvent.RemoveSection) { // TODO
            val toBeRemoved = components.find { it is ConcreteEntityComponent && name == it.text }
            if(toBeRemoved != null)
                remove(toBeRemoved as ConcreteEntityComponent)
            //s.jText.text = value
        }
        else if(typeEvent == TypeEvent.RenameAttribute) {
            val element = components.find { it is AttributeComponent && name == it.nameAttribute }
            if(element != null) {
                val attributeElement = element as AttributeComponent
                attributeElement.nameAttribute = value!!
                attributeElement.jLabel.text = value
            }
        }
        else if(typeEvent == TypeEvent.RenameSection) {
            val element = components.find { it is ConcreteEntityComponent && name == it.text }
            if(element != null) {
                val attributeElement = element as ConcreteEntityComponent
                attributeElement.text = value!!
                attributeElement.jLabel.text = value
            }
        }
        revalidate()
        repaint()
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


            notifyObservers{
                it.addEntity(text, entity)
            }
            revalidate()
        }
        popupmenu.add(a)

        val c = JMenuItem("Rename Tag")
        c.addActionListener {
            val text = JOptionPane.showInputDialog("Rename")
            // it works, but we just update model, which is not good
            //entity.name = text

            // notify controler, not model!!!
            notifyObservers{
                it.renameEntity(entity,text)
            }
        }
        popupmenu.add(c)

        val deleteEntityButton = JMenuItem("Remove Tag")
        deleteEntityButton.addActionListener {
            val text = JOptionPane.showInputDialog("entity name to be removed")
            notifyObservers{
                it.deleteEntity(entity,text)
            }
        }
        popupmenu.add(deleteEntityButton)

        val b = JMenuItem("Add attribute")
        b.addActionListener {
            val text = JOptionPane.showInputDialog("attribute name")
            notifyObservers{
                it.addAttribute(entity,text)
            }
            revalidate()
        }
        popupmenu.add(b)

        val r = JMenuItem("Remove attribute")
        r.addActionListener {
            val text = JOptionPane.showInputDialog("attribute name to be removed")
            notifyObservers{
                it.removeAttribute(entity,text)
            }
        }
        popupmenu.add(r)

        val renameAttributeButton = JMenuItem("Rename attribute")
        renameAttributeButton.addActionListener {
            val attributeName = JOptionPane.showInputDialog("Which attribute should be renamed?")
            if(entity.attributes[attributeName] != null) {
                val newName = JOptionPane.showInputDialog("New name")
                notifyObservers {
                    it.renameAttribute(entity, attributeName, newName)
                }
            }
        }
        popupmenu.add(renameAttributeButton)


        val en = JMenuItem("Add section")
        en.addActionListener {
            val text = JOptionPane.showInputDialog("Section name")
            notifyObservers{
                it.addSection(entity,text)
            }
            revalidate()
        }
        popupmenu.add(en)

        val renameSectionButton = JMenuItem("Rename section")
        renameSectionButton.addActionListener {
            val sectionName = JOptionPane.showInputDialog("Which section should be renamed?")
            if(entity.children.find{it.name == sectionName} != null) {
                val newName = JOptionPane.showInputDialog("New name")
                notifyObservers {
                    it.renameSection(entity, sectionName, newName)
                }
                notifyObservers{
                    it.renameSection(entity, sectionName, newName)
                }
            }

        }
        popupmenu.add(renameSectionButton)

        val removeSectionButton = JMenuItem("Remove section")
        removeSectionButton.addActionListener {
            val text = JOptionPane.showInputDialog("Section name")
            notifyObservers{
                it.removeSection(entity,text)
            }
            revalidate()
        }
        popupmenu.add(removeSectionButton)

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e))
                    popupmenu.show(this@ComponentSkeleton, e.x, e.y)
            }
        })
    }

    inner class AttributeComponent(var nameAttribute: String, var insideTextField:String = "") : JPanel() {
        val jText = JTextField(insideTextField)
        val jLabel = JLabel(nameAttribute)

        init {
            layout = GridLayout(1, 2)

            setMaximumSize(Dimension(50, 15))
            add(jLabel)
            jText.addKeyListener(object: KeyListener{
                override fun keyTyped(e: KeyEvent) {  }
                override fun keyPressed(e: KeyEvent?) {
                }

                override fun keyReleased(e: KeyEvent?) {
                    notifyObservers{
                        it.changeAttributeText(entity,nameAttribute,jText.text)
                    }
                }
            })
            add(jText)
        }
    }

    inner class ConcreteEntityComponent(var text: String,var insideTextField:String = "") : JPanel() {
        val jLabel = JLabel(text)
        val jTextField = JTextField(insideTextField)
        init {
            layout = BorderLayout()

            setMaximumSize(Dimension(50, 15) )
            name = text
            add(jLabel,BorderLayout.NORTH)
            jTextField.addKeyListener(object: KeyListener{
                override fun keyTyped(e: KeyEvent) {  }
                override fun keyPressed(e: KeyEvent?) {
                }

                override fun keyReleased(e: KeyEvent?) {
                    notifyObservers{
                        it.changeSectionText(entity,name,jTextField.text)
                    }
                }
            })
            add(jTextField,BorderLayout.CENTER)
        }
    }
}

class WindowSkeleton(var root: Entity?=null) : JFrame("title") {
    lateinit var componentSkeleton: ComponentSkeleton

    var jScrollPane = JScrollPane()

    //val undoStack = UndoStack()
    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(700, 1000)
        layout = BorderLayout()
        jScrollPane.viewport.add(ComponentSkeleton(root!!, Controller()))
        add(jScrollPane)

        var serializeButton = JButton("Serialize")
        serializeButton.setBounds(0, 230, 50, 20)
        serializeButton.addActionListener {
            println(root!!.serialization())
        }

        var loadButton = JButton("Load")
        loadButton.addActionListener {
            val b = Book("ToPOWINNOBYC", "WSORKU BOOK")
            val s1 = Student(7, b, "Cristiano", "Ronaldo", StudentType.Doctoral)
            //createXMLObject(s1)
        }

        var createRootElementButton = JButton("Create new root")
        createRootElementButton.addActionListener {
            val text = JOptionPane.showInputDialog("text")
            root = Entity(text, null)
            //componentSkeleton = ComponentSkeleton(root!!,)
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

    /*fun createXMLObject(o: Any, parentComponentSkeleton: ComponentSkeleton?=null){

        val obj = o::class
        if(parentComponentSkeleton == null) {
            root = Entity(tableName(obj).toString(), null)
            componentSkeleton = ComponentSkeleton(root!!)
            jScrollPane.viewport.add(componentSkeleton)
            repaint()
            createXMLObject(o, componentSkeleton)
        }
        else {
            obj.declaredMemberProperties.forEach {
                if (!Ignore(it)) {
                    if (it.returnType.classifier.isCollection())
                    {
                        if(innerText(it,o)) {
                            var listName = it.name
                            val e = Entity(it.name, parentComponentSkeleton.entity)
                            val listElement = ComponentSkeleton(e)
                            parentComponentSkeleton.add(listElement)
                            val coll = it.call(o) as Collection<*>
                            coll.forEach {
                                if (it != null){
                                    listElement.add(ConcreteEntityComponent(listName, it.toString()))
                                    EntityConcrete(listName, it.toString(), listElement.entity)
                                    revalidate()

                                }
                            }
                        }
                        else{
                            val coll = it.call(o) as Collection<*>
                            parentComponentSkeleton.entity!!.attributes[it.name] = it.call(o).toString()
                            parentComponentSkeleton!!.add(AttributeComponent(it.name,coll.toString()))
                        }
                    } else if (it.returnType.classifier.isEnum())
                    {
                        if(innerText(it,o)) {
                            parentComponentSkeleton.add(ConcreteEntityComponent(fieldName(it),it.call(o).toString()))
                            parentComponentSkeleton.entity!!.attributes[it.name] = it.call(o).toString()
                            revalidate()
                        }
                        else{
                            parentComponentSkeleton.add(ConcreteEntityComponent(it.name,it.call(o).toString()))
                            EntityConcrete(fieldName(it), it.call(o).toString(), parentComponentSkeleton.entity)
                            revalidate()
                        }
                    }
                    else if(it.call(o)!!::class.isData)
                    {
                        var e = Entity(it.name,parentComponentSkeleton.entity)

                        var newComponent = ComponentSkeleton(e)
                        parentComponentSkeleton.add(newComponent)
                        createXMLObject(it.call(o)!!::class.javaObjectType.cast(it.call(o)), newComponent)
                        revalidate()
                    }
                    else
                    {
                        if(innerText(it,o)) {
                            parentComponentSkeleton.entity!!.attributes[it.name] = it.call(o).toString()
                            parentComponentSkeleton!!.add(AttributeComponent(fieldName(it),it.call(o).toString()))
                            revalidate()
                        }
                        else{
                            parentComponentSkeleton.add(ConcreteEntityComponent(fieldName(it), it.call(o).toString()))
                            EntityConcrete(fieldName(it), it.call(o).toString(), parentComponentSkeleton.entity)
                            revalidate()
                        }
                    }
                }
            }
        }*/
    //}

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
    var root = Entity("rootsad",null)

    val w = WindowSkeleton(root)

    val b = Book("title","JK ROwling")
    val s1 = Student(7, b,"Cristiano", "Ronaldo", StudentType.Doctoral)
    //w.createXMLObject(s1, null)
    w.open()
}