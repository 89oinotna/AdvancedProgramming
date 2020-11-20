public class MainC
{
    public static void main(String[] args)
    {
        BSub x = new BSub();
        ASuper y = x;
        y.tee();
        x.tee();
    }
}