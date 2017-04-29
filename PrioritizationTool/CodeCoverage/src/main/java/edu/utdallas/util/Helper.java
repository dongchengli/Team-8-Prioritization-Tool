package edu.utdallas.util;

import java.util.Arrays;
import java.util.HashSet;

public class Helper {
	
//	public static HashMap<String , ArrayList<String>> executedClass = new HashMap<String , ArrayList<String>>();
//	public static HashMap<String , ArrayList<Integer>> executedMethodLine = new HashMap<String , ArrayList<Integer>>();
//	public static HashMap<String , ArrayList<String>> class_method = new HashMap<String , ArrayList<String>>();
	
//    private static HashMap<String, HashSet<String>> myClass_Method = new HashMap<String, HashSet<String>>();
//
//    private static String[] classesNotToInstrument = {"org/joda/time","org/apache/commons"};
//    static public boolean isClassUnderTest(String className){
//        for (String classToFilter : classesNotToInstrument){
//            if(className.toLowerCase().contains(classToFilter))
//                return true;
//        }
//        return false;
//    }
//    
//	private static String classPath = "default";
//    
//    public static HashMap<String, HashSet<String>> getMyClass_Method() {
//    	return myClass_Method;
//    }
//    
//    public static String getClassPath() {
//    	return Helper.classPath;
//    }
//    
//    public static void setClassPath(String classPath) {
//    	Helper.classPath = classPath;
//    	myClass_Method.put(classPath, new HashSet<String>());
//    }
//    
//	public static void addExecutedLine(String content){
//		if(myClass_Method.get(Helper.getClassPath()) != null)
//			myClass_Method.get(Helper.getClassPath()).add(content);
//	}
	
	public static HashSet<String> cov = new HashSet<String>();
	public static void addExecutedLine(String content){
		if(content != null)	cov.add(content);
	}
}
