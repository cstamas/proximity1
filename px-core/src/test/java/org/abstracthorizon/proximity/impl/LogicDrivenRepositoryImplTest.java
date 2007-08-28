/*

   Copyright 2005-2007 Tamas Cservenak (t.cservenak@gmail.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.abstracthorizon.proximity.impl;

import junit.framework.Assert;
import junit.framework.TestCase;

// TODO: Auto-generated Javadoc
/**
 * The Class LogicDrivenRepositoryImplTest.
 */
public class LogicDrivenRepositoryImplTest
    extends TestCase
{

    /** The repo. */
    private LogicDrivenRepositoryImpl repo = new LogicDrivenRepositoryImpl();

    /**
     * Dummy method.
     */
    public void testDummy()
    {
        Assert.assertEquals( repo.getGroupId(), repo.getGroupId() );
    }

}
