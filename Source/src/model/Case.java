package model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.InspectionProtocol.InspectionProtocolEntity;
import model.enums.ProtocolEntityType;


public class Case {

    protected List<InspectionProtocolEntity> entries;
    protected HashMap<ProtocolEntityType, Integer> count;
    protected HashMap<ProtocolEntityType, Double> length;
    protected HashMap<ProtocolEntityType, List<String>> character;
    protected Integer deformation;
    protected List<Sewer> sewers;
    protected List<Environment> environments;
    
    public Case(List<InspectionProtocolEntity> entries, HashMap<ProtocolEntityType, Integer> count, HashMap<ProtocolEntityType, Double> length, HashMap<ProtocolEntityType, List<String>> character){
        this.entries = entries;
        this.count = count;
        this.length = length;
        this.character = character;
        sewers = new ArrayList<Sewer>();
        environments = new ArrayList<Environment>();
        
        deformation = 0;
        for(InspectionProtocolEntity entity : entries){
            if(entity.getCode().equals(ProtocolEntityType.BAA)){
                if(deformation < entity.getQuantifier()){
                    deformation = (int) entity.getQuantifier();
                }
            }
        }
    }
    
    public void addSewer(Sewer sewer){
        if(!sewers.contains(sewer)){
            sewers.add(sewer);
        }
    }
    
    public void addEnvironment(Environment environment){
        if(!environments.contains(environment)){
            environments.add(environment);
        }
    }

    public List<InspectionProtocolEntity> getEntries() {
        return entries;
    }

    public void setEntries(List<InspectionProtocolEntity> entries) {
        this.entries = entries;
    }

    public HashMap<ProtocolEntityType, Integer> getCount() {
        return count;
    }
    
    public double getCount(ProtocolEntityType entity_type) {
        if(count.get(entity_type)==null){
            return 0;
        }
        
        return count.get(entity_type);
    }

    public void setCount(HashMap<ProtocolEntityType, Integer> count) {
        this.count = count;
    }

    public HashMap<ProtocolEntityType, Double> getLength() {
        return length;
    }

    public void setLength(HashMap<ProtocolEntityType, Double> length) {
        this.length = length;
    }
    
    public HashMap<ProtocolEntityType, List<String>> getCharacter() {
        return character;
    }

    public void setCharacter(HashMap<ProtocolEntityType, List<String>> character) {
        this.character = character;
    }

    public List<Sewer> getSewers() {
        return sewers;
    }

    public void setSewers(List<Sewer> sewers) {
        this.sewers = sewers;
    }

    public List<Environment> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<Environment> environments) {
        this.environments = environments;
    }

    public Integer getDeformation() {
        return deformation;
    }
    
    
    
}
