parser grammar BSLMethodDescriptionParser;

options {
    tokenVocab = BSLMethodDescriptionLexer;
    contextSuperClass = 'BSLParserRuleContext';
}

// структура описания
methodDescription: deprecate? description? parameters? callOptions? retursValues? examples? EOF;

deprecate: SPACE* DEPRECATE_KEYWORD deprecateDescription? EOL?;
deprecateDescription: ~EOL+;

description: EOL* descriptionString+;
descriptionString: ~(PARAMETERS_KEYWORD | RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)+ EOL*;

parameters: SPACE* PARAMETERS_KEYWORD (EOL parametersString*)?;
parametersString:
    parameterString
    | subParameterString
    | typeWithDescription
    | typeWithoutDescription
    | (~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)+ EOL*)
;
parameterString: SPACE* parameterName (typeWithDescription | typeWithoutDescription);
subParameterString: SPACE* starPreffix SPACE* parameterName (typeWithDescription | typeWithoutDescription);
parameterName: WORD;

callOptions: SPACE* CALL_OPTIONS_KEYWORD (EOL callOptionsString*)?;
callOptionsString: ~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | EOL)+ EOL*;

retursValues: SPACE* RETURNS_KEYWORD (EOL retursValuesString*)?;
retursValuesString:
    retursValueString
    | typeWithDescription
    | typeWithoutDescription
    | (~(EXAMPLE_KEYWORD | EOL)+ EOL*);
retursValueString: SPACE* types spitter typeDescription;

examples: SPACE* EXAMPLE_KEYWORD (EOL examplesString*)?;
examplesString: ~EOL+ EOL*;

typeWithDescription: spitter types spitter typeDescription;
typeWithoutDescription: spitter types spitter? EOL*;
typeDescription: ~EOL+ EOL*;

spitter: SPACE* DASH SPACE*;
starPreffix: STAR+;
types:
    HYPERLINK
    | COMPLEX_TYPE
    | ((WORD | DOTSWORD) COLON)
    | ((WORD | DOTSWORD) (COMMA SPACE* (WORD | DOTSWORD))*)
    ;