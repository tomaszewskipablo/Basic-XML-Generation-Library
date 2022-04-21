import kotlin.reflect.KClass

class Xml constructor( val header: String, var root: EntityConcrete){


    fun serialize(clazz: KClass<*>):String{
        return "";
    }

}

