#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.DAO.entities;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * Document entity
 *
 * Created by talbot on 18.02.15.
 */
@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@Document(collection = "documents")
public class DocumentEntity {

    @ToString
    @EqualsAndHashCode
    @Getter
    @Setter
    @NoArgsConstructor
    @XmlRootElement(name = "document")
    public static final class XmlDocument {

        @Getter
        @Setter
        @ToString
        @NoArgsConstructor
        @EqualsAndHashCode
        public static class PropertyEntity {

            @Field("name")
            private String name;

            @Field("property")
            private List<PropertyEntity> propertyEntities;

            @XmlAttribute(name = "name")
            public void setName(final String name) {
                this.name = name;
            }

            @XmlElement(name = "property")
            public void setPropertyEntities(final List<PropertyEntity> propertyEntities) {
                this.propertyEntities = propertyEntities;
            }
        }

        @Field("property")
        private List<PropertyEntity> properties;

        @XmlElement(name = "property")
        public void setProperties(final List<PropertyEntity> properties) {
            this.properties = properties;
        }
    }

    @Id
    private String id;

    @CreatedDate
    private Date createdAt = new Date();

    private String fileName;

    @Field("document")
    private XmlDocument document;
}
