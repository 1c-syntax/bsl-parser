parser grammar BSLMethodDescriptionParser;

options {
    tokenVocab = BSLMethodDescriptionLexer;
    contextSuperClass = 'BSLParserRuleContext';
}

// структура описания
methodDescription: depricate? description? parameters? callOptions? retursValues? examples? EOF;

depricate: SPACE* DEPRICATE_KEYWORD SPACE depricateDescription EOL;
depricateDescription: ~EOL*;

description: descriptionString* EOF?;
descriptionString: (~(PARAMETERS_KEYWORD | RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)+ EOL?) | EOL;

parameters: SPACE* PARAMETERS_KEYWORD SPACE* EOL parametersString* EOF?;
parametersString: (~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)+ EOL?) | EOL;

callOptions: SPACE* CALL_OPTIONS_KEYWORD SPACE* EOL callOptionsString* EOF?;
callOptionsString: (~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | EOL)+ EOL?) | EOL;

retursValues: SPACE* RETURNS_KEYWORD SPACE* EOL retursValuesString* EOF?;
retursValuesString: (~(EXAMPLE_KEYWORD | EOL)+ EOL?) | EOL;

examples: SPACE* EXAMPLE_KEYWORD SPACE* EOL examplesString* EOF?;
examplesString: (~EOL+ EOL?) | EOL;