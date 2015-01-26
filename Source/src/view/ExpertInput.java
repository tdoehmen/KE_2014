package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import model.Expert;
import model.enums.Scale;
import model.enums.SurfaceCondition;
import control.Controller;

public class ExpertInput extends JDialog {
    private HashMap<String, JComboBox<String>> comboBoxs;
    private JButton btnOk;

    /**
     * Create the dialog.
     * @param assessment_level 
     * @param infos 
     */
    public ExpertInput(Set<String> infos, int assessment_level, final Controller controller) {
        comboBoxs = new HashMap<String, JComboBox<String>>();

        /*ImageIcon img = new ImageIcon(getClass().getClassLoader().getResource("/images/expert.gif"));
        setIconImage(img.getImage());*/
        String description = "";
        switch(assessment_level){
        case 1:
        case 2:
            description = "Technical Possibility";
            break;
        case 3:
        case 4:
            description = "Risks";
            break;
        case 5:
        case 6:
            description = "Costs";
            break;
        }
        setTitle("Assessment Level: "+assessment_level+" ("+description+")");
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        getContentPane().setLayout(gridBagLayout);
        
        int i = 0;
        for(String info : infos){
            String[] choice = null;
            String labelText = "";
            if(info.equals(Expert.available_space)){
                choice = Scale.valueStrings();
                labelText = "Available space: ";
            }else if(info.equals(Expert.avoid_noise_and_dust)){
                choice = Scale.valueStrings();
                labelText = "Avoid noise and dust: ";
            }else if(info.equals(Expert.avoid_vibration)){
                choice = Scale.valueStrings();
                labelText = "Avoid vibration: ";
            }else if(info.equals(Expert.bedding_sufficient)){
                choice = new String[]{"YES","NO"};
                labelText = "Bedding sufficient? ";
            }else if(info.equals(Expert.excavation_pit_possible)){
                choice = new String[]{"YES","NO"};
                labelText = "Excavation pit possible? ";
            }else if(info.equals(Expert.hydraulic_capacity_increase_required)){
                choice = new String[]{"YES","NO"};
                labelText = "Increase hydraulic capacity? ";
            }else if(info.equals(Expert.hydraulic_capacity_reduction_possible)){
                choice = new String[]{"YES","NO"};
                labelText = "Reduction of hydraulic capacity possible? ";
            }else if(info.equals(Expert.importance_of_surface_infrastucture)){
                choice = Scale.valueStrings();
                labelText = "Surface infrastructure importance: ";
            }else if(info.equals(Expert.protective_trees_present)){
                choice = new String[]{"YES","NO"};
                labelText = "Any protective trees? ";
            }else if(info.equals(Expert.static_load_capacity_sufficient)){
                choice = new String[]{"YES","NO"};
                labelText = "Static load capacity compromised? ";
            }else if(info.equals(Expert.surface_condition)){
                choice = SurfaceCondition.valueStrings();
                labelText = "Street condition: ";
            }else if(info.equals(Expert.using_both_manholes_possible)){
                choice = new String[]{"YES","NO"};
                labelText = "Both manholes accessible? ";
            }else if(info.equals(Expert.water_law_permission_possible)){
                choice = new String[]{"YES","NO"};
                labelText = "Water law permission possible? ";
            }
            
            JLabel lblExpertinfotext = new JLabel(labelText);

            GridBagConstraints gbc_lblExpertinfotext = new GridBagConstraints();
            gbc_lblExpertinfotext.insets = new Insets(0, 0, 5, 5);
            gbc_lblExpertinfotext.anchor = GridBagConstraints.EAST;
            gbc_lblExpertinfotext.gridx = 0;
            gbc_lblExpertinfotext.gridy = i;
            getContentPane().add(lblExpertinfotext, gbc_lblExpertinfotext);
            
            JComboBox<String> comboBox = new JComboBox<String>();
            DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<String>(choice);
            comboModel.addElement("?");
            comboBox.setModel(comboModel);
            GridBagConstraints gbc_comboBox = new GridBagConstraints();
            gbc_comboBox.insets = new Insets(0, 0, 5, 0);
            gbc_comboBox.anchor = GridBagConstraints.EAST;
            gbc_comboBox.gridx = 1;
            gbc_comboBox.gridy = i;
            getContentPane().add(comboBox, gbc_comboBox);
            comboBox.setSelectedItem("?");
            comboBoxs.put(info, comboBox);
            
            i++;
        }

        btnOk = new JButton("OK");
        GridBagConstraints gbc_btnOk = new GridBagConstraints();
        gbc_btnOk.gridx = 1;
        gbc_btnOk.gridy = i;
        getContentPane().add(btnOk, gbc_btnOk);
        btnOk.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                HashMap<String, Object> infos = new HashMap<String, Object>();
                for(String info : comboBoxs.keySet()){
                    String value_string = (String)comboBoxs.get(info).getSelectedItem();
                    if(!value_string.equals("?")){
                        Object value = null;
                        if(info.equals(Expert.available_space)){
                            value = Scale.valueOf(value_string);
                        }else if(info.equals(Expert.avoid_noise_and_dust)){
                            value = Scale.valueOf(value_string);
                        }else if(info.equals(Expert.avoid_vibration)){
                            value = Scale.valueOf(value_string);
                        }else if(info.equals(Expert.bedding_sufficient)){
                            value = value_string.equals("YES") ? true : false; 
                        }else if(info.equals(Expert.excavation_pit_possible)){
                            value = value_string.equals("YES") ? true : false; 
                        }else if(info.equals(Expert.hydraulic_capacity_increase_required)){
                            value = value_string.equals("YES") ? true : false; 
                        }else if(info.equals(Expert.hydraulic_capacity_reduction_possible)){
                            value = value_string.equals("YES") ? true : false; 
                        }else if(info.equals(Expert.importance_of_surface_infrastucture)){
                            value = Scale.valueOf(value_string);
                        }else if(info.equals(Expert.protective_trees_present)){
                            value = value_string.equals("YES") ? true : false; 
                        }else if(info.equals(Expert.static_load_capacity_sufficient)){
                            value = value_string.equals("YES") ? true : false; 
                        }else if(info.equals(Expert.surface_condition)){
                            value = SurfaceCondition.valueOf(value_string);
                        }else if(info.equals(Expert.using_both_manholes_possible)){
                            value = value_string.equals("YES") ? true : false; 
                        }else if(info.equals(Expert.water_law_permission_possible)){
                            value = value_string.equals("YES") ? true : false; 
                        }
                        infos.put(info, value);
                        
                    }
                }
                controller.saveInfosFromExpertDialog(infos);
                dispose();
            }
        });
        
        pack();
    }

}
