# Basic-XML-Generation-Library
XML Generation Library in kotlin - University Institute of Lisbon

### 1. Data model description

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

## 2. how to use the data model

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



## 2. how to use automatic inference (Phase 2)
