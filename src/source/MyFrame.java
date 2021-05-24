package source;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

@SuppressWarnings("deprecation")
public class MyFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	// frame
	private JFrame frameAbout, frameHelp;
//	private String[] listGraphDemo = { "0", "1", "2", "3", "4", "5" };
	private String data[][], head[];
	private JComboBox<String> cbbBeginPoint = new JComboBox<String>();
	private JComboBox<String> cbbEndPoint = new JComboBox<String>();
	private JComboBox<String> cbbChoosePath = new JComboBox<String>();
	private JComboBox<String> cbbGraphDemo = new JComboBox<String>();

	private JRadioButton radUndirected, radDirected;
	private JButton btnRunAll, btnRandom, btnRunTry, btnNext, btnPrev;

	private JTable tableMatrix;
	private JTable tableLog;

	// draw
	private JPanel drawPanel = new JPanel();
	private JButton btnPoint, btnLine, btnUpdate, btnMove, btnOpen, btnSave, btnNew, btnDfs, btnBfs;
	// graph
	private MyDraw myDraw = new MyDraw();

	// log
	private JTextArea textLog;
//	private JTextArea textMatrix;

	private JTextField textNumerPoint;

	private MyPopupMenu popupMenu;
	
	//algo
	private int maxsize;
	private int index;
	private ArrayList<Integer> updateTrace;

	private int indexBeginPoint = 0, indexEndPoint = 0;
	private int step = 0;
	private boolean mapType = false;

	int WIDTH_SELECT, HEIGHT_SELECT;

	MyDijkstra dijkstra = new MyDijkstra();
	MyTry Try = new MyTry();
	Algorithm algo = new Algorithm();

	public MyFrame(String title) {
		setTitle(title);
		setLayout(new BorderLayout(5, 5));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// addMenu
		add(creatMenu(), BorderLayout.PAGE_START);
		// add content
		add(creatSelectPanel(), BorderLayout.WEST);
		add(creatPaintPanel(), BorderLayout.CENTER);
		add(creatLogPanel(), BorderLayout.PAGE_END);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		//set icon
		URL iconURL = getClass().getResource("/icon/iconMain.png");
		ImageIcon icon = new ImageIcon(iconURL);
		setIconImage(icon.getImage());
		
	}

	private JMenuBar creatMenu() {

		JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		// menuFile.add(menuFileNew);
		menuFile.add(createMenuItem("New", KeyEvent.VK_N, Event.CTRL_MASK));
		menuFile.add(createMenuItem("Open", KeyEvent.VK_O, Event.CTRL_MASK));
		menuFile.add(createMenuItem("Save", KeyEvent.VK_S, Event.CTRL_MASK));
		menuFile.addSeparator();
		menuFile.add(createMenuItem("Exit", KeyEvent.VK_X, Event.CTRL_MASK));

		JMenu menuHelp = new JMenu("Help");
		menuHelp.setMnemonic(KeyEvent.VK_H);
		menuHelp.add(createMenuItem("Help", KeyEvent.VK_H, Event.CTRL_MASK));
		menuHelp.add(createMenuItem("About", KeyEvent.VK_A, Event.CTRL_MASK));

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menuFile);
		menuBar.add(menuHelp);
		return menuBar;
	}

	private JPanel creatSelectPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel panelTop = new JPanel(new GridLayout(6, 1, 5, 5));
		JPanel panelBottom = new JPanel(new BorderLayout());

		JPanel panelMapTypeTemp = new JPanel(new GridLayout(1, 2, 5, 5));
		panelMapTypeTemp.setBorder(new EmptyBorder(0, 10, 0, 5));
		panelMapTypeTemp.add(radUndirected = createRadioButton("Undirected", true));
		panelMapTypeTemp.add(radDirected = createRadioButton("Directed", false));
		ButtonGroup groupMapType = new ButtonGroup();
		groupMapType.add(radUndirected);
		groupMapType.add(radDirected);
		JPanel panelMapType = new JPanel(new BorderLayout());
		panelMapType.setBorder(new TitledBorder("Map Type"));
		panelMapType.add(panelMapTypeTemp);

		//
		btnRandom = new JButton("Random");
		JPanel panelInputMethod = new JPanel(new BorderLayout());
		panelInputMethod.setBorder(new TitledBorder("Input Random"));
		panelInputMethod.add(btnRandom);
		btnRandom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawDemo();

			}
		});

		JPanel panelSelectPointTemp = new JPanel(new GridLayout(1, 2, 15, 5));
		panelSelectPointTemp.setBorder(new EmptyBorder(0, 15, 0, 5));
		panelSelectPointTemp.add(cbbBeginPoint = createComboxBox("Begin"));
		panelSelectPointTemp.add(cbbEndPoint = createComboxBox("End"));
		JPanel panelSelectPoint = new JPanel(new BorderLayout());
		panelSelectPoint.setBorder(new TitledBorder("Point"));
		panelSelectPoint.add(panelSelectPointTemp);
		
		JPanel panelRunTry = new JPanel(new GridLayout(1, 1, 15, 5));
		panelRunTry.setBorder(new EmptyBorder(0, 15, 0, 5));
		panelRunTry.add(btnRunTry = createButton("Run"));
		JPanel panelRunT = new JPanel(new BorderLayout());
		panelRunT.setBorder(new TitledBorder("All path"));
		panelRunT.add(panelRunTry);
		
		JPanel panelNextPrev = new JPanel(new GridLayout(1,3,5,5));
		panelNextPrev.setBorder(new EmptyBorder(0, 0, 0, 0));
		panelNextPrev.add(btnPrev = createButton("Prev"));
		panelNextPrev.add(cbbChoosePath = createComboxBox("Path"));
		panelNextPrev.add(btnNext = createButton("Next"));
		JPanel panelNextPrevBox = new JPanel(new BorderLayout());
		panelNextPrevBox.setBorder(new TitledBorder("Select path"));
		panelNextPrevBox.add(panelNextPrev);
		
		JPanel panelAlgo = new JPanel(new GridLayout(1,2,5,5));
		panelAlgo.setBorder(new EmptyBorder(0, 0, 0, 0));
//		panelAlgo.add(btnDfs = createButton("DFS"));
		panelAlgo.add(btnBfs = createButton("BFS"));
		panelAlgo.add(btnRunAll = createButton("Dijkstra"));
		JPanel panelAlgorithm = new JPanel(new BorderLayout());
		panelAlgorithm.setBorder(new TitledBorder("Algorithm"));
		panelAlgorithm.add(panelAlgo);
//
//		JPanel panelRunTemp = new JPanel(new GridLayout(1, 2, 15, 5));
//		panelRunTemp.setBorder(new EmptyBorder(0, 15, 0, 5));
//		panelRunTemp.add(btnRunStep = createButton("Run Step"));
//		JPanel panelRun = new JPanel(new BorderLayout());
//		panelRun.setBorder(new TitledBorder("Dijkstra"));
//		panelRun.add(panelRunTemp);

		panelTop.add(panelMapType);
		panelTop.add(panelInputMethod);
		panelTop.add(panelSelectPoint);
		panelTop.add(panelRunT);
		panelTop.add(panelNextPrevBox);
		panelTop.add(panelAlgorithm);
//		panelTop.add(panelRun);

		JScrollPane scroll = new JScrollPane(tableMatrix = createTable());
		scroll.setPreferredSize(panelTop.getPreferredSize());
		panelBottom.add(scroll);
		tableMatrix.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {

				int row = tableMatrix.getSelectedRow();
				int colum = tableMatrix.getSelectedColumn();
				int indexLine = 1;
				indexLine = myDraw.getIndexLine((row + 1), (colum + 1));
				if (indexLine < 1) {

				} else
					myDraw.changeCost(indexLine);
				actionUpdate();
			}
		});
		panel.add(panelTop, BorderLayout.PAGE_START);
		panel.add(panelBottom, BorderLayout.CENTER);
		panel.setBorder(new EmptyBorder(0, 5, 0, 0));
		WIDTH_SELECT = (int) panel.getPreferredSize().getWidth();
		HEIGHT_SELECT = (int) panel.getPreferredSize().getHeight();
		return panel;
	}

	private JPanel creatPaintPanel() {
		drawPanel.setLayout(new BoxLayout(drawPanel, BoxLayout.Y_AXIS));
		drawPanel.setBorder(new TitledBorder(""));
		drawPanel.setBackground(null);
		Icon icon;
		// String link = File.separator + "icon" + File.separator;
		String link = "/icon/";

		icon = getIcon(link + "iconNew.png");
		drawPanel.add(btnNew = createButtonImage(icon, "New graph"));
		
		icon = getIcon(link + "iconOpen.png");
		drawPanel.add(btnOpen = createButtonImage(icon, "Open graph"));
		
		icon = getIcon(link + "iconSave.png");
		drawPanel.add(btnSave = createButtonImage(icon, "Save graph"));
		
		icon = getIcon(link + "iconMove.png");
		drawPanel.add(btnMove = createButtonImage(icon, "Move Point"));
		
		icon = getIcon(link + "iconOk.png");
		drawPanel.add(btnUpdate = createButtonImage(icon, "Update Graph"));

		icon = getIcon(link + "iconPoint.png");
		drawPanel.add(btnPoint = createButtonImage(icon, "Draw Point"));

		icon = getIcon(link + "iconLine.png");
		drawPanel.add(btnLine = createButtonImage(icon, "Draw line"));


		popupMenu = createPopupMenu();
		myDraw.setComponentPopupMenu(popupMenu);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(drawPanel, BorderLayout.WEST);
		panel.add(myDraw, BorderLayout.CENTER);
		return panel;
	}

	private ImageIcon getIcon(String link) {
		return new ImageIcon(getClass().getResource(link));
	}

	private JPanel creatLogPanel() {
		textLog = new JTextArea("Path: ");
		textLog.setRows(4);
		textLog.setEditable(false);
		JScrollPane scrollPath = new JScrollPane(textLog);
		@SuppressWarnings("unused")
		JScrollPane scroll = new JScrollPane(tableLog = createTable());

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder("Log"));
		panel.add(scrollPath, BorderLayout.PAGE_START);

		panel.setPreferredSize(new Dimension(WIDTH_SELECT * 7 / 2, HEIGHT_SELECT / 5));
		return panel;
	}

	private JMenuItem createMenuItem(String title, int keyEvent, int event) {
		JMenuItem mi = new JMenuItem(title);
		mi.setMnemonic(keyEvent);
		mi.setAccelerator(KeyStroke.getKeyStroke(keyEvent, event));
		mi.addActionListener(this);
		return mi;
	}

	private MyPopupMenu createPopupMenu() {
		MyPopupMenu popup = new MyPopupMenu();

		popup.add(createMenuItem("Change cost", 0, 0));
		popup.add(createMenuItem("Delete", 0, 0));

		return popup;
	}

	// create radioButton on group btnGroup and add to panel
	private JRadioButton createRadioButton(String lable, Boolean select) {
		JRadioButton rad = new JRadioButton(lable);
		rad.addActionListener(this);
		rad.setSelected(select);
		return rad;
	}

	// create button and add to panel
	private JButton createButton(String lable) {
		JButton btn = new JButton(lable);
		btn.addActionListener(this);
		return btn;
	}

	// create buttonImage and add to panel
	private JButton createButtonImage(Icon icon, String toolTip) {
		JButton btn = new JButton(icon);
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.addActionListener(this);
		btn.setToolTipText(toolTip);
		return btn;
	}

	// create comboBox and add to panel
	private JComboBox<String> createComboxBox(String title) {
		String list[] = { title };
		JComboBox<String> cbb = new JComboBox<String>(list);
		cbb.addActionListener(this);
		cbb.setEditable(false);
		cbb.setMaximumRowCount(5);
		return cbb;
	}

	// create matrix panel with cardLayout

	private JTable createTable() {
		JTable table = new JTable();
		return table;
	}

	// ------------------ Action ------------------//

	private void actionUpdate() {
		updateListPoint();
		updateListPath();
		resetDataDijkstra();
		setDrawResultOrStep(false);
		myDraw.setDrawTry(false);
		reDraw();
		loadMatrix();
		clearLog();
	}

	private void actionDrawPoint() {
		myDraw.setDraw(1);
		setDrawResultOrStep(false);
		myDraw.setDrawTry(false);
	}

	private void actionDrawLine() {
		myDraw.setDraw(2);
		setDrawResultOrStep(false);
		myDraw.setDrawTry(false);
	}

	private void actionOpen() {
		File file = new File("");
        String currentDirectory = file.getAbsolutePath() + "\\src\\demo";
		JFileChooser fc = new JFileChooser(new File(currentDirectory));
		fc.setDialogTitle("Open graph");
		int select = fc.showOpenDialog(this);
		if (select == 0) {
			String path = fc.getSelectedFile().toString();
			myDraw.readFile(path);
			textLog.setText("Done read.");
			actionUpdate();
		}
	}

	private void actionSave() {
		File file = new File("");
        String currentDirectory = file.getAbsolutePath() + "\\src\\save";
		JFileChooser fc = new JFileChooser(new File(currentDirectory));
		fc.setDialogTitle("Save graph");
		int select = fc.showSaveDialog(this);
		Toolkit tool = Toolkit.getDefaultToolkit();
        Dimension ScreenSize = tool.getScreenSize();
		if (select == 0) {
			String path = fc.getSelectedFile().getPath() + ".png";
			System.out.println(path);
			try {
	            Robot robot = new Robot();
	            Rectangle capture = new Rectangle((WIDTH_SELECT+65), 50, (ScreenSize.width-WIDTH_SELECT-65), (ScreenSize.height-230));
	            BufferedImage Image = robot.createScreenCapture(capture);
	            ImageIO.write(Image, "jpg", new File(path));
	        }
	        catch (Exception ex) {
	            System.out.println(ex);
	        }

		}
	}

	private void actionNew() {
		setDrawResultOrStep(false);
		myDraw.setDrawResult(false);
		myDraw.setDrawStep(false);
		myDraw.setDrawTry(false);
		myDraw.setResetGraph(true);
		myDraw.repaint();
		myDraw.init();
		updateListPoint();
		updateListPath();
		clearLog();
		clearMatrix();
	}

	private void actionChoosePoint() {
		myDraw.setDrawTry(false);
		resetDataDijkstra();
		setDrawResultOrStep(false);
		reDraw();
		clearLog();
	}
	
	
	private void actionChoosePath() {
		int indexPath = cbbChoosePath.getSelectedIndex();
		String log = "Path " + indexPath + ": ";
		int arr[] = Try.getP(indexPath - 1);
		for (int i = 0; i < arr.length - 1; i++) log += arr[i] + " -> ";
		log += arr[arr.length - 1];
		textLog.setText(log);
		myDraw.setP(arr);
		setDrawResultOrStep(false);
		myDraw.repaint();
	}

	private void showDialogChangeCost() {
		int index = myDraw.indexLineContain(popupMenu.getPoint());
		if (index > 0) {
			myDraw.changeCost(index);
			actionUpdate();
		} else {
			JOptionPane.showMessageDialog(null, "Haven't line seleced!");
		}
	}

	private void showDialogDelete() {
		int index = myDraw.indexPointContain(popupMenu.getPoint());
		if (index <= 0) {
			index = myDraw.indexLineContain(popupMenu.getPoint());
			if (index > 0) {
				// show message dialog
				MyLine ml = myDraw.getData().getArrMyLine().get(index);
				String message = "Do you want delete the line from " + ml.getIndexPointA() + " to "
						+ ml.getIndexPointB();
				int select = JOptionPane.showConfirmDialog(this, message, "Delete line", JOptionPane.OK_CANCEL_OPTION);
				if (select == 0) {
					myDraw.deleteLine(index);
					actionUpdate();
				}
			} else {
				JOptionPane.showMessageDialog(null, "Haven't point or line seleced!");
			}
		} else {
			// show message dialog
			String message = "Do you want delete the point " + index;
			int select = JOptionPane.showConfirmDialog(this, message, "Delete point", JOptionPane.OK_CANCEL_OPTION);
			if (select == 0) {
				myDraw.deletePoint(index);
				actionUpdate();
			}
		}
	}
	
	private void updateListPath() {
		int size = Try.getCount() + 1;
		String listPoint[] = new String[size];
		listPoint[0] = "Path";
		for (int i = 1; i < listPoint.length; i++) {
			listPoint[i] = String.valueOf(i);
		}
		cbbChoosePath.setModel(new DefaultComboBoxModel<String>(listPoint));
		cbbChoosePath.setMaximumRowCount(5);
	}

	private void updateListPoint() {
		int size = myDraw.getData().getArrMyPoint().size();
		String listPoint[] = new String[size];
		listPoint[0] = "Begin";
		for (int i = 1; i < listPoint.length; i++) {
			listPoint[i] = String.valueOf(i);
		}

		cbbBeginPoint.setModel(new DefaultComboBoxModel<String>(listPoint));
		cbbBeginPoint.setMaximumRowCount(5);

		if (size > 1) {
			listPoint = new String[size + 1];
			listPoint[0] = "End";
			for (int i = 1; i < listPoint.length; i++) {
				listPoint[i] = String.valueOf(i);
			}
			listPoint[listPoint.length - 1] = "All";
		} else {
			listPoint = new String[1];
			listPoint[0] = "End";
		}

		cbbEndPoint.setModel(new DefaultComboBoxModel<String>(listPoint));
		cbbEndPoint.setMaximumRowCount(5);
	}

	private void setEnableDraw(boolean check, String matrix) {
		// btnLine.setEnabled(check);
		// btnPoint.setEnabled(check);
		// btnUpdate.setEnabled(check);

		// CardLayout cl = (CardLayout) (matrixPandl.getLayout());
		// cl.show(matrixPandl, matrix);
		cbbGraphDemo.setEnabled(!check);
	}

	private void setEnableMapType(boolean mapType) {
		this.mapType = mapType;
		myDraw.setTypeMap(mapType);
		setDrawResultOrStep(false);
		myDraw.repaint();
		resetDataDijkstra();
		loadMatrix();
	}

	private void setDrawResultOrStep(boolean check) {
		myDraw.setDrawResult(check);
		myDraw.setDrawStep(check);
	}

	private void resetDataDijkstra() {
		step = 0;
		dijkstra = new MyDijkstra();
		dijkstra.setMapType(mapType);
		dijkstra.setArrMyPoint(myDraw.getData().getArrMyPoint());
		dijkstra.setArrMyLine(myDraw.getData().getArrMyLine());
		dijkstra.input();
		dijkstra.processInput();
	}

	private void reDraw() {
		myDraw.setReDraw(true);
		myDraw.repaint();
	}

	private void clearMatrix() {
		DefaultTableModel model = new DefaultTableModel();
		tableMatrix.setModel(model);
	}

	private void clearLog() {
		DefaultTableModel model = new DefaultTableModel();
		tableLog.setModel(model);
		clearPath();
	}

	private void clearPath() {
		textLog.setText("Path : ");
	}

	private void loadMatrix() {
		final int width = 35;
		final int col = WIDTH_SELECT / width - 1;
		int infinity = dijkstra.getInfinity();
		int a[][] = dijkstra.getA();
		head = new String[a.length - 1];
		data = new String[a[0].length - 1][a.length - 1];
		for (int i = 1; i < a[0].length; i++) {
			head[i - 1] = String.valueOf(i);
			for (int j = 1; j < a.length; j++) {
				if (a[i][j] == infinity) {
					data[i - 1][j - 1] = "-1";
				} else {
					data[i - 1][j - 1] = String.valueOf(a[i][j]);
				}
			}
		}
		DefaultTableModel model = new DefaultTableModel(data, head);
		tableMatrix.setModel(model);
		if (tableMatrix.getColumnCount() > col) {
			for (int i = 0; i < head.length; i++) {
				TableColumn tc = tableMatrix.getColumnModel().getColumn(i);
				tc.setPreferredWidth(width);
			}
			tableMatrix.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		} else {
			tableMatrix.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
	}

	private void loadLog(boolean isStep) {
		final int width = 70;
		final int col = tableLog.getWidth() / width - 1;
		int infinity = dijkstra.getInfinity();
		int logLen[][] = dijkstra.getLogLen();
		int logP[][] = dijkstra.getLogP();
		head = new String[logLen.length - 1];
		data = new String[dijkstra.getNumberPointChecked()][logLen.length - 1];
		boolean check[] = new boolean[logLen.length - 1];

		for (int i = 0; i < logLen.length - 1; i++) {
			head[i] = String.valueOf(i + 1);
			check[i] = false;
			data[0][i] = "[∞, ∞]";
		}

		data[0][indexBeginPoint - 1] = "[0, " + indexBeginPoint + "]";

		for (int i = 1; i < data.length; i++) {
			int min = infinity, indexMin = -1;
			// // check "*" for min len
			for (int j = 1; j < logLen.length; j++) {
				if (min > logLen[i][j] && !check[j - 1]) {
					min = logLen[i][j];
					indexMin = j - 1;
				}
			}
			if (indexMin > -1) {
				check[indexMin] = true;
			}

			for (int j = 1; j < logLen.length; j++) {

				if (min > logLen[i][j] && !check[j - 1]) {
					min = logLen[i][j];
					indexMin = j - 1;
				}

				String p = "∞";
				if (logP[i][j] > 0) {
					p = logP[i][j] + "";
				}
				if (check[j - 1]) {
					data[i][j - 1] = "-";
				} else if (logLen[i][j] == infinity) {
					data[i][j - 1] = "[∞, " + p + "]";
				} else {
					data[i][j - 1] = "[" + logLen[i][j] + ", " + p + "]";
				}
			}

			if (indexMin > -1) {
				data[i - 1][indexMin] = "*" + data[i - 1][indexMin];
			}
		}

		// check "*" for min len of row last
		int min = infinity, indexMin = -1;
		for (int j = 1; j < logLen.length; j++) {
			if (min > logLen[data.length - 1][j] && !check[j - 1]) {
				min = logLen[data.length - 1][j];
				indexMin = j - 1;
			}
		}
		if (indexMin > -1) {
			check[indexMin] = true;
			data[data.length - 1][indexMin] = "*" + data[data.length - 1][indexMin];
		}

		// update data for table log
		DefaultTableModel model = new DefaultTableModel(data, head);
		tableLog.setModel(model);
		if (tableLog.getColumnCount() > col) {
			for (int i = 0; i < head.length; i++) {
				TableColumn tc = tableLog.getColumnModel().getColumn(i);
				tc.setPreferredWidth(width);
			}
			// tableLog.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		} else {
			// tableLog.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
	}

	private void drawDemo() {
		Random rd = new Random();
		int demo = rd.nextInt(5);
		if (demo < 1 || demo > 4)
			demo = 1;
		myDraw.readDemo(demo);
		actionUpdate();
	}
	private boolean checkRun() {
		int size = myDraw.getData().getArrMyPoint().size() - 1;
		indexBeginPoint = cbbBeginPoint.getSelectedIndex();
		indexEndPoint = cbbEndPoint.getSelectedIndex();
		if (indexEndPoint == size + 1) { // all Point
			indexEndPoint = -1;
		}

		if (size < 1 || indexBeginPoint == 0 || indexEndPoint == 0) {
			JOptionPane.showMessageDialog(null, "Error chose points or don't Update graph to chose points", "Error",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}
	
	private boolean checkRunAlgo() {
		int size = myDraw.getData().getArrMyPoint().size() - 1;
		indexBeginPoint = cbbBeginPoint.getSelectedIndex();

		if (size < 1 || indexBeginPoint == 0) {
			JOptionPane.showMessageDialog(null, "Error chose points or don't Update graph to chose points", "Error",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	private void setBeginEndPoint() {
		myDraw.setIndexBeginPoint(indexBeginPoint);
		myDraw.setIndexEndPoint(indexEndPoint);
		dijkstra.setBeginPoint(indexBeginPoint);
		dijkstra.setEndPoint(indexEndPoint);
	}

	private void runAll() {
		if (checkRun()) {
			resetDataDijkstra();
			setBeginEndPoint();
			dijkstra.dijkstra();
			textLog.setText(dijkstra.tracePath());
			loadLog(false);

			myDraw.setDrawStep(false);
			myDraw.setDrawResult(true);
			myDraw.setDrawTry(false);
			myDraw.setA(dijkstra.getA());
			myDraw.setP(dijkstra.getP());
			myDraw.setInfinity(dijkstra.getInfinity());
			myDraw.setLen(dijkstra.getLen());
			myDraw.setCheckedPointMin(dijkstra.getCheckedPointMin());
			myDraw.repaint();
		}
	}
	
	private void runTry() {
		if (checkRun()) {
			Try.setMapType(mapType);
			Try.setArrMyPoint(myDraw.getData().getArrMyPoint());
			Try.setArrMyLine(myDraw.getData().getArrMyLine());
			myDraw.setIndexBeginPoint(indexBeginPoint);
			myDraw.setIndexEndPoint(indexEndPoint);
			Try.setBeginPoint(indexBeginPoint);
			Try.setEndPoint(indexEndPoint);
			Try.input();
			if (indexBeginPoint != indexEndPoint) Try.BT(1);
			JOptionPane.showMessageDialog(null, Try.countPath(), "Log", JOptionPane.INFORMATION_MESSAGE);
			
			myDraw.setDrawStep(false);
			myDraw.setDrawResult(false);
			myDraw.setDrawTry(true);
			myDraw.setA(Try.getA());
			updateListPath();
			if (Try.getCount() > 0) {
				myDraw.setP(Try.getP(0));
				cbbChoosePath.setSelectedIndex(1);
			}
			myDraw.setCount(Try.getCount());
			myDraw.repaint();
		}
	}
	
	private void runDfs() {
		if (checkRunAlgo()) {
			algo.setMapType(mapType);
			algo.setArrMyPoint(myDraw.getData().getArrMyPoint());
			algo.setArrMyLine(myDraw.getData().getArrMyLine());
			myDraw.setIndexBeginPoint(indexBeginPoint);
			myDraw.setIndexEndPoint(indexEndPoint);
			algo.setBeginPoint(indexBeginPoint);
			algo.input();
			algo.DFS();
			
//			int [] a = algo.getP();
//			for (int i = 0; i < a.length; i++) {
//				System.out.print(a[i] + " ");
//			}
		}
	}
	
	private void runBfs() {
		if (checkRunAlgo()) {
			resetDataDijkstra();
			setDrawResultOrStep(false);
			myDraw.setDrawTry(false);
			reDraw();
			loadMatrix();
			clearLog();
			algo.setMapType(mapType);
			algo.setArrMyPoint(myDraw.getData().getArrMyPoint());
			algo.setArrMyLine(myDraw.getData().getArrMyLine());
			myDraw.setIndexBeginPoint(indexBeginPoint);
			myDraw.setIndexEndPoint(indexEndPoint);
			algo.setBeginPoint(indexBeginPoint);
			algo.input();
			algo.BFS();
			updateTrace = new ArrayList<Integer>();
			JFrame mainFrame;
			JPanel controlPanel = new JPanel();
	        controlPanel.setLayout(new FlowLayout());
			mainFrame = new JFrame("BFS");
	        mainFrame.setSize(300, 100);
	        mainFrame.setLayout(new GridLayout(1, 1));
			maxsize = algo.getMaxsize();
			index = 0;
	        JButton btnNext = new JButton("Next");
	        btnNext.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                if (index < maxsize) {
	    				myDraw.setDrawStep(true);
	    				myDraw.setDrawResult(false);
	    				myDraw.setDrawTry(false);
	    				myDraw.setA(algo.getA());
	    				myDraw.setDad(algo.getDad());
	    				int arr[] = algo.getP(index);
	    				myDraw.setP(arr);
	    				for (int i = 0; i < arr.length; i++)
	    					updateTrace.add(arr[i]);
	    				myDraw.setTrace(updateTrace);
	    				myDraw.repaint();
	    				index++;
	                } else {
	                	myDraw.setDrawStep(false);
	    				myDraw.setDrawResult(false);
	    				myDraw.setDrawTry(false);
	                	mainFrame.setVisible(false);
	                	JOptionPane.showMessageDialog(null, "Done BFS", "Log", JOptionPane.INFORMATION_MESSAGE);
	                }
	            }
	        });
	        controlPanel.add(btnNext);
	        mainFrame.add(controlPanel);
	        mainFrame.setVisible(true);
		}
	}
	
	private void increasePath() {
		int currentIndex = cbbChoosePath.getSelectedIndex();
		if (currentIndex == Try.getCount()) return;
		int indexPath = currentIndex + 1;
		cbbChoosePath.setSelectedIndex(indexPath);
		String log = "Path " + indexPath + ": ";
		int arr[] = Try.getP(indexPath - 1);
		for (int i = 0; i < arr.length - 1; i++) log += arr[i] + " -> ";
		log += arr[arr.length - 1];
		textLog.setText(log);
		myDraw.setP(arr);
		setDrawResultOrStep(false);
		myDraw.repaint();
	}
	
	private void reductionPath() {
		int currentIndex = cbbChoosePath.getSelectedIndex();
		if (currentIndex == 0) return;
		int indexPath = currentIndex - 1;
		cbbChoosePath.setSelectedIndex(indexPath);
		String log = "Path " + indexPath + ": ";
		int arr[] = Try.getP(indexPath - 1);
		for (int i = 0; i < arr.length - 1; i++) log += arr[i] + " -> ";
		log += arr[arr.length - 1];
		textLog.setText(log);
		myDraw.setP(arr);
		setDrawResultOrStep(false);
		myDraw.repaint();
	}

	private void showHelp() {
		if (frameHelp == null) {
			frameHelp = new HelpAndAbout(0, "Help");
		}
		frameHelp.setVisible(true);
	}

	private void showAbout() {
		if (frameAbout == null) {
			frameAbout = new HelpAndAbout(1, "About");
		}
		frameAbout.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		// select button in paint
		if (e.getSource() == btnUpdate) {
			actionUpdate();
		}

		if (e.getSource() == btnPoint) {
			actionDrawPoint();
		}

		if (e.getSource() == btnLine) {
			actionDrawLine();
		}
		if (e.getSource() == btnMove) {
			myDraw.setDraw(3);
		}

		if (e.getSource() == btnNew) {
			actionNew();
		}

		// select input method
		if (command == "Draw") {
			setEnableDraw(true, "outputMatrix");
		} else if (command == "Matrix") {
			setEnableDraw(true, "inputMatrix");
		} else if (command == "Demo") {
			setEnableDraw(false, "outputMatrix");
			drawDemo();
		}

		// select Map type
		if (e.getSource() == radUndirected) {
			setEnableMapType(false);
		} else if (e.getSource() == radDirected) {
			setEnableMapType(true);
		}

		if (e.getSource() == cbbGraphDemo) {
			drawDemo();
		}

		// select point
		if (e.getSource() == cbbBeginPoint || e.getSource() == cbbEndPoint) {
			actionChoosePoint();
		}
		
		// select path
		if (e.getSource() == cbbChoosePath) {
			actionChoosePath();
		}
		
		if (e.getSource() == btnPrev) {
			reductionPath();
		}
		
		if (e.getSource() == btnNext) {
			increasePath();
		}
		
		// select run

		if (e.getSource() == btnRunAll) {
			runAll();
		}
		
		if (e.getSource() == btnDfs) {
			runDfs();
		}
		
		if (e.getSource() == btnBfs) {
			runBfs();
		}
		
		if (e.getSource() == btnRunTry) {
			runTry();
		}
		// select menu bar
		if (command == "New") {
			actionNew();
		}
		if (command == "Open" || e.getSource() == btnOpen) {
			actionOpen();
		}
		if (command == "Save" || e.getSource() == btnSave) {
			actionSave();
		}
		if (command == "Exit") {
			System.exit(0);
		}
		if (command == "About") {
			showAbout();
		}
		if (command == "Help") {
			showHelp();
		}

		// select popup menu
		if (command == "Change cost") {
			showDialogChangeCost();
		}
		if (command == "Delete") {
			showDialogDelete();
		}
	}

}