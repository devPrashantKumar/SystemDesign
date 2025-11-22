## SOLID Principles

### Single Responsibility

#### WithoutSingleResponsibility
here in this Invoice class have three responsibility **calculateInvoice, printInvoice and saveDBInInvoice**
if in future we need to change in any method, we again need to test whole class, which violates maintainability principle

#### SingleResponsibility
here we have segregated responsibility in different classes, if we make change in one class, we do not need to test other class.

<hr>

### Open/closed Principle

#### WithoutOpenClosedPrinciple
here in this if we want to provide support of calculate are to other shape , we need to make change in existing calculate area,
which violates closed for modification principle.

#### OpenClosedPrinciple
here in this if we want to provide support of calculate area to other shape , we only need to implement Shape interface,
we don't need to make any change in AreaCalculator

<hr>

### Interface Segregation Principle

#### WithoutInterfaceSegregationPrinciple
here in this robot client is forced to implement eat behavior, although robot do no support eat behavior

#### InterfaceSegregationPrinciple
here in this we have broken "Worker" interface into smaller "Eatable and Workable" interface,
now robot is not forced to implement eat behaviour.

<hr>

### Dependency Inversion Principle

#### WithoutDependencyInversionPrinciple
here in this High level datastore in dependent on low level module MySDLDatabase, 
in future if we want to use different database, we need to make changes in datastore.    

#### DependencyInversionPrinciple
here in this High level module **DataStore** and low level module **MySQLDatabase** both are depends on abstraction **Database**