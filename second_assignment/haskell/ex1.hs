
module Ex1 where
import Data.List
data ListBag a = LB [(a, Int)] | Empty
  deriving (Show, Eq) 


wf :: Eq a => ListBag a -> Bool
wf Empty=True 
wf (LB [x])=True
wf (LB (x:xs))=False

singleton v=LB [(v,1)]

fromList []=Empty
fromList lst=
    let sorted=Data.List.sort lst in
        LB (foldl f [] lst)
    where
        f [] x=[(x, 1)]
        f ((v, m):acc) x =if x==v then (v, m+1):acc else (x, 1):acc

isEmpty bag=bag==Empty

mul v Empty = 0
mul v (LB [])=0
mul v (LB ((v', m):xs))=if v'==v then m else mul v (LB xs)

toList Empty=[]
toList (LB lst)=
    foldl (\acc (v,m) -> f v m acc) [] lst
    where 
        f v 0 acc=v:acc
        f v m acc=if m<0 then acc else f v (m-1) (v:acc)

sumBag :: (Ord a1, Ord a2) => [a1] -> [a2] -> ListBag a3
sumBag bag bag'=
    let a=Data.List.sort bag in
    let b=Data.List.sort bag' in
    LB (f [] a b)
    where 
        f acc [] []=acc

instance Foldable ListBag where