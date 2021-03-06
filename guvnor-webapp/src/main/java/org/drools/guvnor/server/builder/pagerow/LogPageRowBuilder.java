/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.server.builder.pagerow;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.rpc.LogEntry;
import org.drools.guvnor.client.rpc.LogPageRow;
import org.drools.guvnor.client.rpc.PageRequest;

public class LogPageRowBuilder {
    public List<LogPageRow> createRows(final PageRequest pageRequest,
                                       final LogEntry[] logEntries) {
        int rowNumber = 0;
        int rowMinNumber = pageRequest.getStartRowIndex();
        int rowMaxNumber = pageRequest.getPageSize() == null ? logEntries.length : Math.min( rowMinNumber + pageRequest.getPageSize(),
                                                                                             logEntries.length );
        int resultsSize = (pageRequest.getPageSize() == null ? logEntries.length : pageRequest.getPageSize());
        List<LogPageRow> rowList = new ArrayList<LogPageRow>( resultsSize );
        for ( rowNumber = rowMinNumber; rowNumber < rowMaxNumber; rowNumber++ ) {
            LogEntry e = logEntries[rowNumber];
            LogPageRow row = new LogPageRow();
            row.setSeverity( e.severity );
            row.setMessage( e.message );
            row.setTimestamp( e.timestamp );
            rowList.add( row );
        }

        return rowList;
    }

}
