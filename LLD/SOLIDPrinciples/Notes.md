## SOLID Principles

### Single Responsibility

#### WithoutSingleResponsibility
here in this Invoice class have three responsibility **calculateInvoice, printInvoice and saveDBInInvoice**
if in future we need to change in any method, we again need to test whole class, which violates maintainability principle


#### SingleResponsibility
here we have segregated responsibility in different classes, if we make change in one class, we do not need to test other class.