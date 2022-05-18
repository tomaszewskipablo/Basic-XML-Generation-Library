// define XML entity and attribute names, different from the identifiers in Kotlin code
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class XmlName(val text: String)


// mark object properties to ignore when generating XML
@Target(AnnotationTarget.PROPERTY)
annotation class XmlIgnore

// define which object property will be used for the content between the tags
@Target(AnnotationTarget.PROPERTY)
annotation class XmlTagContent
