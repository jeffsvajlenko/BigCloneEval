package cloneMatchingAlgorithms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import database.Clone;

public interface CloneMatcher {
	public boolean isDetected(Clone clone) throws SQLException;
	public void close() throws SQLException;
	
	public static String getTableName(long toolid) {
		return "tool_" + toolid + "_clones";
	}
	
	public static CloneMatcher load(long toolid, String clazz, String params) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		//long time = System.currentTimeMillis();
		Class<?> mClass = Class.forName("cloneMatchingAlgorithms." + clazz);
		Constructor<?> constructor = mClass.getConstructor(long.class, String.class);
		CloneMatcher matcher =  (CloneMatcher) constructor.newInstance(toolid, params);
		//time = System.currentTimeMillis() - time;
		//System.out.println("TIME: " + time);
		return matcher;
	}
	
}
