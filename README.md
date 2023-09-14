# JLISP - A LISP interpreter and REPL

Greenspun's Tenth Rule: Any sufficiently complicated C or Fortran program contains an ad hoc,
informally-specified, bug-ridden, slow implementation of half of Common Lisp.

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

The **memory model** describes the in-memory structure -- the AST essentially -- that the Parser generates.
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

The **language model** describes, well, the lists -- or, more precisely, the S-Expressions
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
from SExpression) but cells are *not* sexprs. In the memory model, however, atoms and cells are the
things that can be referred to from cell fields: they are all *references*.

So, in JLISP, a cell is a pair of Refs, and the Atom and Cell classes inherit from Ref, which
is a simple marker interface in the JLISP memory model.

The ```SExpression``` class in ```org.ulithi.jlisp.core``` is not only the super-class for
```Atom``` and ```List```, but it is the bridge between the language model and the memory model.
The ```SExpression``` class's primary function is to transform cells and references to atoms
and lists.

### A Word About NIL

In JLISP, ```NIL``` is a condition, not a value. ```NIL``` is not defined as a value in JLISP.
For atoms, T (true) and F (false) are the Boolean values. An empty list when coerced, to a Boolean,
evaluates to F: all other lists evaluate to T. The ```NIL?``` predicate returns T for the F atom
and the empty list, and returns T otherwise. ```NIL``` only exists in the memory model, as an
end-of-list marker.

I originally tried to implement ```NIL``` with similar semantics to early LISP versions, where
NIL was false was an atom was an empty list was a list terminator ... but it proved to be more
overloading than I could manage. So, I gave up and decided to go a more Scheme-like route by
simply excluding ```NIL``` as a first-class concept.

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

Implementation-wise, the environment is a list of maps. The outer-most list contains
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

* http://www.lispworks.com/documentation/lw71/CLHS/Front/X_Master.htm - Dictionary of LISP functions.
* https://stackoverflow.com/questions/16606172/is-an-empty-list-in-lisp-built-from-a-cons-cell - Good
comments on NIL, atoms and lists
* https://people.csail.mit.edu/jaffer/r5rs/Pairs-and-lists.html - Scheme manual
* https://gigamonkeys.com/book/ - Practical Common Lisp
* https://bernsteinbear.com/blog/lisp/
* https://buildyourownlisp.com/
* http://lisp2d.net/teach/i.php - Good discussion of the memory model
* http://www-formal.stanford.edu/jmc/history/lisp/node3.html - McCarthy's history of LISP
* https://dept-info.labri.fr/~strandh/Teaching/MTP/Common/David-Lamkins/contents.html - Learning LISP
* https://en.wikipedia.org/wiki/Greenspun%27s_tenth_rule
