# Basic-XML-Generation-Library
XML Generation Library in kotlin - University Institute of Lisbon

## 1. Data model description

Data model implements composit design pattern. 

<img src="https://user-images.githubusercontent.com/32485994/171930514-5445b278-2927-4dc4-b824-efec91b1f97a.png" width=40% height=40%>

**Entity** implements EntityAbstract class, can store Entities and EntityContrete elements in a list of children.
It also has a dictionary that can keep attributes. Entity represents a Tag in XML.  
To create Entity object: 
```
var root = Entity("default name", null)
```  
Example entity in serialized to XML:  
```
<Entity attribute1="text1" attribute2="text2">
      <childEntity></childEntity>
</Entity>
```

**EntityContrete** implements EntityAbstract class, can store insideText as a string.
EntityContrete represents a Section in XMl. 
To create EntityContrete object: 
```
EntityConcrete("sectionName", "insideText", parent)
```  
```
<EntityContrete>SomeInsideText</EntityContrete>
```

Both Entity and EntityContrete implements accept method for Visitor pattern.  

## 2. How to use the data model

Function **createXMLObject** can be used to create XML object from object from the memory.  

```
root.createXMLObject(objectToCreateXMLObjectFrom, root)
```
</br>
Entity have manipulation methods for:  </br>
adding new entities, removing entities, renaming Entities  </br>
adding new attributes, removing attributes, renaming attributes  </br>
adding new sections, removing sections, renaming sections  </br></br>

```
root.addEntity("newEtity")
```
</br>

Functions can be used to create or manipulate XML object.    
</br>
fun **serialization()** returns can be called on entity element to serialize Entity object to XML. Function returns string.  
</br>
Special characters are serialzied to their text representation.  
List of special characters with their text representations:  
```
"<"  ->  "&lt;"
"&"  ->  "&amp;"
">"  ->  "&gt;"
"\"" ->  "&quot;"
"\'" ->  "&apos;"
```
</br>
Root Entity can be injeted into xml object that wrapps Entity and contains elements representing xml header.

```
var xml = Xml(root, "1.0", "UTF-8", "no")
```

Xml class contains **serialize()** function to serialize root element with the header before.



## 3. How to use automatic inference
Automatic inferance consist of 3 annonations: </br></br>
@XmlIgnore</br>
This meta-annotation determines whether object is treated as EntityConcrete class, applies only to non-dataclasses objects. 
Results in serializing by printing object property as the content between the tags.</br></br>
@XmlTagContent</br>
This meta-annotation determines whether object is omitted when generating XML object.</br></br>
@XmlName("New Name")</br>
This meta-annotation determines whether class or property name has different name then original one. In thsi example class or property in XML representation object would be "New Name".</br>

Example use of automatic inference </br>
```
@XmlName("STUDENT")
data class Student(
    @XmlTagContent
    val number: Int,
    val book: Book,
    val name: String,
    @XmlIgnore
    val secondName: String,
    @XmlTagContent
    val type: StudentType? = null,
    @XmlTagContent
    val lista: List<String> = listOf("Ajay","Vijay","Prakash"),
)
```

## 4. How to use automatic inference
In project in GUI package, Graphical app can be found. App lets user create and edit XML object, by using one of the 9 option from option menu. Option manu can be dislplayed by clicking right mouse click.</br>

<img src="https://user-images.githubusercontent.com/32485994/171948434-683b8ef0-a130-4238-8a6c-3ee787f98803.png" width=40% height=40%>

Entity can be serialized to a file or console.</br>

GUI lets user load default hard coded entity.</br>
<img src="https://user-images.githubusercontent.com/32485994/171947866-e1f091d5-bf0e-4d55-86a5-d6121fa06e3e.png" width=40% height=40%>

