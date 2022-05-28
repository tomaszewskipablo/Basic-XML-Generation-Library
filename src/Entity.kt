import jdk.jfr.EventType
import java.io.File
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

abstract class EntityAbstract(name: String, var parent: Entity? = null) {
    init {
        parent?.children?.add(this)
    }

    open var name:String = name



    abstract fun accept(v: Visitor)
}

class Entity(name: String, parent: Entity? = null) : EntityAbstract(name, parent), IObservable<Event> {
    override val observers: MutableList<Event> = mutableListOf()

    val children = mutableListOf<EntityAbstract>()
    var attributes = HashMap<String, String>()

    fun rename(nameNew: String){
        name = nameNew
        notifyObservers {
            it(TypeEvent.RenameEntity, name, nameNew,null)
        }
    }

    fun addAttribute(attributeName:String, insideText:String ){
        attributes[attributeName] = insideText
        notifyObservers {
            it(TypeEvent.AddAttribute, attributeName,insideText, null)
        }
    }

    fun removeAtttribute(attributeName:String){
        attributes.remove(attributeName)
        notifyObservers {
            it(TypeEvent.RemoveAttribute, attributeName,"", null)
        }
    }

    fun removeEntity(entity: Entity){
            children.remove(entity)
        notifyObservers {
            it(TypeEvent.RemoveEntity, entity.name,"", null)
        }
    }

    fun addEntity(entity: Entity){
        notifyObservers {
            it(TypeEvent.AddEntity, "","", entity)
        }
    }

    fun addSection(sectionName: String, insideText: String){
        val n = EntityConcrete(sectionName, insideText, this)
        notifyObservers {
            it(TypeEvent.AddSection, sectionName,insideText, null)
        }
    }

    fun removeSection(sectionName: String, insideText: String){
        val element = children.find {  it.name == sectionName } // it is ConcreteEntityComponent &&
        if(element != null) {
            val toBeRemoved = element as EntityConcrete
            children.remove(toBeRemoved)
            notifyObservers {
                it(TypeEvent.RemoveSection, sectionName, element.innerText, null)
            }
        }
    }

    fun changeAttributeText(name: String, insideText: String){
        attributes[name] = insideText
    }

    fun changeSectionText(name: String, insideText: String){
        val element = children.find {  it.name == name } // it is ConcreteEntityComponent &&
        if(element != null) {
            val toBeChanged = element as EntityConcrete
            toBeChanged.innerText = insideText
        }
    }

    fun renameAttribute(name: String, nameNew:String){
        val value = attributes[name]
        attributes.remove(name)
        attributes[nameNew] = value!!
        notifyObservers {
            it(TypeEvent.RenameAttribute, name,nameNew, null)
        }
    }

    fun renameSection(name: String, nameNew:String){
        val element = children.find {  it.name == name } // it is ConcreteEntityComponent &&
        if(element != null) {
            val toBeRenamed = element as EntityConcrete
        toBeRenamed!!.name = nameNew
        notifyObservers {
            it(TypeEvent.RenameSection, name, nameNew, null)
        }
        }
    }

    override var name: String = name
        set(value) {
            field=value
            notifyObservers {
                it(TypeEvent.RemoveEntity, "", name, null)
            }
        }

    override fun accept(v: Visitor) {
        if(v.visit(this)) { // if any children
            children.forEach {
                it.accept(v)
            }
        }

        v.endVisit(this)
    }

    fun search(accept: (EntityConcrete) -> Boolean = {true}): List<EntityConcrete>{
        val concreteEntitiesList = mutableListOf<EntityConcrete>()
        var entity: EntityConcrete? = null

        val searchVisitor = object : Visitor {
            override fun visit(e: EntityConcrete) {
                if (accept(e))
                    concreteEntitiesList.add(e)
            }

            override fun visit(e: Entity): Boolean {
                return entity == null
            }
        }
        this.accept(searchVisitor)
        return concreteEntitiesList
    }

    fun escapespecialCharacter(text: String):String{
        return text
            .replace("<", "&lt;")
            .replace("&","&amp;")
            .replace(">","&gt;")
            .replace("\"","&quot;")
            .replace("\'","&apos;")
    }

    fun serialization() : String{

        var xmlText = ""
        accept(object : Visitor {
            var depth = 0
            override fun visit(e: EntityConcrete) {
                xmlText += "\t".repeat(depth) + "<" + escapespecialCharacter(e.name) + ">" + escapespecialCharacter(e.innerText) + "</" + escapespecialCharacter(e.name) + ">" + "\n"
            }

            override fun visit(e: Entity): Boolean {
                xmlText += "\t".repeat(depth) + "<" + escapespecialCharacter(e.name) + wirteAttributes(e.attributes) +">" + "\n"
                depth++
                return true
            }

            override fun endVisit(e: Entity) {
                depth--
                xmlText += "\t".repeat(depth) + "</" + escapespecialCharacter(e.name) + ">" +"\n"
            }
        })
        return xmlText
    }

    fun wirteAttributes(att:HashMap<String, String>):String{
        var attributes = ""
        att.forEach(){
            attributes += " " + escapespecialCharacter(it.key) + "=\"" + escapespecialCharacter(it.value) + "\""
    }
        return attributes
    }
}

class EntityConcrete(name: String, var innerText:String, parent: Entity? = null) : EntityAbstract(name, parent) {
    override fun accept(v: Visitor) {
        v.visit(this)
        }
    }

interface Visitor {
    fun visit(e: EntityConcrete) {}
    fun visit(e: Entity) = true
    fun endVisit(e: Entity) {}
}

interface IObservable<O> {
    val observers: MutableList<O>

    fun addObserver(observer: O) {
        observers.add(observer)
    }

    fun removeObserver(observer: O) {
        observers.remove(observer)
    }

    fun notifyObservers(handler: (O) -> Unit) {
        observers.toList().forEach { handler(it) }
    }
}

typealias Event = (typeEvent:TypeEvent,name: String?, value:String?, entity: Entity?) -> Unit

enum class TypeEvent {RenameEntity,RemoveEntity, AddEntity, AddAttribute, RemoveAttribute, RenameAttribute, AddSection,RemoveSection, RenameSection}
