package source;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Algorithm{ 
	private int size;
	private int beginPoint;
	private int a[][];
	private int dad[];
    private int maxsize;
	private int count[];
	private ArrayList<int[]> result = new ArrayList<int[]>();
	private int[] trace;
	private ArrayList<MyPoint> arrMyPoint = new ArrayList<MyPoint>();
	private ArrayList<MyLine> arrMyLine = new ArrayList<MyLine>();
	private boolean mapType = false;
	
	public Algorithm() {
	}
	
	public void input() {
		maxsize = 0;
		size = arrMyPoint.size();
		dad = new int[size];
		count = new int[size];
		a = new int[size][size];
		trace = new int[size];
    	size--;
    	for(int i = 1; i <= size; i++) {
    		trace[i] = 0;
    	}

		for (int i = 1; i < arrMyLine.size(); i++) {
			a[arrMyLine.get(i).getIndexPointA()][arrMyLine.get(i).getIndexPointB()] = arrMyLine.get(i).getCost();
			if (!mapType) {
				a[arrMyLine.get(i).getIndexPointB()][arrMyLine.get(i).getIndexPointA()] = arrMyLine.get(i).getCost();
			}
		}
	}
    
    public void DFS() {
    	
    }
    
    public void BFS() {
        Queue<Integer> queue = new LinkedList<>();
        queue.clear();
        queue.offer(beginPoint);
        trace[beginPoint] = 1;
        count[1] = 1;
        while(!queue.isEmpty()) {
        	int u = queue.poll();
        	for (int i = 1; i <= size; i++) {
        		if (a[u][i] != 0) {
        			if (trace[i] == 0) {
        				dad[i] = u;
        				trace[i] = trace[u] + 1;
        				count[trace[i]]++;
        				maxsize = Math.max(maxsize, trace[i]);
        				queue.offer(i);
        			}
        		}
        	}
        }
        result.clear();
        int i = 1;
        while (i <= maxsize) {
        	int arr[] = new int[count[i]];
        	int d = 0;
        	for (int j = 1; j <= size; j++) {
        		if (trace[j] == i) {
        			arr[d] = j;
        			d++;
        		}
        	}
        	result.add(arr);
        	i++;
        }
    }
    
    public String countStep() {
    	if (maxsize > 1) return "There are " + Integer.toString(maxsize) + " paths";
    	else return "There is " + Integer.toString(maxsize) + " path";
    }

	public int getBeginPoint() {
		return beginPoint;
	}

	public void setBeginPoint(int beginPoint) {
		this.beginPoint = beginPoint;
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

	public int getMaxsize() {
		return maxsize;
	}

	public void setMaxsize(int maxsize) {
		this.maxsize = maxsize;
	}

	public int[] getDad() {
		return dad;
	}

	public void setDad(int[] dad) {
		this.dad = dad;
	}		
}
