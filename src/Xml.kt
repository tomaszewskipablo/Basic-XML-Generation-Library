import kotlin.reflect.KClass

class Xml constructor( val header: String, var root: Entity){


    fun serialize(clazz: KClass<*>):String{
        return "";
    }

    fun printModel(){
        println(root.serialization())
    }

}

