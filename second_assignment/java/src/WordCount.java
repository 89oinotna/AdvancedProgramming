import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class WordCount extends MapReduce<String, List<String>, String, Integer, Integer> {
    private final Reader reader;
    private final File dst;

    public WordCount(Path rootPath, File dst){
        reader=new Reader(rootPath);
        this.dst=dst;
    }

    @Override
    protected Stream<Pair<String, List<String>>> read() {
        try {
            return reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Stream.empty();
    }

    @Override
    protected void write(Stream<Pair<String, Integer>> r) {
        try {
            Writer.write(dst, r);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Stream<Pair<String, Integer>> map(Stream<Pair<String, List<String>>> s) {
        return s.parallel()
                .flatMap(WordCount::countWords);

    }

    /**
     * retrieve a stream for a file
     * @param file
     * @return
     */
    private static Stream<Pair<String, Integer>> countWords(Pair<String, List<String>> file){
        return file.value
                .parallelStream()
                .map(s -> s.split("\\W+"))
                .flatMap(
                        line -> Arrays.stream(line)
                        .parallel()
                        .filter(word -> word.length()>3)
                        .collect(
                            Collectors.toConcurrentMap(
                                    x->x,
                                    x -> 1,
                                    (s, e) -> s+e
                            )
                        ).entrySet()
                        .parallelStream()
                        .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
                );
    }

    @Override
    protected Stream<Pair<String, Integer>> reduce(Stream<Pair<String, List<Integer>>> in) {
        var r=in.parallel()
                .map(
                        p -> new Pair<>(p.getKey(),
                                p.getValue().parallelStream().mapToInt(Integer::intValue).sum())
                );
       // var a=r.collect(toList());
        return r.sequential();
    }





    @Override
    protected int compare(String v1, String v2) {
        return v1.compareTo(v2);
    }
}