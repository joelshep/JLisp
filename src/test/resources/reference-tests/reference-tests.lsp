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
