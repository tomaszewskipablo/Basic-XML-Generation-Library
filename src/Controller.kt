class Controller() : GUIEvent{
    override fun addAttribute() {
        TODO("Not yet implemented")
    }

    override fun renameEntity(entity: Entity, newName:String) {
        entity.rename(newName)
    }

    override fun renameEntity() {
        TODO("Not yet implemented")
    }

    override fun addEntity(newEntityName:String, parentEntity: Entity){
        val e = Entity(newEntityName,parentEntity)
        //parentEntity.children.add(entity)
        parentEntity.addEntity(e)
    }
}