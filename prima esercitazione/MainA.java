public class MainA
{
    public static void main(String[] args)
    {
        ASuper x = new BSub();
        x.foo();
        x.bar();
    }
}