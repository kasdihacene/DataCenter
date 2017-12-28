package fr.upmc.datacenter.dataprovider.interfaces;

/**
 * 
 * @author Hacene KASDI
 * @version 28.12.17.HK
 * 
 * This class allows adding and removing <code>ApplicationContainer</code>
 *
 */
public interface DataProviderDispatcherI {
	/**
	 * Add an ApplicationContainer 
	 * @param applicationURI
	 * @param dispatcherURI
	 * @throws Exception
	 */
	public void addApplicationContainer(String applicationURI, String dispatcherURI) throws Exception;
	/**
	 * Remove an ApplicationContainer according to his URI
	 * @param applicationURI
	 * @throws Exception
	 */
	public void removeApplicationContainer(String applicationURI) throws Exception;

}
