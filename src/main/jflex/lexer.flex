package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.ParserSym;
import lyc.compiler.model.*;
import static lyc.compiler.constants.Constants.*;

%%

%public
%class Lexer
%unicode
%cup
%line
%column
%throws CompilerException
%eofval{
  return symbol(ParserSym.EOF);
%eofval}


%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}


LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Identation =  [ \t\f]

OpenComment = "/*"
CloseComment = "*/"
Comment = {OpenComment} (.)* {CloseComment}

Plus = "+"
Mult = "*"
Sub = "-"
Div = "/"
Assig = "="

Greater = ">"
Less = "<"

And = "&"
Or = "||"
Not = "not"

OpenBracket = "("
CloseBracket = ")"
OpenCurlyBracket = "{"
CloseCurlyBracket = "}"
OpenSquareBracket = "["
CloseSquareBracket = "]"
Colon = ":"
Comma = ","
Quote = [\"]

Letter = [a-zA-Z]
Digit = [0-9]

Init = "init"
Read = "read"
Write = "write"

While = "while"
If = "if"
Else = "else"

Iguales = "Iguales"
Do = "DO"
Case = "CASE"
Default = "DEFAULT"
Enddo = "ENDDO"

Int = "Int"
Float = "Float"
String = "String"

WhiteSpace = {LineTerminator} | {Identation}
Identifier = {Letter} ({Letter}|{Digit})*
IntegerConstant = {Sub}?{Digit}+
FloatConstant = {Digit}+ "." {Digit} | {Digit}+ "." | "." {Digit}+
StringConstant = {Quote} (.)+ {Quote}

%%


/* keywords */

<YYINITIAL> {
  /* identifiers */
  {Identifier}                              { if(yylength() > MAX_LENGTH){ throw new InvalidLengthException(yytext()); }
                                               else{return symbol(ParserSym.IDENTIFIER, yytext());}}
  /* Constants */
  {IntegerConstant}                         { long number = Long.parseLong(yytext());
                                              if(number > MAX_INT_16 || number < 0){ throw new InvalidIntegerException(yytext()); }
                                                else{return symbol(ParserSym.INTEGER_CONSTANT, yytext());}}

  {FloatConstant}                           { return symbol(ParserSym.FLOAT_CONSTANT, yytext()); }

  {StringConstant}                          { if(yylength() > MAX_LENGTH){ throw new InvalidLengthException(yytext()); }
                                              	else{return symbol(ParserSym.STRING_CONSTANT, yytext());}}


  /* operators */
  {Assig}                                   { return symbol(ParserSym.ASSIG); }
  {Plus}                                    { return symbol(ParserSym.PLUS); }
  {Sub}                                     { return symbol(ParserSym.SUB); }
  {Mult}                                    { return symbol(ParserSym.MULT); }
  {Div}                                     { return symbol(ParserSym.DIV); }

  {Greater}                                 { return symbol(ParserSym.GREATER); }
  {Less}                                    { return symbol(ParserSym.LESS); }

  {And}                                     { return symbol(ParserSym.AND); }
  {Or}                                      { return symbol(ParserSym.OR); }
  {Not}                                     { return symbol(ParserSym.NOT); }

  {OpenBracket}                             { return symbol(ParserSym.OPEN_BRACKET); }
  {CloseBracket}                            { return symbol(ParserSym.CLOSE_BRACKET); }
  {OpenCurlyBracket}                        { return symbol(ParserSym.OPEN_CURLY_BRACKET); }
  {CloseCurlyBracket}                       { return symbol(ParserSym.CLOSE_CURLY_BRACKET); }
  {OpenSquareBracket}                       { return symbol(ParserSym.OPEN_SQUARE_BRACKET); }
  {CloseSquareBracket}                      { return symbol(ParserSym.CLOSE_SQUARE_BRACKET); }
  {Colon}                                   { return symbol(ParserSym.COLON); }
  {Comma}                                   { return symbol(ParserSym.COMMA); }
  {Quote}                                   { return symbol(ParserSym.QUOTE); }

  {Init}                                    { return symbol(ParserSym.INIT); }
  {Read}                                    { return symbol(ParserSym.READ); }
  {Write}                                   { return symbol(ParserSym.WRITE); }

  {While}                                   { return symbol(ParserSym.WHILE); }
  {If}                                      { return symbol(ParserSym.IF); }
  {Else}                                    { return symbol(ParserSym.ELSE); }

  {Iguales}                                 { return symbol(ParserSym.IGUALES); }
  {Do}                                      { return symbol(ParserSym.DO); }
  {Case}                                    { return symbol(ParserSym.CASE); }
  {Default}                                 { return symbol(ParserSym.DEFAULT); }
  {Enddo}                                   { return symbol(ParserSym.ENDDO); }

  {Int}                                     { return symbol(ParserSym.INT); }
  {Float}                                   { return symbol(ParserSym.FLOAT); }
  {String}                                  { return symbol(ParserSym.STRING); }


  /* whitespace */
  {WhiteSpace}                              { /* ignore */ }
  {Comment}                                 { /* ignore */ }

}


/* error fallback */
[^]                                         { throw new UnknownCharacterException(yytext()); }
