# JLisp Functions

This is brief documentation of the native functions in JLisp with examples of their usage.

### ATOM
Returns true if the argument is an `atom`; and false otherwise.  
`(ATOM (QUOTE 3))` => Atom.T  
`(ATOM (QUOTE (1 2 3)))` => Atom.F

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
`(CONS HELLO ())` => `(HELLO . NIL)`

### DEFUN
Creates a user-defined function and returns its name as a literal `atom`.  
Lang.java  
`(defun eleven () (QUOTE 11))` => `eleven`  
`(defun average (x y) (QUOTIENT (PLUS x y) 2))` = > `average`

### EQUAL
Util.java

### EQL
Util.java

### F
Symbol representing the Boolean `false` value.  
Logic.java  
`(F)` => false

### GREATER | >
Returns true if the arguments are strictly decreasing in order, false otherwise.  
Math.java  
`(> 34 24)` => `T`  
`(> 104 98 67 23 8 -4)` => `T`  
`(> 104 98 67 23 8 8)` => `F`

### IF
Allows execution of a form to be dependent on a single test-form.  
Lang.java  
`(IF (> 1 2) (QUOTE BAZ) (QUOTE FOO) )` => `FOO`

### INTEGERP
Returns true if the argument is an integer; and false otherwise.  
Predicate.java  
`(INTEGERP (QUOTE 3))` => Atom.T
`(INTEGERP (QUOTE HELLO))` => Atom.F
`(INTEGERP (QUOTE (1 2 3)))` => Atom.F

### LENGTH
Returns the number of top-level elements in a given `list`.  
Collections.java  
`(LENGTH (QUOTE ()))` => `0`  
`(LENGTH (QUOTE (1 2 3)))` => `3`  
`(LENGTH (QUOTE (1 (A B C) 3 (DEF))))` => `4`

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
`(MINUS 47)` => -47  
`(MINUS 72 12 7)` => 53  
`(MINUS 11 5)` => 6

### PLUS | +
Returns the sum of the arguments.  
Math.java  
`(+ 2 3)` => 5  
`(+ 2 3 4 5)` => 14

### QUOTE
Returns its argument as-is.  
Lang.java  
`(QUOTE FOO)` => FOO  
`(QUOTE (FOO BAR))` => `( FOO BAR )`

There is also the shorthand single-quote notation:  
`'FOO` => FOO  
`'(FOO BAR)` => `( FOO BAR )`

### QUOTIENT | /
Returns the first argument divided by the successive arguments.  
Math.java  
`(QUOTIENT 72 8)` = 9  
`(QUOTIENT 200 4 5 5)` => 2

### REMAINDER | %
Returns the remainder of the first argument when divided by the successive arguments.  
Math.java  
`(REMAINDER 77 8)` = 5  
`(REMAINDER 77 8 3)` = 2

### T
Symbol representing the Boolean `true` value.  
Logic.java  
`(T)` => true

### TIMES | *
Returns the product of the arguments.  
Math.java  
`(* 4 5)` => 20  
`(TIMES (PLUS 1 2) (MINUS 7 3))` => 12
