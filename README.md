# JLISP - A LISP interpreter and REPL

Greenspun's Tenth Rule: Any sufficiently complicated C or Fortran program contains an ad hoc,
informally-specified, bug-ridden, slow implementation of half of Common Lisp.

## Introduction

JLISP is a LISP interpreter and REPL (Read-Evaluate-Print-Loop), implemented in Java.

I authored JLISP primarily as a learning experience. I've wanted to learn LISP since I was 18 or
so, and have been interested in interpreters and compilers for almost as long. But until recently,
I've never come to grips with either. My formal CS training is minimal -- a few 100-level classes --
and my practical experience otherwise never involved developing or implementing even a small language.

In the past few years, I have learned a little FORTH -- another old, deceptively simple-looking
language -- and developed a primitive interpreter and REPL for it. Now I've set that aside for a
while and am trying LISP.

## Quick Start

JLISP is a standard Maven-based project, with minimal dependencies. As supplied, it compiles
with JDK-17 but will probably compile with older versions as well.

To build the jar and run the unit test suite:

```mvn clean package```

To build an executable jar:

```mvn clean package shade:shade```

To run the JLisp REPL from the executable jar:

```java -jar target/org-ulithi-jlisp-0.1-SNAPSHOT.jar```

See Functions.md for some information about the built-in functions in the JLisp interpreter.

## The Interpreter

JLISP's interpreter has three phases:
1. Lexing, a.k.a. scanning: In this phase the interpreter reads the raw program text and converts it
to an ordered list of *tokens*, skipping over comments and insignificant whitespace. (An example of
significant whitespace is space characters in a string literal, such as "Hello World".) Tokens can
be keywords, functions, symbols or literals. The Lexer (in the org.ulithi.jlisp.lexer package)
performs very little semantic processing: it is essentially producing groups of character data for
the next phase -- parsing -- to assign meaning to.
2. Parsing: In this phase, the tokenized program text produced by the Lexer is ingested by the Parser
(in the org.ulithi.jlisp.parser package), which then produces an s-expression representation for the
program. The JLISP s-expression representation is a classic LISP "dotted-pair" structure. More on
this later.
3. Evaluation: Finally, after the program text has been tokenized, parsed and a valid s-expression
representation has been generated, the program expression(s) are evaluated by recursively walking
and evaluating the s-expression. This process produces a new s-expression representing the output of
the evaluation.

## Conceptual Model

As of JLISP 0.2, JLISP's internal model of LISP forms closely follows the language semantics.
Prior, JLISP had a secondary abstract syntax tree (AST) implementation as well, but it was
fundamentally flawed in that it provided no straightforward way to unambiguously represent a single
atom, like "FOO", with a single element list like ( FOO ). This led to some subtle and not-so-subtle
bugs. It turned out to be easier to just remove the secondary AST and have the parser directly
generate S-expression instead.

LISP expressions -- also known as "forms" -- are represented as S-Expressions (symbolic expressions).
S-Expressions (usually referred to as sexps in the code) are either *atoms* -- numeric and alphanumeric
literals, and symbols like variable and function names -- or lists of atoms and sexprs:

```
s_expression = atomic_symbol | "(" s_expression "." s_expression ")" | list
list = "(" s_expression [s_expression] ")"
```
The `org.ulithi.jlisp.core` package contains the implementations of S-Expressions, Lists and Atoms:
the basic building blocks of the LISP language.

Internally, S-Expressions are composed of `Cell` objects. A JLISP Cell is very similar to a LISP
CONS cell. Like a CONS cell, a JLISP Cell consists of two fields: each field is a pointer (or, in
JLISP, a reference: Java doesn't have pointers). The first field/pointer is the CAR of the CONS cell,
the second pointer is the CDR. If we were drawing a cell, it would look like a rectangle on its side,
split into two boxes -- one for each field -- with pointers coming from each. The CAR pointer, or
reference, points to an atom or the beginning of another list. The CDR pointer either points to
the next cell in the list, or terminates the list with a NIL or other atom. In text, it is much
easier to represent a cell using dotted-pair notation like this: `(4 . NIL)`. This is a cell whose
CAR is the numeric literal 4, and whose CDR is the special value NIL: think of NIL as a null pointer
or reference. `(4 . NIL)` is equivalent to the single-element list `(4)`. Simple LISP expressions are
stored in memory as linked lists of cells. For example `(+ 1 2)` would be stored as
`[+ *]-->[1 *]-->[2 NIL]` (where * represents a pointer to the next cell in the list). In dotted-pair
notation, this is represented as `(+ . (1 . (2 . NIL)))`.

(At the moment, JLISP is unable to parse the dotted-pair version of a sexpr as program text, but it
does use dotted-pair notation when serialized a parse tree as a String. I.e., it can output it, but
not yet accept it as input.)

To manage all this, in JLISP a cell is a pair of `Ref` (reference) instances. Cells themselves are
`Refs`, as are `SExpressions` which includes all atoms and lists.

## Evaluation

***Note: This section is outdated and needs to be re-written.***

Eval starts with a cell, which is *probably* the root cell of an S-expression.

If the cell CAR is a string, boolean or numeric literal (i.e. an atom that is not a symbol), just
evaluate it and return.

If the cell CAR is an atom that is a symbol, then it is a function name. That means the
remainder of the parse tree are parameters.

If it's not an Atom then, I think, it's an error.

So iteratively follow the CDR references from that cell. If the CAR of the "next" cell is an
Atom, evaluate it and apply the operator. If the CAR of the "next" cell is a List, then recursively
evaluate the list, and then apply the operator. So in pseudocode:

```
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
```
### Function Invocation

There are three modes of function invocation:
* Default: Most functions are invoked with a single List/Expression that contains all the fully
evaluated arguments to the function. E.g., the TIMES function invoked via ```(* 4 5 6)``` receives
its arguments as a single List: ```(2 3 4)```. A function like CDR, which takes a List as an
argument, is invoked in a similar way: ```(CDR `(1 2 3))``` invokes the CDR function with
```((1 2 3))```. It receives a List of arguments which has a single element: the list that CDR
operates upon.
* Defining functions: Functions which define symbols (including other functions) are invoked with
List/Expression containing all the fully evaluated arguments, as well as a reference to the current
runtime environment (see next section). Currently, there is a single defining function in JLISP:
DEFUN. Evaluating ```(DEFUN plus2 (x) (+ x 2))``` results in a list of three elements being
passed to the DEFUN function: ```(plus2 (x) (+ x 2))```. Note that none of the arguments have been
evaluated as anything other than literals. This leads us to the third mode of function invocation ...
* Special functions: Arguments for special functions are not evaluated before the function is
invoked. In some cases, like QUOT and DEFUN, the arguments are treated as literal expressions. In
other cases, like IF and SETQ, some arguments are treated as literals or as symbol names, while
others are evaluated within the function implementation. Special functions that evaluate some of
their arguments are additionally designated as *reentrant* (because they re-enter the eval routine
as part of their normal evaluation).

So, defining functions are always special functions, but some special functions are reentrant and
others are not.

## The Environment

The _environment_ is a set of _bindings_. A _binding_ associates a _name_ -- e.g., a function
name, a variable name, a symbol name, etc. -- with the "meaning" associated with the name. For
a function, the meaning is the implementation, for a variable it is the variable's current
value, and for a symbol it is the associated enumerated value (e.g. the symbol ```T``` means, or
evaluates to _true_).

The JLISP environment has three main sections. The "core" section holds bindings created by the core
(built-in) JLISP language. These bindings are created automatically when the interpreter starts and
cannot be changed or shadowed (ie, you can't create a new binding with the same name as is used in
an existing binding in the core language).

The "extension" section allows for additional bindings defined in customer-specified namespaces.
This includes bindings loaded from function "packages".

"Core" and "extension" bindings are both global in scope.

Finally, the "dynamic" section is where dynamically-scoped bindings are created at run-time.
Bindings in the "dynamic" section can shadow other bindings in the "dynamic" and "extension"
sections for as long as the related dynamic scope is active.

Implementation-wise, the environment is a list of maps. The outermost list contains
the "core" section, any "extension" sub-environments in the order in which they are registered, and
then the dynamic sub-environments. Every function invocation results in a new sub-environment being
appended to the list -- for symbols defined in the dynamic scope of the function -- which is removed
when the function completes.

Each sub-environment corresponds to a map which is keyed by the symbol's lexeme (character
representation), and whose values are the currently bound "meanings".

Resolving a name entails traversing the maps from the tail of the list (the most recently added,
representing the locally bound names for the function currently being evaluated) to the head of
the list (the "core" section): the first sub-environment with a binding for the name "wins".

At this time, contextual information about bindings is not maintained: e.g., it is not possible to
distinguish between a function named "foo" and a variable named "foo". Within the same local scope,
however, a name may only be bound once: attempting to bind a name twice within the same local scope
results in an error.

## References

* https://lispcookbook.github.io/cl-cookbook/functions.html - The Common Lisp Cookbook
* https://www.cs.cmu.edu/Groups/AI/html/cltl/clm/node1.html - Common Lisp the Language (CMU)
* https://jtra.cz/stuff/lisp/sclr/index.html - Simplified Common Lisp Reference
* http://www.lispworks.com/documentation/lw71/CLHS/Front/X_Master.htm - Dictionary of LISP functions.
* https://stackoverflow.com/questions/16606172/is-an-empty-list-in-lisp-built-from-a-cons-cell - Good
comments on NIL, atoms and lists
* https://people.csail.mit.edu/jaffer/r5rs/Pairs-and-lists.html - Scheme manual
* https://www.scheme.com/tspl2d/grammar.html - Scheme syntax
* https://gigamonkeys.com/book/ - Practical Common Lisp
* https://bernsteinbear.com/blog/lisp/
* https://buildyourownlisp.com/
* http://lisp2d.net/teach/i.php - Good discussion of the memory model
* http://www-formal.stanford.edu/jmc/history/lisp/node3.html - McCarthy's history of LISP
* https://dept-info.labri.fr/~strandh/Teaching/MTP/Common/David-Lamkins/contents.html - Learning LISP
* https://en.wikipedia.org/wiki/Greenspun%27s_tenth_rule
* https://stackoverflow.com/questions/3482389/how-many-primitives-does-it-take-to-build-a-lisp-machine-ten-seven-or-five
... which has me thinking I need to stop implementing functions in Java for a bit, start baking
in LISP primitives like eval, and see if the interpreter encourages a little more LISP in terms
of LISP ...
* https://paulgraham.com/ - Thoughts on LISP and other things
* https://onecompiler.com/commonlisp/ - An online LISP REPL
