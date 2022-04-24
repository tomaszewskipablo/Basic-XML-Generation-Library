import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class Xml constructor( val header: String){

    var root = Entity("",null)
    fun serialize(o: Any){
        val obj = o::class
        root = Entity(tableName(obj).toString(),null)
        obj.declaredMemberProperties.forEach {
            if(!Ignore(it)) {
                if (true) // is primitive type
                    EntityConcrete(fieldName(it), innerText(it), root)
                else    // If object type -> Entity, we need to create next EntityConcrete as children from that Entity
                    Entity(fieldName(it),root)
            }
        }
    }

    fun printModel(){
        println(header)
        println(root.serialization())
    }

    fun search(accept: (EntityConcrete) -> Boolean = {true}): List<EntityConcrete> {
    return root.search(::innerTextLonger)
    }

    private fun tableName(c: KClass<*>) =
        if(c.hasAnnotation<XmlName>()) c.findAnnotation<XmlName>()!!.text
        else c.simpleName

    private fun fieldName(c: KProperty<*>) =
        if(c.hasAnnotation<XmlName>()) c.findAnnotation<XmlName>()!!.text
        else c.name

    private fun innerText(c: KProperty<*>) =
        if(c.hasAnnotation<XmlTagContent>()) c.findAnnotation<XmlTagContent>()!!.text
        else ""

    private fun Ignore(c: KProperty<*>) =
        c.hasAnnotation<XmlIgnore>()

}

