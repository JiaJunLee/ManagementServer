package smart.management.customer_type

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Service
import smart.management.common.BaseService

@Service
class CustomerTypeService extends BaseService<CustomerType, String> {

    @Autowired CustomerTypeRepository customerTypeRepository

    @Override
    PagingAndSortingRepository<CustomerType, String> getRepository() {
        return customerTypeRepository
    }

}
