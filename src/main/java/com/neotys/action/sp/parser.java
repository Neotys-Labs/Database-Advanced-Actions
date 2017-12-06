package com.neotys.action.sp;
/**
*
* @author vijesh
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.*;

import com.neotys.extensions.action.engine.Context;

public class parser {

	//public static void main(String[] args) throws IOException {
	public static Reader parse(Context context,String filepath) throws IOException{
		// TODO Auto-generated method stub
		
		String parsedstring="";
		 
		   Reader reader ;
		// TODO Auto-generated method stub
		File log= new File(filepath);
		//	File log= new File("D:\\log.txt");
		String search = "<variablevalue>";  // <- changed to work with String.replaceAll()
		String replacement = "<variablevalue>";
		//file reading
		FileReader fr = new FileReader(log);
		String s;
		try {
		    BufferedReader br = new BufferedReader(fr);

		    while ((s = br.readLine()) != null) {
		    	  String result="";
		    	// search for NL varaiable using regx
		    	
		    	
		    	String mydata = "some string with 'the data i want' inside";
		    	Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
		    	Matcher matcher = pattern.matcher(s);
		    	if (matcher.find())
		    	{
		    	    System.out.println(matcher.group(1));
		    	    String replacevalue=context.getVariableManager().getValue(matcher.group(1));
		    	    //get correspoding NL varaiable value
		    	     result= s.replaceAll("\\$\\{(.*?)\\}", replacevalue);
		    	    // System.out.println(result);
		    	}
		    	else{
		    		result=result.concat(s);
		    	//	System.out.println(result);
		    	}
		    	
		    	 
		    	parsedstring=parsedstring.concat(result+"\n");
		    	    	    	 
		  
		        
		        //write back to file
		    	
		    	
		        
		    }
		    System.out.println(parsedstring);
		    reader = new StringReader(parsedstring);
		    
		}
		finally{
			
		}
		return reader;

	}

}
