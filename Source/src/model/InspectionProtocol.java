package model;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import model.enums.ProtocolEntityType;


public class InspectionProtocol {

    List<InspectionProtocolEntity> entries;
    HashMap<ProtocolEntityType, Integer> count;
    HashMap<ProtocolEntityType, Double> length;
    HashMap<ProtocolEntityType, List<String>> character;

    public InspectionProtocol(File protocol){
        entries = new ArrayList<InspectionProtocolEntity>();
        count = new HashMap<ProtocolEntityType, Integer>();
        length = new HashMap<ProtocolEntityType, Double>();
        character = new HashMap<ProtocolEntityType, List<String>>();
                
        Scanner sc = null;
        try {
            sc = new Scanner(protocol);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String identifier_line = sc.nextLine().trim();
        String[] identifier = identifier_line.split(";");
        while(sc.hasNext()){
            InspectionProtocolEntity new_entity = new InspectionProtocolEntity();
            
            String value_line = sc.nextLine().trim();
            String[] values = value_line.split(";");
            for(int i = 0; i<identifier.length; i++){
                if(values[i].equals("null")){
                    continue;
                }else if(identifier[i].equals("sewer_identifier")){
                    new_entity.setSewer_identifier(values[i]); 
                }else if(identifier[i].equals("position")){
                    new_entity.setPosition(Double.parseDouble(values[i])); 
                }else if(identifier[i].equals("code")){
                    try{
                        new_entity.setCode(ProtocolEntityType.valueOf(values[i])); 
                    }catch(IllegalArgumentException e){
                        continue;
                    }
                }else if(identifier[i].equals("characterization")){
                    new_entity.setCharacterization(values[i]); 
                }else if(identifier[i].equals("quantifier")){
                    new_entity.setQuantifier(Double.parseDouble(values[i])); 
                }else if(identifier[i].equals("length")){
                    new_entity.setLength(Double.parseDouble(values[i])); 
                }
            }
            
            if(new_entity.getCode() == null){
                continue;
            }
            
            entries.add(new_entity);
            
            //update damage count list
            Integer current_count = 1;
            if(count.containsKey(new_entity.getCode())){
                current_count = count.get(new_entity.getCode());
                current_count = current_count + 1;
            }
            count.put(new_entity.getCode(),current_count);
            
            //update damage length list
            Double current_max_length = new_entity.getLength();
            if(!length.containsKey(new_entity.getCode()) || current_max_length > length.get(new_entity.getCode())){
                length.put(new_entity.getCode(), current_max_length);
            }
            
            //update damage character list
            List<String> current_character_list = character.get(new_entity.getCode());
            if(current_character_list == null){
                current_character_list = new ArrayList<String>();
                character.put(new_entity.getCode(), current_character_list);
            }
            if(!current_character_list.contains(new_entity.getCharacterization())){
                current_character_list.add(new_entity.getCharacterization());
            }
        }
        
        
    }
    
    public List<InspectionProtocolEntity> getEntries() {
        return entries;
    }

    public HashMap<ProtocolEntityType, Integer> getCount() {
        return count;
    }

    public HashMap<ProtocolEntityType, Double> getLength() {
        return length;
    }
    
    public HashMap<ProtocolEntityType, List<String>> getCharacter() {
        return character;
    }

    public class InspectionProtocolEntity {

        protected String sewer_identifier;
        protected double position;
        protected ProtocolEntityType code;
        protected String characterization;
        protected double quantifier;
        protected double length;
        
        public String getSewer_identifier() {
            return sewer_identifier;
        }
        public void setSewer_identifier(String sewer_identifier) {
            this.sewer_identifier = sewer_identifier;
        }
        public double getPosition() {
            return position;
        }
        public void setPosition(double position) {
            this.position = position;
        }
        public ProtocolEntityType getCode() {
            return code;
        }
        public void setCode(ProtocolEntityType code) {
            this.code = code;
        }
        public String getCharacterization() {
            return characterization;
        }
        public void setCharacterization(String characterization) {
            this.characterization = characterization;
        }
        public double getQuantifier() {
            return quantifier;
        }
        public void setQuantifier(double quantifier) {
            this.quantifier = quantifier;
        }
        public double getLength() {
            return length;
        }
        public void setLength(double length) {
            this.length = length;
        }
        
        
    }
}
