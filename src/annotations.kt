/**
 * This meta-annotation determines whether class or object name has different name then original one.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class XmlName(val text: String)


/**
 * This meta-annotation determines whether object is omitted when generating XML object.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class XmlIgnore


/**
 * This meta-annotation determines whether object is treated as EntityConcrete class, applies only to non-dataclasses objects.
 * Results in serializing by printing object property as the content between the tags.
 * @see EntityConcrete
 */
@Target(AnnotationTarget.PROPERTY)
annotation class XmlTagContent
