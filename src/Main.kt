


fun main() {
    val e = EntityConcrete("name","textB",null)
    val eSub1 = EntityConcrete("nameSub1","textBSub1",e)
    val eSub2 = EntityConcrete("nameSub2","textBSub2",e)
    val eSub2Sub1 = EntityConcrete("nameSub2Sub1","textBSub2Sub1",eSub2)
    val xml = Xml("header", e)
    print("s");

}