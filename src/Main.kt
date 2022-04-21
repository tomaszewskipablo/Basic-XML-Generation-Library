


fun main() {
    val root = Entity("name",null)
    val eSub1 = EntityConcrete("nameSub1","textBSub1",root)
    val eSub2 = EntityConcrete("nameSub2","textBSub2",root)
    val eSub2Sub1 = Entity("nameSub2Sub1",root)
    val xml = Xml("header", root)
    print("s");

}