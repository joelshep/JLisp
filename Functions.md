# JLisp Functions

This is brief documentation of the native functions in JLisp with examples of their usage.

## List Operations

### APPEND
Concatenates the elements of lists given as arguments into a new list.  
Collections.java  
`(APPEND '(A B) '(C D))` => `( A B C D )`  
`(APPEND 'A)` => `A`  
`(APPEND '((A) (B)) '((C) (D)))` => `( ( A ) ( B ) ( C ) ( D ) )`

### ASSOC
An association list (a.k.a. "alist") is a list of pairs. The ASSOC func takes a key value and an
alist, and returns the first pair such that the given key is equal to the `car` of the pair, or
`NIL` otherwise.  
Collections.java  
`(ASSOC 'oak '((pine cones) (oak acorns) (oak seeds)))` => `( oak acorns )`
`(ASSOC 'birch '((pine cones) (oak acorns) (maple seeds)))` => `NIL`

### CAR
Returns the first element of a `list` as an S-expression.  
Lang.java  
`(CAR (QUOTE (HELLO)))` => `'HELLO'`  
`(CAR (QUOTE ((A B) (C D) (E F))))` => `( A B )`  
`(CAR (QUOTE ()))` => `()`

### CDR
Returns the remainder of a `list` as a `list`.  
Lang.java  
`(CDR (QUOTE (HELLO)))` => `()`  
`(CDR (QUOTE (4 5 6)))` => `( 5 6 )`  
`(CDR (QUOTE ()))` => `()`

### CONS
Accepts two arguments and returns a `list` whose CAR is the first argument and the
CDR of the `list` is the second argument.  
Lang.java  
`(CONS () ()))` => `()`  
`(CONS 1 (QUOTE (2 3)))` => `( 1 2 3 )`  
`(CONS HELLO ())` => `( HELLO )`

### LENGTH
Returns the number of top-level elements in a given `list`.  
Collections.java  
`(LENGTH (QUOTE ()))` => `0`  
`(LENGTH (QUOTE (1 2 3)))` => `3`  
`(LENGTH (QUOTE (1 (A B C) 3 (DEF))))` => `4`

### LIST
Constructs a list whose elements are the given arguments.  
Collections.java  
`(LIST 'A 'B 'C)` => `( A B C )`  
`(LIST (LIST 'A 'B) (LIST 'C 'D))` => `( ( A B ) ( C D ) )`

### NTH
Returns the list element at the given zero-based index, or NIL if the index is greater than
or equal to the length of the list.  
Collections.java  
`(NTH 2 '(A B C D))` => `C`  
`(NTH 0 '((A B) (C D) (E F) (G H)))` => `( A B )`  
`(NTH 4 '((A B) (C D) (E F) (G H)))` => `NIL`

### SIZE
Non-standard function. Returns the total number of elements in the given list, including elements
in any nested lists. If the list is NIL, returns 0.  
Collections.java  
`(SIZE '(A B C D))` => `4`  
`(SIZE `(A (1 2) B (3 4)))` => `6`

## Predicates and Operators

### ATOM
Returns true if the argument is an `atom`; and false otherwise.  
Predicate.java  
`(ATOM (QUOTE 3))` => `T`  
`(ATOM (QUOTE (1 2 3)))` => `F`

### EQUAL
Util.java

### EQL
Util.java

### GREATER | >
Returns true if the arguments are strictly decreasing in order, false otherwise.  
Math.java  
`(> 34 24)` => `T`  
`(> 104 98 67 23 8 -4)` => `T`  
`(> 104 98 67 23 8 8)` => `F`

### INTEGERP
Returns true if the argument is an integer; and false otherwise.  
Predicate.java  
`(INTEGERP (QUOTE 3))` => `T`  
`(INTEGERP (QUOTE HELLO))` => `F`  
`(INTEGERP (QUOTE (1 2 3)))` => `F`

### LESS | <
Returns true if the arguments are strictly increasing in order, false otherwise.  
Math.java  
`(< 5 8)` => `T`  
`(< -4 8 23 67 98 104)` => `T`  
`(< -4 8 23 67 48 104)` => `F`

### MINUS | -
When invoked with a single argument, returns the negation of the argument. Otherwise,
subtracts from the first argument all successive arguments and returns the result.  
Math.java  
`(MINUS 47)` => `-47`  
`(MINUS 72 12 7)` => `53`  
`(MINUS 11 5)` => `6`

### MINUSP
Returns true if the argument is a negative integer; and false otherwise.  
Predicate.java  
`(MINUSP -3)` => `T`  
`(MINUSP 4)` => `F`  
`(MINUSP 0)` => `F`

### PLUS | +
Returns the sum of the arguments.  
Math.java  
`(+ 2 3)` => `5`  
`(+ 2 3 4 5)` => `14`

### PLUSP
Returns true if the argument is a positive integer; and false otherwise.  
Predicate.java  
`(PLUSP 3)` => `T`  
`(PLUSP -4)` => `F`  
`(PLUSP 0)` => `F`

### QUOTIENT | /
Returns the first argument divided by the successive arguments.  
Math.java  
`(QUOTIENT 72 8)` = `9`  
`(QUOTIENT 200 4 5 5)` => `2`

### REMAINDER | %
Returns the remainder of the first argument when divided by the successive arguments.  
Math.java  
`(REMAINDER 77 8)` = `5`  
`(REMAINDER 77 8 3)` = `2`

### TIMES | *
Returns the product of the arguments.  
Math.java  
`(* 4 5)` => `20`  
`(TIMES (PLUS 1 2) (MINUS 7 3))` => `12`

### ZEROP
Returns true if the argument is the integer 0 (zero); and false otherwise.  
Predicate.java  
`(ZEROP (+ -3 3))` => `T`  
`(ZEROP (+ -3 2))` => `F`  
`(ZEROP (+ 3 -2))` => `F`  
`(ZEROP 0)` => `T`

## Flow of Control

### COND
Evaluates a series of conditionals: when one evaluates to true, evaluates and returns the result
of the corresponding form. COND is similar to a select-case statement in other languages.  
Lang.java  
`(COND (( = s Y) T) ((= s y) T) ((= s N) F) ((= s n) F) (T nil))`  
... returns T if s is 'y' or 'Y', F if s is 'n' or 'N', and `nil` otherwise.

### IF
Allows execution of a form to be dependent on a single test-form.  
Lang.java  
`(IF (> 1 2) (QUOTE BAZ) (QUOTE FOO) )` => `FOO`

### MAPCAR
Applies a function iteratively to a list of arguments composed by selecting the nth argument from
each of the one or more given lists of argument, one list for each function parameter. To apply a
function to a single list of arguments, use `APPLY`.  
Lang.java  
`(MAPCAR (LAMBDA (x) (+ x 2)) '(3 5 7))` => `( 5 7 9 )`
`(MAPCAR 'PLUS '(3 5 7) '(4 5 6))` => `( 7 10 13 )`

## Extension

### APPLY
Applies a function to an argument list. The argument list should contain the arguments needed for
a single function execution. To apply a function iteratively to one more argument lists, use
MAPCAR.  
Lang.java  
`(APPLY '+ '(1 2 3 4))` => `10`  
`(APPLY 'CDR '((A B C)))` => `( B C )`

### DEFUN
Creates a user-defined function and returns its name as a literal `atom`.  
Lang.java  
`(defun eleven () (QUOTE 11))` => `eleven`  
`(defun average (x y) (QUOTIENT (PLUS x y) 2))` = > `average`

### DEFMACRO
Creates a user-defined macro

### EVAL
Evaluates a form and returns the result. Note that the form itself is the result of
evaluating the arguments to EVAL.  
Lang.java  
`(EVAL '(+ 1 2 3))` => `6`  
`(SETQ A 'B)`  
`(SETQ B 'C)")`  
`(EVAL A)` => `C`

### SETQ
Assigns the value of the second argument to the symbol specified by the first argument. If the
symbol has not been defined previously, `SETQ` creates it as a global variable.



### LAMBDA

### MACROEXPAND


### QUOTE
Returns its argument as-is.  
Lang.java  
`(QUOTE FOO)` => FOO  
`(QUOTE (FOO BAR))` => `( FOO BAR )`

There is also the shorthand single-quote notation:  
`'FOO` => FOO  
`'(FOO BAR)` => `( FOO BAR )`

## IO (Input/Output)

### FORMAT
Produces, and optionally outputs, a formatted string. The first argument specifies the "destination"
for the string, the second argument specifies the formatter, and the remaining arguments are
the values to be formatted. Currently, the destination must be `T` or `F`. If the destination is
`T`, the formatted string is written to STDOUT and the function returns `NIL`. Otherwise, the
function immediately returns the formatted string.   
The format string passed to `FORMAT` must conform to the [Java MessageFormat format string
specification](https://docs.oracle.com/javase/8/docs/api/java/text/MessageFormat.html).  
IO.java
`(FORMAT T "Hello {0}!" "Bob")` => `NIL` (writes "Hello Bob!" to STDOUT)  
`(FORMAT F "Hello {0}, how are {1}?" "Bob" "things")` => `Hello Bob, how are things?`

### WRITE
Writes the first argument to a stream specified by the second argument, and returns the first
argument. NOTE: Currently the second argument is ignored: output always goes to STDOUT.  
IO.java  
`(WRITE "Tour de France")` => `Tour de France` (and writes the same to STDOUT)

## Logic

The `AND`, `NOT` and `OR` functions evaluate the boolean nature of arguments using a "truthy"
definition for Boolean true. If an argument is an atom, it is considered "true" if it is a Boolean
true, a non-zero integer, or a non-empty string: otherwise, it is considered "false". Non-atoms
are considered "true" as long as they are not `NIL`.

### AND
Returns `NIL` if any argument evaluates to `NIL` or `F`. Otherwise, returns the value of the last
argument, or `T` if invoked without arguments.  
Logic.java  
`(AND 1 2)` => `2`  
`(AND)` => `T`  
`(AND 1 NIL 2)` => `NIL`  

### F
Symbol representing the Boolean `false` value.  
Logic.java  
`(F)` => `F`

### NOT
Returns `T` if its argument is `NIL` or evaluates to `F`; returns `F` otherwise.  
Logic.java  

### OR
Returns `T` if any argument evaluates to `T`. Otherwise, returns the value of the last argument,
or `F` if invoked without arguments.

### T
Symbol representing the Boolean `true` value.  
Logic.java  
`(T)` => `T`

## Testing

### EXPECT
Non-standard function. Evaluates to arguments and returns `T` if they are `EQUAL`, and logs to
STDERR and returns `F` otherwise. Intended to support unit testing.  
Expect.java  
`(EXPECT (+ 1 2) 4)` => `F`  
`(EXPECT (APPEND '(A) '() '(B) '()) (A B))` => `T`

### WHEN
Non-standard function. Accepts a LISP expression to evaluate, evaluates it and returns
`T`. `WHEN` is used in unit tests to set up pre-conditions -- e.g., initializing variables,
defining functions  -- for subsequent `EXPECT` expressions.  
Expect.java  
(WHEN (DEFUN PLUSONE (X) (+ X 1))) => `T`  
(EXPECT (MAPCAR 'PLUSONE '(0 1 2 3)) '(1 2 3 4)) => `T`

### THEN
Non-standard function. Erases all user-defined functions and symbols from the environment. Intended
for use in unit tests.
