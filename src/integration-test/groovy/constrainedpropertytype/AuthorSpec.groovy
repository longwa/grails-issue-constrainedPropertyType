package constrainedpropertytype

import grails.core.GrailsApplication
import grails.gorm.transactions.Rollback
import grails.gorm.validation.PersistentEntityValidator
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

@Integration
@Rollback
class AuthorSpec extends Specification {
    GrailsApplication grailsApplication

    void "constrained property type for to-one association is the actual object type"() {
        def a = new Author(publisher: new Publisher(name: 'name'))
        a.save(flush: true, failOnError: true)

        when:
        PersistentEntityValidator validator = grailsApplication.mappingContext.getEntityValidator(Author.gormPersistentEntity) as PersistentEntityValidator
        def constraints = validator.getConstrainedProperties()

        then:
        constraints['publisher'].propertyType == Publisher

        and:
        a.publisher.name
    }

    void "proxy testing"() {
        def id = Author.withNewSession {
            def a = new Author(publisher: new Publisher(name: 'name'))
            a.save(flush: true, failOnError: true)
            a.id
        }

        when:
        Author author = Author.get(id)

        then:
        author.publisher.name
    }
}
