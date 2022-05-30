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
        entityObject.removeAtttribute(attributeName)
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

    fun addEntity(name: String) {
        val e = entityObject.addEntity(name)
        notifyObservers {
            it(TypeEvent.AddEntity, "", "", e)
        }
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
    }

    fun changeSectionText(name: String, insideText: String) {
        entityObject.changeSectionText(name, insideText)
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
