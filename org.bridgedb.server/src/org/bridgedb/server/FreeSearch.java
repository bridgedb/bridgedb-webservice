// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
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

import java.util.Set;

import org.bridgedb.IDMapper;
import org.bridgedb.Xref;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

/**
 * Resource that handles the xref queries
 */
public class FreeSearch extends IDMapperResource {
	String searchStr;
	int limit = 0;

	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			searchStr = urlDecode((String) getRequest().getAttributes().get( IDMapperService.PAR_QUERY ));
			String limitStr = (String)getRequest().getAttributes().get( IDMapperService.PAR_TARGET_LIMIT );

			if ( null != limitStr ) 
			{
				limit = new Integer( limitStr ).intValue();
			}
		} catch(Exception e) {
			throw new ResourceException(e);
		}
	}

	@Get
	public String search() 
	{
		try 
		{
			IDMapper mapper = getIDMappers();
			Set<Xref> results = mapper.freeSearch(searchStr, limit);

			StringBuilder result = new StringBuilder();
			for(Xref x : results) {
				result.append(x.getId());
				result.append("\t");
				result.append(x.getDataSource().getFullName());
				result.append("\n");
			}

			return(result.toString());
		} catch( Exception e ) {
			e.printStackTrace();
			setStatus( Status.SERVER_ERROR_INTERNAL );
			return e.getMessage();
		}
	}

}
