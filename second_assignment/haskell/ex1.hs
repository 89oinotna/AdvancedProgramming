
module Ex1 where
import Data.List
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
fromList []=LB []
fromList lst=
    iter [] lst
    where
        iter acc []=LB acc
        iter acc (l:ls)=
            case g l ls of
                (item, m, lst)->iter ((item, m):acc) lst
         where
            g x lst=foldl (\acc@(v, m, ls) x -> 
                if v==x then 
                    (v, m+1, ls) 
                else (v, m, x:ls)) (x, 0, []) lst

    
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
        f v 0 acc=v:acc
        f v m acc=if m<0 then acc else f v (m-1) (v:acc)

sumBag :: Eq a => ListBag [a] -> ListBag [a] -> Maybe (ListBag [a])
sumBag (LB []) bag'@(LB b') = Just bag'
sumBag bag@(LB b) (LB []) = Just bag
sumBag bag@(LB b) bag'@(LB b') =
    if wf bag && wf bag' then 
        -- Not so efficient but very simple :P
        Just (fromList (toList bag ++ toList bag')) 
    else Nothing


instance Foldable ListBag where
    foldMap f (LB []) = mempty
    foldMap f (LB ((v,m):xs))=f v <> foldMap f (LB xs)

mapLB :: Eq b => (a->b) -> ListBag a -> ListBag b
mapLB f lbag@(LB ls)=
    g f ls []
    where
        g f [] acc=fromList (toList (LB acc)) --f could map 2 value in a to same value in b
        g f (((v, m):xs)) acc=g f xs ((f v, m):acc)