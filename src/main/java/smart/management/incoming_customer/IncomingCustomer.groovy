package smart.management.incoming_customer

import smart.management.common.BaseDocument

import javax.validation.constraints.NotNull

class IncomingCustomer extends BaseDocument {

    @NotNull List<String> visibilityUserGroupIds = [:]

    @NotNull String name
    @NotNull String income
    @NotNull String cost

}
