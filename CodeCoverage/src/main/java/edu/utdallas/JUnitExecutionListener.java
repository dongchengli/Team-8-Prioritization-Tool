package edu.utdallas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Iterator;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import edu.utdallas.util.Helper;

public class JUnitExecutionListener extends RunListener {
    
    File report;
    PrintWriter writer = null;
    
    File reportAdditional;
    PrintWriter writerAdditional = null;
    
    long startTime, endTime;
    Map<String, ArrayList<Map<String, HashSet<String>>>> content = new HashMap<String, ArrayList<Map<String, HashSet<String>>>>();
    Map<String, ArrayList<Integer>> pRi = new HashMap<String, ArrayList<Integer>>();
    Map<String, HashSet<String>> methodline ;
    HashSet<String> covLine ;
    
    public void writeSuitFile(String fileName, String content){
        File file = new File(fileName);
        
        try (FileOutputStream fop = new FileOutputStream(file)) {
            if (!file.exists()) {
                file.createNewFile();
            }
            
            byte[] contentInBytes = content.getBytes();
            
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String CalculateMax(String className, ArrayList<String> calculatedMethod,
                               ArrayList<Map<String, HashSet<String>>> methodBody) {
        String methodTemp = null;
        int Max = 0;
        for (Map<String, HashSet<String>> method : methodBody) {
            String key = (String) method.keySet().toArray()[0];
            if (!calculatedMethod.isEmpty() && calculatedMethod.contains(key))
                continue;
            int sizes = pRi.get(className + ":" + key).get(1);
            if (sizes > Max) {
                Max = sizes;
                methodTemp = key;
            }
        }
        if (pRi.get(className + ":" + methodTemp) != null)
            pRi.get(className + ":" + methodTemp).set(1, Max);
        return methodTemp;
    }
    
    public ArrayList<Map<String, HashSet<String>>> CalculateLeftPRI(String className,
                                                                    ArrayList<String> calculatedMethod, ArrayList<Map<String, HashSet<String>>> methodBody) {
        String LastMethod = calculatedMethod.get(calculatedMethod.size() - 1);
        HashSet<String> temp2 = new HashSet<String>();// =
        // method.get(LastMethod);
        for (Map<String, HashSet<String>> method : methodBody) {
            String name = (String) method.keySet().toArray()[0];
            if (name.equals(LastMethod)) {
                temp2 = method.get(name);
                break;
            }
        }
        
        for (int i = 0; i < methodBody.size(); i++) {
            String name = (String) methodBody.get(i).keySet().toArray()[0];
            if (calculatedMethod.contains(name))
                continue;
            HashSet<String> temp1 = methodBody.get(i).get(name);
            int Count = 0;
            ArrayList<String> rest = new ArrayList<String>();
            for (String line : temp1) {
                if (!temp2.contains(line))
                    Count++;
                else
                    rest.add(line);
            }
            
            for (String line : rest) {
                methodBody.get(i).get(name).remove(line);
            }
            
            pRi.get(className + ":" + name).set(1, Count);
        }
        
        return methodBody;
    }
    
    public void CaluculatePRI(String className, ArrayList<Map<String, HashSet<String>>> methodBody) {
        ArrayList<String> calculatedMethod = new ArrayList<String>();
        int index = methodBody.size();
        while ((index--) > 0) {
            String maxMethod = CalculateMax(className, calculatedMethod, methodBody);
            calculatedMethod.add(maxMethod);
            methodBody = CalculateLeftPRI(className, calculatedMethod, methodBody);
        }
    }
    
    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);
        startTime = System.currentTimeMillis();
        report = new File(System.getProperty("user.dir") + "/total-cov.txt");
        try {
            writer = new PrintWriter(report);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        reportAdditional = new File(System.getProperty("user.dir") + "/additional-cov.txt");
        try {
            writerAdditional = new PrintWriter(reportAdditional);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        writer.println(
                       "Each Statement that has been covered by the Test is listed below as well as its path and class name for each Methods: ");
    }
    
    @Override
    public void testFailure(Failure failure) throws Exception {
        // TODO Auto-generated method stub
        super.testFailure(failure);
        endTime = System.currentTimeMillis();
        System.out.println("Find BUG time consumed:" + (endTime - startTime) + "ms");
    }
    
    @Override
    public void testStarted(Description description) throws Exception {
        // TODO Auto-generated method stub
        super.testStarted(description);
        System.out.println(description.getClassName() + ":" + description.getMethodName());
        if (pRi.get(description.getClassName() + ":" + description.getMethodName()) == null) {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            pRi.put(description.getClassName() + ":" + description.getMethodName(), temp);
        }
    }
    
    @Override
    public void testFinished(Description description) throws Exception {
        super.testStarted(description);
        covLine = new HashSet<String>(Helper.cov);
        Map<String, HashSet<String>> methodlinetemp = new HashMap<String, HashSet<String>>();
        methodlinetemp.put(description.getMethodName(), covLine);
        
        methodline = new HashMap<String, HashSet<String>>(methodlinetemp);
        if (content.get(description.getClassName()) == null) {
            ArrayList<Map<String, HashSet<String>>> methodList = new ArrayList<Map<String, HashSet<String>>>();
            content.put(description.getClassName(), methodList);
        } else
            content.get(description.getClassName()).add(methodline);
        
        pRi.get(description.getClassName() + ":" + description.getMethodName()).add(Helper.cov.size());
        pRi.get(description.getClassName() + ":" + description.getMethodName()).add(Helper.cov.size());
        
        Helper.cov.clear();
    }
    
    public ArrayList<Map<String, HashSet<String>>> SortByTotalStrategy(ArrayList<Map<String, HashSet<String>>> method) {
        Map<String, Integer> temp = new HashMap<String, Integer>();
        ArrayList<Map<String, HashSet<String>>> methodReturn = new ArrayList<Map<String, HashSet<String>>>();
        for (Map<String, HashSet<String>> methodBody : method) {
            String methodName = (String) methodBody.keySet().toArray()[0];
            int sizes = methodBody.get(methodName).size();
            temp.put(methodName, sizes);
        }
        
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(temp.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> arg0, Entry<String, Integer> arg1) {
                // TODO Auto-generated method stub
                return (arg1.getValue()).compareTo(arg0.getValue());
            }
        });
        
        Map<String, Integer> result = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        
        for (Map.Entry<String, Integer> methodList : result.entrySet()) {
            for (int i = 0; i < method.size(); i++) {
                String methodName = (String) method.get(i).keySet().toArray()[0];
                String name = methodList.getKey();
                if (name.contains(methodName)) {
                    Map<String, HashSet<String>> temp1 = new HashMap<String, HashSet<String>>();
                    temp1.put(name, method.get(i).get(name));
                    methodReturn.add(temp1);
                    break;
                }
            }
        }
        
        return methodReturn;
    }
    
    public ArrayList<Map<String, HashSet<String>>> SortByAdditionalStrategy(String className ,
                                                                            ArrayList<Map<String, HashSet<String>>> method) {
        
        Map<String, Integer> temp = new HashMap<String, Integer>();
        ArrayList<Map<String, HashSet<String>>> methodReturn = new ArrayList<Map<String, HashSet<String>>>();
        for (Map<String, HashSet<String>> methodBody : method) {
            String methodName = (String) methodBody.keySet().toArray()[0];
            int sizes = pRi.get(className+":"+methodName).get(1);//methodBody.get(methodName).size();
            temp.put(methodName, sizes);
        }
        
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(temp.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> arg0, Entry<String, Integer> arg1) {
                // TODO Auto-generated method stub
                return (arg1.getValue()).compareTo(arg0.getValue());
            }
        });
        
        Map<String, Integer> result = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        
        for (Map.Entry<String, Integer> methodList : result.entrySet()) {
            for (int i = 0; i < method.size(); i++) {
                String methodName = (String) method.get(i).keySet().toArray()[0];
                String name = methodList.getKey();
                if (name.contains(methodName)) {
                    Map<String, HashSet<String>> temp1 = new HashMap<String, HashSet<String>>();
                    temp1.put(name, method.get(i).get(name));
                    methodReturn.add(temp1);
                    break;
                }
            }
        }
        
        return methodReturn;
    }
    
    @Override
    public void testRunFinished(Result result) throws Exception {
        // TODO Auto-generated method stub
        super.testRunFinished(result);
        for (Map.Entry<String, ArrayList<Map<String, HashSet<String>>>> methodBody : content.entrySet()) {
            ArrayList<Map<String, HashSet<String>>> method = SortByTotalStrategy(methodBody.getValue());
            for (int i = 0; i < method.size(); i++) {
                String name = (String) method.get(i).keySet().toArray()[0];
                writer.println(methodBody.getKey() + ":" + name);
            }
            CaluculatePRI(methodBody.getKey(), methodBody.getValue());
        }
        
        for (Map.Entry<String, ArrayList<Map<String, HashSet<String>>>> methodBody : content.entrySet()) {
            ArrayList<Map<String, HashSet<String>>> method = SortByAdditionalStrategy(methodBody.getKey() , methodBody.getValue());
            for (int i = 0; i < method.size(); i++) {
                String name = (String) method.get(i).keySet().toArray()[0];
                writerAdditional.println(methodBody.getKey() + ":" + name);
            }
        }
        
        Map<String , Integer> classOrder = new HashMap<>();
        Map<String , Integer> classOrderAdditional = new HashMap<>();
        for (Map.Entry<String, ArrayList<Integer>> pRiBody : pRi.entrySet()) {
            System.out.println("->" + pRiBody.getKey());
            System.out.println("\t" + "Total Strategy:" + pRiBody.getValue().get(0) + "  Additional Strategy:"
                               + pRiBody.getValue().get(1));
            
            String className = pRiBody.getKey().substring(0, pRiBody.getKey().indexOf(":"));
            if(classOrder.keySet().contains(className)){
                int num = pRiBody.getValue().get(0) + classOrder.get(className);
                classOrder.put(className , num);
            }else{
                classOrder.put(className , pRiBody.getValue().get(0));
            }
            
            if(classOrderAdditional.keySet().contains(className)){
                int num = pRiBody.getValue().get(1) + classOrderAdditional.get(className);
                classOrderAdditional.put(className , num);
            }else{
                classOrderAdditional.put(className , pRiBody.getValue().get(1));
            }
        }
        
        
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(classOrder.entrySet());
        List<Map.Entry<String, Integer>> listAdditional = new LinkedList<Map.Entry<String, Integer>>(classOrderAdditional.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> arg0, Entry<String, Integer> arg1) {
                // TODO Auto-generated method stub
                return (arg1.getValue()).compareTo(arg0.getValue());
            }
        });
        Collections.sort(listAdditional, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> arg0, Entry<String, Integer> arg1) {
                // TODO Auto-generated method stub
                return (arg1.getValue()).compareTo(arg0.getValue());
            }
        });
        
        Map<String, Integer> result1 = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            result1.put(entry.getKey(), entry.getValue());
        }
        Map<String, Integer> result1Additional = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : listAdditional) {
            result1Additional.put(entry.getKey(), entry.getValue());
        }
        
        String content = "import org.junit.runner.RunWith;\n"
        + "import org.junit.runners.Suite;\n@RunWith(Suite.class)\n@Suite.SuiteClasses({";
        for(Map.Entry<String , Integer> ss : result1.entrySet())
            content += ss.getKey() + ".class,\n" ;
        content += "})\n";
        content += "public class FeatureTestSuite{\n}";
        writeSuitFile(System.getProperty("user.dir")+"/src/test/java/FeatureTestSuite.java", content);
        
        String contentAdditional = "import org.junit.runner.RunWith;\n"
        + "import org.junit.runners.Suite;\n@RunWith(Suite.class)\n@Suite.SuiteClasses({";
        /*******Start:Put total-max class as the first class*******/
        String firstTotalClass = null;
        Iterator<Map.Entry<String, Integer>> iter = result1.entrySet().iterator();
        if(iter.hasNext()){
            firstTotalClass = iter.next().getKey() + ".class,\n" ;
        }
        contentAdditional += firstTotalClass;
        /*******The End*******/
        for(Map.Entry<String , Integer> ss : result1Additional.entrySet()){
            String tmpClass = ss.getKey() + ".class,\n" ;
            if(!tmpClass.equals(firstTotalClass)){	//Ignore first total class
                contentAdditional += tmpClass;
            }
        }
        contentAdditional += "})\n";
        contentAdditional += "public class FeatureTestSuite{\n}";
        writeSuitFile(System.getProperty("user.dir")+"/src/test/java/FeatureTestSuiteAdditional.java", contentAdditional);
        
        writer.close();
        writerAdditional.close();
    }
    
}