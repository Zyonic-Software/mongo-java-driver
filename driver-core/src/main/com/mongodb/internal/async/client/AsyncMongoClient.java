/*
 * Copyright 2008-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.internal.async.client;

import com.mongodb.ClientSessionOptions;
import com.mongodb.annotations.Immutable;
import com.mongodb.connection.ClusterDescription;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.event.ClusterListener;
import com.mongodb.internal.async.SingleResultCallback;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.Closeable;
import java.util.List;

/**
 * A client-side representation of a MongoDB cluster.  Instances can represent either a standalone MongoDB instance, a replica set,
 * or a sharded cluster.  Instance of this class are responsible for maintaining an up-to-date state of the cluster,
 * and possibly cache resources related to this, including background threads for monitoring, and connection pools.
 * <p>
 * Instance of this class serve as factories for {@code MongoDatabase} instances.
 * </p>
 */
@Immutable
public interface AsyncMongoClient extends Closeable {

    /**
     * Creates a client session with default options.
     *
     * <p>Note: A ClientSession instance can not be used concurrently in multiple asynchronous operations.</p>
     *
     * @param callback the callback that is passed the clientSession or a {@code MongoClientException} if the MongoDB cluster to which
     *                 this client is connected does not support sessions
     * @mongodb.server.release 3.6
     * @since 3.8
     */
    void startSession(SingleResultCallback<AsyncClientSession> callback);

    /**
     * Creates a client session.
     *
     * <p>Note: A ClientSession instance can not be used concurrently in multiple asynchronous operations.</p>
     *
     * @param options  the options for the client session
     * @param callback the callback that is passed the clientSession or a {@code MongoClientException} if the MongoDB cluster to which
     *                 this client is connected does not support sessions
     * @mongodb.server.release 3.6
     * @since 3.6
     */
    void startSession(ClientSessionOptions options, SingleResultCallback<AsyncClientSession> callback);

    /**
     * Gets the database with the given name.
     *
     * @param name the name of the database
     * @return the database
     * @throws IllegalArgumentException if databaseName is invalid
     * @see com.mongodb.MongoNamespace#checkDatabaseNameValidity(String)
     */
    AsyncMongoDatabase getDatabase(String name);

    /**
     * Close the client, which will close all underlying cached resources, including, for example,
     * sockets and background monitoring threads.
     */
    void close();

    /**
     * Get a list of the database names
     *
     * @return an iterable containing all the names of all the databases
     * @mongodb.driver.manual reference/command/listDatabases List Databases
     */
    AsyncMongoIterable<String> listDatabaseNames();

    /**
     * Get a list of the database names
     *
     * @param clientSession the client session with which to associate this operation
     * @return an iterable containing all the names of all the databases
     * @mongodb.driver.manual reference/command/listDatabases List Databases
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    AsyncMongoIterable<String> listDatabaseNames(AsyncClientSession clientSession);

    /**
     * Gets the list of databases
     *
     * @return the list databases iterable interface
     */
    AsyncListDatabasesIterable<Document> listDatabases();

    /**
     * Gets the list of databases
     *
     * @param clientSession the client session with which to associate this operation
     * @return the list databases iterable interface
     * @mongodb.driver.manual reference/command/listDatabases List Databases
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    AsyncListDatabasesIterable<Document> listDatabases(AsyncClientSession clientSession);

    /**
     * Gets the list of databases
     *
     * @param resultClass the class to cast the database documents to
     * @param <TResult>   the type of the class to use instead of {@code Document}.
     * @return the list databases iterable interface
     */
    <TResult> AsyncListDatabasesIterable<TResult> listDatabases(Class<TResult> resultClass);

    /**
     * Gets the list of databases
     *
     * @param clientSession the client session with which to associate this operation
     * @param resultClass the class to cast the database documents to
     * @param <TResult>   the type of the class to use instead of {@code Document}.
     * @return the list databases iterable interface
     * @mongodb.driver.manual reference/command/listDatabases List Databases
     * @since 3.6
     * @mongodb.server.release 3.6
     */
    <TResult> AsyncListDatabasesIterable<TResult> listDatabases(AsyncClientSession clientSession, Class<TResult> resultClass);

    /**
     * Creates a change stream for this client.
     *
     * @return the change stream iterable
     * @mongodb.driver.dochub core/changestreams Change Streams
     * @since 3.8
     * @mongodb.server.release 4.0
     */
    AsyncChangeStreamIterable<Document> watch();

    /**
     * Creates a change stream for this client.
     *
     * @param resultClass the class to decode each document into
     * @param <TResult>   the target document type of the iterable.
     * @return the change stream iterable
     * @mongodb.driver.dochub core/changestreams Change Streams
     * @since 3.8
     * @mongodb.server.release 4.0
     */
    <TResult> AsyncChangeStreamIterable<TResult> watch(Class<TResult> resultClass);

    /**
     * Creates a change stream for this client.
     *
     * @param pipeline the aggregation pipeline to apply to the change stream.
     * @return the change stream iterable
     * @mongodb.driver.dochub core/changestreams Change Streams
     * @since 3.8
     * @mongodb.server.release 4.0
     */
    AsyncChangeStreamIterable<Document> watch(List<? extends Bson> pipeline);

    /**
     * Creates a change stream for this client.
     *
     * @param pipeline    the aggregation pipeline to apply to the change stream
     * @param resultClass the class to decode each document into
     * @param <TResult>   the target document type of the iterable.
     * @return the change stream iterable
     * @mongodb.driver.dochub core/changestreams Change Streams
     * @since 3.8
     * @mongodb.server.release 4.0
     */
    <TResult> AsyncChangeStreamIterable<TResult> watch(List<? extends Bson> pipeline, Class<TResult> resultClass);

    /**
     * Creates a change stream for this client.
     *
     * @param clientSession the client session with which to associate this operation
     * @return the change stream iterable
     * @since 3.8
     * @mongodb.server.release 4.0
     * @mongodb.driver.dochub core/changestreams Change Streams
     */
    AsyncChangeStreamIterable<Document> watch(AsyncClientSession clientSession);

    /**
     * Creates a change stream for this client.
     *
     * @param clientSession the client session with which to associate this operation
     * @param resultClass the class to decode each document into
     * @param <TResult>   the target document type of the iterable.
     * @return the change stream iterable
     * @since 3.8
     * @mongodb.server.release 4.0
     * @mongodb.driver.dochub core/changestreams Change Streams
     */
    <TResult> AsyncChangeStreamIterable<TResult> watch(AsyncClientSession clientSession, Class<TResult> resultClass);

    /**
     * Creates a change stream for this client.
     *
     * @param clientSession the client session with which to associate this operation
     * @param pipeline the aggregation pipeline to apply to the change stream.
     * @return the change stream iterable
     * @since 3.8
     * @mongodb.server.release 4.0
     * @mongodb.driver.dochub core/changestreams Change Streams
     */
    AsyncChangeStreamIterable<Document> watch(AsyncClientSession clientSession, List<? extends Bson> pipeline);

    /**
     * Creates a change stream for this client.
     *
     * @param clientSession the client session with which to associate this operation
     * @param pipeline    the aggregation pipeline to apply to the change stream
     * @param resultClass the class to decode each document into
     * @param <TResult>   the target document type of the iterable.
     * @return the change stream iterable
     * @since 3.8
     * @mongodb.server.release 4.0
     * @mongodb.driver.dochub core/changestreams Change Streams
     */
    <TResult> AsyncChangeStreamIterable<TResult> watch(AsyncClientSession clientSession, List<? extends Bson> pipeline,
                                                       Class<TResult> resultClass);

    /**
     * Gets the current cluster description.
     *
     * <p>
     * This method will not block, meaning that it may return a {@link ClusterDescription} whose {@code clusterType} is unknown
     * and whose {@link com.mongodb.connection.ServerDescription}s are all in the connecting state.  If the application requires
     * notifications after the driver has connected to a member of the cluster, it should register a {@link ClusterListener} via
     * the {@link ClusterSettings} in {@link com.mongodb.MongoClientSettings}.
     * </p>
     *
     * @return the current cluster description
     * @see ClusterSettings.Builder#addClusterListener(ClusterListener)
     * @see com.mongodb.MongoClientSettings.Builder#applyToClusterSettings(com.mongodb.Block)
     */
    ClusterDescription getClusterDescription();
}
