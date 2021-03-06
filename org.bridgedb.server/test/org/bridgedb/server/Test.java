/*
 * BridgeDb,
 * An abstraction layer for identifier mapping services, both local and online.
 * Copyright (c) 2006 - 2009  BridgeDb Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.bridgedb.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bridgedb.AttributeMapper;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.restlet.Context;
import org.restlet.data.Parameter;
import org.restlet.util.Series;
import org.restlet.util.WrapperList;

@Disabled("Not run by default as it depends on local Derby files, as specified in the gdb.config")
public class Test {
	private static IDMapper mapper;
	private static Server server;
	private static boolean configExists;
	
	@BeforeAll
	public static void setUp() throws Exception {
	    
	   
	    File f= new File(IDMapperService.CONF_GDBS);
	    
	    configExists = f.exists();
	    
	    if (configExists)
	    {
	    
    	    if (server == null)
            {
                server = new Server();
                server.run(8183, null, false);
            }
    		if(mapper == null) {
    		    
    			// Create a client
    			Class.forName("org.bridgedb.webservice.bridgerest.BridgeRest");
    			//mapper = BridgeDb.connect("idmapper-bridgerest:http://localhost:8183/Human");
    			mapper = BridgeDb.connect("idmapper-bridgerest:https://webservice.bridgedb.org/Human");

    		}
	    }
	    else
	    {
	        System.err.println("Skipping server tests. No " + IDMapperService.CONF_GDBS + " found.");
	    }
		
	}
	
	

	@AfterEach
    public void tearDown() throws Exception
    {
	    if (server != null)
	    {
	        server.stop();
	    }
    }



    public IDMapper getLocalService() {
		return mapper;
	}
	
	@org.junit.jupiter.api.Test
	public void testLocalMapID() throws IDMapperException, ClassNotFoundException {
		
	    if (configExists)
	    {
    	    IDMapper mapper = getLocalService();
    		
    		Xref hmdb = new Xref ("HMDB0000108", DataSource.getExistingBySystemCode("Ch"));
    		Xref insr = new Xref ("3643", DataSource.getExistingBySystemCode("L"));
    		Xref affy = new Xref ("33162_at", DataSource.getExistingBySystemCode("X"));
    		//Set<Xref> result = mapper.mapID(insr);
    		//assertTrue (result.contains(affy));
    		//assertTrue(mapper.xrefExists(insr));
    		
    		Set<Xref> result = mapper.mapID(hmdb);
    		assertTrue (result.contains(hmdb));
    		
	    }
	}
	
	@org.junit.jupiter.api.Test
	public void testLocalCapabilities() throws IDMapperException, ClassNotFoundException {
		
	    if (configExists)
	    {
    	    IDMapper mapper = getLocalService();
    		
    		IDMapperCapabilities cap = mapper.getCapabilities();
    		
    		Set<DataSource> supported = cap.getSupportedSrcDataSources();
    		assertTrue (supported.contains(DataSource.getExistingBySystemCode("L")));
    
    		String val = cap.getProperty("SCHEMAVERSION");
    		assertNotNull(val);
    		
    		//cap.getBaseUrl();
    		
    		
    		Set<DataSource> srcDs = cap.getSupportedSrcDataSources();
    		assertTrue(srcDs.size() > 0);
    		assertTrue(cap.isFreeSearchSupported());
    		assertTrue(cap.isMappingSupported(DataSource.getExistingBySystemCode("S"), DataSource.getExistingBySystemCode("L")));
    		//assertFalse(cap.isMappingSupported(
    		//		DataSource.getExistingBySystemCode("??"), DataSource.getExistingBySystemCode("!!")));
	    }
	}
	
	@org.junit.jupiter.api.Test
	public void testLocalSearch() throws IDMapperException, ClassNotFoundException {
		
		DataSourceTxt.init();
		
	    if (configExists)
        {
    	    IDMapper mapper = getLocalService();
    		
    	    IDMapperService idMapperService = new IDMapperService(null, false);
    	    Context testContext = new Context();
    	    Map<String, Object> attributeMap = new HashMap<String, Object>();
    	    //parameters.add(system, "HMDB0000108");
    	    
			attributeMap.put("PAR_ID", "HMDB0000108");
    	    testContext.setAttributes(attributeMap);
    	    testContext.setParameters(null);
    	    
    	    idMapperService.setContext(testContext);
    	    String id = "HMDB0000108";
    	    String sysCode = "Ch";
    		Xref checkXref = new Xref(id, DataSource.getExistingBySystemCode(sysCode));

    		idMapperService.createRoot();
    		
    		//Set<Xref> result = mapper.freeSearch("1234", 100);
    		//System.out.println(result);
    		//assertTrue(result.size() > 0);
        }
	}
	
	@org.junit.jupiter.api.Test
	public void testLocalAttributes() throws ClassNotFoundException, IDMapperException {
		
	    if (configExists)
        {
    	    AttributeMapper mapper = (AttributeMapper)getLocalService();
    		
    		Xref insr = new Xref("3643", DataSource.getExistingBySystemCode("L"));
    		Xref hmdb = new Xref ("HMDB0000108", DataSource.getExistingBySystemCode("Ch"));
    		Xref newHmdb = new Xref("HMDB0000108", DataSource.getExistingBySystemCode("Ch"));
    		
    		String checkId = hmdb.getId();
    		if (hmdb.getDataSource() == DataSource.getExistingBySystemCode("Ch") && checkId.length() == 11) {
    			String newId = checkId.replace("0000", "00");
    			newHmdb = new Xref(newId, DataSource.getExistingBySystemCode("Ch"));
    		}
    			// remove additional zeros in the ID
    			// create a new Xref object with the correct ID
    		
    		Map<String, Set<String>> attrMap = mapper.getAttributes(hmdb);
    		assertNull(attrMap.get("Symbol"));
    		
    		attrMap = mapper.getAttributes(newHmdb);
    		assertNotNull(attrMap.get("Symbol"));

    		/*Map<String, Set<String>> attrMap = mapper.getAttributes(insr);
    		assertNotNull(attrMap.get("Symbol"));
			assertEquals(2, attrMap.get("Symbol").size());
    		
    		Set<String> attrValues = mapper.getAttributes(insr, "Symbol");
			assertEquals(2, attrValues.size());
    		
    		Map<Xref, String> xrefMap = mapper.freeAttributeSearch("INSR", "Symbol", 1);
			assertEquals(1, xrefMap.size());
    
    		xrefMap = mapper.freeAttributeSearch("INSR", "Symbol", 100);
    		assertTrue(xrefMap.containsKey(insr));
    		assertTrue(xrefMap.size() > 1);
    		
    		Set<String> attrs = mapper.getAttributeSet();
    		assertTrue(attrs.size() > 0);
    		*/
        }
	}
}
