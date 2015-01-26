package model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import control.Rules.Rule;


public class ReconstructionMethod {
    
    protected String method_name;
    protected HashMap<String, Object> properties;
    protected boolean suitable;
    protected double costs;
    protected double min_lifetime;
    protected double max_lifetime;
    protected LinkedHashMap<Rule,String> technical_risks;
    protected LinkedHashMap<Rule,String> external_risks;
    protected List<String> preparation_work;
    protected List<String> finishing_work;
    
    /*protected int costs;
    protected int lifetime;
    protected int min_dimension_mm;
    protected int max_dimension_mm;
    protected List<ProfileType> supported_profile_types;
    protected List<Material> supported_materials;
    protected List<Damage> applicable_to;
    protected boolean improves_static_load_capacity;
    protected boolean requires_static_load_capacity;
    protected boolean improves_bedding;
    protected boolean requires_proper_bedding;
    protected boolean requires_only_one_manhole;
    protected boolean increase_hydraulic_capacity_possible;
    protected boolean reduces_hydraulic_capacity;
    protected boolean creates_vibration;
    protected boolean impact_on_soil_and_trees;
    protected Scale creates_noise_and_dust;
    protected Scale required_space;*/
    
    public ReconstructionMethod(String method_name){
        this.method_name = method_name;
        properties = new HashMap<String, Object>();
        suitable = true;
        costs = 0.0;
        technical_risks = new LinkedHashMap<Rule,String>();
        external_risks = new LinkedHashMap<Rule,String>();
        preparation_work = new ArrayList<String>();
        finishing_work = new ArrayList<String>();
    }
    
    public void addProperty(String identifier, Object value){        
        properties.put(identifier, value);
    }
    
    public Object getProperty(String identifier){
        return properties.get(identifier);
    }
    
    public HashMap<String, Object> getProperties(){
        return properties;
    }
    
    public String getName(){
        return method_name;
    }
    
    public void setNotSuitable(){
        this.suitable = false;
    }
    
    public boolean isSuitable(){
        return suitable;
    }

    public double getCosts() {
        return costs;
    }

    public void addCosts(double costs) {
        this.costs+=costs;
    }

    public LinkedHashMap<Rule,String> getTechnicalRisks() {
        return technical_risks;
    }

    public void addTechnicalRisk(Rule risk, String method_property) {
        technical_risks.put(risk,method_property);
    }
    
    public LinkedHashMap<Rule,String> getExternalRisks() {
        return external_risks;
    }

    public void addExternalRisk(Rule risk, String expert_property) {
        external_risks.put(risk,expert_property);
    }

    public double getMin_lifetime() {
        return min_lifetime;
    }

    public void setMin_lifetime(double min_lifetime) {
        this.min_lifetime = min_lifetime;
    }

    public double getMax_lifetime() {
        return max_lifetime;
    }

    public void setMax_lifetime(double max_lifetime) {
        this.max_lifetime = max_lifetime;
    }

    public void addPreparation_work(String preparation) {
        preparation_work.add(preparation);
    }

    public void addFinishing_work(String finishing) {
        finishing_work.add(finishing);
    }

    public List<String> getPreparation_work() {
        return preparation_work;
    }

    public List<String> getFinishing_work() {
        return finishing_work;
    }
    
    public double getCostsPa(){
        return getCosts()/((getMin_lifetime()+getMax_lifetime())/2);
    }
    
}
