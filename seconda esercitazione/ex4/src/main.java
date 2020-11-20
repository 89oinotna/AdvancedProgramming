
import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class main {
/*the properties and their capabilities (read-only, read-write);
the events it permits to subscribe.*/
    public static void main(String[] args){
        if(args.length<1){
            return;
        }
        Class<?> inspect;
        try {
            inspect=Class.forName(args[0]);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        inspectProperties(inspect);
        inspectEvents(inspect);
    }

    public static void inspectProperties(Class<?> c){
        Field[] fields=c.getDeclaredFields();

        for(Field f: fields){
            System.out.format("Field name: %s %s %s \n", f.getName(), Modifier.toString(f.getModifiers()), f.getType().toGenericString());
        }
    }

    public static void inspectEvents(Class<?> c){
    }
}
