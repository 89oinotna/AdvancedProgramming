import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;
public class XMLSerializer {

    /**
     * Serialize an Array of Object of the same class and write
     * the XML serialization in a file.
     * @param arr array of object with same type
     * @param fileName name of the file in which you want to save XML
     * @throws IOException {@link FileWriter#FileWriter(String)}
     */
    public static void serialize(Object[] arr, String fileName) throws IOException {
        StringBuilder sb=new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n").append("<root>\n");
        for(Object obj:arr){
            if (Objects.isNull(obj)) {
                throw new RuntimeException("The object to serialize is null");
            }
            Class<?> clazz=obj.getClass();
            if(!clazz.isAnnotationPresent(XMLable.class)){
                throw new RuntimeException("The class "
                        + clazz.getSimpleName()
                        + " is not annotated with XMLable");
            }
            sb.append("\t<").append(clazz.getSimpleName()).append(">\n");
            HashMap<String, Fieldd> map=inspect(obj, clazz);
            sb.append(
                    map.entrySet()
                    .stream()
                    .map(entry ->{
                                    if(entry.getValue().getValue()==null){
                                        return "\t\t<"+entry.getKey()+" type=\""+entry.getValue().getType()+"\"/>";
                                    }
                                    else{
                                        return "\t\t<"+entry.getKey()+" type=\""+entry.getValue().getType()+"\">"
                                                + entry.getValue().getValue()+ "</"+entry.getKey()+">";
                                    }
                            }

                    )
                    .collect(Collectors.joining("\n"))
            );
            sb.append("\n\t</").append(clazz.getSimpleName()).append(">\n").append("</root>");

        }
        System.out.println(sb.toString());
        BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
        out.write(sb.toString());
        try {
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }


    /**
     * Used to represent a simpler Field instance
     */
    private static class Fieldd{
        String type;
        Object value;

        public Fieldd(String type, Object value) {
            this.type = type;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }


    }

    /**
     * Support method to serialize that inspect a single object
     * @param obj object to inspect
     * @param clazz class of the object to inspect
     * @return an HashMap<String, Fieldd> representing the object fields
     * in which the key is the field name and the value is type of
     * {@link Fieldd}
     */
    private static HashMap<String, Fieldd> inspect(Object obj, Class<?> clazz){
        HashMap<String, Fieldd> map=new HashMap<>();
        Field[] fields=clazz.getDeclaredFields();
        for(Field field:fields){
            field.setAccessible(true);
            XMLfield ann=field.getAnnotation(XMLfield.class);
            if(ann!=null){
                try {
                    map.put("".equals(ann.name())?field.getName():ann.name(),
                            new Fieldd(ann.type(),field.get(obj)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }


    /**
     * Deserialize a XML file, containing XMLable objects, given it's path
     * @param filePath path of the xml
     * @return Initialized array with XMLable object in the file
     * @throws IOException
     * @throws SAXException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static Object[] deserialize(String filePath) throws IOException, SAXException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        Document doc=readXMLFile(filePath);
        doc.getDocumentElement().normalize();
        NodeList nl=doc.getDocumentElement().getChildNodes();
        Object[] obArr= new Object[nl.getLength()];
        for(int i=0; i<nl.getLength(); i++){
            if(nl.item(i).getNodeType() == Node.ELEMENT_NODE)
                obArr[i]=objectFromNode(nl.item(i));
        }
        return obArr;
    }

    /**
     * Construct the XMLable Object corresponding to the given node
     * @param item Node representing the XMLable
     * @return Instantiated Object
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private static Object objectFromNode(Node item) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String nodeName=item.getNodeName();
        Class<?> clazz=Class.forName(nodeName);
        if(!clazz.isAnnotationPresent(XMLable.class)){
            throw new RuntimeException("The class "
                    + clazz.getSimpleName()
                    + " is not annotated with XMLable");
        }
        Object obj=null;
        try{
            obj=Objects.requireNonNull(clazz.getDeclaredConstructor()).newInstance();
        }catch(NullPointerException e){
            throw new RuntimeException("The class "
                    + clazz.getSimpleName()
                    + " has not a constructor with no args");
        }
        NodeList nl=item.getChildNodes();
        HashMap<String, Element> map=new HashMap<>();
        for(int i=0; i<nl.getLength(); i++){
            if(nl.item(i).getNodeType() == Node.ELEMENT_NODE){
                map.put(nl.item(i).getNodeName(), (Element)nl.item(i));
            }
        }
        Field[] fields=clazz.getDeclaredFields();
        for(Field field:fields){
            Element f=map.get(getFieldElementName(field, clazz));
            if(f==null){
                field.set(obj, null);
            }else{
                field.set(obj, objectFromElement(f));
            }

        }
        return obj;
    }

    /**
     * Get the corresponding name of this XMLfield
     * @param field field to check for name
     * @param clazz class in which the field is declared
     * @return XMLfield name of the field
     */
    private static String getFieldElementName(Field field, Class<?> clazz){
        field.setAccessible(true);
        if(Modifier.isStatic(field.getModifiers())){
            throw new RuntimeException("The class "
                    + clazz.getSimpleName()
                    + " has a static field");
        }
        XMLfield ann=field.getAnnotation(XMLfield.class);
        if(ann!=null){
            return "".equals(ann.name())?field.getName():ann.name();
        }
        else{
            throw new RuntimeException("The class "
                    + clazz.getSimpleName()
                    + " has a non annotated XMLfield: "+field.getName());
        }
    }

    /**
     * Instantiate Object from an element in the XML corresponding to an XMLfield
     * @param f Element representing the XMLfield
     * @return Instantiated Object of type=XMLfield.type
     * @throws ClassNotFoundException
     */
    private static Object objectFromElement(Element f) throws ClassNotFoundException {
        String type=f.getAttribute("type");
        Class<?> fieldClazz=myForName(type);
        if(!(fieldClazz.isPrimitive()||fieldClazz.equals(String.class))){
            throw new RuntimeException("Field type is not primitive/String: "
                    + fieldClazz.getSimpleName());
        }
        Object fieldValue=null;
        if(f.hasChildNodes()) {
            //use PropertyEditor to correctly type convert String -> Class Type
            PropertyEditor editor = PropertyEditorManager.findEditor(fieldClazz);
            editor.setAsText(f.getTextContent());
            fieldValue= editor.getValue();
        }
        return fieldValue;
    }

    /**
     * Custom method to get Class type also for primitive type
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    private static Class<?> myForName(String className) throws ClassNotFoundException {
        //TODO: it is possible to do a lookup on all the available packages
        //https://stackoverflow.com/questions/1308961/how-do-i-get-a-list-of-packages-and-or-classes-on-the-classpath
        switch(className){
            case "void":
                return void.class;
            case "boolean":
                return boolean.class;
            case "char":
                return char.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            default:
                return Class.forName("java.lang."+className);
        }

    }

    /**
     * Read the xml file given filePath
     * @param filePath Path of the file to read
     * @return Document representing the parsed xml file
     * @throws IOException
     * @throws SAXException
     */
    private static Document readXMLFile(String filePath) throws IOException, SAXException {
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        StringBuilder xml=new StringBuilder();
        DocumentBuilder builder = null;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            String line;
            while ((line=in.readLine())!=null) {
                xml.append(line);
            }
        } finally{
            in.close();
        }
        ByteArrayInputStream input = new ByteArrayInputStream(xml.toString().getBytes(StandardCharsets.UTF_8));

        return Objects.requireNonNull(builder).parse(input);


    }

}
