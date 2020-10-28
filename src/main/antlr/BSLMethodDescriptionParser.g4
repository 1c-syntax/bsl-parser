parser grammar BSLMethodDescriptionParser;

options {
    tokenVocab = BSLMethodDescriptionLexer;
    contextSuperClass = 'BSLParserRuleContext';
}

// структура описания
methodDescription: depricate? description parameters? callOptions? retursValues? examples? EOF;

depricate: SPACE* DEPRICATE_KEYWORD SPACE depricateDescription;
depricateDescription: ~EOL* EOL;

description: descriptionString*;
descriptionString: ~(PARAMETERS_KEYWORD | RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)* EOL;

parameters: SPACE* PARAMETERS_KEYWORD SPACE* EOL parametersString*;
parametersString: ~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)* EOL;

callOptions: SPACE* CALL_OPTIONS_KEYWORD SPACE* EOL callOptionsString*;
callOptionsString: ~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | EOL)* EOL;

retursValues: SPACE* RETURNS_KEYWORD SPACE* EOL retursValuesString*;
retursValuesString: ~(EXAMPLE_KEYWORD | EOL)* EOL;

examples: SPACE* EXAMPLE_KEYWORD SPACE* EOL examplesString*;
examplesString: ~EOL* EOL;