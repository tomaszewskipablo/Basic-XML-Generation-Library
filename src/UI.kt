import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.PrintWriter
import javax.swing.*
import javax.swing.border.CompoundBorder
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf

interface GUIEvent{
    fun renameEntity(entity: ObservableEntity, newName:String)
    fun addEntity(newEntityName:String, parentEntity: ObservableEntity)
    fun deleteEntity(entity: ObservableEntity, removeEntity:String)
    fun addAttribute(entity: ObservableEntity, newEntityName:String, insideText:String)
    fun removeAttribute(entity: ObservableEntity, removeAttribute: String, insideText:String)
    fun renameAttribute(entity: ObservableEntity, name: String, nameNew:String)
    fun changeAttributeText(entity: ObservableEntity, name:String, nameNew:String)
    fun addSection(entity: ObservableEntity, sectionName:String, insideText:String)
    fun removeSection(entity: ObservableEntity, sectionName:String, insideText:String)
    fun renameSection(entity: ObservableEntity, name:String, newName: String)
    fun changeSectionText(entity: ObservableEntity, name:String, insideText:String)
}

class ComponentSkeleton(var entity: Entity, val controller: Controller) : JPanel(), IObservable<GUIEvent> {
    override val observers: MutableList<GUIEvent> = mutableListOf<GUIEvent>()
    var observableEntity = ObservableEntity(entity)
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

        observableEntity.addObserver {Event, value, name, entity -> handleThisEvent(Event, value, name, entity) }
        addObserver(controller)
    }

    // Update View
    fun handleThisEvent(typeEvent: TypeEvent, name: String?, value: String?, child: Entity?){
        if(typeEvent == TypeEvent.RenameEntity) {
            nameEntity = name!!
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
        }
        else if(typeEvent == TypeEvent.AddSection) {
            add(ConcreteEntityComponent(name!!,value!!))
        }
        else if(typeEvent == TypeEvent.RemoveSection) {
            val toBeRemoved = components.find { it is ConcreteEntityComponent && name == it.text }
            if(toBeRemoved != null)
                remove(toBeRemoved as ConcreteEntityComponent)
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

    private fun createPopupMenu() {
        val popupmenu = JPopupMenu("Actions")
        val a = JMenuItem("Add Tag")
        a.addActionListener {
            val text = JOptionPane.showInputDialog("Tag name")
            notifyObservers{
                it.addEntity(text, observableEntity)
            }
        }
        popupmenu.add(a)

        val deleteEntityButton = JMenuItem("Remove Tag")
        deleteEntityButton.addActionListener {
            val text = JOptionPane.showInputDialog("entity name to be removed")
            notifyObservers{
                it.deleteEntity(observableEntity,text)
            }
        }
        popupmenu.add(deleteEntityButton)

        val c = JMenuItem("Rename Tag")
        c.addActionListener {
            val text = JOptionPane.showInputDialog("Rename")
            controller.execute(RenameEntityCommand(observableEntity, text, observableEntity.entityObject.name))
            notifyObservers{
                it.renameEntity(observableEntity,text)
            }
        }
        popupmenu.add(c)

        val b = JMenuItem("Add attribute")
        b.addActionListener {
            val text = JOptionPane.showInputDialog("attribute name")
            notifyObservers{
                it.addAttribute(observableEntity,text, "")
            }
            revalidate()
        }
        popupmenu.add(b)
        b.background = Color.LIGHT_GRAY

        val r = JMenuItem("Remove attribute")
        r.addActionListener {
            val text = JOptionPane.showInputDialog("attribute name to be removed")
            notifyObservers{
                it.removeAttribute(observableEntity,text, "")
            }
        }
        r.background = Color.LIGHT_GRAY
        popupmenu.add(r)

        val renameAttributeButton = JMenuItem("Rename attribute")
        renameAttributeButton.addActionListener {
            val attributeName = JOptionPane.showInputDialog("Which attribute should be renamed?")
            if(entity.attributes[attributeName] != null) {
                val newName = JOptionPane.showInputDialog("New name")
                notifyObservers {
                    it.renameAttribute(observableEntity, attributeName, newName)
                }
            }
        }
        renameAttributeButton.background = Color.LIGHT_GRAY
        popupmenu.add(renameAttributeButton)

        val en = JMenuItem("Add section")
        en.background = Color.GRAY
        en.addActionListener {
            val text = JOptionPane.showInputDialog("Section name")
            notifyObservers{
                it.addSection(observableEntity,text, "")
            }
            revalidate()
        }
        popupmenu.add(en)

        val removeSectionButton = JMenuItem("Remove section")
        removeSectionButton.background = Color.GRAY
        removeSectionButton.addActionListener {
            val text = JOptionPane.showInputDialog("Section name")
            val element = entity.children.find{it.name == text}
            if(element != null) {
                val s = element as EntityConcrete
                notifyObservers {
                    it.removeSection(observableEntity, text, s.innerText)
                }
                revalidate()
            }
        }
        popupmenu.add(removeSectionButton)

        val renameSectionButton = JMenuItem("Rename section")
        renameSectionButton.background = Color.GRAY
        renameSectionButton.addActionListener {
            val sectionName = JOptionPane.showInputDialog("Which section should be renamed?")
            if(entity.children.find{it.name == sectionName} != null) {
                val newName = JOptionPane.showInputDialog("New name")
                notifyObservers {
                    it.renameSection(observableEntity, sectionName, newName)
                }
            }
        }
        popupmenu.add(renameSectionButton)



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
                        it.changeAttributeText(observableEntity,nameAttribute,jText.text)
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
                        it.changeSectionText(observableEntity,name,jTextField.text)
                    }
                }
            })
            add(jTextField,BorderLayout.CENTER)
        }
    }
}

class WindowSkeleton(var root: Entity, var controller: Controller, val version:String,
                     val codding:String, val standalone:String) : JFrame("title") {
    var xmlHeader = "<?xml version=\"$version\" encoding=\"$codding\" standalone=\"$standalone\" ?>"
    lateinit var componentSkeleton: ComponentSkeleton
    var modeWriteToList: MutableList<WriteToMode> = mutableListOf()

    var jScrollPane = JScrollPane()

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(700, 900)
        layout = BorderLayout()
        jScrollPane.viewport.add(ComponentSkeleton(root, controller))
        add(jScrollPane)

        val serializeButton = JButton("Serialize")
        serializeButton.setBounds(0, 230, 50, 20)
        serializeButton.addActionListener {
            writeTo(xmlHeader, root.serialization(), modeWriteToList)
        }

        val loadButton = JButton("Load")
        loadButton.addActionListener {
            val b = Book("great book", "great book about nothing")
            val s1 = Student(7, b, "Cristiano", "Ronaldo", StudentType.Doctoral)
            root.createXMLObject(s1, root)
        }

        val undo = JButton("Undo")
        undo.addActionListener {
            controller.undoStack.undo()
        }

        val fileCheckBox = JCheckBox("File")
        fileCheckBox.setBounds(100, 100, 50, 50)
        val consoleCheckBox = JCheckBox("Console", true)
        modeWriteToList.add(WriteToMode.Console)

        consoleCheckBox.setBounds(100, 150, 50, 50)
        fileCheckBox.addActionListener {
            if(fileCheckBox.isSelected)
                modeWriteToList.add(WriteToMode.File)
            else
                modeWriteToList.remove(WriteToMode.File)
        }
        consoleCheckBox.addActionListener {
            if(consoleCheckBox.isSelected)
                modeWriteToList.add(WriteToMode.Console)
            else
                modeWriteToList.remove(WriteToMode.Console)
        }

        val manipulationPanel = JPanel()
        manipulationPanel.layout = BoxLayout(manipulationPanel, BoxLayout.X_AXIS)
        manipulationPanel.add(serializeButton)
        manipulationPanel.add(loadButton)
        manipulationPanel.add(undo)
        add(manipulationPanel, BorderLayout.NORTH)

        manipulationPanel.add(fileCheckBox)
        manipulationPanel.add(consoleCheckBox)
    }

    fun open() {
        isVisible = true
    }

    private fun writeTo(xmlHeader:String, xml:String, modeWriteToList: MutableList<WriteToMode>){
        if(modeWriteToList.find { it ==  WriteToMode.File}!= null) {
            val writer = PrintWriter("file.txt")
            writer.append(xmlHeader)
            writer.append('\n')
            writer.append(xml)
            writer.close()
        }
        if(modeWriteToList.find { it ==  WriteToMode.Console}!= null) {
            println(xmlHeader)
            println(xml)
        }
    }
}

enum class WriteToMode {File, Console}

fun main() {
    var root = Entity("default name",null)
    var controller = Controller()
    val w = WindowSkeleton(root, controller, "1.0","UTF-8", "no")

    w.open()
}