import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class main {
    public static void main(String[] args){
        String[] ss=new String[args.length-1];
        if (args.length - 1 >= 0) System.arraycopy(args, 1, ss, 1, args.length - 1);
        Arrays.sort(ss);
        for(String s: ss) System.out.println(s);
    }
}
