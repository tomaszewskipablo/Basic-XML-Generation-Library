import java.util.*

class UndoStack {
    val stack = Stack<Command>()

    fun execute(c: Command) {
        c.run()
        stack.add(c)
    }

    fun undo() {
        if (stack.isNotEmpty())
            stack.pop().undo()
    }

    fun clearStack(){
        while (stack.isNotEmpty())
            stack.pop().undo()
    }
}

interface Command {
    fun run()
    fun undo()
}

class AddCommand(val entityParent: Entity, val entity: Entity) : Command {
    override fun run() {
        entityParent.addEntity(entity)
    }

    override fun undo() {
        entityParent.removeEntity(entity)
    }
}

class RemoveCommand(val entityParent: Entity, val entity: Entity) : Command {
    override fun run() {
        entityParent.removeEntity(entity)
    }

    override fun undo() {
        entityParent.addEntity(entity)
    }
}

class RenameEntityCommand(val entity: Entity, val newName:String, val oldName:String) : Command {
    override fun run() {
        entity.rename(newName)
    }

    override fun undo() {
        entity.rename(oldName)
    }
}

class AddAttributeCommand(val entityParent: Entity, val attributeName:String, val insideText:String) : Command {
    override fun run() {
        entityParent.addAttribute(attributeName, insideText)
    }

    override fun undo() {
        entityParent.removeAtttribute(attributeName)
    }
}

class RemoveAttributeCommand(val entityParent: Entity, val attributeName:String, val insideText:String) : Command {
    override fun run() {
        entityParent.removeAtttribute(attributeName)
    }

    override fun undo() {
        entityParent.addAttribute(attributeName, insideText)
    }
}

class RenameAttributeCommand(val entity: Entity, val newName:String, val oldName:String) : Command {
    override fun run() {
        entity.renameAttribute(newName, oldName)
    }

    override fun undo() {
        entity.renameAttribute(oldName,newName)
    }
}

class AddSectionCommand(val entityParent: Entity, val sectionName:String, val insideText:String) : Command {
    override fun run() {
        entityParent.addSection(sectionName, insideText)
    }

    override fun undo() {
        entityParent.removeSection(sectionName, insideText)
    }
}

class RemoveSectionCommand(val entityParent: Entity, val sectionName:String, val insideText:String) : Command {
    override fun run() {
        entityParent.removeSection(sectionName, insideText)
    }

    override fun undo() {
        entityParent.addSection(sectionName, insideText)
    }
}

class RenameSectionCommand(val entity: Entity, val newName:String, val oldName:String) : Command {
    override fun run() {
        entity.renameSection(newName, oldName)
    }

    override fun undo() {
        entity.renameSection(oldName,newName)
    }
}
