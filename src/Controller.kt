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

}