import tools.MyTable;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        int columnNo = 7;
        String columnsName = "buyCost,livingCost,doorsNo,peopleNo,bootSize,security";
        String sep = ",";
        MyTable table = new MyTable(columnsName, sep);

        try(BufferedReader in = new BufferedReader(new FileReader("src/res/car_bayes/train"))){
            in.lines().forEach(e -> table.add(e, sep));
            table.initPathProbability();
            table.showPathProbability();
        } catch (IOException e){
            e.printStackTrace();
        }

        try(BufferedReader in = new BufferedReader(new FileReader("src/res/car_bayes/test"))){
            AtomicInteger good = new AtomicInteger();
            AtomicInteger bad = new AtomicInteger();
            in.lines().forEach(e ->{
                String realy = e.split(",")[6];
                String c = table.classify(e, ",");
                if (realy.equals(c))
                    good.getAndIncrement();
                else
                    bad.getAndIncrement();
                System.out.println(realy + "=>"+c);
            });
            System.out.println("Good="+good.get());
            System.out.println("Bad="+bad.get());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
