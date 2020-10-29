parser grammar BSLMethodDescriptionParser;

options {
    tokenVocab = BSLMethodDescriptionLexer;
    contextSuperClass = 'BSLParserRuleContext';
}

// структура описания
methodDescription: depricate? description parameters? callOptions? retursValues? examples? EOF?;

depricate: SPACE* DEPRICATE_KEYWORD SPACE depricateDescription;
depricateDescription: ~EOL* eo;

description: descriptionString*;
descriptionString: ~(PARAMETERS_KEYWORD | RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)* eo;

parameters: SPACE* PARAMETERS_KEYWORD SPACE* EOL parametersString*;
parametersString: ~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)* eo;

callOptions: SPACE* CALL_OPTIONS_KEYWORD SPACE* EOL callOptionsString*;
callOptionsString: ~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | EOL)* eo;

retursValues: SPACE* RETURNS_KEYWORD SPACE* EOL retursValuesString*;
retursValuesString: ~(EXAMPLE_KEYWORD | EOL)* eo;

examples: SPACE* EXAMPLE_KEYWORD SPACE* EOL examplesString*;
examplesString: ~EOL* eo;

eo: (EOL | EOF);