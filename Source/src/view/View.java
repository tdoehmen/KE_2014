package view;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import model.ReconstructionMethod;
import control.Controller;


public class View {

    private JFrame frmEntscheidungswissenSanierungsverfahren;
    private JLabel lblSewerDatabase;
    private JTextField textFieldDb;
    private JButton btnSelectDbFile;
    private JLabel lblInspectionProtocol;
    private JTextField textFieldProtocol;
    private JButton btnSelectProtocolFile;
    private JButton btnStartMethodAssessment;
    private final Action readSewerDatabase = new ReadSewerDatabaseAction();
    private final Action readInspectionProtocol = new ReadInspectionProtocolAction();
    private Controller controller;
    private final Action startAssessment = new StartMethodAssessment();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    View window = new View();
                    window.frmEntscheidungswissenSanierungsverfahren.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public View() {
        initialize();
        controller = new Controller(this);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmEntscheidungswissenSanierungsverfahren = new JFrame();
        frmEntscheidungswissenSanierungsverfahren.setTitle("Sewer Repair Assessment");
        frmEntscheidungswissenSanierungsverfahren.setBounds(100, 100, 535, 250);
        frmEntscheidungswissenSanierungsverfahren.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /*ImageIcon img = new ImageIcon(getClass().getResource("/images/expert.gif"));
        frmEntscheidungswissenSanierungsverfahren.setIconImage(img.getImage());*/
        
        lblSewerDatabase = new JLabel("Sewer Database");
        
        textFieldDb = new JTextField();
        textFieldDb.setEditable(false);
        textFieldDb.setColumns(10);
        
        btnSelectDbFile = new JButton();
        btnSelectDbFile.setAction(readSewerDatabase);
        
        lblInspectionProtocol = new JLabel("Inspection Protocol");
        
        textFieldProtocol = new JTextField();
        textFieldProtocol.setEditable(false);
        textFieldProtocol.setColumns(10);
        
        btnSelectProtocolFile = new JButton();
        btnSelectProtocolFile.setAction(readInspectionProtocol);
        
        btnStartMethodAssessment = new JButton("Start Method Assessment");
        btnStartMethodAssessment.setAction(startAssessment);

        GroupLayout groupLayout = new GroupLayout(frmEntscheidungswissenSanierungsverfahren.getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(134)
                    .addComponent(btnStartMethodAssessment, GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                    .addGap(139))
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(textFieldDb, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                        .addComponent(textFieldProtocol, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                    .addGap(14)
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(btnSelectProtocolFile, GroupLayout.PREFERRED_SIZE, 235, Short.MAX_VALUE)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGap(2)
                            .addComponent(btnSelectDbFile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addContainerGap())
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblInspectionProtocol)
                    .addContainerGap(417, Short.MAX_VALUE))
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblSewerDatabase)
                    .addContainerGap(430, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(12)
                    .addComponent(lblSewerDatabase)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(textFieldDb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSelectDbFile))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblInspectionProtocol)
                    .addGap(7)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(textFieldProtocol, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSelectProtocolFile))
                    .addGap(43)
                    .addComponent(btnStartMethodAssessment, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(23))
        );
        frmEntscheidungswissenSanierungsverfahren.getContentPane().setLayout(groupLayout);
    }

    private class ReadSewerDatabaseAction extends AbstractAction {
        public ReadSewerDatabaseAction() {
            putValue(NAME, "Select DB File...");
            putValue(SHORT_DESCRIPTION, "Some short description");
            //putValue(SMALL_ICON, new ImageIcon(getClass().getResource("../images/database.gif")));
        }
        public void actionPerformed(ActionEvent e) {
            //Create a file chooser
            final JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(frmEntscheidungswissenSanierungsverfahren);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                controller.setSewerDatabase(file);
            } 
        }
    }
    
    private class ReadInspectionProtocolAction extends AbstractAction {
        public ReadInspectionProtocolAction() {
            putValue(NAME, "Select Protocol File...");
            putValue(SHORT_DESCRIPTION, "Some short description");
            //putValue(SMALL_ICON, new ImageIcon(getClass().getResource("../images/protocol.gif")));
        }
        public void actionPerformed(ActionEvent e) {
          //Create a file chooser
            final JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(frmEntscheidungswissenSanierungsverfahren);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                controller.setInspectionProtocol(file);
            } 
        }
    }
    
    public void updateDBPath(String pathDb){
        textFieldDb.setText(pathDb);
    }
    
    public void updateProtocolPath(String pathProtocol){
        textFieldProtocol.setText(pathProtocol);
    }
    
    private class StartMethodAssessment extends AbstractAction {
        public StartMethodAssessment() {
            putValue(NAME, "Start Method Assessment");
            putValue(SHORT_DESCRIPTION, "Some short description");
        }
        public void actionPerformed(ActionEvent e) {
            controller.startMethodAssessment();
        }
    }
    
    public void showResultsInGUI(List<ReconstructionMethod> methods) {
        ResultsOutput resultsDialog = new ResultsOutput(methods, controller);
        resultsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        resultsDialog.setModal(true);
        resultsDialog.setLocationRelativeTo(frmEntscheidungswissenSanierungsverfahren);
        resultsDialog.setVisible(true);
    }
    
    public void showNoResultsMessage(){
        JOptionPane.showMessageDialog(frmEntscheidungswissenSanierungsverfahren, "No feasible method could be found.");
    }

    public void openExpertDialog(Set<String> infos, int assessment_level) {
        ExpertInput expertDialog = new ExpertInput(infos,assessment_level, controller);
        expertDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        expertDialog.setModal(true);
        expertDialog.setLocationRelativeTo(frmEntscheidungswissenSanierungsverfahren);
        expertDialog.setVisible(true);
    }
    
}
