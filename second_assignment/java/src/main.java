import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class main {

    public static void main(String[] args){
        File dst= new File("dst.txt");
        try {
            if(dst.createNewFile()) {
                WordCount wc = new WordCount(Paths.get("")
                        .toAbsolutePath(), dst);
                var res=wc.map(wc.read());
                var res1=wc.reduce(wc.combine(res));
                wc.write(res1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
