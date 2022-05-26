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

    fun addEntity(entity: Entity){
        notifyObservers {
            it(TypeEvent.AddEntity, "","", entity)
        }
    }

    fun addAttribute(attributeName:String){
        notifyObservers {
            it(TypeEvent.AddAttribute, attributeName,"", null)
        }
    }

    fun removeAtttribute(attributeName:String){
        println(attributeName)
        notifyObservers {
            it(TypeEvent.RemoveAttribute, attributeName,"", null)
        }
    }

    fun removeEntity(entity: Entity, removeEntity:String){
        val toBeRemoved = entity.children.find { it.name == removeEntity }
        if(toBeRemoved != null)
            children.remove(toBeRemoved)
        notifyObservers {
            it(TypeEvent.RemoveEntity, removeEntity,"", entity)
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

    fun serialization() : String{

        var xmlText = ""
        accept(object : Visitor {
            var depth = 0
            override fun visit(e: EntityConcrete) {
                xmlText += "\t".repeat(depth) + "<" + e.name + ">" + e.innerText + "</" + e.name + ">" + "\n"
            }

            override fun visit(e: Entity): Boolean {
                xmlText += "\t".repeat(depth) + "<" + e.name + wirteAttributes(e.attributes) +">" + "\n"
                depth++
                return true
            }

            override fun endVisit(e: Entity) {
                depth--
                xmlText += "\t".repeat(depth) + "</" + e.name + ">" +"\n"
            }
        })
        return xmlText
    }

    fun wirteAttributes(att:HashMap<String, String>):String{
        var attributes = ""
        att.forEach(){
            attributes += " " + it.key + "=\"" + it.value + "\""
    }
        return attributes
    }
}

class EntityConcrete(name: String, val innerText:String, parent: Entity? = null) : EntityAbstract(name, parent) {
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

enum class TypeEvent {RenameEntity,RemoveEntity, AddEntity, AddAttribute, AddSection, RemoveAttribute}
