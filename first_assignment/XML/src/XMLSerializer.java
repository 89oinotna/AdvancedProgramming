import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Objects;

public class XMLSerializer {


    void serialize(Object[] arr, String fileName){
        StringBuilder sb=new StringBuilder();
        for(Object a:arr){
            if (Objects.isNull(a)) {
                throw new RuntimeException("The object to serialize is null");
            }
            Class<?> clazz=a.getClass();
            if(!clazz.isAnnotationPresent(XMLable.class)){
                throw new RuntimeException("The class "
                        + clazz.getSimpleName()
                        + " is not annotated with XMLable");
            }
            HashMap<String, Fieldd> map=inspect(a, clazz);
            sb.append();
        }
    }

    private class Fieldd{
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

    private HashMap<String, Fieldd> inspect(Object a, Class<?> clazz){
        HashMap<String, Fieldd> map=new HashMap<>();
        Field[] fields=clazz.getDeclaredFields();
        for(Field field:fields){
            XMLField ann=field.getAnnotation(XMLField.class);
            if(ann!=null){
                try {
                    map.put("".equals(ann.name())?field.getName():ann.name(),
                            new Fieldd(ann.type(),field.get(a)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }
}
