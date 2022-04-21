abstract class EntityAbstract(val name: String, val parent: Entity? = null) {
    init {
        parent?.children?.add(this)
    }

    abstract fun accept(v: Visitor)
}

class Entity(name: String, parent: Entity? = null) : EntityAbstract(name, parent) {
    val children = mutableListOf<EntityAbstract>()
    val attributes = mutableListOf<String>()

    override fun accept(v: Visitor) {
        if(v.visit(this)) { // if any children
            children.forEach {
                it.accept(v)
            }
        }
    }


}

class EntityConcrete(name: String, textBetweenEntities:String, parent: Entity? = null) : EntityAbstract(name, parent) {


    override fun accept(v: Visitor) {
        v.visit(this)
        }
    }

interface Visitor {
    fun visit(e: EntityConcrete) {}
    fun visit(e: Entity) = true
}



