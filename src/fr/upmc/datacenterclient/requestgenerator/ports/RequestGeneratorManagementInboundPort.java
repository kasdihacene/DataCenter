package fr.upmc.datacenterclient.requestgenerator.ports;

//Copyright Jacques Malenfant, Univ. Pierre et Marie Curie.
//
//Jacques.Malenfant@lip6.fr
//
//This software is a computer program whose purpose is to provide a
//basic component programming model to program with components
//distributed applications in the Java programming language.
//
//This software is governed by the CeCILL-C license under French law and
//abiding by the rules of distribution of free software.  You can use,
//modify and/ or redistribute the software under the terms of the
//CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
//URL "http://www.cecill.info".
//
//As a counterpart to the access to the source code and  rights to copy,
//modify and redistribute granted by the license, users are provided only
//with a limited warranty  and the software's author,  the holder of the
//economic rights,  and the successive licensors  have only  limited
//liability. 
//
//In this respect, the user's attention is drawn to the risks associated
//with loading,  using,  modifying and/or developing or reproducing the
//software by the user in light of its specific status of free software,
//that may mean  that it is complicated to manipulate,  and  that  also
//therefore means  that it is reserved for developers  and  experienced
//professionals having in-depth computer knowledge. Users are therefore
//encouraged to load and test the software's suitability as regards their
//requirements in conditions enabling the security of their systems and/or 
//data to be ensured and,  more generally, to use and operate it in the 
//same conditions as regards security. 
//
//The fact that you are presently reading this means that you have had
//knowledge of the CeCILL-C license and that you accept its terms.

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;

/**
 * The class <code>RequestGeneratorManagementInboundPort</code> implements the
 * inbound port through which the component management methods are called.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : May 5, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class				RequestGeneratorManagementInboundPort
extends		AbstractInboundPort
implements	RequestGeneratorManagementI
{
	private static final long serialVersionUID = 1L ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public				RequestGeneratorManagementInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(RequestGeneratorManagementI.class, owner) ;

		assert	owner != null && owner instanceof RequestGenerator ;
	}

	public				RequestGeneratorManagementInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, RequestGeneratorManagementI.class, owner);

		assert	owner != null && owner instanceof RequestGenerator ;
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#startGeneration()
	 */
	@Override
	public void			startGeneration() throws Exception
	{
		final RequestGenerator rg = (RequestGenerator) this.owner ;
		this.owner.handleRequestAsync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							rg.startGeneration() ;
							return null;
						}
					}) ;
	}

	/**
	 * @see fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#stopGeneration()
	 */
	@Override
	public void			stopGeneration() throws Exception
	{
		final RequestGenerator rg = (RequestGenerator) this.owner ;
		this.owner.handleRequestAsync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							rg.stopGeneration() ;
							return null;
						}
					}) ;
	}

	/**
	 * @see fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#getMeanInterArrivalTime()
	 */
	@Override
	public double		getMeanInterArrivalTime() throws Exception
	{
		final RequestGenerator rg = (RequestGenerator) this.owner ;
		return this.owner.handleRequestSync(
				new ComponentI.ComponentService<Double>() {
					@Override
					public Double call() throws Exception {
						return rg.getMeanInterArrivalTime() ;
					}
				}) ;
	}

	/**
	 * @see fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#setMeanInterArrivalTime(double)
	 */
	@Override
	public void			setMeanInterArrivalTime(final double miat)
	throws Exception
	{
		final RequestGenerator rg = (RequestGenerator) this.owner ;
		this.owner.handleRequestAsync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							rg.setMeanInterArrivalTime(miat) ;
							return null;
						}
					}) ;
	}
}
