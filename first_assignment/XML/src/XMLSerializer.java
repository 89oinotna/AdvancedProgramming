import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;
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
            sb.append("<").append(clazz.getSimpleName()).append(">\n");
            HashMap<String, Fieldd> map=inspect(obj, clazz);
            sb.append(
                    map.entrySet()
                    .stream()
                    .map(entry ->
                            "\t<"+entry.getKey()+" type=\""+entry.getValue().getType()+"\">"
                            + entry.getValue().getValue() + "</"+entry.getKey()+">"
                    )
                    .collect(Collectors.joining("\n"))
            );
            sb.append("\n</").append(clazz.getSimpleName()).append(">\n");

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
     *
     * @param filePath
     */
    public static void deserialize(String filePath) throws FileNotFoundException {
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        StringBuilder xml=new StringBuilder();
        try {
            String line;
            while ((line=in.readLine())!=null) {
                xml.append(line);
            }
        }catch (IOException e){

        }finally{
            try {
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        Arrays.stream(xml.toString().split("(?<=(<\\w*>))"))
                .forEach(item-> {
                    System.out.println(item);;
                });
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
}
