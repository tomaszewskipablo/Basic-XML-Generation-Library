abstract class EntityAbstract(val name: String, val parent: Entity? = null) {
    init {
        parent?.children?.add(this)
    }

    abstract fun accept(v: Visitor)
}

class Entity(name: String, parent: Entity? = null) : EntityAbstract(name, parent) {
    val children = mutableListOf<EntityAbstract>()
    val attributes = HashMap<String, String>()


    override fun accept(v: Visitor) {
        if(v.visit(this)) { // if any children
            children.forEach {
                it.accept(v)
            }
        }

        v.endVisit(this)
    }

    // Search for entity by name, TODO search by creteria
    fun search(name: String): EntityConcrete?{
        var entity: EntityConcrete? = null

        val searchVisitor = object : Visitor {
            override fun visit(e: EntityConcrete) {
                if (e.name == name)
                    entity = e
            }

            override fun visit(e: Entity): Boolean { // it can't have name to check, so we just if element is not found, if not true to continue
                return entity == null
            }
        }
        this.accept(searchVisitor)
        return entity
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



