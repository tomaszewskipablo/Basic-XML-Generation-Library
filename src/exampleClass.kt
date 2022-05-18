@XmlName("STUDENT")
data class Student(
    @XmlTagContent
    val number: Int,
    val book: Book,
    //@XmlTagContent("smallNumber")
    val name: String,
    @XmlIgnore
    val secondName: String,
    @XmlTagContent
    val type: StudentType? = null,
    @XmlTagContent
    val lista: List<String> = listOf("Ajay","Vijay","Prakash")
)

@XmlName("BOOK")
data class Book(
    val name: String,
    @XmlIgnore
    val secondName: String,
)

enum class StudentType {
    Bachelor, Master, Doctoral
}