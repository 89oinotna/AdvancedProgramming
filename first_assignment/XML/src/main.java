import java.io.IOException;

public class main {
    public static void main(String[] args){
        Student s=new Student();
        Student s1=new Student();
        Student[] ss=new Student[2];
        ss[0]=s;
        ss[1]=s1;
        try {
            XMLSerializer.serialize(ss, "ciao");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
