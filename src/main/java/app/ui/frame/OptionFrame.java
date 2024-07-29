package app.ui.frame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import app.http.HttpOption;

public class OptionFrame extends JFrame {
	
	private JPanel optionPanel;
	private HttpOption httpOption;
	private Properties properties;
	private GridBagLayout bagLayout;
	private GridBagConstraints gConstraints;

	
	public OptionFrame(HttpOption option) {
		this.httpOption = option;
		this.properties = option.getProperties();
		this.optionPanel = new JPanel();
		
		setTitle("JDK Settings");
		
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        bagLayout = new GridBagLayout();
        gConstraints = new GridBagConstraints();
        gConstraints.fill = GridBagConstraints.BOTH;
        optionPanel.setLayout(bagLayout);
        add(optionPanel);

        // First row: JDK6 Path
        JLabel jdk6Label = new JLabel("JDK6 Path : ");
        JTextField jdk6TextField = new JTextField();
        jdk6TextField.setText(properties.getProperty(httpOption.JDK6_PATH));
		insertGrid(jdk6Label, 0, 0, 1, 1, 0.05, 1, new Insets(2, 2, 2, 2));
		insertGrid(jdk6TextField, 1, 0, 1, 1, 0.95, 1, new Insets(2, 2, 2, 2));
        
        
        JLabel jdk6jarLabel = new JLabel("JDK6 Jar Path : ");
        JTextField jdk6JarTextField = new JTextField();
        jdk6JarTextField.setText(properties.getProperty(httpOption.JDK6_JAR_PATH));
		insertGrid(jdk6jarLabel, 0, 1, 1, 1, 0.05, 1, new Insets(2, 2, 2, 2));
		insertGrid(jdk6JarTextField, 1, 1, 1, 1, 0.95, 1, new Insets(2, 2, 2, 2));


        // Second row: JDK7 Path
        JLabel jdk7Label = new JLabel("JDK7 Path : ");
        JTextField jdk7TextField = new JTextField();
        jdk7TextField.setText(properties.getProperty(httpOption.JDK7_PATH));
		insertGrid(jdk7Label, 0, 2, 1, 1, 0.05, 1, new Insets(2, 2, 2, 2));
		insertGrid(jdk7TextField, 1, 2, 1, 1, 0.95, 1, new Insets(2, 2, 2, 2));

        
        JLabel jdk7jarLabel = new JLabel("JDK7 Jar Path : ");
        JTextField jdk7JarTextField = new JTextField();
        jdk7JarTextField.setText(properties.getProperty(httpOption.JDK7_JAR_PATH));
		insertGrid(jdk7jarLabel, 0, 3, 1, 1, 0.05, 1, new Insets(2, 2, 2, 2));
		insertGrid(jdk7JarTextField, 1, 3, 1, 1, 0.95, 1, new Insets(2, 2, 2, 2));



        // Third row: JDK8 Path
        JLabel jdk8Label = new JLabel("JDK8 Path : ");
        JTextField jdk8TextField = new JTextField();
        jdk8TextField.setText(properties.getProperty(httpOption.JDK8_PATH));
		insertGrid(jdk8Label, 0, 4, 1, 1, 0.05, 1, new Insets(2, 2, 2, 2));
		insertGrid(jdk8TextField, 1, 4, 1, 1, 0.95, 1, new Insets(2, 2, 2, 2));

        
        JLabel jdk8jarLabel = new JLabel("JDK8 Jar Path : ");
        JTextField jdk8JarTextField = new JTextField();
        jdk8JarTextField.setText(properties.getProperty(httpOption.JDK8_JAR_PATH));
		insertGrid(jdk8jarLabel, 0, 5, 1, 1, 0.05, 1, new Insets(2, 2, 2, 2));
		insertGrid(jdk8JarTextField, 1, 5, 1, 1, 0.95, 1, new Insets(2, 2, 2, 2));

        
        
        JButton saveButton = new JButton("Save");
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	properties.setProperty(httpOption.JDK6_PATH, jdk6TextField.getText());
            	properties.setProperty(httpOption.JDK7_PATH, jdk7TextField.getText());
            	properties.setProperty(httpOption.JDK8_PATH, jdk8TextField.getText());
            	
            	properties.setProperty(httpOption.JDK6_JAR_PATH, jdk6JarTextField.getText());
            	properties.setProperty(httpOption.JDK7_JAR_PATH, jdk7JarTextField.getText());
            	properties.setProperty(httpOption.JDK8_JAR_PATH, jdk8JarTextField.getText());

            	try {
					httpOption.save();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
	            	JOptionPane.showMessageDialog(null, "Error!", "Info", JOptionPane.ERROR_MESSAGE);
				}
            	
            	JOptionPane.showMessageDialog(null, "Saved!", "Info", JOptionPane.PLAIN_MESSAGE);
            }
        });
        
		insertGrid(saveButton, 0, 6, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2));

        setSize(800, 300);
        
		Dimension frameSize = getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);


        setVisible(true);
		
	}
	
	private void insertGrid(Component component, int x, int y, int w, int h, double wx, double wy,
			int anchor, int fill, Insets insets) {
		gConstraints.gridx = x;
		gConstraints.gridy = y;
		gConstraints.anchor = anchor;
		gConstraints.fill = fill;
		gConstraints.gridwidth = w;
		gConstraints.gridheight = h;
		gConstraints.weightx = wx;
		gConstraints.weighty = wy;
		gConstraints.insets = insets;
		optionPanel.add(component, gConstraints);
		bagLayout.setConstraints(component, gConstraints);
	}

	
	private void insertGrid(Component component, int x, int y, int w, int h, double wx, double wy,
			Insets insets) {
		gConstraints.gridx = x;
		gConstraints.gridy = y;
		gConstraints.anchor = (x == 0) ? GridBagConstraints.EAST : GridBagConstraints.WEST;
		gConstraints.fill = (x == 0) ? GridBagConstraints.NONE : GridBagConstraints.BOTH;
		gConstraints.gridwidth = w;
		gConstraints.gridheight = h;
		gConstraints.weightx = wx;
		gConstraints.weighty = wy;
		gConstraints.insets = insets;
		optionPanel.add(component, gConstraints);
		bagLayout.setConstraints(component, gConstraints);
	}
	
}
