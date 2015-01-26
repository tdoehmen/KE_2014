package control;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import model.Case;
import model.Environment;
import model.Expert;
import model.InspectionProtocol;
import model.InspectionProtocol.InspectionProtocolEntity;
import model.ReconstructionMethod;
import model.ReconstructionMethods;
import model.Sewer;
import model.SewerDatabase;
import control.Rules.Rule;


public class Assessment {
    private Expert expert;
    private Rules rules;
    private Controller controller;
    
    public Assessment(Expert expert, Controller controller){
        this.expert = expert;
        this.rules = new Rules();
        this.controller = controller;
    }
    
    public void start(SewerDatabase database, InspectionProtocol protocol, ReconstructionMethods methods){
        Case abstracted_case = abstract_case(database, protocol);
        List<ReconstructionMethod> candidate_set = null;
        for(int assessment_level = 0; assessment_level<6; assessment_level++) {
            List<Rule> norm_rules = specify_norms(abstracted_case, methods, assessment_level);
            while(!norm_rules.isEmpty()){
                Rule norm_rule = select(norm_rules);
                evaluate(norm_rule, methods, abstracted_case);
            } 
            candidate_set = match(methods,assessment_level);
            if(candidate_set != null && !candidate_set.isEmpty()){                    
                break;
            }
        }
        
        showResults(candidate_set);
    }

    public Case abstract_case(SewerDatabase database, InspectionProtocol protocol){
        Case abstract_case = new Case(protocol.getEntries(), protocol.getCount(), protocol.getLength(), protocol.getCharacter());
        for(InspectionProtocolEntity entity : protocol.getEntries()){
            Sewer sewer = database.getSewer().get(entity.getSewer_identifier());
            abstract_case.addSewer(sewer);
            Environment environment = database.getEnvironment().get(entity.getSewer_identifier());
            abstract_case.addEnvironment(environment);
        }
        
        return abstract_case;
    }
    
    public List<Rule> specify_norms(Case abstracted_case, ReconstructionMethods methods, int assessment_level){
        LinkedHashMap<String, Rule> assessment_level_rules = rules.getAssessment_level_rules(assessment_level);
        List<String> assessment_level_norms = new ArrayList<String>(assessment_level_rules.keySet());
        List<String> relevantNorms = null;
        if(assessment_level==5){
            relevantNorms = new ArrayList<String>(assessment_level_rules.keySet());
        }else{
            relevantNorms = methods.getNormsVaryingAmongMethods(assessment_level_norms);
        }
        ArrayList<Rule> relevantRules = new ArrayList<Rule>();
        LinkedHashSet<String> relevant_expert_information = new LinkedHashSet<String>();
        for(String relevantNorm : relevantNorms){
            Rule relevantRule = assessment_level_rules.get(relevantNorm);
            relevantRules.add(relevantRule);
            relevant_expert_information.addAll(Arrays.asList(relevantRule.getExpert_input()));
        }
        expert.getInformation(relevant_expert_information, assessment_level);
        return relevantRules;
    }
    
    public Rule select(List<Rule> norm_rules){
        Rule rule = norm_rules.get(0);
        norm_rules.remove(rule);
        return rule;
    }
    
    public void evaluate(Rule rule, ReconstructionMethods methods, Case abstracted_case){
        for(ReconstructionMethod method : methods.getMethodList()){
            if(!method.isSuitable()){
                continue;
            }
            rule.evaluate(method, abstracted_case, expert);
        }     
    }
    
    public List<ReconstructionMethod> match(ReconstructionMethods methods, int assessment_level){
        //always remove the unsuitable methods
        methods.removeUnsuitableMethods();
        
        //if all assessment steps are done
        if(assessment_level == 5){
            /*
             * method.feasible = true
                method.total_costs = MIN(other_methods.costs)
                    INDICATES
                method.isCandidate = true
                
                method.feasible = true
                method.total_costs = MIN(remaining_methods.costs)
                method.yearly_costs < MIN(candidates.yearly_costs)
                    INDICATES
                method.isCandidate = true
                
                method.feasible = true
                method.total_costs = MIN(remaining_methods.costs)
                method.technical_risks.count < MIN(candidates.technical_risks.count)
                    INDICATES
                method.isCandidate = true
                
                method.feasible = true
                method.total_costs = MIN(remaining_methods.costs)
                method.external_risks.count < MIN(candidates.external_risks.count)
                    INDICATES
                method.isCandidate = true

             */
           //List<ReconstructionMethod> candidate_set = methods.getMethodList();
            List<ReconstructionMethod> candidate_set = new ArrayList<ReconstructionMethod>();

            ReconstructionMethod cheapestMethod = methods.getCheapestMethod();
            candidate_set.add(cheapestMethod);
            methods.getMethodList().remove(cheapestMethod);
            
            cheapestMethod = methods.getCheapestMethod();
            boolean better = true;
            for(ReconstructionMethod candidate : candidate_set){
                if(!(cheapestMethod.getCostsPa()<candidate.getCostsPa())){
                    better = false;
                }
            }
            if(better){
                candidate_set.add(cheapestMethod);
                methods.getMethodList().remove(cheapestMethod);
            }
            
            cheapestMethod = methods.getCheapestMethod();
            better = true;
            for(ReconstructionMethod candidate : candidate_set){
                if(!(cheapestMethod.getTechnicalRisks().size()<candidate.getTechnicalRisks().size())){
                    better = false;
                }
            }
            if(better){
                candidate_set.add(cheapestMethod);
                methods.getMethodList().remove(cheapestMethod);
            }
            
            cheapestMethod = methods.getCheapestMethod();
            better = true;
            for(ReconstructionMethod candidate : candidate_set){
                if(!(cheapestMethod.getExternalRisks().size()<candidate.getExternalRisks().size())){
                    better = false;
                } 
            }
            if(better){
                candidate_set.add(cheapestMethod);
                methods.getMethodList().remove(cheapestMethod);
            }
       
            
            return candidate_set;
        }
        
        return null;
    }
    
    public void showResults(List<ReconstructionMethod> methods){
        controller.showResultsInGUI(methods);
    }
    
}
