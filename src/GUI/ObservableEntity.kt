import org.w3c.dom.Attr
import kotlin.reflect.full.declaredMemberProperties

class ObservableEntity(var entityObject: Entity) :IObservable<Event> {

    override val observers: MutableList<Event> = mutableListOf()

    fun renameEntity(nameNew: String){
        entityObject.renameEntity(nameNew)
        notifyObservers {
            it(TypeEvent.RenameEntity, nameNew, "", null)
        }
    }

    fun addAttribute(attributeName: String, insideText: String) {
        entityObject.addAttribute(attributeName, insideText)
        notifyObservers {
            it(TypeEvent.AddAttribute, attributeName, insideText, null)
        }
    }

    fun removeAtttribute(attributeName: String) {
        entityObject.removeAttribute(attributeName)
        notifyObservers {
            it(TypeEvent.RemoveAttribute, attributeName, "", null)
        }
    }

    fun removeEntity(name:String) {
        entityObject.removeEntity(name)
        notifyObservers {
            it(TypeEvent.RemoveEntity, name, "", null)
        }
    }

    fun addEntity(name: String) : Entity {
        val e = entityObject.addEntity(name)
        notifyObservers {
            it(TypeEvent.AddEntity, "", "", e)
        }
        return e
    }

    fun addSection(sectionName: String, insideText: String) {
        entityObject.addSection(sectionName, insideText)
        notifyObservers {
            it(TypeEvent.AddSection, sectionName, insideText, null)
        }
    }

    fun removeSection(sectionName: String, insideText: String) {
        val element = entityObject.children.find { it.name == sectionName } // it is ConcreteEntityComponent &&
        if (element != null) {
            element as EntityConcrete
            entityObject.removeSection(sectionName, insideText)
            notifyObservers {
                it(TypeEvent.RemoveSection, sectionName, element.innerText, null)
            }
        }
    }

    fun changeAttributeText(name: String, insideText: String) {
       entityObject.changeAttributeText(name, insideText)
        notifyObservers {
            it(TypeEvent.ChangeAttributeInsideText, name, insideText, null)
        }
    }

    fun changeSectionText(name: String, insideText: String) {
        entityObject.changeSectionText(name, insideText)
        notifyObservers {
            it(TypeEvent.ChangeSectionInsideText, name, insideText, null)
        }
    }

    fun renameAttribute(name: String, nameNew: String) {
        if(entityObject.renameAttribute(name,nameNew))
            notifyObservers {
                it(TypeEvent.RenameAttribute, name, nameNew, null)
            }
    }

    fun renameSection(name: String, nameNew: String) {
        if(entityObject.renameSection(name,nameNew)) // element exist
            notifyObservers {
                it(TypeEvent.RenameSection, name, nameNew, null)
            }
    }

        fun removeAllChildren(){
            val EntitiesToRemove = mutableListOf<String>()
            val EntityConcreteToRemove = mutableListOf<String>()

            entityObject.children.forEach(){
                if(it is Entity)
                    EntitiesToRemove.add(it.name)
                if(it is EntityConcrete)
                    EntityConcreteToRemove.add(it.name)
            }

            EntitiesToRemove.forEach(){
                val name = it.toString()
                entityObject.removeEntity(name)
                notifyObservers {
                    it(TypeEvent.RemoveEntity, name, "", null)
                }
            }

            EntityConcreteToRemove.forEach(){
                val name = it.toString()
                entityObject.removeSection(name, "")
                notifyObservers {
                    it(TypeEvent.RemoveSection, name, "", null)
                }
            }

            val attributeToRemove = mutableListOf<String>()

            entityObject.attributes.forEach()
            {
               attributeToRemove.add(it.key)
            }

           attributeToRemove.forEach(){
                val name = it
                entityObject.removeAttribute(name)
                notifyObservers {
                    it(TypeEvent.RemoveAttribute, name, "", null)
                }
            }
    }

    fun createXMLObject(o: Any, parentEntity: ObservableEntity, isRoot: Boolean = false) {
        val obj = o::class
        if (isRoot){
                parentEntity.removeAllChildren()
                parentEntity.renameEntity(tableName(obj)!!)
                createXMLObject(o, parentEntity)
        } else {
            obj.declaredMemberProperties.forEach { it ->
                if (!Ignore(it)) {
                    if (it.returnType.classifier.isCollection()) {
                        if (innerText(it, o)) {
                            var listName = it.name
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
                        var dataClassEntity = addEntity(it.name)
                        var obserEntity = ObservableEntity(dataClassEntity)     // TODO DATACLASS IS NOT BEING OBSERVED
                        createXMLObject(it.call(o)!!::class.javaObjectType.cast(it.call(o)), obserEntity)
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

enum class TypeEvent {RenameEntity,RemoveEntity, AddEntity, AddAttribute, RemoveAttribute, RenameAttribute, AddSection,RemoveSection, RenameSection, ChangeAttributeInsideText, ChangeSectionInsideText}
