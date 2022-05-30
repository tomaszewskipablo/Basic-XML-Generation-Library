import jdk.jfr.EventType
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf

/**
 * Abstract class the represent the first level of related elements that are organized in a hierarchy.
 * The top element of Composite design patter.
 */
abstract class EntityAbstract(name: String, var parent: Entity? = null) {
    init {
        parent?.children?.add(this)
    }

    open var name:String = name

    abstract fun accept(v: Visitor)
}

/**
 * The element represent simple entity that
 * content between the entities consists exclusively of plain text (no tags ) or a sequence (zero or more) of entities.
 *
 * Entity has list of children which represents children xml element and dictionary of attributes that represents xml entity attributes
 */
class Entity(name: String, parent: Entity? = null) : EntityAbstract(name, parent) {
    val children = mutableListOf<EntityAbstract>()
    var attributes = HashMap<String, String>()

     /**
     * Implements Entity.renameEntity
     *
     * @param nameNew new name for entity
     */
    fun renameEntity(nameNew: String){
        super.name = nameNew
        name = nameNew
    }

    /**
     * Implements Entity.renameEntity
     * Adds attribute to entity children list
     *
     * @param attributeName key for attribute for entity
     * @param insideText value for attribute for entity
     */
    fun addAttribute(attributeName:String, insideText:String ){
        attributes[attributeName] = insideText
    }

    /**
     * Implements Entity.removeAttribute
     * Removes attribute from entity children list
     *
     * @param attributeName key for attribute for entity
     * @param insideText value for attribute for entity
     */
    fun removeAttribute(attributeName:String){
        attributes.remove(attributeName)
    }

    /**
     * Implements Entity.removeAttribute
     * Removes attribute from entity children list
     *
     * @param attributeName key for attribute for entity
     * @param insideText value for attribute for entity
     */
    fun removeEntity(name: String){
        children.remove(name)
    }

    /**
     * Implements Entity.addEntity
     * adds new entity to entity children list
     *
     * @param name new of new entity
     */
    fun addEntity(name: String):Entity{
        return Entity(name, this)
    }

    /**
     * Implements Entity.addSection
     *
     * adds new section to entity children list
     * section represented by EntityConcrete
     * @see EntityConcrete
     *
     * @param sectionName name of section
     * @param insideText value for inside text of section
     */
    fun addSection(sectionName: String, insideText: String){
        val n = EntityConcrete(sectionName, insideText, this)
    }

    /**
     * Implements Entity.addSection
     * adds new section to entity children list
     * section represented by EntityConcrete
     * @see EntityConcrete
     *
     * @param sectionName name of section
     * @param insideText value for inside text of section
     */
    fun removeSection(sectionName: String, insideText: String){
        val element = children.find {  it.name == sectionName } // it is ConcreteEntityComponent &&
        if(element != null) {
            val toBeRemoved = element as EntityConcrete
            children.remove(toBeRemoved)
        }
    }

    /**
     * Implements Entity.changeAttributeText
     *
     * updates insideText of entity's attribute of key name
     *
     * @param name key of attributes dictionary
     * @param insideText value for inside text of attributes dictionary
     */
    fun changeAttributeText(name: String, insideText: String){
        attributes[name] = insideText
    }

    /**
     * Implements Entity.changeSectionText
     *
     * updates insideText of entity's section represented by ConcreteEntityComponent
     *
     * @param name key of section
     * @param insideText value for inside text of section
     */
    fun changeSectionText(name: String, insideText: String){
        val element = children.find {  it is EntityConcrete && it.name == name}
        if(element != null) {
            val toBeChanged = element as EntityConcrete
            toBeChanged.innerText = insideText
        }
    }

    /**
     * Implements Entity.renameAttribute
     *
     * renames entity's attribute
     *
     * @param sectionName name of attribute
     * @param insideText value for inside text of attribute
     */
    fun renameAttribute(name: String, nameNew:String): Boolean{
        if(attributes.containsKey(name)) {
            val value = attributes[name]
            attributes.remove(name)
            attributes[nameNew] = value!!
            return true
        }
        return false
    }

    /**
     * Implements Entity.renameSection
     *
     * renames entity's section
     *
     * @param sectionName name of section
     * @param insideText value for inside text of section
     */
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
        if(v.visit(this)) {
            children.forEach {
                it.accept(v)
            }
        }

        v.endVisit(this)
    }

    /**
     * Implements Entity.search
     *
     * Entity search function, that retrieves EntityConcrete matching the criteria
     *
     * @param accept acceptance criterion as decision function
     *
     * @return list of concreteEntity matching passed criterion
     */
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

    /**
     * Implements Entity.escapespecialCharacter
     *
     * Replace escaping charcaters in a string by certain characters
     *
     * @param text string to be under checked for special characters occurance
     *
     * @return text with special characters replaced
     */
    fun escapeSpecialCharacter(text: String):String{
        return text
            .replace("<", "&lt;")
            .replace("&","&amp;")
            .replace(">","&gt;")
            .replace("\"","&quot;")
            .replace("\'","&apos;")
    }

    /**
     * Implements Entity.serialization
     *
     * Serialize entity object to text
     *
     * @return xml text
     */
    fun serialization() : String{
        var xmlText = ""
        accept(object : Visitor {
            var depth = 0
            override fun visit(e: EntityConcrete) {
                xmlText += "\t".repeat(depth) + "<" + escapeSpecialCharacter(e.name) + ">" + escapeSpecialCharacter(e.innerText) + "</" + escapeSpecialCharacter(e.name) + ">" + "\n"
            }

            override fun visit(e: Entity): Boolean {
                xmlText += "\t".repeat(depth) + "<" + escapeSpecialCharacter(e.name) + writeAttributes(e.attributes) +">" + "\n"
                depth++
                return true
            }

            override fun endVisit(e: Entity) {
                depth--
                xmlText += "\t".repeat(depth) + "</" + escapeSpecialCharacter(e.name) + ">" +"\n"
            }
        })
        return xmlText
    }

    /**
     * Implements Entity.writeAttributes
     *
     * Helper for Entity.serialization to write entity's attributes
     *
     * @return text representing entity's attributes
     */
    fun writeAttributes(att:HashMap<String, String>):String{
        var attributes = ""
        att.forEach(){
            attributes += " " + escapeSpecialCharacter(it.key) + "=\"" + escapeSpecialCharacter(it.value) + "\""
    }
        return attributes
    }

    /**
     * Implements Entity.createXMLObject
     *
     * creates XML object from memory object
     *
     * @param o object to build entity from
     * @param parentEntity parent entity for new entity object
     */
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

/**
 * Class representing section of XML object
 */
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

