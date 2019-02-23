package smart.management.customer_type

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import smart.management.common.ServerResponse
import smart.management.security.AuthenticationAnnotation

@RestController
@RequestMapping('/customer_type')
class CustomerTypeController {

    @Autowired CustomerTypeService customerTypeService

    @RequestMapping('/all')
    ServerResponse all() {
        return new ServerResponse(content: customerTypeService.findAll().toList(), message: 'query successful')
    }

    @RequestMapping('/delete')
    @AuthenticationAnnotation
    ServerResponse delete(String customerTypeId) {
        return new ServerResponse(content: customerTypeService.deleteById(customerTypeId), message: 'delete successful')
    }

    @RequestMapping('/save')
    @AuthenticationAnnotation
    ServerResponse create(CustomerType customerType) {
        return new ServerResponse(content: customerTypeService.save(customerType), message: 'save successful')
    }

}
