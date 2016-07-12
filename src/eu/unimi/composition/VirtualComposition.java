package eu.unimi.composition;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGraphMlCodec;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

import eu.unimi.retriever.Retriever;
import eu.unimi.util.DBConn;

public class VirtualComposition {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		mxGraphModel model1 = new mxGraphModel();
		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();
		mxGraphModel model = (mxGraphModel) graph.getModel();
		model.beginUpdate();
		//mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		//layout.setDisableEdgeStyle(false);
		//layout.execute(graph.getDefaultParent());
		Object o1;
		Object o2;
		Object o3;
		Object o0;
		
		try{
//			o0 = new mxCell("*");
//			o1 = new mxCell("1");
//			o2 = new mxCell("2");
//			o3 = new mxCell("3");
//			model.add(o0, o1, 0);
//			model.add(o0, o2, 0);
//			model.add(o2, o3, 0);
			
			//o0 = graph.insertVertex(parent , "0", "*", 0, 0, 0, 0);
			o1 = graph.insertVertex(parent , "1", "Hello", 0, 0, 0, 0);
			o2 = graph.insertVertex(parent , "2", "Hello2", 0, 0, 0, 0);
			o3 = graph.insertVertex(o2 , "3", "Hello3", 0, 0, 0, 0);
			//model.setRoot(o0);
			
//			Object e1 = graph.insertEdge(parent, null, null, o0, o1);
//			Object e2 = graph.insertEdge(parent, null, null, o0, o2);
//			Object e3 = graph.insertEdge(parent, null, null, o2, o3);
			
			
		} finally {
			model.endUpdate();
		}
		
		mxCodec enc = new mxCodec();
		
		mxGraphModel mod = (mxGraphModel) graph.getModel();
		Map<String,Object> cells = mod.getCells();
		Document doc = mxDomUtils.createDocument();
		Object[] objs = cells.values().toArray();
//		for(Object obj : objs){
//			//Node nod = enc.encode(obj);
//			Node nod2 = doc.importNode(enc.encode(obj), false);
//			doc.appendChild(nod2);
//			String x = mxUtils.getPrettyXml(nod2);
//			System.out.println(x);
//		}
		Node nod = enc.encode(objs);
		
//		mxCodec cod = new mxCodec();
//		Node nod = cod.encode(graph);
//		//Document doc = mxGraphMlCodec.encode(graph);
		
		Document graphML = mxGraphMlCodec.encode(graph);
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer;
		
		try {
			transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			//DOMSource source = new DOMSource(nod);
			DOMSource source = new DOMSource(graphML);
			//StreamResult result = new StreamResult(System.out);
			StreamResult result = new StreamResult(new File("/Users/jonny/Desktop/grafo.xml"));
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		mxCell o5 = (mxCell) ((mxGraphModel) graph.getModel()).getNearestCommonAncestor(o2, o3);
		System.out.println(graph.getModel().isAncestor(o1, o3));
		
		System.out.println(o5.getValue());
		
		
		
//		try {
//			String xml = mxUtils.readFile("/Users/jonny/Desktop/grafo.xml");
//			System.out.println(xml);
//		} catch (IOException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
//		
		// Leggo il file
//		Document newDoc = null;
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		
//		File f = new File("/Users/jonny/Desktop/grafo.xml"); 
//		DocumentBuilder builder;
//		try {
//			builder = factory.newDocumentBuilder();
//			newDoc = builder.parse(f);
//		} catch (SAXException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ParserConfigurationException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		mxGraph graph2 = new mxGraph();
//		NodeList nodes = newDoc.getChildNodes();
//		
//		Object test = enc.decodeCell(newDoc, true);
		//Object dec = enc.decode(newDoc);
		//graph2.addCells((Object[]) dec);
//		
//		Object par = graph2.getDefaultParent();
//		mxCell root = (mxCell)graph2.getModel().getRoot();
//		mxGraphModel modGraph2 = (mxGraphModel)graph2.getModel();
//		Map<String,Object> map2 = modGraph2.getCells();
//		for(Object val : map2.values()){
//			mxCell cel = (mxCell)val;
//			System.out.println(cel.getValue());
//		}
//		
		
		//System.out.println(root.getChildCount());
		Document graphml = mxUtils.loadDocument("/Users/jonny/Desktop/grafo.xml");
		mxGraphMlCodec.decode(graphml, graph2);
//		mxCell root = (mxCell) graph2.getModel().getRoot();
//		mxCell parent2 = (mxCell) graph2.getDefaultParent();
//		System.out.println("Root: " + parent2.getValue());
//		mxGraphModel modProva = (mxGraphModel) graph2.getModel();
//		Object[] listChildren = modProva.getChildren(modProva, parent2);
//		for(Object obj : listChildren){
//			Object[] sChildren = modProva.getChildren(modProva, obj);
//			mxCell obj1 = (mxCell) obj;
//			System.out.println(obj1.getValue());
//			for(Object obj2 : sChildren){
//				mxCell cell = (mxCell) obj2;
//				System.out.println(cell.getValue() instanceof String);
//				System.out.println(cell.getValue());
//			}
//		}
		
		toVirtualCertificate(graph2);
	}
	
	public static VirtualCertificate toVirtualCertificate(mxGraph graph){
		if(graph != null){
			mxGraphModel modTest = (mxGraphModel) graph.getModel();
			mxCell root = (mxCell) modTest.getRoot();
			if(modTest.getChildCount(root) != 0){
				mxGraph results = new mxGraph();
				System.out.println("Ci sono più elementi");
				Object[] childrens = mxGraphModel.getChildren(modTest, root);
				for(Object obj : childrens){
					mxCell obj1 = (mxCell) obj;
					System.out.println(obj1.getValue());
					Object[] sChildren = mxGraphModel.getChildren(modTest, obj1);
					for(Object objchil : sChildren){
						recursiveToVirtualCertificate(objchil, graph, results);
						mxCell obj2 = (mxCell) objchil;
						System.out.println(obj2.getValue());
					}
				}
			}
		}
		
		
		
		return null;
		
	}

	private static void recursiveToVirtualCertificate(Object objchil,
			mxGraph graph, mxGraph results) {
		// TODO Auto-generated method stub
		
	}

}
