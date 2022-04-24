[1mdiff --git a/src/Entity.kt b/src/Entity.kt[m
[1mindex e9b712d..3ceb69b 100644[m
[1m--- a/src/Entity.kt[m
[1m+++ b/src/Entity.kt[m
[36m@@ -1,3 +1,5 @@[m
[32m+[m[32mimport java.io.File[m
[32m+[m
 abstract class EntityAbstract(val name: String, val parent: Entity? = null) {[m
     init {[m
         parent?.children?.add(this)[m
[36m@@ -21,14 +23,14 @@[m [mclass Entity(name: String, parent: Entity? = null) : EntityAbstract(name, parent[m
         v.endVisit(this)[m
     }[m
 [m
[31m-    // Search for entity by name, TODO search by creteria[m
[31m-    fun search(name: String): EntityConcrete?{[m
[32m+[m[32m    fun search(accept: (EntityConcrete) -> Boolean = {true}): List<EntityConcrete>{[m
[32m+[m[32m        val concreteEntitiesList = mutableListOf<EntityConcrete>()[m
         var entity: EntityConcrete? = null[m
 [m
         val searchVisitor = object : Visitor {[m
             override fun visit(e: EntityConcrete) {[m
[31m-                if (e.name == name)[m
[31m-                    entity = e[m
[32m+[m[32m                if (accept(e))[m
[32m+[m[32m                    concreteEntitiesList.add(e)[m
             }[m
 [m
             override fun visit(e: Entity): Boolean { // it can't have name to check, so we just if element is not found, if not true to continue[m
[36m@@ -36,7 +38,7 @@[m [mclass Entity(name: String, parent: Entity? = null) : EntityAbstract(name, parent[m
             }[m
         }[m
         this.accept(searchVisitor)[m
[31m-        return entity[m
[32m+[m[32m        return concreteEntitiesList[m
     }[m
 [m
     fun serialization() : String{[m
[36m@@ -84,4 +86,3 @@[m [minterface Visitor {[m
 }[m
 [m
 [m
[31m-[m
[1mdiff --git a/src/Main.kt b/src/Main.kt[m
[1mindex 36a64fc..6c73af2 100644[m
[1m--- a/src/Main.kt[m
[1m+++ b/src/Main.kt[m
[36m@@ -10,7 +10,12 @@[m [mfun main() {[m
     val eSub2 = EntityConcrete("nameSub2Sub1","textBSub2",root)[m
     val eSub2Sub1 = Entity("partanInParent",root)[m
 [m
[31m-    val x = root.search("nameSub2Sub1")[m
[32m+[m[32m    val x = root.search(::innerTextLonger)[m
     xml.printModel()[m
 [m
[31m-}[m
\ No newline at end of file[m
[32m+[m[32m}[m
[32m+[m
[32m+[m[32m// Accept criteria[m
[32m+[m[32mfun nameStartsWith (n:EntityConcrete) = n.name.startsWith("name")[m
[32m+[m[32mfun innerTextLonger (n:EntityConcrete) = n.innerText.length > 6[m
[41m+[m
