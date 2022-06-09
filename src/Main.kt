import kotlin.test.assertEquals

fun main() {
    val b = Book("sds","sda")
    val s1 = Student(7, b,"Cristiano", "Ronaldo", StudentType.Doctoral)


    var root = Entity("default name",null)
    root.createXMLObject(b, root)
    var xml = Xml(root,"1.0","UTF-8", "no")

    root.renameEntity("newRootName")
    root.addSection("newSection","Text")
    root.addAttribute("newAttribute","Text,@!.,,,<><><<><")


    val x = xml.search(::innerTextLonger)
}

// Accept criteria
fun nameStartsWith (n:EntityConcrete) = n.name.startsWith("name")
fun innerTextLonger (n:EntityConcrete) = n.innerText.length > 6

