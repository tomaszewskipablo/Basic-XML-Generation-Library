class Xml constructor(var root: Entity,val version:String,
                      val codding:String, val standalone:String){
    fun serialize() : String{
        return "<?xml version=\"$version\" encoding=\"$codding\" standalone=\"$standalone\" ?>" + "\n" + root.serialization()
    }

    fun search(accept: (EntityConcrete) -> Boolean = {true}): List<EntityConcrete> {
        return root.search(::innerTextLonger)
    }
}
