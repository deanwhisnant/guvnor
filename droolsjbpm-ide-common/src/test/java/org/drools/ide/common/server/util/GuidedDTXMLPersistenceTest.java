/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ide.common.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.TypeSafeGuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
import org.junit.Before;
import org.junit.Test;

public class GuidedDTXMLPersistenceTest {

    //    public void testXML() {

    //            //final String xml = p.marshal( new RuleModel() );
    //
    //    }

    @Before
    public void setUp() throws Exception {
        GuidedDTXMLPersistence.getInstance();
    }

    @Test
    public void testRoundTrip() {

        TypeSafeGuidedDecisionTable dt = new TypeSafeGuidedDecisionTable();

        dt.getActionCols().add( new ActionInsertFactCol() );
        ActionSetFieldCol set = new ActionSetFieldCol();
        set.setFactField( "foo" );
        dt.getActionCols().add( set );

        dt.getMetadataCols().add( new MetadataCol() );

        dt.getAttributeCols().add( new AttributeCol() );

        dt.getConditionCols().add( new ConditionCol() );

        dt.setData( RepositoryUpgradeHelper.makeDataLists( new String[][]{new String[]{"1", "hola"}} ) );
        dt.setTableName( "blah" );

        String xml = GuidedDTXMLPersistence.getInstance().marshal( dt );
        System.out.println( xml );
        assertNotNull( xml );
        assertEquals( -1,
                      xml.indexOf( "ActionSetField" ) );
        assertEquals( -1,
                      xml.indexOf( "ConditionCol" ) );
        assertEquals( -1,
                      xml.indexOf( "GuidedDecisionTable" ) );

        TypeSafeGuidedDecisionTable dt_ = GuidedDTXMLPersistence.getInstance().unmarshal( xml );
        assertNotNull( dt_ );
        assertEquals( "blah",
                      dt_.getTableName() );
        assertEquals( 1,
                      dt_.getMetadataCols().size() );
        assertEquals( 1,
                      dt_.getAttributeCols().size() );
        assertEquals( 2,
                      dt_.getActionCols().size() );
        assertEquals( 1,
                      dt_.getConditionCols().size() );

    }

    @Test
    public void testBackwardsCompatability() throws Exception {
        String xml = BRLPersistenceTest.loadResource( "ExistingDecisionTable.xml" );
        TypeSafeGuidedDecisionTable dt_ = GuidedDTXMLPersistence.getInstance().unmarshal( xml );
        assertNotNull( dt_ );
        assertEquals( "blah",
                      dt_.getTableName() );
        assertEquals( 1,
                      dt_.getMetadataCols().size() );
        assertEquals( 1,
                      dt_.getAttributeCols().size() );
        assertEquals( 2,
                      dt_.getActionCols().size() );
        assertEquals( 1,
                      dt_.getConditionCols().size() );

        assertTrue( dt_.getActionCols().get( 1 ) instanceof ActionSetFieldCol );
        ActionSetFieldCol asf = (ActionSetFieldCol) dt_.getActionCols().get( 1 );
        assertEquals( "foo",
                      asf.getFactField() );
        assertEquals( false,
                      asf.isUpdate() );
    }

}
