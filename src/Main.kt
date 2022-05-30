
fun main() {
    val b = Book("sds","sda")
    val s1 = Student(7, b,"Cristiano", "Ronaldo", StudentType.Doctoral)


    var root = Entity("default name",null)
    root.createXMLObject(s1, root)
    var xml = Xml(root,"1.0","UTF-8", "no")

    println(xml.serialize())


    val x = xml.search(::innerTextLonger)
}

// Accept criteria
fun nameStartsWith (n:EntityConcrete) = n.name.startsWith("name")
fun innerTextLonger (n:EntityConcrete) = n.innerText.length > 6

