// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperStack;
import org.bridgedb.bio.Organism;
import org.bridgedb.rdb.GdbProvider;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * Base resource implementation that provides methods shared
 * between the idmapper resources (such as access to the IDMapper objects).
 */
public class IDMapperResource extends ServerResource {
	private IDMapperStack mappers;
	private String orgName;
	
	protected DataSource parseDataSource(String dsName) {
		if(dsName == null) return null;
		DataSource ds = null;
		//Try parsing by full name
		if(DataSource.getFullNames().contains(dsName)) {
			ds = DataSource.getExistingByFullName(dsName);
		} else { //If not possible, use system code
			ds = DataSource.getExistingBySystemCode(dsName);
		}
		return ds;
	}
	
	@Override
	protected void doInit() throws ResourceException {
		try {
		orgName = urlDecode(
				(String) getRequest().getAttributes().get(IDMapperService.PAR_ORGANISM)
		);
		String requestedID = urlDecode(
				(String) getRequest().getAttributes().get(IDMapperService.PAR_ID)
		);
		
		// Check for new HMDB identifier in request
		if(requestedID != null) {
			if (requestedID.startsWith("HMDB") && requestedID.length()==11) {
				String newId = requestedID.replace("0000", "00");
				Map<String, Object> newIdAttributes = new HashMap<>();
				newIdAttributes = getRequest().getAttributes();
				// Put newId as value for key id in order to replace the wrong HMDB id
				newIdAttributes.put("id", newId);
				// Set attributes of the request to be the new map of attributes with the correct HMDB id
				getRequest().setAttributes(newIdAttributes);
			}
		}
		
		initIDMappers();
		} catch(UnsupportedEncodingException e) {
			throw new ResourceException(e);
		}
	}
	
	/**
	 * Decode the parameter from the url to remove %20 etc.
	 */
	protected String urlDecode(String string) throws UnsupportedEncodingException {
		return string == null ? null : URLDecoder.decode(string, "UTF-8");
	}
	
	private void initIDMappers() {
		Organism org = Organism.fromLatinName(orgName);
		if(org == null) { //Fallback on code
			org = Organism.fromCode(orgName);
		}
		if(org == null) { //Fallback on shortname
			org = Organism.fromShortName(orgName);
		}
		if(org == null) {
			throw new IllegalArgumentException("Unknown organism: " + orgName + "<p><font size='+1'><i>Double check the spelling. We are expecting an entry like: Human</i></font></p>");
		}
		mappers = getGdbProvider().getStack(org);
		if (mappers.getSize() == 0)
		{
			throw new IllegalArgumentException("No database found for: " + orgName +"<p><font size='+1'><i>Verify that the database is supported and properly referenced in gdb.config.</i></font></p>");
		}
	}
	protected IDMapperStack getIDMappers() {
		return mappers;
	}
	
	private GdbProvider getGdbProvider() {
		return ((IDMapperService)getApplication()).getGdbProvider();
	}
}
