


fun main() {
    val root = Entity("name",null)
    root.attributes["Id"] = "1"
    root.attributes["class"] = "big"
    var xml = Xml("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>", root)
    val eSub1 = EntityConcrete("nameSub1","textBSub1",root)
    val eSub2 = EntityConcrete("nameSub2Sub1","textBSub2",root)
    val eSub2Sub1 = Entity("partanInParent",root)

    val x = root.search("nameSub2Sub1")
    xml.printModel()

}