import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

abstract class MapReduce<K, V, IK extends Comparable<IK>, IV, R> {
    /*
    1) Assign Map -> Combine
    2) Sorting and Partitioning (For parallel usage)
    3) Assign Reduce
    */

    /**
     * Combines all map results
     * grouping values by the provided comparator function
     * @param l
     * @return
     */
    public final Stream<Pair<IK, List<IV>>> combine(Stream<Pair<IK, IV>> l){
        Stream<Pair<IK, List<IV>>> res= Stream.<Pair<IK, List<IV>>>builder().build();

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
        //List<Pair<IK, List<IV>>> a=res.collect(toList());
        return res;
    }

    protected abstract Stream<Pair<K, V>> read();
    protected abstract void write(Stream<Pair<IK, R>> r);
    protected abstract Stream<Pair<IK, IV>> map(Stream<Pair<K, V>> s);
    protected abstract Stream<Pair<IK, R>> reduce(Stream<Pair<IK, List<IV>>> in);
    protected abstract int compare(IK v1, IK v2);


}
