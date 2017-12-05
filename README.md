# SQL-Stored-Procedure

This action executes SQL stored procedures from NeoLoad during a Load Test. It enables you to perform administrative tasks like cleaning a database after a test. It also allows you to get business-related metrics that you can monitor, for example the number of items in a work queue or the number of checkouts during the last minute.

| Property           | Value             |
| ----------------   | ----------------   |
| Maturity           | Experimental |
| Author             | Neotys Professional Services |
| License            | [BSD Simplified](https://www.neotys.com/documents/legal/bsd-neotys.txt) |
| NeoLoad            | 5.2 (Enterprise or Professional Edition w/ Integration & Advanced Usage and NeoLoad Web option required)|
| Requirements       |    |
| Bundled in NeoLoad | No |
| Download Binaries  | See the [latest release](https://github.com/Neotys-Labs/SQL-Stored-Procedure/releases/latest)


## Installation

1. Download the [latest release](https://github.com/Neotys-Labs/SQL-Stored-Procedure/releases/latest)
1. Read the NeoLoad documentation to see [How to install a custom Advanced Action](https://www.neotys.com/documents/doc/neoload/latest/en/html/#25928.htm)

## Parameters

| Name                     | Description       |
| ---------------          | ----------------- |
| connectionName           | the name of the connection as specified in the 'SQL Connect' action |
| StoredProc Name          | name of the stored procedure to execute |
| parameter Signature      | datatype and 'IN', 'OUT' or 'INOUT', comma separated. Ex: "VARCHAR-IN, INT-INOUT, NUMERIC-OUT, VARCHAR-IN" |
| parameter values         | A comma separated string of IN, OUT & INOUT parameter value(s). For OUT need to pass ‘?’ as value. Ex: " Martin,1234,?,ab34g" |


## Status Codes

* NL-SP-ERROR: PLSQL or I/O error
