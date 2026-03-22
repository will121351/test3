package com.jspxcms.core.service.impl;

import com.jspxcms.core.domain.Model;
import com.jspxcms.core.domain.Site;
import com.jspxcms.core.service.ModelPortService;
import com.jspxcms.core.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PONY on 2017/7/17.
 */
@Service
public class ModelPortServiceImpl implements ModelPortService {
    public ModelPortServiceImpl(@Autowired ModelService modelService) {
        Assert.notNull(modelService,"modelService is required; it must not be null");
        this.modelService = modelService;
    }

    @XmlRootElement
    private static class Models {
        @XmlElement(name = "model")
        public List<Model> getModels() {
            return models;
        }

        public void setModels(List<Model> models) {
            this.models = models;
        }

        private List<Model> models = new ArrayList<>();
    }

    public List<Model> importModel(Integer siteId, Reader reader) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Models.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        XMLInputFactory xif = XMLInputFactory.newFactory();
        xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        xif.setProperty(XMLInputFactory.SUPPORT_DTD, true);
        try {
            XMLStreamReader xsr = xif.createXMLStreamReader(reader);
            Models models = (Models) unmarshaller.unmarshal(xsr);
            List<Model> modelList = models.getModels();
            modelService.importModels(modelList, siteId);
            return modelList;
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportModel(Site site, Writer writer) throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Models.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        // output pretty printed
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        List<Model> modelList = modelService.findList(site.getId(), null);
        Models xm = new Models();
        xm.setModels(modelList);
        marshaller.marshal(xm, writer);
    }

    private ModelService modelService;
}
