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
package org.abstracthorizon.proximity.logic;

import org.abstracthorizon.proximity.Item;
import org.abstracthorizon.proximity.ProximityRequest;
import org.abstracthorizon.proximity.Repository;

// TODO: Auto-generated Javadoc
/**
 * Repository logic that drives repository's behaviour.
 * <p>
 * Asked on every request got by Repository, it have a few simple questions to answer and postprocess items after local
 * and remote retrieval.
 * 
 * @author cstamas
 */
public interface RepositoryLogic
{

    /**
     * Return true if repository should check for local cached version of the path.
     * 
     * @param repository the repository
     * @param request the request
     * 
     * @return true, if should check for local copy
     */
    boolean shouldCheckForLocalCopy( Repository repository, ProximityRequest request );

    /**
     * Postprocess item if needed after local copy found.
     * 
     * @param item the item
     * @param repository the repository
     * @param request the request
     * 
     * @return the item
     */
    Item afterLocalCopyFound( Repository repository, ProximityRequest request, Item item );

    /**
     * Return true if repository should initiate remote lookup.
     * 
     * @param repository the repository
     * @param request the request
     * @param localItem the local item
     * 
     * @return true is there is need to check remote copy
     */
    boolean shouldCheckForRemoteCopy( Repository repository, ProximityRequest request, Item localItem );

    /**
     * Postprocess item if needed after remote retrieval.
     * 
     * @param localItem - the artifact found locally
     * @param repository - the artifact found remotely
     * @param request the request
     * @param remoteItem the remote item
     * 
     * @return the item
     */
    Item afterRemoteCopyFound( Repository repository, ProximityRequest request, Item localItem, Item remoteItem );

    /**
     * Return true if reposotiry should store the remote retrieved item in a local writable store.
     * 
     * @param localItem the local item
     * @param remoteItem the remote item
     * @param repository the repository
     * @param request the request
     * 
     * @return true, if should store locally after remote retrieval
     */
    boolean shouldStoreLocallyAfterRemoteRetrieval( Repository repository, ProximityRequest request, Item localItem,
        Item remoteItem );

    /**
     * Choose tha artifact to serve.
     * 
     * @param repository the repository
     * @param request the request
     * @param localItem the local item
     * @param remoteItem the remote item
     * 
     * @return the item
     */
    public Item afterRetrieval( Repository repository, ProximityRequest request, Item localItem, Item remoteItem );

}
