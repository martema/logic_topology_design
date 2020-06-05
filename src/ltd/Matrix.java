package ltd;


import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;




public class Matrix  {
	private int row;
	private int column;
	double[][] m;
	
	
	public Matrix(int row, int column){
		this.row = row;
		this.column = column;
		m = new double[row][column];
		for(int i=0; i<this.row; i++){
			for(int j=0; j<this.column; j++){
				m[i][j]=-1;
		    	}
		    }
		 
	}

	public Matrix (Matrix matrix){
		row = matrix.getRow();
		column = matrix.getColumn();
		m = new double[row][column];
		for(int i=0; i<row; i++){
			for(int j=0; j<column; j++){
				m[i][j] = matrix.getElement(i, j);
			}
		}
	}
	
	public Matrix() {
		// TODO Auto-generated constructor stub
	}


	public int getRow() {
		return row;
	}


	public void setRow(int row) {
		this.row = row;
	}


	public int getColumn() {
		return column;
	}


	public void setColumn(int column) {
		this.column = column;
	}


	public double[][] getM() {
		return m;
	}


	public void setM(double[][] m) {
		this.m = m;
	}


	public void setElement(int i, int j, double elem){
		m[i][j] = elem;
	}
	
	public double getElement(int i, int j){
		return m[i][j];
	}
	
	public double getMax(){
		double max = 0;
		   
		for(int i=0; i<this.row; i++){
			for(int j=0; j<this.column; j++){
				if(m[i][j]>max){
					max = m[i][j];
		    	}
		    }
		 }
		    	
		return max;
	}
	
	public int getRowMax(){
		double max = 0;
		int iMax = 0;
		
		for(int i=0; i<this.row; i++){
			for(int j=0; j<this.column; j++){
				if(m[i][j]>max){
					iMax = i;
					max = m[i][j];
				}
			}
		}
		
		return iMax;
	}
	
	public int getColumnMax(){
		double max = 0;
		int jMax = 0;
		
		for(int i=0; i<this.row; i++){
			for(int j=0; j<this.column; j++){
				if(m[i][j]>max){
					jMax = j;
					max = m[i][j];
				}
			}
		}
		
		return jMax;
	}
	
	public boolean isPresent(double elem){
		boolean flag = false;
		
		for(int i=0; i<this.row; i++){
			for(int j=0; j<this.column; j++){
				if(m[i][j]==elem)
					flag = true;
			}
		}
		
		return flag;
	}
	
	public int rowElem(double elem){
		int rowElem;
		
		for(int i=0; i<this.row; i++){
			for(int j=0; j<this.column; j++){
				if(m[i][j]==elem){
					rowElem = i;
					return rowElem;
				}
			}
		}
		
		return 0;
	}
	
	
	public int colElem(double elem){
		int colElem;
		
		for(int i=0; i<this.row; i++){
			for(int j=0; j<this.column; j++){
				if(m[i][j]==elem){
					colElem = j;
					return colElem;
				}
			}
		}
		
		return 0;
	}
	
	public void findEmpties(Matrix positions){
		int index;
		
		for(int i=0; i<this.row; i++){
			for(int j=0; j<this.column; j++){
				if(m[i][j]==-1){
					index = (j + 1) % this.column;
					if(m[i][index]==-1){
						positions.setElement(0, 0, i);
						positions.setElement(0, 1, j);
						positions.setElement(1, 0, i);
						positions.setElement(1, 1, index);
						return;
					}
					index = (j + this.column -1) % this.column;
					if(m[i][index]==-1){
						positions.setElement(0, 0, i);
						positions.setElement(0, 1, j);
						positions.setElement(1, 0, i);
						positions.setElement(1, 1, index);
						return;
					}
					index = (i + 1) % this.row;
					if(m[index][j]==-1){
						positions.setElement(0, 0, i);
						positions.setElement(0, 1, j);
						positions.setElement(1, 0, index);
						positions.setElement(1, 1, j);
						return;
					}
					index = (i + this.row - 1) % this.row;
					if(m[index][j]==-1){
						positions.setElement(0, 0, i);
						positions.setElement(0, 1, j);
						positions.setElement(1, 0, index);
						positions.setElement(1, 1, j);
						return;
					}
				}
			}
		}	
		
	}
	
	public void findNearEmpty(Matrix positions, int i, int j){
		int index;
		
		index = (j + 1) % this.column;
		if(m[i][index]==-1){
			positions.setElement(0, 0, i);
			positions.setElement(0, 1, index);
		}
		index = (j + this.column -1) % this.column;
		if(m[i][index]==-1){
			positions.setElement(0, 0, i);
			positions.setElement(0, 1, index);
		}
		index = (i + 1) % this.row;
		if(m[index][j]==-1){
			positions.setElement(0, 0, index);
			positions.setElement(0, 1, j);
		}
		index = (i + this.row - 1) % this.row;
		if(m[index][j]==-1){
			positions.setElement(0, 0, index);
			positions.setElement(0, 1, j);
		}
	}
	
	public boolean full(){
		for(int i=0; i<this.row; i++){
			for(int j=0; j<this.column; j++){
				if(m[i][j]==-1)
					return false;
			}
		}
		
		return true;

	}

	public void clone(Matrix cloned){
		
		cloned.setColumn(this.column);
		cloned.setRow(this.row);

		for(int i=0; i<this.row; i++){
			for(int j=0; j<this.column; j++){
				cloned.setElement(i, j, getElement(i,j));
			}
		}
		
		
	}
	
	public String toString (){
		String s = "";
		
		for(int i=0; i<this.row; i++){
			for(int j=0; j<this.column; j++){
				s = s + (m[i][j] + "  ");
			}
				s = s + ("\n");
		}
		
		return s;
	}
	
    public DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> matrixToGraph(){
    	DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> graph;
    	DefaultWeightedEdge edge = new DefaultWeightedEdge();

    	int index;

    	graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
    	
    	for(int i=0; i<this.row; i++){
			for(int j=0; j<this.column; j++){
				graph.addVertex("v" + (int)m[i][j]);
			}
		}
    	
    	for(int i=0; i<this.row; i++){
			for(int j=0; j<this.column; j++){
				index = (j + 1) % this.column;
				edge = graph.addEdge("v" + (int)m[i][j], "v" + (int)m[i][index]);
				if(edge != null)		
					graph.setEdgeWeight(edge, 1);

				index = (j + this.column -1) % this.column;
				edge = graph.addEdge("v" + (int)m[i][j], "v" + (int)m[i][index]);
				if(edge != null)
					graph.setEdgeWeight(edge, 1);

				
				index = (i + 1) % this.row;
				edge = graph.addEdge("v" + (int)m[i][j], "v" + (int)m[index][j]);
				if(edge != null)
					graph.setEdgeWeight(edge, 1);

				index = (i + this.row - 1) % this.row;
				edge = graph.addEdge("v" + (int)m[i][j], "v" + (int)m[index][j]);
				if(edge != null)
					graph.setEdgeWeight(edge, 1);

			}
		}
    	return graph;
    	
    }  
    
    public void swap(double entry1, double entry2){
    	int row1;
    	int row2;
    	int col1;
    	int col2;
    	
    	row1 = rowElem(entry1);
    	col1 = colElem(entry1);
    	row2 = rowElem(entry2);
    	col2 = colElem(entry2);
    	
    	m[row1][col1] = entry2;
    	m[row2][col2] = entry1;
    }

}
