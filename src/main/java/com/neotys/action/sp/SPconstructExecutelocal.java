package com.neotys.action.sp;

/**
 * class to construct stored function command dynamically & execute it
 * @author Vijesh
 *
 */
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.net.URLClassLoader;

//import oracle.jdbc.OracleTypes;

import java.lang.String;
import java.util.*;
import java.sql.*;

import com.neotys.extensions.action.engine.Context;

public class SPconstructExecutelocal {

	public static String callOracleStoredFunctOUTParameter(Context context,String con,String spname,String paramtypes[], String B[]) throws SQLException
	{

		Connection dbConnection = null;
	     CallableStatement callableStatement = null;

	//String Neoload="";
	String Neoload= spname;
	String param2="";
	String param3="";
	String param4="";
	String param5="";
	String param6="";
	List<Integer> IOindexlist= new ArrayList<Integer>();  // to store INOUT/OUT parameres index
	int paramcount=paramtypes.length;
	int index;
	String prepCallStatement ="";



	//prepare callable statement
	context.getLogger().debug("Total number of parmeters:"+ paramcount);

	dbConnection=(Connection)context.getCurrentVirtualUser().get(con);
	//String prepCallStatement = "{call getDBUSERByUserId(?,?,?,?)}";

	//prepCallStatement=prepCallStatement.concat("begin :result := fcrsit116_1.ap_ol_neft_process_fj_sdmc(?:var_pi_cod,?,?); end;");

//	prepCallStatement=prepCallStatement.concat("begin :result := " + spname + "(");    //orndinal binding & named binding cannot be combined???
	//prepCallStatement=prepCallStatement.concat("begin ? := " + spname + "(");

	prepCallStatement=prepCallStatement.concat("{call " + spname + "(");
	//"{call "+Neoload+"(?)}"
	/*
	for (int i=0;i<paramcount-1;i++){
		prepCallStatement=prepCallStatement.concat("?,");
	}
	prepCallStatement=prepCallStatement.concat("?); end;");




	context.getLogger().debug("debugging info callablestatement is ...:"+ prepCallStatement);
	Neoload=Neoload.concat("\n"+"prepared call statement PLSQL is : "+prepCallStatement);
	callableStatement=dbConnection.prepareCall(prepCallStatement);

	callableStatement.registerOutParameter("result",java.sql.Types.NUMERIC);
	context.getLogger().debug("Registered Result");

	context.getLogger().debug("Register & setvalues");

		//based on parameter data type use approprite function to setvalues & register
		for (int i=0;i<paramcount;i++){
			//parse data type & paramtype    dataparmatypes[0]= VARCHAR     dataparmatypes[1]= IN
			String[] dataparmatypes = paramtypes[i].split("-");
			context.getLogger().debug("Register & setvalues"+ i+ "-th parm");
			switch(dataparmatypes[0]) {

			case "INT":

				context.getLogger().debug("integer");
						if ((dataparmatypes[1].equals("OUT"))|| dataparmatypes[1].equals("INOUT")){
							//Register variable
							callableStatement.registerOutParameter(i+1, java.sql.Types.INTEGER);
							IOindexlist.add(i);

							if( (dataparmatypes[1].equals("INOUT"))){
								//assign value
								callableStatement.setInt(i+1,Integer.parseInt(B[i]));

							}

						}else{
							//just assign value

							callableStatement.setInt(i+1,Integer.parseInt(B[i]));
						}


				break;

			case "VARCHAR":

				//context.getLogger().debug("varchar");

						if ((dataparmatypes[1].equals("OUT"))|| dataparmatypes[1].equals("INOUT")){
							//Register variable
							context.getLogger().debug("ot/inout: varchar");
							callableStatement.registerOutParameter(i+1,java.sql.Types.VARCHAR);
							IOindexlist.add(i);

							if( (dataparmatypes[1].equals("INOUT"))){
								//assign value
								context.getLogger().debug("INOUT Rgister");
								callableStatement.setString(i+1,B[i]);

							}

						}else{
							//just assign value

							callableStatement.setString(i+1,B[i]);
						    }

				break;


			}



	}//for loop end
	*/

	//***********************************************************************************

	for (int i=0;i<paramcount-1;i++){
		prepCallStatement=prepCallStatement.concat("?,");
	}
	prepCallStatement=prepCallStatement.concat("?)}");




	context.getLogger().debug("debugging info callablestatement is ...:"+ prepCallStatement);
	Neoload=Neoload.concat("\n"+"prepared call statement PLSQL is : "+prepCallStatement+"\n");
	callableStatement=dbConnection.prepareCall(prepCallStatement);

	//callableStatement.registerOutParameter(1,java.sql.Types.NUMERIC);
	context.getLogger().debug("Registered Result");

	context.getLogger().debug("Register & setvalues");

		//based on parameter data type use approprite function to setvalues & register
		for (int i=0;i<paramcount;i++){
			//parse data type & paramtype    dataparmatypes[0]= VARCHAR     dataparmatypes[1]= IN
			String[] dataparmatypes = paramtypes[i].split("-");

			context.getLogger().debug("Register & setvalues"+ i+ "-th parm" + dataparmatypes[0]);
			switch(dataparmatypes[0]) {

			case "INT":

				context.getLogger().debug("integer");
						if ((dataparmatypes[1].equals("OUT"))|| dataparmatypes[1].equals("INOUT")){
							//Register variable
							callableStatement.registerOutParameter(i+1, java.sql.Types.INTEGER);
							IOindexlist.add(i);

							if( (dataparmatypes[1].equals("INOUT"))){
								//assign value
								callableStatement.setInt(i+1,Integer.parseInt(B[i]));

							}

						}else{
							//just assign value

							callableStatement.setInt(i+1,Integer.parseInt(B[i]));
						}


				break;

			case "VARCHAR":

				//context.getLogger().debug("varchar");

						if ((dataparmatypes[1].equals("OUT"))|| dataparmatypes[1].equals("INOUT")){
							//Register variable
							context.getLogger().debug("ot/inout: varchar");
							callableStatement.registerOutParameter(i+1,java.sql.Types.VARCHAR);
							IOindexlist.add(i);

							if( (dataparmatypes[1].equals("INOUT"))){
								//assign value
								context.getLogger().debug("INOUT Rgister");
								callableStatement.setString(i+1,B[i]);

							}

						}else{
							//just assign value

							callableStatement.setString(i+1,B[i]);
							context.getLogger().debug("in: varchar");
						    }

				break;


			}



	}//for loop end




	try{

	context.getLogger().debug("RUN Till");

	callableStatement.execute();
	//context.getLogger().debug("callable statement executed status= " + callableStatement.getString(1));
	//context.getLogger().debug("callable statement executed status= " + callableStatement.getString("result"));
	//System.out.println("OUTPUT=RESULT STATUS : "+callableStatement.getString("result"));
	//Neoload=Neoload.concat("\n"+"OUTPUT=RESULT STATUS : "+callableStatement.getString("result")+"\n"); //added by vijesh


	//Retrive INOUT & OUT values
	//iterate through list

	context.getLogger().debug("debugging info INOUT/OUT indexes are ...:"+ IOindexlist.toString());

	String outvalues="";
	/*
	for(Integer i:IOindexlist){

		index= i.intValue();

		String[] dataparmatypes = paramtypes[index].split("-");

		switch(dataparmatypes[0]) {

		case "INT":

					if (dataparmatypes[1].equals("OUT")){

						//Register variable
						outvalues=outvalues.concat("OUT_"+ index+ "="+callableStatement.getInt(index+1)+",");
					}else{
						outvalues=outvalues.concat("INOUT_"+ index+"="+ callableStatement.getInt(index+1)+",");
					}


			break;

		case "VARCHAR":

			if (dataparmatypes[1].equals("OUT")){

				//Register variable
				outvalues=outvalues.concat("OUT_"+ index+ "="+callableStatement.getString(index+1)+",");
			}else{
				outvalues=outvalues.concat("INOUT_"+ index+"="+ callableStatement.getString(index+1)+",");
			}

			break;


		} //switch end


	}//for end
	*/
//*****************************************************************************
	for(Integer i:IOindexlist){

		index= i.intValue();

		String[] dataparmatypes = paramtypes[index].split("-");

		switch(dataparmatypes[0]) {

		case "INT":

					if (dataparmatypes[1].equals("OUT")){

						//Register variable
						outvalues=outvalues.concat("OUT_"+ index+ "="+callableStatement.getInt(index+1)+",");
					}else{
						outvalues=outvalues.concat("INOUT_"+ index+"="+ callableStatement.getInt(index+1)+",");
					}


			break;

		case "VARCHAR":

			if (dataparmatypes[1].equals("OUT")){

				//Register variable
				outvalues=outvalues.concat("OUT_"+ index+ "="+callableStatement.getString(index+1)+",");
			}else{
				outvalues=outvalues.concat("INOUT_"+ index+"="+ callableStatement.getString(index+1)+",");
			}

			break;


		} //switch end


	}//for end

	context.getLogger().debug("INOUT/OUT parameter values are ...:"+ outvalues+ "\n");
	Neoload=Neoload.concat("INOUT & OUT values are:"+ outvalues+"END \n");


	}

	catch (SQLException e)
	{
	System.out.println(e.getMessage());
	Neoload=Neoload.concat(e.getMessage()+"\n");
	throw e;
	}

	finally
	{
	if (callableStatement != null)
	{
	callableStatement.close();
	}
	if (dbConnection != null)
	{
	//dbConnection.close();   commented by vijesh
	}
	}
	//Neoload = "Result =  "+param2+"\n"+"var_po_userrefno =  "+param3+"\n"+"var_po_codstatus =  "+param4+"\n"+"var_o_error_code =  "+param5+"\n"+"var_o_error_desc =  "+param6;
	//Neoload=Neoload.concat("Result =  "+param2+"\n"+"var_po_userrefno =  "+param3+"\n"+"var_po_codstatus =  "+param4+"\n"+"var_o_error_code =  "+param5+"\n"+"var_o_error_desc =  "+param6);
	//System.out.println(Neoload);

	return Neoload;

	}

public static void setvalue(){


}

	}
