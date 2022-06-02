import java.util.*

class UndoStack {
    val stack = Stack<Command>()
    val redoStack = Stack<Command>()

    fun execute(c: Command) {
        c.run()
        stack.add(c)
    }

    fun undo() {
        if (stack.isNotEmpty()) {
            stack.peek().undo()
            redoStack.add(stack.pop())
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            redoStack.peek().run()
            stack.add(redoStack.pop())
        }
    }

    fun clearStack(){
        while (stack.isNotEmpty())
            stack.pop().undo()
        while (redoStack.isNotEmpty())
            stack.pop().undo()
    }
}

interface Command {
    fun run()
    fun undo()
}

class AddCommand(val entityParent: ObservableEntity, val name: String) : Command {
    private lateinit var child: ObservableEntity
    override fun run() {
        child = entityParent.addEntity(name)
    }

    override fun undo() {
        entityParent.removeEntity(child.entityObject.name)
    }
}

class RemoveCommand(val entityParent: ObservableEntity, val name: String) : Command {
    override fun run() {
        entityParent.removeEntity(name)
    }

    override fun undo() {
        entityParent.addEntity(name)
    }
}

class RenameEntityCommand(val entity: ObservableEntity, val newName:String, val oldName:String) : Command {
    override fun run() {
        entity.renameEntity(newName)
    }

    override fun undo() {
        entity.renameEntity(oldName)
    }
}

class AddAttributeCommand(val entityParent: ObservableEntity, val attributeName:String, val insideText:String) : Command {
    override fun run() {
        entityParent.addAttribute(attributeName, insideText)
    }

    override fun undo() {
        entityParent.removeAtttribute(attributeName)
    }
}

class RemoveAttributeCommand(val entityParent: ObservableEntity, val attributeName:String, val insideText:String) : Command {
    override fun run() {
        entityParent.removeAtttribute(attributeName)
    }

    override fun undo() {
        entityParent.addAttribute(attributeName, insideText)
    }
}

class RenameAttributeCommand(val entity: ObservableEntity, val newName:String, val oldName:String) : Command {
    override fun run() {
        entity.renameAttribute(newName, oldName)
    }

    override fun undo() {
        entity.renameAttribute(oldName,newName)
    }
}

class AddSectionCommand(val entityParent: ObservableEntity, val sectionName:String, val insideText:String) : Command {
    override fun run() {
        entityParent.addSection(sectionName, insideText)
    }

    override fun undo() {
        entityParent.removeSection(sectionName, insideText)
    }
}

class RemoveSectionCommand(val entityParent: ObservableEntity, val sectionName:String, val insideText:String) : Command {
    override fun run() {
        entityParent.removeSection(sectionName, insideText)
    }

    override fun undo() {
        entityParent.addSection(sectionName, insideText)
    }
}

class RenameSectionCommand(val entity: ObservableEntity, val newName:String, val oldName:String) : Command {
    override fun run() {
        entity.renameSection(newName, oldName)
    }

    override fun undo() {
        entity.renameSection(oldName,newName)
    }
}

class ChangeAttributeTextCommand(val entity: ObservableEntity, val name:String, val insideText:String, val insideTextOld:String) : Command {
    override fun run() {
        entity.changeAttributeText(name, insideText)
    }

    override fun undo() {
        entity.changeAttributeText(name, insideTextOld)
    }
}

class ChangeSectionTextCommand(val entity: ObservableEntity, val name:String, val insideText:String, val insideTextOld:String) : Command {
    override fun run() {
        entity.changeSectionText(name, insideText)
    }

    override fun undo() {
        entity.changeSectionText(name, insideTextOld)
    }
}

