# JLISP - A LISP interpreter and REPL

## Introduction

JLISP is a LISP interpreter and REPL (Read-Evaluate-Print-Loop), implemented in Java.

I authored JLISP primarily as a learning experience: I've wanted to learn LISP since I was 18 or
so, and have been interested in interpreters and compilers for almost as long. But until recently,
I've never come to grips with either. My formal CS training is minimal -- a few 100-level classes --
and my practical experience otherwise never involved developing or implementing even a small language.

In the past few years, I have learned a little FORTH -- another old, deceptively simple-looking
language -- and developed a primitive interpreter and REPL for it. Now I've set that aside for a
while and trying LISP.

## The Interpreter

JLISP's interpreter has three phases:
1. Lexing, a.k.a. scanning: In this phase the interpreter reads the raw program text and converts it
to an ordered list of *tokens*, skipping over comments and insignificant whitespace. (An example of
significant whitespace is space characters in a string literal, such as "Hello World".) Tokens can
be keywords, functions, symbols or literals. The Lexer (in the org.ulithi.jlisp.lexer package)
performs very little semantic processing: it is essentially producing groups of character data for
the next phase -- parsing -- to assign meaning to.
2. Parsing: In this phase, the tokenized program text produced by the Lexer is ingested by the Parser
(in the org.ulithi.jlisp.parser package), which then produces the "parse tree" -- a.k.a. abstract
syntax tree or AST -- for the program. The JLISP parse tree is a classic LISP "dotted-pair" structure.
More on this later.
3. Evaluation: Finally, after the program text has been tokenized, parsed and a valid parse tree
generated, the program expression(s) are evaluated by recursively walking and evaluating the parse
tree nodes. This process produces an S-Expression representing the output of the evaluation.

## Conceptual Model(s)

There are two layers to JLISP's representation of a LISP expression: the physical memory model, and
the conceptual language model.

The memory model describes the in-memory structure -- the AST essentially -- that the Parser generates.
JLISP builds the parse tree, or AST, from simple CONS cells, which can be presented as a *dotted pair*.
A CONS cell consists of two fields: each field is a pointer (or, in JLISP, a reference: Java doesn't
have pointers). The first field/pointer is the CAR of the CONS cell, the second pointer is the CDR.
If we were drawing a cell, it would look like a rectangle on its side, split into two boxes -- one
for each field -- with pointers coming from each. In text, it is much easier to represent a cell as
in dotted-pair notation like this: `(4 . NIL)`. This is a cell whose CAR is the numeric literal 4,
and whose CDR is the special value NIL: think if it as a null pointer or reference. Simple LISP
expressions are stored in memory as linked lists of cells. For example `(+ 1 2)` would be stored as
`(+ *)-->(1 *)-->(2 NIL)` (where * represents a pointer to the next cell in the list). In dotted-pair
notation, this is represented as `(+ . (1 . (2 . NIL)))`.

The conceptual language model describes, well, the lists -- or, more precisely, the S-Expressions
(Symbolic Expressions) that LISP syntax builds on. S-Expressions (sexprs, or sexps) are either
*atoms* -- numeric and alphanumeric literals, and symbols like variable and function names -- or
lists of atoms and sexprs:
```
s_expression = atomic_symbol | "(" s_expression "." s_expression ")" | list
list = "(" s_expression [s_expression] ")"
```
At the moment, JLISP is unable to parse the dotted-pair version of a sexpr as program text, but it
does use dotted-pair notation when serialized a parse tree as a String. I.e., it can output it, but
not yet accept it as input.

So, how do these two conceptual worlds come together?

A cell field can be one of several things:
1. A reference to an atom.
2. A reference to a list.
3. A reference to another cell.
4. NIL (which is a special case of being both an atom and a list).

This is where it gets a touch messy. Atoms and lists are part of the language model. Cells are not:
they're part of the memory model. At first, I made them all sexprs (i.e., I had them all inherit
from SExpression, but cells are *not* sexprs. But, in the memory model, atoms, lists and cells
are all things that can be referred to from cell fields: they are all *references*.

So, in JLISP, a cell is a pair of Refs, and Atom, List and Cell classes all inherit from Ref, which
is a simple marker interface in the JLISP memory model.

## Evaluation

Eval starts with a cell, which is *probably* the root cell of a parse tree.

If the cell CAR is a string, boolean or numeric literal (i.e. an atom that is not a symbol), just
evaluate it and return.

If the cell CAR is an atom that is a symbol, then it is a function name. That means the
remainder of the parse tree are parameters.

If it's not an Atom then, I think, it's an error.

So iteratively follow the CDR references from that cell. If the CAR of the "next" cell is an
Atom, evaluate it and apply the operator. If the CAR of the "next" cell is a List, then recursively
evaluate the list, and then apply the operator. So in pseudocode:

if (cell.CAR is a literal) {
   return cell.CAR.literalValue;
}

if (cell.CAR is a symbol) {
   func = cell.CAR;

   while (cell.CDR != NIL) {
      if (cell.CDR.CAR is an Atom) {
          if (accum == null) { accum = eval((Atom)cell.CDR.CAR; }
          else { accum = eval_func(func, accum, eval((Atom)cell.CDR.CAR); }
      } else if (cell.CDR.CAR is a list) {
          if (accum == null) { accum = eval((List)cell.CDR.CAR); }
          else { accum = eval_func(func, accum, eval((List)cell.CDR.CAR)); }
      }
      cell = cell.CDR;
   }
   return accum;
}

ERROR

)






Running and compiling the interpreter
======================================

* 	The project comes with a custom Makefile to compiile the project to the version of Java present on the current system.  The makefile can be run 	from the directory of the project with the command: `make`

* 	The binary files can be quickly removed to re-build the project with `make clean`

* 	The project uses several custom packages, so when invoking the project with `java` the classpath must be set to the top-most directory of the 		binary files

* 	A generic run command can look like this: `java -cp ./bin org.ulithi.jlisp.main.LispInterpreter < file1 > file2`

	* This will run the lisp interpreter taking the data from file1 as input and direct all output to file2

	* This example can be found in Runfile


Design information
==================

The Lisp interpreter has two main packages: *Lexer* and *Parser*. These fill the standard interpreter roles of lexical analysis and parsing respectively.  

Lexer
-----

This contains the functionality to split the input into meaningful "chunks" according to the Lisp semantics. For instance, literal words (PLUS, MINUS, T, NIL, etc.) are taken as single tokens while parentheses and dots are individual tokens. For numeric literals, the sign is kept with the literal (+1, -5, etc.) in the token list. This list of tokens is then made available via the `getTokens()` method.

Parser
------

The parser is what does most of the "work" of the interpreter. It has several parts:

*	Converting the input program to dot-notation
*	Creating the parse tree from the program tokens
*	Call the evaluation on the parse tree in a top-down fashion
*	Handles the S-Expression data structure
*	Maintains an environment hash for defined functions and variables during function calls
*	Handles any errors raised by S-Expression evaluation or other parsing operations

### ParseTree

The parse tree is a standard tree model where each node is a `TreeNode`. This abstract class represents either an S-Expression or an Atom and defines any common behavior. It also employs the factory design pattern so that one may call `TreeNode.create(fromData)` and it will return either an `Atom` or `SExpression` based on the content of the passed data.

### Atoms

These represent the standard literals of the language: variables, function names, numerics, etc. When "evaluated" by the program, they either return themselves, or in the case of a variable, its value in the current environment. They have only one attribute - a string of the literal which is represented.

### S-Expressions

S-Expressions are represented in the interpreter by the `SExpression` class. This class has two fields to reflect the structure of a Lisp S-Expression: address and data.  These fields are `TreeNode` objects and thus enabling the S-Expressions to be recursive in nature. 

Evaluation also happens recursively: as per the operational semantics, the CDR is passed to any primitive functions and they operate from there. So if, in the operational semantics, CADR is used, they just take the CAR of their input.  The `evaluate` method of the `SExpression` is set up to use one main version of the function but allowing essentially any combination of the parameters (including none) and passes defaults when none are given. This is inconsequential to the actual running of the interpreter, but provides compatibility for evaluation that takes no regard for either literal interpretation, environment variables, or both.

### User-Defined Functions

When a call is made to DEFUN, the appropriate parts of the S-Expression are broken apart and used to define a new `UserFunction` object and bind it to the current environment. This is accomplished via an `Environment` class which has a static hash table for functions and a static hashtable for variables. The variable hash table is used to keep track of bindings made when calling a user-defined function.  The function body is evaluated and passed a hash of the bindings to use. The S-Expression `evaluate` function merges the new bindings into the current variable bindings and when finished, restores the previous state of the environment. This is to prevent "upward" binding of variables where a variable bound later in a program can be used by a function defined earlier.

### Other notable components

#### Patterns

The parser uses a class of static regular expressions common to identifying legal function names, variable names, etc.  They are kept in the `Patterns` class and are static and public to the entire `Parser` package.

#### Program Evaluation

The parser takes the input of tokens and processes it statement-by-statement. If one of them errors, program termination is halted an no further statements are executed. This is mainly to avoid errors if a later statement requires something defined by one that failed.

#### Debug Mode

In the event of errors, if one wants to see a stack trace of the error, the program can be run with the `-d` flag set: `java -cp ./bin org.ulithi.jlisp.main.LispInterpreter -d < infile` and the stack trace of any error will be sent to `stdout`

## References

https://buildyourownlisp.com/
http://lisp2d.net/teach/i.php (good discussion of the memory model)
