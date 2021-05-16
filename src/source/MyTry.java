package source;

import java.util.ArrayList;

public class MyTry{ 
	private int size;
	private int beginPoint, endPoint;
	private int a[][];
	private ArrayList<int[]> result = new ArrayList<int[]>();
	private int[] trace;
	private boolean[] visited;
	private ArrayList<MyPoint> arrMyPoint = new ArrayList<MyPoint>();
	private ArrayList<MyLine> arrMyLine = new ArrayList<MyLine>();
	private boolean mapType = false;
	
	private int count;
	
	public MyTry() {
	}
	
	public void input() {
		count = 0;
		result.clear();
		size = arrMyPoint.size();
		a = new int[size][size];
		trace = new int[size];
    	visited = new boolean[size];
    	size--;
    	for(int i = 1; i <= size; i++) {
    		visited[i] = false;
    	}
    	
    	trace[0] = beginPoint;
    	visited[beginPoint] = true;

		for (int i = 1; i < arrMyLine.size(); i++) {
			a[arrMyLine.get(i).getIndexPointA()][arrMyLine.get(i).getIndexPointB()] = arrMyLine.get(i).getCost();
			if (!mapType) {
				a[arrMyLine.get(i).getIndexPointB()][arrMyLine.get(i).getIndexPointA()] = arrMyLine.get(i).getCost();
			}
		}
	}
	
	public void Sol(int line) {
		count++;
		int arr[] = new int[line];
		for (int i = 0; i < line; i++) {
    		arr[i] = trace[i];
    	}
		result.add(arr);
	}
    
    public void BT(int line){
        if (trace[line - 1] == endPoint) {
        	Sol(line);
        } else {
        	for(int i = 1; i < size; i++) 
        		if (a[trace[line - 1]][i] != 0){
	              if (!visited[i]){ 
	                  visited[i] = true;
	                  trace[line] = i;
	                  BT(line + 1);
	                  trace[line] = 0;
	                  visited[i] = false;
	              }
        	}
        }
    }
    
    public String countPath() {
    	if (count > 1) return "There are " + Integer.toString(count) + " paths";
    	else return "There is " + Integer.toString(count) + " path";
    }

	public int getBeginPoint() {
		return beginPoint;
	}

	public void setBeginPoint(int beginPoint) {
		this.beginPoint = beginPoint;
	}

	public int getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(int endPoint) {
		this.endPoint = endPoint;
	}

	public int[][] getA() {
		return a;
	}

	public void setA(int[][] a) {
		this.a = a;
	}

	public int[] getP(int index) {
		return result.get(index);
	}

	public ArrayList<MyPoint> getArrMyPoint() {
		return arrMyPoint;
	}

	public void setArrMyPoint(ArrayList<MyPoint> arrMyPoint) {
		this.arrMyPoint = arrMyPoint;
	}

	public ArrayList<MyLine> getArrMyLine() {
		return arrMyLine;
	}

	public void setArrMyLine(ArrayList<MyLine> arrMyLine) {
		this.arrMyLine = arrMyLine;
	}

	public boolean isMapType() {
		return mapType;
	}

	public void setMapType(boolean mapType) {
		this.mapType = mapType;
	}

	public int getSize() {
		return size - 1;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
