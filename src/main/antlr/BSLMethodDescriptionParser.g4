parser grammar BSLMethodDescriptionParser;

options {
    tokenVocab = BSLMethodDescriptionLexer;
    contextSuperClass = 'BSLParserRuleContext';
}

// структура описания
methodDescription: deprecate? description? parameters? callOptions? retursValues? examples? EOF;

deprecate: DEPRECATE_KEYWORD deprecateDescription? (EOL+ | EOF);
deprecateDescription: ~EOL+;

description: descriptionString+;
descriptionString: ~(PARAMETERS_KEYWORD | RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)+ (EOL+ | EOF);

parameters: PARAMETERS_KEYWORD ((EOL parametersString*) | EOF);
parametersString: ~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | CALL_OPTIONS_KEYWORD | EOL)+ (EOL+ | EOF);

callOptions: CALL_OPTIONS_KEYWORD ((EOL callOptionsString*) | EOF);
callOptionsString: ~(RETURNS_KEYWORD | EXAMPLE_KEYWORD | EOL)+ (EOL+ | EOF);

retursValues: RETURNS_KEYWORD ((EOL retursValuesString*) | EOF);
retursValuesString: ~(EXAMPLE_KEYWORD | EOL)+ (EOL+ | EOF);

examples: EXAMPLE_KEYWORD ((EOL examplesString*) | EOF);
examplesString: ~EOL+ (EOL+ | EOF);
