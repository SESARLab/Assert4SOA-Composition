package eu.unimi.composition;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGraphMlCodec;
import com.mxgraph.io.mxModelCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

import eu.assert4soa.datamodel.ASSERT;
import eu.assert4soa.datamodel.PartiallyOrderedSet;
import eu.assert4soa.datamodel.datamodelFactory;
import eu.unimi.ematchmakerconfiguration.ComparisonType;
import eu.unimi.ematchmakerconfiguration.EMatchMakerConfiguration;
import eu.unimi.ematchmakerconfiguration.EMatchMakerModel;
import eu.unimi.ematchmakerconfiguration.EMatchMakerTestEvidence;
import eu.unimi.ematchmakerconfiguration.EvidenceWeight;
import eu.unimi.ematchmakerconfiguration.ModelType;
import eu.unimi.ematchmakerconfiguration.ModelWeight;
import eu.unimi.ematchmakerconfiguration.OrderComparison;
import eu.unimi.ematchmakerconfiguration.QualityLevel;
import eu.unimi.matchmaker.SlaveMatchMakerE;
import eu.unimi.ontologyquestioner.BaseXOntologyManager;
import eu.unimi.ontologyquestioner.IOntologyQuestioner;
import eu.unimi.retriever.Retriever;
import eu.unimi.util.DBConn;
import eu.unimi.util.RuleOperation;
import eu.unimi.util.Util;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Test enqueue
		//testEnqueue();
		//testCheckRule();
		//testMergeGraph();
		//createGraphBPEL();
		
		BaseXOntologyManager ontology = new BaseXOntologyManager("localhost", 1984, "developer", "developer", "Ontology");
		SlaveMatchMakerE matchMaker = new SlaveMatchMakerE(null, ontology );
		
		DBConn conn = createConn();
		datamodelFactory dmf = datamodelFactory.eINSTANCE;
		
		ArrayList<String> results = new ArrayList<String>();
		List<ASSERT> asserts = new ArrayList<ASSERT>();
		
		System.out.println("");
		System.out.println("ASSERT Presenti nel DB");
		System.out.println("");
		
		results = ontology.getAllAssert();
		if (results != null){
			System.out.println("Numero di assert presenti nel db: " + results.size());
			
			for(String result : results) {
				asserts.add(dmf.createASSERT(result));
			}
		}
		
		System.out.println("Finita creazione ASSERT");
		
		Set<ASSERT> elementToUse = new HashSet<ASSERT>();
		
		long startTime, endTime, duration;
		
		mxGraph bpel = null;
		
		for(int i=38; i<=50; i=i+3){
		//for(int i = 1; i <=2; i++){
			elementToUse.clear();
			for(int j = 0; j <= 49; j++){
				elementToUse.add(asserts.get(j));
			}
			
			startTime = System.currentTimeMillis();
			
			bpel = loadGraph20(elementToUse, ontology, conn, matchMaker);
			
			bpel = createVirtualCertificate(bpel, ontology);
			
			endTime = System.currentTimeMillis();
			duration = endTime - startTime;
			
			System.out.println("Tempo di esecuzione: " + duration);
		}
		
		conn.closeConn();
		
	}
	
	private static mxGraph loadGraph20(Set<ASSERT> asserts,
			BaseXOntologyManager ontology, DBConn conn,
			SlaveMatchMakerE matchMaker) {
		mxGraph bpel = new mxGraph();
		Object parent = bpel.getDefaultParent();
		mxGraphModel model = (mxGraphModel) bpel.getModel();
		
		GraphBPELNode BpelNode1 = new GraphBPELNode();
		BpelNode1.setId("1");
		BpelNode1.setType(VirtualNodeType.OPERATION);
		BpelNode1.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode2 = new GraphBPELNode();
		BpelNode2.setId("2");
		BpelNode2.setType(VirtualNodeType.CERT);
		BpelNode2.setConfPath("conf.json");
		BpelNode2.loadCertificates(matchMaker, ontology, asserts);
		BpelNode2.setConn(conn);
		//BpelNode2.insertTestCategory(Retriever.getTestCategory(BpelNode2.getCert()), Retriever.getTestType(BpelNode2.getCert()));
		//BpelNode2.setCERTs(null);
		
		GraphBPELNode BpelNode3 = new GraphBPELNode();
		BpelNode3.setId("3");
		BpelNode3.setType(VirtualNodeType.CERT);
		BpelNode3.setConfPath("conf.json");
		BpelNode3.loadCertificates(matchMaker, ontology, asserts);
		BpelNode3.setConn(conn);
		//BpelNode3.insertTestCategory(Retriever.getTestCategory(BpelNode3.getCert()), Retriever.getTestType(BpelNode3.getCert()));
		//BpelNode3.setCERTs(null);
		
		GraphBPELNode BpelNode4 = new GraphBPELNode();
		BpelNode4.setId("4");
		BpelNode4.setType(VirtualNodeType.CERT);
		BpelNode4.setConfPath("conf.json");
		BpelNode4.loadCertificates(matchMaker, ontology, asserts);
		BpelNode4.setConn(conn);
		//BpelNode4.insertTestCategory(Retriever.getTestCategory(BpelNode4.getCert()), Retriever.getTestType(BpelNode4.getCert()));
		//BpelNode4.setCERTs(null);
		
		GraphBPELNode BpelNode5 = new GraphBPELNode();
		BpelNode5.setId("5");
		BpelNode5.setType(VirtualNodeType.OPERATION);
		BpelNode5.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode6 = new GraphBPELNode();
		BpelNode6.setId("6");
		BpelNode6.setType(VirtualNodeType.OPERATION);
		BpelNode6.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode7 = new GraphBPELNode();
		BpelNode7.setId("7");
		BpelNode7.setType(VirtualNodeType.CERT);
		BpelNode7.setConfPath("conf.json");
		BpelNode7.loadCertificates(matchMaker, ontology, asserts);
		BpelNode7.setConn(conn);
		//BpelNode7.insertTestCategory(Retriever.getTestCategory(BpelNode7.getCert()), Retriever.getTestType(BpelNode7.getCert()));
		//BpelNode7.setCERTs(null);
		
		GraphBPELNode BpelNode8 = new GraphBPELNode();
		BpelNode8.setId("8");
		BpelNode8.setType(VirtualNodeType.CERT);
		BpelNode8.setConfPath("conf.json");
		BpelNode8.loadCertificates(matchMaker, ontology, asserts);
		BpelNode8.setConn(conn);
		//BpelNode8.insertTestCategory(Retriever.getTestCategory(BpelNode8.getCert()), Retriever.getTestType(BpelNode8.getCert()));
		//BpelNode8.setCERTs(null);
		
		GraphBPELNode BpelNode9 = new GraphBPELNode();
		BpelNode9.setId("9");
		BpelNode9.setType(VirtualNodeType.CERT);
		BpelNode9.setConfPath("conf.json");
		BpelNode9.loadCertificates(matchMaker, ontology, asserts);
		BpelNode9.setConn(conn);
		//BpelNode9.setCERTs(null);
		
		GraphBPELNode BpelNode10 = new GraphBPELNode();
		BpelNode10.setId("10");
		BpelNode10.setType(VirtualNodeType.CERT);
		BpelNode10.setConfPath("conf.json");
		BpelNode10.loadCertificates(matchMaker, ontology, asserts);
		BpelNode10.setConn(conn);
		//BpelNode10.insertTestCategory(Retriever.getTestCategory(BpelNode10.getCert()), Retriever.getTestType(BpelNode10.getCert()));
		//BpelNode10.setCERTs(null);
		
		GraphBPELNode BpelNode11 = new GraphBPELNode();
		BpelNode11.setId("11");
		BpelNode11.setType(VirtualNodeType.CERT);
		BpelNode11.setConfPath("conf.json");
		BpelNode11.loadCertificates(matchMaker, ontology, asserts);
		BpelNode11.setConn(conn);
		//BpelNode11.insertTestCategory(Retriever.getTestCategory(BpelNode11.getCert()), Retriever.getTestType(BpelNode11.getCert()));
		//BpelNode11.setCERTs(null);
		
		GraphBPELNode BpelNode12 = new GraphBPELNode();
		BpelNode12.setId("12");
		BpelNode12.setType(VirtualNodeType.CERT);
		BpelNode12.setConfPath("conf.json");
		BpelNode12.loadCertificates(matchMaker, ontology, asserts);
		BpelNode12.setConn(conn);
		//BpelNode12.insertTestCategory(Retriever.getTestCategory(BpelNode12.getCert()), Retriever.getTestType(BpelNode12.getCert()));
		//BpelNode12.setCERTs(null);
		
		GraphBPELNode BpelNode13 = new GraphBPELNode();
		BpelNode13.setId("13");
		BpelNode13.setType(VirtualNodeType.OPERATION);
		BpelNode13.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode14 = new GraphBPELNode();
		BpelNode14.setId("14");
		BpelNode14.setType(VirtualNodeType.CERT);
		BpelNode14.setConfPath("conf.json");
		BpelNode14.loadCertificates(matchMaker, ontology, asserts);
		BpelNode14.setConn(conn);
		//BpelNode14.insertTestCategory(Retriever.getTestCategory(BpelNode14.getCert()), Retriever.getTestType(BpelNode14.getCert()));
		//BpelNode14.setCERTs(null);
		
		GraphBPELNode BpelNode15 = new GraphBPELNode();
		BpelNode15.setId("15");
		BpelNode15.setType(VirtualNodeType.CERT);
		BpelNode15.setConfPath("conf.json");
		BpelNode15.loadCertificates(matchMaker, ontology, asserts);
		BpelNode15.setConn(conn);
		
		GraphBPELNode BpelNode16 = new GraphBPELNode();
		BpelNode16.setId("16");
		BpelNode16.setType(VirtualNodeType.CERT);
		BpelNode16.setConfPath("conf.json");
		BpelNode16.loadCertificates(matchMaker, ontology, asserts);
		BpelNode16.setConn(conn);
		
		GraphBPELNode BpelNode17 = new GraphBPELNode();
		BpelNode17.setId("17");
		BpelNode17.setType(VirtualNodeType.CERT);
		BpelNode17.setConfPath("conf.json");
		BpelNode17.loadCertificates(matchMaker, ontology, asserts);
		BpelNode17.setConn(conn);
		
		GraphBPELNode BpelNode18 = new GraphBPELNode();
		BpelNode18.setId("18");
		BpelNode18.setType(VirtualNodeType.CERT);
		BpelNode18.setConfPath("conf.json");
		BpelNode18.loadCertificates(matchMaker, ontology, asserts);
		BpelNode18.setConn(conn);
		
		GraphBPELNode BpelNode19 = new GraphBPELNode();
		BpelNode19.setId("19");
		BpelNode19.setType(VirtualNodeType.CERT);
		BpelNode19.setConfPath("conf.json");
		BpelNode19.loadCertificates(matchMaker, ontology, asserts);
		BpelNode19.setConn(conn);
		
		GraphBPELNode BpelNode20 = new GraphBPELNode();
		BpelNode20.setId("20");
		BpelNode20.setType(VirtualNodeType.CERT);
		BpelNode20.setConfPath("conf.json");
		BpelNode20.loadCertificates(matchMaker, ontology, asserts);
		BpelNode20.setConn(conn);
		
		GraphBPELNode BpelNode21 = new GraphBPELNode();
		BpelNode21.setId("21");
		BpelNode21.setType(VirtualNodeType.CERT);
		BpelNode21.setConfPath("conf.json");
		BpelNode21.loadCertificates(matchMaker, ontology, asserts);
		BpelNode21.setConn(conn);
		
		GraphBPELNode BpelNode22 = new GraphBPELNode();
		BpelNode22.setId("22");
		BpelNode22.setType(VirtualNodeType.CERT);
		BpelNode22.setConfPath("conf.json");
		BpelNode22.loadCertificates(matchMaker, ontology, asserts);
		BpelNode22.setConn(conn);
		
		GraphBPELNode BpelNode23 = new GraphBPELNode();
		BpelNode23.setId("23");
		BpelNode23.setType(VirtualNodeType.CERT);
		BpelNode23.setConfPath("conf.json");
		BpelNode23.loadCertificates(matchMaker, ontology, asserts);
		BpelNode23.setConn(conn);
		
		
		Object o1 = bpel.insertVertex(parent, null, BpelNode1, 0, 0, 0, 0);
		Object o2 = bpel.insertVertex(parent, null, BpelNode2, 0, 0, 0, 0);
		Object o3 = bpel.insertVertex(parent, null, BpelNode3, 0, 0, 0, 0);
		Object o4 = bpel.insertVertex(parent, null, BpelNode4, 0, 0, 0, 0);
		Object o5 = bpel.insertVertex(parent, null, BpelNode5, 0, 0, 0, 0);
		Object o6 = bpel.insertVertex(parent, null, BpelNode6, 0, 0, 0, 0);
		Object o7 = bpel.insertVertex(parent, null, BpelNode7, 0, 0, 0, 0);
		Object o8 = bpel.insertVertex(parent, null, BpelNode8, 0, 0, 0, 0);
		Object o9 = bpel.insertVertex(parent, null, BpelNode9, 0, 0, 0, 0);
		Object o10 = bpel.insertVertex(parent, null, BpelNode10, 0, 0, 0, 0);
		Object o11 = bpel.insertVertex(parent, null, BpelNode11, 0, 0, 0, 0);
		Object o12 = bpel.insertVertex(parent, null, BpelNode12, 0, 0, 0, 0);
		Object o13 = bpel.insertVertex(parent, null, BpelNode13, 0, 0, 0, 0);
		Object o14 = bpel.insertVertex(parent, null, BpelNode14, 0, 0, 0, 0);
		Object o15 = bpel.insertVertex(parent, null, BpelNode15, 0, 0, 0, 0);
		Object o16 = bpel.insertVertex(parent, null, BpelNode16, 0, 0, 0, 0);
		Object o17 = bpel.insertVertex(parent, null, BpelNode17, 0, 0, 0, 0);
		Object o18 = bpel.insertVertex(parent, null, BpelNode18, 0, 0, 0, 0);
		Object o19 = bpel.insertVertex(parent, null, BpelNode19, 0, 0, 0, 0);
		Object o20 = bpel.insertVertex(parent, null, BpelNode20, 0, 0, 0, 0);
		Object o21 = bpel.insertVertex(parent, null, BpelNode21, 0, 0, 0, 0);
		Object o22 = bpel.insertVertex(parent, null, BpelNode22, 0, 0, 0, 0);
		Object o23 = bpel.insertVertex(parent, null, BpelNode23, 0, 0, 0, 0);
		
		bpel.insertEdge(parent, null, null, o1, o2);
		bpel.insertEdge(parent, null, null, o1, o3);
		bpel.insertEdge(parent, null, null, o1, o4);
		bpel.insertEdge(parent, null, null, o1, o17);
		bpel.insertEdge(parent, null, null, o1, o20);
		bpel.insertEdge(parent, null, null, o17, o5);
		bpel.insertEdge(parent, null, null, o20, o5);
		bpel.insertEdge(parent, null, null, o2, o5);
		bpel.insertEdge(parent, null, null, o3, o5);
		bpel.insertEdge(parent, null, null, o4, o5);
		bpel.insertEdge(parent, null, null, o5, o6);
		bpel.insertEdge(parent, null, null, o6, o7);
		bpel.insertEdge(parent, null, null, o6, o8);
		bpel.insertEdge(parent, null, null, o6, o9);
		bpel.insertEdge(parent, null, null, o6, o18);
		bpel.insertEdge(parent, null, null, o6, o22);
		bpel.insertEdge(parent, null, null, o18, o19);
		bpel.insertEdge(parent, null, null, o7, o10);
		bpel.insertEdge(parent, null, null, o8, o11);
		bpel.insertEdge(parent, null, null, o9, o12);
		bpel.insertEdge(parent, null, null, o22, o23);
		bpel.insertEdge(parent, null, null, o10, o13);
		bpel.insertEdge(parent, null, null, o11, o13);
	    bpel.insertEdge(parent, null, null, o12, o13);
	    bpel.insertEdge(parent, null, null, o19, o13);
	    bpel.insertEdge(parent, null, null, o23, o13);
		bpel.insertEdge(parent, null, null, o13, o14);
		bpel.insertEdge(parent, null, null, o14, o15);
		bpel.insertEdge(parent, null, null, o15, o16);
		bpel.insertEdge(parent, null, null, o16, o21);
		
		
		return bpel;
	}

	private static mxGraph loadGraph18(Set<ASSERT> asserts,
			BaseXOntologyManager ontology, DBConn conn,
			SlaveMatchMakerE matchMaker) {
		mxGraph bpel = new mxGraph();
		Object parent = bpel.getDefaultParent();
		mxGraphModel model = (mxGraphModel) bpel.getModel();
		
		GraphBPELNode BpelNode1 = new GraphBPELNode();
		BpelNode1.setId("1");
		BpelNode1.setType(VirtualNodeType.OPERATION);
		BpelNode1.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode2 = new GraphBPELNode();
		BpelNode2.setId("2");
		BpelNode2.setType(VirtualNodeType.CERT);
		BpelNode2.setConfPath("conf.json");
		BpelNode2.loadCertificates(matchMaker, ontology, asserts);
		BpelNode2.setConn(conn);
		//BpelNode2.insertTestCategory(Retriever.getTestCategory(BpelNode2.getCert()), Retriever.getTestType(BpelNode2.getCert()));
		//BpelNode2.setCERTs(null);
		
		GraphBPELNode BpelNode3 = new GraphBPELNode();
		BpelNode3.setId("3");
		BpelNode3.setType(VirtualNodeType.CERT);
		BpelNode3.setConfPath("conf.json");
		BpelNode3.loadCertificates(matchMaker, ontology, asserts);
		BpelNode3.setConn(conn);
		//BpelNode3.insertTestCategory(Retriever.getTestCategory(BpelNode3.getCert()), Retriever.getTestType(BpelNode3.getCert()));
		//BpelNode3.setCERTs(null);
		
		GraphBPELNode BpelNode4 = new GraphBPELNode();
		BpelNode4.setId("4");
		BpelNode4.setType(VirtualNodeType.CERT);
		BpelNode4.setConfPath("conf.json");
		BpelNode4.loadCertificates(matchMaker, ontology, asserts);
		BpelNode4.setConn(conn);
		//BpelNode4.insertTestCategory(Retriever.getTestCategory(BpelNode4.getCert()), Retriever.getTestType(BpelNode4.getCert()));
		//BpelNode4.setCERTs(null);
		
		GraphBPELNode BpelNode5 = new GraphBPELNode();
		BpelNode5.setId("5");
		BpelNode5.setType(VirtualNodeType.OPERATION);
		BpelNode5.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode6 = new GraphBPELNode();
		BpelNode6.setId("6");
		BpelNode6.setType(VirtualNodeType.OPERATION);
		BpelNode6.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode7 = new GraphBPELNode();
		BpelNode7.setId("7");
		BpelNode7.setType(VirtualNodeType.CERT);
		BpelNode7.setConfPath("conf.json");
		BpelNode7.loadCertificates(matchMaker, ontology, asserts);
		BpelNode7.setConn(conn);
		//BpelNode7.insertTestCategory(Retriever.getTestCategory(BpelNode7.getCert()), Retriever.getTestType(BpelNode7.getCert()));
		//BpelNode7.setCERTs(null);
		
		GraphBPELNode BpelNode8 = new GraphBPELNode();
		BpelNode8.setId("8");
		BpelNode8.setType(VirtualNodeType.CERT);
		BpelNode8.setConfPath("conf.json");
		BpelNode8.loadCertificates(matchMaker, ontology, asserts);
		BpelNode8.setConn(conn);
		//BpelNode8.insertTestCategory(Retriever.getTestCategory(BpelNode8.getCert()), Retriever.getTestType(BpelNode8.getCert()));
		//BpelNode8.setCERTs(null);
		
		GraphBPELNode BpelNode9 = new GraphBPELNode();
		BpelNode9.setId("9");
		BpelNode9.setType(VirtualNodeType.CERT);
		BpelNode9.setConfPath("conf.json");
		BpelNode9.loadCertificates(matchMaker, ontology, asserts);
		BpelNode9.setConn(conn);
		//BpelNode9.setCERTs(null);
		
		GraphBPELNode BpelNode10 = new GraphBPELNode();
		BpelNode10.setId("10");
		BpelNode10.setType(VirtualNodeType.CERT);
		BpelNode10.setConfPath("conf.json");
		BpelNode10.loadCertificates(matchMaker, ontology, asserts);
		BpelNode10.setConn(conn);
		//BpelNode10.insertTestCategory(Retriever.getTestCategory(BpelNode10.getCert()), Retriever.getTestType(BpelNode10.getCert()));
		//BpelNode10.setCERTs(null);
		
		GraphBPELNode BpelNode11 = new GraphBPELNode();
		BpelNode11.setId("11");
		BpelNode11.setType(VirtualNodeType.CERT);
		BpelNode11.setConfPath("conf.json");
		BpelNode11.loadCertificates(matchMaker, ontology, asserts);
		BpelNode11.setConn(conn);
		//BpelNode11.insertTestCategory(Retriever.getTestCategory(BpelNode11.getCert()), Retriever.getTestType(BpelNode11.getCert()));
		//BpelNode11.setCERTs(null);
		
		GraphBPELNode BpelNode12 = new GraphBPELNode();
		BpelNode12.setId("12");
		BpelNode12.setType(VirtualNodeType.CERT);
		BpelNode12.setConfPath("conf.json");
		BpelNode12.loadCertificates(matchMaker, ontology, asserts);
		BpelNode12.setConn(conn);
		//BpelNode12.insertTestCategory(Retriever.getTestCategory(BpelNode12.getCert()), Retriever.getTestType(BpelNode12.getCert()));
		//BpelNode12.setCERTs(null);
		
		GraphBPELNode BpelNode13 = new GraphBPELNode();
		BpelNode13.setId("13");
		BpelNode13.setType(VirtualNodeType.OPERATION);
		BpelNode13.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode14 = new GraphBPELNode();
		BpelNode14.setId("14");
		BpelNode14.setType(VirtualNodeType.CERT);
		BpelNode14.setConfPath("conf.json");
		BpelNode14.loadCertificates(matchMaker, ontology, asserts);
		BpelNode14.setConn(conn);
		//BpelNode14.insertTestCategory(Retriever.getTestCategory(BpelNode14.getCert()), Retriever.getTestType(BpelNode14.getCert()));
		//BpelNode14.setCERTs(null);
		
		GraphBPELNode BpelNode15 = new GraphBPELNode();
		BpelNode15.setId("15");
		BpelNode15.setType(VirtualNodeType.CERT);
		BpelNode15.setConfPath("conf.json");
		BpelNode15.loadCertificates(matchMaker, ontology, asserts);
		BpelNode15.setConn(conn);
		
		GraphBPELNode BpelNode16 = new GraphBPELNode();
		BpelNode16.setId("16");
		BpelNode16.setType(VirtualNodeType.CERT);
		BpelNode16.setConfPath("conf.json");
		BpelNode16.loadCertificates(matchMaker, ontology, asserts);
		BpelNode16.setConn(conn);
		
		GraphBPELNode BpelNode17 = new GraphBPELNode();
		BpelNode17.setId("17");
		BpelNode17.setType(VirtualNodeType.CERT);
		BpelNode17.setConfPath("conf.json");
		BpelNode17.loadCertificates(matchMaker, ontology, asserts);
		BpelNode17.setConn(conn);
		
		GraphBPELNode BpelNode18 = new GraphBPELNode();
		BpelNode18.setId("18");
		BpelNode18.setType(VirtualNodeType.CERT);
		BpelNode18.setConfPath("conf.json");
		BpelNode18.loadCertificates(matchMaker, ontology, asserts);
		BpelNode18.setConn(conn);
		
		GraphBPELNode BpelNode19 = new GraphBPELNode();
		BpelNode19.setId("19");
		BpelNode19.setType(VirtualNodeType.CERT);
		BpelNode19.setConfPath("conf.json");
		BpelNode19.loadCertificates(matchMaker, ontology, asserts);
		BpelNode19.setConn(conn);
		
		GraphBPELNode BpelNode20 = new GraphBPELNode();
		BpelNode20.setId("20");
		BpelNode20.setType(VirtualNodeType.CERT);
		BpelNode20.setConfPath("conf.json");
		BpelNode20.loadCertificates(matchMaker, ontology, asserts);
		BpelNode20.setConn(conn);
		
		GraphBPELNode BpelNode21 = new GraphBPELNode();
		BpelNode21.setId("21");
		BpelNode21.setType(VirtualNodeType.CERT);
		BpelNode21.setConfPath("conf.json");
		BpelNode21.loadCertificates(matchMaker, ontology, asserts);
		BpelNode21.setConn(conn);
		
		
		Object o1 = bpel.insertVertex(parent, null, BpelNode1, 0, 0, 0, 0);
		Object o2 = bpel.insertVertex(parent, null, BpelNode2, 0, 0, 0, 0);
		Object o3 = bpel.insertVertex(parent, null, BpelNode3, 0, 0, 0, 0);
		Object o4 = bpel.insertVertex(parent, null, BpelNode4, 0, 0, 0, 0);
		Object o5 = bpel.insertVertex(parent, null, BpelNode5, 0, 0, 0, 0);
		Object o6 = bpel.insertVertex(parent, null, BpelNode6, 0, 0, 0, 0);
		Object o7 = bpel.insertVertex(parent, null, BpelNode7, 0, 0, 0, 0);
		Object o8 = bpel.insertVertex(parent, null, BpelNode8, 0, 0, 0, 0);
		Object o9 = bpel.insertVertex(parent, null, BpelNode9, 0, 0, 0, 0);
		Object o10 = bpel.insertVertex(parent, null, BpelNode10, 0, 0, 0, 0);
		Object o11 = bpel.insertVertex(parent, null, BpelNode11, 0, 0, 0, 0);
		Object o12 = bpel.insertVertex(parent, null, BpelNode12, 0, 0, 0, 0);
		Object o13 = bpel.insertVertex(parent, null, BpelNode13, 0, 0, 0, 0);
		Object o14 = bpel.insertVertex(parent, null, BpelNode14, 0, 0, 0, 0);
		Object o15 = bpel.insertVertex(parent, null, BpelNode15, 0, 0, 0, 0);
		Object o16 = bpel.insertVertex(parent, null, BpelNode16, 0, 0, 0, 0);
		Object o17 = bpel.insertVertex(parent, null, BpelNode17, 0, 0, 0, 0);
		Object o18 = bpel.insertVertex(parent, null, BpelNode18, 0, 0, 0, 0);
		Object o19 = bpel.insertVertex(parent, null, BpelNode19, 0, 0, 0, 0);
		Object o20 = bpel.insertVertex(parent, null, BpelNode20, 0, 0, 0, 0);
		Object o21 = bpel.insertVertex(parent, null, BpelNode21, 0, 0, 0, 0);
		
		bpel.insertEdge(parent, null, null, o1, o2);
		bpel.insertEdge(parent, null, null, o1, o3);
		bpel.insertEdge(parent, null, null, o1, o4);
		bpel.insertEdge(parent, null, null, o1, o17);
		bpel.insertEdge(parent, null, null, o1, o20);
		bpel.insertEdge(parent, null, null, o17, o5);
		bpel.insertEdge(parent, null, null, o20, o5);
		bpel.insertEdge(parent, null, null, o2, o5);
		bpel.insertEdge(parent, null, null, o3, o5);
		bpel.insertEdge(parent, null, null, o4, o5);
		bpel.insertEdge(parent, null, null, o5, o6);
		bpel.insertEdge(parent, null, null, o6, o7);
		bpel.insertEdge(parent, null, null, o6, o8);
		bpel.insertEdge(parent, null, null, o6, o9);
		bpel.insertEdge(parent, null, null, o6, o18);
		bpel.insertEdge(parent, null, null, o18, o19);
		bpel.insertEdge(parent, null, null, o7, o10);
		bpel.insertEdge(parent, null, null, o8, o11);
		bpel.insertEdge(parent, null, null, o9, o12);
		bpel.insertEdge(parent, null, null, o10, o13);
		bpel.insertEdge(parent, null, null, o11, o13);
	    bpel.insertEdge(parent, null, null, o12, o13);
	    bpel.insertEdge(parent, null, null, o19, o13);
		bpel.insertEdge(parent, null, null, o13, o14);
		bpel.insertEdge(parent, null, null, o14, o15);
		bpel.insertEdge(parent, null, null, o15, o16);
		bpel.insertEdge(parent, null, null, o16, o21);
		
		
		return bpel;
	}

	private static mxGraph loadGraph16(Set<ASSERT> asserts,
			BaseXOntologyManager ontology, DBConn conn,
			SlaveMatchMakerE matchMaker) {
		mxGraph bpel = new mxGraph();
		Object parent = bpel.getDefaultParent();
		mxGraphModel model = (mxGraphModel) bpel.getModel();
		
		GraphBPELNode BpelNode1 = new GraphBPELNode();
		BpelNode1.setId("1");
		BpelNode1.setType(VirtualNodeType.OPERATION);
		BpelNode1.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode2 = new GraphBPELNode();
		BpelNode2.setId("2");
		BpelNode2.setType(VirtualNodeType.CERT);
		BpelNode2.setConfPath("conf.json");
		BpelNode2.loadCertificates(matchMaker, ontology, asserts);
		BpelNode2.setConn(conn);
		//BpelNode2.insertTestCategory(Retriever.getTestCategory(BpelNode2.getCert()), Retriever.getTestType(BpelNode2.getCert()));
		//BpelNode2.setCERTs(null);
		
		GraphBPELNode BpelNode3 = new GraphBPELNode();
		BpelNode3.setId("3");
		BpelNode3.setType(VirtualNodeType.CERT);
		BpelNode3.setConfPath("conf.json");
		BpelNode3.loadCertificates(matchMaker, ontology, asserts);
		BpelNode3.setConn(conn);
		//BpelNode3.insertTestCategory(Retriever.getTestCategory(BpelNode3.getCert()), Retriever.getTestType(BpelNode3.getCert()));
		//BpelNode3.setCERTs(null);
		
		GraphBPELNode BpelNode4 = new GraphBPELNode();
		BpelNode4.setId("4");
		BpelNode4.setType(VirtualNodeType.CERT);
		BpelNode4.setConfPath("conf.json");
		BpelNode4.loadCertificates(matchMaker, ontology, asserts);
		BpelNode4.setConn(conn);
		//BpelNode4.insertTestCategory(Retriever.getTestCategory(BpelNode4.getCert()), Retriever.getTestType(BpelNode4.getCert()));
		//BpelNode4.setCERTs(null);
		
		GraphBPELNode BpelNode5 = new GraphBPELNode();
		BpelNode5.setId("5");
		BpelNode5.setType(VirtualNodeType.OPERATION);
		BpelNode5.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode6 = new GraphBPELNode();
		BpelNode6.setId("6");
		BpelNode6.setType(VirtualNodeType.OPERATION);
		BpelNode6.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode7 = new GraphBPELNode();
		BpelNode7.setId("7");
		BpelNode7.setType(VirtualNodeType.CERT);
		BpelNode7.setConfPath("conf.json");
		BpelNode7.loadCertificates(matchMaker, ontology, asserts);
		BpelNode7.setConn(conn);
		//BpelNode7.insertTestCategory(Retriever.getTestCategory(BpelNode7.getCert()), Retriever.getTestType(BpelNode7.getCert()));
		//BpelNode7.setCERTs(null);
		
		GraphBPELNode BpelNode8 = new GraphBPELNode();
		BpelNode8.setId("8");
		BpelNode8.setType(VirtualNodeType.CERT);
		BpelNode8.setConfPath("conf.json");
		BpelNode8.loadCertificates(matchMaker, ontology, asserts);
		BpelNode8.setConn(conn);
		//BpelNode8.insertTestCategory(Retriever.getTestCategory(BpelNode8.getCert()), Retriever.getTestType(BpelNode8.getCert()));
		//BpelNode8.setCERTs(null);
		
		GraphBPELNode BpelNode9 = new GraphBPELNode();
		BpelNode9.setId("9");
		BpelNode9.setType(VirtualNodeType.CERT);
		BpelNode9.setConfPath("conf.json");
		BpelNode9.loadCertificates(matchMaker, ontology, asserts);
		BpelNode9.setConn(conn);
		//BpelNode9.setCERTs(null);
		
		GraphBPELNode BpelNode10 = new GraphBPELNode();
		BpelNode10.setId("10");
		BpelNode10.setType(VirtualNodeType.CERT);
		BpelNode10.setConfPath("conf.json");
		BpelNode10.loadCertificates(matchMaker, ontology, asserts);
		BpelNode10.setConn(conn);
		//BpelNode10.insertTestCategory(Retriever.getTestCategory(BpelNode10.getCert()), Retriever.getTestType(BpelNode10.getCert()));
		//BpelNode10.setCERTs(null);
		
		GraphBPELNode BpelNode11 = new GraphBPELNode();
		BpelNode11.setId("11");
		BpelNode11.setType(VirtualNodeType.CERT);
		BpelNode11.setConfPath("conf.json");
		BpelNode11.loadCertificates(matchMaker, ontology, asserts);
		BpelNode11.setConn(conn);
		//BpelNode11.insertTestCategory(Retriever.getTestCategory(BpelNode11.getCert()), Retriever.getTestType(BpelNode11.getCert()));
		//BpelNode11.setCERTs(null);
		
		GraphBPELNode BpelNode12 = new GraphBPELNode();
		BpelNode12.setId("12");
		BpelNode12.setType(VirtualNodeType.CERT);
		BpelNode12.setConfPath("conf.json");
		BpelNode12.loadCertificates(matchMaker, ontology, asserts);
		BpelNode12.setConn(conn);
		//BpelNode12.insertTestCategory(Retriever.getTestCategory(BpelNode12.getCert()), Retriever.getTestType(BpelNode12.getCert()));
		//BpelNode12.setCERTs(null);
		
		GraphBPELNode BpelNode13 = new GraphBPELNode();
		BpelNode13.setId("13");
		BpelNode13.setType(VirtualNodeType.OPERATION);
		BpelNode13.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode14 = new GraphBPELNode();
		BpelNode14.setId("14");
		BpelNode14.setType(VirtualNodeType.CERT);
		BpelNode14.setConfPath("conf.json");
		BpelNode14.loadCertificates(matchMaker, ontology, asserts);
		BpelNode14.setConn(conn);
		//BpelNode14.insertTestCategory(Retriever.getTestCategory(BpelNode14.getCert()), Retriever.getTestType(BpelNode14.getCert()));
		//BpelNode14.setCERTs(null);
		
		GraphBPELNode BpelNode15 = new GraphBPELNode();
		BpelNode15.setId("15");
		BpelNode15.setType(VirtualNodeType.CERT);
		BpelNode15.setConfPath("conf.json");
		BpelNode15.loadCertificates(matchMaker, ontology, asserts);
		BpelNode15.setConn(conn);
		
		GraphBPELNode BpelNode16 = new GraphBPELNode();
		BpelNode16.setId("16");
		BpelNode16.setType(VirtualNodeType.CERT);
		BpelNode16.setConfPath("conf.json");
		BpelNode16.loadCertificates(matchMaker, ontology, asserts);
		BpelNode16.setConn(conn);
		
		GraphBPELNode BpelNode17 = new GraphBPELNode();
		BpelNode17.setId("17");
		BpelNode17.setType(VirtualNodeType.CERT);
		BpelNode17.setConfPath("conf.json");
		BpelNode17.loadCertificates(matchMaker, ontology, asserts);
		BpelNode17.setConn(conn);
		
		GraphBPELNode BpelNode18 = new GraphBPELNode();
		BpelNode18.setId("18");
		BpelNode18.setType(VirtualNodeType.CERT);
		BpelNode18.setConfPath("conf.json");
		BpelNode18.loadCertificates(matchMaker, ontology, asserts);
		BpelNode18.setConn(conn);
		
		GraphBPELNode BpelNode19 = new GraphBPELNode();
		BpelNode19.setId("19");
		BpelNode19.setType(VirtualNodeType.CERT);
		BpelNode19.setConfPath("conf.json");
		BpelNode19.loadCertificates(matchMaker, ontology, asserts);
		BpelNode19.setConn(conn);
		
		
		Object o1 = bpel.insertVertex(parent, null, BpelNode1, 0, 0, 0, 0);
		Object o2 = bpel.insertVertex(parent, null, BpelNode2, 0, 0, 0, 0);
		Object o3 = bpel.insertVertex(parent, null, BpelNode3, 0, 0, 0, 0);
		Object o4 = bpel.insertVertex(parent, null, BpelNode4, 0, 0, 0, 0);
		Object o5 = bpel.insertVertex(parent, null, BpelNode5, 0, 0, 0, 0);
		Object o6 = bpel.insertVertex(parent, null, BpelNode6, 0, 0, 0, 0);
		Object o7 = bpel.insertVertex(parent, null, BpelNode7, 0, 0, 0, 0);
		Object o8 = bpel.insertVertex(parent, null, BpelNode8, 0, 0, 0, 0);
		Object o9 = bpel.insertVertex(parent, null, BpelNode9, 0, 0, 0, 0);
		Object o10 = bpel.insertVertex(parent, null, BpelNode10, 0, 0, 0, 0);
		Object o11 = bpel.insertVertex(parent, null, BpelNode11, 0, 0, 0, 0);
		Object o12 = bpel.insertVertex(parent, null, BpelNode12, 0, 0, 0, 0);
		Object o13 = bpel.insertVertex(parent, null, BpelNode13, 0, 0, 0, 0);
		Object o14 = bpel.insertVertex(parent, null, BpelNode14, 0, 0, 0, 0);
		Object o15 = bpel.insertVertex(parent, null, BpelNode15, 0, 0, 0, 0);
		Object o16 = bpel.insertVertex(parent, null, BpelNode16, 0, 0, 0, 0);
		Object o17 = bpel.insertVertex(parent, null, BpelNode17, 0, 0, 0, 0);
		Object o18 = bpel.insertVertex(parent, null, BpelNode17, 0, 0, 0, 0);
		Object o19 = bpel.insertVertex(parent, null, BpelNode17, 0, 0, 0, 0);
		
		bpel.insertEdge(parent, null, null, o1, o2);
		bpel.insertEdge(parent, null, null, o1, o3);
		bpel.insertEdge(parent, null, null, o1, o4);
		bpel.insertEdge(parent, null, null, o1, o17);
		bpel.insertEdge(parent, null, null, o17, o5);
		bpel.insertEdge(parent, null, null, o2, o5);
		bpel.insertEdge(parent, null, null, o3, o5);
		bpel.insertEdge(parent, null, null, o4, o5);
		bpel.insertEdge(parent, null, null, o5, o6);
		bpel.insertEdge(parent, null, null, o6, o7);
		bpel.insertEdge(parent, null, null, o6, o8);
		bpel.insertEdge(parent, null, null, o6, o9);
		bpel.insertEdge(parent, null, null, o6, o18);
		bpel.insertEdge(parent, null, null, o18, o19);
		bpel.insertEdge(parent, null, null, o7, o10);
		bpel.insertEdge(parent, null, null, o8, o11);
		bpel.insertEdge(parent, null, null, o9, o12);
		bpel.insertEdge(parent, null, null, o10, o13);
		bpel.insertEdge(parent, null, null, o11, o13);
	    bpel.insertEdge(parent, null, null, o12, o13);
	    bpel.insertEdge(parent, null, null, o19, o13);
		bpel.insertEdge(parent, null, null, o13, o14);
		bpel.insertEdge(parent, null, null, o14, o15);
		bpel.insertEdge(parent, null, null, o15, o16);
		
		
		
		return bpel;
	}

	private static mxGraph loadGraph14(Set<ASSERT> asserts,
			BaseXOntologyManager ontology, DBConn conn,
			SlaveMatchMakerE matchMaker) {
		mxGraph bpel = new mxGraph();
		Object parent = bpel.getDefaultParent();
		mxGraphModel model = (mxGraphModel) bpel.getModel();
		
		GraphBPELNode BpelNode1 = new GraphBPELNode();
		BpelNode1.setId("1");
		BpelNode1.setType(VirtualNodeType.OPERATION);
		BpelNode1.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode2 = new GraphBPELNode();
		BpelNode2.setId("2");
		BpelNode2.setType(VirtualNodeType.CERT);
		BpelNode2.setConfPath("conf.json");
		BpelNode2.loadCertificates(matchMaker, ontology, asserts);
		BpelNode2.setConn(conn);
		//BpelNode2.insertTestCategory(Retriever.getTestCategory(BpelNode2.getCert()), Retriever.getTestType(BpelNode2.getCert()));
		//BpelNode2.setCERTs(null);
		
		GraphBPELNode BpelNode3 = new GraphBPELNode();
		BpelNode3.setId("3");
		BpelNode3.setType(VirtualNodeType.CERT);
		BpelNode3.setConfPath("conf.json");
		BpelNode3.loadCertificates(matchMaker, ontology, asserts);
		BpelNode3.setConn(conn);
		//BpelNode3.insertTestCategory(Retriever.getTestCategory(BpelNode3.getCert()), Retriever.getTestType(BpelNode3.getCert()));
		//BpelNode3.setCERTs(null);
		
		GraphBPELNode BpelNode4 = new GraphBPELNode();
		BpelNode4.setId("4");
		BpelNode4.setType(VirtualNodeType.CERT);
		BpelNode4.setConfPath("conf.json");
		BpelNode4.loadCertificates(matchMaker, ontology, asserts);
		BpelNode4.setConn(conn);
		//BpelNode4.insertTestCategory(Retriever.getTestCategory(BpelNode4.getCert()), Retriever.getTestType(BpelNode4.getCert()));
		//BpelNode4.setCERTs(null);
		
		GraphBPELNode BpelNode5 = new GraphBPELNode();
		BpelNode5.setId("5");
		BpelNode5.setType(VirtualNodeType.OPERATION);
		BpelNode5.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode6 = new GraphBPELNode();
		BpelNode6.setId("6");
		BpelNode6.setType(VirtualNodeType.OPERATION);
		BpelNode6.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode7 = new GraphBPELNode();
		BpelNode7.setId("7");
		BpelNode7.setType(VirtualNodeType.CERT);
		BpelNode7.setConfPath("conf.json");
		BpelNode7.loadCertificates(matchMaker, ontology, asserts);
		BpelNode7.setConn(conn);
		//BpelNode7.insertTestCategory(Retriever.getTestCategory(BpelNode7.getCert()), Retriever.getTestType(BpelNode7.getCert()));
		//BpelNode7.setCERTs(null);
		
		GraphBPELNode BpelNode8 = new GraphBPELNode();
		BpelNode8.setId("8");
		BpelNode8.setType(VirtualNodeType.CERT);
		BpelNode8.setConfPath("conf.json");
		BpelNode8.loadCertificates(matchMaker, ontology, asserts);
		BpelNode8.setConn(conn);
		//BpelNode8.insertTestCategory(Retriever.getTestCategory(BpelNode8.getCert()), Retriever.getTestType(BpelNode8.getCert()));
		//BpelNode8.setCERTs(null);
		
		GraphBPELNode BpelNode9 = new GraphBPELNode();
		BpelNode9.setId("9");
		BpelNode9.setType(VirtualNodeType.CERT);
		BpelNode9.setConfPath("conf.json");
		BpelNode9.loadCertificates(matchMaker, ontology, asserts);
		BpelNode9.setConn(conn);
		//BpelNode9.setCERTs(null);
		
		GraphBPELNode BpelNode10 = new GraphBPELNode();
		BpelNode10.setId("10");
		BpelNode10.setType(VirtualNodeType.CERT);
		BpelNode10.setConfPath("conf.json");
		BpelNode10.loadCertificates(matchMaker, ontology, asserts);
		BpelNode10.setConn(conn);
		//BpelNode10.insertTestCategory(Retriever.getTestCategory(BpelNode10.getCert()), Retriever.getTestType(BpelNode10.getCert()));
		//BpelNode10.setCERTs(null);
		
		GraphBPELNode BpelNode11 = new GraphBPELNode();
		BpelNode11.setId("11");
		BpelNode11.setType(VirtualNodeType.CERT);
		BpelNode11.setConfPath("conf.json");
		BpelNode11.loadCertificates(matchMaker, ontology, asserts);
		BpelNode11.setConn(conn);
		//BpelNode11.insertTestCategory(Retriever.getTestCategory(BpelNode11.getCert()), Retriever.getTestType(BpelNode11.getCert()));
		//BpelNode11.setCERTs(null);
		
		GraphBPELNode BpelNode12 = new GraphBPELNode();
		BpelNode12.setId("12");
		BpelNode12.setType(VirtualNodeType.CERT);
		BpelNode12.setConfPath("conf.json");
		BpelNode12.loadCertificates(matchMaker, ontology, asserts);
		BpelNode12.setConn(conn);
		//BpelNode12.insertTestCategory(Retriever.getTestCategory(BpelNode12.getCert()), Retriever.getTestType(BpelNode12.getCert()));
		//BpelNode12.setCERTs(null);
		
		GraphBPELNode BpelNode13 = new GraphBPELNode();
		BpelNode13.setId("13");
		BpelNode13.setType(VirtualNodeType.OPERATION);
		BpelNode13.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode14 = new GraphBPELNode();
		BpelNode14.setId("14");
		BpelNode14.setType(VirtualNodeType.CERT);
		BpelNode14.setConfPath("conf.json");
		BpelNode14.loadCertificates(matchMaker, ontology, asserts);
		BpelNode14.setConn(conn);
		//BpelNode14.insertTestCategory(Retriever.getTestCategory(BpelNode14.getCert()), Retriever.getTestType(BpelNode14.getCert()));
		//BpelNode14.setCERTs(null);
		
		GraphBPELNode BpelNode15 = new GraphBPELNode();
		BpelNode15.setId("15");
		BpelNode15.setType(VirtualNodeType.CERT);
		BpelNode15.setConfPath("conf.json");
		BpelNode15.loadCertificates(matchMaker, ontology, asserts);
		BpelNode15.setConn(conn);
		
		GraphBPELNode BpelNode16 = new GraphBPELNode();
		BpelNode16.setId("16");
		BpelNode16.setType(VirtualNodeType.CERT);
		BpelNode16.setConfPath("conf.json");
		BpelNode16.loadCertificates(matchMaker, ontology, asserts);
		BpelNode16.setConn(conn);
		
		GraphBPELNode BpelNode17 = new GraphBPELNode();
		BpelNode17.setId("17");
		BpelNode17.setType(VirtualNodeType.CERT);
		BpelNode17.setConfPath("conf.json");
		BpelNode17.loadCertificates(matchMaker, ontology, asserts);
		BpelNode17.setConn(conn);
		
		
		Object o1 = bpel.insertVertex(parent, null, BpelNode1, 0, 0, 0, 0);
		Object o2 = bpel.insertVertex(parent, null, BpelNode2, 0, 0, 0, 0);
		Object o3 = bpel.insertVertex(parent, null, BpelNode3, 0, 0, 0, 0);
		Object o4 = bpel.insertVertex(parent, null, BpelNode4, 0, 0, 0, 0);
		Object o5 = bpel.insertVertex(parent, null, BpelNode5, 0, 0, 0, 0);
		Object o6 = bpel.insertVertex(parent, null, BpelNode6, 0, 0, 0, 0);
		Object o7 = bpel.insertVertex(parent, null, BpelNode7, 0, 0, 0, 0);
		Object o8 = bpel.insertVertex(parent, null, BpelNode8, 0, 0, 0, 0);
		Object o9 = bpel.insertVertex(parent, null, BpelNode9, 0, 0, 0, 0);
		Object o10 = bpel.insertVertex(parent, null, BpelNode10, 0, 0, 0, 0);
		Object o11 = bpel.insertVertex(parent, null, BpelNode11, 0, 0, 0, 0);
		Object o12 = bpel.insertVertex(parent, null, BpelNode12, 0, 0, 0, 0);
		Object o13 = bpel.insertVertex(parent, null, BpelNode13, 0, 0, 0, 0);
		Object o14 = bpel.insertVertex(parent, null, BpelNode14, 0, 0, 0, 0);
		Object o15 = bpel.insertVertex(parent, null, BpelNode15, 0, 0, 0, 0);
		Object o16 = bpel.insertVertex(parent, null, BpelNode16, 0, 0, 0, 0);
		Object o17 = bpel.insertVertex(parent, null, BpelNode17, 0, 0, 0, 0);
		
		bpel.insertEdge(parent, null, null, o1, o2);
		bpel.insertEdge(parent, null, null, o1, o3);
		bpel.insertEdge(parent, null, null, o1, o4);
		bpel.insertEdge(parent, null, null, o1, o17);
		bpel.insertEdge(parent, null, null, o17, o5);
		bpel.insertEdge(parent, null, null, o2, o5);
		bpel.insertEdge(parent, null, null, o3, o5);
		bpel.insertEdge(parent, null, null, o4, o5);
		bpel.insertEdge(parent, null, null, o5, o6);
		bpel.insertEdge(parent, null, null, o6, o7);
		bpel.insertEdge(parent, null, null, o6, o8);
		bpel.insertEdge(parent, null, null, o6, o9);
		bpel.insertEdge(parent, null, null, o7, o10);
		bpel.insertEdge(parent, null, null, o8, o11);
		bpel.insertEdge(parent, null, null, o9, o12);
		bpel.insertEdge(parent, null, null, o10, o13);
		bpel.insertEdge(parent, null, null, o11, o13);
	    bpel.insertEdge(parent, null, null, o12, o13);
		bpel.insertEdge(parent, null, null, o13, o14);
		bpel.insertEdge(parent, null, null, o14, o15);
		bpel.insertEdge(parent, null, null, o15, o16);
		
		
		
		return bpel;
	}

	private static mxGraph loadGraph12(Set<ASSERT> asserts,
			BaseXOntologyManager ontology, DBConn conn,
			SlaveMatchMakerE matchMaker) {
		mxGraph bpel = new mxGraph();
		Object parent = bpel.getDefaultParent();
		mxGraphModel model = (mxGraphModel) bpel.getModel();
		
		GraphBPELNode BpelNode1 = new GraphBPELNode();
		BpelNode1.setId("1");
		BpelNode1.setType(VirtualNodeType.OPERATION);
		BpelNode1.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode2 = new GraphBPELNode();
		BpelNode2.setId("2");
		BpelNode2.setType(VirtualNodeType.CERT);
		BpelNode2.setConfPath("conf.json");
		BpelNode2.loadCertificates(matchMaker, ontology, asserts);
		BpelNode2.setConn(conn);
		//BpelNode2.insertTestCategory(Retriever.getTestCategory(BpelNode2.getCert()), Retriever.getTestType(BpelNode2.getCert()));
		//BpelNode2.setCERTs(null);
		
		GraphBPELNode BpelNode3 = new GraphBPELNode();
		BpelNode3.setId("3");
		BpelNode3.setType(VirtualNodeType.CERT);
		BpelNode3.setConfPath("conf.json");
		BpelNode3.loadCertificates(matchMaker, ontology, asserts);
		BpelNode3.setConn(conn);
		//BpelNode3.insertTestCategory(Retriever.getTestCategory(BpelNode3.getCert()), Retriever.getTestType(BpelNode3.getCert()));
		//BpelNode3.setCERTs(null);
		
		GraphBPELNode BpelNode4 = new GraphBPELNode();
		BpelNode4.setId("4");
		BpelNode4.setType(VirtualNodeType.CERT);
		BpelNode4.setConfPath("conf.json");
		BpelNode4.loadCertificates(matchMaker, ontology, asserts);
		BpelNode4.setConn(conn);
		//BpelNode4.insertTestCategory(Retriever.getTestCategory(BpelNode4.getCert()), Retriever.getTestType(BpelNode4.getCert()));
		//BpelNode4.setCERTs(null);
		
		GraphBPELNode BpelNode5 = new GraphBPELNode();
		BpelNode5.setId("5");
		BpelNode5.setType(VirtualNodeType.OPERATION);
		BpelNode5.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode6 = new GraphBPELNode();
		BpelNode6.setId("6");
		BpelNode6.setType(VirtualNodeType.OPERATION);
		BpelNode6.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode7 = new GraphBPELNode();
		BpelNode7.setId("7");
		BpelNode7.setType(VirtualNodeType.CERT);
		BpelNode7.setConfPath("conf.json");
		BpelNode7.loadCertificates(matchMaker, ontology, asserts);
		BpelNode7.setConn(conn);
		//BpelNode7.insertTestCategory(Retriever.getTestCategory(BpelNode7.getCert()), Retriever.getTestType(BpelNode7.getCert()));
		//BpelNode7.setCERTs(null);
		
		GraphBPELNode BpelNode8 = new GraphBPELNode();
		BpelNode8.setId("8");
		BpelNode8.setType(VirtualNodeType.CERT);
		BpelNode8.setConfPath("conf.json");
		BpelNode8.loadCertificates(matchMaker, ontology, asserts);
		BpelNode8.setConn(conn);
		//BpelNode8.insertTestCategory(Retriever.getTestCategory(BpelNode8.getCert()), Retriever.getTestType(BpelNode8.getCert()));
		//BpelNode8.setCERTs(null);
		
		GraphBPELNode BpelNode9 = new GraphBPELNode();
		BpelNode9.setId("9");
		BpelNode9.setType(VirtualNodeType.CERT);
		BpelNode9.setConfPath("conf.json");
		BpelNode9.loadCertificates(matchMaker, ontology, asserts);
		BpelNode9.setConn(conn);
		//BpelNode9.setCERTs(null);
		
		GraphBPELNode BpelNode10 = new GraphBPELNode();
		BpelNode10.setId("10");
		BpelNode10.setType(VirtualNodeType.CERT);
		BpelNode10.setConfPath("conf.json");
		BpelNode10.loadCertificates(matchMaker, ontology, asserts);
		BpelNode10.setConn(conn);
		//BpelNode10.insertTestCategory(Retriever.getTestCategory(BpelNode10.getCert()), Retriever.getTestType(BpelNode10.getCert()));
		//BpelNode10.setCERTs(null);
		
		GraphBPELNode BpelNode11 = new GraphBPELNode();
		BpelNode11.setId("11");
		BpelNode11.setType(VirtualNodeType.CERT);
		BpelNode11.setConfPath("conf.json");
		BpelNode11.loadCertificates(matchMaker, ontology, asserts);
		BpelNode11.setConn(conn);
		//BpelNode11.insertTestCategory(Retriever.getTestCategory(BpelNode11.getCert()), Retriever.getTestType(BpelNode11.getCert()));
		//BpelNode11.setCERTs(null);
		
		GraphBPELNode BpelNode12 = new GraphBPELNode();
		BpelNode12.setId("12");
		BpelNode12.setType(VirtualNodeType.CERT);
		BpelNode12.setConfPath("conf.json");
		BpelNode12.loadCertificates(matchMaker, ontology, asserts);
		BpelNode12.setConn(conn);
		//BpelNode12.insertTestCategory(Retriever.getTestCategory(BpelNode12.getCert()), Retriever.getTestType(BpelNode12.getCert()));
		//BpelNode12.setCERTs(null);
		
		GraphBPELNode BpelNode13 = new GraphBPELNode();
		BpelNode13.setId("13");
		BpelNode13.setType(VirtualNodeType.OPERATION);
		BpelNode13.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode14 = new GraphBPELNode();
		BpelNode14.setId("14");
		BpelNode14.setType(VirtualNodeType.CERT);
		BpelNode14.setConfPath("conf.json");
		BpelNode14.loadCertificates(matchMaker, ontology, asserts);
		BpelNode14.setConn(conn);
		//BpelNode14.insertTestCategory(Retriever.getTestCategory(BpelNode14.getCert()), Retriever.getTestType(BpelNode14.getCert()));
		//BpelNode14.setCERTs(null);
		
		GraphBPELNode BpelNode15 = new GraphBPELNode();
		BpelNode15.setId("15");
		BpelNode15.setType(VirtualNodeType.CERT);
		BpelNode15.setConfPath("conf.json");
		BpelNode15.loadCertificates(matchMaker, ontology, asserts);
		BpelNode15.setConn(conn);
		
		GraphBPELNode BpelNode16 = new GraphBPELNode();
		BpelNode16.setId("16");
		BpelNode16.setType(VirtualNodeType.CERT);
		BpelNode16.setConfPath("conf.json");
		BpelNode16.loadCertificates(matchMaker, ontology, asserts);
		BpelNode16.setConn(conn);
		
		Object o1 = bpel.insertVertex(parent, null, BpelNode1, 0, 0, 0, 0);
		Object o2 = bpel.insertVertex(parent, null, BpelNode2, 0, 0, 0, 0);
		Object o3 = bpel.insertVertex(parent, null, BpelNode3, 0, 0, 0, 0);
		Object o4 = bpel.insertVertex(parent, null, BpelNode4, 0, 0, 0, 0);
		Object o5 = bpel.insertVertex(parent, null, BpelNode5, 0, 0, 0, 0);
		Object o6 = bpel.insertVertex(parent, null, BpelNode6, 0, 0, 0, 0);
		Object o7 = bpel.insertVertex(parent, null, BpelNode7, 0, 0, 0, 0);
		Object o8 = bpel.insertVertex(parent, null, BpelNode8, 0, 0, 0, 0);
		Object o9 = bpel.insertVertex(parent, null, BpelNode9, 0, 0, 0, 0);
		Object o10 = bpel.insertVertex(parent, null, BpelNode10, 0, 0, 0, 0);
		Object o11 = bpel.insertVertex(parent, null, BpelNode11, 0, 0, 0, 0);
		Object o12 = bpel.insertVertex(parent, null, BpelNode12, 0, 0, 0, 0);
		Object o13 = bpel.insertVertex(parent, null, BpelNode13, 0, 0, 0, 0);
		Object o14 = bpel.insertVertex(parent, null, BpelNode14, 0, 0, 0, 0);
		Object o15 = bpel.insertVertex(parent, null, BpelNode15, 0, 0, 0, 0);
		Object o16 = bpel.insertVertex(parent, null, BpelNode16, 0, 0, 0, 0);
		
		bpel.insertEdge(parent, null, null, o1, o2);
		bpel.insertEdge(parent, null, null, o1, o3);
		bpel.insertEdge(parent, null, null, o1, o4);
		bpel.insertEdge(parent, null, null, o1, o16);
		bpel.insertEdge(parent, null, null, o1, o15);
		bpel.insertEdge(parent, null, null, o16, o5);
		bpel.insertEdge(parent, null, null, o15, o5);
		bpel.insertEdge(parent, null, null, o2, o5);
		bpel.insertEdge(parent, null, null, o3, o5);
		bpel.insertEdge(parent, null, null, o4, o5);
		bpel.insertEdge(parent, null, null, o5, o6);
		bpel.insertEdge(parent, null, null, o6, o7);
		bpel.insertEdge(parent, null, null, o6, o8);
		bpel.insertEdge(parent, null, null, o6, o9);
		bpel.insertEdge(parent, null, null, o7, o10);
		bpel.insertEdge(parent, null, null, o8, o11);
		bpel.insertEdge(parent, null, null, o9, o12);
		bpel.insertEdge(parent, null, null, o10, o13);
		bpel.insertEdge(parent, null, null, o11, o13);
	    bpel.insertEdge(parent, null, null, o12, o13);
		bpel.insertEdge(parent, null, null, o13, o14);
		//bpel.insertEdge(parent, null, null, o14, o15);
		//bpel.insertEdge(parent, null, null, o15, o16);
		
		
		return bpel;
	}

	private static mxGraph loadGraph6(Set<ASSERT> asserts,
			BaseXOntologyManager ontology, DBConn conn,
			SlaveMatchMakerE matchMaker) {
		mxGraph bpel = new mxGraph();
		Object parent = bpel.getDefaultParent();
		mxGraphModel model = (mxGraphModel) bpel.getModel();
		
		GraphBPELNode BpelNode1 = new GraphBPELNode();
		BpelNode1.setId("1");
		BpelNode1.setType(VirtualNodeType.OPERATION);
		BpelNode1.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode2 = new GraphBPELNode();
		BpelNode2.setId("2");
		BpelNode2.setType(VirtualNodeType.CERT);
		BpelNode2.setConfPath("conf.json");
		BpelNode2.loadCertificates(matchMaker, ontology, asserts);
		BpelNode2.setConn(conn);
		//BpelNode2.insertTestCategory(Retriever.getTestCategory(BpelNode2.getCert()), Retriever.getTestType(BpelNode2.getCert()));
		//BpelNode2.setCERTs(null);
		
		GraphBPELNode BpelNode3 = new GraphBPELNode();
		BpelNode3.setId("3");
		BpelNode3.setType(VirtualNodeType.CERT);
		BpelNode3.setConfPath("conf.json");
		BpelNode3.loadCertificates(matchMaker, ontology, asserts);
		BpelNode3.setConn(conn);
		//BpelNode3.insertTestCategory(Retriever.getTestCategory(BpelNode3.getCert()), Retriever.getTestType(BpelNode3.getCert()));
		//BpelNode3.setCERTs(null);
		
		GraphBPELNode BpelNode4 = new GraphBPELNode();
		BpelNode4.setId("4");
		BpelNode4.setType(VirtualNodeType.CERT);
		BpelNode4.setConfPath("conf.json");
		BpelNode4.loadCertificates(matchMaker, ontology, asserts);
		BpelNode4.setConn(conn);
		//BpelNode4.insertTestCategory(Retriever.getTestCategory(BpelNode4.getCert()), Retriever.getTestType(BpelNode4.getCert()));
		//BpelNode4.setCERTs(null);
		
		GraphBPELNode BpelNode5 = new GraphBPELNode();
		BpelNode5.setId("5");
		BpelNode5.setType(VirtualNodeType.OPERATION);
		BpelNode5.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode6 = new GraphBPELNode();
		BpelNode6.setId("6");
		BpelNode6.setType(VirtualNodeType.OPERATION);
		BpelNode6.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode7 = new GraphBPELNode();
		BpelNode7.setId("7");
		BpelNode7.setType(VirtualNodeType.CERT);
		BpelNode7.setConfPath("conf.json");
		BpelNode7.loadCertificates(matchMaker, ontology, asserts);
		BpelNode7.setConn(conn);
		//BpelNode7.insertTestCategory(Retriever.getTestCategory(BpelNode7.getCert()), Retriever.getTestType(BpelNode7.getCert()));
		//BpelNode7.setCERTs(null);
		
		GraphBPELNode BpelNode9 = new GraphBPELNode();
		BpelNode9.setId("9");
		BpelNode9.setType(VirtualNodeType.CERT);
		BpelNode9.setConfPath("conf.json");
		BpelNode9.loadCertificates(matchMaker, ontology, asserts);
		BpelNode9.setConn(conn);
		//BpelNode9.setCERTs(null);
		
		GraphBPELNode BpelNode10 = new GraphBPELNode();
		BpelNode10.setId("10");
		BpelNode10.setType(VirtualNodeType.CERT);
		BpelNode10.setConfPath("conf.json");
		BpelNode10.loadCertificates(matchMaker, ontology, asserts);
		BpelNode10.setConn(conn);
		//BpelNode10.insertTestCategory(Retriever.getTestCategory(BpelNode10.getCert()), Retriever.getTestType(BpelNode10.getCert()));
		//BpelNode10.setCERTs(null);
		
		GraphBPELNode BpelNode12 = new GraphBPELNode();
		BpelNode12.setId("12");
		BpelNode12.setType(VirtualNodeType.CERT);
		BpelNode12.setConfPath("conf.json");
		BpelNode12.loadCertificates(matchMaker, ontology, asserts);
		BpelNode12.setConn(conn);
		
		GraphBPELNode BpelNode13 = new GraphBPELNode();
		BpelNode13.setId("13");
		BpelNode13.setType(VirtualNodeType.OPERATION);
		BpelNode13.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		
		Object o1 = bpel.insertVertex(parent, null, BpelNode1, 0, 0, 0, 0);
		Object o2 = bpel.insertVertex(parent, null, BpelNode2, 0, 0, 0, 0);
		Object o3 = bpel.insertVertex(parent, null, BpelNode3, 0, 0, 0, 0);
		//Object o4 = bpel.insertVertex(parent, null, BpelNode4, 0, 0, 0, 0);
		Object o5 = bpel.insertVertex(parent, null, BpelNode5, 0, 0, 0, 0);
		Object o6 = bpel.insertVertex(parent, null, BpelNode6, 0, 0, 0, 0);
		Object o7 = bpel.insertVertex(parent, null, BpelNode7, 0, 0, 0, 0);
		Object o9 = bpel.insertVertex(parent, null, BpelNode9, 0, 0, 0, 0);
		Object o10 = bpel.insertVertex(parent, null, BpelNode10, 0, 0, 0, 0);
		Object o12 = bpel.insertVertex(parent, null, BpelNode12, 0, 0, 0, 0);
		Object o13 = bpel.insertVertex(parent, null, BpelNode13, 0, 0, 0, 0);
		
		
		bpel.insertEdge(parent, null, null, o1, o2);
		bpel.insertEdge(parent, null, null, o1, o3);
		//bpel.insertEdge(parent, null, null, o1, o4);
		bpel.insertEdge(parent, null, null, o2, o5);
		bpel.insertEdge(parent, null, null, o3, o5);
		//bpel.insertEdge(parent, null, null, o4, o5);
		bpel.insertEdge(parent, null, null, o5, o6);
		bpel.insertEdge(parent, null, null, o6, o7);
		
		bpel.insertEdge(parent, null, null, o6, o9);
		bpel.insertEdge(parent, null, null, o7, o10);
		
		bpel.insertEdge(parent, null, null, o9, o12);
		bpel.insertEdge(parent, null, null, o12, o13);
		bpel.insertEdge(parent, null, null, o10, o13);
		
		return bpel;
	}

	private static mxGraph loadGraph8(Set<ASSERT> asserts,
			BaseXOntologyManager ontology, DBConn conn,
			SlaveMatchMakerE matchMaker) {
		// TODO Auto-generated method stub
		mxGraph bpel = new mxGraph();
		Object parent = bpel.getDefaultParent();
		mxGraphModel model = (mxGraphModel) bpel.getModel();
		
		GraphBPELNode BpelNode1 = new GraphBPELNode();
		BpelNode1.setId("1");
		BpelNode1.setType(VirtualNodeType.OPERATION);
		BpelNode1.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode2 = new GraphBPELNode();
		BpelNode2.setId("2");
		BpelNode2.setType(VirtualNodeType.CERT);
		BpelNode2.setConfPath("conf.json");
		BpelNode2.loadCertificates(matchMaker, ontology, asserts);
		BpelNode2.setConn(conn);
		//BpelNode2.insertTestCategory(Retriever.getTestCategory(BpelNode2.getCert()), Retriever.getTestType(BpelNode2.getCert()));
		//BpelNode2.setCERTs(null);
		
		GraphBPELNode BpelNode3 = new GraphBPELNode();
		BpelNode3.setId("3");
		BpelNode3.setType(VirtualNodeType.CERT);
		BpelNode3.setConfPath("conf.json");
		BpelNode3.loadCertificates(matchMaker, ontology, asserts);
		BpelNode3.setConn(conn);
		//BpelNode3.insertTestCategory(Retriever.getTestCategory(BpelNode3.getCert()), Retriever.getTestType(BpelNode3.getCert()));
		//BpelNode3.setCERTs(null);
		
		GraphBPELNode BpelNode4 = new GraphBPELNode();
		BpelNode4.setId("4");
		BpelNode4.setType(VirtualNodeType.CERT);
		BpelNode4.setConfPath("conf.json");
		BpelNode4.loadCertificates(matchMaker, ontology, asserts);
		BpelNode4.setConn(conn);
		//BpelNode4.insertTestCategory(Retriever.getTestCategory(BpelNode4.getCert()), Retriever.getTestType(BpelNode4.getCert()));
		//BpelNode4.setCERTs(null);
		
		GraphBPELNode BpelNode5 = new GraphBPELNode();
		BpelNode5.setId("5");
		BpelNode5.setType(VirtualNodeType.OPERATION);
		BpelNode5.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode6 = new GraphBPELNode();
		BpelNode6.setId("6");
		BpelNode6.setType(VirtualNodeType.OPERATION);
		BpelNode6.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode7 = new GraphBPELNode();
		BpelNode7.setId("7");
		BpelNode7.setType(VirtualNodeType.CERT);
		BpelNode7.setConfPath("conf.json");
		BpelNode7.loadCertificates(matchMaker, ontology, asserts);
		BpelNode7.setConn(conn);
		//BpelNode7.insertTestCategory(Retriever.getTestCategory(BpelNode7.getCert()), Retriever.getTestType(BpelNode7.getCert()));
		//BpelNode7.setCERTs(null);
		
		GraphBPELNode BpelNode9 = new GraphBPELNode();
		BpelNode9.setId("9");
		BpelNode9.setType(VirtualNodeType.CERT);
		BpelNode9.setConfPath("conf.json");
		BpelNode9.loadCertificates(matchMaker, ontology, asserts);
		BpelNode9.setConn(conn);
		//BpelNode9.setCERTs(null);
		
		GraphBPELNode BpelNode10 = new GraphBPELNode();
		BpelNode10.setId("10");
		BpelNode10.setType(VirtualNodeType.CERT);
		BpelNode10.setConfPath("conf.json");
		BpelNode10.loadCertificates(matchMaker, ontology, asserts);
		BpelNode10.setConn(conn);
		//BpelNode10.insertTestCategory(Retriever.getTestCategory(BpelNode10.getCert()), Retriever.getTestType(BpelNode10.getCert()));
		//BpelNode10.setCERTs(null);
		
		
		GraphBPELNode BpelNode12 = new GraphBPELNode();
		BpelNode12.setId("12");
		BpelNode12.setType(VirtualNodeType.CERT);
		BpelNode12.setConfPath("conf.json");
		BpelNode12.loadCertificates(matchMaker, ontology, asserts);
		BpelNode12.setConn(conn);
		//BpelNode12.insertTestCategory(Retriever.getTestCategory(BpelNode12.getCert()), Retriever.getTestType(BpelNode12.getCert()));
		//BpelNode12.setCERTs(null);
		
		GraphBPELNode BpelNode13 = new GraphBPELNode();
		BpelNode13.setId("13");
		BpelNode13.setType(VirtualNodeType.OPERATION);
		BpelNode13.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode14 = new GraphBPELNode();
		BpelNode14.setId("14");
		BpelNode14.setType(VirtualNodeType.CERT);
		BpelNode14.setConfPath("conf.json");
		BpelNode14.loadCertificates(matchMaker, ontology, asserts);
		BpelNode14.setConn(conn);
		//BpelNode14.insertTestCategory(Retriever.getTestCategory(BpelNode14.getCert()), Retriever.getTestType(BpelNode14.getCert()));
		//BpelNode14.setCERTs(null);
		
		Object o1 = bpel.insertVertex(parent, null, BpelNode1, 0, 0, 0, 0);
		Object o2 = bpel.insertVertex(parent, null, BpelNode2, 0, 0, 0, 0);
		Object o3 = bpel.insertVertex(parent, null, BpelNode3, 0, 0, 0, 0);
		Object o4 = bpel.insertVertex(parent, null, BpelNode4, 0, 0, 0, 0);
		Object o5 = bpel.insertVertex(parent, null, BpelNode5, 0, 0, 0, 0);
		Object o6 = bpel.insertVertex(parent, null, BpelNode6, 0, 0, 0, 0);
		Object o7 = bpel.insertVertex(parent, null, BpelNode7, 0, 0, 0, 0);
		Object o9 = bpel.insertVertex(parent, null, BpelNode9, 0, 0, 0, 0);
		Object o10 = bpel.insertVertex(parent, null, BpelNode10, 0, 0, 0, 0);
		Object o12 = bpel.insertVertex(parent, null, BpelNode12, 0, 0, 0, 0);
		Object o13 = bpel.insertVertex(parent, null, BpelNode13, 0, 0, 0, 0);
		Object o14 = bpel.insertVertex(parent, null, BpelNode14, 0, 0, 0, 0);
		
		bpel.insertEdge(parent, null, null, o1, o2);
		bpel.insertEdge(parent, null, null, o1, o3);
		bpel.insertEdge(parent, null, null, o1, o4);
		bpel.insertEdge(parent, null, null, o2, o5);
		bpel.insertEdge(parent, null, null, o3, o5);
		bpel.insertEdge(parent, null, null, o4, o5);
		bpel.insertEdge(parent, null, null, o5, o6);
		bpel.insertEdge(parent, null, null, o6, o7);
		
		bpel.insertEdge(parent, null, null, o6, o9);
		bpel.insertEdge(parent, null, null, o7, o10);
		
		bpel.insertEdge(parent, null, null, o9, o12);
		bpel.insertEdge(parent, null, null, o10, o13);
		
	    bpel.insertEdge(parent, null, null, o12, o13);
		bpel.insertEdge(parent, null, null, o13, o14);
		
		return bpel;
	}

	private static mxGraph loadGraph10(Set<ASSERT> asserts,
			BaseXOntologyManager ontology, DBConn conn, SlaveMatchMakerE matchMaker) {
		
		mxGraph bpel = new mxGraph();
		Object parent = bpel.getDefaultParent();
		mxGraphModel model = (mxGraphModel) bpel.getModel();
		
		GraphBPELNode BpelNode1 = new GraphBPELNode();
		BpelNode1.setId("1");
		BpelNode1.setType(VirtualNodeType.OPERATION);
		BpelNode1.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode2 = new GraphBPELNode();
		BpelNode2.setId("2");
		BpelNode2.setType(VirtualNodeType.CERT);
		BpelNode2.setConfPath("conf.json");
		BpelNode2.loadCertificates(matchMaker, ontology, asserts);
		BpelNode2.setConn(conn);
		//BpelNode2.insertTestCategory(Retriever.getTestCategory(BpelNode2.getCert()), Retriever.getTestType(BpelNode2.getCert()));
		//BpelNode2.setCERTs(null);
		
		GraphBPELNode BpelNode3 = new GraphBPELNode();
		BpelNode3.setId("3");
		BpelNode3.setType(VirtualNodeType.CERT);
		BpelNode3.setConfPath("conf.json");
		BpelNode3.loadCertificates(matchMaker, ontology, asserts);
		BpelNode3.setConn(conn);
		//BpelNode3.insertTestCategory(Retriever.getTestCategory(BpelNode3.getCert()), Retriever.getTestType(BpelNode3.getCert()));
		//BpelNode3.setCERTs(null);
		
		GraphBPELNode BpelNode4 = new GraphBPELNode();
		BpelNode4.setId("4");
		BpelNode4.setType(VirtualNodeType.CERT);
		BpelNode4.setConfPath("conf.json");
		BpelNode4.loadCertificates(matchMaker, ontology, asserts);
		BpelNode4.setConn(conn);
		//BpelNode4.insertTestCategory(Retriever.getTestCategory(BpelNode4.getCert()), Retriever.getTestType(BpelNode4.getCert()));
		//BpelNode4.setCERTs(null);
		
		GraphBPELNode BpelNode5 = new GraphBPELNode();
		BpelNode5.setId("5");
		BpelNode5.setType(VirtualNodeType.OPERATION);
		BpelNode5.setOperation(VirtualNodeOperation.PARALLEL);
		
		GraphBPELNode BpelNode6 = new GraphBPELNode();
		BpelNode6.setId("6");
		BpelNode6.setType(VirtualNodeType.OPERATION);
		BpelNode6.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode7 = new GraphBPELNode();
		BpelNode7.setId("7");
		BpelNode7.setType(VirtualNodeType.CERT);
		BpelNode7.setConfPath("conf.json");
		BpelNode7.loadCertificates(matchMaker, ontology, asserts);
		BpelNode7.setConn(conn);
		//BpelNode7.insertTestCategory(Retriever.getTestCategory(BpelNode7.getCert()), Retriever.getTestType(BpelNode7.getCert()));
		//BpelNode7.setCERTs(null);
		
		GraphBPELNode BpelNode8 = new GraphBPELNode();
		BpelNode8.setId("8");
		BpelNode8.setType(VirtualNodeType.CERT);
		BpelNode8.setConfPath("conf.json");
		BpelNode8.loadCertificates(matchMaker, ontology, asserts);
		BpelNode8.setConn(conn);
		//BpelNode8.insertTestCategory(Retriever.getTestCategory(BpelNode8.getCert()), Retriever.getTestType(BpelNode8.getCert()));
		//BpelNode8.setCERTs(null);
		
		GraphBPELNode BpelNode9 = new GraphBPELNode();
		BpelNode9.setId("9");
		BpelNode9.setType(VirtualNodeType.CERT);
		BpelNode9.setConfPath("conf.json");
		BpelNode9.loadCertificates(matchMaker, ontology, asserts);
		BpelNode9.setConn(conn);
		//BpelNode9.setCERTs(null);
		
		GraphBPELNode BpelNode10 = new GraphBPELNode();
		BpelNode10.setId("10");
		BpelNode10.setType(VirtualNodeType.CERT);
		BpelNode10.setConfPath("conf.json");
		BpelNode10.loadCertificates(matchMaker, ontology, asserts);
		BpelNode10.setConn(conn);
		//BpelNode10.insertTestCategory(Retriever.getTestCategory(BpelNode10.getCert()), Retriever.getTestType(BpelNode10.getCert()));
		//BpelNode10.setCERTs(null);
		
		GraphBPELNode BpelNode11 = new GraphBPELNode();
		BpelNode11.setId("11");
		BpelNode11.setType(VirtualNodeType.CERT);
		BpelNode11.setConfPath("conf.json");
		BpelNode11.loadCertificates(matchMaker, ontology, asserts);
		BpelNode11.setConn(conn);
		//BpelNode11.insertTestCategory(Retriever.getTestCategory(BpelNode11.getCert()), Retriever.getTestType(BpelNode11.getCert()));
		//BpelNode11.setCERTs(null);
		
		GraphBPELNode BpelNode12 = new GraphBPELNode();
		BpelNode12.setId("12");
		BpelNode12.setType(VirtualNodeType.CERT);
		BpelNode12.setConfPath("conf.json");
		BpelNode12.loadCertificates(matchMaker, ontology, asserts);
		BpelNode12.setConn(conn);
		//BpelNode12.insertTestCategory(Retriever.getTestCategory(BpelNode12.getCert()), Retriever.getTestType(BpelNode12.getCert()));
		//BpelNode12.setCERTs(null);
		
		GraphBPELNode BpelNode13 = new GraphBPELNode();
		BpelNode13.setId("13");
		BpelNode13.setType(VirtualNodeType.OPERATION);
		BpelNode13.setOperation(VirtualNodeOperation.ALTERNATIVE);
		
		GraphBPELNode BpelNode14 = new GraphBPELNode();
		BpelNode14.setId("14");
		BpelNode14.setType(VirtualNodeType.CERT);
		BpelNode14.setConfPath("conf.json");
		BpelNode14.loadCertificates(matchMaker, ontology, asserts);
		BpelNode14.setConn(conn);
		//BpelNode14.insertTestCategory(Retriever.getTestCategory(BpelNode14.getCert()), Retriever.getTestType(BpelNode14.getCert()));
		//BpelNode14.setCERTs(null);
		
		Object o1 = bpel.insertVertex(parent, null, BpelNode1, 0, 0, 0, 0);
		Object o2 = bpel.insertVertex(parent, null, BpelNode2, 0, 0, 0, 0);
		Object o3 = bpel.insertVertex(parent, null, BpelNode3, 0, 0, 0, 0);
		Object o4 = bpel.insertVertex(parent, null, BpelNode4, 0, 0, 0, 0);
		Object o5 = bpel.insertVertex(parent, null, BpelNode5, 0, 0, 0, 0);
		Object o6 = bpel.insertVertex(parent, null, BpelNode6, 0, 0, 0, 0);
		Object o7 = bpel.insertVertex(parent, null, BpelNode7, 0, 0, 0, 0);
		Object o8 = bpel.insertVertex(parent, null, BpelNode8, 0, 0, 0, 0);
		Object o9 = bpel.insertVertex(parent, null, BpelNode9, 0, 0, 0, 0);
		Object o10 = bpel.insertVertex(parent, null, BpelNode10, 0, 0, 0, 0);
		Object o11 = bpel.insertVertex(parent, null, BpelNode11, 0, 0, 0, 0);
		Object o12 = bpel.insertVertex(parent, null, BpelNode12, 0, 0, 0, 0);
		Object o13 = bpel.insertVertex(parent, null, BpelNode13, 0, 0, 0, 0);
		Object o14 = bpel.insertVertex(parent, null, BpelNode14, 0, 0, 0, 0);
		
		bpel.insertEdge(parent, null, null, o1, o2);
		bpel.insertEdge(parent, null, null, o1, o3);
		bpel.insertEdge(parent, null, null, o1, o4);
		bpel.insertEdge(parent, null, null, o2, o5);
		bpel.insertEdge(parent, null, null, o3, o5);
		bpel.insertEdge(parent, null, null, o4, o5);
		bpel.insertEdge(parent, null, null, o5, o6);
		bpel.insertEdge(parent, null, null, o6, o7);
		bpel.insertEdge(parent, null, null, o6, o8);
		bpel.insertEdge(parent, null, null, o6, o9);
		bpel.insertEdge(parent, null, null, o7, o10);
		bpel.insertEdge(parent, null, null, o8, o11);
		bpel.insertEdge(parent, null, null, o9, o12);
		bpel.insertEdge(parent, null, null, o10, o13);
		bpel.insertEdge(parent, null, null, o11, o13);
	    bpel.insertEdge(parent, null, null, o12, o13);
		bpel.insertEdge(parent, null, null, o13, o14);
		
		return bpel;
	}

//	private static void createGraphBPEL() {
//		BaseXOntologyManager ontology = new BaseXOntologyManager("localhost", 1984, "developer", "developer");
//		SlaveMatchMakerE matchMaker = new SlaveMatchMakerE(null, ontology );
//		
//		DBConn conn = createConn();
//		datamodelFactory dmf = datamodelFactory.eINSTANCE;
//		
//		ArrayList<String> results = new ArrayList<String>();
//		Set<ASSERT> asserts = new HashSet<ASSERT>();
//		
//		System.out.println("");
//		System.out.println("ASSERT Presenti nel DB");
//		System.out.println("");
//		
//		results = ontology.getAllAssert();
//		if (results != null){
//			System.out.println("Numero di assert presenti nel db: " + results.size());
//			
//			for(String result : results) {
//				asserts.add(dmf.createASSERT(result));
//			}
//		}
//		
//		System.out.println("Finita creazione ASSERT");
//		
//		mxGraph bpel = new mxGraph();
//		Object parent = bpel.getDefaultParent();
//		mxGraphModel model = (mxGraphModel) bpel.getModel();
//		
//		GraphBPELNode BpelNode1 = new GraphBPELNode();
//		BpelNode1.setId("1");
//		BpelNode1.setType(VirtualNodeType.OPERATION);
//		BpelNode1.setOperation(VirtualNodeOperation.PARALLEL);
//		
//		GraphBPELNode BpelNode2 = new GraphBPELNode();
//		BpelNode2.setId("2");
//		BpelNode2.setType(VirtualNodeType.CERT);
//		BpelNode2.setConfPath("conf.json");
//		BpelNode2.loadCertificates(matchMaker, ontology, asserts);
//		BpelNode2.setConn(conn);
//		BpelNode2.insertTestCategory(Retriever.getTestCategory(BpelNode2.getCert()), Retriever.getTestType(BpelNode2.getCert()));
//		//BpelNode2.setCERTs(null);
//		
//		GraphBPELNode BpelNode3 = new GraphBPELNode();
//		BpelNode3.setId("3");
//		BpelNode3.setType(VirtualNodeType.CERT);
//		BpelNode3.setConfPath("conf.json");
//		BpelNode3.loadCertificates(matchMaker, ontology, asserts);
//		BpelNode3.setConn(conn);
//		BpelNode3.insertTestCategory(Retriever.getTestCategory(BpelNode3.getCert()), Retriever.getTestType(BpelNode3.getCert()));
//		//BpelNode3.setCERTs(null);
//		
//		GraphBPELNode BpelNode4 = new GraphBPELNode();
//		BpelNode4.setId("4");
//		BpelNode4.setType(VirtualNodeType.CERT);
//		BpelNode4.setConfPath("conf.json");
//		BpelNode4.loadCertificates(matchMaker, ontology, asserts);
//		BpelNode4.setConn(conn);
//		BpelNode4.insertTestCategory(Retriever.getTestCategory(BpelNode4.getCert()), Retriever.getTestType(BpelNode4.getCert()));
//		//BpelNode4.setCERTs(null);
//		
//		GraphBPELNode BpelNode5 = new GraphBPELNode();
//		BpelNode5.setId("5");
//		BpelNode5.setType(VirtualNodeType.OPERATION);
//		BpelNode5.setOperation(VirtualNodeOperation.PARALLEL);
//		
//		GraphBPELNode BpelNode6 = new GraphBPELNode();
//		BpelNode6.setId("6");
//		BpelNode6.setType(VirtualNodeType.OPERATION);
//		BpelNode6.setOperation(VirtualNodeOperation.ALTERNATIVE);
//		
//		GraphBPELNode BpelNode7 = new GraphBPELNode();
//		BpelNode7.setId("7");
//		BpelNode7.setType(VirtualNodeType.CERT);
//		BpelNode7.setConfPath("conf.json");
//		BpelNode7.loadCertificates(matchMaker, ontology, asserts);
//		BpelNode7.setConn(conn);
//		BpelNode7.insertTestCategory(Retriever.getTestCategory(BpelNode7.getCert()), Retriever.getTestType(BpelNode7.getCert()));
//		//BpelNode7.setCERTs(null);
//		
//		GraphBPELNode BpelNode8 = new GraphBPELNode();
//		BpelNode8.setId("8");
//		BpelNode8.setType(VirtualNodeType.CERT);
//		BpelNode8.setConfPath("conf.json");
//		BpelNode8.loadCertificates(matchMaker, ontology, asserts);
//		BpelNode8.setConn(conn);
//		BpelNode8.insertTestCategory(Retriever.getTestCategory(BpelNode8.getCert()), Retriever.getTestType(BpelNode8.getCert()));
//		//BpelNode8.setCERTs(null);
//		
//		GraphBPELNode BpelNode9 = new GraphBPELNode();
//		BpelNode9.setId("9");
//		BpelNode9.setType(VirtualNodeType.CERT);
//		BpelNode9.setConfPath("conf.json");
//		BpelNode9.loadCertificates(matchMaker, ontology, asserts);
//		BpelNode9.setConn(conn);
//		//BpelNode9.setCERTs(null);
//		
//		GraphBPELNode BpelNode10 = new GraphBPELNode();
//		BpelNode10.setId("10");
//		BpelNode10.setType(VirtualNodeType.CERT);
//		BpelNode10.setConfPath("conf.json");
//		BpelNode10.loadCertificates(matchMaker, ontology, asserts);
//		BpelNode10.setConn(conn);
//		BpelNode10.insertTestCategory(Retriever.getTestCategory(BpelNode10.getCert()), Retriever.getTestType(BpelNode10.getCert()));
//		//BpelNode10.setCERTs(null);
//		
//		GraphBPELNode BpelNode11 = new GraphBPELNode();
//		BpelNode11.setId("11");
//		BpelNode11.setType(VirtualNodeType.CERT);
//		BpelNode11.setConfPath("conf.json");
//		BpelNode11.loadCertificates(matchMaker, ontology, asserts);
//		BpelNode11.setConn(conn);
//		BpelNode11.insertTestCategory(Retriever.getTestCategory(BpelNode11.getCert()), Retriever.getTestType(BpelNode11.getCert()));
//		//BpelNode11.setCERTs(null);
//		
//		GraphBPELNode BpelNode12 = new GraphBPELNode();
//		BpelNode12.setId("12");
//		BpelNode12.setType(VirtualNodeType.CERT);
//		BpelNode12.setConfPath("conf.json");
//		BpelNode12.loadCertificates(matchMaker, ontology, asserts);
//		BpelNode12.setConn(conn);
//		BpelNode12.insertTestCategory(Retriever.getTestCategory(BpelNode12.getCert()), Retriever.getTestType(BpelNode12.getCert()));
//		//BpelNode12.setCERTs(null);
//		
//		GraphBPELNode BpelNode13 = new GraphBPELNode();
//		BpelNode13.setId("13");
//		BpelNode13.setType(VirtualNodeType.OPERATION);
//		BpelNode13.setOperation(VirtualNodeOperation.ALTERNATIVE);
//		
//		GraphBPELNode BpelNode14 = new GraphBPELNode();
//		BpelNode14.setId("14");
//		BpelNode14.setType(VirtualNodeType.CERT);
//		BpelNode14.setConfPath("conf.json");
//		BpelNode14.loadCertificates(matchMaker, ontology, asserts);
//		BpelNode14.setConn(conn);
//		BpelNode14.insertTestCategory(Retriever.getTestCategory(BpelNode14.getCert()), Retriever.getTestType(BpelNode14.getCert()));
//		//BpelNode14.setCERTs(null);
//		
//		Object o1 = bpel.insertVertex(parent, null, BpelNode1, 0, 0, 0, 0);
//		Object o2 = bpel.insertVertex(parent, null, BpelNode2, 0, 0, 0, 0);
//		Object o3 = bpel.insertVertex(parent, null, BpelNode3, 0, 0, 0, 0);
//		Object o4 = bpel.insertVertex(parent, null, BpelNode4, 0, 0, 0, 0);
//		Object o5 = bpel.insertVertex(parent, null, BpelNode5, 0, 0, 0, 0);
//		Object o6 = bpel.insertVertex(parent, null, BpelNode6, 0, 0, 0, 0);
//		Object o7 = bpel.insertVertex(parent, null, BpelNode7, 0, 0, 0, 0);
//		Object o8 = bpel.insertVertex(parent, null, BpelNode8, 0, 0, 0, 0);
//		Object o9 = bpel.insertVertex(parent, null, BpelNode9, 0, 0, 0, 0);
//		Object o10 = bpel.insertVertex(parent, null, BpelNode10, 0, 0, 0, 0);
//		Object o11 = bpel.insertVertex(parent, null, BpelNode11, 0, 0, 0, 0);
//		Object o12 = bpel.insertVertex(parent, null, BpelNode12, 0, 0, 0, 0);
//		Object o13 = bpel.insertVertex(parent, null, BpelNode13, 0, 0, 0, 0);
//		Object o14 = bpel.insertVertex(parent, null, BpelNode14, 0, 0, 0, 0);
//		
//		Object e1 = bpel.insertEdge(parent, null, null, o1, o2);
//		Object e2 = bpel.insertEdge(parent, null, null, o1, o3);
//		Object e3 = bpel.insertEdge(parent, null, null, o1, o4);
//		Object e4 = bpel.insertEdge(parent, null, null, o2, o5);
//		Object e5 = bpel.insertEdge(parent, null, null, o3, o5);
//		Object e6 = bpel.insertEdge(parent, null, null, o4, o5);
//		Object e7 = bpel.insertEdge(parent, null, null, o5, o6);
//		Object e8 = bpel.insertEdge(parent, null, null, o6, o7);
//		Object e9 = bpel.insertEdge(parent, null, null, o6, o8);
//		Object e10 = bpel.insertEdge(parent, null, null, o6, o9);
//		Object e11 = bpel.insertEdge(parent, null, null, o7, o10);
//		Object e12 = bpel.insertEdge(parent, null, null, o8, o11);
//		Object e13 = bpel.insertEdge(parent, null, null, o9, o12);
//		Object e14 = bpel.insertEdge(parent, null, null, o10, o13);
//		Object e15 = bpel.insertEdge(parent, null, null, o11, o13);
//		Object e16 = bpel.insertEdge(parent, null, null, o12, o13);
//		Object e17 = bpel.insertEdge(parent, null, null, o13, o14);
//		
//		
//		
////		mxCodec codec = new mxCodec();   
////		String xml = mxUtils.getXml(codec.encode(bpel.getModel())); 
////		System.out.println(xml);
//		//Object[] obj = bpel.getChildVertices(bpel.getDefaultParent());
//		
//		//mxCell cell = getSequence(bpel);
//		
//		
//		
//		
//		bpel = createVirtualCertificate(bpel, ontology);
//		
//		
//		//Multimap<String,String> newNode = GraphBPELNode.mergeTest(BpelNode2, VirtualNodeOperation.SEQUENCE, BpelNode3, ontology);
//		
//		// TODO: METODO PER COPIARE TUTTE LE CELLE DI UN GRAFO IN UN ALTRO
//		mxGraph graph2 = new mxGraph();
//		graph2.addCells(bpel.cloneCells(bpel.getChildCells(bpel.getDefaultParent())));
//		
//		//bpel.getModel().remove(o3);
//		System.out.println();
//		
//		conn.closeConn();
//		
//	}
	
	private static mxGraph createVirtualCertificate(mxGraph bpel, IOntologyQuestioner ontology) {
		mxGraphModel model = (mxGraphModel) bpel.getModel();
		Map<String, Object> map = new HashMap<String, Object>();
		map = model.getCells();
		Object[] vertex = bpel.getChildVertices(bpel.getDefaultParent());
		while(vertex.length > 1){
			bpel = eliminateSeq(bpel, ontology);
			bpel = eliminatePar(bpel, ontology);
			
			vertex = bpel.getChildVertices(bpel.getDefaultParent());
			//System.out.println("HI");
		}
		return bpel;
	}

	private static mxGraph eliminatePar(mxGraph graph, IOntologyQuestioner ontology) {
		mxCell par = getParoAlt(graph);
		if(par != null){
			graph = mergeParAlt(par, graph, ontology);
			graph = eliminatePar(graph, ontology);
		} else {
			return graph;
		}
		return graph;
	}

	private static mxGraph mergeParAlt(mxCell par, mxGraph graph,
			IOntologyQuestioner ontology) {
		GraphBPELNode parent = (GraphBPELNode) par.getValue();
		ArrayList<mxCell> children = Util.getChildren((mxGraphModel) graph.getModel(), par);
		GraphBPELNode newNode = new GraphBPELNode();
		Multimap<String,String> test = null;
		
		String computedProperty = GraphBPELNode.mergeProperty((GraphBPELNode)children.get(0).getValue(), parent.getOperation(), (GraphBPELNode)children.get(1).getValue(), ontology);	
		mxGraph newModel = GraphBPELNode.mergeModel((GraphBPELNode)children.get(0).getValue(), parent.getOperation(), (GraphBPELNode)children.get(1).getValue());
		// TODO: passare l'ontologia
		test = GraphBPELNode.mergeTest((GraphBPELNode)children.get(0).getValue(), parent.getOperation(), (GraphBPELNode)children.get(1).getValue(), ontology);

		newNode.setModel(newModel);
		newNode.setsecProperty(computedProperty);
		newNode.setType(VirtualNodeType.CERT);
		newNode.setTestCategories(test);

		if(children.size() > 2){
			children.remove(0);
			children.remove(1);
			for(mxCell child : children){
				computedProperty = GraphBPELNode.mergeProperty(newNode, parent.getOperation(), (GraphBPELNode)child.getValue(), ontology);	
				newModel = GraphBPELNode.mergeModel(newNode, parent.getOperation(), (GraphBPELNode)child.getValue());
				// TODO: passare l'ontologia
				test = GraphBPELNode.mergeTest(newNode, parent.getOperation(), (GraphBPELNode)child.getValue(), ontology);

				newNode.setType(VirtualNodeType.CERT);
				newNode.setTestCategories(test);
			}
		} 
		
		graph = changeInPlace(par, newNode, graph, parent.getOperation());
		
		return graph;
	}

	private static mxCell getParoAlt(mxGraph graph) {
		Object[] vertex = graph.getChildVertices(graph.getDefaultParent());
		if(vertex != null && vertex.length > 0){
			mxCell cell = null;
			for(Object vert : vertex){
				cell = (mxCell)vert;
				GraphBPELNode node = ((GraphBPELNode)cell.getValue());
				if(node.getType().equals(VirtualNodeType.OPERATION)){
					Object[] edges = graph.getOutgoingEdges(vert);
					if(edges != null && edges.length > 1){
						for(Object edge : edges){
							mxCell edgeCell = (mxCell) edge;
							mxCell target = (mxCell) edgeCell.getTarget();
							GraphBPELNode edgeTarget = (GraphBPELNode) target.getValue();
							if(edgeTarget.getType().equals(VirtualNodeType.CERT)){
								ArrayList<mxCell> childrens = Util.getChildren((mxGraphModel) graph.getModel(), target);
								if(childrens != null && childrens.size() == 1){
									mxCell termNode = childrens.get(0);
									GraphBPELNode node2 = ((GraphBPELNode)cell.getValue());
									if(node2.getType().equals(VirtualNodeType.OPERATION)){
										return cell;
									}
								}
							}
						}
					}
				}
			}
		}
 		return null;
	}

	/**
	 * Return the edge in the sequence (with source and target)
	 * @param graph
	 * @return
	 */
	private static mxCell getSequence(mxGraph graph) {
		Object[] vertex = graph.getChildVertices(graph.getDefaultParent());
		if(vertex != null && vertex.length > 0){
			mxCell cell = null;
			for(Object vert : vertex){
				cell = (mxCell)vert;
				GraphBPELNode node = ((GraphBPELNode)cell.getValue());
				if(node.getType().equals(VirtualNodeType.CERT)){
					Object[] edges = graph.getOutgoingEdges(vert);
					if(edges != null && edges.length == 1){
						mxCell edge = (mxCell)edges[0];
						mxCell target = (mxCell) edge.getTarget();
						if(target.getValue() != null){
							GraphBPELNode edgeTarget = (GraphBPELNode) target.getValue();
							if(edgeTarget.getType().equals(VirtualNodeType.CERT)){
								return edge;
							}
						}
						
					}
				}
			}
		}
		return null;
		
	}

	private static mxGraph eliminateSeq(mxGraph graph, IOntologyQuestioner ontology) {
		mxCell edge = getSequence(graph);
		if(edge != null){
			graph = mergeSeq(edge, graph, ontology);
			graph = eliminateSeq(graph, ontology);
		} else {
			return graph;
		}
		return graph;
	}

	private static mxGraph mergeSeq(mxCell edge, mxGraph graph, IOntologyQuestioner ontology) {
		if(edge != null && graph != null){
			GraphBPELNode newNode = new GraphBPELNode();
			GraphBPELNode target = (GraphBPELNode)edge.getTarget().getValue();
			GraphBPELNode source = (GraphBPELNode)edge.getSource().getValue();
			
			String computedProperty = GraphBPELNode.mergeProperty(source, VirtualNodeOperation.SEQUENCE, target, ontology);	
			mxGraph newModel = GraphBPELNode.mergeModel(source, VirtualNodeOperation.SEQUENCE, target);
			// TODO: passare l'ontologia
			Multimap<String,String> test = GraphBPELNode.mergeTest(source, VirtualNodeOperation.SEQUENCE, target, ontology);
			
			//TODO: passare la conn
			newNode.setModel(newModel);
			newNode.setsecProperty(computedProperty);
			newNode.setType(VirtualNodeType.CERT);
			newNode.setTestCategories(test);
			graph = changeInPlace(edge, newNode, graph, VirtualNodeOperation.SEQUENCE);
			
			return graph;
		}
		return graph;
	}

	private static mxGraph changeInPlace(mxCell cell, GraphBPELNode newNode,
			mxGraph graph, VirtualNodeOperation sequence) {
		if(graph != null && cell != null && newNode != null){
			ArrayList<mxCell> parentList = new ArrayList<>();
			ArrayList<mxCell> childrenList = new ArrayList<>();
			
			switch(sequence){
			case SEQUENCE: default:
				//TODO: Inserire la gestione della sostituzione
				mxCell source = (mxCell) cell.getSource();
				Object[] parentEdges = mxGraphModel.getIncomingEdges(graph.getModel(), source);
				for(Object edge : parentEdges){
					mxCell c = (mxCell) edge;
					parentList.add((mxCell) c.getSource());
					
				}
				
				// Trovo il figlio
				mxCell target = (mxCell) cell.getTarget();
				Object[] children = mxGraphModel.getOutgoingEdges(graph.getModel(), target);
				for(Object child : children){
					mxCell childrens= (mxCell) ((mxCell)child).getTarget();
					childrenList.add(childrens);
				}
				
				//TODO: Devo eliminare anche i lati ?
				
				graph = Util.removeVertexWithEdge(graph, source);
				graph = Util.removeVertexWithEdge(graph, target);
				
				Object obj = graph.insertVertex(graph.getDefaultParent(), null, newNode, 0, 0, 0, 0);
				
				if(parentList != null){
					for(mxCell myCell : parentList){
						graph.insertEdge(graph.getDefaultParent(), null, null, myCell, obj);
					}
				}
				
				if(childrenList != null){
					for(mxCell myCell : childrenList){
						graph.insertEdge(graph.getDefaultParent(), null, null, obj, myCell);
					}	
				}
				return graph;
				
			case PARALLEL: case ALTERNATIVE:
				parentList = Util.getParent((mxGraphModel) graph.getModel(), cell);
				ArrayList<mxCell> childrens = Util.getChildren((mxGraphModel) graph.getModel(), cell);
				HashSet<mxCell> cellToEliminate = new HashSet<mxCell>(childrens);
				cellToEliminate.add(cell);
				if(childrens != null){
					ArrayList<mxCell> grandChilds = new ArrayList<mxCell>();
					for(mxCell child : childrens){
						grandChilds = Util.getChildren((mxGraphModel) graph.getModel(), child);
						cellToEliminate.addAll(grandChilds);
						for(mxCell grandChild : grandChilds){
							Object[] edges = mxGraphModel.getOutgoingEdges((mxGraphModel) graph.getModel(), grandChild);
							for(Object edge : edges){
								mxCell grand = (mxCell) ((mxCell)edge).getTarget();
								childrenList.add(grand);
							}
						}
					}
					
					for(mxCell cellToEl : cellToEliminate){
						graph = Util.removeVertexWithEdge(graph, cellToEl);
						//graph.getModel().remove(cellToEl);
					}
					
					Object objToInsert = graph.insertVertex(graph.getDefaultParent(), null, newNode, 0, 0, 0, 0);
					
					//Rimuovo i duplicati
					HashSet<mxCell> hs = new HashSet<mxCell>(parentList);
					parentList.clear();
					parentList.addAll(hs);
					
					hs.clear();
					hs.addAll(childrenList);
					childrenList.clear();
					childrenList.addAll(hs);
					
					if(parentList != null){
						for(mxCell myCell : parentList){
							graph.insertEdge(graph.getDefaultParent(), null, null, myCell, objToInsert);
						}
					}
					
					if(childrenList != null){
						for(mxCell myCell : childrenList){
							graph.insertEdge(graph.getDefaultParent(), null, null, objToInsert, myCell);
						}	
					}
					
				}
				return graph;
			}
		}
		return graph;
	}

	private static DBConn createConn(){
		Properties prop = new Properties();
    	DBConn dbConn = null;
		try {
    		prop.load(new FileInputStream("config.properties"));
    		
    		dbConn = new DBConn(null, prop.getProperty("database"), prop.getProperty("dbuser"), prop.getProperty("dbpassword"));
    		//String result = dbConn.checkRule("confidentiality_in_transit", "seq", "confidentiality_in_transit_at_rest");
    		//System.out.println(result);
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
		return dbConn;
	}

	private static void testMergeGraph() {
		Object o1;
		Object o2;
		Object o3;
		Object o0;
		
		Object o11;
		Object o21;
		Object o31;
		Object o01;
		
		mxGraph graph = new mxGraph();
		mxGraph graph2 = new mxGraph();
		
		Object parent = graph.getDefaultParent();
		Object parent2 = graph2.getDefaultParent();
		
		mxGraphModel model = (mxGraphModel) graph.getModel();
		mxGraphModel model2 = (mxGraphModel) graph2.getModel();
		
		model.beginUpdate();
		try{
//			o0 = new mxCell("*");
//			o1 = new mxCell("1");
//			o2 = new mxCell("2");
//			o3 = new mxCell("3");
//			model.add(o0, o1, 0);
//			model.add(o0, o2, 0);
//			model.add(o2, o3, 0);
			//model.setRoot(o0);
			
			
			o0 = graph.insertVertex(parent, "0", "*", 0, 0, 0, 0);
			model.setRoot(o0);
			o1 = graph.insertVertex(parent, "1", "1", 0, 0, 0, 0);
			o2 = graph.insertVertex(parent , "2", "2", 0, 0, 0, 0);
			o3 = graph.insertVertex(parent , "3", "3", 0, 0, 0, 0);
			Object e1 = graph.insertEdge(parent, null, null, o0, o1);
			Object e2 = graph.insertEdge(parent, null, null, o0, o2);
			Object e3 = graph.insertEdge(parent, null, null, o2, o3);
			
			//Ritorno il terminale
			Object test = ((mxCell)e1).getTerminal(false);
			Object[] edges = mxGraphModel.getOutgoingEdges(model, o2);
			
//			Object e1 = graph.insertEdge(parent, null, null, o0, o1);
//			Object e2 = graph.insertEdge(parent, null, null, o0, o2);
//			Object e3 = graph.insertEdge(parent, null, null, o2, o3);
			
			
		} finally {
			model.endUpdate();
		}
		
		model2.beginUpdate();
		try{
//			o01 = new mxCell("*");
//			o11 = new mxCell("4");
//			o21 = new mxCell("5");
//			o31 = new mxCell("6");
//			
//			model2.add(o01, o11, 0);
//			model2.add(o11, o21, 0);
//			model2.add(o21, o31, 0);
//			model2.setRoot(o01);
//			
			
			
			o01 = graph2.insertVertex(parent2 , "0", "*", 0, 0, 0, 0);
			model2.setRoot(o01);
			o11 = graph2.insertVertex(parent2 , "1", "4", 0, 0, 0, 0);
			o21 = graph2.insertVertex(parent2 , "2", "5", 0, 0, 0, 0);
			o31 = graph2.insertVertex(parent2 , "3", "6", 0, 0, 0, 0);
			Object e1 = graph2.insertEdge(parent2, null, null, o01, o11);
			Object e2 = graph2.insertEdge(parent2, null, null, o11, o21);
			Object e3 = graph2.insertEdge(parent2, null, null, o21, o31);
			
			
		}finally{
			model2.endUpdate();
		}
		
//		graph = Util.mergeGraph(graph, VirtualNodeOperation.SEQUENCE, graph2);
//		mxCell root = (mxCell) model.getRoot();
//		System.out.println(root.getValue());
//		Object[] edges = mxGraphModel.getOutgoingEdges(model, root);
//		ArrayList<mxCell> children = Util.getChildren(model, root);
//		printGraph(model, children);
		
		
//		graph = Util.mergeGraph(graph, VirtualNodeOperation.PARALLEL, graph2);
//		mxCell root = (mxCell) model.getRoot();
//		System.out.println(root.getValue());
//		Object[] edges = mxGraphModel.getOutgoingEdges(model, root);
//		ArrayList<mxCell> children = Util.getChildren(model, root);
//		printGraph(model, children);
		
		graph = Util.mergeGraph(graph, VirtualNodeOperation.ALTERNATIVE, graph2);
		mxCell root = (mxCell) model.getRoot();
		System.out.println(root.getValue());
		Object[] edges = mxGraphModel.getOutgoingEdges(model, root);
		ArrayList<mxCell> children = Util.getChildren(model, root);
		printGraph(model, children);
		
	}

	/**
	 * Stampa tutti i path del grafo
	 * @param model
	 * @param children
	 */
	private static void printGraph(mxGraphModel model, ArrayList<mxCell> children) {
		ArrayList<mxCell> parents = null;
		ArrayList<mxCell> grandChildes = null;
		for (Object child : children) {
			ArrayList<Object> childArr = new ArrayList<Object>();
			childArr.add(child);
			
			 //parents =  mxGraphModel.getParents(model, childArr.toArray());
			 //parents =  Util.getParent(model, child);
			
//			for (Object parent : parents) {
//				mxCell par = (mxCell) parent;
//				System.out.println("Padre: " + par.getValue() + " Figlio: "
//						+ ((mxCell) child).getValue());
//			}
			grandChildes = Util.getChildren(model, child);
			if(grandChildes.equals(null) || grandChildes.size() == 0){
				System.out.println("Foglia: " + ((mxCell)child).getValue());
			}
			
			for (Object grandChild : grandChildes) {
				mxCell par = (mxCell) grandChild;
				System.out.println("Padre: " + ((mxCell)child).getValue() + " Figlio: "
						+ ((mxCell) grandChild).getValue());
			} 
			printGraph(model, Util.getChildren(model, child));
		}
	}

	public static void testJSON(){
		EMatchMakerConfiguration config = new EMatchMakerConfiguration();
		config.setComparisonType(ComparisonType.ADVANCED);
		config.setSecurityPropertyComplete("confidentiality_intransit");
		EMatchMakerModel model = new EMatchMakerModel(ModelType.WSDL, ModelWeight.CONFIDENT, QualityLevel.HIGH);
		EMatchMakerTestEvidence evidence = new EMatchMakerTestEvidence("functionality", "input_partitioning.random_input", QualityLevel.LOW, EvidenceWeight.ATTACK, 100);
		config.setOrderComparison(OrderComparison.PF);
		config.setModel(model);
		config.setTestEvidence(evidence);
		
		
		
		ObjectMapper obj = new ObjectMapper();
		
		try {
			obj.writeValue(new File("conf.json"), config);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testCheckRule(){
		Properties prop = new Properties();
		 
    	try {
    		prop.load(new FileInputStream("config.properties"));
    		DBConn dbConn = new DBConn(null, prop.getProperty("database"), prop.getProperty("dbuser"), prop.getProperty("dbpasword"));
    		String result = dbConn.checkRule("confidentiality_intransit", RuleOperation.SEQ, "confidentiality_intransit_atrest");
    		System.out.println(result);
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
	}
	
	public static void testEnqueue(){
		datamodelFactory dmf = datamodelFactory.eINSTANCE;
		PartiallyOrderedSet<Set<String>> order = dmf.createPartialOrder();
		Set<String> set1 = new HashSet<String>();
		set1.add("Prova1");
		set1.add("Prova1b");
		set1.add("Prova1c");
		
		Set<String> set2 = new HashSet<String>();
		set2.add("Prova2");
		set2.add("Prova2b");
		set2.add("Prova2c");
		
		Set<String> set3 = new HashSet<String>();
		set3.add("Prova3");
		set3.add("Prova3b");
		set3.add("Prova3c");
		
		Set<String> set4 = new HashSet<String>();
		set4.add("Prova4");
		set4.add("Prova4b");
		set4.add("Prova4c");
		
		order.add(set1);
		order.add(set2);
		order.addRelation(set1, set3);
		order.addRelation(set3, set4);
		
		LinkedList<String> list = Util.enqueuePartialOrder(order);
		ListIterator<String> iter = list.listIterator();
		while(iter.hasNext()){
			String ele = iter.next();
			System.out.println(ele);
		}
	
	}

}
