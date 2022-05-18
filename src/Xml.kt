import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf

class Xml constructor( val header: String){

    var root: Entity? = null;
    fun createXMLObject(o: Any, parent: Entity?){
        val obj = o::class
        if(root == null) {
            root = Entity(tableName(obj).toString(), null)
            createXMLObject(o, root)
        }
        else {
            obj.declaredMemberProperties.forEach {
                if (!Ignore(it)) {
                    if (it.returnType.classifier.isCollection()) // is dataClass or List
                    {
                        createXMLObject(it, Entity(fieldName(it), parent))
                    } else if (it.returnType.classifier.isEnum())    // Enum
                        EntityConcrete(fieldName(it), innerText(it,o), parent)
                    else    // Primitive type
                        EntityConcrete(fieldName(it), innerText(it,o), parent)
                }
            }
        }
    }

    fun printModel(){
        println(header)
        println(root!!.serialization())
    }

    fun search(accept: (EntityConcrete) -> Boolean = {true}): List<EntityConcrete> {
    return root!!.search(::innerTextLonger)
    }

    private fun tableName(c: KClass<*>) =
        if(c.hasAnnotation<XmlName>()) c.findAnnotation<XmlName>()!!.text
        else c.simpleName

    private fun fieldName(c: KProperty<*>) =
        if(c.hasAnnotation<XmlName>()) c.findAnnotation<XmlName>()!!.text
        else c.name

    private fun innerText(c: KProperty<*>, o:Any) =
        if(c.hasAnnotation<XmlTagContent>()) c.findAnnotation<XmlTagContent>()!!.text
        else c.call(o).toString()

    private fun Ignore(c: KProperty<*>) =
        c.hasAnnotation<XmlIgnore>()


    fun KClassifier?.isEnum() = this is KClass<*> && this.isSubclassOf(Enum::class)
    fun KClassifier?.isCollection() = this is KClass<*> && this.isSubclassOf(Collection::class)
}

