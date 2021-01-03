module Ex2 
(
  mapLB
)
where
import Ex1
instance Foldable ListBag where
    foldMap f (LB []) = mempty
    foldMap f (LB ((v,m):xs))=f v <> foldMap f (LB xs)


mapLB :: Eq b => (a->b) -> ListBag a -> ListBag b
mapLB f lbag=
    fromList (map f (toList lbag)) --f could map 2 value in a to same value in b

{-

Explain (in a comment in the same file) why it is not possible 
to define an instance of Functor for ListBag by providing mapLB 
as the implementation of fmap.

instance Functor ListBag where
    fmap = mapLB
    error: "No instance for (Eq b) arising from a use of ‘mapLB’"
    
    We can't define fmap as mapLB because of their type

    fmap :: (a -> b) -> f a -> f b
    mapLB :: Eq b => (a -> b) -> ListBag a -> ListBag b

    We have the constraint Eq b in the mapLB required by fromList
    that doesn't allow us to define a fmap from it

-}