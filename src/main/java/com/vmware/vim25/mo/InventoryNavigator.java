package com.vmware.vim25.mo;

import java.lang.reflect.Method;
import java.rmi.RemoteException;

import com.vmware.vim25.*;
import com.vmware.vim25.mo.util.*;

public class InventoryNavigator
{
  private ManagedEntity rootEntity = null;
  private SelectionSpec[] selectionSpecs = null;

  public InventoryNavigator(ManagedEntity rootEntity)
  {
    this.rootEntity = rootEntity;
  }

  /**
   * Retrieve container contents from specified parent recursively if requested.
   * @param recurse retrieve contents recursively from the root down
   * @throws RemoteException 
   * @throws RuntimeFault 
   * @throws InvalidProperty 
   */
  public ManagedEntity[] searchManagedEntities(boolean recurse) throws InvalidProperty, RuntimeFault, RemoteException  
  {
    String[][] typeinfo = new String[][] { new String[] { "ManagedEntity", }};
    return searchManagedEntities(typeinfo, recurse);
  }

  /**
    * Get the first ManagedObjectReference from current node for the specified type
    */
  public ManagedEntity[] searchManagedEntities(String type) throws InvalidProperty, RuntimeFault, RemoteException  
  {
    String[][] typeinfo = new String[][] { new String[] { type,  "name",}, };
    return searchManagedEntities(typeinfo, true);
  }

  /**
  * Retrieve content recursively with multiple properties.
  * the typeinfo array contains typename + properties to retrieve.
  *
  * @param typeinfo 2D array of properties for each typename
  * @param recurse retrieve contents recursively from the root down
  *
  * @return retrieved object contents
   * @throws RemoteException 
   * @throws RuntimeFault 
   * @throws InvalidProperty 
  */
  public ManagedEntity[] searchManagedEntities(String[][] typeinfo, boolean recurse ) throws InvalidProperty, RuntimeFault, RemoteException  
  {
    ObjectContent[] ocs = retrieveObjectContents(typeinfo, recurse);
    return createManagedEntities(ocs);
  }
  
  private ObjectContent[] retrieveObjectContents(String[][] typeinfo, boolean recurse ) throws InvalidProperty, RuntimeFault, RemoteException
  {
    if (typeinfo == null || typeinfo.length == 0) 
    {
      return null;
    }
  
    PropertyCollector pc = rootEntity.getServerConnection().getServiceInstance().getPropertyCollector();
  
    if (recurse && selectionSpecs==null) 
    {
      AboutInfo ai = rootEntity.getServerConnection().getServiceInstance().getAboutInfo();
      
      /* The apiVersion values in all the shipped products
      "2.0.0"    VI 3.0
      "2.5.0"    VI 3.5 (and u1)
      "2.5u2"   VI 3.5u2 (and u3, u4)
      "4.0"       vSphere 4.0 (and u1)
      "4.1"       vSphere 4.1
      "5.0"       vSphere 5.0
      ******************************************************/
      if(ai.apiVersion.startsWith("4") || ai.apiVersion.startsWith("5"))
      {
        selectionSpecs = PropertyCollectorUtil.buildFullTraversalV4();
      }
      else
      {
        selectionSpecs = PropertyCollectorUtil.buildFullTraversal();
      }
    }
  
    PropertySpec[] propspecary = PropertyCollectorUtil.buildPropertySpecArray(typeinfo);
  
    ObjectSpec os = new ObjectSpec();
    os.setObj(rootEntity.getMOR());
    os.setSkip(Boolean.FALSE);
    os.setSelectSet(selectionSpecs);
    
    PropertyFilterSpec spec = new PropertyFilterSpec();
    spec.setObjectSet(new ObjectSpec[] { os });
    spec.setPropSet(propspecary);
    
    return pc.retrieveProperties(new PropertyFilterSpec[] { spec } );
  }
      
  private ManagedEntity[] createManagedEntities(ObjectContent[] ocs) 
  {
    if(ocs==null)
    {
      return new ManagedEntity[] {};
    }
    ManagedEntity[] mes = new ManagedEntity[ocs.length];
    
    for(int i=0; i<mes.length; i++)
    {
      ManagedObjectReference mor = ocs[i].getObj();
      mes[i] = MorUtil.createExactManagedEntity(rootEntity.getServerConnection(), mor);
    }
    return mes;
  }
  
   /**
    * Get the ManagedObjectReference for an item under the
    * specified parent node that has the type and name specified.
    *
    * @param type type of the managed object
    * @param name name to match
    * @return First ManagedEntity object of the type / name pair found
    * @throws RemoteException 
    * @throws RuntimeFault 
    * @throws InvalidProperty 
    */
  public ManagedEntity searchManagedEntity(String type, String name) throws InvalidProperty, RuntimeFault, RemoteException  
  {
    if (name == null || name.length() == 0)
    {
      return null;
    }
    
    if(type==null) 
    {
      type = "ManagedEntity";
    }
    
    String[][] typeinfo = new String[][] { new String[] { type,  "name",}, };

    ObjectContent[] ocs = retrieveObjectContents(typeinfo, true);

    if (ocs==null || ocs.length == 0) 
    {
      return null;
    }

    for (int i = 0; i < ocs.length; i++) 
    {
      DynamicProperty[] propSet = ocs[i].getPropSet();
      
      if (propSet.length > 0) 
      {
        String nameInPropSet = (String) propSet[0].getVal();
        if(name.equalsIgnoreCase(nameInPropSet))
        {
          ManagedObjectReference mor = ocs[i].getObj();
          return MorUtil.createExactManagedEntity(rootEntity.getServerConnection(), mor);
        }
      }
    }
    return null;
  }

  public ManagedEntity searchManagedEntity(ManagedObjectReference mor,
	         String propertyName) throws InvalidProperty, RuntimeFault, RemoteException {
	      ObjectContent[] ocs = retrieveObjectContents(mor,propertyName,true);

		if (ocs==null || ocs.length == 0) 
		{
			return null;
		}

		for (int i = 0; i < ocs.length; i++) 
		{
			DynamicProperty[] propSet = ocs[i].getPropSet();
			
			if (propSet.length > 0) 
			{
				String nameInPropSet = (String) propSet[0].getName();
				if(propertyName.equalsIgnoreCase(nameInPropSet))
				{
					ManagedObjectReference morr = ocs[i].getObj();
					return MorUtil.createExactManagedEntity(rootEntity.getServerConnection(), morr);
				}
			}
		}
		return null;
	}

	private ObjectContent[] retrieveObjectContents( ManagedObjectReference mor,
	         String propertyName,boolean recurse) throws InvalidProperty, RuntimeFault, RemoteException
	{

		PropertyCollector pc = rootEntity.getServerConnection().getServiceInstance().getPropertyCollector();
	
		if (recurse && selectionSpecs==null) 
		{
		  AboutInfo ai = rootEntity.getServerConnection().getServiceInstance().getAboutInfo();
		  
		  /* The apiVersion values in all the shipped products
		  "2.0.0"    VI 3.0
		  "2.5.0"    VI 3.5 (and u1)
		  "2.5u2"   VI 3.5u2 (and u3, u4)
		  "4.0"       vSphere 4.0 (and u1)
		  "4.1"       vSphere 4.1
		  "5.0"       vSphere 5.0
   ******************************************************/
			if(ai.apiVersion.startsWith("4") || ai.apiVersion.startsWith("5"))
			{
			  selectionSpecs = PropertyCollectorUtil.buildFullTraversalV4();
			}
			else
			{
			  selectionSpecs = PropertyCollectorUtil.buildFullTraversal();
			}
		}
	
		
		PropertySpec proSpec = new PropertySpec();
		proSpec.setType( mor.getType() );
		proSpec.setPathSet( new String[]{propertyName} );
		proSpec.setAll(new Boolean( propertyName == null || propertyName.equals("") ));
	
		ObjectSpec os = new ObjectSpec();
		os.setObj( mor );
		os.setSkip(Boolean.FALSE);
		os.setSelectSet(selectionSpecs);
		
		PropertyFilterSpec spec = new PropertyFilterSpec();
		spec.setObjectSet(new ObjectSpec[] { os });
		spec.setPropSet(new PropertySpec[] { proSpec });

		return pc.retrieveProperties(new PropertyFilterSpec[] { spec } );
	}

	   /**
	    * Determines of a method 'methodName' exists for the Object 'obj'
	    * @param obj The Object to check
	    * @param methodName The method name
	    * @param parameterTypes Array of Class objects for the parameter types
	    * @return true if the method exists, false otherwise
	    */
	   @SuppressWarnings("rawtypes")
	boolean methodExists(Object obj, 
	         String methodName, 
	         Class[] parameterTypes) {
	      boolean exists = false;
	      try {
	         Method method = obj.getClass().getMethod(methodName, parameterTypes);
	         if (method != null) {
	            exists = true;
	         }
	      } catch(Exception e){}
	      return exists;
	   }
}
