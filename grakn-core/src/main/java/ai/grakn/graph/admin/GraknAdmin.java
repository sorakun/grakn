/*
 * Grakn - A Distributed Semantic Database
 * Copyright (C) 2016  Grakn Labs Limited
 *
 * Grakn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Grakn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Grakn. If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */

package ai.grakn.graph.admin;

import ai.grakn.GraknGraph;
import ai.grakn.concept.Concept;
import ai.grakn.concept.ConceptId;
import ai.grakn.concept.EntityType;
import ai.grakn.concept.RelationType;
import ai.grakn.concept.ResourceType;
import ai.grakn.concept.RoleType;
import ai.grakn.concept.RuleType;
import ai.grakn.concept.Type;
import ai.grakn.concept.TypeId;
import ai.grakn.concept.TypeLabel;
import ai.grakn.exception.InvalidGraphException;
import ai.grakn.util.Schema;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import javax.annotation.CheckReturnValue;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Admin interface for {@link GraknGraph}.
 *
 * @author Filipe Teixeira
 */
public interface GraknAdmin {

    /**
     *
     * @param vertex A vertex which contains properties necessary to build a concept from.
     * @param <T> The type of the concept being built
     * @return A concept built using the provided vertex
     */
    @CheckReturnValue
    <T extends Concept> T buildConcept(Vertex vertex);

    /**
     * Utility function to get a read-only Tinkerpop traversal.
     *
     * @return A read-only Tinkerpop traversal for manually traversing the graph
     */
    @CheckReturnValue
    GraphTraversal<Vertex, Vertex> getTinkerTraversal();

    /**
     * A flag to check if batch loading is enabled and consistency checks are switched off
     *
     * @return true if batch loading is enabled
     */
    @CheckReturnValue
    boolean isBatchGraph();

    //------------------------------------- Meta Types ----------------------------------
    /**
     * Get the root of all Types.
     *
     * @return The meta type -> type.
     */
    @CheckReturnValue
    Type getMetaConcept();

    /**
     * Get the root of all Relation Types.
     *
     * @return The meta relation type -> relation-type.
     */
    @CheckReturnValue
    RelationType getMetaRelationType();

    /**
     * Get the root of all the Role Types.
     *
     * @return The meta role type -> role-type.
     */
    @CheckReturnValue
    RoleType getMetaRoleType();

    /**
     * Get the root of all the Resource Types.
     *
     * @return The meta resource type -> resource-type.
     */
    @CheckReturnValue
    ResourceType getMetaResourceType();

    /**
     * Get the root of all the Entity Types.
     *
     * @return The meta entity type -> entity-type.
     */
    @CheckReturnValue
    EntityType getMetaEntityType();

    /**
     * Get the root of all Rule Types;
     *
     * @return The meta rule type -> rule-type.
     */
    @CheckReturnValue
    RuleType getMetaRuleType();

    /**
     * Get the root of all inference rules.
     *
     * @return The meta rule -> inference-rule.
     */
    @CheckReturnValue
    RuleType getMetaRuleInference();

    /**
     * Get the root of all constraint rules.
     *
     * @return The meta rule -> constraint-rule.
     */
    @CheckReturnValue
    RuleType getMetaRuleConstraint();

    //------------------------------------- Admin Specific Operations ----------------------------------

    /**
     * Converts a Type Label into a type Id for this specific graph. Mapping labels to ids will differ between graphs
     * so be sure to use the correct graph when performing the mapping.
     *
     * @param label The label to be converted to the id
     * @return The matching type id
     */
    @CheckReturnValue
    TypeId convertToId(TypeLabel label);

    /**
     * Commits to the graph without submitting any commit logs.
     * @return the commit log that would have been submitted if it is needed.
     * @throws InvalidGraphException when the graph does not conform to the object concept
     */
    Optional<String> commitNoLogs() throws InvalidGraphException;

    /**
     * Check if there are duplicate resources in the provided set of vertex IDs
     * @param index index of the resource to find duplicates of
     * @param resourceVertexIds vertex Ids containing potential duplicates
     * @return true if there are duplicate resources and PostProcessing can proceed
     */
    boolean duplicateResourcesExist(String index, Set<ConceptId> resourceVertexIds);

    /**
     * Merges the provided duplicate resources
     *
     * @param resourceVertexIds The resource vertex ids which need to be merged.
     * @return True if a commit is required.
     */
    boolean fixDuplicateResources(String index, Set<ConceptId> resourceVertexIds);

    /**
     * Updates the counts of all the types
     *
     * @param conceptCounts The concepts and the changes to put on their counts
     */
    void updateConceptCounts(Map<ConceptId, Long> conceptCounts);

    /**
     * Creates a new shard for the concept
     * @param conceptId the id of the concept to shard
     */
    void shard(ConceptId conceptId);

    /**
     *
     * @param key The concept property tp search by.
     * @param value The value of the concept
     * @return A concept with the matching key and value
     */
    @CheckReturnValue
    <T extends Concept> T getConcept(Schema.ConceptProperty key, Object value);

    /**
     * Closes the root session this graph stems from. This will automatically rollback any pending transactions.
     */
    void closeSession();

    /**
     * Immediately closes the session and deletes the graph.
     * Should be used with caution as this will invalidate any pending transactions
     */
    void delete();
}
