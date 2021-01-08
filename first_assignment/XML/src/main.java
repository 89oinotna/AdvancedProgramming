import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.String;


public class main {
    public static void main(String[] args){
        Student s=new Student("ciao", null, 10);
        Student s1=new Student();
        Student[] ss=new Student[1];
        ss[0]=s;
        //Sss[1]=s1;
        try {
            XMLSerializer.serialize(ss, "ciao");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Object[] des=XMLSerializer.deserialize("ciao");
            for(Object stud:des){
                System.out.println(((Student) stud).firstName);
            }
        } catch (IOException | SAXException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
