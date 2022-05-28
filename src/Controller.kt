class Controller() : GUIEvent{
    val undoStack = UndoStack()
    fun execute(c: Command) {
        undoStack.execute(c)
    }
    override fun renameEntity(entity: Entity, newName:String) {
        execute(RenameEntityCommand(entity, newName, entity.name))
    }

    override fun addEntity(newEntityName:String, parentEntity: Entity): Entity{
        val e = Entity(newEntityName,parentEntity)
        execute(AddCommand(parentEntity, e))
        return e
    }

    override fun deleteEntity(entity: Entity, removeEntity:String){
        val e = entity.children.find { it is Entity && it.name == removeEntity}
        if(e != null) {
            execute(RemoveCommand(entity, e as Entity))
        }
    }

    override fun removeAttribute(entity: Entity, attributeName:String, insideText:String){
        execute(RemoveAttributeCommand(entity, attributeName, insideText))
    }

    override fun addAttribute(entity: Entity, attributeName:String, insideText:String){
        execute(AddAttributeCommand(entity, attributeName, insideText))
    }

    override fun renameAttribute(entity: Entity, name:String, newName: String){
        execute(RenameAttributeCommand(entity, name, newName))
    }

    override fun addSection(entity: Entity, sectionName:String, insideText:String){
        execute(AddSectionCommand(entity,sectionName, insideText))
    }

    override fun removeSection(entity: Entity, sectionName:String, insideText:String){
        execute(RemoveSectionCommand(entity,sectionName, insideText))
    }

    override fun renameSection(entity: Entity, name:String, newName: String){
        execute(RenameSectionCommand(entity, name,newName))
    }

    override fun changeAttributeText(entity: Entity, name:String, insideText:String){
        entity.changeAttributeText(name, insideText)
    }

    override fun changeSectionText(entity: Entity, name:String, insideText:String){
        entity.changeSectionText(name, insideText)
    }

    fun clearStack(){
        undoStack.clearStack()
    }
}