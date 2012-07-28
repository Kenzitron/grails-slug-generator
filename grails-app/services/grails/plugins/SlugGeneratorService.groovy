package grails.plugins

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class SlugGeneratorService {

    static transactional = false
    
    def grailsApplication
    
    /**
     * Generate a unique slug for the property in the domain class based on the value. All the diacritical
     * marks are removed from the value before generate the slug and then, if the slug exists in the database
     * a counter is added.
     * 
     * @param theClazz The domain class
     * @param property The property of the domain class
     * @param value The value from which generate the slug
     * @return The unique slug generated or null if there is an error
     */
    public String generateSlug(Class theClazz, String property, String value) {
        
        // Check if the class if a DomainClass
        if (!grailsApplication.isArtefactOfType("Domain", theClazz)) {
            return null
        }
        
        // Get the GrailsDomainClass related to the class
        def grailsClass = new DefaultGrailsDomainClass(theClazz)
        
        // Check if the class has the property
        def persistentProperty = grailsClass.getPersistentProperty(property)
        if (!persistentProperty) {
            return null
        }
        
        // Generate the initial slug and look for it 
        def initialSlug = SlugCodec.encode(value)
        def instance = theClazz."findBy${property.capitalize()}"(initialSlug)
        if (!instance) {
            return initialSlug
        }
        
        int c = 1
        def slug = ""
        while (true) {
            slug = "${initialSlug}-${c}"
            instance = theClazz."findBy${property.capitalize()}"(slug)
            if (!instance) {
                return slug
            } else {
                c++
            }
        }
    }
    
//    /**
//     * Removes diacritical marks from string:
//     * assert 'Ivan' == removeDiacriticalMarks('Iván')
//     * 
//     * @param string The string to remove the diacritical marks
//     */
//    private String removeDiacriticalMarks(String string) {
//        Pattern p = Pattern.compile("\\p{InCombiningDiacriticalMarks}+", Pattern.UNICODE_CASE);
//
//        return Normalizer.normalize(string, Normalizer.Form.NFD).replaceAll(p, "");
//    }
}