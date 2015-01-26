package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import model.enums.LandUse;
import model.enums.Material;
import model.enums.ProfileType;
import model.enums.WastewaterType;


public class SewerDatabase {

    protected HashMap<String, Sewer> sewer;
    protected HashMap<String, Environment> environment;
    
    public SewerDatabase(File database){
        sewer = new HashMap<String, Sewer>();
        environment = new HashMap<String, Environment>();
        Scanner sc = null;
        try {
            sc = new Scanner(database);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String identifier_line = sc.nextLine().trim();
        String[] identifier = identifier_line.split(";");
        while(sc.hasNext()){
            Sewer new_sewer = new Sewer();
            Environment new_environment = new Environment();
            
            String value_line = sc.nextLine().trim();
            String[] values = value_line.split(";");
            for(int i = 0; i<identifier.length; i++){
                if(values[i] == null || values[i].isEmpty()){
                    continue;
                }else if(identifier[i].equals("sewer_identifier")){
                   new_sewer.setSewer_identifier(values[i]); 
                }else if(identifier[i].equals("material")){
                    new_sewer.setMaterial(Material.valueOf(values[i])); 
                }else if(identifier[i].equals("profile_type")){
                    new_sewer.setProfile_type(ProfileType.valueOf(values[i])); 
                }else if(identifier[i].equals("wastewater_type")){
                    new_sewer.setWastewater_type(WastewaterType.valueOf(values[i])); 
                }else if(identifier[i].equals("construction_year")){
                    new_sewer.setConstruction_year(Integer.parseInt(values[i])); 
                }else if(identifier[i].equals("dimension_mm")){
                    new_sewer.setDimension_mm(Integer.parseInt(values[i])); 
                }else if(identifier[i].equals("length_m")){
                    new_sewer.setLength_m(Double.parseDouble(values[i])); 
                }else if(identifier[i].equals("depth")){
                    new_sewer.setDepth(Double.parseDouble(values[i])); 
                }else if(identifier[i].equals("in_groundwater")){
                    new_sewer.setIn_groundwater(values[i].equals("true")); 
                }else if(identifier[i].equals("water_protection_area")){
                    new_environment.setWater_protection_area(values[i].equals("true")); 
                }else if(identifier[i].equals("land_use")){
                    new_environment.setLand_use(LandUse.valueOf(values[i])); 
                }
            }
            
            sewer.put(new_sewer.getSewer_identifier(),new_sewer);
            environment.put(new_sewer.getSewer_identifier(),new_environment);
        }
    }

    public HashMap<String,Sewer> getSewer() {
        return sewer;
    }

    public HashMap<String,Environment> getEnvironment() {
        return environment;
    }
    
    
}
