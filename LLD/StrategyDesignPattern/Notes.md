### StrategyDesignPattern

#### WithoutStrategyDesignPattern
here we need to provide sports drive capability to Off-road and sports vehicle,
we have to override (same implementation) drive method in both classes, which is code duplication. 

#### WithStrategyDesignPattern
here we have made driveCapability configurable in vehicle class, 
the class which is extending it will pass the object of the required drive capability.