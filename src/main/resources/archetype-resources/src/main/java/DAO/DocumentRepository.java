#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.DAO;

import ${package}.DAO.entities.DocumentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for document entities
 *
 * Created by talbot on 18.02.15.
 */
public interface DocumentRepository extends MongoRepository<DocumentEntity, String> {
}
