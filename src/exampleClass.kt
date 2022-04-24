@XmlName("STUDENT")
data class Student(
    @XmlTagContent
    val number: Int,
    val name: String,
    @XmlIgnore
    val secondName: String,
    val type: StudentType? = null
)

enum class StudentType {
    Bachelor, Master, Doctoral
}