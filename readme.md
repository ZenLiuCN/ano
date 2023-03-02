# Ano

Helper for make Annotation Processor 


## version strategy 
+ versioned as `major.minor.patch`;
+ major version may contain api changes.
+ minor version may contain api change before reach `1.0.0`, after `1.0.0` there won't contain any api changes.
+ patch version number only contains bug fix or improvements.
## versions
1. `0.1.0`: first release
    + `JavaGenerator` for generate Java sources by annotation processing.
2. `0.2.0`: 
   + added `BaseGenerator` for generate none java sources code;
   + `com.squareup:javapoet` now is `provide` dependency.