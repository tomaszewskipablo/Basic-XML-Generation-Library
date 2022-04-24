


fun main() {
    var xml = Xml("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>")
/*    root.attributes["Id"] = "1"
    root.attributes["class"] = "big"
    var xml = Xml("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>", root)
    val eSub1 = EntityConcrete("nameSub1","textBSub1",root)
    val eSub2 = EntityConcrete("nameSub2Sub1","textBSub2",root)
    val eSub2Sub1 = Entity("partanInParent",root)*/

    val s1 = Student(7, "Cristiano", "Ronaldo", StudentType.Doctoral)

    xml.serialize(s1)
    val x = xml.search(::innerTextLonger)

    xml.printModel()

}

// Accept criteria
fun nameStartsWith (n:EntityConcrete) = n.name.startsWith("name")
fun innerTextLonger (n:EntityConcrete) = n.innerText.length > 6

