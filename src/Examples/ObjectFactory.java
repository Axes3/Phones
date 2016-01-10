//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-548 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.12.05 at 11:04:38 AM GMT 
//


package Examples;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the Examples package. 
 * <p>An ObjectFactory allows you to programmatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Myphonelibrary_QNAME = new QName("", "myphonelibrary");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: Examples
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PhoneLibrary }
     * 
     */
    public PhoneLibrary createPhoneLibrary() {
        return new PhoneLibrary();
    }

    /**
     * Create an instance of {@link Customer }
     * 
     */
    public Customer createCustomer() {
        return new Customer();
    }

    /**
     * Create an instance of {@link Catalog }
     * 
     */


    /**
     * Create an instance of {@link Stock }
     * 
     */
    public Stock createStock() {
        return new Stock();
    }

    /**
     * Create an instance of {@link Phone }
     * 
     */
    public Phone createPhone() {
        return new Phone();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PhoneLibrary }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "myphonelibrary")
    public JAXBElement<PhoneLibrary> createMyphonelibrary(PhoneLibrary value) {
        return new JAXBElement<PhoneLibrary>(_Myphonelibrary_QNAME, PhoneLibrary.class, null, value);
    }

}
