import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.*;

abstract class MapReduce<K, V, IK extends Comparable<IK>, IV, R> {
    /*
    1) Assign Map -> Combine
    2) Sorting and Partitioning (For parallel usage)
    3) Assign Reduce
    */

    /**
     * Template method to perform MapReduce from read -> write
     * @return the reduced stream
     */
    public final Stream<Pair<IK, R>> start(){
        var read=read();
        var mapped=map(read);
        //we could also implement a reduce directly after the map to decrease the size
        var combined=combine(mapped);
        var reduced=reduce(combined);
        write(reduced);
        return reduced;
    }

    //combine is a concrete operation used by the template method

    /**
     * Combines all map results
     * grouping values by the provided comparator function
     * @param l
     * @return
     */
    private Stream<Pair<IK, List<IV>>> combine(Stream<Pair<IK, IV>> l){
        Supplier<TreeMap<IK, List<IV>>> makeTreeMap = () -> new TreeMap<>(this::compare);
        return l.collect(
                makeTreeMap,
                (map, it) -> {
                    // If map dont contains key create the list
                    if (!map.containsKey(it.getKey()))
                        map.put(it.getKey(), new ArrayList<>());

                    // Put value in the corresponding list
                    map.get(it.getKey()).add(it.getValue());
                },
                (m1, m2) -> m2.forEach((k, v) -> {
                    // If m1 dont contains k create the list
                    if (!m1.containsKey(k))
                        m1.put(k, new ArrayList<>());

                    // Merge the two lists
                    m1.get(k).addAll(v);
                })
        )
                .entrySet()
                .parallelStream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()));

        /*Stream<Pair<IK, List<IV>>> res= Stream.<Pair<IK, List<IV>>>builder().build();

        Iterator<Pair<IK, IV>> it=l.sorted((p1, p2) -> compare(p1.getKey(), p2.getKey()))
                .sequential().iterator();
        Pair<IK, IV> last=it.next();
        Pair<IK, ArrayList<IV>> acc=new Pair<>(last.getKey(), new ArrayList<>(Collections.singletonList(last.getValue())));
        do {
            Pair<IK, IV> curr=it.next();
            if(compare(curr.getKey(), last.getKey())==0){
                acc.getValue().add(curr.getValue());
            }
            else{
                res=Stream.concat(res, Stream.of(new Pair<>(acc.getKey(), acc.getValue())));
                acc=new Pair<>(curr.getKey(), new ArrayList<>(Collections.singletonList(last.getValue())));
            }
            last=curr;
        }while(it.hasNext());
        res=Stream.concat(res, Stream.of(new Pair<>(acc.getKey(), acc.getValue())));
        return res;*/
    }

    protected abstract Stream<Pair<K, V>> read();
    protected abstract void write(Stream<Pair<IK, R>> r);
    protected abstract Stream<Pair<IK, IV>> map(Stream<Pair<K, V>> s);
    protected abstract Stream<Pair<IK, R>> reduce(Stream<Pair<IK, List<IV>>> in);
    protected abstract int compare(IK v1, IK v2);


}
