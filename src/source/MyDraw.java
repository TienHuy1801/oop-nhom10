package source;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

class MyDraw extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private MyData data = new MyData();
	private ArrayList<Integer> arrPointResultStep = new ArrayList<Integer>();
	private int len[];
	private int a[][];
	private int p[];
	private ArrayList<Integer> trace;
	private int dad[];
	private int infinity;
	private int x = 0, y = 0, r = 15, r2 = 2 * r; // ban kinh, duong kinh
	private int indexPointBeginLine, indexPointEndLine, indexTemp;
	private Point pointBeginLine;
	private Point point;
	boolean checkDrawLine = false, isFindPoint = true;
	private int draw = 0; // draw point or line or move
	private Color colorBackGround = Color.lightGray, colorCost = Color.white, colorIndex = Color.black,
			colorDraw = Color.white, colorStep = Color.getHSBColor(50, 50, 50), colorStepMin = Color.pink,
			colorResult = Color.blue, colorBegin = Color.green, colorEnd = Color.red;
	private int sizeLine = 1, sizeLineResult = 2;
	private boolean drawResult = false;
	private boolean drawStep = false;
	private boolean drawTry = false;
	private boolean reDraw = false;
	private boolean resetGraph = false;
	private boolean typeMap = false;
	private boolean checkedPointMin[];
	private int indexBeginPoint, indexEndPoint;
	private int drawWith, drawHeight;
	private int count;
	
	//Setup Input
	private Point centerPoint;
	private int R;

	//init
	public MyDraw() {
		init();
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	public void init() {
		data.getArrMyLine().clear();
		data.getArrMyPoint().clear();
		MyPoint p0 = new MyPoint(new Ellipse2D.Float(50, 50, 50, 50));
		data.getArrMyPoint().add(p0);
		data.getArrMyLine().add(new MyLine(creatLine(p0.getP(), p0.getP()), 0, 0, -1));
	}

	//main draw
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(colorBackGround);
		Graphics2D g2d = (Graphics2D) g;
		// draw line
		reDraw(g2d, false);

		// draw result dijjkstra
		if (drawResult) {
			if (indexEndPoint == -1) {
				drawResultAllPoint(g2d);
			} else {
				drawResult(g2d);
			}
			// drawResult = false;
		}

		// draw Step dijjkstra
		if (drawStep) {
			drawResultStep(g2d);
			// drawStep = false;
		}
		
		//draw result Try
		if (drawTry) {
			drawTryPath(g2d);
		}

		// redraw the begin graph
		if (reDraw) {
			reDraw(g2d, true);
			reDraw = false;
		}

		// reset graph to graph space
		if (resetGraph) {
			resetGraph(g2d);
			init();
			resetGraph = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) { // click
		x = e.getX();
		y = e.getY();
		if (draw == 1) { // draw point
			Ellipse2D.Float el = new Ellipse2D.Float(x - r, y - r, r2, r2);
			MyPoint mp = new MyPoint(el);
			data.getArrMyPoint().add(mp);
			repaint();
		}
		// mouse right
		if (e.getButton() == MouseEvent.BUTTON3) {
			System.out.println("Right Clicked");
			isRightClick = true;
			pointRight = e.getPoint();
		}
	}

	@Override
	//on click
	public void mousePressed(MouseEvent e) { 
		pointBeginLine = e.getPoint();
		point = e.getPoint();
		e.getPoint();
		e.getPoint();
		data.getArrMyPoint().get(indexTemp).getEl().x = e.getX() - r;
		data.getArrMyPoint().get(indexTemp).getEl().y = e.getY() - r;
	}

	@Override
	//out click
	public void mouseReleased(MouseEvent e) { 
		boolean drawAgaine = false;
		if (checkDrawLine) {
			indexPointEndLine = indexPointContain(new Point(e.getX(), e.getY()));
			if (indexPointEndLine > 0) {
				isFindPoint = false;
			}

			for (int i = 1; i < data.getArrMyLine().size(); i++) {
				MyLine line = data.getArrMyLine().get(i);
				if (typeMap) { // directed
					// draw again <=> change cost
					if (line.getIndexPointA() == indexPointBeginLine && line.getIndexPointB() == indexPointEndLine) {
						drawAgaine = true;
						break;
					} // draw line reverse <=> not change cost
					else if (line.getIndexPointA() == indexPointEndLine
							&& line.getIndexPointB() == indexPointBeginLine) {
						addLineToList(indexPointBeginLine, indexPointEndLine, line.getCost());
						drawAgaine = true;
						break;
					}
				} else { // undirected
					// draw again <=> change cost
					if ((line.getIndexPointA() == indexPointBeginLine && line.getIndexPointB() == indexPointEndLine)
							|| (line.getIndexPointA() == indexPointEndLine
									&& line.getIndexPointB() == indexPointBeginLine)) {
						drawAgaine = true;
						break;
					}
				}
			}
			if (!drawAgaine) {
				int cost = showDialogCost(indexPointBeginLine, indexPointEndLine);
				addLineToList(indexPointBeginLine, indexPointEndLine, cost);
			}
			checkDrawLine = false;
		}
		data.getArrMyLine().get(indexTemp).setIndexPointA(data.getArrMyLine().get(indexTemp).getIndexPointB());
		updateLine();
		repaint();// clear wrong line
		isFindPoint = true; // accept find first point
	}

	@Override
	public void mouseEntered(MouseEvent e) { // in frame
		// System.out.println("Entered");
	}

	@Override
	public void mouseExited(MouseEvent e) { // out frame
		// System.out.println("Exited");
	}

	@Override
	public void mouseDragged(MouseEvent e) { // move mouse
		if (isFindPoint) { // find point is true
			indexPointBeginLine = indexPointContain(pointBeginLine);
			if (indexPointBeginLine > 0) {
				isFindPoint = false;
			}
		}
		// drawing line or point
		if (draw == 2 || draw == 1 || indexPointBeginLine >= 0) {
			int dx = e.getX() - point.x;
			int dy = e.getY() - point.y;
			// move point
			if ((draw == 1 || draw == 3) && indexPointBeginLine > 0) {
				Ellipse2D.Float el = data.getArrMyPoint().get(indexPointBeginLine).getEl();

				el.x += dx;
				el.y += dy;
				data.getArrMyPoint().get(indexPointBeginLine).setEl(el);
			}
			// draw line
			if (draw == 2 && indexPointBeginLine >= 0) {
				checkDrawLine = true;
				data.getArrMyLine().get(indexTemp).setIndexPointA(indexPointBeginLine);
				Ellipse2D.Float el = data.getArrMyPoint().get(indexTemp).getEl();
				el.x += dx;
				el.y += dy;
				data.getArrMyPoint().get(indexTemp).setEl(el);
			}
			updateLine();
			repaint();
			point.x += dx;
			point.y += dy;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	// find index Point in list Point
	protected int indexPointContain(Point point) {
		for (int i = 1; i < data.getArrMyPoint().size(); i++) {
			if (data.getArrMyPoint().get(i).getEl().getBounds2D().contains(point)) {
				return i;
			}
		}
		return -1;
	}

	// find index Line in list Line
	protected int indexLineContain(Point point) {
		for (int i = 1; i < data.getArrMyLine().size(); i++) {
			if (data.getArrMyLine().get(i).containerPoint(point)) {
				return i;
			}
		}
		return -1;
	}

	protected int getIndexLine(int pA, int pB) {
		for (int i = 0; i <= data.getArrMyLine().size(); i++) {

			if (data.getArrMyLine().get(i).getIndexPointA() == pA && data.getArrMyLine().get(i).getIndexPointB() == pB
					|| data.getArrMyLine().get(i).getIndexPointB() == pA
							&& data.getArrMyLine().get(i).getIndexPointA() == pB) {
				return i;

			}

		}
		return -1;
	}

	// show dialog input cost
	protected int showDialogCost(int indexPointBeginLine, int indexPointEndLine) {
		int cost = 0;
		if (indexPointEndLine > 0 && indexPointEndLine < data.getArrMyPoint().size()
				&& indexPointEndLine != indexPointBeginLine) {
			String c = null;
			boolean ok = false;
			while (!ok) {
				try {
					c = JOptionPane.showInputDialog(null,
							"Input Cost from " + indexPointBeginLine + " to " + indexPointEndLine, "Change cost",
							1);
					cost = Integer.parseInt(c);
					if (cost > 0) {
						return cost;
					}
				} catch (NumberFormatException ex) { // input error number
				}
				// cancel
				if (c == null)
					break;
			}
		}
		return cost;
	}

	// Add line to list line
	protected void addLineToList(int indexPointBeginLine, int indexPointEndLine, int cost) {
		if (cost > 0) {
			MyLine ml = new MyLine(
					creatLine(data.getArrMyPoint().get(indexPointBeginLine).getP(),
							data.getArrMyPoint().get(indexPointEndLine).getP()),
					indexPointBeginLine, indexPointEndLine, cost);
			data.getArrMyLine().add(ml);
			repaint();
		}
	}

	// change cost
	protected void changeCost(int indexLine) {
		int cost = showDialogCost(data.getArrMyLine().get(indexLine).getIndexPointA(),
				data.getArrMyLine().get(indexLine).getIndexPointB());
		if (cost > 0) {
			data.getArrMyLine().get(indexLine).setCost(cost);
			for (int i = 1; i < data.getArrMyLine().size(); i++) {
				if (data.getArrMyLine().get(i).getIndexPointA() == data.getArrMyLine().get(indexLine).getIndexPointB()
						&& data.getArrMyLine().get(i).getIndexPointB() == data.getArrMyLine().get(indexLine)
								.getIndexPointA()) {
					data.getArrMyLine().get(i).setCost(cost);
					break;
				}
			}
			repaint();
		}
	}

	// delete line
	protected void deleteLine(int indexLine) {
		data.getArrMyLine().remove(indexLine);
	}

	// delete point
	protected void deletePoint(int indexPoint) {
		for (int i = 1; i < data.getArrMyLine().size(); i++) {
			int a = data.getArrMyLine().get(i).getIndexPointA();
			int b = data.getArrMyLine().get(i).getIndexPointB();
			// delete line of indexPoint
			if (a == indexPoint || b == indexPoint) {
				data.getArrMyLine().remove(i);
				i--;
			} else { // down line have indexPointA or indexPointB > indexPoint
				if (a > indexPoint) {
					data.getArrMyLine().get(i).setIndexPointA(a - 1);
				}
				if (b > indexPoint) {
					data.getArrMyLine().get(i).setIndexPointB(b - 1);
				}
			}
		}
		// delete point have indexPoint
		data.getArrMyPoint().remove(indexPoint);
	}

	//create line
	private Line2D.Double creatLine(Point p1, Point p2) {
		Line2D.Double l = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
		return l;
	}
	
	// update location line after move point
	private void updateLine() { 
		for (int i = 0; i < data.getArrMyLine().size(); i++) {
			data.getArrMyLine().get(i)
					.setL(creatLine(data.getArrMyPoint().get(data.getArrMyLine().get(i).getIndexPointA()).getP(),
							data.getArrMyPoint().get(data.getArrMyLine().get(i).getIndexPointB()).getP()));
		}
	}

	public void resetGraph(Graphics2D g2d) {
		g2d.setColor(colorBackGround);
		g2d.fillRect(0, 0, 600, 600);
	}

	private void reDraw(Graphics2D g2d, boolean checkReDraw) {
		resetGraph(g2d);
		for (int i = 0; i < data.getArrMyLine().size(); i++) {
			data.getArrMyLine().get(i).drawLine(g2d,
					data.getArrMyPoint().get(data.getArrMyLine().get(i).getIndexPointA()).getP(),
					data.getArrMyPoint().get(data.getArrMyLine().get(i).getIndexPointB()).getP(), colorCost, colorDraw,
					sizeLine, typeMap);
		}

		// draw point
		for (int i = 1; i < data.getArrMyPoint().size(); i++) {
			data.getArrMyPoint().get(i).draw(g2d, i, colorDraw, colorIndex);
		}
	}

	private void drawResult(Graphics2D g2d) {
		if (checkedPointMin[indexEndPoint]) {
			String cost;
			int i = indexEndPoint;
			while (i != indexBeginPoint) {
				cost = String.valueOf(len[i]);
				MyLine ml = new MyLine(
						creatLine(data.getArrMyPoint().get(p[i]).getP(), data.getArrMyPoint().get(i).getP()), i, p[i],
						a[p[i]][i]);

				ml.drawLine(g2d, data.getArrMyPoint().get(p[i]).getP(), data.getArrMyPoint().get(i).getP(), colorCost,
						colorResult, sizeLineResult, typeMap);

				data.getArrMyPoint().get(i).drawResult(g2d, i, colorResult, colorIndex, cost, colorResult);

				i = p[i];
			}

			cost = "";
			data.getArrMyPoint().get(indexBeginPoint).drawResult(g2d, indexBeginPoint, colorBegin, colorIndex, cost,
					colorBegin);
			cost = String.valueOf(len[indexEndPoint]);
			data.getArrMyPoint().get(indexEndPoint).drawResult(g2d, indexEndPoint, colorEnd, colorIndex, cost,
					colorEnd);
		}
	}

	private void drawResultAllPoint(Graphics2D g2d) {
		int size = data.getArrMyPoint().size() - 1;
		String cost;
		for (int i = 1; i <= size; i++) {
			if (i != indexBeginPoint && a[p[i]][i] < infinity && p[i] > 0) {
				cost = len[i] + "";
				MyLine ml = new MyLine(
						creatLine(data.getArrMyPoint().get(p[i]).getP(), data.getArrMyPoint().get(i).getP()), i, p[i],
						a[p[i]][i]);

				ml.drawLine(g2d, data.getArrMyPoint().get(p[i]).getP(), data.getArrMyPoint().get(i).getP(), colorCost,
						colorResult, sizeLineResult, typeMap);

				data.getArrMyPoint().get(i).drawResult(g2d, i, colorResult, colorIndex, cost, colorResult);
			}

		}

		cost = "";
		data.getArrMyPoint().get(indexBeginPoint).drawResult(g2d, indexBeginPoint, colorBegin, colorIndex, cost,
				colorBegin);
	}
	
	private void drawTryPath(Graphics2D g2d) {
		if (count != 0 && indexBeginPoint != indexEndPoint) {
			int cost = 0;
			int size = p.length;
			for (int i = 0; i < size - 1; i++) {
				if (i != 0) cost += a[p[i - 1]][p[i]];
				MyLine ml = new MyLine(creatLine(data.getArrMyPoint().get(p[i]).getP(), data.getArrMyPoint().get(p[i + 1]).getP()), p[i], p[i + 1], a[p[i]][p[i+1]]);
				ml.drawLine(g2d, data.getArrMyPoint().get(p[i]).getP(), data.getArrMyPoint().get(p[i + 1]).getP(), colorCost, colorResult, sizeLineResult, typeMap);
	
				if(i != 0) data.getArrMyPoint().get(p[i]).drawResult(g2d, p[i], colorResult, colorIndex, Integer.toString(cost), colorResult);
			}
			data.getArrMyPoint().get(p[0]).drawResult(g2d, p[0], colorBegin, colorIndex, "", colorBegin);
			cost += a[p[size - 2]][p[size - 1]];
			data.getArrMyPoint().get(p[size - 1]).drawResult(g2d, p[size - 1], colorEnd, colorIndex, Integer.toString(cost), colorEnd);
		}
	}

	private void drawResultStep(Graphics2D g2d) {
		int size = p.length;
		for (int i = 0; i < size; i++) {
			int u = p[i];
			while (dad[u] != 0) {
				MyLine ml = new MyLine(creatLine(data.getArrMyPoint().get(u).getP(), data.getArrMyPoint().get(dad[u]).getP()), u, dad[u], a[u][dad[u]]);
				ml.drawLine(g2d, data.getArrMyPoint().get(u).getP(), data.getArrMyPoint().get(dad[u]).getP(), colorCost, colorResult, sizeLineResult, typeMap);
				data.getArrMyPoint().get(dad[u]).drawResult(g2d, dad[u], colorResult, colorIndex, "", colorResult);
				u = dad[u];
			}
		}
		for (int i : trace) {
			int u = i;
			while (dad[u] != 0) {
				MyLine ml = new MyLine(creatLine(data.getArrMyPoint().get(u).getP(), data.getArrMyPoint().get(dad[u]).getP()), u, dad[u], a[u][dad[u]]);
				ml.drawLine(g2d, data.getArrMyPoint().get(u).getP(), data.getArrMyPoint().get(dad[u]).getP(), colorCost, colorResult, sizeLineResult, typeMap);
				data.getArrMyPoint().get(dad[u]).drawResult(g2d, dad[u], colorResult, colorIndex, "", colorResult);
				u = dad[u];
			}
			data.getArrMyPoint().get(i).drawResult(g2d, i, colorResult, colorIndex, "", colorResult);
		}
	}

	//create circle graph
	public void createGraph(int numberPoint) {
		centerPoint = new Point(getWidth() / 2, getHeight() / 2);
		init();
		R = (centerPoint.x > centerPoint.y) ? centerPoint.y : centerPoint.x;
		R = R * 4 / 5;
		for (int i = 1; i <= numberPoint; i++) {
			double phi = -90 + 360.0 * i / numberPoint;
			phi = phi * Math.PI / 180;
			int x = centerPoint.x + (int) (R * Math.cos(phi));
			int y = centerPoint.y + (int) (R * Math.sin(phi));

			data.getArrMyPoint().add(new MyPoint(new Ellipse2D.Float(x, y, r2, r2)));
		}
	}

	//open demo (random)
	public void readDemo(int demo) {
		drawResult = false;
		drawStep = false;
		drawTry = false;
		File file = new File("");
        String currentDirectory = file.getAbsolutePath() + "\\src\\demo\\input" + demo + ".txt";
		FileInputStream fi;
		if (demo > 0)
		try {
			fi = new FileInputStream(currentDirectory);
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(fi);
        	String input;
	        while (scanner.hasNextLine()) {
	            input = scanner.nextLine();
	            String[] words = input.split("\\s");
	            int numberPoint = Integer.parseInt(words[0]);
	            int numberLine = Integer.parseInt(words[1]);
	            createGraph(numberPoint);
	            for (int i = 0; i < numberLine; i++){
	                int u, v, x;
	                input = scanner.nextLine();
	                words = input.split("\\s");
	                u = Integer.parseInt(words[0]);
	                v = Integer.parseInt(words[1]);
	                x = Integer.parseInt(words[2]);
	                data.getArrMyLine().add(new MyLine(
							creatLine(data.getArrMyPoint().get(u).getP(), data.getArrMyPoint().get(v).getP()),
							u, v, x));
	            }
	        }  
			repaint();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "File not found", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}	

	//open file
	public void readFile(String path) {
		drawResult = false;
		drawStep = false;
		drawTry = false;
		FileInputStream fi;
		try {
			fi = new FileInputStream(path);
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(fi);
        	String input;
	        while (scanner.hasNextLine()) {
	            input = scanner.nextLine();
	            String[] words = input.split("\\s");
	            int numberPoint = Integer.parseInt(words[0]);
	            int numberLine = Integer.parseInt(words[1]);
	            createGraph(numberPoint);
	            for (int i = 0; i < numberLine; i++){
	                int u, v, x;
	                input = scanner.nextLine();
	                words = input.split("\\s");
	                u = Integer.parseInt(words[0]);
	                v = Integer.parseInt(words[1]);
	                x = Integer.parseInt(words[2]);
	                data.getArrMyLine().add(new MyLine(
							creatLine(data.getArrMyPoint().get(u).getP(), data.getArrMyPoint().get(v).getP()),
							u, v, x));
	            }
	        }  
			repaint();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, "File not found", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public int getDrawWith() {
		return drawWith;
	}

	public void setDrawWith(int drawWith) {
		this.drawWith = drawWith;
	}

	public int getDrawHeight() {
		return drawHeight;
	}

	public void setDrawHeight(int drawHeight) {
		this.drawHeight = drawHeight;
	}

	protected boolean isRightClick = false;
	protected Point pointRight;

	public MyData getData() {
		return data;
	}

	public void setData(MyData data) {
		this.data = data;
	}

	public boolean isResetGraph() {
		return resetGraph;
	}

	public void setResetGraph(boolean resetGraph) {
		this.resetGraph = resetGraph;
	}

	public boolean isReDraw() {
		return reDraw;
	}

	public void setReDraw(boolean reDraw) {
		this.reDraw = reDraw;
	}

	public void setIndexBeginPoint(int indexBeginPoint) {
		this.indexBeginPoint = indexBeginPoint;
	}

	public int getIndexBeginPoint() {
		return indexBeginPoint;
	}

	public void setIndexEndPoint(int indexEndPoint) {
		this.indexEndPoint = indexEndPoint;
	}

	public int getIndexEndPoint() {
		return indexEndPoint;
	}

	public boolean[] getCheckedPointMin() {
		return checkedPointMin;
	}

	public void setCheckedPointMin(boolean[] checkedPointMin) {
		this.checkedPointMin = checkedPointMin;
	}

	public boolean isDrawStep() {
		return drawStep;
	}

	public void setDrawStep(boolean drawStep) {
		this.drawStep = drawStep;
	}

	public ArrayList<Integer> getArrPointResultStep() {
		return arrPointResultStep;
	}

	public void setArrPointResultStep(ArrayList<Integer> arrPointResultStep) {
		this.arrPointResultStep = arrPointResultStep;
	}

	public int[] getP() {
		return p;
	}

	public void setP(int[] p) {
		this.p = p;
	}

	public int[][] getA() {
		return a;
	}

	public void setA(int[][] a) {
		this.a = a;
	}

	public int getInfinity() {
		return infinity;
	}

	public void setInfinity(int infinity) {
		this.infinity = infinity;
	}

	public int[] getLen() {
		return len;
	}

	public void setLen(int[] len) {
		this.len = len;
	}

	public boolean isDrawResult() {
		return drawResult;
	}

	public void setDrawResult(boolean drawResult) {
		this.drawResult = drawResult;
	}

	public boolean isTypeMap() {
		return typeMap;
	}

	public void setTypeMap(boolean typeMap) {
		this.typeMap = typeMap;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public boolean isDrawTry() {
		return drawTry;
	}

	public void setDrawTry(boolean drawTry) {
		this.drawTry = drawTry;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int[] getDad() {
		return dad;
	}

	public void setDad(int[] dad) {
		this.dad = dad;
	}

	public ArrayList<Integer> getTrace() {
		return trace;
	}

	public void setTrace(ArrayList<Integer> trace) {
		this.trace = trace;
	}
}