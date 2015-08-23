package io.pivotal.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

    /**
     * By default File#delete fails for non-empty directories, it works like "rm". 
     * We need something a little more brutual - this does the equivalent of "rm -r"
     * @param path Root File Path
     * @return true iff the file and all sub files/directories have been removed
     * @throws FileNotFoundException
     */
    public static boolean deleteRecursive(File path) throws FileNotFoundException{
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()){
            for (File f : path.listFiles()){
                ret = ret && Utils.deleteRecursive(f);
            }
        }
        
        
        return ret && path.delete();
    }	

    public static void main(String[] args){
    	
        DateFormat df = new SimpleDateFormat("mm/dd/yyyy hh:mma");
        df.setTimeZone(TimeZone.getTimeZone("EST"));
        try{
        	Date date = df.parse("8/19/2015 4:00pm");
        	System.out.println(date.toString());
        } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally{}
    	
    }
    
}
