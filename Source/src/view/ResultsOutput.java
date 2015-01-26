package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import model.ReconstructionMethod;

import com.itextpdf.text.DocumentException;

import control.Controller;
import control.PdfOutput;

public class ResultsOutput extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTable table;
    private Controller controller;

    /**
     * Create the dialog.
     * @param methods 
     */
    public ResultsOutput(List<ReconstructionMethod> methods, Controller controller) {
        this.controller = controller;
        setTitle("Assessment Result");
        setBounds(100, 100, 450, 300);
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setLayout(new FlowLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
                
        int max_technical_risk = 0;
        int max_external_risk = 0;
        int max_prep_work = 0;
        int max_finish_work = 0;
        for(ReconstructionMethod method : methods){
            if(method.getTechnicalRisks().size() > max_technical_risk){
                max_technical_risk = method.getTechnicalRisks().size();
            }
            if(method.getExternalRisks().size() > max_external_risk){
                max_external_risk = method.getExternalRisks().size();
            }
            if(method.getPreparation_work().size() > max_prep_work){
                max_prep_work = method.getPreparation_work().size();
            }
            if(method.getFinishing_work().size() > max_finish_work){
                max_finish_work = method.getFinishing_work().size();
            }
        }
        
        String[] columnNames = new String[max_technical_risk+max_external_risk+max_prep_work+max_finish_work+4];
        columnNames[0] = "Technical Risks";
        columnNames[max_technical_risk] = "External Risks";
        columnNames[max_technical_risk+max_external_risk] = "Preparation Work";
        columnNames[max_technical_risk+max_external_risk+max_prep_work] = "Finishing Work";
        columnNames[max_technical_risk+max_external_risk+max_prep_work+max_finish_work] = "Min. Lifetime";
        columnNames[max_technical_risk+max_external_risk+max_prep_work+max_finish_work+1] = "Max. Lifetime";
        columnNames[max_technical_risk+max_external_risk+max_prep_work+max_finish_work+2] = "Total Costs";
        columnNames[max_technical_risk+max_external_risk+max_prep_work+max_finish_work+3] = "~ Costs p.a.";
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Method Name", columnNames);
        for(ReconstructionMethod method : methods){
            columnNames = new String[max_technical_risk+max_external_risk+max_prep_work+max_finish_work+4];
            int i = 0;
            for(String technical_risk : method.getTechnicalRisks().values()){
                columnNames[i] =  technical_risk;
                i++;
            }
            i = max_technical_risk;
            for(String external_risk : method.getExternalRisks().values()){
                columnNames[i] =  external_risk;
                i++;
            }
            i = max_technical_risk+max_external_risk;
            for(String preparation_work : method.getPreparation_work()){
                columnNames[i] =  preparation_work;
                i++;
            }
            i = max_technical_risk+max_external_risk+max_prep_work;
            for(String finishing_work : method.getFinishing_work()){
                columnNames[i] =  finishing_work;
                i++;
            }
            i = max_technical_risk+max_external_risk+max_prep_work+max_finish_work;
            columnNames[i] = (int)method.getMin_lifetime()+"";
            i++;
            columnNames[i] = (int)method.getMax_lifetime()+"";
            i++;
            columnNames[i] = (int)method.getCosts()+"";
            i++;
            columnNames[i] = (int)method.getCostsPa()+"";
            i++;
            
            model.addColumn(method.getName(), columnNames);
        }
        
        table = new JTable(model){
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
             }
            
            public boolean getScrollableTracksViewportWidth()
            {
                return getPreferredSize().width < getParent().getWidth();
            }
        };
        table.setShowGrid(false);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable x, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                boolean selected = table.getSelectionModel().isSelectedIndex(row);
                Component component = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(table, value, false, false, -1, -2);
                ((JLabel) component).setHorizontalAlignment(SwingConstants.CENTER);
                if (selected) {
                    component.setFont(component.getFont().deriveFont(Font.BOLD));
                } else {
                    component.setFont(component.getFont().deriveFont(Font.PLAIN));
                }
                return component;
            }
        });
        
        final JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton saveButton = new JButton("Save as PDF");
                saveButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //Create a file chooser
                        final JFileChooser fc = new JFileChooser();
                        int returnVal = fc.showSaveDialog(contentPanel);
                        
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try {
                                int width = scrollPane.getWidth();
                                int height = scrollPane.getHeight();
                                scrollPane.setSize((int) table.getPreferredSize().getWidth()+20, height);
                                scrollPane.doLayout();
                                PdfOutput.write(file, scrollPane);
                                scrollPane.setSize(width, height);
                                JOptionPane.showMessageDialog(contentPanel, "PDF successfully created.");
                            } catch (FileNotFoundException e1) {
                                JOptionPane.showMessageDialog(contentPanel, "Creating PDF failed. ("+e1.getMessage()+")");
                            } catch (DocumentException e1) {
                                JOptionPane.showMessageDialog(contentPanel, "Creating PDF failed. ("+e1.getMessage()+")");
                            }
                        } 
                    }
                });
                buttonPane.add(saveButton);
            }
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
        }
        
        pack();
        }

}
