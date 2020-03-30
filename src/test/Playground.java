package test;
// TESTBED NOT USED
import java.util.concurrent.TimeUnit;
public class Playground {
    public static void main(String[] args){
        System.out.println("Hello World");
        do{
            try{
                TimeUnit.SECONDS.sleep(3);
                int[] a = new int[]{1,3,4,5};
                String b = "ASDF";
                System.out.println("KASKD");
                System.out.println(b.charAt(123));
                break;
            } catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            } catch (Exception e){
                System.out.println("ASDF");
                e.printStackTrace();
                break;
            }
        } while(true);

    }
}
