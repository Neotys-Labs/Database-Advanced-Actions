# Database Advanced Actions for Oracle Database

This repository contains NeoLoad Advanced Actions that allows performance testers using NeoLoad to perform complex database-related administrative tasks during a load test. Typicall at the begining of the test to prepare the database, or at the end for a clean-up.

## SQL Stored Procedure

The 'SP Execute' Advanced Action executes SQL stored procedures from NeoLoad.

## PL/SQL 

The 'PLSQL Execute' Advanced Action executes any PL/SQL file from NeoLoad.

| Property           | Value             |
| ----------------   | ----------------  |
| Maturity           | Experimental      |
| Author             | Neotys Professional Services |
| License            | [BSD Simplified](https://www.neotys.com/documents/legal/bsd-neotys.txt) |
| NeoLoad            | 5.2 (Enterprise or Professional Edition w/ Integration & Advanced Usage and NeoLoad Web option required)|
| Requirements       |    |
| Bundled in NeoLoad | No |
| Download Binaries  | See the [latest release](https://github.com/Neotys-Labs/Database-Advanced-Actions/releases/latest)

## Installation

1. Download the [latest release](https://github.com/Neotys-Labs/Database-Advanced-Actions/releases/latest)
1. Read the NeoLoad documentation to see [How to install a custom Advanced Action](https://www.neotys.com/documents/doc/neoload/latest/en/html/#25928.htm)

## SQL Stored Procedure - Parameters

| Name                     | Description       |
| ---------------          | ----------------- |
| connectionName           | the name of the connection as specified in the 'SQL Connect' action |
| StoredProc Name          | name of the stored procedure to execute |
| parameter Signature      | datatype and 'IN', 'OUT' or 'INOUT', comma separated. Ex: "VARCHAR-IN, INT-INOUT, NUMERIC-OUT, VARCHAR-IN" |
| parameter values         | A comma separated string of IN, OUT & INOUT parameter value(s). For OUT need to pass ‘?’ as value. Ex: " Martin,1234,?,ab34g" |

Status Codes:
* NL-SP_ERROR :  Any error while calling the stored procedure or I/O error. 

## PL/SQL - Parameters

| Name                     | Description       |
| ---------------          | ----------------- |
| connectionName           | the name of the connection as specified in the 'SQL Connect' action |
| PLSQL file name          | PLSQL filename. Absolute path or relative to <project>/custom-resources/ (when stored in this folder, the files is automatically transeferred to all Load Generators). This file must be formatted as per the rule mentioned below.|
| contentFile_parse      | Whether to parse the file to replace variables.  Possible values are Y/N. Default value= N.|

Status Codes:
* NL-PLSQL_ERROR :  Any PL/SQL error or I/O error.

### PLSQL File formatting guidelines

#### Rule 1
Outside block each sql statement should end with ;
Inside  each sql statement should end with ;+
End of each block should end with ;#  ( Ex:-     END;#)

#### Rule2
Better not  to use notepad++ for formatting PLSQL file. Use notepad & while saving  select “save type=all files (*.*)”   and Encoding=ANSI.


## Status Codes

* NL-SP-ERROR: PLSQL or I/O error
