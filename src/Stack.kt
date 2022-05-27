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

