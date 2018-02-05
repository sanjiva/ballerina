package lang.annotations.doc;

@Description{value:"Self annotating an annotation",
                 paramValue:@Param{value:"some parameter value"},
                 queryParamValue:[@QueryParam{
                    name:"first query param name", 
                    value:"first query param value"}],
                 queryParamValue2:[@QueryParam{}],
                 code:[7,8,9],
                 args: @Args{}}
annotation Description attach service, resource, function, parameter, annotation, connector, action, typemapper, struct, const {
    string value = "Description of the service/function";
    int[] code;
    Param paramValue;
    QueryParam[] queryParamValue;
    QueryParam[] queryParamValue2;
    string[] paramValue2;
    Args args;
}

annotation Param attach service, function, connector {
    string value = "Description of the input param";
}

annotation QueryParam attach service {
    string name = "default name";
    string value = "default value";
}

annotation Doc attach service, function, connector {
    Description des;
}

annotation Args attach parameter {
    string value = "default value for 'Args' annotation in doc package";
}