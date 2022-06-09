import org.junit.*
import kotlin.test.assertEquals

class Tests {
    val b = Book("sds", "sda")
    val s1 = Student(7, b, "Cristiano", "Ronaldo", StudentType.Doctoral)
    var root = Entity("default name", null)
    var xml = Xml(root, "1.0", "UTF-8", "no")

    @Before
    fun CreateXMLObject() {
        root.createXMLObject(b, root)
    }

    @Test
    fun testCreateXMLObject() {
        assertEquals(
            root.serialization().replace("\n", "").replace("\t", ""),
            "<BOOK secondName=\"sda\"><name>sds</name></BOOK>"
        )
    }

    @Test
    fun testAddNewEntity() {
        root.addEntity("newEtity")
        assertEquals(
            xml.serialize().replace("\n", "").replace("\t", ""),
            ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
                    "<BOOK secondName=\"sda\">\n" +
                    "\t<name>sds</name>\n" +
                    "\t<newEtity>\n" +
                    "\t</newEtity>\n" +
                    "</BOOK>").replace("\n", "").replace("\t", "")
        )
    }

    @Test
    fun testRenameEntity() {
        root.addEntity("newEtity")
        assertEquals(
            xml.serialize().replace("\n", "").replace("\t", ""),
            ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
                    "<BOOK secondName=\"sda\">\n" +
                    "\t<name>sds</name>\n" +
                    "\t<newEtity>\n" +
                    "\t</newEtity>\n" +
                    "</BOOK>").replace("\n", "").replace("\t", "")
        )
    }

    @Test
    fun testNewCreateXMLObject() {
        root.children.clear()
        root.createXMLObject(s1, root)
        assertEquals(
            xml.serialize().replace("\n", "").replace("\t", ""),
            ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
                    "<STUDENT lista2=\"[Ajay, Vijay, Prakash]\" name=\"Cristiano\" type=\"Doctoral\" secondName=\"sda\">\n" +
                    "\t<BOOK secondName=\"sda\">\n" +
                    "\t\t<name>sds</name>\n" +
                    "\t</BOOK>\n" +
                    "\t<lista>Ajay</lista>\n" +
                    "\t<lista>Vijay</lista>\n" +
                    "\t<lista>Prakash</lista>\n" +
                    "\t<number>7</number>\n" +
                    "</STUDENT>").replace("\n", "").replace("\t", "")
        )
    }

    @Test
    fun testMorefunctinos() {
        root.children.clear()
        root.createXMLObject(s1, root)
        root.addEntity("newEtity").addSection("new", "section").addAttribute("new", "attribute").addEntity("ss")
            .addEntity("ssaa").addAttribute("ee", "empty")
        assertEquals(
            xml.serialize().replace("\n", "").replace("\t", ""),
            ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
                    "<STUDENT lista2=\"[Ajay, Vijay, Prakash]\" name=\"Cristiano\" type=\"Doctoral\" secondName=\"sda\">\n" +
                    "\t<BOOK secondName=\"sda\">\n" +
                    "\t\t<name>sds</name>\n" +
                    "\t</BOOK>\n" +
                    "\t<lista>Ajay</lista>\n" +
                    "\t<lista>Vijay</lista>\n" +
                    "\t<lista>Prakash</lista>\n" +
                    "\t<number>7</number>\n" +
                    "\t<newEtity new=\"attribute\">\n" +
                    "\t\t<new>section</new>\n" +
                    "\t\t<ss>\n" +
                    "\t\t\t<ssaa ee=\"empty\">\n" +
                    "\t\t\t</ssaa>\n" +
                    "\t\t</ss>\n" +
                    "\t</newEtity>\n" +
                    "</STUDENT>\n").replace("\n", "").replace("\t", "")
        )
    }
        @Test
        fun testManipulationOnRoot() {
            root.children.clear()
            root.createXMLObject(s1, root)

            root.renameEntity("newRootName")
            root.addSection("newSection", "Text")
            root.addAttribute("newAttribute", "Text,@!.,,,<><><<><")

            assertEquals(
                xml.serialize().replace("\n", "").replace("\t", ""),
                ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
                        "<newRootName newAttribute=\"Text,@!.,,,&amp;lt;&gt;&amp;lt;&gt;&amp;lt;&amp;lt;&gt;&amp;lt;\" lista2=\"[Ajay, Vijay, Prakash]\" name=\"Cristiano\" type=\"Doctoral\" secondName=\"sda\">\n" +
                        "\t<BOOK secondName=\"sda\">\n" +
                        "\t\t<name>sds</name>\n" +
                        "\t</BOOK>\n" +
                        "\t<lista>Ajay</lista>\n" +
                        "\t<lista>Vijay</lista>\n" +
                        "\t<lista>Prakash</lista>\n" +
                        "\t<number>7</number>\n" +
                        "\t<newSection>Text</newSection>\n" +
                        "</newRootName>").replace("\n", "").replace("\t", "")
            )
        }

        @Test
        fun testRmoving() {
            root.children.clear()
            root.createXMLObject(s1, root)

            root.removeAttribute("name")
            root.removeSection("number")

            assertEquals(
                xml.serialize().replace("\n", "").replace("\t", ""),
                ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
                        "<STUDENT lista2=\"[Ajay, Vijay, Prakash]\" type=\"Doctoral\" secondName=\"sda\">\n" +
                        "\t<BOOK secondName=\"sda\">\n" +
                        "\t\t<name>sds</name>\n" +
                        "\t</BOOK>\n" +
                        "\t<lista>Ajay</lista>\n" +
                        "\t<lista>Vijay</lista>\n" +
                        "\t<lista>Prakash</lista>\n" +
                        "</STUDENT>").replace("\n", "").replace("\t", "")
            )
        }

    @Test
    fun testRmovingEntity() {
        root.children.clear()
        root.createXMLObject(s1, root)
        root.removeEntity("BOOK")

        assertEquals(
            xml.serialize().replace("\n", "").replace("\t", ""),
            ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
                    "<STUDENT lista2=\"[Ajay, Vijay, Prakash]\" name=\"Cristiano\" type=\"Doctoral\" secondName=\"sda\">\n" +
                    "\t<lista>Ajay</lista>\n" +
                    "\t<lista>Vijay</lista>\n" +
                    "\t<lista>Prakash</lista>\n" +
                    "\t<number>7</number>\n" +
                    "</STUDENT>").replace("\n", "").replace("\t", "")
        )
    }

    @Test
    fun testChangingContentSection() {
        root.children.clear()
        root.createXMLObject(s1, root)
        root.changeSectionText("number","HERE Test, no more numbers")

        assertEquals(
            xml.serialize().replace("\n", "").replace("\t", ""),
            ("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
                    "<STUDENT lista2=\"[Ajay, Vijay, Prakash]\" name=\"Cristiano\" type=\"Doctoral\" secondName=\"sda\">\n" +
                    "\t<BOOK secondName=\"sda\">\n" +
                    "\t\t<name>sds</name>\n" +
                    "\t</BOOK>\n" +
                    "\t<lista>Ajay</lista>\n" +
                    "\t<lista>Vijay</lista>\n" +
                    "\t<lista>Prakash</lista>\n" +
                    "\t<number>HERE Test, no more numbers</number>\n" +
                    "</STUDENT>").replace("\n", "").replace("\t", "")
        )
    }

    @Test
    fun testChangingContentAttribute() {
        root.children.clear()
        root = Entity("root",null)
        root.createXMLObject(s1, root)
        root.changeAttributeText("name","Leo")
        root.changeAttributeText("secondName","Messi")

        assertEquals(
            root.serialization().replace("\n", "").replace("\t", ""),
            ("<STUDENT lista2=\"[Ajay, Vijay, Prakash]\" name=\"Leo\" type=\"Doctoral\" secondName=\"Messi\">\n" +
                    "\t<BOOK secondName=\"sda\">\n" +
                    "\t\t<name>sds</name>\n" +
                    "\t</BOOK>\n" +
                    "\t<lista>Ajay</lista>\n" +
                    "\t<lista>Vijay</lista>\n" +
                    "\t<lista>Prakash</lista>\n" +
                    "\t<number>7</number>\n" +
                    "</STUDENT>").replace("\n", "").replace("\t", "")
        )
    }
}