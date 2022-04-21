abstract class Entity(val name: String, textBetweenEntities:String, val parent: EntityConcrete? = null) {
    init {
        parent?.children?.add(this)
    }
}

class EntityConcrete(name: String, textBetweenEntities:String, parent: EntityConcrete? = null) : Entity(name, textBetweenEntities, parent) {
    val children = mutableListOf<Entity>()
    val attributes = mutableListOf<String>()
}