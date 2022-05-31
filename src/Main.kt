import kotlin.test.assertEquals

fun main() {
    val b = Book("sds","sda")
    val s1 = Student(7, b,"Cristiano", "Ronaldo", StudentType.Doctoral)


    var root = Entity("default name",null)
    root.createXMLObject(s1, root)
    var xml = Xml(root,"1.0","UTF-8", "no")

    println(xml.serialize())

/*    assertEquals(xml.serialize(), "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
            "<STUDENT lista2=\"[Ajay, Vijay, Prakash]\" name=\"Cristiano\" type=\"Doctoral\">\n" +
            "\t<BOOK secondName=\"sda\">\n" +
            "\t\t<name>sds</name>\n" +
            "\t</BOOK>\n" +
            "\t<STUDENT>\n" +
            "\t</STUDENT>\n" +
            "\t<lista>Ajay</lista>\n" +
            "\t<lista>Vijay</lista>\n" +
            "\t<lista>Prakash</lista>\n" +
            "\t<number>7</number>\n" +
            "</STUDENT>")*/

    val x = xml.search(::innerTextLonger)
}

// Accept criteria
fun nameStartsWith (n:EntityConcrete) = n.name.startsWith("name")
fun innerTextLonger (n:EntityConcrete) = n.innerText.length > 6

