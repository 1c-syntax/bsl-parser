/**
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2026
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Fedkin <nixel2007@gmail.com>, Sergey Batanov <sergey.batanov@dmpas.ru>
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * BSL Parser is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * BSL Parser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BSL Parser.
 */
/**
 * @author Maximov Valery <maximovvalery@gmail.com>
 */
lexer grammar BSLDescriptionLexer;

options { caseInsensitive=true; }

// KEYWORDS
PARAMETERS_KEYWORD:     'PARAMETERS:'                   | 'ПАРАМЕТРЫ:';
RETURNS_KEYWORD:        'RETURNS:'                      | 'ВОЗВРАЩАЕМОЕ ЗНАЧЕНИЕ:';
EXAMPLE_KEYWORD:        'EXAMPLE:'  | 'EXAMPLES:'       | 'ПРИМЕР:'     | 'ПРИМЕРЫ:';
CALL_OPTIONS_KEYWORD:   'CALL OPTIONS:'                 | 'ВАРИАНТЫ ВЫЗОВА:';
DEPRECATE_KEYWORD:      'DEPRECATE' | 'DEPRECATE.'      | 'УСТАРЕЛА'    | 'УСТАРЕЛА.';
SEE_KEYWORD:            'SEE'                           | 'СМ.';
OF_KEYWORD:             'OF'                            | 'CONTAINS'    | 'ИЗ';

// COMMON
EOL     : '\r'? '\n';
SPACE   : [ \t]+;
STAR    : '*'+;
DASH    : [-–];
COLON   : ':';
COMMA   : ',';
// OTHER
COMMENT : '//';
WORD    : LETTER (LETTER | DIGIT)*;
DOTSWORD: WORD ('.' WORD)+;
LPAREN  : '(';
RPAREN  : ')';

ANYSYMBOL: .;

// LITERALS
fragment DIGIT: [0-9];
fragment LETTER: [\p{Letter}] | '_';
