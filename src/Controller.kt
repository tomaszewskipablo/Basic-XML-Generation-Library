class Controller() : GUIEvent{
    override fun renameEntity(entity: Entity, newName:String) {
        entity.rename(newName)
    }

    override fun addEntity(newEntityName:String, parentEntity: Entity){
        val e = Entity(newEntityName,parentEntity)
        //parentEntity.children.add(entity)
        parentEntity.addEntity(e)
    }

    override fun deleteEntity(entity: Entity, removeEntity:String){
        entity.removeEntity(entity, removeEntity)
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