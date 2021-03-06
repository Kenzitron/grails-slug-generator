Grails-slug-generator
=====================

This plugin generates unique slugs for String properties. Its main use case is to generate unique and nice names for domain instances that can be used in URLs, such as `/user/show/<slug>`.

For example, instead of having a URL like http://www.domain.com/user/25 with a number identifying the user, you can generate a unique URL based on the user's name: http://www.domain.com/user/ivan-lopez (from name Iván López).

Usage
-----

The plugin provides a Grails Service, `slugGeneratorService`, that can be injected into any artefact of your Grails application. The service has only one method, `generateSlug(Class domainClass, String property, String value)`, which is used to generate the unique slugs.

How does it work? The method first generates a slug from the given value and then checks whether that slug is already used by another domain instance of the given type. If the slug is unique, then the method returns that value. Otherwise, it appends a number and tries again. This is repeated until a unique slug is found and returned.

The typical use case is to automatically set the slug when inserting a new domain object or when updating an existing one whose source property (for example 'name') has changed. Here's a concrete example:

``` groovy
class Dummy {
    
    def slugGeneratorService
    
    String name
    String slug = ""
    
    def beforeInsert() {
        this.slug = slugGeneratorService.generateSlug(this.class, "slug", name)
    }

    def beforeUpdate() {
        if (isDirty('name')) {
            this.slug = slugGeneratorService.generateSlug(this.class, "slug", name)
        }
    }
}
```

With the above code, you get the following behavior when using the `Dummy` domain class:
``` groovy
def dummy = new Dummy(name:"Iván López").save()
assert dummy.slug == "ivan-lopez"

dummy.name = "Another name!!"
dummy.save(flush:true)
def dummyUpdated = Dummy.get(1)
assert dummyUpdated.slug == "another-name"

def dummy2 = new Dummy(name:"Iván López").save()
assert dummy2.slug == "ivan-lopez-1"
```

The plugin supports full UTF-8, so you can use, for instance, ciryllic chars or right-to-left writing. Check out [the tests](https://github.com/lmivan/grails-slug-generator/blob/master/test/integration/grails/plugins/SlugGeneratorTests.groovy) for more examples.

Additional codec
----------------

The plugin also includes a SlugCodec that you can call like the other Grails builtin codecs:

``` groovy
assert "ivan-lopez" == "Iván López!!".encodeAsSlug()
```

