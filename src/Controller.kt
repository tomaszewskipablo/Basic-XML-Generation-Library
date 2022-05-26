class Controller() : GUIEvent{
    override fun renameEntity(entity: Entity, newName:String) {
        entity.rename(newName)
    }

    override fun addEntity(newEntityName:String, parentEntity: Entity){
        val e = Entity(newEntityName,parentEntity)
        //parentEntity.children.add(entity)
        parentEntity.addEntity(e)
    }

    override fun addAttribute(entity: Entity, attributeName:String){
        entity.attributes[attributeName] = ""
        entity.addAttribute(attributeName)
    }

    override fun removeAttribute(entity: Entity, attributeName:String){
        entity.attributes.remove(attributeName)
        entity.removeAtttribute(attributeName)
    }
    override fun deleteEntity(entity: Entity, removeEntity:String){
        entity.removeEntity(entity, removeEntity)
    }
}