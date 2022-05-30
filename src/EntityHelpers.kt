import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf

fun tableName(c: KClass<*>) =
    if(c.hasAnnotation<XmlName>()) c.findAnnotation<XmlName>()!!.text
    else c.simpleName

fun fieldName(c: KProperty<*>) =
    if(c.hasAnnotation<XmlName>()) c.findAnnotation<XmlName>()!!.text
    else c.name

fun innerText(c: KProperty<*>, o:Any) =
    c.hasAnnotation<XmlTagContent>()

fun Ignore(c: KProperty<*>) =
    c.hasAnnotation<XmlIgnore>()


fun KClassifier?.isEnum() = this is KClass<*> && this.isSubclassOf(Enum::class)
fun KClassifier?.isCollection() = this is KClass<*> && this.isSubclassOf(Collection::class)