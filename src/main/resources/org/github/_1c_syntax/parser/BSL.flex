package org.github._1c_syntax.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

import static org.github._1c_syntax.parser.psi.BSLTypes.*;

%%

%public
%class BSLLexer
%implements FlexLexer
%unicode
%ignorecase
%function advance
%type IElementType
%eof{   return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \n\t\f]
LINE_COMMENT=("//")[^\r\n]*
LETTER=[:letter:] | "_"
DIGIT=[:digit:]
FLOAT_EXPONENT = [eE] [+-]? {DIGIT}+
NUM_FLOAT = ( ( ({DIGIT}+ "." {DIGIT}*) | ({DIGIT}* "." {DIGIT}+) ) {FLOAT_EXPONENT}?) | ({DIGIT}+ {FLOAT_EXPONENT})
IDENTIFIER = {LETTER} ( {LETTER} | {DIGIT} )*
SUB_WORD="процедура"|"функция"|"procedure"|"function"
ENDSUB_KEYWORD="endprocedure"|"endfunction"|"конецпроцедуры"|"конецфункции"
EXPORT_KEYWORD="экспорт"|"export"
BYVAL_KEYWORD="знач"|"val"
TRUE="true"|"истина"
FALSE="false"|"ложь"
UNDEFINED="undefined"|"неопределено"
NULL="null"
QUOTE="\""
SQUOTE="'"
BAR="|"
STRINGSTART= {QUOTE}([^\"\n\r])*
STRING= {QUOTE}([^\"\n\r])*{QUOTE}
STRINGTAIL= {BAR}([^\"\n\r])*{QUOTE}
STRINGPART= {BAR}([^\"\n\r])*
DATETIME= {SQUOTE}([^\'\n\r])*{SQUOTE}?

%state WAIT_IDENTIFIER

%%

<YYINITIAL> {
{LINE_COMMENT}                            { return COMMENT; }
"."                                       { yybegin(WAIT_IDENTIFIER); return DOT; }

"["                                       { return LBRACK; }
"]"                                       { return RBRACK; }

"("                                       { return LPAREN; }
")"                                       { return RPAREN; }

":"                                       { return COLON; }
";"                                       { return SEMICOLON; }
","                                       { return COMMA; }

"="                                       { return ASSIGN; }

"+"                                       { return PLUS; }

"-"                                       { return MINUS; }

"<="                                      { return LESS_OR_EQUAL; }
"<>"                                      { return NOT_EQUAL; }
"<"                                       { return LESS; }

"*"                                       { return MUL; }

"/"                                       { return QUOTIENT; }

"%"                                       { return MODULO; }

">="                                      { return GREATER_OR_EQUAL; }
">"                                       { return GREATER; }

"?"                                       { return QUESTION; }

{WHITE_SPACE} { return TokenType.WHITE_SPACE;}
{TRUE} { return BOOLEAN_TRUE; }
{FALSE} { return BOOLEAN_FALSE; }
{UNDEFINED} { return UNDEFINED; }
{NULL} { return NULL; }
{STRINGSTART} { return STRINGSTART; }
{STRING} { return STRING; }
{STRINGTAIL} { return STRINGTAIL; }
{STRINGPART} { return STRINGPART; }
{DATETIME} { return DATETIME; }
{SUB_WORD} { return SUB_KEYWORD; }
{EXPORT_KEYWORD} { return EXPORT_KEYWORD; }
{BYVAL_KEYWORD} { return BYVAL_KEYWORD; }
{ENDSUB_KEYWORD} { return ENDSUB_KEYWORD; }
"конецесли"|"endif" { return ENDIF_KEYWORD;}
"конеццикла"|"enddo" { return ENDDO_KEYWORD; }
"если"|"if" { return IF_KEYWORD; }
"иначеесли"|"elsif" { return ELSEIF_KEYWORD; }
"иначе"|"else" { return ELSE_KEYWORD; }
"тогда"|"then" { return THEN_KEYWORD; }
"пока"|"while" { return WHILE_KEYWORD; }
"цикл"|"do" { return DO_KEYWORD; }
"для"|"for" { return FOR_KEYWORD; }
"по"|"to" { return TO_KEYWORD; }
"каждого"|"each" { return EACH_KEYWORD; }
"из"|"from" { return FROM_KEYWORD; }
"попытка"|"try" { return TRY_KEYWORD; }
"исключение"|"except" { return EXCEPT_KEYWORD; }
"конецпопытки"|"endtry" { return ENDTRY_KEYWORD; }
"возврат"|"return" { return RETURN_KEYWORD; }
"продолжить"|"continue" { return CONTINUE_KEYWORD; }
"вызватьисключение"|"raise" { return RAISE_KEYWORD; }
"перем"|"var" { return VAR_KEYWORD; }
"не"|"not" { return NOT_KEYWORD; }
"или"|"or" { return OR_KEYWORD; }
"и"|"and" { return AND_KEYWORD; }
"новый"|"new" { return NEW_KEYWORD; }
"&"{IDENTIFIER}? { return COMPILER_DIRECTIVE; }
"#"("use"|"использовать")[^\r\n]* { return USING; }
"#!"[^\r\n]* { return SHEBANG; }
"#"[^\r\n]* { return PREPROCESSOR; }

{IDENTIFIER}                              { return IDENTIFIER; }
{NUM_FLOAT}                               { return FLOAT; }
{DIGIT}+                                  { return DECIMAL; }
}

<WAIT_IDENTIFIER> {
{IDENTIFIER}  { yybegin(YYINITIAL); return IDENTIFIER; }
{WHITE_SPACE} { return TokenType.WHITE_SPACE; }
.             { return TokenType.BAD_CHARACTER; }
}
