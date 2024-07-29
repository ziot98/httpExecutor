package app.ui.frame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import app.executor.RedirectProcessExecutor;
import app.http.HttpOption;
import app.ui.component.AddAction;
import app.ui.component.ButtonColumn;
import app.ui.component.DeleteAction;
import app.util.JsonUtil;

public class MainFrame extends JFrame {

	private static MainFrame instance;
	
	private RedirectProcessExecutor client;
	private HttpOption option;
	
	private JPanel mainPanel;
	private GridBagLayout bagLayout;
	private GridBagConstraints gConstraints;
	private JTextArea resultTextArea;
	private JTable headerTable;
	private JTextArea bodyTextArea;
	private JComboBox<String> methodComboBox;
	private Properties properties;
	private int lineNum = 0;
	private String originalText = "";
	private JScrollPane bodyScrollPane;
	private JScrollPane resultScrollPane;
	private JLabel stateTextlabel;
	private JButton stopButton;
	private Thread processRunThread;

	
	public static MainFrame getInstance() {
		return instance;
	}

	public MainFrame(RedirectProcessExecutor client, HttpOption option) {
		this.client = client;
		this.option = option;
		this.properties = option.getProperties();
		this.mainPanel = new JPanel();
		
		MainFrame.instance = this;

		setTitle("HTTP Tool");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		createMenubar();
		

		bagLayout = new GridBagLayout();
		
		mainPanel.setLayout(bagLayout);

		gConstraints = new GridBagConstraints();
		gConstraints.fill = GridBagConstraints.BOTH;

		createJDKVersionSelection();
		createTLSVersionSelection();
		createURLInput();
		createMethodCombo();
		createHeaderTable();
		createBodyInput();
		createResultDisplay();
		createStateBar();
		createButtons();
		
		add(mainPanel);

		pack();
		setSize(getWidth() + 50, getHeight() + 40);
		//mainPanel.setPreferredSize(new Dimension(686, 870));
		//this.setSize(686, 870);
		
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
		mainPanel.add(component, gConstraints);
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
		mainPanel.add(component, gConstraints);
		bagLayout.setConstraints(component, gConstraints);
	}

	private void createMenubar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu optionMenu = new JMenu("Settings");
		JMenuItem optionMenuItem = new JMenuItem("JDK Settings");

		optionMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OptionFrame optionFrame = new OptionFrame(option);
			}
		});

		menuBar.add(optionMenu);
		optionMenu.add(optionMenuItem);

		this.setJMenuBar(menuBar);
	}

	private void createJDKVersionSelection() {
		// First Line: JDK Version Selection
		JLabel jdkLabel = new JLabel("JDK Version : ");
		JPanel boxPanel = new JPanel();
		boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.X_AXIS));
		String[] jdkVersions = { "6", "7", "8" };
		JComboBox<String> jdkComboBox = new JComboBox<>(jdkVersions);

		// Default version 8
		jdkComboBox.setSelectedIndex(option.getJdkVersion() - 6);

		jdkComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String item = (String) e.getItem();
					option.setJdkVersion(Integer.parseInt(item));
				}
			}
		});

		//jPanel.add(jdkComboBox);
		boxPanel.add(jdkComboBox);

		insertGrid(jdkLabel, 0, lineNum, 1, 1, 0.05, 0.037, new Insets(2, 2, 2, 2));
		insertGrid(boxPanel, 1, lineNum, 1, 1, 0.95, 0.037, GridBagConstraints.WEST
				, GridBagConstraints.NONE, new Insets(2, 2, 2, 2));
	}

	private void createTLSVersionSelection() {
		JLabel tlsLabel = new JLabel("TLS Version : ");
		
		JPanel boxPanel = new JPanel();
		boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.X_AXIS));
		
		JCheckBox tlsCheckBox10 = new JCheckBox(HttpOption.TLSv1);
		JCheckBox tlsCheckBox11 = new JCheckBox(HttpOption.TLSv1_1);
		JCheckBox tlsCheckBox12 = new JCheckBox(HttpOption.TLSv1_2);
		JCheckBox tlsCheckBox13 = new JCheckBox(HttpOption.TLSv1_3);

		tlsCheckBox10.addItemListener((e) -> HttpOption.setTLSv1(e
				.getStateChange() == ItemEvent.SELECTED));
		tlsCheckBox11.addItemListener((e) -> HttpOption.setTLSv1_1(e
				.getStateChange() == ItemEvent.SELECTED));
		tlsCheckBox12.addItemListener((e) -> HttpOption.setTLSv1_2(e
				.getStateChange() == ItemEvent.SELECTED));
		tlsCheckBox13.addItemListener((e) -> HttpOption.setTLSv1_3(e
				.getStateChange() == ItemEvent.SELECTED));

		tlsCheckBox10.setSelected(option.isTLSv1());
		tlsCheckBox11.setSelected(option.isTLSv1_1());
		tlsCheckBox12.setSelected(option.isTLSv1_2());
		tlsCheckBox13.setSelected(option.isTLSv1_3());

		boxPanel.add(tlsCheckBox10);
		boxPanel.add(tlsCheckBox11);
		boxPanel.add(tlsCheckBox12);
		boxPanel.add(tlsCheckBox13);

		lineNum++;

		insertGrid(tlsLabel, 0, lineNum, 1, 1, 0.05, 0.037, new Insets(2, 2, 2, 2));
		insertGrid(boxPanel, 1, lineNum, 1, 1, 0.95, 0.037, GridBagConstraints.WEST
				, GridBagConstraints.NONE, new Insets(2, 2, 2, 2));	}

	private void createURLInput() {
		// Second Line: URL Input
		JLabel urlLabel = new JLabel("URL : ");
		JTextField urlTextField = new JTextField();
		urlTextField.setText(option.getUrl());

		DocumentListener dl = new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				option.setUrl(urlTextField.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				option.setUrl(urlTextField.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				option.setUrl(urlTextField.getText());
			}
		};

		urlTextField.getDocument().addDocumentListener(dl);

		lineNum++;

		insertGrid(urlLabel, 0, lineNum, 1, 1, 0.05, 0.037, new Insets(2, 2, 2, 2));
		insertGrid(urlTextField, 1, lineNum, 1, 1, 0.95, 0.037, new Insets(2, 2, 2, 60));

	}

	private void createMethodCombo() {
		// Third Line: Method CombtjsQoBox
		JLabel methodLabel = new JLabel("Method : ");
		String[] methods = { "GET", "POST", "PUT", "DELETE" };
		JPanel boxPanel = new JPanel();
		boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.X_AXIS));
		
		methodComboBox = new JComboBox<>(methods);

		// Show/hide body input based on method selection
		methodComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedMethod = (String) methodComboBox
						.getSelectedItem();
				option.setMethod(selectedMethod);
				bodyTextArea.setEditable("PUT".equals(selectedMethod)
						|| "POST".equals(selectedMethod));
			}
		});

		boxPanel.add(methodComboBox);

		lineNum++;

		insertGrid(methodLabel, 0, lineNum, 1, 1, 0.05, 0.037, new Insets(2, 2, 2, 2));
		insertGrid(boxPanel, 1, lineNum, 1, 1, 0.95, 0.037, GridBagConstraints.WEST
				, GridBagConstraints.NONE, new Insets(2, 2, 2, 2));
	}

	private void createHeaderTable() {
		JLabel headerLabel = new JLabel("Header : ");
		String[] columnNames = { "Key", "Value", "", "" };

		JSONObject jsonObject = option.getHeader();

		DefaultTableModel model = null;

		if (jsonObject == null) {
			model = new DefaultTableModel(new String[][] { { "Content-Type",
					"application/json", "ADD", "DELETE" } }, columnNames);
		} else {
			Iterator<String> keys = jsonObject.keys();
			List<String[]> headerList = new ArrayList<String[]>();
			while (keys.hasNext()) {
				String key = keys.next();
				String value = jsonObject.getString(key);
				headerList.add(new String[] { key, value, "ADD", "DELETE" });
			}
			String[][] headerArray = new String[headerList.size()][];
			for (int i = 0; i < headerArray.length; i++) {
				headerArray[i] = headerList.get(i);
			}

			model = new DefaultTableModel(headerArray, columnNames);
		}

		headerTable = new JTable(model);
		// TableCellListener tableCellListener = new
		// TableCellListener(headerTable, new HeaderCellAction());

		ButtonColumn addButtonColumn = new ButtonColumn(headerTable,
				new AddAction(), 2);
		ButtonColumn deleteButtonColumn = new ButtonColumn(headerTable,
				new DeleteAction(), 3);

		headerTable.getColumnModel().getColumn(0).setPreferredWidth(80);
		headerTable.getColumnModel().getColumn(1).setPreferredWidth(80);
		headerTable.getColumnModel().getColumn(2).setPreferredWidth(5);
		headerTable.getColumnModel().getColumn(3).setPreferredWidth(5);

		JScrollPane headerScrollPane = new JScrollPane(headerTable);
		headerScrollPane.setPreferredSize(new Dimension(450, 100));

		lineNum++;

		insertGrid(headerLabel, 0, lineNum, 1, 1, 0.05, 0.11, new Insets(2, 2, 2, 2));
		insertGrid(headerScrollPane, 1, lineNum, 1, 1, 0.95, 0.11, new Insets(2, 2, 2, 2));

	}

	private void createBodyInput() {
		JLabel bodyLabel = new JLabel("Body : ");
		this.bodyTextArea = new JTextArea(15, 30);
		bodyTextArea.setLineWrap(true);
		bodyScrollPane = new JScrollPane(bodyTextArea);

		DocumentListener dl = new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				option.setBody(bodyTextArea.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				option.setBody(bodyTextArea.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				option.setBody(bodyTextArea.getText());
			}
		};

		bodyTextArea.setText(option.getBody());

		bodyTextArea.getDocument().addDocumentListener(dl);

		String selectedMethod = option.getMethod();

		if (selectedMethod.equals("GET"))
			methodComboBox.setSelectedIndex(0);
		if (selectedMethod.equals("POST"))
			methodComboBox.setSelectedIndex(1);
		if (selectedMethod.equals("PUT"))
			methodComboBox.setSelectedIndex(2);
		if (selectedMethod.equals("DELETE"))
			methodComboBox.setSelectedIndex(3);

		bodyTextArea.setEditable("PUT".equals(selectedMethod)
				|| "POST".equals(selectedMethod));

		JPopupMenu bodyTextpMenu = new JPopupMenu();
		JMenuItem jsonPrettyItem = new JMenuItem("JsonPretty");
		bodyTextpMenu.add(jsonPrettyItem);

		jsonPrettyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String jsonPrettyBodyString = JsonUtil.getPrettyJson(bodyTextArea
						.getText());
				bodyTextArea.setText(jsonPrettyBodyString);
				//ScrollUtil.scroll(bodyScrollPane, ScrollUtil.HCENTER);
				bodyTextArea.select(0, 0);
			}
		});

		bodyTextArea.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					bodyTextpMenu.show(bodyTextArea, e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		add(bodyTextpMenu);
		

		lineNum++;

		insertGrid(bodyLabel, 0, lineNum, 1, 1, 0.05, 0.33, new Insets(2, 2, 2, 2));
		insertGrid(bodyScrollPane, 1, lineNum, 1, 1, 0.95, 0.33, new Insets(2, 2, 2, 2));

		bodyTextArea.select(0, 0);
	}

	private void createResultDisplay() {
		JLabel resultLabel = new JLabel("Result : ");
		this.resultTextArea = new JTextArea(15, 50);
		resultTextArea.setEditable(false);
		resultTextArea.setLineWrap(true);

		JPopupMenu pMenu = new JPopupMenu();
		JMenuItem clearItem = new JMenuItem("Clear");
		JCheckBoxMenuItem jsonPrettyItem = new JCheckBoxMenuItem("JsonPretty");
		pMenu.add(clearItem);
		pMenu.add(jsonPrettyItem);

		clearItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resultTextArea.setText(null);
				originalText = "";
			}
		});

		jsonPrettyItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				printPrettyJson(e.getStateChange() == ItemEvent.SELECTED);
				if (resultTextArea != null) resultTextArea.select(0, 0);
			}
		});

		jsonPrettyItem.setSelected(option.isJSONPretty());

		resultTextArea.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					pMenu.show(resultTextArea, e.getX(), e.getY());
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		add(pMenu);

		resultScrollPane = new JScrollPane(resultTextArea);

		lineNum++;

		insertGrid(resultLabel, 0, lineNum, 1, 1, 0.05, 0.33, new Insets(2, 2, 2, 2));
		insertGrid(resultScrollPane, 1, lineNum, 1, 1, 0.95, 0.33, new Insets(2, 2, 2, 2));

	}

	private void createButtons() {
		JButton runButton = new JButton("Run");

		runButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// make data
				JSONObject runData = new JSONObject();
				JSONObject headerObj = JsonUtil.getHeaderJsonObject(headerTable);
				runData.put("URL", option.getUrl());
				runData.put("METHOD", option.getMethod());
				runData.put("HEADER", headerObj);

				if (JsonUtil.isJsonContent(headerObj)) {
					String validatedJString = JsonUtil.valiateJson(option.getBody());
					runData.put("BODY", validatedJString);
				} else {
					runData.put("BODY", option.getBody());
				}

				String asisString = runData.toString();
				String jsonData = StringEscapeUtils.escapeJson(asisString);


				// TLS Version Check
				ArrayList<String> TLSVersionStrings = new ArrayList<>();

				if (HttpOption.isTLSv1())
					TLSVersionStrings.add(HttpOption.TLSv1);
				if (HttpOption.isTLSv1_1())
					TLSVersionStrings.add(HttpOption.TLSv1_1);
				if (HttpOption.isTLSv1_2())
					TLSVersionStrings.add(HttpOption.TLSv1_2);
				if (HttpOption.isTLSv1_3())
					TLSVersionStrings.add(HttpOption.TLSv1_3);

				StringJoiner sj = new StringJoiner(",");

				TLSVersionStrings.stream().forEach((s) -> sj.add(s));

				String TLSVersionString = "-Dhttps.protocols=" + sj.toString();

				saveState(sj.toString(), headerObj.toString());
				
		        TimerTask task = new TimerTask() {
		        	private int seconds = 1;
		            public void run() {
		            	System.out.println("seconds " + seconds);
		            	HttpOption.setState("Sending.. " + "(" + seconds + "s)");
		            	seconds++;
		            }
		        };
		        Timer timer = new Timer("Timer");
		        long delay = 1000;
		        long period = 1000;
		        timer.scheduleAtFixedRate(task, delay, period);

				// run process
				processRunThread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						
						String javaCommand = null;
						String jarPath = null;
						
						// JDK Version Setting
						int JDKVersion = option.getJdkVersion();

						if (JDKVersion == 6) {
							javaCommand = properties.getProperty(HttpOption.JDK6_PATH);
							jarPath = properties.getProperty(HttpOption.JDK6_JAR_PATH);
						} else if (JDKVersion == 7) {
							javaCommand = properties.getProperty(HttpOption.JDK7_PATH);
							jarPath = properties.getProperty(HttpOption.JDK7_JAR_PATH);
						} else if (JDKVersion == 8) {
							javaCommand = properties.getProperty(HttpOption.JDK8_PATH);
							jarPath = properties.getProperty(HttpOption.JDK8_JAR_PATH);
						}
						
						stopButton.setVisible(true);
						HttpOption.setState("Sending..");
						
						client.run(new String[] { javaCommand, "-DFile.Encoding=UTF-8",
								TLSVersionString, "-jar", jarPath, jsonData,
								"useBase64" }, resultTextArea);
						
						timer.cancel();
						stopButton.setVisible(false);
						String stateString = HttpOption.getState();
						stateString = stateString.replace("Sending..", "Done.");
						HttpOption.setState(stateString);

					}
				});
				
				processRunThread.start();
			}
		});

		lineNum++;

		insertGrid(runButton, 0, lineNum, 2, 1, 1, 0.037, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2));
	}
	
	private void createStateBar() {
		// First Line: JDK Version Selection
		JLabel stateTilelabel = new JLabel("State : ");
		JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		stateTextlabel = new JLabel("");


		stopButton = new JButton("stop");
		stopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				processRunThread.interrupt();
				stopButton.setVisible(false);
			}
		});
		
		jPanel.add(stateTextlabel);
		jPanel.add(stopButton);
		
		stopButton.setVisible(false);
		
		Thread stateUpdater = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					stateTextlabel.setText(HttpOption.getState());
				}
			}
		});
		
		stateUpdater.start();

		
		lineNum++;

		insertGrid(stateTilelabel, 0, lineNum, 1, 1, 0.05, 0.037, new Insets(2, 2, 2, 2));
		insertGrid(jPanel, 1, lineNum, 1, 1, 0.95, 0.037, new Insets(2, 2, 2, 2));
	}




	private void saveState(String tlsVersions, String headerStrings) {
		properties.setProperty(HttpOption.JDK_VERSION,
				Integer.toString(option.getJdkVersion()));
		properties.setProperty(HttpOption.TLS_VERSION, tlsVersions);
		properties.setProperty(HttpOption.URL, option.getUrl());
		properties.setProperty(HttpOption.METHOD, option.getMethod());
		properties.setProperty(HttpOption.HEADER, headerStrings);
		properties.setProperty(HttpOption.BODY, option.getBody());

		try {
			option.save();
		} catch (FileNotFoundException e) {
			System.out.println("option save fail");
			e.printStackTrace();
		}
	}

	private void printPrettyJson(boolean isJsonPretty) {
		HttpOption.setJSONPretty(isJsonPretty);
		properties.setProperty(HttpOption.JSON_PRETTY,
				String.valueOf(isJsonPretty));
		try {
			option.save();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		if (isJsonPretty) {
			String resultString = resultTextArea.getText();
			if (resultString != null && !resultString.equals("")) {
				originalText = resultTextArea.getText();
				String resultBodyString = "* Response Body :\n";
				int resultAreaIndex = resultString.indexOf(resultBodyString);
				String frontResultString = resultString.substring(0,
						resultAreaIndex);
				String backResultString = resultString
						.substring(resultAreaIndex + resultBodyString.length());
				String prettyJson = JsonUtil.getPrettyJson(backResultString);

				resultTextArea.setText(null);
				resultTextArea.append(frontResultString);
				resultTextArea.append(resultBodyString);
				resultTextArea.append(prettyJson);
			}
		} else {
			if (!originalText.equals("")){
				resultTextArea.setText(originalText);
			}
		}

	}

}
