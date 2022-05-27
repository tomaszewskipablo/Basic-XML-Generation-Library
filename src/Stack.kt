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
        entityParent.removeEntity(entity.name)
    }
}

class RemoveCommand(val entityParent: Entity, val entity: Entity) : Command {
    override fun run() {
        entityParent.removeEntity(entity.name)
    }

    override fun undo() {
        entityParent.addEntity(entity)
    }
}
