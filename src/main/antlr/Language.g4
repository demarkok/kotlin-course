grammar Language;


file
    :   block EOF
    ;

block
    :   statement*
    ;

blockWithBraces
    :   '{' block '}'
    ;

statement
    :   functionDeclaration
    #   functionDeclarationStatement

    |   variableDeclaration
    #   variableDeclarationStatement

    |   expression
    #   expressionStatement

    |   'while' parExpression blockWithBraces
    #   whileStatement

    |   'if' parExpression blockWithBraces ('else' blockWithBraces)?
    #   ifStatement

    |   assignment
    #   assignmentStatement


    |   'return' expression
    #   returnStatement

    |   'println' '(' arguments? ')'
    #   printlnStatement
    ;

functionDeclaration
    :   'fun' Identifier '(' parameterNames? ')' blockWithBraces
    ;

variableDeclaration
    :   'var' Identifier ('=' expression)?
    ;

parameterNames
    :   Identifier (',' Identifier)*
    ;

assignment
    :   variableAccess '=' expression
    ;

variableAccess
    :   Identifier
    ;

expression
    :   functionCall
    #   functionCallExpression

    |   variableAccess
    #   variableAccessExpression

    |   Literal
    #   literalExpression

    |   parExpression
    #   parenthesesExpression

    |   leftOperand = expression  operator = ('*' | '/' | '%')          rightOperand = expression   # binaryExpression
    |   leftOperand = expression  operator = ('+' | '-')                rightOperand = expression   # binaryExpression
    |   leftOperand = expression  operator = ('<=' | '>=' | '>' | '<')  rightOperand = expression   # binaryExpression
    |   leftOperand = expression  operator = ('==' | '!=')              rightOperand = expression   # binaryExpression
    |   leftOperand = expression  operator = ('+'|'-')                  rightOperand = expression   # binaryExpression
    |   leftOperand = expression  operator = '&&'                       rightOperand = expression   # binaryExpression
    |   leftOperand = expression  operator = '||'                       rightOperand = expression   # binaryExpression
    ;

functionCall
    :   Identifier '(' arguments? ')'
    ;

arguments
    :   expression (',' expression)*
    ;


parExpression
    :   '(' expression ')'
    ;


// Keywords
IF            : 'if';
ELSE          : 'else';
WHILE         : 'while';
FUN           : 'fun';
VAR           : 'var';
RETURN        : 'return';
PRINTLN       : 'println';


LPAREN          : '(';
RPAREN          : ')';
LBRACE          : '{';
RBRACE          : '}';
COMMA           : ',';


ASSIGN          : '=';
GT              : '>';
LT              : '<';
EQUAL           : '==';
LE              : '<=';
GE              : '>=';
NOTEQUAL        : '!=';
AND             : '&&';
OR              : '||';
ADD             : '+';
SUB             : '-';
MUL             : '*';
DIV             : '/';
MOD             : '%';



//  Identifiers
Identifier
    :   Letter LetterOrDigit*
    ;

fragment
Letter
    :   [a-zA-Z$_]
    ;

fragment
LetterOrDigit
    :   [a-zA-Z0-9$_]
    ;


Literal
    :   DecimalNumeral IntegerTypeSuffix?
    ;

fragment
IntegerTypeSuffix
    :   [lL]
    ;

fragment
DecimalNumeral
    :   '0'
    |   NonZeroDigit (Digits? | Underscores Digits)
    ;

fragment
Digits
    :   Digit (DigitOrUnderscore* Digit)?
    ;

fragment
DigitOrUnderscore
    :   Digit
    |   '_'
    ;

fragment
NonZeroDigit
    :   [1-9]
    ;

fragment
Digit
    :   '0'
    |   NonZeroDigit
    ;

fragment
Underscores
    :   '_'+
    ;

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

// Comments
LINE_COMMENT
    :   '//' ~[\r\n]* -> channel(HIDDEN)
    ;

