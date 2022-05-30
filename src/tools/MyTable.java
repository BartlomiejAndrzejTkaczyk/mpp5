package tools;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class MyTable{
    public List<Map<String, String>> table = new ArrayList<>();
    public Map<String, List<String>> uniqValues = new HashMap<>();
    private Map<String, Integer> keyCount = new HashMap<>();
    List<String> columnName = new ArrayList<>();

    Map<String, Map<String, Map<String, Float>>> pathProbability = new HashMap<>();
    public MyTable(String namesLien, String sep){
        String[] names = namesLien.split(sep);
        columnName.addAll(Arrays.asList(names));
        columnName.add("key");
    }

    public void add(String lien, String sep){
        Map<String, String> temp = new HashMap<>();
        String[] eles = lien.split(sep);
        for (int i=0;i< eles.length;i++){
            String name = columnName.get(i);
            String ele = eles[i];
            temp.put(name, ele);

            if (!uniqValues.containsKey(name)) uniqValues.put(name, new LinkedList<>());
            if(!uniqValues.get(name).contains(ele)) uniqValues.get(name).add(ele);
        }
        table.add(temp);
    }

    public void initPathProbability(){
        uniqValues.get("key").forEach(ele -> keyCount.put(ele, 0));

        for (Map<String, String> row : table){
            keyCount.put(row.get("key"), keyCount.get(row.get("key"))+1);
        }


        float tableSize = table.size();
        List<String> keys = uniqValues.get("key");
        keys.forEach(
                k->{
                    pathProbability.put(k, new HashMap<>());
                    Map<String, Map<String, Float>> columnSteps = pathProbability.get(k);
                    columnName.forEach(
                            cn->{
                                columnSteps.put(cn, new HashMap<>());
                                Map<String, Float> valSteps = columnSteps.get(cn);
                                List<String> valName = uniqValues.get(cn);
                                valName.forEach(
                                        vn->{
                                            float value = ((float) howManyTimes(cn, vn, k)) / keyCount.get(k);
//                                            value = value * (keyCount.get(k) / tableSize);
                                            valSteps.put(vn, value);
                                        });
                            }
                    );
                }
        );
    }

    private int howManyTimes(String columnName, String columnVal, String keyVal){
        int res=0;
        for (Map<String, String> row : table){
            if(! row.get("key").equals(keyVal))continue;
            if (! row.get(columnName).equals(columnVal)) continue;
            res++;
        }
        return res;
    }

    public String classify(String lien, String sep){
        String[] temp = lien.split(sep);
        Map<String, String> eles = new HashMap<>();
        Map<String, Float> typeToVal = new HashMap<>();
        for (int i=0; i<temp.length; i++){
            if (columnName.get(i).equals("key"))continue;
            eles.put(columnName.get(i), temp[i]);
        }

        pathProbability.forEach(
                (k, csn) ->{
                    AtomicReference<Float> value = new AtomicReference<>(null);
                    eles.forEach(
                            (cnEle, cvEle) ->{
                                if (value.get() == null) value.set(csn.get(cnEle).get(cvEle));
                                else value.getAndUpdate(v -> v*csn.get(cnEle).get(cvEle));
                            }
                    );
                    value.updateAndGet(v -> v * (((float)keyCount.get(k))/table.size()));
                    typeToVal.put(k, value.get());
                }
        );
        List<String> temp2 = new ArrayList<>();
        typeToVal
                 .entrySet()
                 .stream()
                 .sorted((e1,e2) -> -1*Float.compare(e1.getValue(), e2.getValue()))
                 .forEach((ele) -> temp2.add(ele.getKey()));
        return temp2.get(0);
    }

    public void showPathProbability(){
        pathProbability.forEach(
                (k, columnSteps) ->{
                    System.out.println(k + " =");
                    columnSteps.forEach(
                            (columnName, valSteps) ->{
                                System.out.println("\t"+columnName);
                                valSteps.forEach(
                                        (valueName, value) ->{
                                            System.out.println("\t\t"+valueName + " = " + value);
                                        }
                                );
                            }
                    );
                }
        );
    }

}
