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
parametersString: ~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)+ EOL*;

callOptions: SPACE* CALL_OPTIONS_KEYWORD (EOL callOptionsString*)?;
callOptionsString: ~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | EOL)+ EOL*;

retursValues: SPACE* RETURNS_KEYWORD (EOL retursValuesString*)?;
retursValuesString: ~(EXAMPLE_KEYWORD | EOL)+ EOL*;

examples: SPACE* EXAMPLE_KEYWORD (EOL examplesString*)?;
examplesString: ~EOL+ EOL*;
