package org.crygier.graphql

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.persistence.EntityManager

@Configuration
@ContextConfiguration(loader = SpringBootContextLoader, classes = TestApplication)
class EmbeddedSchemaBuildTest extends Specification {

    @Autowired
    private EntityManager entityManager;

    private GraphQLSchemaBuilder builder;

    void setup() {
        builder = new GraphQLSchemaBuilder(entityManager);
    }

    def 'Correctly read embedded keys'() {
        when:
        def embeddingEntity = entityManager.getMetamodel().getEntities().stream().filter { e -> e.name == "EmbeddingTest"}.findFirst().get()
        def graphQlObject = builder.getGraphQLOutputType(embeddingEntity)

        then:
        graphQlObject.fieldDefinitions.size() == 1
    }

    def 'Correctly extract embedded basic query fields'() {
        when:
        def embeddingEntity = entityManager.getMetamodel().getEntities().stream().filter { e -> e.name == "EmbeddingTest"}.findFirst().get()
        def graphQlFieldDefinition = builder.getQueryFieldDefinition(embeddingEntity)

        then:
        graphQlFieldDefinition.arguments.size() == 0
    }

    def 'Correctly extract a whole moddel with embeddings'() {
        when:
        def q = builder.getQueryType()

        then:
        true
    }

}
