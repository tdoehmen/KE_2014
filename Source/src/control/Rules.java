package control;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import model.Case;
import model.Environment;
import model.Expert;
import model.InspectionProtocol.InspectionProtocolEntity;
import model.ReconstructionMethod;
import model.Sewer;
import model.enums.LandUse;
import model.enums.Material;
import model.enums.ProfileType;
import model.enums.ProtocolEntityType;
import model.enums.Scale;
import model.enums.SurfaceCondition;


public class Rules {
    
    public List<LinkedHashMap<String,Rule>> assessment_level_rules;

    public Rules(){
        assessment_level_rules = new ArrayList<LinkedHashMap<String,Rule>>();
        createRules();
    }

    public LinkedHashMap<String, Rule> getAssessment_level_rules(int assessment_level) {
        return assessment_level_rules.get(assessment_level);
    }

    public static Boolean evaluateProtocolEntityRule(String rule, ProtocolEntityType damage_type, Case abstracted_case, Expert expert){
        
        int index = -1;
        String comparator = "";
        String variable = "";
        String value = "";
        
        String[] comparators = new String[]{"=","!=","<",">"};
        for(String c : comparators){
            index = rule.indexOf(c); 
            if( index != -1 ){
                comparator = c;
                variable = rule.substring(0, index);
                value = rule.substring(index+c.length()).trim();
                break;
            }
        }
        
        //implement
        if(variable.equals("LENGTH")){
            if(comparator.equals("<")){
                return abstracted_case.getLength().get(damage_type) < Double.parseDouble(value);
            }
            else if(comparator.equals(">")){
                return abstracted_case.getLength().get(damage_type) > Double.parseDouble(value);
            }
        }else if(variable.equals("COUNT")){
            if(comparator.equals("<")){
                return abstracted_case.getCount(damage_type) < Integer.parseInt(value);
            }
            else if(comparator.equals(">")){
                return abstracted_case.getCount(damage_type) > Integer.parseInt(value);
            }
        }else if(variable.equals("DEFORMATION")){
            if(comparator.equals("<")){
                return abstracted_case.getDeformation() < Integer.parseInt(value);
            }
            else if(comparator.equals(">")){
                return abstracted_case.getDeformation() > Integer.parseInt(value);
            }
        }else if(variable.equals("CHARACTER")){
            if(comparator.equals("<")){
                for(String character : abstracted_case.getCharacter().get(damage_type)){
                    if( character.compareTo(value) >= 0 ){
                        return false;
                    }
                }
                return true;
            }
            else if(comparator.equals(">")){
                for(String character : abstracted_case.getCharacter().get(damage_type)){
                    if( character.compareTo(value) <= 0 ){
                        return false;
                    }
                }
                return true;
            }
        }
        
        throw new RuntimeException("Rule: "+rule+" with abstracted case: "+abstracted_case+" could not be evaluated.");
    }

    public abstract class Rule {
        protected int assessment_level;
        protected String norm;
        protected String[] expert_input;
        
        public Rule(String norm, int assesment_level, String... expert_input ){
            this.norm = norm;
            this.assessment_level = assesment_level;
            this.expert_input = expert_input;
        }
        
        public void evaluate(ReconstructionMethod method, Case abstracted_case, Expert expert){
            if(!isRelevant(abstracted_case, expert)){
                return;
            }
            
            Object norm_value = method.getProperty(norm);
            
            try{
                evaluateRule(norm_value, method, abstracted_case, expert);
            }catch(RuntimeException e){
                throw new RuntimeException("Method: "+method.getName()+" created an error when evaluating norm: "+norm,e);
            }
        }
        
        
        public abstract void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert);
        
        protected abstract boolean isRelevant(Case abstracted_case, Expert expert);

        public int getAssessment_level() {
            return assessment_level;
        }
        
        public String[] getExpert_input(){
            return expert_input;
        }

    }
    
    public class DamageRule extends Rule{
        protected ProtocolEntityType damage_type;
        
        public DamageRule(String norm, int assesment_level, ProtocolEntityType damage_type) {
            super(norm, assesment_level);
            this.damage_type = damage_type;
        }
        
        @Override
        public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
            if(norm_value instanceof String){
                norm_value = Rules.evaluateProtocolEntityRule((String)norm_value, damage_type, abstracted_case, expert);
            }
            
            if(norm_value == null){
                method.addTechnicalRisk(this,"deformation");
            }else if(!((Boolean)norm_value)){
                method.setNotSuitable();
            }
        }

        @Override
        protected boolean isRelevant(Case abstracted_case, Expert expert) {
            for(ProtocolEntityType type : abstracted_case.getCount().keySet()){
                if(type.toString().equals(norm)){
                    return true;
                }
            }
            return false;
        }  
    }
    
    public class ProfileRule extends Rule{
        protected ProfileType profile_type;
        
        public ProfileRule(String norm, int assesment_level, ProfileType profile_type) {
            super(norm, assesment_level);
            this.profile_type = profile_type;
        }
        
        @Override
        public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
            if(norm_value instanceof String && norm_value.equals("~")){
                norm_value = true;
                method.addTechnicalRisk(this,norm);
            }
            
            if(!((Boolean)norm_value)){
                method.setNotSuitable();
            }
        }

        @Override
        protected boolean isRelevant(Case abstracted_case, Expert expert) {
            boolean relevant = false;
            for(Sewer sewer : abstracted_case.getSewers()){
                if(sewer.getProfile_type().equals(profile_type)){
                    relevant = true;
                }
            }
            return relevant;
        }  
    }
    
    public class MaterialRule extends Rule{
        protected Material material_type;
        
        public MaterialRule(String norm, int assesment_level, Material material_type) {
            super(norm, assesment_level);
            this.material_type = material_type;
        }
        
        @Override
        public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
            if(norm_value instanceof String && norm_value.equals("~")){
                norm_value = true;
                method.addTechnicalRisk(this,norm);
            }
            
            if(!((Boolean)norm_value)){
                method.setNotSuitable();
            }
        }

        @Override
        protected boolean isRelevant(Case abstracted_case, Expert expert) {
            boolean relevant = false;
            for(Sewer sewer : abstracted_case.getSewers()){
                if(sewer.getMaterial().equals(material_type)){
                    relevant = true;
                }
            }
            return relevant;
        }  
    }
    
    public class CostsRule extends Rule{
        protected int dimension_mm;
        
        public CostsRule(String norm, int assesment_level, int dimension_mm) {
            super(norm, assesment_level);
            this.dimension_mm = dimension_mm;
        }
        
        @Override
        public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
            double costs = 0.0;
            for(Sewer sewer : abstracted_case.getSewers()){
                if(sewer.getDimension_mm() == dimension_mm){
                    /*costs for whole sewer*/
                    if((Boolean)method.getProperty("costs_per_meter")){
                        costs += sewer.getLength_m()*((Double)norm_value);
                    }else /*costs per damage*/{
                        for(InspectionProtocolEntity entity : abstracted_case.getEntries()){
                            double lastPosition = 0;
                            if(entity.getCode().isDamage()){
                                     /*per damage meter*/
                                if(((Double)method.getProperty("costs_per_damage_meter"))!=0.0){
                                    costs += (entity.getLength()/1000.0)*((Double)norm_value)*(Double)method.getProperty("costs_per_damage_meter");
                                    if((Boolean)method.getProperty("preparation_work_CREATE_EXCLAVATION_PIT") &&
                                            (lastPosition==0 || Math.abs((entity.getPosition()-lastPosition))>5)){
                                        costs += 3500;
                                        lastPosition = entity.getPosition();
                                    }
                                }else/*per damage piece*/{
                                    costs += (Double)norm_value;
                                }
                                
                            }
                        }
                    }
                }
            }
            method.addCosts(costs);
        }

        @Override
        protected boolean isRelevant(Case abstracted_case, Expert expert) {
            return true;
        }  
    }
    
    
    private void createRules() {
        LinkedHashMap<String,Rule> assessment_level_rules_0 = new LinkedHashMap<String,Rule>();
        LinkedHashMap<String,Rule> assessment_level_rules_1 = new LinkedHashMap<String,Rule>();
        LinkedHashMap<String,Rule> assessment_level_rules_2 = new LinkedHashMap<String,Rule>();
        LinkedHashMap<String,Rule> assessment_level_rules_3 = new LinkedHashMap<String,Rule>();
        LinkedHashMap<String,Rule> assessment_level_rules_4 = new LinkedHashMap<String,Rule>();
        LinkedHashMap<String,Rule> assessment_level_rules_5 = new LinkedHashMap<String,Rule>();
        assessment_level_rules.add(assessment_level_rules_0);
        assessment_level_rules.add(assessment_level_rules_1);
        assessment_level_rules.add(assessment_level_rules_2);
        assessment_level_rules.add(assessment_level_rules_3);
        assessment_level_rules.add(assessment_level_rules_4);
        assessment_level_rules.add(assessment_level_rules_5);

        assessment_level_rules_0.put("BAA_DEFORMATION", new DamageRule("BAA_DEFORMATION",0,ProtocolEntityType.BAA));
        assessment_level_rules_0.put("BAB_CRACK", new DamageRule("BAB_CRACK",0,ProtocolEntityType.BAB));
        assessment_level_rules_0.put("BAC_BURST_OR_COLLAPSE", new DamageRule("BAC_BURST_OR_COLLAPSE",0,ProtocolEntityType.BAC));
        assessment_level_rules_0.put("BAF_SURFACE_DAMAGE", new DamageRule("BAF_SURFACE_DAMAGE",0,ProtocolEntityType.BAF));
        assessment_level_rules_0.put("BAH_DAMAGED_JUNCTION", new DamageRule("BAH_DAMAGED_JUNCTION",0,ProtocolEntityType.BAH));
        assessment_level_rules_0.put("BAI_INTRUDING_SEALING_MATERIAL", new DamageRule("BAI_INTRUDING_SEALING_MATERIAL",0,ProtocolEntityType.BAI));
        assessment_level_rules_0.put("BAJ_DISPLACED_CONNECTION", new DamageRule("BAJ_DISPLACED_CONNECTION",0,ProtocolEntityType.BAJ));
        assessment_level_rules_0.put("BAK_DAMAGES_INTERIOR_LINING", new DamageRule("BAK_DAMAGES_INTERIOR_LINING",0,ProtocolEntityType.BAK));
        assessment_level_rules_0.put("BBF_INFILTRATION", new DamageRule("BBF_INFILTRATION",0,ProtocolEntityType.BBF));
        assessment_level_rules_0.put("BBG_EXTFILTRATION", new DamageRule("BBG_EXTFILTRATION",0,ProtocolEntityType.BBG));
        
        assessment_level_rules_1.put("min_dimension_mm", new Rule("min_dimension_mm",1){
            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                for(Sewer sewer : abstracted_case.getSewers()){
                    if(sewer.getDimension_mm()<(Double)norm_value){
                        method.setNotSuitable();
                    }
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
            
        });
        assessment_level_rules_1.put("max_dimension_mm", new Rule("max_dimension_mm",1){
            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                for(Sewer sewer : abstracted_case.getSewers()){
                    if(sewer.getDimension_mm()>(Double)norm_value){
                        method.setNotSuitable();
                    }
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
            
        });
        assessment_level_rules_1.put("profile_type_CIRCLE", new ProfileRule("profile_type_CIRCLE",1,ProfileType.CIRCLE));
        assessment_level_rules_1.put("profile_type_EGG", new ProfileRule("profile_type_CIRCLE",1,ProfileType.EGG));
        assessment_level_rules_1.put("profile_type_OTHER", new ProfileRule("profile_type_CIRCLE",1,ProfileType.OTHER));
        assessment_level_rules_1.put("metarial_B_CONCRETE", new MaterialRule("metarial_B_CONCRETE",1,Material.B_CONCRETE));
        assessment_level_rules_1.put("metarial_FZ_FIBRE_CEMENT", new MaterialRule("metarial_FZ_FIBRE_CEMENT",1,Material.FZ_FIBRE_CEMENT));
        assessment_level_rules_1.put("metarial_GG_GREY_CAST", new MaterialRule("metarial_GG_GREY_CAST",1,Material.GG_GREY_CAST));
        assessment_level_rules_1.put("metarial_GGG_DUCTILE_IRONT", new MaterialRule("metarial_GGG_DUCTILE_IRONT",1,Material.GGG_DUCTILE_IRONT));
        assessment_level_rules_1.put("metarial_MA_BRICKWORK", new MaterialRule("metarial_MA_BRICKWORK",1,Material.MA_BRICKWORK));
        assessment_level_rules_1.put("meterial_PE_POLYETHYLENE_WELDED", new MaterialRule("meterial_PE_POLYETHYLENE_WELDED",1,Material.PE_POLYETHYLENE_WELDED));
        assessment_level_rules_1.put("metarial_PE_POLYETHYLENE", new MaterialRule("metarial_PE_POLYETHYLENE",1,Material.PE_POLYETHYLENE));
        assessment_level_rules_1.put("metarial_PP_POLYPROPELENE", new MaterialRule("metarial_PP_POLYPROPELENE",1,Material.PP_POLYPROPELENE));
        assessment_level_rules_1.put("metarial_PVC_POLYVINYLCHLORID", new MaterialRule("metarial_PVC_POLYVINYLCHLORID",1,Material.PVC_POLYVINYLCHLORID));
        assessment_level_rules_1.put("metarial_SB_REINFORCED_CONCRETE", new MaterialRule("metarial_SB_REINFORCED_CONCRETE",1,Material.SB_REINFORCED_CONCRETE));
        assessment_level_rules_1.put("metarial_STZ_STONEWARE", new MaterialRule("metarial_STZ_STONEWARE",1,Material.STZ_STONEWARE));
        assessment_level_rules_1.put("material_THIN_WALLED", new MaterialRule("material_THIN_WALLED",1,Material.THIN_WALLED));
        assessment_level_rules_1.put("metarial_OTHER", new MaterialRule("metarial_OTHER",1,Material.OTHER));

        //this rule is executed in assessment level 2
        assessment_level_rules_2.put("requires_static_load_capacity", new Rule("requires_static_load_capacity",2,"static_load_capacity_sufficient") {
          
            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                //if "requires_static_load_capacity" value from current selected method is false
                //then there is no potential risk
                if(!(Boolean)norm_value){
                    return;
                }
                
                //when static_load_capacity is required and there is no expert information, then
                //there is a risk, so add this to the technical risk list
                if(expert.isStatic_load_capacity_sufficient() == null){
                    method.addTechnicalRisk(this,"static_load_capacity_sufficient");
                }
                //if static_load_capacity is required and the expert sais that the current
                //sewer load capacitiy is not sufficient, then this method is not Suitable
                else if(!expert.isStatic_load_capacity_sufficient()){
                    method.setNotSuitable();
                }
            }
            
            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                //it is generally relevant for every case
                return true;
            }
        });
        assessment_level_rules_2.put("requires_proper_bedding", new Rule("requires_proper_bedding",2,"bedding_sufficient") {
            
            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if(!(Boolean)norm_value){
                    return;
                }
                
                if(expert.isBedding_sufficient() == null){
                    method.addTechnicalRisk(this,"bedding_sufficient");
                }
                else if(!expert.isBedding_sufficient()){
                    method.setNotSuitable();
                }
            }
            
            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_2.put("reduces_hydraulic_capacity", new Rule("reduces_hydraulic_capacity",2,"hydraulic_capacity_reduction_possible") {
            
            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {  
                if(norm_value.equals(Scale.NONE)){
                    return;
                }
                
                if(expert.isHydraulic_capacity_reduction_possible()==null){
                    method.addTechnicalRisk(this,"hydraulic_capacity_reduction_possible");
                }else{
                    if(norm_value.equals(Scale.LOW)){
                        method.addTechnicalRisk(this,norm);
                    }else{
                        method.setNotSuitable();
                    }
                }
            }
            
            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_2.put("increases_hydraulic_capacity_possible", new Rule("increases_hydraulic_capacity_possible",2,"hydraulic_capacity_increase_required") {
            
            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {  
                if((Boolean)norm_value){
                    return;
                }
                
                if(expert.isHydraulic_capacity_increase_required()==null){
                    method.addTechnicalRisk(this, "hydraulic_capacity_increase_required");
                }else if(expert.isHydraulic_capacity_increase_required()&&!(Boolean)norm_value){
                    method.setNotSuitable();
                }
            }
            
            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_2.put("requires_only_one_manhole", new Rule("requires_only_one_manhole",2,"using_both_manholes_possible"){
            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case,
                    Expert expert) {
                if((Boolean)norm_value){
                    return;
                }
                
                if(expert.isUsing_both_manholes_possible() == null){
                    method.addTechnicalRisk(this, "using_both_manholes_possible");
                }else if(!expert.isUsing_both_manholes_possible()){
                    method.setNotSuitable();
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }            
        });
        assessment_level_rules_2.put("requires_water_law_permission", new Rule("requires_water_law_permission",2,"water_law_permission_possible") {
            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if(!(Boolean)norm_value){
                    return;
                }
                
                if(expert.isWater_law_permission_possible()==null){
                    method.addTechnicalRisk(this,"water_law_permission_possible");
                }else if(!expert.isWater_law_permission_possible()){
                    method.setNotSuitable();
                }
            }
            
            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                boolean relevant = false;
                for(Environment environment : abstracted_case.getEnvironments()){
                    if(environment.isWater_protection_area()){
                        relevant = true;
                    }
                }
                return relevant;
            }
        });        
        assessment_level_rules_2.put("requires_exclavation_pit", new Rule("requires_exclavation_pit",2,"excavation_pit_possible",
                "excavation_pit_possible", "importance_of_surface_infrastucture") {
            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if(!(Boolean)norm_value){
                    return;
                }
                
                if(expert.isExcavation_pit_possible()==null){
                    method.addTechnicalRisk(this, "excavation_pit_possible");
                }else if(!expert.isExcavation_pit_possible()){
                    method.setNotSuitable();
                }else{
                    for(Environment environment : abstracted_case.getEnvironments()){
                        if(environment.getLand_use().equals(LandUse.BUILDING) || 
                                environment.getLand_use().equals(LandUse.WATERWAY) ||
                                 (expert.getImportance_of_surface_infrastucture() != null &&
                                    expert.getImportance_of_surface_infrastucture().equals(Scale.FULL))){
                            method.setNotSuitable();
                        }else if(!environment.getLand_use().equals(LandUse.FIELD) &&
                                !environment.getLand_use().equals(LandUse.FOREST) ){
                            if(expert.getSurface_condition()==null || 
                                    !expert.getSurface_condition().equals(SurfaceCondition.REQUIRES_RECONSTRUCTION) ||
                                        !expert.getSurface_condition().equals(SurfaceCondition.BELOW_AVERAGE)){
                                method.addTechnicalRisk(this, "excavation_pit_possible");
                            }
                            if(expert.getImportance_of_surface_infrastucture()==null || 
                                    !expert.getImportance_of_surface_infrastucture().equals(Scale.NONE) ||
                                        !expert.getImportance_of_surface_infrastucture().equals(Scale.LOW)){
                                method.addExternalRisk(this, "importance_of_surface_infrastucture");
                            }
                        }
                    }
                }
            }
            
            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        
        assessment_level_rules_3.put("required_space", new Rule("required_space",3,"available_space") {

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case,Expert expert) {
                if(norm_value.equals(Scale.NONE) || norm_value.equals(Scale.LOW)){
                    return;
                }
                
                if(expert.getAvailable_space() == null){
                    method.addExternalRisk(this, "available_space");
                }else if(norm_value.equals(Scale.HIGH)){
                    if(!expert.getAvailable_space().equals(Scale.FULL) &&
                            !expert.getAvailable_space().equals(Scale.HIGH) ){
                        method.addExternalRisk(this, "available_space");
                    }
                }
            }
            
            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }        
        });
        assessment_level_rules_3.put("creates_noise_and_dust", new Rule("creates_noise_and_dust",3,"avoid_noise_and_dust"){
            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case,
                    Expert expert) {
                if(norm_value.equals(Scale.NONE) || norm_value.equals(Scale.LOW) ||
                        (expert.getAvoid_noise_and_dust() != null &&
                        (expert.getAvoid_noise_and_dust().equals(Scale.LOW) ||
                        expert.getAvoid_noise_and_dust().equals(Scale.NONE) ))){
                    return;
                }
                
                if(expert.getAvoid_noise_and_dust() == null){
                    method.addExternalRisk(this, "avoid_noise_and_dust");
                }else if(expert.getAvoid_noise_and_dust().equals(Scale.FULL) ||
                        expert.getAvoid_noise_and_dust().equals(Scale.HIGH)){
                    method.addExternalRisk(this, "avoid_noise_and_dust");
                }else if(expert.getAvoid_noise_and_dust().equals(Scale.MEDIUM)){
                    if(!norm_value.equals(Scale.MEDIUM)){
                        method.addExternalRisk(this, "avoid_noise_and_dust"); 
                    }
                }                    
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_3.put("creates_vibration", new Rule("creates_vibration",3,"avoid_vibration"){
            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case,
                    Expert expert) {
                if(norm_value.equals(Scale.NONE)){
                    return;
                }
                
                if(expert.getAvoid_vibration() == null){
                    method.addExternalRisk(this, "avoid_vibration");
                }else if(expert.getAvoid_vibration().equals(Scale.FULL)){
                    method.addExternalRisk(this, "avoid_vibration");
                }else if(expert.getAvoid_vibration().equals(Scale.HIGH)){
                    if(!norm_value.equals(Scale.LOW)){
                        method.addExternalRisk(this, "avoid_vibration"); 
                    }
                }else if(expert.getAvoid_vibration().equals(Scale.MEDIUM)){
                    if(!norm_value.equals(Scale.LOW)&&!norm_value.equals(Scale.MEDIUM)){
                        method.addExternalRisk(this, "avoid_vibration"); 
                    }
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_3.put("impact_on_soil_and_trees", new Rule("impact_on_soil_and_trees",3,"protective_trees_present"){
            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case,
                    Expert expert) {
                if(!(Boolean) norm_value){
                    return;
                }
                
                if(expert.getProtective_trees_present()==null ||
                        expert.getProtective_trees_present()){
                    method.addExternalRisk(this, "protective_trees_present");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        
        assessment_level_rules_4.put("costs_100", new CostsRule("costs_100",4,100));
        assessment_level_rules_4.put("costs_200", new CostsRule("costs_200",4,200));
        assessment_level_rules_4.put("costs_300", new CostsRule("costs_300",4,300));
        assessment_level_rules_4.put("costs_400", new CostsRule("costs_400",4,400));
        assessment_level_rules_4.put("costs_500", new CostsRule("costs_500",4,500));
        assessment_level_rules_4.put("costs_600", new CostsRule("costs_600",4,600));
        assessment_level_rules_4.put("costs_700", new CostsRule("costs_700",4,700));
        assessment_level_rules_4.put("costs_800", new CostsRule("costs_800",4,800));
        assessment_level_rules_4.put("preparation_work_WATER_LAW_PERMISSION", new Rule("preparation_work_WATER_LAW_PERMISSION", 4){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    method.addCosts(1000);
                    method.addPreparation_work("Water Law Permission");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                boolean relevant = false;
                for(Environment environment : abstracted_case.getEnvironments()){
                    if( environment.isWater_protection_area() ){
                        relevant = true;
                    }
                }
                return relevant;
            }
        });
        assessment_level_rules_4.put("preparation_work_DEFORMATION_REPORT", new Rule("preparation_work_DEFORMATION_REPORT", 4){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    boolean in_danger = false;
                    for(ProtocolEntityType entityType : abstracted_case.getCount().keySet()){
                        if( entityType.equals(ProtocolEntityType.BAA) ||
                                entityType.equals(ProtocolEntityType.BAB) ||
                                entityType.equals(ProtocolEntityType.BAC) ){
                            in_danger = true;
                        }
                    }
                    if(in_danger){
                        for(Sewer sewer : abstracted_case.getSewers()){
                            method.addCosts(2*sewer.getLength_m());
                        }
                        method.addPreparation_work("Deformation Report");
                    }
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_4.put("preparation_work_STATIC_REPORT", new Rule("preparation_work_STATIC_REPORT", 4,"static_load_capacity_sufficient"){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    if(expert.isStatic_load_capacity_sufficient()==null){
                        method.addCosts(500);
                        method.addPreparation_work("Static Report");
                    }
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_4.put("preparation_work_SOIL_REPORT", new Rule("preparation_work_SOIL_REPORT", 4, "bedding_sufficient"){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    if(expert.isBedding_sufficient()==null){
                        method.addCosts(500);
                        method.addPreparation_work("Soil Report");
                    }
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_4.put("preparation_work_REMOVE_OBSTACLES", new Rule("preparation_work_REMOVE_OBSTACLES", 4){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    method.addCosts(abstracted_case.getCount(ProtocolEntityType.BAG)*200);
                    method.addCosts(abstracted_case.getCount(ProtocolEntityType.BBA)*200);
                    method.addCosts(abstracted_case.getCount(ProtocolEntityType.BBE)*200);
                    method.addPreparation_work("Remove Obstacles, Roots");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_4.put("preparation_work_REMOVE_ADHESIVE_MATERIAL", new Rule("preparation_work_REMOVE_ADHESIVE_MATERIAL", 4){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    method.addCosts(abstracted_case.getCount(ProtocolEntityType.BBB)*200);
                    method.addPreparation_work("Remove Adhesive Materials");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_4.put("preparation_work_FILLING_OF_CAVITY", new Rule("preparation_work_FILLING_OF_CAVITY", 4){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    method.addCosts(abstracted_case.getCount(ProtocolEntityType.BAP)*200);
                    method.addPreparation_work("Fill of Cavities");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_4.put("preparation_work_FIX_GROUWATER_INFILTRATION", new Rule("preparation_work_FIX_GROUWATER_INFILTRATION", 4){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    method.addCosts(abstracted_case.getCount(ProtocolEntityType.BBF)*200);
                    method.addCosts(abstracted_case.getCount(ProtocolEntityType.BBG)*200);
                    method.addPreparation_work("Fix Groundwater Infiltration");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_4.put("preparation_work_DRAINAGE_BLOCKING", new Rule("preparation_work_DRAINAGE_BLOCKING", 4){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    method.addCosts(100);
                    method.addPreparation_work("Install Temporary Drainage Barrier");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_4.put("preparation_work_SURFACE_REROUTING", new Rule("preparation_work_SURFACE_REROUTING", 4){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    method.addCosts(((Double)method.getProperty("duration"))*200);
                    method.addPreparation_work("Install Surface Water Rerouting");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_4.put("preparation_work_GROUNDWATER_LOWERING", new Rule("preparation_work_GROUNDWATER_LOWERING", 4){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    boolean in_groundwater = false;
                    for(Sewer sewer : abstracted_case.getSewers()){
                        if( sewer.isIn_groundwater() ){
                            method.addCosts(4000);
                            in_groundwater = true;
                        }
                    }
                    if(in_groundwater){
                        method.addPreparation_work("Groundwater Lowering");
                    }
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_4.put("finishing_work_RECREATE_LATERAL_CONNECTION_INSIDE", new Rule("finishing_work_RECREATE_LATERAL_CONNECTION_INSIDE", 4){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if(norm_value.equals("~")){
                    method.addTechnicalRisk(this, "finishing_work_RECREATE_LATERAL_CONNECTION_INSIDE");
                    norm_value = false;
                }
                
                if((Boolean)norm_value){
                    method.addCosts(abstracted_case.getCount(ProtocolEntityType.BCA)*500);
                    method.addFinishing_work("Recreate Lateral Connection from Inside");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_4.put("finishing_work_RECREATE_LATERAL_CONNECTION_OUTSIDE", new Rule("finishing_work_RECREATE_LATERAL_CONNECTION_OUTSIDE", 4){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if(norm_value.equals("~")){
                    method.addTechnicalRisk(this, "finishing_work_RECREATE_LATERAL_CONNECTION_OUTSIDE");
                    norm_value = false;
                }
                
                if((Boolean)norm_value){
                    method.addCosts(abstracted_case.getCount(ProtocolEntityType.BCA)*1500);
                    method.addFinishing_work("Recreate Lateral Connection from Outside");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        
        assessment_level_rules_5.put("preparation_work_HYDRAULIC_DIMENSIONING", new Rule("preparation_work_HYDRAULIC_DIMENSIONING", 5){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    if(expert.isHydraulic_capacity_increase_required() == null ||
                            expert.isHydraulic_capacity_reduction_possible() == null){
                        method.addCosts(1500);
                        method.addPreparation_work("Hydraulic Dimensioning");
                    }
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_5.put("preparation_work_STATIC_DIMENSIONING", new Rule("preparation_work_STATIC_DIMENSIONING", 5){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    int previous_dimension = 0;
                    for(Sewer sewer : abstracted_case.getSewers()){
                        if(previous_dimension == 0 || previous_dimension!=sewer.getDimension_mm()){
                            previous_dimension = sewer.getDimension_mm();
                            method.addCosts(500);
                        }
                    }
                    method.addPreparation_work("Static Dimensioning");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_5.put("preparation_work_CLEANING", new Rule("preparation_work_CLEANING", 5){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    for(Sewer sewer : abstracted_case.getSewers()){
                        method.addCosts(2*sewer.getLength_m());
                    }
                    method.addPreparation_work("Cleaning");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_5.put("finishing_work_INSPECTION", new Rule("finishing_work_INSPECTION", 5){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    method.addCosts(200*abstracted_case.getSewers().size());
                    method.addFinishing_work("Optical Inspection");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_5.put("finishing_work_LEAKAGE_TEST", new Rule("finishing_work_LEAKAGE_TEST", 5){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    method.addCosts(200*abstracted_case.getSewers().size());
                    method.addFinishing_work("Leakage Test");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_5.put("finishing_work_CREATE_SHAFT_CONNECTION", new Rule("finishing_work_CREATE_SHAFT_CONNECTION", 5){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                if((Boolean)norm_value){
                    method.addCosts(abstracted_case.getCount(ProtocolEntityType.BCD)*200);
                    method.addFinishing_work("Create Shaft Connections");
                }
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_5.put("lifetime_min", new Rule("lifetime_min", 5){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                method.setMin_lifetime((Double)norm_value);
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
        assessment_level_rules_5.put("lifetime_max", new Rule("lifetime_max", 5){

            @Override
            public void evaluateRule(Object norm_value, ReconstructionMethod method, Case abstracted_case, Expert expert) {
                method.setMax_lifetime((Double)norm_value);
            }

            @Override
            protected boolean isRelevant(Case abstracted_case, Expert expert) {
                return true;
            }
        });
    }
}
