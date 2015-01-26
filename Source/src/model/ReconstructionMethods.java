package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import model.enums.ProtocolEntityType;
import model.enums.Scale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import control.Rules;


public class ReconstructionMethods {
    private List<ReconstructionMethod> method_list;
    
    public ReconstructionMethods(File methodsFile){
        Map<Integer, ReconstructionMethod> method_map = new TreeMap<Integer, ReconstructionMethod>();
        
        //Get the workbook instance for XLS file 
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook (new FileInputStream(methodsFile));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        //Get first sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0);
         
        
        //Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();
        
        //get method names from head row
        Row headRow = rowIterator.next();
        Iterator<Cell> headCellIterator = headRow.cellIterator();
        while(headCellIterator.hasNext()){
            Cell methodNameCell = headCellIterator.next();
            String method_name = methodNameCell.getStringCellValue();
            ReconstructionMethod new_method = new ReconstructionMethod(method_name);
            method_map.put(methodNameCell.getColumnIndex(), new_method);
        }            
            
        //read values from whole matrix
        while(rowIterator.hasNext()){
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            //skip empty rows
            if(!cellIterator.hasNext()){
                continue;
            }
            
            //get variable name
            Cell variableNameCell = cellIterator.next();
            String variable_name = variableNameCell.getStringCellValue();
            
            //get values for each method
            while(cellIterator.hasNext()){
                Cell valueCell = cellIterator.next();
                ReconstructionMethod current_method = method_map.get(valueCell.getColumnIndex());
                if(current_method==null){
                    continue;
                }
                Object value = null;
                
                switch(valueCell.getCellType()){
                case Cell.CELL_TYPE_NUMERIC:
                    value = valueCell.getNumericCellValue();
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = valueCell.getStringCellValue();
                    if( value.equals("o") ){
                        value = false;
                    }else if(value.equals("x")){
                        value = true;
                    }
                    
                    for(String scaleString : Scale.valueStrings()){
                        if(value.equals(scaleString)){
                            value = Scale.valueOf(scaleString);
                        }
                    }
                    
                    break;
                case Cell.CELL_TYPE_BLANK:
                    value = "~";
                    break;
                default: 
                    throw new RuntimeException("Unexpected DataType in Excel sheet, type: "+valueCell.getCellType()+
                            ", row: "+valueCell.getRowIndex()+", column:"+valueCell.getColumnIndex());
                }
                
                current_method.addProperty(variable_name, value);
            } 
            
        }
        
        method_list = new ArrayList<ReconstructionMethod>(method_map.values());
    }

    
    
    public void removeUnsuitableMethods(){
        ArrayList<ReconstructionMethod> new_method_list = new ArrayList<ReconstructionMethod>();
        for(ReconstructionMethod method : method_list){
            if(method.isSuitable()){
                new_method_list.add(method);
            }
        }
        
        method_list = new_method_list;
    }
    
    public List<ReconstructionMethod> getMethodList(){
        return method_list;
    }
    
    public ReconstructionMethod getCheapestMethod(){
        ReconstructionMethod methodCheapest = null;
        for(ReconstructionMethod method : method_list){
            if(methodCheapest == null || method.getCosts()<methodCheapest.getCosts()){
                methodCheapest = method;
            }
        }
        return methodCheapest;
    }
    
    public List<String> getNormsVaryingAmongMethods(List<String> norms_to_be_considered) {
        //1. take norms which differ between the suitable measures
          SortedSet<String> varying_norms = new TreeSet<String>();
          SortedMap<String, Object> norm_values = new TreeMap<String, Object>();
          for(ReconstructionMethod method : method_list){
              if(method.isSuitable()){
                  for(String norm : norms_to_be_considered){
                      if(norm_values.get(norm)==null){
                          norm_values.put(norm, method.getProperties().get(norm));
                      }else{
                          //if norm is varying among the left candidates, add it to relevant_norms
                          if(!norm_values.get(norm).equals(method.getProperties().get(norm))){
                              varying_norms.add(norm);
                          }
                      }
                  }
              }
          }
          
          return new ArrayList<String>(varying_norms);
    }

    public void removeGenerallyImpossibleMethods(Case abstracted_case, Expert expert){      
        HashMap<ProtocolEntityType, Integer> case_types = abstracted_case.getCount();
        System.out.println("-------Methods before first Assessment------");
        System.out.println("suitability, name");
        for(ReconstructionMethod method : method_list){
            System.out.println((method.isSuitable()?"true":"false")+", "+method.getName());
        }
        
        for(ReconstructionMethod method : method_list){
            for(ProtocolEntityType case_type : case_types.keySet()){
                if(!method.isSuitable() || method.getProperty(case_type.toString()) == null){
                    continue;
                }
                
                Object norm_value = method.getProperty(case_type.toString());
                if(norm_value instanceof String){
                    norm_value = Rules.evaluateProtocolEntityRule((String) norm_value, case_type, abstracted_case, expert);
                }
                
                if(norm_value instanceof Boolean){
                    if(!(Boolean) norm_value){
                        method.setNotSuitable();
                    }else{
                        continue;
                    }
                }else{
                    throw new RuntimeException("Method: "+method.getName()+" created an error when evaluating case: "+case_type.toString());
                }
            }
        }
        
        System.out.println("-------Methods after first Assessment------");
        System.out.println("suitability, name");
        for(ReconstructionMethod method : method_list){
            System.out.println((method.isSuitable()?"true":"false")+", "+method.getName());
        }
    }
}
