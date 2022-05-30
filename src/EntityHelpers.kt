import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf

/**
 * Retrieves name of object property for dataclasses and lists
 * @see XmlName
 */
fun tableName(c: KClass<*>) =
    if(c.hasAnnotation<XmlName>()) c.findAnnotation<XmlName>()!!.text
    else c.simpleName

/**
 * Retrieves name of object property for attributes, sections and enums
 * @see XmlName
 */
fun fieldName(c: KProperty<*>) =
    if(c.hasAnnotation<XmlName>()) c.findAnnotation<XmlName>()!!.text
    else c.name

/**
 * Retrieves weather property object has XmlTagContent annotation
 * @see XmlTagContent
 */
fun innerText(c: KProperty<*>, o:Any) =
    c.hasAnnotation<XmlTagContent>()

/**
 * Retrieves weather property has XmlIgnore annotation (is ignored for creating xml object)
 * @see Ignore
 */
fun Ignore(c: KProperty<*>) =
    c.hasAnnotation<XmlIgnore>()

/**
 * Retrieves weather object property is enum type
 */
fun KClassifier?.isEnum() = this is KClass<*> && this.isSubclassOf(Enum::class)

/**
 * Retrieves weather object property is Collection
 */
fun KClassifier?.isCollection() = this is KClass<*> && this.isSubclassOf(Collection::class)