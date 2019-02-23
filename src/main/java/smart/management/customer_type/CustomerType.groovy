package smart.management.customer_type

import smart.management.common.BaseDocument

import javax.validation.constraints.NotNull

class CustomerType extends BaseDocument {

    @NotNull String name
    @NotNull String description

}
