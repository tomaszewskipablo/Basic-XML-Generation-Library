@XmlName("STUDENT")
data class Student(
    @XmlTagContent("bigNumber")
    val number: Int,
    @XmlTagContent("smallNumber")
    val name: String,
    @XmlIgnore
    val secondName: String,
    val type: StudentType? = null
)

enum class StudentType {
    Bachelor, Master, Doctoral
}