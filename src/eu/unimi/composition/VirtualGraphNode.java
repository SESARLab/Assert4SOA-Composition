package eu.unimi.composition;

import java.util.LinkedList;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;

import eu.assert4soa.datamodel.ASSERT;

public class VirtualGraphNode {
	private String id;
	private VirtualNodeType type;
	private VirtualNodeOperation operation;
	private LinkedList<ASSERT> substituteCERT;
	private ASSERT assertCert;
	private mxGraphModel graph;
	private VirtualCertificate virtualCertificate;
	
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
	 * @return the virtualCertificate
	 */
	public VirtualCertificate getVirtualCertificate() {
		return virtualCertificate;
	}
	
	/**
	 * @param virtualCertificate the virtualCertificate to set
	 */
	public void setVirtualCertificate(VirtualCertificate virtualCertificate) {
		this.virtualCertificate = virtualCertificate;
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
	 * @return the assertCert
	 */
	public ASSERT getAssertCert() {
		return assertCert;
	}
	
	/**
	 * @param assertCert the assertCert to set
	 */
	public void setAssertCert(ASSERT assertCert) {
		this.assertCert = assertCert;
	}
	
	/**
	 * @return the graph
	 */
	public mxGraphModel getGraph() {
		return graph;
	}
	
	/**
	 * @param graph the graph to set
	 */
	public void setGraph(mxGraphModel graph) {
		this.graph = graph;
	}
	
	/**
	 * @param type
	 * @param operation
	 * @param assertCert
	 * @param graph
	 */
	public VirtualGraphNode(VirtualNodeType type,
			VirtualNodeOperation operation, ASSERT assertCert, mxGraphModel graph) {
		this.type = type;
		this.operation = operation;
		this.assertCert = assertCert;
		this.graph = graph;
	}
	
	/**
	 * 
	 */
	public VirtualGraphNode() {
	}
	
	/**
	 * @param type
	 * @param operation
	 * @param assertCert
	 * @param graph
	 * @param virtualCertificate
	 */
	public VirtualGraphNode(VirtualNodeType type,
			VirtualNodeOperation operation, ASSERT assertCert, mxGraphModel graph,
			VirtualCertificate virtualCertificate) {
		this.type = type;
		this.operation = operation;
		this.assertCert = assertCert;
		this.graph = graph;
		this.virtualCertificate = virtualCertificate;
	}

	/**
	 * @param type
	 * @param operation
	 * @param substituteCERT
	 * @param assertCert
	 * @param graph
	 * @param virtualCertificate
	 */
	public VirtualGraphNode(VirtualNodeType type,
			VirtualNodeOperation operation, LinkedList<ASSERT> substituteCERT,
			ASSERT assertCert, mxGraphModel graph,
			VirtualCertificate virtualCertificate) {
		this.type = type;
		this.operation = operation;
		this.substituteCERT = substituteCERT;
		this.assertCert = assertCert;
		this.graph = graph;
		this.virtualCertificate = virtualCertificate;
	}
}
