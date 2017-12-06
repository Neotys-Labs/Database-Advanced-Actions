package com.neotys.action.sp;

/**
*
* @author vijesh
*/

import java.util.List;

public class Table {
   private String name;
   private List<String> headerRow;
   private List<List<String>> toupleList;

   /**
    * @return the headerRow
    */
   public List<String> getHeaderRow() {
       return headerRow;
   }

   /**
    * @param headerRow the headerRow to set
    */
   public void setHeaderRow(List<String> headerRow) {
       this.headerRow = headerRow;
   }

   /**
    * @return the toupleList
    */
   public List<List<String>> getToupleList() {
       return toupleList;
   }

   /**
    * @param toupleList the toupleList to set
    */
   public void setToupleList(List<List<String>> toupleList) {
       this.toupleList = toupleList;
   }

   /**
    * @return the name
    */
   public String getName() {
       return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
       this.name = name;
   }
}
