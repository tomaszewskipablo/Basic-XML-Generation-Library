@XmlName("STUDENT")
data class Student(
    @XmlTagContent("bigNumber")
    val number: Int,
    //@XmlTagContent("smallNumber")
    val name: String,
    @XmlIgnore
    val secondName: String,
    val type: StudentType? = null,
    val lista: List<String> = listOf("Ajay","Vijay","Prakash")
)

enum class StudentType {
    Bachelor, Master, Doctoral
}