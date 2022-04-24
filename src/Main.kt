


fun main() {
    val root = Entity("name",null)
    val eSub1 = EntityConcrete("nameSub1","textBSub1",root)
    val eSub2 = EntityConcrete("nameSub2Sub1","textBSub2",root)
    val eSub2Sub1 = Entity("partanInParent",root)
    val xml = EntityConcrete("nameSub2Sub1", "textBSub2", eSub2Sub1)
    val x = root.search("nameSub2Sub1")
    print("s");

}