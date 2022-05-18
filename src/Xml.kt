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
                        val e = Entity(it.name, parent)
                        val coll = it.call(o) as Collection<*>
                        coll.forEach{
                            if(it != null)
                                createXMLObject(it, e)
                        }
                    } else if (it.returnType.classifier.isEnum())    // Enum
                    {
                        if(innerText(it,o)) {
                            parent!!.attributes[fieldName(it)] = it.call(o).toString()
                        }
                        else{
                            EntityConcrete(fieldName(it), it.call(o).toString(), parent)
                        }
                    }
                    else if(it.call(o)!!::class.isData)
                    {
                        val e = Entity(it.name, parent)
                        createXMLObject(it.call(o)!!::class.javaObjectType.cast(it.call(o)), e)
                    }
                    else    // Primitive type
                    {
                            if(innerText(it,o)) {
                                parent!!.attributes[fieldName(it)] = it.call(o).toString()
                            }
                            else{
                                EntityConcrete(fieldName(it), it.call(o).toString(), parent)
                            }
                    }
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
        if(c.hasAnnotation<XmlTagContent>()) true
        else false

    private fun Ignore(c: KProperty<*>) =
        c.hasAnnotation<XmlIgnore>()


    fun KClassifier?.isEnum() = this is KClass<*> && this.isSubclassOf(Enum::class)
    fun KClassifier?.isCollection() = this is KClass<*> && this.isSubclassOf(Collection::class)
}

