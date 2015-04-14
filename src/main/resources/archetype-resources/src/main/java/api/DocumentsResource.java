#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.api;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import ${package}.DAO.DocumentRepository;
import ${package}.DAO.entities.DocumentEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.util.List;

/**
 * Spring REST controller for "documents" resource
 *
 * Created by talbot on 14.02.15.
 */
@RestController
@RequestMapping("/api/documents")
@Api("documents")
@Slf4j
public class DocumentsResource {

    @Autowired
    private DocumentRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(
            value = "/{documentId}",
            method = RequestMethod.GET
    )
    @ApiOperation(
            value = "Retrieves XML document file by its string identifier",
            response = String.class,
            produces = MediaType.APPLICATION_XML_VALUE,
            httpMethod = "GET"
    )
    public void getDocumentById(@ApiParam(name = "Document object ID") @PathVariable final String documentId, final HttpServletResponse response) throws IOException {
        final DocumentEntity entity = this.repository.findOne(documentId);
        if (entity == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=" + entity.getFileName());
        entity.setCreatedAt(null);
        entity.setFileName(null);
        entity.setId(null);
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(DocumentEntity.class);
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(entity.getDocument(), response.getOutputStream());
            response.flushBuffer();
        } catch (final JAXBException | IOException e) {
            log.warn(e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    @ApiOperation(
            value = "Retrieves list of all XML documents",
            response = List.class,
            produces = MediaType.APPLICATION_JSON_VALUE,
            httpMethod = "GET"
    )
    public List<DocumentEntity> getDocumentList() {
        final Query query = new Query();
        query.fields().include("id").include("createdAt").include("fileName");
        return this.mongoTemplate.find(query, DocumentEntity.class);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    @ApiOperation(
            value = "Creates XML document on server by uploaded XML file, returns newly created document",
            response = DocumentEntity.class,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            httpMethod = "POST"
    )
    public DocumentEntity uploadNewDocument(@ApiParam(name = "Document file", value = "file") @RequestParam("file") final MultipartFile file) throws IOException {
        final DocumentEntity documentEntity = new DocumentEntity();
        if (file != null && !file.isEmpty()) {
            try {
                final JAXBContext jaxbContext = JAXBContext.newInstance(DocumentEntity.class);
                final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                documentEntity.setDocument((DocumentEntity.XmlDocument) unmarshaller.unmarshal(file.getInputStream()));
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
                return documentEntity;
            } catch (JAXBException e) {
                log.error(e.getMessage(), e);
                return documentEntity;
            }
        } else {
            log.warn("Trying to upload empty file");
            return null;
        }
        if (documentEntity.getDocument() != null && this.repository != null) {
            documentEntity.setFileName(
                    file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty() ?
                            file.getOriginalFilename() :
                            "untitled.xml"
            );
            this.repository.save(documentEntity);
            documentEntity.setDocument(null);
        }
        return documentEntity;
    }

    @RequestMapping(
            value = "/{documentId}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation(
            value = "Deletes XML document by its string identifier",
            httpMethod = "DELETE"
    )
    public void delete(@ApiParam(name = "Document object ID") @PathVariable("documentId") final String documentId, final HttpServletResponse response) {
        try {
            this.repository.delete(documentId);
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
