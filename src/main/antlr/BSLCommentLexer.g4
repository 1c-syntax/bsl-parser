/**
 * This file is a part of BSL Parser.
 *
 * Copyright © 2018-2020
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com>, Sergey Batanov <sergey.batanov@dmpas.ru>
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
lexer grammar BSLCommentLexer;

@members {
public BSLCommentLexer(CharStream input, boolean crAwareCostructor) {
  super(input);
  _interp = new CRAwareLexerATNSimulator(this, _ATN);
  validateInputStream(_ATN, input);
}
}

//COMMENT_START: '//';

COMMENT_MINUS: '-';
COMMENT_COMMA: ',';
COMMENT_MUL: '*';
COMMENT_MULTIMUL: '**';
COMMENT_WHITE_SPACE: [ \t\f]+ -> channel(HIDDEN);
COMMENT_PARAMETERS : 'ПАРАМЕТРЫ:' | 'PARAMETERS:';
COMMENT_RETURNS: 'ВОЗВРАЩАЕМОЕ ЗНАЧЕНИЕ:' | 'RETURNS:';
COMMENT_EXAMPLE: 'EXAMPLE:' | 'ПРИМЕР:';
COMMENT_CONTAINS : 'ИЗ' | 'CONTAINS';
COMMENT_NEWLINE: [\r\n] -> channel(HIDDEN);
COMMENT_STRING : (~[ ,'-'\t\f\n\r])+;
