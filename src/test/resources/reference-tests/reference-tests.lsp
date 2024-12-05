;;;; Reference Tests
;;; Unit-test forms which have been drawn from books and other reviewed sources, that are
;;; intended to exercise a broad range of LISP functionality, at a high level (complete forms),
;;; and which are validated less extensively than in-code unit-tests.

;; A couple of tests just to make sure the basic plumbing is working.
(EXPECT '43 '43)
(EXPECT (+ 3 2) '5)
(EXPECT (CDR (CAR (QUOTE ((1 2 3) (4 5 6))))) '(2 3))

;; Quote and '
(EXPECT (QUOTE (A B C)) '(A B C))

;; Forms for basic list operations.
(EXPECT (CAR '(FAST COMPUTERS ARE NICE')) 'FAST)
(EXPECT (CDR '(FAST COMPUTERS ARE NICE)) '(COMPUTERS ARE NICE))
(EXPECT (CAR '(A B C)) 'A)
(EXPECT (CDR '(A B C)) '(B C))
(EXPECT (CAR '((A B) C)) '(A B))
(EXPECT (CDR '((A B) C)) '(C))
(EXPECT (CAR (CDR '(A B C))) 'B)
(EXPECT (CAR '(CDR (A B C))) 'CDR)

;; Forms for NIL.
(EXPECT (CAR NIL) NIL)
(EXPECT (CDR NIL) NIL)

;; Forms for GETF
(EXPECT (GETF '(A B 4 D A X) 'A) 'B)
(EXPECT (GETF '(A B 4 D A X) 'X) NIL)
(EXPECT (GETF '(A B 4 D A X) 'Y) NIL)
(EXPECT (GETF '(A B 4 D A X) '4) 'D)
(EXPECT (GETF '(A B 4 D A X) 'B) NIL)
(EXPECT (GETF '(A B 4 D A X) 'X 'NOT-FOUND) 'NOT-FOUND)
(EXPECT (GETF () 'A 'B) 'B)
(EXPECT (GETF () 'A) NIL)

(THEN)
(WHEN (SETQ X '()))
(EXPECT (GETF X 'prop1) NIL)
(EXPECT (GETF X 'prop1 7) 7)
(EXPECT (GETF X 'prop1) NIL)

;; Forms for MAPCAR
(THEN)
(WHEN (DEFUN PLUSONE (X) (+ X 1)))
(WHEN (SETQ A '(0 1 2 3 4 5 6 7 8 9)))
(EXPECT  (MAPCAR 'PLUSONE A) '(1 2 3 4 5 6 7 8 9 10))
(EXPECT (MAPCAR (LAMBDA (X) (+ X 1)) '(0 1 2 3 4 5 6 7 8 9)) '(1 2 3 4 5 6 7 8 9 10))

;; Forms for COND, plus recursion.
(THEN)
(WHEN (DEFUN N! (N) (COND ((EQL N 0) 1) (T (* N (N! (- N 1)))))))
(EXPECT (N! 5) 120)
