public class MainB
{
    public static void main(String[] args)
    {
        ASuper x = new BSub();
        BSub y = x;
        y.bar();
    }
}