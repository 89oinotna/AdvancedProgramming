
module Ex1
(
    ListBag(LB),
    singleton,
    wf,
    fromList,
    empty,
    isEmpty,
    mul,
    toList,
    sumBag
) where
import Data.Maybe

data ListBag a = LB [(a, Int)]
  deriving (Show, Eq) 

singleton :: Eq a => a -> ListBag a
singleton v=LB [(v,1)]

wf :: Eq a => ListBag a -> Bool
wf (LB [])=True
wf (LB (x:xs))=
    f [] x xs
    where
        f acc x@(v,m) []=isNothing (lookup v acc)
        f acc x@(v,m) (l:ls)=isNothing (lookup v acc) && f (x : acc) l ls


fromList :: Eq a => [a] -> ListBag a
fromList lst=
    LB (groupValues [] lst)
    where 
        groupValues acc []=acc
        -- tail recursive
        groupValues acc (x:xs)=groupValues ((x, 1 + length (filter (== x) xs)):acc) (filter (/= x) xs)

empty :: ListBag a
empty = LB []
    
isEmpty :: ListBag a -> Bool
isEmpty (LB [])=True 
isEmpty (LB _)=False 

mul :: Eq a => a -> ListBag a -> Int
mul v (LB [])=0
mul v (LB ((v', m):xs))=if v'==v then m else mul v (LB xs)

toList :: ListBag a -> [a]
toList (LB [])=[]
toList (LB lst)=
    foldl (\acc (v,m) -> f v m acc) [] lst
    where 
        f v 0 acc=acc
        f v m acc=if m<0 then acc else f v (m-1) (v:acc)

sumBag :: Eq a => ListBag a -> ListBag a -> ListBag a
sumBag (LB []) bag'@(LB b') = bag'
sumBag bag@(LB b) (LB []) = bag
sumBag bag@(LB b) bag'@(LB b') =
    --if wf bag && wf bag' then 
        -- Maybe not so efficient but very simple :P
        fromList (toList bag ++ toList bag') 
    --else Nothing