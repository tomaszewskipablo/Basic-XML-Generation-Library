import jdk.jfr.EventType
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf

abstract class EntityAbstract(name: String, var parent: Entity? = null) {
    init {
        parent?.children?.add(this)
    }

    open var name:String = name



    abstract fun accept(v: Visitor)
}

class Entity(name: String, parent: Entity? = null) : EntityAbstract(name, parent) {

    val children = mutableListOf<EntityAbstract>()
    var attributes = HashMap<String, String>()

    fun renameEntity(nameNew: String){
        super.name = nameNew
        name = nameNew
    }

    fun addAttribute(attributeName:String, insideText:String ){
        attributes[attributeName] = insideText

    }

    fun removeAttribute(attributeName:String){
        attributes.remove(attributeName)
    }

    fun removeEntity(name: String){
        children.remove(name)
    }

    fun addEntity(name: String):Entity{
        return Entity(name, this)
    }

    fun addSection(sectionName: String, insideText: String){
        val n = EntityConcrete(sectionName, insideText, this)
    }

    fun removeSection(sectionName: String, insideText: String){
        val element = children.find {  it.name == sectionName } // it is ConcreteEntityComponent &&
        if(element != null) {
            val toBeRemoved = element as EntityConcrete
            children.remove(toBeRemoved)
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

    fun renameAttribute(name: String, nameNew:String): Boolean{
        if(attributes.containsKey(name)) {
            val value = attributes[name]
            attributes.remove(name)
            attributes[nameNew] = value!!
            return true
        }
        return false
    }

    fun renameSection(name: String, nameNew:String) : Boolean{
        val element = children.find {  it.name == name } // it is ConcreteEntityComponent &&
        if(element != null) {
            val toBeRenamed = element as EntityConcrete
            toBeRenamed!!.name = nameNew
            return true
        }
        return false
    }

    override var name: String = name
        set(value) {
            field=value
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

    fun createXMLObject(o: Any, parentEntity: Entity) {
        val obj = o::class
        if (parentEntity.name != tableName(obj)) {
            parentEntity.renameEntity(tableName(obj)!!)
            createXMLObject(o, parentEntity)
        } else {
            obj.declaredMemberProperties.forEach { it ->
                if (!Ignore(it)) {
                    if (it.returnType.classifier.isCollection()) {
                        if (innerText(it, o)) {
                            var listName = it.name

                            val listEntity = parentEntity.addEntity(tableName(obj)!!)
                            val coll = it.call(o) as Collection<*>
                            coll.forEach {
                                if (it != null) {
                                    parentEntity.addSection(listName,it.toString())
                                }
                            }
                        } else {
                            val coll = it.call(o) as Collection<*>
                            parentEntity.addAttribute(fieldName(it),it.call(o).toString())
                        }
                    } else if (it.returnType.classifier.isEnum()) {
                        if (innerText(it, o)) {
                            parentEntity.addAttribute(fieldName(it),it.call(o).toString())
                        } else {
                            parentEntity.addSection(fieldName(it),it.call(o).toString())
                        }
                    } else if (it.call(o)!!::class.isData) {
                        var dataClassEntity = parentEntity.addEntity(it.name)
                        createXMLObject(it.call(o)!!::class.javaObjectType.cast(it.call(o)), dataClassEntity)
                    } else {
                        if (innerText(it, o)) {
                            parentEntity.addSection(fieldName(it),it.call(o).toString())
                        } else {
                            parentEntity.addAttribute(fieldName(it),it.call(o).toString())
                        }
                    }
                }
            }
        }
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

