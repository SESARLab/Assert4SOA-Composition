package eu.unimi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mxgraph.io.mxGraphMlCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

import eu.assert4soa.datamodel.ASSERT;
import eu.assert4soa.datamodel.PartiallyOrderedSet;
import eu.unimi.composition.VirtualNodeOperation;

public final class Util {
	private Util() throws Exception{
		throw new Exception("Error initializing Util Class");
	}
	
	/**
	 * Function useful to order the partial order in a queue
	 * @param partialOrder
	 * @return
	 */
	public static <E> LinkedList<E> enqueuePartialOrder(PartiallyOrderedSet<Set<E>> partialOrder){
		LinkedList <E> result = null;
		if(partialOrder != null && !partialOrder.isEmpty()){
			result = new LinkedList<E>();
			while(!partialOrder.isEmpty()){
				Set<Set<E>> max = partialOrder.getMaximelElements();
				Iterator<Set<E>> set = max.iterator();
				while(set.hasNext()){
					Set<E> el = set.next();
					Iterator<E> elements = el.iterator();
					while(elements.hasNext()){
						E element = elements.next();
						result.addLast(element);
					}
				}
				partialOrder.removeElements(max);
			}
		}
		return result;
	}
	
	public static mxGraph mergeGraph(mxGraph graphLeft, VirtualNodeOperation op, mxGraph graphRight){
		switch (op) {
		case SEQUENCE:
			graphLeft = mergeSequence(graphLeft, graphRight);
			break;
		case PARALLEL:
			graphLeft = mergeParallel(graphLeft, graphRight);
			break;
		case ALTERNATIVE:
			graphLeft = mergeAlternative(graphLeft, graphRight);
			break;
		}
		return graphLeft;
		
	}
	
	/**
	 * Merge alternative Model graph
	 * @param graphLeft
	 * @param graphRight
	 * @return
	 */
	private static mxGraph mergeAlternative(mxGraph graphL,
			mxGraph graphR) {
		mxGraphModel graphLeft = (mxGraphModel) graphL.getModel();
		mxGraphModel graphRight = (mxGraphModel) graphR.getModel();
		
		// Prendo la root del grafo sinistro
		ArrayList<Object> childList = new ArrayList<>(1);
		childList.add(graphLeft.getRoot());
		
		Object[] edges = null;
		
		// Elimino le foglie nel grafo di destra
		ArrayList<Object> leafRight = Util.findLeaf(graphRight);
		if(!leafRight.equals(null) && leafRight.size() > 0){
			for(Object leaf : leafRight){
				Object[] leafEdges = mxGraphModel.getEdges(graphRight, leaf);
				if(!leafEdges.equals(null) && leafEdges.length > 0){
					for(Object leafEdge : leafEdges){
						graphRight.remove(leafEdge);
					}
				}
				graphRight.remove(leafRight.get(0));
			}
		}

		// Calcolo le nuove foglie a questo punto
		leafRight = Util.findLeaf(graphRight);
		// Calcolo le foglie del grafo di sinistra
		ArrayList<Object> leftLeaf = Util.findLeaf(graphLeft);	
		
		ArrayList<mxCell> rightChildren = Util.getChildren(graphRight, graphRight.getRoot());
		ArrayList<Object> newLeftChildren = new ArrayList<>(rightChildren.size());
		if(!rightChildren.equals(null)){
			for(mxCell child : rightChildren){
				boolean leaf = false;
				if(leafRight.contains(child)) leaf=true;
				
				edges = mxGraphModel.getIncomingEdges(graphRight, child);
				if(!edges.equals(null) && edges.length > 0){
					for(Object edge : edges){
						child.removeEdge((mxICell) edge, false);
					}
				}
				Object o = graphL.insertVertex(graphLeft.getParent(graphLeft.getRoot()), null, child.getValue(), 0, 0, 0, 0);
				newLeftChildren.add(o);
				if(!leftLeaf.equals(null) && leaf == true){
					for(Object lefty : leftLeaf){
						graphL.insertEdge(graphLeft.getParent(graphLeft.getRoot()), null, null, lefty, o, null);
					}
						
					
				}
			}
		}
		
		if(!childList.equals(null) && childList.size() > 0){
			for(Object childLeft : childList){
				if(!newLeftChildren.equals(null) && newLeftChildren.size() > 0){
					for(Object childRight : newLeftChildren){
						graphL.insertEdge(graphLeft.getParent(graphLeft.getRoot()), null, null, childLeft, childRight, null);
					}
				}
			}
		}
		
		//Funzione ricorsiva sull'albero
		if(!rightChildren.equals(null)){
			for(Object childRight : rightChildren){
				graphL = recurMergeAlt(newLeftChildren, childRight, graphL, graphR, leafRight, leftLeaf);
			}
		}
		
		
		return graphL;
	}

	private static mxGraph recurMergeAlt(ArrayList<Object> childList,
			Object childRight, mxGraph graphL, mxGraph graphR,
			ArrayList<Object> leafRight, ArrayList<Object> leftLeaf) {
		mxGraphModel graphLeft = (mxGraphModel) graphL.getModel();
		mxGraphModel graphRight = (mxGraphModel) graphR.getModel();
		
		Object[] edges = null;
		
		ArrayList<mxCell> rightChildren = Util.getChildren(graphRight, childRight);
		ArrayList<Object> newLeftChildren = new ArrayList<>(rightChildren.size());
		if(!rightChildren.equals(null)){
			for(mxCell child : rightChildren){
				boolean leaf = false;
				if(leafRight.contains(child)) leaf=true;
				
				edges = mxGraphModel.getIncomingEdges(graphRight, child);
				if(!edges.equals(null) && edges.length > 0){
					for(Object edge : edges){
						child.removeEdge((mxICell) edge, false);
					}
				}
				Object o = graphL.insertVertex(graphLeft.getParent(graphLeft.getRoot()), null, child.getValue(), 0, 0, 0, 0);
				newLeftChildren.add(o);
				if(!leftLeaf.equals(null) && leaf == true){
					for(Object lefty : leftLeaf){
						graphL.insertEdge(graphLeft.getParent(graphLeft.getRoot()), null, null, lefty, o, null);
					}
						
					
				}
			}
		}
		
		if(!childList.equals(null) && childList.size() > 0){
			for(Object childLeft : childList){
				if(!newLeftChildren.equals(null) && newLeftChildren.size() > 0){
					for(Object newChild : newLeftChildren){
						graphL.insertEdge(graphLeft.getParent(graphLeft.getRoot()), null, null, childLeft, newChild, null);
					}
				}
			}
		}
		
		if(!rightChildren.equals(null)){
			//Funzione ricorsiva sull'albero
			for(Object child : rightChildren){
				graphL = recurMergeAlt(newLeftChildren, child, graphL, graphR, leafRight, leftLeaf);
			}
		}
		
		
		return graphL;
	}

	/**
	 * Merge parallel model graph
	 * @param graphLeft
	 * @param graphRight
	 * @return
	 */
	private static mxGraph mergeParallel(mxGraph graphL,
			mxGraph graphR) {
		mxGraphModel graphLeft = (mxGraphModel) graphL.getModel();
		mxGraphModel graphRight = (mxGraphModel) graphR.getModel();
		
		// Prendo la root del grafo sinistro
		ArrayList<Object> childList = new ArrayList<>(1);
		childList.add(graphLeft.getRoot());
		
		Object[] edges = null;
		
		ArrayList<mxCell> rightChildren = Util.getChildren(graphRight, graphRight.getRoot());
		ArrayList<Object> newLeftChildren = new ArrayList<>(rightChildren.size());
		if(!rightChildren.equals(null)){
			for(mxCell child : rightChildren){
				edges = mxGraphModel.getIncomingEdges(graphRight, child);
				if(!edges.equals(null) && edges.length > 0){
					for(Object edge : edges){
						child.removeEdge((mxICell) edge, false);
					}
				}
				Object o = graphL.insertVertex(graphLeft.getParent(graphLeft.getRoot()), null, child.getValue(), 0, 0, 0, 0);
				newLeftChildren.add(o);
			}
		}
		
		if(!childList.equals(null) && childList.size() > 0){
			for(Object childLeft : childList){
				if(!newLeftChildren.equals(null) && newLeftChildren.size() > 0){
					for(Object childRight : newLeftChildren){
						graphL.insertEdge(graphLeft.getParent(graphLeft.getRoot()), null, null, childLeft, childRight, null);
					}
				}
			}
		}
		
		//Funzione ricorsiva sull'albero
		for(Object childRight : rightChildren){
			graphL = recurMergeSeq(newLeftChildren, childRight, graphL, graphR);
		}
		return graphL;
	}

	/**
	 * Merge Sequence model graph
	 * @param graphLeft
	 * @param graphRight
	 * @return
	 */
	private static mxGraph mergeSequence(mxGraph graphL,
			mxGraph graphR) {
		
		mxGraphModel graphLeft = (mxGraphModel) graphL.getModel();
		mxGraphModel graphRight = (mxGraphModel) graphR.getModel();
		
		// Trovo le foglie del grafo sinistro
		ArrayList<Object> childList = Util.findLeaf(graphLeft);
		Object[] edges = null;
		
		//Trovo i figli della root su cui mi andr˜ a connettere
		Object root = graphRight.getRoot();
		ArrayList<mxCell> rightChildren = Util.getChildren(graphRight, root);
		ArrayList<Object> newLeftChildren = new ArrayList<>(rightChildren.size());
		if(!rightChildren.equals(null)){
			for(mxCell child : rightChildren){
				edges = mxGraphModel.getIncomingEdges(graphRight, child);
				if(!edges.equals(null) && edges.length > 0){
					for(Object edge : edges){
						child.removeEdge((mxICell) edge, false);
					}
				}
				Object o = graphL.insertVertex(graphLeft.getParent(graphLeft.getRoot()), null, child.getValue(), 0, 0, 0, 0);
				newLeftChildren.add(o);
			}
		}
		
		if(!childList.equals(null) && childList.size() > 0){
			for(Object childLeft : childList){
				if(!newLeftChildren.equals(null) && newLeftChildren.size() > 0){
					for(Object childRight : newLeftChildren){
						graphL.insertEdge(graphLeft.getParent(graphLeft.getRoot()), null, null, childLeft, childRight, null);
					}
				}
			}
		}
		
		//Funzione ricorsiva sull'albero
		for(Object childRight : rightChildren){
			graphL = recurMergeSeq(newLeftChildren, childRight, graphL, graphR);
		}
		return graphL;
		
	}

	/**
	 * Ritorna sempre il solito model fasullo
	 * @return
	 */
	public static mxGraph loadFakeModel(){
		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();
		
		Object o1 = graph.insertVertex(parent, null, 1, 0, 0, 0, 0);
		Object o2 = graph.insertVertex(parent, null, 2, 0, 0, 0, 0);
		Object o3 = graph.insertVertex(parent, null, 3, 0, 0, 0, 0);
		Object o4 = graph.insertVertex(parent, null, 4, 0, 0, 0, 0);
		Object o5 = graph.insertVertex(parent, null, 5, 0, 0, 0, 0);
		Object o6 = graph.insertVertex(parent, null, 6, 0, 0, 0, 0);
		Object o7 = graph.insertVertex(parent, null, 7, 0, 0, 0, 0);
		Object o8 = graph.insertVertex(parent, null, 8, 0, 0, 0, 0);
		
		
		Object e1 = graph.insertEdge(parent, null, null, o1, o2);
		Object e2 = graph.insertEdge(parent, null, null, o2, o3);
		Object e3 = graph.insertEdge(parent, null, null, o2, o4);
		Object e4 = graph.insertEdge(parent, null, null, o4, o5);
		Object e5 = graph.insertEdge(parent, null, null, o6, o7);
		Object e6 = graph.insertEdge(parent, null, null, o7, o8);
		
		return graph;
	}

	
	
	private static mxGraph recurMergeSeq(ArrayList<Object> childList,
			Object childRight, mxGraph graphL, mxGraph graphR) {
		
		mxGraphModel graphLeft = (mxGraphModel) graphL.getModel();
		mxGraphModel graphRight = (mxGraphModel) graphR.getModel();
		Object[] edges = null;
		
		//Trovo i figli dell'elemento del grafo destro
		ArrayList<mxCell> rightChildren = Util.getChildren(graphRight, childRight);
		ArrayList<Object> newLeftChildren = new ArrayList<>();
		if(!rightChildren.equals(null) && rightChildren.size() > 0){
			for(mxCell child : rightChildren){
				edges = mxGraphModel.getIncomingEdges(graphRight, child);
				if(!edges.equals(null) && edges.length > 0){
					for(Object edge : edges){
						child.removeEdge((mxICell) edge, false);
					}
				}
				
				Object o = graphL.insertVertex(graphLeft.getParent(graphLeft.getRoot()), null, child.getValue(), 0, 0, 0, 0);
				newLeftChildren.add(o);
			}
			
			if(!childList.equals(null) && childList.size() > 0){
				for(Object childLeft : childList){
					if(!newLeftChildren.equals(null) && newLeftChildren.size() > 0){
						for(Object child : newLeftChildren){
							graphL.insertEdge(graphLeft.getParent(graphLeft.getRoot()), null, null, childLeft, child, null);
						}
					}
				}
			}
			
			//Funzione ricorsiva sull'albero
			for(Object child : rightChildren){
				graphL = recurMergeSeq(newLeftChildren, child, graphL, graphR);
			}
			
		}
		return graphL;
	}

	/**
	 * Check if a cell is a leaf of the graph
	 * @param model
	 * @param cell
	 * @return
	 */
	private static boolean isLeaf(mxGraphModel model, Object cell){
		if(((mxCell)cell).isVertex()){
			Object[] edges = mxGraphModel.getOutgoingEdges(model, cell);
			//int i = model.getChildCount(cell); 
			if(edges.length == 0){
				return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * Return the children of a node
	 * @param model
	 * @param cell
	 * @return
	 */
	public static ArrayList<mxCell> getChildren(mxGraphModel model, Object cell){
		if(model != null && cell != null){
			Object[] edges = mxGraphModel.getOutgoingEdges(model, cell);
			ArrayList<mxCell> children = new ArrayList<mxCell>();
			for(Object edge : edges){
				mxICell child = ((mxCell)edge).getTerminal(false);
				children.add((mxCell) child);
			}
			return children;
		}
		return null;
	}
	
	/**
	 * Find the parent of a vertex looking the edges
	 * @param model
	 * @param cell
	 * @return
	 */
	public static ArrayList<mxCell> getParent(mxGraphModel model, Object cell){
		if(model != null && cell != null){
			Object[] edges = mxGraphModel.getIncomingEdges(model, cell);
			ArrayList<mxCell> children = new ArrayList<mxCell>();
			for(Object edge : edges){
				mxICell child = ((mxCell)edge).getTerminal(true);
				children.add((mxCell) child);
			}
			return children;
		}
		return null;
	}
	
	/**
	 * Return the leaf of a graph (not checking the type of leaf)
	 * @param graph
	 * @return
	 */
	public static ArrayList<Object> findLeaf(mxGraphModel graph){
		if(graph != null){
			// Trovo le foglie del grafo a sinistra
			Map<String, Object> cells = graph.getCells();
			// cells.remove(graphLeft.getRoot());
			Iterator<Entry<String, Object>> i = cells.entrySet().iterator();
			ArrayList<Object> childList = new ArrayList<Object>();
			while (i.hasNext()) {
				Entry<String, Object> el = i.next();
				if (isLeaf(graph, el.getValue())) {
					childList.add(el.getValue());
				}
			}
			return childList;
		}
		return null;
		
	}

	public static mxGraph removeVertexWithEdge(mxGraph graph, mxCell source) {
		if(source.isVertex()){
			mxGraphModel model = (mxGraphModel) graph.getModel();
			
			model.beginUpdate();
			try{
				Object[] edges = mxGraphModel.getEdges(model, source);
				if (edges != null) {
					for (Object edge : edges) {
						model.remove(edge);
					}
					model.remove(source);
				}
			} finally {
				model.endUpdate();
			}
		}
		return graph;
		
	}
}
