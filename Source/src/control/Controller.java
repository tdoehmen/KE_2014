package control;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import model.Expert;
import model.InspectionProtocol;
import model.ReconstructionMethod;
import model.ReconstructionMethods;
import model.SewerDatabase;
import view.View;


public class Controller{

    private View window;
    private File sewerDatabase;
    private File inspectionProtocol;
    private File methodsFile;
    private Assessment assessment;
    private Expert expert;
    
    public Controller(View window){
        this.window = window;
        methodsFile = new File("methods.xlsx");
    }
    
    public File getSewerDatabase() {
        return sewerDatabase;
    }

    public void setSewerDatabase(File sewerDatabase) {
        this.sewerDatabase = sewerDatabase;
        window.updateDBPath(sewerDatabase.getAbsolutePath());
    }

    public File getInspectionProtocol() {
        return inspectionProtocol;
    }

    public void setInspectionProtocol(File inspectionProtocol) {
        this.inspectionProtocol = inspectionProtocol;
        window.updateProtocolPath(inspectionProtocol.getAbsolutePath());
    }
    
    public void startMethodAssessment(){
        SewerDatabase database = new SewerDatabase(sewerDatabase);
        InspectionProtocol protocol = new InspectionProtocol(inspectionProtocol);
        ReconstructionMethods methods = new ReconstructionMethods(methodsFile);
        
        expert = new Expert(this);
        
        assessment = new Assessment(expert,this);
        assessment.start(database, protocol, methods);
    }

    public void showResultsInGUI(List<ReconstructionMethod> methods) {
        if(methods.isEmpty()){
            window.showNoResultsMessage();
        }else{
            window.showResultsInGUI(methods);
        }
    }

    public void openExpertDialog(Set<String> infos, int assessment_level) {
        window.openExpertDialog(infos, assessment_level);
    }

    public void saveInfosFromExpertDialog(HashMap<String, Object> infos) {
        expert.setInformation(infos);
    }
    
    
}
