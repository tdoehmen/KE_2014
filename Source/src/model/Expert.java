package model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import control.Controller;
import model.enums.Scale;
import model.enums.SurfaceCondition;

public class Expert {

    protected Controller controller;
    protected LinkedHashMap<String, Object> expert_input;
    
    //level 3
    public static String static_load_capacity_sufficient = "static_load_capacity_sufficient";
    public static String bedding_sufficient = "bedding_sufficient";
    public static String hydraulic_capacity_reduction_possible = "hydraulic_capacity_reduction_possible";
    public static String hydraulic_capacity_increase_required = "hydraulic_capacity_increase_required";
    public static String water_law_permission_possible = "water_law_permission_possible";
    public static String excavation_pit_possible = "excavation_pit_possible";
    public static String using_both_manholes_possible = "using_both_manholes_possible";
    //level 4
    public static String available_space = "available_space";
    public static String avoid_noise_and_dust = "avoid_noise_and_dust";
    public static String avoid_vibration = "avoid_vibration";
    public static String importance_of_surface_infrastucture = "importance_of_surface_infrastucture";
    public static String protective_trees_present = "protective_trees_present";
    public static String surface_condition = "surface_condition";
    //level 5,6

    public Expert(Controller controller){
        this.controller = controller;
        expert_input = new LinkedHashMap<String, Object>();
    }
    
    public void getInformation(Set<String> infos, int assessment_level){
        LinkedHashSet<String> infos_filtered = new LinkedHashSet<String>();
        for(String info : infos){
            if(!expert_input.containsKey(info)){
                infos_filtered.add(info);
            }
        }
        if(!infos_filtered.isEmpty()){
            controller.openExpertDialog(infos_filtered,assessment_level);
        }
    }
    
    public void setInformation(HashMap<String,Object> infos){
        for(String info : infos.keySet()){
            if(infos.get(info)!=null){
                expert_input.put(info, infos.get(info));
            }
        }
    }

    public Boolean isStatic_load_capacity_sufficient() {
        return (Boolean) expert_input.get(static_load_capacity_sufficient);
    }

    public Boolean isBedding_sufficient() {
        return (Boolean) expert_input.get(bedding_sufficient);
    }

    public Boolean isHydraulic_capacity_reduction_possible() {
        return (Boolean) expert_input.get(hydraulic_capacity_reduction_possible);
    }

    public Boolean isHydraulic_capacity_increase_required() {
        return (Boolean) expert_input.get(hydraulic_capacity_increase_required);
    }

    public Boolean isWater_law_permission_possible() {
        return (Boolean) expert_input.get(water_law_permission_possible);
    }

    public Boolean isExcavation_pit_possible() {
        return (Boolean) expert_input.get(excavation_pit_possible);
    }

    public Boolean isUsing_both_manholes_possible() {
        return (Boolean) expert_input.get(using_both_manholes_possible);
    }

    public Scale getAvailable_space() {
        return (Scale) expert_input.get(available_space);
    }

    public Scale getAvoid_noise_and_dust() {
        return (Scale) expert_input.get(avoid_noise_and_dust);
    }

    public Scale getAvoid_vibration() {
        return (Scale) expert_input.get(avoid_vibration);
    }

    public Scale getImportance_of_surface_infrastucture() {
        return (Scale) expert_input.get(importance_of_surface_infrastucture);
    }

    public SurfaceCondition getSurface_condition() {
        return (SurfaceCondition) expert_input.get(surface_condition);
    }

    public Boolean getProtective_trees_present() {
        return (Boolean) expert_input.get(protective_trees_present);
    }
    
    

}
