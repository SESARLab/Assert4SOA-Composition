package eu.unimi.composition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mxgraph.view.mxGraph;

import eu.assert4soa.datamodel.ASSERT;
import eu.assert4soa.datamodel.PartiallyOrderedSet;
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

public class GraphBPELNode {
	private String id;
	private VirtualNodeType type;
	private VirtualNodeOperation operation;
	private LinkedList<ASSERT> substituteCERT;
	private String confPath;
	private DBConn conn;
	private String secProperty;
	private mxGraph model;
	private String modelType;
	/**
	 * @return the modelType
	 */
	public String getModelType() {
		return modelType;
	}

	/**
	 * @param modelType the modelType to set
	 */
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	private ArrayList<String> context = new ArrayList<String>();
	private Multimap<String,String> TestCategories;
	
	/**
	 * 
	 */
	public boolean insertTestCategory(String category, String evidence){
		if(this.TestCategories.put(category, evidence)){
			return true;
		}
		return false;
	}
	
	public Collection<String> getTestEvidenceFromKey(String testCategory){
		if(this.isTestCategoryInside(testCategory)){
			return this.TestCategories.get(testCategory);
		}
		return null;
	}
	
	public boolean isTestCategoryInside(String testCategory){
		if(this.TestCategories.containsKey(testCategory)){
			return true;
		}
		return false;
	}
	
	public int sizeTestCategory(){
		if(!TestCategories.equals(null)){
			return TestCategories.size();
		}
		return 0;
	}
	
	/**
	 * @return the context
	 */
	public ArrayList<String> getContext() {
		return context;
	}
	/**
	 * @param context the context to set
	 */
	public void setContext(ArrayList<String> context) {
		this.context = context;
	}
	/**
	 * @return the testCategories
	 */
	public Multimap<String, String> getTestCategories() {
		return TestCategories;
	}
	/**
	 * @param testCategories the testCategories to set
	 */
	public void setTestCategories(Multimap<String, String> testCategories) {
		TestCategories = testCategories;
	}
	/**
	 * @return the computedProperty
	 */
	public String getsecProperty() {
		return secProperty;
	}
	/**
	 * @param computedProperty the computedProperty to set
	 */
	public void setsecProperty(String computedProperty) {
		this.secProperty = computedProperty;
	}
	/**
	 * @return the model
	 */
	public mxGraph getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(mxGraph model) {
		this.model = model;
	}
	
	/**
	 * @return the conn
	 */
	public DBConn getConn() {
		return conn;
	}
	/**
	 * @param conn the conn to set
	 */
	public void setConn(DBConn conn) {
		this.conn = conn;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the type
	 */
	public VirtualNodeType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(VirtualNodeType type) {
		this.type = type;
	}
	/**
	 * @return the operation
	 */
	public VirtualNodeOperation getOperation() {
		return operation;
	}
	/**
	 * @param operation the operation to set
	 */
	public void setOperation(VirtualNodeOperation operation) {
		this.operation = operation;
	}
	/**
	 * @return the substituteCERT
	 */
	public LinkedList<ASSERT> getCERTs() {
		return substituteCERT;
	}
	/**
	 * @param substituteCERT the substituteCERT to set
	 */
	public void setCERTs(LinkedList<ASSERT> substituteCERT) {
		this.substituteCERT = substituteCERT;
	}
	/**
	 * @return the confPath
	 */
	public String getConfPath() {
		return confPath;
	}
	/**
	 * @param confPath the confPath to set
	 */
	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}
	
	/** 
	 * Return the first element of the list 
	 * @return the ASSERT of the node
	 */
	public ASSERT getCert(){
		return this.substituteCERT.peekFirst();
	}
	
	/**
	 * Change the certificate loaded by the 
	 * @return the new ASSERT of the node
	 */
	public ASSERT changeCert(){
		//Elimino il primo elemento tra i candidati
		this.substituteCERT.pollFirst();
		return this.substituteCERT.peekFirst();
	}
		
	/**
	 * Return true if the node is a CERT
	 * @return
	 */
	public boolean isCert(){
		if(type.equals(VirtualNodeType.CERT)){
			return true;
		}
		return false;
	}
		
	/**
	 * Load the certificate based on the JSON request stored in the file
	 */
	public void loadCertificates(SlaveMatchMakerE slave, BaseXOntologyManager ontology, Set<ASSERT> asserts){
		try {
			ObjectMapper obj = new ObjectMapper();
			EMatchMakerConfiguration conf = obj.readValue(new File(this.confPath), EMatchMakerConfiguration.class);
			slave.setConfig(conf);
			PartiallyOrderedSet<Set<ASSERT>> result = slave.getResults(conf, ontology, asserts);
			substituteCERT = Util.enqueuePartialOrder(result);
			this.insertTestCategory(Retriever.getTestCategory(this.getCert()), Retriever.getTestType(this.getCert()));
			//Carico anche il modello del grafo
			this.model = Util.loadFakeModel();
			this.modelType = Retriever.getModelType(this.getCert());
			this.secProperty = Retriever.getCompleteSecurityProperty(this.getCert());
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String retrieveSecProperty(GraphBPELNode node){
		String compSecPro = null;
		if(node.getsecProperty() != null){
			compSecPro = node.getsecProperty();
		} else {
			if(node.getCert() != null){
				compSecPro = Retriever.getCompleteSecurityProperty(node.getCert());
			} else {
				return compSecPro;
			}
		}
		return compSecPro;
	}
	
	
	public static mxGraph mergeModel(GraphBPELNode node, VirtualNodeOperation operation, GraphBPELNode otherNode){
		//GraphBPELNode newNode = new GraphBPELNode();
		//newNode.setModel(Util.mergeGraph(node.getModel(), operation, otherNode.getModel()));
		//return newNode;
		mxGraph newNode = new mxGraph();
		newNode = Util.mergeGraph(node.getModel(), operation, otherNode.getModel());
		return newNode;
	}
	
	public static Multimap<String,String> mergeTest(GraphBPELNode node, VirtualNodeOperation operation, GraphBPELNode otherNode, IOntologyQuestioner ontology){
		//TODO: Ricordarsi di gestire il caso in cui ci siano nella stessa categoria diverse
		Multimap<String,String> newNode = HashMultimap.create();;
		
		switch(operation){
		case ALTERNATIVE: case PARALLEL:
			newNode.putAll(otherNode.getTestCategories());
			newNode.putAll(node.getTestCategories());
			break;
		case SEQUENCE: default:
			if(node.sizeTestCategory() > 0 && otherNode.sizeTestCategory() > 0){
				String evidence = null;
				// Costruisco l'intersezione tra le chiavi delle categorie
				HashSet<String> intersect = new HashSet<String>(node.getTestCategories().keySet());
				intersect.retainAll(otherNode.getTestCategories().keySet());
				// Prendo i valori di tutte due i nodi e trovo il common ancestor
				for(String interKey : intersect){
					Collection<String> nodeEvidence = node.getTestEvidenceFromKey(interKey);
					Collection<String> otherNodeEvidence = node.getTestEvidenceFromKey(interKey);
					for(String nodeEv : nodeEvidence){
						for(String otherNodeEv : otherNodeEvidence){
							evidence = ontology.nearestCommonAncestor(nodeEv, otherNodeEv);
							// Trovo il common ancesto
							newNode.put(interKey, evidence);
						}
					}
				}
			}
			
			break;
		}
		
		return newNode;
	}
	
	/**
	 * 
	 */
	public GraphBPELNode() {
		this.context.add("in_transit");
		this.context.add("in_transit_at_rest");
		this.context.add("in_transit_at_rest_in_usage");
		this.TestCategories = HashMultimap.create();
	}

	public static String mergeProperty(GraphBPELNode source,
			VirtualNodeOperation sequence, GraphBPELNode target, IOntologyQuestioner ontology) {
		return ontology.nearestCommonAncestor(source.secProperty, target.secProperty);
	}
	
}
