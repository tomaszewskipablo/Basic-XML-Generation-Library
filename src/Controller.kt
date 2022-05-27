class Controller() : GUIEvent{
    val undoStack = UndoStack()
    fun execute(c: Command) {
        undoStack.execute(c)
    }
    override fun renameEntity(entity: Entity, newName:String) {
        execute(RenameEntityCommand(entity, newName, entity.name))
    }

    override fun addEntity(newEntityName:String, parentEntity: Entity){
        val e = Entity(newEntityName,parentEntity)
        execute(AddCommand(parentEntity, e))
    }

    override fun deleteEntity(entity: Entity, removeEntity:String){
        val e = entity.children.find { it is Entity && it.name == removeEntity}
        if(e != null) {
            execute(RemoveCommand(entity, e as Entity))
        }
    }

    override fun addAttribute(entity: Entity, attributeName:String){
        entity.addAttribute(attributeName)
    }

    override fun removeAttribute(entity: Entity, attributeName:String){
        entity.removeAtttribute(attributeName)
    }

    override fun renameAttribute(entity: Entity, name:String, newName: String){
        entity.renameAttribute(name, newName)
    }

    override fun addSection(entity: Entity, sectionName:String){
        entity.addSection(sectionName)
    }

    override fun removeSection(entity: Entity, sectionName:String){
        entity.removeSection(sectionName)
    }

    override fun renameSection(entity: Entity, name:String, newName: String){
        entity.renameSection(name, newName)
    }

    override fun changeAttributeText(entity: Entity, name:String, insideText:String){
        entity.changeAttributeText(name, insideText)
    }

    override fun changeSectionText(entity: Entity, name:String, insideText:String){
        entity.changeSectionText(name, insideText)
    }
}