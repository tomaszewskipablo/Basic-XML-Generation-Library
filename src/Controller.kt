class Controller() : GUIEvent{
    val undoStack = UndoStack()
    fun execute(c: Command) {
        undoStack.execute(c)
    }
    override fun renameEntity(entity: ObservableEntity, newName:String) {
        execute(RenameEntityCommand(entity, newName, entity.entityObject.name))
    }

    override fun addEntity(newEntityName:String, parentEntity: ObservableEntity){
        execute(AddCommand(parentEntity, newEntityName))
    }

    override fun deleteEntity(entity: ObservableEntity, removeEntity:String){
        val e = entity.entityObject.children.find { it is Entity && it.name == removeEntity}
        if(e != null) {
            val eN = e as Entity
            execute(RemoveCommand(entity, e.name))
        }
    }

    override fun removeAttribute(entity: ObservableEntity, attributeName:String, insideText:String){
        execute(RemoveAttributeCommand(entity, attributeName, insideText))
    }

    override fun addAttribute(entity: ObservableEntity, attributeName:String, insideText:String){
        execute(AddAttributeCommand(entity, attributeName, insideText))
    }

    override fun renameAttribute(entity: ObservableEntity, name:String, newName: String){
        execute(RenameAttributeCommand(entity, name, newName))
    }

    override fun addSection(entity: ObservableEntity, sectionName:String, insideText:String){
        execute(AddSectionCommand(entity,sectionName, insideText))
    }

    override fun removeSection(entity: ObservableEntity, sectionName:String, insideText:String){
        execute(RemoveSectionCommand(entity,sectionName, insideText))
    }

    override fun renameSection(entity: ObservableEntity, name:String, newName: String){
        execute(RenameSectionCommand(entity, name,newName))
    }

    override fun changeAttributeText(entity: ObservableEntity, name:String, insideText:String){
        entity.changeAttributeText(name, insideText)
    }

    override fun changeSectionText(entity: ObservableEntity, name:String, insideText:String){
        entity.changeSectionText(name, insideText)
    }

    fun clearStack(){
        undoStack.clearStack()
    }
}